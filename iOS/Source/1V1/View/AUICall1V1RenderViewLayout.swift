//
//  AUICall1V1RenderViewLayout.swift
//  AUICallExample
//
//  Created by Bingo on 2023/6/26.
//

import UIKit
import AUIFoundation
import AUIRoomEngineSDK
import SDWebImage

class AUICall1V1RenderView : AUIRoomRenderView {
    
    override init(user: AUIRoomUser) {
        super.init(user: user)
        
        self.layer.shadowColor = UIColor.black.cgColor
        self.layer.shadowRadius = 5
        self.layer.shadowOffset = CGSize(width: 0, height: 0)
        self.layer.shadowOpacity = 0.35

        self.addSubview(self.userAvatarView)
        
        let avatar = user.userAvatar
        if avatar.count > 0 {
            self.userAvatarView.sd_setImage(with: URL(string: avatar), placeholderImage: AUICallBundle1V1.getCommonImage("ic_default_avatar"))
        }
        else {
            self.userAvatarView.image = AUICallBundle.getCommonImage("ic_default_avatar")
        }
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
        
        if self.style == .large {
            self.userAvatarView.frame = CGRect(x: (self.av_width - 120) / 2.0, y: UIView.av_safeTop + 144, width: 120, height: 120)
        }
        else {
            self.userAvatarView.frame = CGRect(x: (self.av_width - 66) / 2.0, y: 36, width: 66, height: 66)
        }
    }
    
    override var style: AUIRoomRenderView.Style {
        didSet {
            self.refreshUI()
        }
    }
    
    private lazy var userAvatarView: UIImageView = {
        let img = UIImageView()
        img.contentMode = .scaleAspectFill
        img.layer.cornerRadius = 8.0
        img.layer.masksToBounds = true
        img.isHidden = true
        return img
    }()
    
    fileprivate(set) var isVideoMute: Bool = false {
        didSet {
            self.refreshUI()
        }
    }
    
    private func refreshUI() {
        self.userAvatarView.isHidden = !self.isVideoMute
        self.displayView.isHidden = self.isVideoMute
        
        if self.style == .large {
            self.backgroundColor = UIColor.clear
            self.layer.cornerRadius = 0.0
            
            self.displayView.layer.cornerRadius = 0.0
            self.userAvatarView.layer.cornerRadius = 8.0
        }
        else {
            self.backgroundColor = AVTheme.fill_medium
            self.layer.cornerRadius = 4.0
            
            self.displayView.layer.cornerRadius = 4.0
            self.userAvatarView.layer.cornerRadius = 4.0
        }
        self.setNeedsLayout()
    }
}

class AUICall1V1RenderViewLayout: AUIRoomRenderViewDefaultLayout {

    init(callController: AUICall1V1Controller) {
        super.init(dimensions: AUICallGlobalConfig.dimensions)
        self.callController = callController
    }
    
    required init?(coder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }
    
    private weak var callController: AUICall1V1Controller?

    override var smallStyleRenderViewDefaultFrame: CGRect {
        var radio = self.dimensions.width / self.dimensions.height
        if !radio.isNormal {
            radio = 9.0 / 16.0
        }
        return CGRect(x: self.av_width - 90.0 - 16.0, y: UIView.av_safeTop + 52.0, width: 90.0, height: 90.0 / radio)
    }
    
    override var smallStyleRenderViewSafeEdge: UIEdgeInsets {
        return UIEdgeInsets(top: UIView.av_safeTop + 52.0, left: 16.0, bottom: UIView.av_safeBottom, right: 16.0)
    }
    
    override func willInsertRenderView(renderView: AUIRoomRenderView) -> Int {
        if renderView.user.userId == self.callController?.destUser.userId {
            return 0
        }
        return super.willInsertRenderView(renderView: renderView)
    }
    
    override func createRenderView(user: AUIRoomUser) -> AUIRoomRenderView? {
        let view = AUICall1V1RenderView(user: user)
        return view
    }
    
    override func layoutRenderViewList() {
        if self.isFloating {
            self.getRenderViewList().forEach { renderView in
                renderView.isHidden = true
            }
            if self.getRenderViewList().count > 0 {
                let first = self.getRenderViewList().first!
                first.isHidden = false
                first.style = .small
                first.frame = self.scrollView.bounds
            }
            self.scrollView.contentSize = self.scrollView.bounds.size
            self.pageIndicators.forEach { view in
                view.isHidden = true
            }
            return
        }
        
        self.getRenderViewList().forEach { renderView in
            renderView.isHidden = false
        }
        self.pageIndicators.forEach { view in
            view.isHidden = false
        }
        super.layoutRenderViewList()
    }
    
    var isFloating: Bool = false {
        didSet {
            self.isUserInteractionEnabled = !self.isFloating
            self.setNeedsLayout()
        }
    }
    
    func muteVideo(uid: String, isMute: Bool) {
        let list = self.getRenderViewList()
        list.forEach { renderView in
            if renderView.user.userId == uid {
                (renderView as! AUICall1V1RenderView).isVideoMute = isMute
            }
        }
    }
}
