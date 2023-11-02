//
//  AUICall1V1Manager.swift
//  AUICallExample
//
//  Created by Bingo on 2023/6/19.
//

import UIKit
import AUIRoomEngineSDK
import AUIMessage
import AUIFoundation

public class AUICall1V1Manager {
    
    public static let defaultManager = AUICall1V1Manager()
    
    init() {
        AUIRoomEngine.registerSDK(scene: "aui-call")
        AUIMessageServiceImpl_Alivc.globalGroupId = AUICallGlobalConfig.globaleGroupID;
    }
    
    public var me: AUIRoomUser? {
        return AUIRoomEngine.currrentUser
    }
    
    public func tryLogin(loginUser: AUIRoomUser?, completed: @escaping (_ success: Bool)->Void) {
        guard let loginUser = loginUser else {
            completed(false)
            return
        }
        
        if AUIRoomEngine.isLogin {
            if AUIRoomEngine.currrentUser!.userId == loginUser.userId {
                AUIRoomEngine.currrentUser!.userNick = loginUser.userNick
                AUIRoomEngine.currrentUser!.userAvatar = loginUser.userAvatar
                self.setupRoomEngine()
                return
            }
            
            self.logout { [weak self] in
                self?.login(loginUser: loginUser, completed: completed)
            }
            return
        }
        self.login(loginUser: loginUser, completed: completed)
    }
    
    private func login(loginUser: AUIRoomUser, completed: @escaping (_ success: Bool)->Void) {
        AUIRoomEngine.currrentUser = loginUser
        AUICallAppServer.fetchRoomEngineLoginToken(uid: loginUser.userId) { token, error in
            guard error == nil else {
                completed(false)
                return
            }
            guard let token = token else {
                completed(false)
                return
            }
            
            AUIRoomEngine.login(token: token) { error in
                if error == nil {
                    self.setupRoomEngine()
                }
                completed(error == nil)
            }
        }
    }
    
    public func logout(completed: (() -> Void)? = nil) {
        self.releaseRoomEngine()
        AUIRoomEngine.logout { error in
            completed?()
        }
    }
    
    private func setupRoomEngine() {
        AUICall1V1Controller.roomEngine = AUIRoomEngine()
        AUICall1V1Controller.roomEngine?.addObserver(delegate: self)
    }
    
    private func releaseRoomEngine() {
        AUICall1V1Controller.roomEngine?.removeObserver(delegate: self)
        AUICall1V1Controller.roomEngine?.destroy()
        AUICall1V1Controller.roomEngine = nil
    }
    
    public var isCalling: Bool {
        return AUICall1V1Controller.isCalling
    }
    
    public func startCall(mode: AUICall1V1Controller.CallMode, destUser: AUIRoomUser, viewController: UIViewController? = nil) {
        let controller = AUICall1V1Controller.startCall(mode: mode, callee: destUser)
        guard let controller = controller else {
            return
        }
        
        let topVC = viewController ?? UIViewController.av_top()
        topVC.av_presentFullScreenViewController(AUICall1V1ViewController(controller: controller), animated: false)
    }
}

extension AUICall1V1Manager: AUIRoomEngineDelegate {
    
    public func onReceivedRequestJoin(user: AUIRoomUser, roomId: String, extra: [AnyHashable : Any]?) {
        debugPrint("MainViewController##onReceivedRequesteJoin:\(user.userId)")
        
        let controller = AUICall1V1Controller.receiveCall(caller: user, roomId: roomId, extra: extra)
        guard let controller = controller else {
            return
        }
        
        let topVC = UIViewController.av_top()
        topVC.av_presentFullScreenViewController(AUICall1V1ViewController(controller: controller), animated: false)
    }
}
