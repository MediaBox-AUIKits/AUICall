//
//  AUICallNVNRenderViewLayout.swift
//  AUICallExample
//
//  Created by Bingo on 2023/7/5.
//

import UIKit
import AUIFoundation
import AUIRoomEngineSDK
import SDWebImage

class AUICallNVNRenderView : AUIRoomRenderView {
    
    override init(user: AUIRoomUser) {
        super.init(user: user)
        
        self.layer.borderColor = AVTheme.border_strong.cgColor
        self.layer.shadowColor = UIColor.black.cgColor
        self.layer.shadowRadius = 5
        self.layer.shadowOffset = CGSize(width: 0, height: 0)
        self.layer.shadowOpacity = 0.35

        self.addSubview(self.contentView)
        self.contentView.addSubview(self.userAvatarView)
        self.contentView.layer.addSublayer(self.gradientlayer)
        self.contentView.addSubview(self.anchorLabel)
        self.contentView.addSubview(self.meLabel)
        self.contentView.addSubview(self.userNameBtn)
        
        self.userNameBtn.isSelected = self.isAudioMute
        self.refreshUI()
    }
    
    required init?(coder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }
    
    deinit {
        debugPrint("deinit: \(self)")
    }
    
    override func layoutSubviews() {
        super.layoutSubviews()
        
        self.contentView.frame = self.bounds
        let av_height = self.contentView.av_height
        let av_width = self.contentView.av_width
        
        self.gradientlayer.frame = CGRect(x: 0, y: av_height - 40, width: av_width, height: 40)
        self.gradientlayer.removeAllAnimations()
        
        let left_right_margin = self.style == .large ? 20.0 : 4.0
        let bot_margin = self.style == .large ? 8.0 : 4.0
        var x = left_right_margin
        
        self.anchorLabel.av_left = x
        self.anchorLabel.av_bottom = av_height - bot_margin
        if !self.anchorLabel.isHidden {
            x = self.anchorLabel.av_right + 1
        }
        
        self.meLabel.av_left = x
        self.meLabel.av_bottom = av_height - bot_margin
        if !self.meLabel.isHidden {
            x = self.meLabel.av_right + 1
        }
        
        self.userNameBtn.sizeToFit()
        self.userNameBtn.frame = CGRect(x: x, y: self.anchorLabel.av_top, width: min(av_width - x - left_right_margin, self.userNameBtn.av_width) , height: 20)
        
        if self.style == .large {
            self.userAvatarView.frame = CGRect(x: (av_width - 120) / 2.0, y: (198.0 / 627.0) * av_height, width: 120, height: 120)
        }
        else {
            self.userAvatarView.frame = CGRect(x: (av_width - 60) / 2.0, y: (av_height - 60) / 2.0, width: 60, height: 60)
        }
    }
    
    override var style: AUIRoomRenderView.Style {
        didSet {
            self.refreshUI()
        }
    }
    
    internal lazy var contentView: UIView = {
        return UIView()
    }()
    
    fileprivate lazy var gradientlayer: CAGradientLayer = {
        let layer = CAGradientLayer()
        layer.cornerRadius = 2
        layer.startPoint = CGPoint(x: 0.5, y: 0.0)
        layer.endPoint = CGPoint(x: 0.5, y: 1.0)
        layer.colors = [UIColor.clear.cgColor, UIColor.black.withAlphaComponent(0.4).cgColor]
        return layer
    }()
    
    fileprivate lazy var userAvatarView: UIImageView = {
        let img = UIImageView()
        img.contentMode = .scaleAspectFill
        img.layer.cornerRadius = 8.0
        img.layer.masksToBounds = true
        img.isHidden = true
        return img
    }()
    
    fileprivate lazy var userNameBtn: UIButton = {
        let btn = UIButton()
        btn.backgroundColor = AVTheme.tsp_fill_infrared
        btn.setImage(AUICallBundleNVN.getCommonImage("ic_render_mic"), for: .normal)
        btn.setImage(AUICallBundleNVN.getCommonImage("ic_render_mic_selected"), for: .selected)
        btn.setTitle("", for: .normal)
        btn.setTitleColor(UIColor.av_color(withHexString: "3A3D48"), for: .normal)
        btn.titleLabel?.font = AVTheme.regularFont(12)
        btn.contentEdgeInsets = UIEdgeInsets(top: 0, left: 4, bottom: 0, right: 4)
        btn.titleLabel?.lineBreakMode = .byTruncatingTail
        btn.layer.cornerRadius = 2
        btn.layer.masksToBounds = true
        return btn
    }()
    
    fileprivate lazy var anchorLabel: UILabel = {
        let label = UILabel()
        label.backgroundColor = AVTheme.colourful_fill_strong
        label.textColor = AVTheme.text_strong
        label.textAlignment = .center
        label.font = AVTheme.regularFont(12)
        label.layer.cornerRadius = 2
        label.layer.masksToBounds = true
        label.isHidden = false
        label.text = "主持人"
        label.sizeToFit()
        label.av_size = CGSize(width: label.av_width + 8, height: 20)
        return label
    }()
    
    fileprivate lazy var meLabel: UILabel = {
        let label = UILabel()
        label.backgroundColor = AVTheme.success_ultrastrong
        label.textColor = AVTheme.text_strong
        label.textAlignment = .center
        label.font = AVTheme.regularFont(12)
        label.layer.cornerRadius = 2
        label.layer.masksToBounds = true
        label.isHidden = false
        label.text = "自己"
        label.sizeToFit()
        label.av_size = CGSize(width: label.av_width + 8, height: 20)
        return label
    }()
    
    fileprivate(set) var isVideoMute: Bool = false {
        didSet {
            self.refreshUI()
        }
    }
    
    fileprivate(set) var isAudioMute: Bool = false {
        didSet {
            self.userNameBtn.isSelected = self.isAudioMute
        }
    }
    
    fileprivate(set) var isMe: Bool = false {
        didSet {
            self.meLabel.isHidden = !self.isMe
            self.setNeedsLayout()
        }
    }
    
    fileprivate(set) var isAnchor: Bool = false {
        didSet {
            self.anchorLabel.isHidden = !self.isAnchor
            self.setNeedsLayout()
        }
    }
    
    fileprivate func startUpdateUserInfo() {
        AUICallAppServer.updateUserInfo(user: self.user) { [weak self] user in
            self?.refreshUserInfo()
        }
    }
    
    fileprivate func refreshUserInfo() {
        var name = self.user.userNick
        if name.isEmpty {
            name = self.user.userId
        }
        self.userNameBtn.setTitle(name, for: .normal)
        let avatar = self.user.userAvatar
        if avatar.count > 0 {
            self.userAvatarView.sd_setImage(with: URL(string: avatar), placeholderImage: AUICallBundle.getCommonImage("ic_default_avatar"))
        }
        else {
            self.userAvatarView.image = AUICallBundle.getCommonImage("ic_default_avatar")
        }
        self.setNeedsLayout()
    }
    
    private func refreshUI() {
        self.userAvatarView.isHidden = !self.isVideoMute
        self.displayView.isHidden = self.isVideoMute
        
        if self.style == .large {
            self.backgroundColor = UIColor.clear
            self.layer.cornerRadius = 0.0
            self.layer.borderWidth = 0.0
            
            self.displayView.layer.cornerRadius = 0.0
            self.userAvatarView.layer.cornerRadius = 8.0
            self.gradientlayer.colors = [UIColor.clear.cgColor, UIColor.black.withAlphaComponent(0.4).cgColor]
        }
        else {
            self.backgroundColor = AVTheme.fill_medium
            self.layer.cornerRadius = 2.0
            self.layer.borderWidth = 1.0
            
            self.displayView.layer.cornerRadius = 2.0
            self.userAvatarView.layer.cornerRadius = 4.0
            self.gradientlayer.colors = [UIColor.clear.cgColor, UIColor.black.withAlphaComponent(0.8).cgColor]
        }
        self.setNeedsLayout()
    }
}


class AUICallNVNRenderViewLayout: AUIRoomRenderViewDefaultLayout {

    init(anchor: AUIRoomUser, me: AUIRoomUser) {
        self.anchor = anchor
        self.me = me
        super.init(dimensions: AUICallGlobalConfig.dimensions)
    }
    
    required init?(coder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }
    
    private var anchor: AUIRoomUser
    private var me: AUIRoomUser
    
    override var smallStyleRenderViewDefaultFrame: CGRect {
        return CGRect(x: self.scrollView.av_width - 8 - 120, y: 16, width: 120, height: 120)
    }
    
    override func willInsertRenderView(renderView: AUIRoomRenderView) -> Int {
        let renderView = renderView as! AUICallNVNRenderView
        if renderView.isAnchor {
            return 0
        }
        let index = self.getRenderViewList().firstIndex { renderView in
            let renderView = renderView as! AUICallNVNRenderView
            return renderView.isMe && !renderView.isAnchor
        }
        if let index = index {
            return index
        }
        return super.willInsertRenderView(renderView: renderView)
    }

    override func createRenderView(user: AUIRoomUser) -> AUIRoomRenderView? {
        let view = AUICallNVNRenderView(user: user)
        if user.userId == self.anchor.userId {
            user.userNick = self.anchor.userNick
            user.userAvatar = self.anchor.userAvatar
        }
        view.isAnchor = user.userId == self.anchor.userId
        view.isMe = user.userId == self.me.userId
        view.refreshUserInfo()
        if !view.isMe {
            view.startUpdateUserInfo()
        }
        return view
    }
    
    override func willLayoutRenderViewList() {
        super.willLayoutRenderViewList()
        if self.isFloating {
            self.getRenderViewList().forEach { renderView in
                renderView.isHidden = true
            }
        }
        else {
            self.getRenderViewList().forEach { renderView in
                renderView.isHidden = false
            }
        }
    }
    
    override func layoutRenderViewList() {
        if self.isFloating {
            self.willLayoutRenderViewList()
            if let renderView = self.getRenderViewList().first {
                renderView.isHidden = false
                renderView.style = .normal
                renderView.frame = self.scrollView.bounds
            }
            return
        }
        
        super.layoutRenderViewList()
    }
    
    var isFloating: Bool = false {
        didSet {
            self.isUserInteractionEnabled = !self.isFloating
            self.setNeedsLayout()
        }
    }
    
    func anchorInfoUpdated() {
        self.getRenderViewList().forEach { renderView in
            let renderView = renderView as! AUICallNVNRenderView
            renderView.isAnchor = renderView.user.userId == self.anchor.userId
        }
    }
    
    func muteVideo(uid: String, isMute: Bool) {
        let list = self.getRenderViewList()
        list.forEach { renderView in
            if renderView.user.userId == uid {
                (renderView as! AUICallNVNRenderView).isVideoMute = isMute
            }
        }
    }
    
    func muteAudio(uid: String, isMute: Bool) {
        let list = self.getRenderViewList()
        list.forEach { renderView in
            if renderView.user.userId == uid {
                (renderView as! AUICallNVNRenderView).isAudioMute = isMute
            }
        }
    }
}
