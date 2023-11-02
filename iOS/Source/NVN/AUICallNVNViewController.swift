//
//  AUICallNVNViewController.swift
//  AUICallExample
//
//  Created by Bingo on 2023/7/4.
//

import UIKit
import AUIFoundation
import AUIRoomEngineSDK

public class AUICallNVNViewController: UIViewController {

    init(controller: AUICallNVNController) {
        self.callController = controller
        super.init(nibName: nil, bundle: nil)
        
        self.callController.addObserver(delegate: self)
        UIApplication.shared.isIdleTimerDisabled = true
    }
    
    required init?(coder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }
    
    deinit {
        UIApplication.shared.isIdleTimerDisabled = false
        self.callController.removeObserver(delegate: self)
        debugPrint("deinit: \(self)")
    }
    
    public override func viewDidLoad() {
        super.viewDidLoad()
        
        self.view.backgroundColor = AVTheme.bg_medium
        
        self.topView.frame = CGRect(x: 0, y: 0, width: self.view.av_width, height: UIView.av_safeTop + 44)
        self.topView.roomTitleLabel.text = self.callController.room.roomName ?? self.callController.room.roomId
        self.topView.finishBtn.clickBlock = {[weak self] btn in
            guard let self = self else {
                return
            }
            let msg = self.callController.isAnchor ? "确定是否解散房间？" : "确定是否退出房间？"
            AVAlertController.show(withTitle: "提示", message: msg, cancelTitle: "取消", okTitle: "确定") { cancel in
                if !cancel {
                    self.callController.finish()
                    AUICallNVNController.showToast(text: self.callController.isAnchor ? "房间已解散" : "您已退出房间")
                }
            }
        }
        self.topView.floatWindowBtn.clickBlock = {[weak self] btn in
            guard let self = self else {
                return
            }

            AUICallFloatWindow.startFloatWindow(floatVC: AUICallNVNFloatViewController(target: self))
        }
        self.topView.switchSpeakerBtn.isSelected = self.callController.userConfig.disabledSpeaker
        self.topView.switchSpeakerBtn.clickBlock = {[weak self] btn in
            guard let self = self else {
                return
            }
            self.callController.switchSpeaker(disabledSpeaker: !btn.isSelected)
        }
        
        self.bottomView.frame = CGRect(x: 0, y: self.view.av_height - UIView.av_safeBottom - 64, width: self.view.av_width, height: UIView.av_safeBottom + 64)
        self.bottomView.muteAudioBtn.selected = self.callController.userConfig.muteAudio
        self.bottomView.muteAudioBtn.action = {[weak self] btn in
            guard let self = self else {
                return
            }
            let mute = !btn.selected
            if !self.callController.isAnchor && self.callController.room.isMuteAudioAll && !mute  {
                AUICallNVNController.showToast(text: "主持人已开启了全员静音，无法开启麦克风")
                return
            }
            self.callController.muteAudio(mute: mute, uid: self.callController.me.userId)
        }
        self.bottomView.muteVideoBtn.selected = self.callController.userConfig.muteVideo
        self.bottomView.muteVideoBtn.action = {[weak self] btn in
            guard let self = self else {
                return
            }
            self.callController.muteVideo(mute: !btn.selected, uid: self.callController.me.userId)
        }
        self.bottomView.memberBtn.action = { [weak self] btn in
            guard let self = self else {
                return
            }
            let vc = AUICallNVNMembersViewController(callController: self.callController)
            self.navigationController?.pushViewController(vc, animated: true)
        }
        self.bottomView.beautyBtn.action = {[weak self] btn in
            guard let _ = self else {
                return
            }
            AUICallNVNController.showToast(text: "该功能暂未开放")
        }
        self.bottomView.moreBtn.action = {[weak self] btn in
            guard let self = self else {
                return
            }
            let panel = AUICallNVNMorePanel(frame: CGRect(x: 0, y: 0, width: self.view.av_width, height: 0))
            panel.show(on: self.view, with: .clickToClose)
            panel.switchCameraBtn.action = { btn in
                self.callController.switchCamera()
            }
            panel.switchMirrorBtn.action = { btn in
                self.callController.switchPreviewMirror(on: !btn.selected)
                btn.selected = self.callController.isPreviewMirror()
            }
            panel.switchMirrorBtn.selected = self.callController.isPreviewMirror()
        }
        
        if let renderLayoutView = self.callController.renderLayoutView {
            self.view.addSubview(renderLayoutView)
            renderLayoutView.frame = CGRect(x: 0, y: self.topView.av_bottom, width: self.view.av_width, height: self.bottomView.av_top - self.topView.av_bottom)
        }
        
        self.onCallingTimeChanged()
        self.updateMemberBtnTitle()
    }
    
    public override var shouldAutorotate: Bool {
        return false
    }
    
    public override var supportedInterfaceOrientations: UIInterfaceOrientationMask {
        return .portrait
    }
    
    public override var preferredInterfaceOrientationForPresentation: UIInterfaceOrientation {
        return .portrait
    }
    
    public override var preferredStatusBarStyle: UIStatusBarStyle {
        return .lightContent
    }
    
    var callController: AUICallNVNController
    weak var floatVC: AUICallNVNFloatViewController? = nil
    
    lazy var topView: AUICallNVNTopView = {
        let view = AUICallNVNTopView()
        self.view.addSubview(view)
        return view
    }()
    
    lazy var bottomView: AUICallNVNBottomView = {
        let view = AUICallNVNBottomView()
        self.view.addSubview(view)
        return view
    }()
    
    func updateMemberBtnTitle() {
        if let renderLayoutView = self.callController.renderLayoutView {
            self.bottomView.memberBtn.title = "成员(\(renderLayoutView.getRenderViewList().count))"
        }
        else {
            self.bottomView.memberBtn.title = "成员"
        }
    }
}

extension AUICallNVNViewController: AUICallNVNControllerDelegate {
    
    public func onCallUserMembersUpdated() {
        self.updateMemberBtnTitle()
    }
    
    public func onCallMuteAudioAllChanged(off: Bool) {
        // 普通用户在收到解除静音时，如果当前是静音状态，需要弹出提示
        if !self.callController.isAnchor && !off && self.callController.userConfig.muteAudio {
            AUICallNVNController.showToast(text: "主持人已解除全员静音，可自行开启麦克风")
        }
        else if off {
            AUICallNVNController.showToast(text: "主持人已开启全员静音")
        }
    }
    
    public func onCallAudioMuteStateChanged(userId: String, off: Bool) {
        if userId == self.callController.me.userId {
            self.bottomView.muteAudioBtn.selected = off
        }
    }
    
    public func onCallVideoMuteStateChanged(userId: String, off: Bool) {
        if userId == self.callController.me.userId {
            self.bottomView.muteVideoBtn.selected = off
        }
    }
    
    public func onCallAudioOuputTypeChanged(disabledSpeaker: Bool) {
        self.topView.switchSpeakerBtn.isSelected = disabledSpeaker
    }
    
    public func onCallingTimeChanged() {
        let time = AVStringFormat.format(withDuration: Float(self.callController.callingSeconds))
        self.topView.timeLabel.text = time
    }
    
    public func onCallingFinish() {
        if self.floatVC != nil {
            AUICallFloatWindow.exitFloatWindow(close: true)
        }
        else {
            self.close()
        }
    }
}

extension AUICallNVNViewController {
    public func show(topVC: UIViewController, _ ani: Bool = true) {
        let nav = AVNavigationController(rootViewController: self)
        topVC.av_presentFullScreenViewController(nav, animated: ani)
    }
    
    public func close(_ ani: Bool = true) {
        self.navigationController?.dismiss(animated: ani)
    }
}

extension AUICallNVNViewController: AVUIViewControllerInteractivePopGesture {
    // 禁止往左滑关闭vc
    public func disableInteractivePopGesture() -> Bool {
        return true
    }
}

extension AUICallNVNViewController: AUICallFloatTargetProtocol {
    public var targetView: UIView? {
        return self.callController.renderLayoutView
    }
    
    public var startFloatingFrame: CGRect {
        let width = 120.0
        let height = 120.0
        return CGRect(x: UIScreen.main.bounds.width - 16 - width, y: UIView.av_safeTop + 52.0, width: width, height: height)
    }
    
    public func startFloating(floatVC: AUICallFloatViewController) -> Void {
        if let renderLayoutView = self.callController.renderLayoutView {
            self.close(false)
            self.floatVC = floatVC as? AUICallNVNFloatViewController
            renderLayoutView.removeFromSuperview()
            renderLayoutView.isFloating = true
        }
    }
    
    public func exitFloating(floatVC: AUICallFloatViewController, close: Bool) -> Void {
        if let renderLayoutView = self.callController.renderLayoutView {
            self.floatVC = nil
            renderLayoutView.frame = CGRect(x: 0, y: self.topView.av_bottom, width: self.view.av_width, height: self.bottomView.av_top - self.topView.av_bottom)
            renderLayoutView.isFloating = false
            self.view.addSubview(renderLayoutView)

            if !close {
                if self.navigationController != nil {
                    UIViewController.av_top().av_presentFullScreenViewController(self.navigationController!, animated: false)
                }
                else {
                    self.show(topVC: UIViewController.av_top(), false)
                }
            }
        }
    }
}
