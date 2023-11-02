//
//  AUICallNVNReceivedController.swift
//  AUICallExample
//
//  Created by Bingo on 2023/7/20.
//

import UIKit
import AUIRoomEngineSDK


public class AUICallNVNReceivedController: NSObject {

    init(roomEngine: AUIRoomEngine, room: AUICallRoom, caller: AUIRoomUser) {
        self.roomEngine = roomEngine
        self.room = room
        self.caller = caller
        super.init()
    }
    
    deinit {
        debugPrint("deinit: \(self)")
    }
    
    private let roomEngine: AUIRoomEngine
    public let room: AUICallRoom
    public let caller: AUIRoomUser
    public private(set) var userConfig = AUICallRoomUserConfig()
    
    public enum CallOverByReason {
        case none
        case inviteCancel     // 取消邀请通话
        case handup           // 自己挂断
        case timeout          // 超时
    }
    public private(set) var overReason: CallOverByReason = .none

    
    public var acceptCompleted: (()->Void)? = nil
    public var overCompleted: ((_ reason: CallOverByReason)->Void)? = nil

    private var anchor: AUIRoomUser {
        return self.room.anchor
    }
    
    private var me: AUIRoomUser {
        return AUIRoomEngine.currrentUser!
    }
    
    internal var renderLayoutView: AUICallNVNRenderViewLayout? {
        return self.roomEngine.renderViewLayout as? AUICallNVNRenderViewLayout
    }
    
    private func start() {
        let layout = AUICallNVNRenderViewLayout(anchor: self.anchor, me: self.me)
        self.roomEngine.renderViewLayout = layout
        self.roomEngine.startPreview()
        self.roomEngine.addObserver(delegate: self)
        self.startCallTimeoutCheck()
        
        if let renderView = self.roomEngine.renderViewLayout?.getRenderViewList().first as? AUICallNVNRenderView {
            renderView.contentView.isHidden = true
        }
        
        AUICallNVNController.isCalling = true
    }
    
    private func over(reason: CallOverByReason) {
        self.roomEngine.stopPreview()
        self.roomEngine.renderViewLayout = nil
        self.roomEngine.removeObserver(delegate: self)
        
        self.stopCallTimeoutCheck()
        AUICallNVNController.isCalling = false
        if reason == .handup {
            self.roomEngine.responseJoin(roomId: self.room.roomId, userId: self.caller.userId, agree: false, extra: nil)
        }
        self.overReason = reason
        self.overCompleted?(self.overReason)
    }
    
    public func accept(userConfig: AUICallRoomUserConfig) {
        self.stopCallTimeoutCheck()
        self.userConfig = userConfig
        self.roomEngine.removeObserver(delegate: self)
        AUICallNVNController.isCalling = false
        self.acceptCompleted?()
    }
    
    public func handup() {
        self.over(reason: .handup)
    }
    
    public func switchCamera() {
        let type = self.roomEngine.getCameraType()
        if type == .invalid {
            return
        }
        self.roomEngine.setCameraType(type: type == .front ? .back : .front)
    }
}

extension AUICallNVNReceivedController {
    
    private func startCallTimeoutCheck() {
        self.perform(#selector(onCallTimeout), with: nil, afterDelay: 60.0)
    }
    
    private func stopCallTimeoutCheck() {
        NSObject.cancelPreviousPerformRequests(withTarget: self, selector: #selector(onCallTimeout), object: nil)
    }
    
    @objc private  func onCallTimeout() {
        self.over(reason: .timeout)
    }
}

extension AUICallNVNReceivedController {
    
    public static func receiveCall(caller: AUIRoomUser, roomId: String, extra: [AnyHashable : Any]?) -> AUICallNVNReceivedController? {
        
        if !AUICallNVNController.validCall(extra: extra) {
            return nil
        }
        
        guard let roomEngine = AUICallNVNController.roomEngine else {
            return nil
        }
        
        if AUICallNVNController.isCalling {
            roomEngine.responseJoin(roomId: roomId, userId: caller.userId, agree: false, extra: nil)
            return nil
        }
        
        caller.update(user: AUICallNVNController.user(extra: extra, key: "user"))
        let anchor = AUICallNVNController.user(extra: extra, key: "anchor") ?? AUIRoomUser("")
        let room = AUICallRoom(roomId, anchor)
        room.roomName = "房间"
        let controller = AUICallNVNReceivedController(roomEngine: roomEngine, room: room, caller: caller)
        controller.start()
        return controller
    }
}

extension AUICallNVNReceivedController: AUIRoomEngineDelegate {
    
    public func onReceivedCancelRequestJoin(user: AUIRoomUser, roomId: String, extra: [AnyHashable : Any]?) {
        if roomId != self.room.roomId {
            return
        }
        self.over(reason: .inviteCancel)
    }
    
}
