//
//  AUICallNVNInviteViewController.swift
//  AUICallExample
//
//  Created by Bingo on 2023/7/19.
//

import UIKit
import AUIFoundation
import AUIRoomEngineSDK

public class AUICallNVNInviteViewController: AVBaseViewController {

    init(callController: AUICallNVNController) {
        self.callController = callController
        super.init(nibName: nil, bundle: nil)
    }
    
    required init?(coder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }
    
    deinit {
        debugPrint("deinit: \(self)")
    }
    
    public override func viewDidLoad() {
        super.viewDidLoad()

        // Do any additional setup after loading the view.
        self.contentView.addSubview(self.inputIdView)
        self.contentView.addSubview(self.nextBtn)

        self.hiddenBackButton = false
        self.hiddenMenuButton = true
        self.titleView.text = "邀请成员"
    }
    
    private let callController: AUICallNVNController
    
    lazy var inputIdView: AUICallInputView = {
        let view = AUICallInputView(frame: CGRect(x: 16.0, y: 30.0, width: self.contentView.av_width - 32.0, height: 70.0))
        view.titleLabel.text = "用户ID"
        view.placeLabel.text = "请输入字母、数字、下划线"
        view.inputTextChanged = {[weak self] inputView in
            self?.nextBtn.isEnabled = !inputView.inputText.isEmpty
        }
        return view
    }()
    
    lazy var nextBtn: AVBlockButton = {
        let btn = AVBlockButton(frame: CGRect(x: 20.0, y: self.contentView.av_height - UIView.av_safeBottom - 44.0, width: self.contentView.av_width - 40.0, height: 44.0))
        btn.layer.cornerRadius = 22.0
        btn.layer.masksToBounds = true
        btn.isEnabled = false
        btn.setTitle("确定", for: .normal)
        btn.setBackgroundColor(AVTheme.colourful_fill_strong, for: .normal)
        btn.setBackgroundColor(AVTheme.colourful_fill_disabled, for: .disabled)
        btn.setTitleColor(AVTheme.text_strong, for: .normal)
        btn.titleLabel?.font = AVTheme.regularFont(16)
        btn.addTarget(self, action: #selector(onNextBtnClicked), for: .touchUpInside)
        return btn
    }()
    
    @objc func onNextBtnClicked() {
        let userId = self.inputIdView.inputText
        if userId.isEmpty { return }
        
        if !AUICallLoginManager.validateUserId(userId) {
            AVAlertController.show("用户ID仅支持字母、数字和下划线", vc: self)
            return
        }
        
        if self.callController.me.userId == userId {
            AVAlertController.show("不能邀请自己通话，请重新输入呼叫用户ID", vc: self)
            return
        }
        
        let hud = AVProgressHUD.showAdded(to: self.view, animated: true)
        hud.backgroundColor = AVTheme.tsp_fill_medium
        hud.iconType = .loading

        let user = AUIRoomUser(userId)
        AUICallAppServer.updateUserInfo(user: user) {[weak self] user in
            guard let self = self else { return }
            self.callController.inviteUser(user: user) { error in
                hud.hide(animated: false)
                if let error = error {
                    AVToastView.show("邀请失败:\(error)", view: self.view, position: .mid)
                    return
                }
                else {
                    self.navigationController?.popViewController(animated: true)
                }
            }
        }
    }
}
