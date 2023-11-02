//
//  AUICallNVNMemberWaitingCell.swift
//  AUICallExample
//
//  Created by Bingo on 2023/7/26.
//

import UIKit
import AUIFoundation
import AUIRoomEngineSDK
import SDWebImage

class AUICallNVNMemberInviteCell: UICollectionViewCell {
    override init(frame: CGRect) {
        super.init(frame: frame)
        
        self.contentView.addSubview(self.userAvatarView)
        self.contentView.addSubview(self.userNameLabel)
        self.contentView.addSubview(self.waitingLabel)
        self.contentView.addSubview(self.actionBtn)
        self.contentView.addSubview(self.lineView)
    }
    
    required init?(coder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }
    
    override func layoutSubviews() {
        super.layoutSubviews()
        
        self.userAvatarView.frame = CGRect(x: 20, y: (self.contentView.av_height - 48) / 2.0, width: 48, height: 48)
        self.waitingLabel.av_left = self.userAvatarView.av_right + 12
        self.waitingLabel.av_top = (self.contentView.av_height - (22 + 4 + self.waitingLabel.av_height)) / 2.0 + 22 + 4
        
        self.actionBtn.frame = CGRect(x: self.contentView.av_width - 20 - 50, y: (self.contentView.av_height - 22) / 2.0, width: 50, height: 22)
        
        self.userNameLabel.frame = CGRect(x: self.waitingLabel.av_left, y: 0, width: self.actionBtn.av_left - self.waitingLabel.av_left, height: 22)
        self.userNameLabel.av_centerY = self.waitingLabel.isHidden ? self.contentView.av_height / 2.0 : self.waitingLabel.av_top - 4 - 22 / 2.0
        
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
    
    lazy var waitingLabel: UILabel = {
        let label = UILabel()
        label.backgroundColor = AVTheme.fill_medium
        label.textColor = AVTheme.text_strong
        label.textAlignment = .center
        label.font = AVTheme.regularFont(12)
        label.layer.cornerRadius = 2
        label.layer.masksToBounds = true
        label.text = "呼叫中"
        label.sizeToFit()
        label.av_size = CGSize(width: label.av_width + 8, height: 20)
        return label
    }()
    
    lazy var lineView: UIView = {
        let view = UIView()
        view.backgroundColor = AVTheme.border_weak
        return view
    }()
    
    lazy var actionBtn: AVBlockButton = {
        let btn = AVBlockButton()
        btn.setTitle("取消", for: .normal)
        btn.setTitle("邀请", for: .selected)
        btn.setTitleColor(AVTheme.text_strong, for: .normal)
        btn.titleLabel?.font = AVTheme.regularFont(12)
        btn.layer.cornerRadius = 11
        btn.layer.borderWidth = 1
        btn.layer.masksToBounds = true
        btn.setBorderColor(AVTheme.border_strong, for: .normal)
        return btn
    }()
    
    var invitedUser: AUICallNVNInviteUser? {
        didSet {
            self.refreshInviteState()
            self.refreshUserInfo()
        }
    }
    
    func refreshInviteState() {
        let timeout = self.invitedUser != nil && self.invitedUser!.state == .over
        self.waitingLabel.text = timeout ? "未接听" : "呼叫中"
        self.actionBtn.isSelected = timeout
    }
    
    func refreshUserInfo() {
        if let invitedUser = self.invitedUser {
            if invitedUser.user.userAvatar.count > 0 {
                self.userAvatarView.sd_setImage(with: URL(string: invitedUser.user.userAvatar), placeholderImage: AUICallBundle.getCommonImage("ic_default_avatar"))
            }
            else {
                self.userAvatarView.image = AUICallBundle.getCommonImage("ic_default_avatar")
            }
            var name = invitedUser.user.userNick
            if name.isEmpty {
                name = invitedUser.user.userId
            }
            self.userNameLabel.text = name
        }
        else {
            self.userAvatarView.image = AUICallBundle.getCommonImage("ic_default_avatar")
            self.userNameLabel.text = ""
        }

        self.setNeedsLayout()
    }
}

