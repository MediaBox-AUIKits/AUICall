//
//  AUICall1V1Controller.swift
//  AUICallExample
//
//  Created by Bingo on 2023/6/20.
//

import UIKit
import AUIRoomEngineSDK

@objc public protocol AUICall1V1ControllerDelegate: NSObjectProtocol {
    @objc optional func onCallModeChanged()
    @objc optional func onCallAudioOuputTypeChanged(disabledSpeaker: Bool)
    @objc optional func onCallAudioMuteStateChanged(userId: String, off: Bool)
    @objc optional func onCallVideoMuteStateChanged(userId: String, off: Bool)
    @objc optional func onCallStateChanged()
    @objc optional func onCallingTimeChanged()
    @objc optional func onCallingError()
}

public class AUICall1V1Controller: NSObject {
    public enum UserRole {
        case caller
        case callee
    }
    
    public enum CallMode {
        case audio
        case video
    }
    
    public enum CallState {
        case none            // 初始化
        case connecting      // 主叫与被叫入会中
        case waiting         // 主叫等待对方接受，被叫待接听
        case connected       // 主叫与被叫通话中
        case over            // 通话结束
    }
    
    public enum CallOverByReason {
        case none
        case calleeReject     // 被呼叫者拒听
        case callerCancel     // 呼叫者取消通话
        case destHandup       // 对方挂断
        case meHandup         // 自己挂断
        case timeout          // 超时
    }
    
    public enum InnerMsgType: Int {
        case audioMode = 10000
    }
    
    init(roomEngine: AUIRoomEngine, mode: CallMode, role: UserRole, destUser: AUIRoomUser) {
        self.roomEngine = roomEngine
        self.roomId = ""
        self.mode = mode
        self.role = role
        self.destUser = destUser
        self.state = .none
        super.init()
        
        self.roomEngine.addObserver(delegate: self)
    }
    
    deinit {
        debugPrint("deinit: \(self)")
    }
    
    public weak var delegate: AUICall1V1ControllerDelegate?
    public private(set) var roomEngine: AUIRoomEngine
    public private(set) var roomId: String
    public private(set) var mode: CallMode {
        didSet {
            self.delegate?.onCallModeChanged?()
        }
    }
    public private(set) var role: UserRole
    public private(set) var state: CallState {
        didSet {
            self.delegate?.onCallStateChanged?()
        }
    }
    public private(set) var overReason: CallOverByReason = .none
    public private(set) var lastError: Error? {
        didSet {
            if self.lastError != nil {
                self.delegate?.onCallingError?()
            }
        }
    }
    
    internal var renderLayoutView: AUICall1V1RenderViewLayout? {
        return self.roomEngine.renderViewLayout as? AUICall1V1RenderViewLayout
    }
    
    public private(set) var destUser: AUIRoomUser
    var me: AUIRoomUser {
        return AUIRoomEngine.currrentUser!
    }
    
    public private(set) var callingSeconds = 0.0
    private lazy var countdownTimer: Timer = {
        let timer = Timer(timeInterval: 1.0, repeats: true) {[weak self] timer in
            if let self = self {
                self.callingSeconds = self.callingSeconds + 1.0
                self.delegate?.onCallingTimeChanged?()
            }
        }
        RunLoop.current.add(timer, forMode: .default)
        return timer
    }()
    
    private func startCallTimeoutCheck() {
        self.perform(#selector(onCallTimeout), with: nil, afterDelay: 60.0)
    }
    
    private func stopCallTimeoutCheck() {
        NSObject.cancelPreviousPerformRequests(withTarget: self, selector: #selector(onCallTimeout), object: nil)
    }
    
    @objc private func onCallTimeout() {
        self.over(reason: .timeout)
    }
    
    private func joinRoom(success: @escaping ()->Void) {
        if self.state == .over {
            return
        }
        AUICallAppServer.fetchRoomEngineConfig(uid: self.me.userId, roomId: self.roomId) { token, timestamp, error in
            if token != nil && timestamp != nil {
                if self.state == .over {
                    return
                }
                let config = AUIRoomEngineConfig()
                config.appId = AUICallGlobalConfig.appID
                config.gslb = AUICallGlobalConfig.gslb
                config.roomId = self.roomId
                config.timestamp = timestamp!
                config.token = token!
                config.dimensions = AUICallGlobalConfig.dimensions
                config.frameRate = AUICallGlobalConfig.frameRate
                self.roomEngine.join(config: config) {[weak self] error in
                    guard let self = self else {
                        return
                    }
                    if self.state == .over {
                        self.roomEngine.leave()
                        return
                    }
                    if error == nil {
                        self.roomEngine.startPublish()
                        if self.mode == .audio {
                            self.roomEngine.switchCamera(off: true, userId: self.me.userId)
                        }
                        self.roomEngine.switchAudioOutput(type: .speaker)
                        success()
                    }
                    else {
                        self.errorOccurs(code: .common, msg: "")
                    }
                }
            }
            else {
                self.errorOccurs(code: .common, msg: "")
            }
        }
    }

    private func start() {
        if self.role != .caller {
            return
        }
        self.state = .connecting
        let layout = AUICall1V1RenderViewLayout(callController: self)
        self.roomEngine.renderViewLayout = layout
        if self.mode == .video {
            self.roomEngine.startPreview()
        }
        self.roomEngine.create {[weak self] roomId, error in
            guard let self = self else {
                return
            }
            if let roomId = roomId {
                debugPrint("roomId:\(roomId)")
                self.roomId = roomId
                self.joinRoom {[weak self] in
                    guard let self = self else {
                        return
                    }
                    self.state = .waiting
                    self.roomEngine.requestJoin(roomId: self.roomId, userId: self.destUser.userId, extra: self.extraDataForJoin(), completed: nil)
                    self.startCallTimeoutCheck()
                }
            }
            else {
                self.errorOccurs(code: .common, msg: "")
            }
        }
    }
    
    private func receive(roomId: String) {
        if self.role != .callee {
            return
        }
        self.roomId = roomId
        self.state = .waiting
        let layout = AUICall1V1RenderViewLayout(callController: self)
        self.roomEngine.renderViewLayout = layout
        if self.mode == .video {
            self.roomEngine.startPreview()
        }
        self.startCallTimeoutCheck()
    }
    
    private func over(reason: CallOverByReason) {
        if self.state == .over {
            return
        }
        
        self.stopCallTimeoutCheck()
        if self.role == .caller && self.state == .waiting {
            self.roomEngine.cancelRequestJoin(roomId: self.roomId, userId: self.destUser.userId, extra: self.extraDataForJoin())
        }
        else if self.role == .callee && self.state == .waiting {
            if reason == .meHandup {
                self.roomEngine.responseJoin(roomId: self.roomId, userId: self.destUser.userId, agree: false, extra: self.extraDataForJoin())
            }
        }
        self.overReason = reason
        self.state = .over
        self.countdownTimer.invalidate()
        self.roomEngine.stopPreview()
        self.roomEngine.leave()
        self.roomEngine.renderViewLayout = nil
        AUICall1V1Controller.isCalling = false
    }
    
    public func handup() {
        self.over(reason: .meHandup)
    }
    
    public func accept(mode: CallMode) {
        if self.role != .callee || self.state != .waiting {
            return
        }
        
        let acceptByAudio = self.mode == .video && self.mode != mode
        self.mode = mode
        if self.mode == .audio {
            self.roomEngine.stopPreview()
        }
        if acceptByAudio { // 使用语音接听
            self.sendAudioModeMessage()
        }
        self.state = .connecting
        self.stopCallTimeoutCheck()
        self.roomEngine.responseJoin(roomId: self.roomId, userId: self.destUser.userId, agree: true, extra: self.extraDataForJoin()) {[weak self] error in
            guard let self = self else {
                return
            }
            if error == nil {
                self.joinRoom {[weak self] in
                    guard let self = self else {
                        return
                    }
                    self.state = .connected
                    self.countdownTimer.fire()
                }
            }
            else {
                self.errorOccurs(code: .common, msg: "")
            }
        }
    }
    
    public func muteAudio(mute: Bool) {
        self.roomEngine.switchMicrophone(off: mute, userId: self.me.userId)
    }
    
    public func muteVideo(mute: Bool) {
        self.roomEngine.switchCamera(off: mute, userId: self.me.userId)
    }
    
    public func switchSpeaker(disabledSpeaker: Bool) {
        self.roomEngine.switchAudioOutput(type: disabledSpeaker ? .headset : .speaker)
        self.delegate?.onCallAudioOuputTypeChanged?(disabledSpeaker: disabledSpeaker)
    }
    
    public func switchCamera() {
        let type = self.roomEngine.getCameraType()
        if type == .invalid {
            return
        }
        self.roomEngine.setCameraType(type: type == .front ? .back : .front)
    }
    
    // 切换到语音接听：根据当前状态执行切换，如果是自己切换的话，需要通知对方（发消息）
    public func switchToAudioMode(isMe: Bool) {
        if self.mode == .audio {
            return
        }
        
        self.mode = .audio
        self.roomEngine.stopPreview()
        self.roomEngine.switchCamera(off: true, userId: self.me.userId)
        if isMe {
            self.sendAudioModeMessage()
        }
    }
}

extension AUICall1V1Controller {
    
    public enum ErrorCode: Int {
        case common = -1
    }
    
    private func errorOccurs(code: ErrorCode, msg: String?) {
        let error = NSError(domain: "aui.call.1v1", code: code.rawValue, userInfo: [NSLocalizedDescriptionKey:msg ?? "unknown"])
        self.lastError = error
    }
}

extension AUICall1V1Controller {
    
    private func sendAudioModeMessage() {
        self.roomEngine.sendCustomMessage(message: ["innerType": InnerMsgType.audioMode.rawValue, "roomId":self.roomId], roomId: nil, userId: self.destUser.userId)
    }
    
    private func extraDataForJoin() -> [AnyHashable: Any] {
        return [
            "mode": self.mode == .video ? "video" : "audio",
            "type": "single",
            "user": [
                "uid": self.me.userId,
                "nick": self.me.userNick,
                "avatar":self.me.userAvatar
            ]
        ]
    }
    
    private static func validCall(extra: [AnyHashable: Any]?) -> Bool {
        if let extra = extra {
            let mode = extra["type"] as? String
            if mode == "single" {
                return true
            }
        }
        return false
    }
    
    private static func callMode(extra: [AnyHashable: Any]?) -> CallMode? {
        if let extra = extra {
            let mode = extra["mode"] as? String
            if mode == "audio" {
                return .audio
            }
            else if mode == "video" {
                return .video
            }
        }
        return nil
    }
    
    private static func user(extra: [AnyHashable: Any]?, key: String) -> AUIRoomUser? {
        if let extra = extra {
            let userData = extra[key] as? [AnyHashable: Any]
            if let userData = userData {
                let uid = userData["uid"] as! String
                let user = AUIRoomUser(uid)
                let nick = userData["nick"] as? String
                if nick != nil {
                    user.userNick = nick!
                }
                let avatar = userData["avatar"] as? String
                if avatar != nil {
                    user.userAvatar = avatar!
                }
                return user
            }
        }
        return nil
    }
}


extension AUICall1V1Controller {
    
    public static var roomEngine: AUIRoomEngine?
    public internal(set) static var isCalling = false
    
    public static func startCall(mode: CallMode, callee: AUIRoomUser) -> AUICall1V1Controller? {
        if self.isCalling {
            return nil
        }
        
        guard let roomEngine = self.roomEngine else {
            return nil
        }
        
        self.isCalling = true
        let controller = AUICall1V1Controller(roomEngine: roomEngine, mode: mode, role: .caller, destUser: callee)
        controller.start()
        return controller
    }
    
    public static func receiveCall(caller: AUIRoomUser, roomId: String, extra: [AnyHashable : Any]?) -> AUICall1V1Controller? {
        if self.isCalling {
            return nil
        }
        
        if !self.validCall(extra: extra) {
            return nil
        }
        
        guard let roomEngine = self.roomEngine else {
            return nil
        }
        
        guard let mode = self.callMode(extra: extra) else {
            return nil
        }
        
        caller.update(user: self.user(extra: extra, key: "user"))
        
        self.isCalling = true
        let controller = AUICall1V1Controller(roomEngine: roomEngine, mode: mode, role: .callee, destUser: caller)
        controller.receive(roomId: roomId)
        return controller
    }
}


extension AUICall1V1Controller: AUIRoomEngineDelegate {
    
    public func onReceivedResponseJoin(user: AUIRoomUser, roomId: String, agree: Bool, extra: [AnyHashable : Any]?) {
        if roomId == self.roomId && self.role == .caller {
            if agree {
                if (self.state == .waiting) {
                    self.state = .connected
                    self.stopCallTimeoutCheck()
                    self.countdownTimer.fire()
                }
            }
            else {
                // 被呼叫者拒听
                self.over(reason: .calleeReject)
            }
        }
    }
    
    public func onReceivedCancelRequestJoin(user: AUIRoomUser, roomId: String, extra: [AnyHashable : Any]?) {
        if roomId == self.roomId && self.role == .callee {
            // 未接通情况下，caller取消通话
            self.over(reason: .callerCancel)
        }
    }
    
    public func onStopedPublish(user: AUIRoomUser) {
        
    }
    
    public func onLeaved(user: AUIRoomUser) {
        if user.userId == self.destUser.userId {
            // 对方结束通话
            self.over(reason: .destHandup)
        }
    }
    
    public func onCameraStateChanged(user: AUIRoomUser, off: Bool) {
        self.delegate?.onCallVideoMuteStateChanged?(userId: user.userId, off: off)
    }
    
    public func onMicrophoneStateChanged(user: AUIRoomUser, off: Bool) {
        self.delegate?.onCallAudioMuteStateChanged?(userId: user.userId, off: off)
    }
    
    public func onReceivedCustomMessage(user: AUIRoomUser, message: [AnyHashable : Any]) {
        if let type = message["innerType"] as? Int {
            if type == InnerMsgType.audioMode.rawValue {  // 切换到语音通话
                let roomId = message["roomId"] as? String
                if roomId != nil && roomId! == self.roomId {
                    self.switchToAudioMode(isMe: false)
                }
            }
        }
    }
}
