//
//  AUICallLoginViewController.swift
//  AUICall
//
//  Created by Bingo on 2023/6/14.
//

import UIKit
import AUIFoundation
import AUIRoomEngineSDK

public class AUICallLoginViewController: AVBaseViewController {

    public override func viewDidLoad() {
        super.viewDidLoad()
        

        // Do any additional setup after loading the view.
        self.contentView.addSubview(self.inputIdView)
        self.contentView.addSubview(self.nextBtn)

        self.hiddenBackButton = true
        self.hiddenMenuButton = true
        self.titleView.text = "APP登录"
        
        self.inputIdView.inputText = AUICallLoginManager.loadLastLoginUserId() ?? ""
    }
    
    var loginCompleted: ((_ currVC: AUICallLoginViewController)->Void)?
    
    lazy var inputIdView: AUICallInputView = {
        let view = AUICallInputView(frame: CGRect(x: 16.0, y: 30.0, width: self.contentView.av_width - 32.0, height: 70.0))
        view.titleLabel.text = "我的用户ID"
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
        btn.setTitle("登录", for: .normal)
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
        self.login(userId: userId, userNick: userId)
    }
    
    func login(userId: String, userNick: String) {
        let hud = AVProgressHUD.showAdded(to: self.view, animated: true)
        hud.backgroundColor = AVTheme.tsp_fill_medium
        hud.iconType = .loading
        hud.labelText = "APP登录中..."
        
        // 先登录APP
        AUICallLoginManager.loginApp(uid: userId) { error in
            hud.hide(animated: false)
            if let _ = error {
                AVAlertController.show("APP登录失败", vc: self)
            }
            else {
                AUICallLoginManager.saveLastLoginUserId(userId: userId)
                self.loginCompleted?(self)
            }
        }
    }
}


class AUICallLoginManager {
    
    static var lastLoginUserId: String? = nil
    static func loadLastLoginUserId() -> String? {
        self.lastLoginUserId = UserDefaults.standard.object(forKey: "auicall_last_login_user_id") as? String
        return self.lastLoginUserId
    }
    
    static func saveLastLoginUserId(userId: String?) {
        self.lastLoginUserId = userId
        UserDefaults.standard.set(self.lastLoginUserId, forKey: "auicall_last_login_user_id")
        UserDefaults.standard.synchronize()
    }
    
    static var loginUser: AUIRoomUser? = nil
    
    static var isAppLogin: Bool {
        return self.loginUser != nil
    }
    
    // app登录，这里仅模拟，客户需要使用自身的用户系统执行登录
    static func loginApp(uid: String, completed: @escaping (_ error: Error?) -> Void) {
        AUICallAppServer.request(path: "/api/v1/live/login", body: ["password": uid, "username": uid]) { response, data, error in
            if error == nil {
                let user = AUIRoomUser(uid)
                AUICallAppServer.updateUserInfo(user: user) { user in
                    self.loginUser = user
                    let auth = data?["token"] as? String
                    if auth != nil && !auth!.isEmpty {
                        AUICallAppServer.serverAuth = auth
                    }
                    completed(nil)
                }
            }
            else {
                completed(error)
            }
        }
    }
    
    // app登出，这里仅模拟
    static func logoutApp() {
        AUICallAppServer.serverAuth = nil
        self.loginUser = nil
    }
    
    static func validateUserId(_ userId: String) -> Bool {
        let set = CharacterSet(charactersIn: "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz_")
        if userId.rangeOfCharacter(from: set.inverted) == nil {
            return true
        }
        return false
    }
}
