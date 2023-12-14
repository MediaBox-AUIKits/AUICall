//
//  AUICallNVNController.swift
//  AUICallExample
//
//  Created by Bingo on 2023/7/5.
//

import UIKit
import AUIFoundation
import AUIRoomEngineSDK

@objc public protocol AUICallNVNControllerDelegate: NSObjectProtocol {
    @objc optional func onCallUserMembersUpdated()
    @objc optional func onCallAudioMuteStateChanged(userId: String, off: Bool)
    @objc optional func onCallVideoMuteStateChanged(userId: String, off: Bool)
    @objc optional func onCallAudioOuputTypeChanged(disabledSpeaker: Bool)
    @objc optional func onCallMuteAudioAllChanged(off: Bool)
    @objc optional func onCallingTimeChanged()
    @objc optional func onCallingFinish()
    @objc optional func onCallingError()
}

public class AUICallNVNController: NSObject {
    
    init(roomEngine: AUIRoomEngine, room: AUICallRoom, userConfig: AUICallRoomUserConfig) {
        self.roomEngine = roomEngine
        self.room = room
        self.userConfig = userConfig
        super.init()
        
        self.roomEngine.addObserver(delegate: self)
    }
    
    deinit {
        debugPrint("deinit: \(self)")
    }
    
    public enum InnerMsgType: Int {
        case removeUser = 11000
        case muteAudioAll = 12000
        case unmuteAudioAll = 13000
        case finish = 14000
        case roomUpdate = 15000
    }
    
    public let roomEngine: AUIRoomEngine
    public let room: AUICallRoom
    public var anchor: AUIRoomUser {
        return self.room.anchor
    }
    public let userConfig: AUICallRoomUserConfig
    
    private lazy var observerArray = {
        return NSHashTable<AUICallNVNControllerDelegate>.weakObjects()
    }()
    
    public var me: AUIRoomUser {
        return AUIRoomEngine.currrentUser!
    }
    
    public var isAnchor: Bool {
        return self.anchor.userId == self.me.userId
    }
    
    internal var renderLayoutView: AUICallNVNRenderViewLayout? {
        return self.roomEngine.renderViewLayout as? AUICallNVNRenderViewLayout
    }

    public private(set) var lastError: Error? {
        didSet {
            if self.lastError != nil {
                self.notifyCallingError()
            }
        }
    }
    
    public private(set) var callingSeconds = 0.0
    private lazy var countdownTimer: Timer = {
        let timer = Timer(timeInterval: 1.0, repeats: true) {[weak self] timer in
            if let self = self {
                self.callingSeconds = self.callingSeconds + 1.0
                self.notifyCallingTimeChanged()
                self.checkInviteUserTimeout()
            }
        }
        RunLoop.current.add(timer, forMode: .default)
        return timer
    }()
    
    public private(set) lazy var inviteUserList = {
        return [AUICallNVNInviteUser]()
    }()
    
    private func start() {
        if self.renderLayoutView == nil {
            let layout = AUICallNVNRenderViewLayout(anchor: self.anchor, me: self.me)
            self.roomEngine.renderViewLayout = layout
        }
        else {
            self.renderLayoutView?.removeFromSuperview()
            self.renderLayoutView?.getRenderViewList().forEach({ renderView in
                if let view = renderView as? AUICallNVNRenderView {
                    view.contentView.isHidden = false
                }
            })
        }
        
        self.roomEngine.startPreview()
        self.roomEngine.startPublish()
        self.roomEngine.switchCamera(off: self.userConfig.muteVideo, userId: self.me.userId)
        self.roomEngine.switchMicrophone(off: self.userConfig.muteAudio, userId: self.me.userId)
        self.roomEngine.switchAudioOutput(type: self.userConfig.disabledSpeaker ? .headset : .speaker)

        self.countdownTimer.fire()
    }
    
    public func finish() {
        self.cancelAllInviteUsers()
        if self.isAnchor {
            // 解散房间，通知所有人
            self.sendCommand(innerType: .finish, userId: nil, data: nil)
        }
        
        self.countdownTimer.invalidate()
        self.roomEngine.stopPreview()
        self.roomEngine.leave()
        self.roomEngine.renderViewLayout = nil
        AUICallNVNController.isCalling = false
        
        self.notifyCallingFinish()
    }
    
    public func muteAudioAll(mute: Bool) {
        if !self.isAnchor {
            return
        }
        
        self.room.isMuteAudioAll = mute
        self.sendCommand(innerType: mute ? .muteAudioAll : .unmuteAudioAll, userId: nil, data: nil)
    }
    
    public func muteAudio(mute: Bool, uid: String, force: Bool = false) {
        // 主播可以对所有人进行操作，全员静音时也可以
        if self.isAnchor {
            self.roomEngine.switchMicrophone(off: mute, userId: uid)
            return
        }
        
        // 成员仅能对自己操作
        if uid == self.me.userId {
            if self.room.isMuteAudioAll && !mute && !force {
                // 成员在全员静音，并且不允许强制开麦的条件下，不进行开麦
                return
            }
            self.roomEngine.switchMicrophone(off: mute, userId: uid)
        }
    }
    
    public func muteVideo(mute: Bool, uid: String) {
        // 主播可以对所有人进行操作，成员仅能对自己操作
        if self.isAnchor || uid == self.me.userId {
            self.roomEngine.switchCamera(off: mute, userId: uid)
        }
    }
    
    public func switchSpeaker(disabledSpeaker: Bool) {
        self.roomEngine.switchAudioOutput(type: disabledSpeaker ? .headset : .speaker)
        self.userConfig.disabledSpeaker = disabledSpeaker
        self.notifyCallAudioOuputTypeChanged(disabledSpeaker: self.userConfig.disabledSpeaker)
    }
    
    public func switchCamera() {
        let type = self.roomEngine.getCameraType()
        if type == .invalid {
            return
        }
        self.roomEngine.setCameraType(type: type == .front ? .back : .front)
    }
    
    public func switchPreviewMirror(on: Bool) {
        self.roomEngine.switchPreviewMirror(on: on)
    }
    
    public func isPreviewMirror() -> Bool {
        return self.roomEngine.getIsPreviewMirror()
    }
    
    public func removeUser(userId: String) {
        if !self.isAnchor || self.me.userId == userId {
            return
        }
        
        self.sendCommand(innerType: .removeUser, userId: userId, data: nil)
    }
}

extension AUICallNVNController {
    public func inviteUser(user: AUIRoomUser, completed: ((_ error: Error?)->Void)? = nil) {
        let result = self.renderLayoutView?.getRenderViewList().filter({ renderView in
            return renderView.user.userId == user.userId
        })
        if result != nil && result!.count > 0 {
            completed?(AUICallNVNController.createError(code: .common, msg: "User already in room"))
            return
        }
        
        let result2 = self.inviteUserList.filter({ inviteUser in
            return inviteUser.user.userId == user.userId
        })
        let inviteUser: AUICallNVNInviteUser? = result2.count > 0 ? result2.first : nil
        if inviteUser != nil && inviteUser!.state == .waiting {
            completed?(AUICallNVNController.createError(code: .common, msg: "User is inviting"))
            return
        }
        
        if inviteUser != nil {
            // 重新邀请
            self.roomEngine.requestJoin(roomId: self.room.roomId, userId: inviteUser!.user.userId, extra: self.extraDataForJoin()) {[weak self] error in
                if error == nil {
                    inviteUser?.startInvite()
                    self?.notifyCallUserMembersUpdated()
                }
                completed?(error)
            }
        }
        else {
            self.roomEngine.requestJoin(roomId: self.room.roomId, userId: user.userId, extra: self.extraDataForJoin()) {[weak self] error in
                if error == nil {
                    let inviteUser = AUICallNVNInviteUser(user)
                    inviteUser.startInvite()
                    self?.inviteUserList.append(inviteUser)
                    self?.notifyCallUserMembersUpdated()
                }
                completed?(error)
            }
        }
    }
    
    public func cancelInviteUser(user: AUIRoomUser) {
        let result = self.inviteUserList.filter({ inviteUser in
            return inviteUser.user.userId == user.userId && inviteUser.state == .waiting
        })
        if result.count > 0 {
            self.roomEngine.cancelRequestJoin(roomId: self.room.roomId, userId: user.userId)
            self.removeInviteUser(user: user)
        }
    }
    
    public func cancelAllInviteUsers() {
        let result = self.inviteUserList.filter({ inviteUser in
            return inviteUser.state == .waiting
        })
        result.forEach { inviteUser in
            self.roomEngine.cancelRequestJoin(roomId: self.room.roomId, userId: inviteUser.user.userId)
        }
        self.inviteUserList.removeAll()
        self.notifyCallUserMembersUpdated()
    }
    
    private func removeInviteUser(user: AUIRoomUser) {
        self.inviteUserList.removeAll { inviteUser in
            return inviteUser.user.userId == user.userId
        }
        self.notifyCallUserMembersUpdated()
    }
    
    private func onInviteUserResponse(accept: Bool, user: AUIRoomUser) {
        if accept {
            self.removeInviteUser(user: user)
        }
        else {
            let ret = self.inviteUserList.filter { inviteUser in
                return inviteUser.user.userId == user.userId
            }
            if let inviteUser = ret.first {
                inviteUser.reject()
                self.notifyCallUserMembersUpdated()
            }
        }
    }
    
    private func checkInviteUserTimeout() {
        var notify = false
        self.inviteUserList.forEach { inviteUser in
            let ret = inviteUser.checkTimeout()
            if ret {
                notify = ret
            }
        }
        if notify {
            self.notifyCallUserMembersUpdated()
        }
    }
    
    
}

extension AUICallNVNController {
    public func addObserver(delegate: AUICallNVNControllerDelegate) {
        if !self.observerArray.contains(delegate) {
            self.observerArray.add(delegate)
        }
    }
    
    public func removeObserver(delegate: AUICallNVNControllerDelegate) {
        self.observerArray.remove(delegate)
    }
    
    private func notifyCallUserMembersUpdated() {
        for delegate in self.observerArray.allObjects {
            if let onCallUserMembersUpdated = delegate.onCallUserMembersUpdated {
                onCallUserMembersUpdated()
            }
        }
    }
    
    private func notifyCallAudioOuputTypeChanged(disabledSpeaker: Bool) {
        for delegate in self.observerArray.allObjects {
            if let onCallAudioOuputTypeChanged = delegate.onCallAudioOuputTypeChanged {
                onCallAudioOuputTypeChanged(disabledSpeaker)
            }
        }
    }
    
    private func notifyCallAudioMuteStateChanged(userId: String, off: Bool) {
        for delegate in self.observerArray.allObjects {
            if let onCallAudioMuteStateChanged = delegate.onCallAudioMuteStateChanged {
                onCallAudioMuteStateChanged(userId, off)
            }
        }
    }
    
    private func notifyCallVideoMuteStateChanged(userId: String, off: Bool) {
        for delegate in self.observerArray.allObjects {
            if let onCallVideoMuteStateChanged = delegate.onCallVideoMuteStateChanged {
                onCallVideoMuteStateChanged(userId, off)
            }
        }
    }
    
    private func notifyCallMuteAudioAllChanged(off: Bool) {
        for delegate in self.observerArray.allObjects {
            if let onCallMuteAudioAllChanged = delegate.onCallMuteAudioAllChanged {
                onCallMuteAudioAllChanged(off)
            }
        }
    }
    
    private func notifyCallingTimeChanged() {
        for delegate in self.observerArray.allObjects {
            if let onCallingTimeChanged = delegate.onCallingTimeChanged {
                onCallingTimeChanged()
            }
        }
    }
    
    private func notifyCallingFinish() {
        for delegate in self.observerArray.allObjects {
            if let onCallingFinish = delegate.onCallingFinish {
                onCallingFinish()
            }
        }
    }
    
    private func notifyCallingError() {
        for delegate in self.observerArray.allObjects {
            if let onCallingError = delegate.onCallingError {
                onCallingError()
            }
        }
    }
}

extension AUICallNVNController {
    
    public internal(set) static var roomEngine: AUIRoomEngine?
    public internal(set) static var isCalling = false
    
    public static func createCall(roomId: String?, roomName: String?, completed: @escaping (_ room: AUICallRoom?, _ error: Error?)->Void) {
        guard let roomEngine = self.roomEngine else {
            completed(nil, self.createError(code: .common, msg: "param error"))
            return
        }
        roomEngine.create(roomId: roomId) { roomId, error in
            if let roomId = roomId {
                debugPrint("CreateCall & roomId:\(roomId)")
                let room = AUICallRoom(roomId, AUIRoomEngine.currrentUser!)
                room.roomName = roomName
                completed(room, nil)
            }
            else {
                completed(nil, error)
            }
        }
    }
    
    public static func startCall(roomName: String?, userConfig: AUICallRoomUserConfig, completed: @escaping (_ controller: AUICallNVNController?, _ error: Error?)->Void) {
        if self.isCalling {
            completed(nil, self.createError(code: .common, msg: "on calling"))
            return
        }
        
        guard let roomEngine = self.roomEngine else {
            completed(nil, self.createError(code: .common, msg: "param error"))
            return
        }
        
        self.isCalling = true
        self.createCall(roomId: nil, roomName: roomName) { room, error in
            if let room = room {
                self.joinRoom(roomEngine: roomEngine, room: room) { error in
                    if let error = error {
                        self.isCalling = false
                        completed(nil, error)
                        return
                    }
                    
                    let controller = AUICallNVNController(roomEngine: roomEngine, room: room, userConfig: userConfig)
                    controller.start()
                    completed(controller, nil)
                }
            }
            else {
                self.isCalling = false
                completed(nil, error)
            }
        }
    }
    
    public static func joinCall(room: AUICallRoom, userConfig: AUICallRoomUserConfig, completed: @escaping (_ controller: AUICallNVNController?, _ error: Error?)->Void) {
        if self.isCalling {
            completed(nil, self.createError(code: .common, msg: "on calling"))
            return
        }
        
        guard let roomEngine = self.roomEngine else {
            completed(nil, self.createError(code: .common, msg: "param error"))
            return
        }

        self.isCalling = true
        self.joinRoom(roomEngine: roomEngine, room: room) { error in
            if let error = error {
                self.isCalling = false
                completed(nil, error)
                return
            }
            
            let controller = AUICallNVNController(roomEngine: roomEngine, room: room, userConfig: userConfig)
            controller.start()
            completed(controller, nil)
        }
    }
    
    private static func joinRoom(roomEngine: AUIRoomEngine, room: AUICallRoom, completed: @escaping (_ error: Error?)->Void) {
        AUICallAppServer.fetchRoomEngineConfig(uid: AUIRoomEngine.currrentUser!.userId, roomId: room.roomId) { token, timestamp, error in
            if token != nil && timestamp != nil {
                let config = AUIRoomEngineConfig()
                config.appId = AUICallGlobalConfig.appID
                config.gslb = AUICallGlobalConfig.gslb
                config.roomId = room.roomId
                config.timestamp = timestamp!
                config.token = token!
                config.dimensions = AUICallGlobalConfig.dimensions
                config.frameRate = AUICallGlobalConfig.frameRate
                roomEngine.join(config: config) { error in
                    if error == nil {
                        completed(nil)
                    }
                    else {
                        completed(self.createError(code: .common, msg: ""))
                    }
                }
            }
            else {
                completed(self.createError(code: .common, msg: ""))
            }
        }
    }
}

extension AUICallNVNController {
    
    public enum ErrorCode: Int {
        case common = -1
    }
    
    private func errorOccurs(code: ErrorCode, msg: String?) {
        self.lastError = AUICallNVNController.createError(code: code, msg: msg)
    }
    
    public static func createError(code: ErrorCode, msg: String?) -> Error {
        let error = NSError(domain: "aui.call.nvn", code: code.rawValue, userInfo: [NSLocalizedDescriptionKey:msg ?? "unknown"])
        return error
    }
}

extension AUICallNVNController {
    
    public func sendCommand(innerType: InnerMsgType, userId: String?, data:[AnyHashable: Any]?) {
        var finalData: [AnyHashable: Any] = [
            "innerType": innerType.rawValue,
            "roomId": self.room.roomId
        ]
        if let data = data {
            finalData.merge(data) { first, _ in
                return first
            }
        }
        self.roomEngine.sendCustomMessage(message: finalData, roomId: self.room.roomId, userId: userId)
    }
    
    private func extraDataForJoin() -> [AnyHashable: Any] {
        return [
            "type": "group",
            "user": AUICallNVNController.extraData(user: self.me),
            "anchor": AUICallNVNController.extraData(user: self.anchor),
        ]
    }
    
    public static func validCall(extra: [AnyHashable: Any]?) -> Bool {
        if let extra = extra {
            let mode = extra["type"] as? String
            if mode == "group" {
                return true
            }
        }
        return false
    }
    
    public static func user(extra: [AnyHashable: Any]?, key: String) -> AUIRoomUser? {
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
    
    public static func extraData(user: AUIRoomUser) -> [AnyHashable: Any] {
        return  [
            "uid": user.userId,
            "nick": user.userNick,
            "avatar":user.userAvatar
        ]
    }
}


extension AUICallNVNController: AUIRoomEngineDelegate {
    
    public func onReceivedResponseJoin(user: AUIRoomUser, roomId: String, agree: Bool, extra: [AnyHashable : Any]?) {
        if roomId != self.room.roomId {
            return
        }
        
        if !agree {
            self.onInviteUserResponse(accept: false, user: user)
        }
    }
    
    public func onStartedPublish(user: AUIRoomUser) {
        debugPrint("onStartedPublish: \(user.userId)")
        if self.isAnchor {
            // 有人进房后，发送当前房间信息
            self.sendCommand(innerType: .roomUpdate, userId: user.userId, data: ["roomInfo": self.room.toData()])
        }
        
        self.onInviteUserResponse(accept: true, user: user)
    }
    
    public func onStopedPublish(user: AUIRoomUser) {
        debugPrint("onStopedPublish: \(user.userId)")

        self.notifyCallUserMembersUpdated()
    }
    
    public func onJoined(user: AUIRoomUser) {
    }
    
    public func onLeaved(user: AUIRoomUser) {
    }
    
    public func onExited() {
        AVAlertController.show(withTitle: nil, message: "你已退出房间", needCancel: false) { cancel in
            self.finish()
        }
    }
    
    private static var showingOpenCameraAlert = false
    public func onReceivedCameraStateChanged(user: AUIRoomUser, off: Bool) {
        if !off {
            
            if AUICallNVNController.showingOpenCameraAlert {
                return
            }
            AUICallNVNController.showingOpenCameraAlert = true
            AVAlertController.show(withTitle: nil, message: "主持人发起打开摄像头交流", cancelTitle: "拒绝", okTitle: "同意") { cancel in
                if !cancel {
                    self.muteVideo(mute: off, uid: self.me.userId)
                }
                AUICallNVNController.showingOpenCameraAlert = false
            }
        }
        else {
            self.muteVideo(mute: off, uid: self.me.userId)
            AUICallNVNController.showToast(text: "您已被主持人关闭摄像头")
        }
    }
    
    private static var showingOpenMicrophoneAlert : Bool = false
    public func onReceivedMicrophoneStateChanged(user: AUIRoomUser, off: Bool) {
        if !off {
            if AUICallNVNController.showingOpenMicrophoneAlert {
                return
            }
            AUICallNVNController.showingOpenMicrophoneAlert = true
            AVAlertController.show(withTitle: nil, message: "主持人发起语音交流", cancelTitle: "拒绝", okTitle: "同意") { cancel in
                if !cancel {
                    self.muteAudio(mute: off, uid: self.me.userId, force: true)
                }
                AUICallNVNController.showingOpenMicrophoneAlert = false
            }
        }
        else {
            self.muteAudio(mute: off, uid: self.me.userId)
            AUICallNVNController.showToast(text: "您已被主持人静音")
        }
    }
    
    public func onCameraStateChanged(user: AUIRoomUser, off: Bool) {
        if user.userId == self.me.userId {
            self.userConfig.muteVideo = off
        }
        self.renderLayoutView?.muteVideo(uid: user.userId, isMute: off)
        self.notifyCallVideoMuteStateChanged(userId: user.userId, off: off)
    }
    
    public func onMicrophoneStateChanged(user: AUIRoomUser, off: Bool) {
        if user.userId == self.me.userId {
            self.userConfig.muteAudio = off
        }
        self.renderLayoutView?.muteAudio(uid: user.userId, isMute: off)
        self.notifyCallAudioMuteStateChanged(userId: user.userId, off: off)
    }
    
    public func onReceivedCustomMessage(user: AUIRoomUser, message: [AnyHashable : Any]) {
        if let type = message["innerType"] as? Int {
            if type == InnerMsgType.removeUser.rawValue { // 移除用户 或者结束会议
                if !self.isAnchor {
                    self.finish()
                    AUICallNVNController.showToast(text: "您已被主持人移出房间")
                }
            }
            if type == InnerMsgType.finish.rawValue { // 移除用户 或者结束会议
                if !self.isAnchor {
                    self.finish()
                    AUICallNVNController.showToast(text: "主持人已解散房间")
                }
            }
            else if type == InnerMsgType.muteAudioAll.rawValue {
                self.room.isMuteAudioAll = true
                if !self.isAnchor {
                    self.muteAudio(mute: true, uid: self.me.userId)
                }
                self.notifyCallMuteAudioAllChanged(off: true)
            }
            else if type == InnerMsgType.unmuteAudioAll.rawValue {
                self.room.isMuteAudioAll = false
                self.notifyCallMuteAudioAllChanged(off: false)
            }
            else if type == InnerMsgType.roomUpdate.rawValue {
                debugPrint("onReceivedCustomMessage:\(message)")
                let roomId = message["roomId"] as? String
                if let roomId = roomId {
                    if roomId == self.room.roomId {
                        let data = message["roomInfo"] as? [AnyHashable : Any]
                        if let data = data {
                            if self.room.update(data: data) {
                                if !self.isAnchor && self.room.isMuteAudioAll {
                                    self.muteAudio(mute: true, uid: self.me.userId)
                                }
                                self.renderLayoutView?.anchorInfoUpdated()
                            }
                        }
                    }
                }
            }
        }
    }
}

extension AUICallNVNController {
    internal static func showToast(text: String) {
        var window: UIWindow? = nil
        if UIApplication.shared.delegate != nil {
            window = UIApplication.shared.delegate!.window!
        }
        if let window = window {
            AVToastView.show(text, view: window, position: .mid)
        }
    }
}
