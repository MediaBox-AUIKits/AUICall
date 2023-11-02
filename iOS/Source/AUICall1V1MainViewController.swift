//
//  AUICallMainViewController.swift
//  AUICall
//
//  Created by Bingo on 2023/6/14.
//

import UIKit
import AUIFoundation
import AUIRoomEngineSDK

public class AUICall1V1MainViewController: AVBaseViewController {
    
    deinit {
        AUICall1V1Manager.defaultManager.logout()
    }

    public override func viewDidLoad() {
        super.viewDidLoad()

        // Do any additional setup after loading the view.
        self.titleView.text = "1v1音视频通话"
        
        self.menuButton.setTitle("登出", for: .normal)
        self.menuButton.setImage(nil, for: .normal)
        self.menuButton.av_width = 60
        self.menuButton.av_right = self.headerView.av_width - 4
        self.menuButton.addTarget(self, action: #selector(onMenuClicked(sender:)), for: .touchUpInside)
        
        self.contentView.addSubview(self.inputIdView)
        self.contentView.addSubview(self.showIdLabel)
        self.contentView.addSubview(self.copyIdBtn)
        
        self.contentView.addSubview(self.audioCallBtn)
        self.contentView.addSubview(self.videoCallBtn)
        
        self.contentView.addSubview(self.startCallBtn)
        
        self.checkLogin(ani: false)
    }
    
    func checkLogin(ani: Bool) {
        if !AUICallLoginManager.isAppLogin {
            let loginVC = AUICallLoginViewController()
            loginVC.loginCompleted = { [weak self] loginVC in
                self?.updateUserId()
                self?.loginRoomEngine(curVC: loginVC) {
                    loginVC.dismiss(animated: true)
                }
            }
            self.av_presentFullScreenViewController(loginVC, animated: ani)
        }
        else {
            self.updateUserId()
            self.loginRoomEngine(curVC: self) {
            }
        }
    }
    
    func loginRoomEngine(curVC: UIViewController, completed: @escaping ()->Void) {
        let hud = AVProgressHUD.showAdded(to: curVC.view, animated: true)
        hud.iconType = .loading
        hud.labelText = "RoomEngine登录中..."
        hud.backgroundColor = AVTheme.tsp_fill_medium
        AUICall1V1Manager.defaultManager.tryLogin(loginUser: AUICallLoginManager.loginUser) { success in
            hud.hide(animated: false)
            if success {
                completed()
            }
            else {
                AVAlertController.show("RoomEngine登录失败", vc: curVC)
            }
        }
    }
    
    lazy var inputIdView: AUICallInputView = {
        let view = AUICallInputView(frame: CGRect(x: 16.0, y: 30.0, width: self.contentView.av_width - 32.0, height: 70.0))
        view.titleLabel.text = "呼叫用户ID"
        view.placeLabel.text = "请输入字母、数字、下划线"
        view.inputTextChanged = {[weak self] inputView in
            self?.startCallBtn.isEnabled = !inputView.inputText.isEmpty
        }
        return view
    }()
    
    lazy var showIdLabel: UILabel = {
        let label = UILabel(frame: CGRect(x: 20.0, y: self.inputIdView.av_bottom + 12.0, width: 0, height: 20.0))
        label.textColor = AVTheme.text_weak
        label.font = AVTheme.regularFont(12)
        return label
    }()
    
    lazy var copyIdBtn: UIButton = {
        let btn = UIButton(frame: CGRect(x: 0.0, y: self.showIdLabel.av_top - 6.0, width: 40.0, height: 32.0))
        btn.setTitle("复制", for: .normal)
        btn.setTitleColor(AVTheme.colourful_fill_strong, for: .normal)
        btn.titleLabel?.font = AVTheme.regularFont(12)
        btn.addTarget(self, action: #selector(onCopyIdBtnClicked), for: .touchUpInside)
        return btn
    }()
    
    lazy var audioCallBtn: UIButton = {
        let btn = UIButton(frame: CGRect(x: 20.0, y: self.showIdLabel.av_bottom + 36.0, width: 100.0, height: 22.0))
        btn.titleLabel?.font = AVTheme.regularFont(14)
        btn.setTitle("语音通话", for: .normal)
        btn.setTitleColor(AVTheme.text_strong, for: .normal)
        btn.setImage(AUICallBundle1V1.getImage("ic_radio_unselected"), for: .normal)
        btn.setImage(AUICallBundle1V1.getImage("ic_radio_selected"), for: .selected)
        btn.titleEdgeInsets = UIEdgeInsets(top: 0, left: 8, bottom: 0, right: -8)
        btn.isSelected = true
        btn.sizeToFit()
        btn.contentHorizontalAlignment = .left
        btn.av_width = btn.av_width + 8
        btn.addTarget(self, action: #selector(onAuidoCallBtnClicked), for: .touchUpInside)
        return btn
    }()
    
    lazy var videoCallBtn: UIButton = {
        let btn = UIButton(frame: CGRect(x: self.audioCallBtn.av_right + 20.0, y: self.showIdLabel.av_bottom + 36.0, width: 100.0, height: 22.0))
        btn.titleLabel?.font = AVTheme.regularFont(14)
        btn.setTitle("视频通话", for: .normal)
        btn.setTitleColor(AVTheme.text_strong, for: .normal)
        btn.setImage(AUICallBundle1V1.getImage("ic_radio_unselected"), for: .normal)
        btn.setImage(AUICallBundle1V1.getImage("ic_radio_selected"), for: .selected)
        btn.titleEdgeInsets = UIEdgeInsets(top: 0, left: 8, bottom: 0, right: -8)
        btn.isSelected = false
        btn.sizeToFit()
        btn.contentHorizontalAlignment = .left
        btn.av_width = btn.av_width + 8
        btn.addTarget(self, action: #selector(onVideoCallBtnClicked), for: .touchUpInside)
        return btn
    }()
    
    lazy var startCallBtn: UIButton = {
        let btn = AVBlockButton(frame: CGRect(x: 20.0, y: self.contentView.av_height - UIView.av_safeBottom - 44.0, width: self.contentView.av_width - 40.0, height: 44.0))
        btn.layer.cornerRadius = 22.0
        btn.layer.masksToBounds = true
        btn.isEnabled = false
        btn.setTitle("开始通话", for: .normal)
        btn.setBackgroundColor(AVTheme.colourful_fill_strong, for: .normal)
        btn.setBackgroundColor(AVTheme.colourful_fill_disabled, for: .disabled)
        btn.setTitleColor(AVTheme.text_strong, for: .normal)
        btn.titleLabel?.font = AVTheme.regularFont(16)
        btn.addTarget(self, action: #selector(onStartCallBtnClicked), for: .touchUpInside)
        return btn
    }()

    func updateUserId() -> Void {
        self.showIdLabel.text = "我的ID:" + (AUICallLoginManager.loginUser?.userId ?? "unknown")
        self.showIdLabel.sizeToFit()
        self.copyIdBtn.av_left = self.showIdLabel.av_right
    }
    
    @objc func onCopyIdBtnClicked() {
        guard let user = AUICallLoginManager.loginUser else { return }
        
        let pasteboard = UIPasteboard.general
        pasteboard.string = user.userId
        
        AVToastView.show("已复制您的ID", view: self.contentView, position: .mid)
    }
    
    @objc func onAuidoCallBtnClicked() {
        self.audioCallBtn.isSelected = true
        self.videoCallBtn.isSelected = false
    }
    
    @objc func onVideoCallBtnClicked() {
        self.audioCallBtn.isSelected = false
        self.videoCallBtn.isSelected = true
    }
    
    @objc func onStartCallBtnClicked() {
        guard let me = AUICall1V1Manager.defaultManager.me else {
            AVAlertController.show("请先登录！", vc: self)
            return
        }
        
        let userId = self.inputIdView.inputText
        if userId.isEmpty { return }
        
        if !AUICallLoginManager.validateUserId(userId) {
            AVAlertController.show("用户ID仅支持字母、数字和下划线", vc: self)
            return
        }
        
        if me.userId == userId {
            AVAlertController.show("不能跟自己通话，请重新输入呼叫用户ID", vc: self)
            return
        }
        
        let user = AUIRoomUser(userId)
        AUICallAppServer.updateUserInfo(user: user) { user in
            AUICall1V1Manager.defaultManager.startCall(mode: self.audioCallBtn.isSelected ? .audio : .video, destUser: user)
        }
    }
    
    // 禁止往左滑关闭vc
    public override func disableInteractivePopGesture() -> Bool {
        return AUICall1V1Manager.defaultManager.isCalling
    }
    
    public override func goBack() {
        if AUICall1V1Manager.defaultManager.isCalling {
            AVToastView.show("请先结束通话", view: self.view, position: .mid)
        }
        else {
            super.goBack()
        }
    }
    
    @objc func onMenuClicked(sender: UIButton) -> Void {
        if AUICall1V1Manager.defaultManager.isCalling {
            AVToastView.show("请先结束通话", view: self.view, position: .mid)
            return
        }
        
        AUICall1V1Manager.defaultManager.logout()
        AUICallLoginManager.logoutApp()
        DispatchQueue.main.async {
            self.checkLogin(ani: true)
        }
    }
}
