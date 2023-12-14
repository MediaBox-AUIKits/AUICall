//
//  AUICallNVNManager.swift
//  AUICallExample
//
//  Created by Bingo on 2023/7/5.
//

import UIKit
import AUIRoomEngineSDK
import AUIMessage
import AUIFoundation

public class AUICallNVNManager {
    
    public static let defaultManager = AUICallNVNManager()
    
    init() {
        AUIRoomEngine.registerSDK(scene: "aui-call")
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
        AUICallAppServer.fetchRoomEngineLoginToken(uid: loginUser.userId) { tokenData, error in
            guard error == nil else {
                completed(false)
                return
            }
            guard let tokenData = tokenData else {
                completed(false)
                return
            }
            
            AUIRoomEngine.login(tokenData: tokenData) { error in
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
        AUICallNVNController.roomEngine = AUIRoomEngine()
        AUICallNVNController.roomEngine?.addObserver(delegate: self)
    }
    
    private func releaseRoomEngine() {
        AUICallNVNController.roomEngine?.removeObserver(delegate: self)
        AUICallNVNController.roomEngine?.destroy()
        AUICallNVNController.roomEngine = nil
    }
    
    public var isCalling: Bool {
        return AUICallNVNController.isCalling
    }
    
    // 通过id、name与config创建一个通话房间，并进入
    public func createCall(roomId: String, roomName: String?, userConfig: AUICallRoomUserConfig, currVC: UIViewController? = nil, completed: (()->Void)? = nil) {
        let topVC = currVC ?? UIViewController.av_top()
        let hud = AVProgressHUD.showAdded(to: topVC.view, animated: true)
        hud.backgroundColor = AVTheme.tsp_fill_medium
        hud.iconType = .loading
        hud.labelText = "创建房间号中"
        AUICallNVNController.createCall(roomId: roomId, roomName: roomName) { room, error in
            hud.hide(animated: false)
            if let error = error {
                AVToastView.show("房间号创建失败:\(error)", view: topVC.view, position: .mid)
                return
            }

            guard let room = room else {
                return
            }
            
            self.joinCall(room: room, userConfig: userConfig, currVC: currVC) {
                completed?()
            }
        }
    }
    
    // 通过roomName与config开始通话
    public func startCall(roomName: String?, userConfig: AUICallRoomUserConfig, currVC: UIViewController? = nil, completed: (()->Void)? = nil) {
        let topVC = currVC ?? UIViewController.av_top()
        let hud = AVProgressHUD.showAdded(to: topVC.view, animated: true)
        hud.backgroundColor = AVTheme.tsp_fill_medium
        hud.iconType = .loading
        hud.labelText = "创建中..."
        AUICallNVNController.startCall(roomName: roomName, userConfig: userConfig) { controller, error in
            hud.hide(animated: false)
            if let error = error {
                AVToastView.show("创建房间失败:\(error)", view: topVC.view, position: .mid)
                return
            }
            
            guard let controller = controller else {
                return
            }
            
            self.presentNVNViewController(controller: controller, topVC: topVC)
            completed?()
        }
    }
    
    // 进入通话
    public func joinCall(room: AUICallRoom, userConfig: AUICallRoomUserConfig, currVC: UIViewController? = nil, completed: (()->Void)? = nil) {
        let topVC = currVC ?? UIViewController.av_top()
        let hud = AVProgressHUD.showAdded(to: topVC.view, animated: true)
        hud.backgroundColor = AVTheme.tsp_fill_medium
        hud.iconType = .loading
        hud.labelText = "加入中..."
        AUICallNVNController.joinCall(room: room, userConfig: userConfig) { controller, error in
            hud.hide(animated: false)
            if let error = error {
                AVToastView.show("加入房间失败:\(error)", view: topVC.view, position: .mid)
                return
            }
            
            guard let controller = controller else {
                return
            }
            
            self.presentNVNViewController(controller: controller, topVC: topVC)
            completed?()
        }
    }
    
    private func presentNVNViewController(controller: AUICallNVNController, topVC: UIViewController) {
        let nvn = AUICallNVNViewController(controller: controller)
        nvn.show(topVC: topVC)
    }
}

extension AUICallNVNManager: AUIRoomEngineDelegate {
    
    public func onReceivedRequestJoin(user: AUIRoomUser, roomId: String, extra: [AnyHashable : Any]?) {
        debugPrint("MainViewController##onReceivedRequesteJoin:\(user.userId)")
        
        let controller = AUICallNVNReceivedController.receiveCall(caller: user, roomId: roomId, extra: extra)
        guard let controller = controller else {
            return
        }
        
        let vc = AUICallNVNReceivedViewController(controller: controller)
        let topVC = UIViewController.av_top()
        topVC.av_presentFullScreenViewController(vc, animated: false)
        
        controller.overCompleted = { [weak vc] reason in
            vc?.dismiss(animated: true)
        }
        controller.acceptCompleted = {[weak vc, weak controller] in
            guard let controller = controller else { return }
            let topVC = vc ?? UIViewController.av_top()
            let hud = AVProgressHUD.showAdded(to: topVC.view, animated: true)
            hud.backgroundColor = AVTheme.tsp_fill_medium
            hud.iconType = .loading
            hud.labelText = "接通中..."
            AUICallNVNController.joinCall(room: controller.room, userConfig: controller.userConfig) { controller, error in
                hud.hide(animated: false)
                if let error = error {
                    AVToastView.show("加入房间失败:\(error)", view: topVC.view, position: .mid)
                    DispatchQueue.main.asyncAfter(deadline: DispatchTime.now() + DispatchTimeInterval.milliseconds(2000)) {
                        vc?.dismiss(animated: true)
                    }
                    return
                }

                guard let controller = controller else {
                    vc?.dismiss(animated: true)
                    return
                }

                vc?.dismiss(animated: false)
                DispatchQueue.main.asyncAfter(deadline: DispatchTime.now() + DispatchTimeInterval.milliseconds(300)) {
                    self.presentNVNViewController(controller: controller, topVC: UIViewController.av_top())
                }
            }
        }
    }
}
