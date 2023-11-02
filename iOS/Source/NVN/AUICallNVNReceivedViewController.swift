//
//  AUICallNVNReceivedViewController.swift
//  AUICallExample
//
//  Created by Bingo on 2023/7/20.
//

import UIKit
import AUIFoundation
import SDWebImage

public class AUICallNVNReceivedViewController: UIViewController {

    init(controller: AUICallNVNReceivedController) {
        self.controller = controller
        super.init(nibName: nil, bundle: nil)
    }
    
    required init?(coder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }
    
    deinit {
        debugPrint("deinit: \(self)")
    }
    
    private let controller: AUICallNVNReceivedController

    public override func viewDidLoad() {
        super.viewDidLoad()
        
        self.view.backgroundColor = AVTheme.bg_medium
        
        if let renderLayoutView = self.controller.renderLayoutView {
            renderLayoutView.frame = self.view.bounds
            self.view.addSubview(renderLayoutView)
        }
        self.renderLayoutMaskView.frame = self.view.bounds
        
        self.userAvatarView.frame = CGRect(x: (self.view.av_width - 120) / 2.0, y: UIView.av_safeTop + 144, width: 120, height: 120)
        self.userNameView.frame = CGRect(x: 0, y: self.userAvatarView.av_bottom + 16, width: self.view.av_width, height: 30)
        self.tipsLabel.frame = CGRect(x: 0, y: self.userNameView.av_bottom + 4, width: self.view.av_width, height: 18)
        self.switchCameraBtn.frame = CGRect(x: self.view.av_width - 6 - 44, y: UIView.av_safeTop, width: 44, height: 44)
        
        let bot = self.view.av_height - UIView.av_safeBottom - 20
        self.handupBtn.center = CGPoint(x: 72 + self.handupBtn.av_width / 2.0, y: bot - self.handupBtn.av_height / 2.0)
        self.audioAcceptBtn.center = CGPoint(x: self.view.av_width - 72 - self.audioAcceptBtn.av_width / 2.0, y: bot - self.audioAcceptBtn.av_height / 2.0)
        
        let videoBot = bot - 110
        self.videoAcceptBtn.center = CGPoint(x: self.view.av_width / 2.0, y: videoBot - self.videoAcceptBtn.av_height / 2.0)
    }
    
    lazy var renderLayoutMaskView: UIView = {
        let view = UIView()
        view.backgroundColor = UIColor.av_color(withHexString: "141416", alpha: 0.4)
        self.view.addSubview(view)
        return view
    }()
    
    lazy var userAvatarView: UIImageView = {
        let img = UIImageView()
        img.contentMode = .scaleAspectFill
        img.layer.cornerRadius = 8.0
        img.layer.masksToBounds = true
        img.sd_setImage(with: URL(string: self.controller.caller.userAvatar), placeholderImage: AUICallBundle.getCommonImage("ic_default_avatar"))
        self.view.addSubview(img)
        return img
    }()
    
    lazy var userNameView: UILabel = {
        let label = UILabel()
        label.font = AVTheme.mediumFont(20)
        label.textColor = AVTheme.text_strong
        label.textAlignment = .center
        label.text = self.controller.caller.userNick == "" ? self.controller.caller.userId : self.controller.caller.userNick
        self.view.addSubview(label)
        return label
    }()
    
    lazy var tipsLabel: UILabel = {
        let label = UILabel()
        label.textColor = AVTheme.text_weak
        label.textAlignment = .center
        label.font = AVTheme.regularFont(12)
        self.view.addSubview(label)
        return label
    }()
    
    lazy var switchCameraBtn: UIButton = {
        let btn = UIButton()
        btn.imageEdgeInsets = UIEdgeInsets(top: 10, left: 10, bottom: 10, right: 10)
        btn.setImage(AUICallBundleNVN.getCommonImage("ic_switch_camera_received"), for: .normal)
        btn.addTarget(self, action: #selector(onSwitchCameraBtnClicked), for: .touchUpInside)
        self.view.addSubview(btn)
        return btn
    }()
    
    lazy var handupBtn: AUICallButton = {
        let btn = AUICallButton.create(title: "挂断", iconBgColor: AVTheme.danger_strong, normalIcon: AUICallBundleNVN.getCommonImage("ic_handup"))
        btn.av_size = CGSize(width: 68, height: 94)
        btn.tappedAction = {[weak self] btn in
            self?.controller.handup()
        }
        self.view.addSubview(btn)
        return btn
    }()
    
    lazy var audioAcceptBtn: AUICallButton = {
        let btn = AUICallButton.create(title: "接听", iconBgColor: AVTheme.success_ultrastrong, normalIcon: AUICallBundleNVN.getCommonImage("ic_accept_audio"))
        btn.av_size = CGSize(width: 68, height: 94)
        btn.tappedAction = {[weak self] btn in
            guard let self = self else { return }
            let userConfig = AUICallRoomUserConfig()
            userConfig.muteVideo = true
            self.controller.accept(userConfig: userConfig)
        }
        self.view.addSubview(btn)
        return btn
    }()
    
    lazy var videoAcceptBtn: AUICallButton = {
        let btn = AUICallButton.create(title: "视频接听", iconBgColor: AVTheme.tsp_fill_ultraweak, normalIcon: AUICallBundleNVN.getCommonImage("ic_accept_video"))
        btn.av_size = CGSize(width: 52, height: 78)
        btn.tappedAction = {[weak self] btn in
            guard let self = self else { return }
            let userConfig = AUICallRoomUserConfig()
            self.controller.accept(userConfig: userConfig)
        }
        self.view.addSubview(btn)
        return btn
    }()
}

extension AUICallNVNReceivedViewController {
    
    @objc func onSwitchCameraBtnClicked() {
        self.controller.switchCamera()
    }
}
