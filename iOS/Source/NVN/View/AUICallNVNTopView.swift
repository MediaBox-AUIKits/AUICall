//
//  AUICallNVNTopView.swift
//  AUICallExample
//
//  Created by Bingo on 2023/7/5.
//

import UIKit
import AUIFoundation

class AUICallNVNTopView: UIView {

    override init(frame: CGRect) {
        super.init(frame: CGRect.zero)
        
        self.backgroundColor = AVTheme.bg_weak
        self.addSubview(self.floatWindowBtn)
        self.addSubview(self.switchSpeakerBtn)
        self.addSubview(self.finishBtn)
        self.addSubview(self.roomTitleLabel)
        self.addSubview(self.timeLabel)
    }
    
    required init?(coder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }
    
    override func layoutSubviews() {
        super.layoutSubviews()
        
        self.floatWindowBtn.frame = CGRect(x: 6, y: UIView.av_safeTop, width: 44, height: 44)
        self.switchSpeakerBtn.frame = CGRect(x: self.floatWindowBtn.av_right, y: UIView.av_safeTop, width: 44, height: 44)
        self.finishBtn.frame = CGRect(x: self.av_width - 50 - 20, y: UIView.av_safeTop + 11, width: 50, height: 22)
        
        let roomTilteMargin = max(self.switchSpeakerBtn.av_right, self.av_width - self.finishBtn.av_left)
        self.roomTitleLabel.frame = CGRect(x: roomTilteMargin, y: UIView.av_safeTop, width: self.av_width - roomTilteMargin - roomTilteMargin, height: 24)
        self.timeLabel.frame = CGRect(x: self.roomTitleLabel.av_left, y: self.roomTitleLabel.av_bottom, width: self.roomTitleLabel.av_width, height: 18)
    }
    
    lazy var floatWindowBtn: AVBlockButton = {
        let btn = AVBlockButton()
        btn.imageEdgeInsets = UIEdgeInsets(top: 10, left: 10, bottom: 10, right: 10)
        btn.setImage(AUICallBundleNVN.getCommonImage("ic_float_window"), for: .normal)
        return btn
    }()
    
    lazy var switchSpeakerBtn: AVBlockButton = {
        let btn = AVBlockButton()
        btn.imageEdgeInsets = UIEdgeInsets(top: 10, left: 10, bottom: 10, right: 10)
        btn.setImage(AUICallBundleNVN.getCommonImage("ic_speaker"), for: .normal)
        btn.setImage(AUICallBundleNVN.getCommonImage("ic_speaker_selected"), for: .selected)
        return btn
    }()
    
    lazy var finishBtn: AVBlockButton = {
        let btn = AVBlockButton()
        btn.backgroundColor = AVTheme.danger_strong
        btn.layer.cornerRadius = 11
        btn.layer.masksToBounds = true
        btn.setTitle("结束", for: .normal)
        btn.setTitleColor(AVTheme.text_strong, for: .normal)
        btn.titleLabel?.font = AVTheme.regularFont(12)
        return btn
    }()
    
    lazy var roomTitleLabel: UILabel = {
        let label = UILabel()
        label.textColor = AVTheme.text_strong
        label.textAlignment = .center
        label.font = AVTheme.regularFont(16)
        return label
    }()
    
    lazy var timeLabel: UILabel = {
        let label = UILabel()
        label.textColor = AVTheme.text_strong
        label.textAlignment = .center
        label.font = AVTheme.regularFont(12)
        return label
    }()
}
