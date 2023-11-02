//
//  AUICall1V1ContentView.swift
//  AUICallExample
//
//  Created by Bingo on 2023/6/20.
//

import UIKit
import AUIFoundation
import SDWebImage

class AUICall1V1ContentView: UIView {

    init(controller: AUICall1V1Controller) {
        self.callController = controller
        self.renderLayoutView = controller.renderLayoutView
        super.init(frame: CGRect.zero)
        
        self.bgImageView.addSubview(self.bgBlurView)
        self.addSubview(self.bgImageView)
        if let renderLayoutView = self.renderLayoutView {
            self.addSubview(renderLayoutView)
        }
        self.addSubview(self.renderLayoutMaskView)
        self.addSubview(self.userAvatarView)
        self.addSubview(self.userNameView)
        self.addSubview(self.tipsLabel)
        
        self.refreshUI()
    }
    
    required init?(coder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }
    
    override func layoutSubviews() {
        super.layoutSubviews()
        
        self.bgImageView.frame = self.bounds
        self.bgBlurView.frame = self.bgImageView.bounds
        
        if self.renderLayoutView?.superview == self {
            self.renderLayoutView?.frame = self.bounds
        }
        self.renderLayoutMaskView.frame = self.bounds
        
        self.userAvatarView.frame = CGRect(x: (self.av_width - 120) / 2.0, y: UIView.av_safeTop + 144, width: 120, height: 120)
        self.userNameView.frame = CGRect(x: 0, y: self.userAvatarView.av_bottom + 16, width: self.av_width, height: 30)
        
        self.tipsLabel.frame = CGRect(x: 0, y: self.userNameView.av_bottom + 4, width: self.av_width, height: 18)
    }
    
    var callController: AUICall1V1Controller

    var renderLayoutView: AUICall1V1RenderViewLayout?
    
    lazy var renderLayoutMaskView: UIView = {
        let view = UIView()
        view.backgroundColor = UIColor.av_color(withHexString: "141416", alpha: 0.4)
        return view
    }()

    lazy var bgImageView: UIImageView = {
        let img = UIImageView()
        img.alpha = 0.5
        img.contentMode = .scaleAspectFill
        img.sd_setImage(with: URL(string: self.callController.me.userAvatar), placeholderImage: nil)
        return img
    }()
    
    lazy var bgBlurView: UIVisualEffectView = {
        let blur = UIVisualEffectView(effect: UIBlurEffect(style: .dark))
        return blur
    }()
    
    lazy var userAvatarView: UIImageView = {
        let img = UIImageView()
        img.contentMode = .scaleAspectFill
        img.layer.cornerRadius = 8.0
        img.layer.masksToBounds = true
        img.sd_setImage(with: URL(string: self.callController.destUser.userAvatar), placeholderImage: AUICallBundle.getCommonImage("ic_default_avatar"))
        return img
    }()
    
    lazy var userNameView: UILabel = {
        let label = UILabel()
        label.font = AVTheme.mediumFont(20)
        label.textColor = AVTheme.text_strong
        label.textAlignment = .center
        label.text = self.callController.destUser.userNick == "" ? self.callController.destUser.userId : self.callController.destUser.userNick
        return label
    }()
    
    lazy var tipsLabel: UILabel = {
        let label = UILabel()
        label.textColor = AVTheme.text_weak
        label.textAlignment = .center
        label.font = AVTheme.regularFont(12)
        self.addSubview(label)
        return label
    }()
    
    func refreshUI() {
        let mode = self.callController.mode
        let state = self.callController.state
        if mode == .audio {
            self.userAvatarView.isHidden = false
            self.userNameView.isHidden = false
            self.renderLayoutView?.isHidden = true
            self.renderLayoutMaskView.isHidden = true
        }
        else {
            self.renderLayoutView?.isHidden = false
            if state == .none || state == .waiting || state == .connecting {
                self.userAvatarView.isHidden = false
                self.userNameView.isHidden = false
                self.renderLayoutMaskView.isHidden = false
            }
            else {
                self.userAvatarView.isHidden = true
                self.userNameView.isHidden = true
                self.renderLayoutMaskView.isHidden = true
            }
        }
    }
    
    func refreshTips() {
        if self.callController.lastError != nil {
            self.tipsLabel.text = "当前通话出现了错误"
            return
        }
        
        self.tipsLabel.text = ""
        switch self.callController.state {
        case .connecting:
            self.tipsLabel.text = "接通中…"
        case .waiting:
            if self.callController.role == .caller {
                self.tipsLabel.text = "正在等待对方接受邀请"
            }
            else {
                if self.callController.mode == .audio {
                    self.tipsLabel.text = "邀请你语音通话"
                }
                else {
                    self.tipsLabel.text = "邀请你视频通话"
                }
            }
        case .over:
            self.tipsLabel.text = "当前通话已结束"
        default: break
        }
    }
}
