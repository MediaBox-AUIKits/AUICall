//
//  AUICallNVNBottomView.swift
//  AUICallExample
//
//  Created by Bingo on 2023/7/5.
//

import UIKit
import AUIFoundation

class AUICallNVNBottomView: UIView {
    
    override init(frame: CGRect) {
        super.init(frame: frame)
        
        self.backgroundColor = AVTheme.bg_weak
        self.addSubview(self.muteAudioBtn)
        self.addSubview(self.muteVideoBtn)
        self.addSubview(self.memberBtn)
        self.addSubview(self.beautyBtn)
        self.addSubview(self.moreBtn)
    }
    
    required init?(coder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }
    
    override func layoutSubviews() {
        super.layoutSubviews()
        
        let margin = (self.av_width - 56 * 5) / 6
        
        self.muteAudioBtn.frame = CGRect(x: margin, y: 8, width: 56, height: 56)
        self.muteVideoBtn.frame = CGRect(x: self.muteAudioBtn.av_right + margin, y: 8, width: 56, height: 56)
        self.memberBtn.frame = CGRect(x: self.muteVideoBtn.av_right + margin, y: 8, width: 56, height: 56)
        self.beautyBtn.frame = CGRect(x: self.memberBtn.av_right + margin, y: 8, width: 56, height: 56)
        self.moreBtn.frame = CGRect(x: self.beautyBtn.av_right + margin, y: 8, width: 56, height: 56)

    }
        
    lazy var muteAudioBtn: AVBaseButton = {
        let btn = AVBaseButton(type: .imageText, titlePos: .bottom)
        btn.image = AUICallBundleNVN.getCommonImage("ic_mute_audio")!
        btn.selectedImage = AUICallBundleNVN.getCommonImage("ic_mute_audio_selected")!
        btn.title = "静音"
        btn.selectedTitle = "取消静音"
        btn.font = AVTheme.regularFont(12)
        btn.color = AVTheme.text_strong
        btn.insets = UIEdgeInsets(top: 4, left: 4, bottom: 4, right: 4)
        btn.spacing = 7
        return btn
    }()
    
    lazy var muteVideoBtn: AVBaseButton = {
        let btn = AVBaseButton(type: .imageText, titlePos: .bottom)
        btn.image = AUICallBundleNVN.getCommonImage("ic_mute_video")!
        btn.selectedImage = AUICallBundleNVN.getCommonImage("ic_mute_video_selected")!
        btn.title = "关摄像头"
        btn.selectedTitle = "开摄像头"
        btn.font = AVTheme.regularFont(12)
        btn.color = AVTheme.text_strong
        btn.insets = UIEdgeInsets(top: 4, left: 4, bottom: 4, right: 4)
        btn.spacing = 7
        return btn
    }()
    
    lazy var memberBtn: AVBaseButton = {
        let btn = AVBaseButton(type: .imageText, titlePos: .bottom)
        btn.image = AUICallBundleNVN.getCommonImage("ic_member")!
        btn.title = "成员(1)"
        btn.font = AVTheme.regularFont(12)
        btn.color = AVTheme.text_strong
        btn.insets = UIEdgeInsets(top: 4, left: 4, bottom: 4, right: 4)
        btn.spacing = 7
        return btn
    }()
    
    lazy var beautyBtn: AVBaseButton = {
        let btn = AVBaseButton(type: .imageText, titlePos: .bottom)
        btn.image = AUICallBundleNVN.getCommonImage("ic_beauty")!
        btn.title = "美颜"
        btn.font = AVTheme.regularFont(12)
        btn.color = AVTheme.text_strong
        btn.insets = UIEdgeInsets(top: 4, left: 4, bottom: 4, right: 4)
        btn.spacing = 7
        return btn
    }()
    
    lazy var moreBtn: AVBaseButton = {
        let btn = AVBaseButton(type: .imageText, titlePos: .bottom)
        btn.image = AUICallBundleNVN.getCommonImage("ic_more")!
        btn.title = "更多"
        btn.font = AVTheme.regularFont(12)
        btn.color = AVTheme.text_strong
        btn.insets = UIEdgeInsets(top: 4, left: 4, bottom: 4, right: 4)
        btn.spacing = 7
        return btn
    }()
}
