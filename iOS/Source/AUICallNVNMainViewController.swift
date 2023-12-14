//
//  AUICallNVNMainViewController.swift
//  AUICallExample
//
//  Created by Bingo on 2023/7/5.
//

import UIKit
import AUIFoundation
import AUIRoomEngineSDK

public class AUICallNVNMainViewController: AVBaseViewController {
    
    deinit {
        AUICallNVNManager.defaultManager.logout()
    }

    public override func viewDidLoad() {
        super.viewDidLoad()

        // Do any additional setup after loading the view.
        self.titleView.text = "多人音视频通话"
        
        self.menuButton.setTitle("登出", for: .normal)
        self.menuButton.setImage(nil, for: .normal)
        self.menuButton.av_width = 60
        self.menuButton.av_right = self.headerView.av_width - 4
        self.menuButton.addTarget(self, action: #selector(onMenuClicked(sender:)), for: .touchUpInside)
        
        self.contentView.addSubview(self.joinBtn)
        self.contentView.addSubview(self.startBtn)
        
        self.checkLogin(ani: false)
    }
    
    func checkLogin(ani: Bool) {
        if !AUICallLoginManager.isAppLogin {
            let loginVC = AUICallLoginViewController()
            loginVC.loginCompleted = { [weak self] loginVC in
                self?.loginRoomEngine(curVC: loginVC) {
                    loginVC.dismiss(animated: true)
                }
            }
            self.av_presentFullScreenViewController(loginVC, animated: ani)
        }
        else {
            self.loginRoomEngine(curVC: self) {
            }
        }
    }
    
    func loginRoomEngine(curVC: UIViewController, completed: @escaping ()->Void) {
        let hud = AVProgressHUD.showAdded(to: curVC.view, animated: true)
        hud.backgroundColor = AVTheme.tsp_fill_medium
        hud.iconType = .loading
        hud.labelText = "RoomEngine登录中..."
        AUICallNVNManager.defaultManager.tryLogin(loginUser: AUICallLoginManager.loginUser) { success in
            hud.hide(animated: false)
            if success {
                completed()
            }
            else {
                AVAlertController.show("RoomEngine登录失败", vc: curVC)
            }
        }
    }
    
    func createBtn() -> AVBlockButton {
        let btn = AVBlockButton(frame: CGRect(x: 20.0, y: 0, width: self.contentView.av_width - 40.0, height: 45.0))
        btn.layer.masksToBounds = true
        btn.setTitleColor(AVTheme.text_strong, for: .normal)
        btn.titleLabel?.font = AVTheme.regularFont(15)
        btn.contentHorizontalAlignment = .left
        
        let line = UIView(frame: CGRect(x: 0, y: 44, width: btn.av_width, height: 1))
        line.backgroundColor = AVTheme.border_weak
        btn.addSubview(line)
        
        let arraw = UIImageView(frame: CGRect(x: btn.av_width - 14, y: (btn.av_height - 14) / 2.0, width: 14, height: 14))
        arraw.image = AVTheme.image(withNamed: "ic_arraw", withModule: "AUIFoundation")
        btn.addSubview(arraw)
        
        return btn
    }

    lazy var startBtn: AVBlockButton = {
        let btn = self.createBtn()
        btn.av_top = 30.0
        btn.setTitle("创建房间", for: .normal)
        btn.addTarget(self, action: #selector(onStartBtnClicked), for: .touchUpInside)
        return btn
    }()
    
    lazy var joinBtn: AVBlockButton = {
        let btn = self.createBtn()
        btn.av_top = self.startBtn.av_bottom
        btn.setTitle("加入房间", for: .normal)
        btn.addTarget(self, action: #selector(onJoinBtnClicked), for: .touchUpInside)
        return btn
    }()
    
    @objc func onStartBtnClicked() {
        let create = AUICallNVNCreateViewController()
        self.navigationController?.pushViewController(create, animated: true)
    }
    
    @objc func onJoinBtnClicked() {
        let create = AUICallNVNJoinViewController()
        self.navigationController?.pushViewController(create, animated: true)
    }
    
    // 禁止往左滑关闭vc
    public override func disableInteractivePopGesture() -> Bool {
        return AUICallNVNManager.defaultManager.isCalling
    }
    
    public override func goBack() {
        if AUICallNVNManager.defaultManager.isCalling {
            AVToastView.show("请先结束通话", view: self.view, position: .mid)
        }
        else {
            super.goBack()
        }
    }
    
    @objc func onMenuClicked(sender: UIButton) -> Void {
        if AUICallNVNManager.defaultManager.isCalling {
            AVToastView.show("请先结束通话", view: self.view, position: .mid)
            return
        }
        
        AUICallNVNManager.defaultManager.logout()
        AUICallLoginManager.logoutApp()
        DispatchQueue.main.async {
            self.checkLogin(ani: true)
        }
    }
}
