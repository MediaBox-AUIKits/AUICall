//
//  AUICall1V1ViewController.swift
//  AUICallExample
//
//  Created by Bingo on 2023/6/19.
//

import UIKit
import AUIFoundation

public class AUICall1V1ViewController: UIViewController {
    
    init(controller: AUICall1V1Controller) {
        self.callController = controller
        super.init(nibName: nil, bundle: nil)
        
        self.callController.delegate = self
        UIApplication.shared.isIdleTimerDisabled = true
    }
    
    required init?(coder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }
    
    deinit {
        UIApplication.shared.isIdleTimerDisabled = false
        debugPrint("deinit: \(self)")
    }
    
    public override func viewDidLoad() {
        super.viewDidLoad()
        
        self.view.backgroundColor = AVTheme.bg_medium
        
        self.callContentView.frame = self.view.bounds
        self.callContentView.addGestureRecognizer(UITapGestureRecognizer(target: self, action: #selector(onContentViewClicked(recognizer:))))
        
        self.floatWindowBtn.frame = CGRect(x: 6, y: UIView.av_safeTop, width: 44, height: 44)
        if self.callController.mode == .video {
            self.switchCameraBtn.frame = CGRect(x: self.view.av_width - 6 - 44, y: UIView.av_safeTop, width: 44, height: 44)
        }
        self.bottomView.frame = CGRect(x: 0, y: self.view.av_height - 300, width: self.view.av_width, height: 300)
        self.bottomView.isHidden = false
        
        self.bottomView.refreshUI()
        self.callContentView.refreshTips()
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
    
    var callController: AUICall1V1Controller
    
    lazy var callContentView: AUICall1V1ContentView = {
        let view = AUICall1V1ContentView(controller: self.callController)
        self.view.addSubview(view)
        return view
    }()
    
    lazy var floatWindowBtn: UIButton = {
        let btn = UIButton()
        btn.imageEdgeInsets = UIEdgeInsets(top: 10, left: 10, bottom: 10, right: 10)
        btn.setImage(AUICallBundle1V1.getCommonImage("ic_float_window"), for: .normal)
        btn.addTarget(self, action: #selector(onFloatWindowBtnClicked), for: .touchUpInside)
        self.view.addSubview(btn)
        return btn
    }()
    
    lazy var switchCameraBtn: UIButton = {
        let btn = UIButton()
        btn.imageEdgeInsets = UIEdgeInsets(top: 10, left: 10, bottom: 10, right: 10)
        btn.setImage(AUICallBundle1V1.getCommonImage("ic_switch_camera"), for: .normal)
        btn.addTarget(self, action: #selector(onSwitchCameraBtnClicked), for: .touchUpInside)
        self.view.addSubview(btn)
        return btn
    }()
    
    lazy var bottomView: AUICall1V1BottomView = {
        let view = AUICall1V1BottomView(controller: self.callController)
        self.view.addSubview(view)
        return view
    }()
    
    var immerseBottomView: AUICall1V1ImmerseBottomView? = nil
    func isImmerseBottomViewHidden(isHidden: Bool) {
        if !isHidden && self.immerseBottomView == nil {
            self.immerseBottomView = AUICall1V1ImmerseBottomView(frame: CGRect(x: 0, y: self.view.av_height - 95, width: self.view.av_width, height: 95))
            self.immerseBottomView!.timeLabel.text = AVStringFormat.format(withDuration: Float(self.callController.callingSeconds))
            self.view.addSubview(self.immerseBottomView!)
        }
        self.immerseBottomView?.isHidden = isHidden
    }
    
    weak var floatVC: AUICall1V1FloatViewController? = nil
}

extension AUICall1V1ViewController {
    
    @objc func onFloatWindowBtnClicked() {
        AUICallFloatWindow.startFloatWindow(floatVC: AUICall1V1FloatViewController(target: self))
    }
    
    @objc func onSwitchCameraBtnClicked() {
        self.callController.switchCamera()
    }
    
    @objc func onContentViewClicked(recognizer: UIGestureRecognizer) {
        if self.callController.mode == .video && self.callController.state == .connected {
            self.bottomView.isHidden = !self.bottomView.isHidden
            self.floatWindowBtn.isHidden = self.bottomView.isHidden
            self.switchCameraBtn.isHidden = self.bottomView.isHidden
            self.isImmerseBottomViewHidden(isHidden: !self.bottomView.isHidden)
        }
    }
}

extension AUICall1V1ViewController: AUICall1V1ControllerDelegate {
    
    public func onCallAudioOuputTypeChanged(disabledSpeaker: Bool) {
        self.bottomView.switchSpeakerBtn.isSelected = disabledSpeaker
    }
    
    public func onCallAudioMuteStateChanged(userId: String, off: Bool) {
        if userId == self.callController.me.userId {
            self.bottomView.muteAudioBtn.isSelected = off
        }
    }
    
    public func onCallVideoMuteStateChanged(userId: String, off: Bool) {
        if userId == self.callController.me.userId {
            self.bottomView.muteVideoBtn.isSelected = off
        }
        self.callController.renderLayoutView?.muteVideo(uid: userId, isMute: off)
    }
    
    public func onCallModeChanged() {
        if self.callController.mode == .audio {
            self.switchCameraBtn.isHidden = true
        }
        self.callContentView.refreshTips()
        self.bottomView.isHidden = false
        self.bottomView.refreshUI()
        self.callContentView.refreshUI()
        self.floatVC?.refreshUI()
    }
    
    public func onCallStateChanged() {
        if (self.callController.state == .over) {
            self.callContentView.refreshTips()
            
            DispatchQueue.main.asyncAfter(deadline: DispatchTime.now() + DispatchTimeInterval.milliseconds(100)) {
                if self.floatVC != nil {
                    AUICallFloatWindow.exitFloatWindow(close: true)
                }
                else {
                    self.dismiss(animated: true)
                }
                
                var tips: String? = nil
                if self.callController.overReason == .destHandup {
                    tips = "对方已结束通话"
                }
                else if self.callController.overReason == .calleeReject {
                    tips = "对方已拒绝"
                }
                else if self.callController.overReason == .meHandup {
                    tips = "已结束通话"
                }
                else if self.callController.overReason == .timeout {
                    tips = "呼叫未响应"
                }
                else if self.callController.overReason == .callerCancel {
                    tips = "对方已结束通话"
                }
                if let tips = tips {
                    var window: UIWindow? = nil
                    if UIApplication.shared.delegate != nil {
                        window = UIApplication.shared.delegate!.window!
                    }
                    if let window = window {
                        AVToastView.show(tips, view: window, position: .mid)
                    }
                }
            }
            return
        }
        
        self.callContentView.refreshTips()
        self.bottomView.isHidden = false
        self.bottomView.refreshUI()
        self.callContentView.refreshUI()
        self.floatVC?.refreshUI()
        self.floatVC?.refreshTips()
    }
    
    public func onCallingTimeChanged() {
        let time = AVStringFormat.format(withDuration: Float(self.callController.callingSeconds))
        self.bottomView.timeLabel.text = time
        self.immerseBottomView?.timeLabel.text = time
        self.floatVC?.refreshTime()
    }
    
    public func onCallingError() {
        self.callContentView.refreshTips()
        self.floatVC?.refreshTips()
    }
}

extension AUICall1V1ViewController: AUICallFloatTargetProtocol {
    public var targetView: UIView? {
        return self.callContentView.renderLayoutView
    }
    
    public var startFloatingFrame: CGRect {
        var width = 90.0
        var height = 160.0
        if self.callController.mode == .audio {
            width = 82.0
            height = 112.0
        }
        return CGRect(x: UIScreen.main.bounds.width - 16 - width, y: UIView.av_safeTop + 52.0, width: width, height: height)
    }
    
    public func startFloating(floatVC: AUICallFloatViewController) -> Void {
        if let renderLayoutView = self.callContentView.renderLayoutView {
            self.dismiss(animated: false)
            self.floatVC = floatVC as? AUICall1V1FloatViewController
            renderLayoutView.removeFromSuperview()
            renderLayoutView.isFloating = true
        }
    }
    
    public func exitFloating(floatVC: AUICallFloatViewController, close: Bool) -> Void {
        if let renderLayoutView = self.callContentView.renderLayoutView {
            self.floatVC = nil
            renderLayoutView.frame = self.callContentView.bounds
            renderLayoutView.isFloating = false
            self.callContentView.insertSubview(renderLayoutView, aboveSubview: self.callContentView.bgImageView)
            
            if !close {
                UIViewController.av_top().av_presentFullScreenViewController(self, animated: false)
            }
        }
    }
}
