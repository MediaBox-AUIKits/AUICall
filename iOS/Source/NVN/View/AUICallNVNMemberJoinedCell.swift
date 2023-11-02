//
//  AUICallNVNMemberJoinedCell.swift
//  AUICallExample
//
//  Created by Bingo on 2023/7/26.
//

import UIKit
import AUIFoundation
import SDWebImage

class AUICallNVNMemberJoinedCell: UICollectionViewCell {
    
    override init(frame: CGRect) {
        super.init(frame: frame)
        
        self.contentView.addSubview(self.userAvatarView)
        self.contentView.addSubview(self.userNameLabel)
        self.contentView.addSubview(self.anchorLabel)
        self.contentView.addSubview(self.meLabel)
        self.contentView.addSubview(self.muteAudioBtn)
        self.contentView.addSubview(self.muteVideoBtn)
        self.contentView.addSubview(self.removeBtn)
        self.contentView.addSubview(self.lineView)
    }
    
    required init?(coder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }
    
    override func layoutSubviews() {
        super.layoutSubviews()
        
        self.userAvatarView.frame = CGRect(x: 20, y: (self.contentView.av_height - 48) / 2.0, width: 48, height: 48)
        
        var x = self.userAvatarView.av_right + 12.0
        self.anchorLabel.av_left = x
        self.anchorLabel.av_top = (self.contentView.av_height - (22 + 4 + self.anchorLabel.av_height)) / 2.0 + 22 + 4
        if !self.anchorLabel.isHidden {
            x = self.anchorLabel.av_right + 2
        }
        
        self.meLabel.av_left = x
        self.meLabel.av_top = self.anchorLabel.av_top
        
        
        var right = self.contentView.av_width
        self.removeBtn.frame = CGRect(x: right - 20 - 50, y: (self.contentView.av_height - 22) / 2.0, width: 50, height: 22)
        right = self.canRemove ? self.removeBtn.av_left : right - 8
        
        self.muteVideoBtn.frame = CGRect(x: right - 30 - 6, y: (self.contentView.av_height - 30) / 2.0, width: 30, height: 30)
        self.muteAudioBtn.frame = CGRect(x: self.muteVideoBtn.av_left - 30, y: (self.contentView.av_height - 30) / 2.0, width: 30, height: 30)

        let showingFlag = !self.anchorLabel.isHidden || !self.meLabel.isHidden
        self.userNameLabel.frame = CGRect(x: self.anchorLabel.av_left, y: 0, width: self.muteAudioBtn.av_left - self.anchorLabel.av_left, height: 22)
        self.userNameLabel.av_centerY = showingFlag ? self.anchorLabel.av_top - 4 - 22 / 2.0 : self.contentView.av_height / 2.0
        
        self.lineView.frame = CGRect(x: 20, y: self.contentView.av_height - 1, width: self.contentView.av_width - 40, height: 1)
    }
    
    lazy var userAvatarView: UIImageView = {
        let img = UIImageView()
        img.contentMode = .scaleAspectFill
        img.layer.cornerRadius = 24.0
        img.layer.masksToBounds = true
        return img
    }()
    
    lazy var userNameLabel: UILabel = {
        let label = UILabel()
        label.textColor = AVTheme.text_strong
        label.font = AVTheme.regularFont(14)
        return label
    }()
    
    lazy var anchorLabel: UILabel = {
        let label = UILabel()
        label.backgroundColor = AVTheme.colourful_fill_strong
        label.textColor = AVTheme.text_strong
        label.textAlignment = .center
        label.font = AVTheme.regularFont(12)
        label.layer.cornerRadius = 2
        label.layer.masksToBounds = true
        label.isHidden = true
        label.text = "主持人"
        label.sizeToFit()
        label.av_size = CGSize(width: label.av_width + 8, height: 20)
        return label
    }()
    
    lazy var meLabel: UILabel = {
        let label = UILabel()
        label.backgroundColor = AVTheme.success_ultrastrong
        label.textColor = AVTheme.text_strong
        label.textAlignment = .center
        label.font = AVTheme.regularFont(12)
        label.layer.cornerRadius = 2
        label.layer.masksToBounds = true
        label.isHidden = true
        label.text = "自己"
        label.sizeToFit()
        label.av_size = CGSize(width: label.av_width + 8, height: 20)
        return label
    }()
    
    lazy var lineView: UIView = {
        let view = UIView()
        view.backgroundColor = AVTheme.border_weak
        return view
    }()
    
    lazy var muteAudioBtn: AVBlockButton = {
        let btn = AVBlockButton()
        btn.setImage(AUICallBundleNVN.getCommonImage("ic_member_mic"), for: .normal)
        btn.setImage(AUICallBundleNVN.getCommonImage("ic_member_mic_selected"), for: .selected)
        return btn
    }()
    
    lazy var muteVideoBtn: AVBlockButton = {
        let btn = AVBlockButton()
        btn.setImage(AUICallBundleNVN.getCommonImage("ic_member_camera"), for: .normal)
        btn.setImage(AUICallBundleNVN.getCommonImage("ic_member_camera_selected"), for: .selected)
        return btn
    }()
    
    lazy var removeBtn: AVBlockButton = {
        let btn = AVBlockButton()
        btn.setTitle("移除", for: .normal)
        btn.setTitleColor(AVTheme.text_strong, for: .normal)
        btn.titleLabel?.font = AVTheme.regularFont(12)
        btn.layer.cornerRadius = 11
        btn.layer.borderWidth = 1
        btn.layer.masksToBounds = true
        btn.isHidden = true
        btn.setBorderColor(AVTheme.border_strong, for: .normal)
        return btn
    }()
    
    var canRemove: Bool = false {
        didSet {
            self.refreshUI()
        }
    }
    
    var renderView: AUICallNVNRenderView? {
        didSet {
            self.refreshUI()
        }
    }
    
    func refreshUI() {
        if let renderView = self.renderView {
            let user = renderView.user
            self.anchorLabel.isHidden = !renderView.isAnchor
            self.meLabel.isHidden = !renderView.isMe
            if user.userAvatar.count > 0 {
                self.userAvatarView.sd_setImage(with: URL(string: user.userAvatar), placeholderImage: AUICallBundle.getCommonImage("ic_default_avatar"))
            }
            else {
                self.userAvatarView.image = AUICallBundle.getCommonImage("ic_default_avatar")
            }
            var name = user.userNick
            if renderView.isMe {
//                name = "自己"
            }
            if name.isEmpty {
                name = user.userId
            }
            self.userNameLabel.text = name
            self.muteAudioBtn.isSelected = renderView.isAudioMute
            self.muteVideoBtn.isSelected = renderView.isVideoMute
        }
        else {
            self.anchorLabel.isHidden = false
            self.meLabel.isHidden = false
            self.userAvatarView.image = AUICallBundle.getCommonImage("ic_default_avatar")
            self.userNameLabel.text = ""
            self.muteAudioBtn.isSelected = false
            self.muteVideoBtn.isSelected = false
        }
        
        if self.anchorLabel.isHidden {
            self.removeBtn.isHidden = !self.canRemove
        }
        else {
            self.removeBtn.isHidden = true
        }
        
        self.setNeedsLayout()
    }
}
