//
//  AUICall1V1FloatViewController.swift
//  AUICallExample
//
//  Created by Bingo on 2023/7/3.
//

import UIKit
import AUIFoundation
import SDWebImage

public class AUICall1V1FloatViewController: AUICallFloatViewController {
    
    deinit {
        debugPrint("deinit: \(self)")
    }
    
    public override func startFloating(win: UIWindow, completed: (UIWindow, UIViewController) -> Void) {
        
        if let renderLayoutView = self.target.targetView {
            
            self.view.addSubview(self.userAvatarView)
            self.view.addSubview(self.timeLabel)
            self.view.addSubview(self.tipsLabel)
            
            var width = self.target.startFloatingFrame.width
            var height = self.target.startFloatingFrame.height
            if self.callVC.callController.mode == .audio {
                self.userAvatarView.frame = CGRect(x: 16, y: 16, width: 50, height: 50)
                self.userAvatarView.layer.cornerRadius = 2
                self.tipsLabel.frame = CGRect(x: 0, y: height - 20 - 16, width: width, height: 20)
                self.timeLabel.frame = CGRect(x: 0, y: height - 20 - 16, width: width, height: 20)
                self.timeLabel.backgroundColor = UIColor.clear
            }
            else {
                self.userAvatarView.frame = CGRect(x: 12, y: 36, width: 66, height: 66)
                self.userAvatarView.layer.cornerRadius = 4
                self.tipsLabel.frame = CGRect(x: 0, y: height - 20 - 8, width: width, height: 20)
                self.timeLabel.frame = CGRect(x: 0, y: height - 20 - 8, width: width, height: 20)
                self.timeLabel.backgroundColor = AVTheme.bg_medium
            }
            
            self.refreshTime()
            self.refreshUI()
            self.refreshTips()
            
            super.startFloating(win: win, completed: completed)
            self.view.sendSubviewToBack(renderLayoutView)
        }
    }
    
    public override func exitFloating(close: Bool, win: UIWindow, completed: (UIWindow, UIViewController) -> Void) {
        super.exitFloating(close: close, win: win, completed: completed)
    }
    
    private var callVC: AUICall1V1ViewController {
        return self.target as! AUICall1V1ViewController
    }
    
    private lazy var userAvatarView: UIImageView = {
        let img = UIImageView()
        img.contentMode = .scaleAspectFill
        img.layer.cornerRadius = 4.0
        img.layer.masksToBounds = true
        img.isHidden = true
        img.sd_setImage(with: URL(string: self.callVC.callController.destUser.userAvatar), placeholderImage: AUICallBundle.getCommonImage("ic_default_avatar"))
        return img
    }()
    
    private lazy var tipsLabel: UILabel = {
        let label = UILabel()
        label.textColor = AVTheme.text_strong
        label.textAlignment = .center
        label.font = AVTheme.regularFont(14)
        return label
    }()
    
    private lazy var timeLabel: UILabel = {
        let label = UILabel()
        label.backgroundColor = AVTheme.bg_medium
        label.textColor = AVTheme.text_strong
        label.textAlignment = .center
        label.font = AVTheme.regularFont(14)
        label.layer.cornerRadius = 2
        label.layer.masksToBounds = true
        label.isHidden = true
        return label
    }()
    
    func refreshUI() {
        let callController = self.callVC.callController
        let mode = callController.mode
        let state = callController.state
        self.timeLabel.isHidden = state != .connected
        self.userAvatarView.isHidden = mode == .video
        if mode == .audio {
            self.userAvatarView.isHidden = false
        }
        else {
            if state == .none || state == .waiting || state == .connecting {
                self.userAvatarView.isHidden = false
            }
            else {
                self.userAvatarView.isHidden = true
            }
        }
    }
    
    func refreshTips() {
        let callController = self.callVC.callController
        if callController.lastError != nil {
            self.tipsLabel.text = "通话出错"
            return
        }
        
        self.tipsLabel.text = ""
        switch callController.state {
        case .connecting:
            self.tipsLabel.text = "接通中…"
        case .waiting:
            self.tipsLabel.text = "等待接听"
        case .over:
            self.tipsLabel.text = "通话结束"
        default: break
        }
    }
    
    func refreshTime() {
        let center = self.timeLabel.center
        self.timeLabel.text = AVStringFormat.format(withDuration: Float(self.callVC.callController.callingSeconds))
        self.timeLabel.sizeToFit()
        self.timeLabel.av_width = self.timeLabel.av_width + 4
        self.timeLabel.av_height = 20
        self.timeLabel.center = center
    }
}
