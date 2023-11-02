//
//  AUICallNVNJoinViewController.swift
//  AUICallExample
//
//  Created by Bingo on 2023/7/6.
//

import UIKit
import AUIFoundation
import AUIRoomEngineSDK

public class AUICallNVNJoinViewController: AVBaseViewController {
    
    public override func viewDidLoad() {
        super.viewDidLoad()
        
        self.titleView.text = "加入房间"
        self.menuButton.isHidden = true

        self.contentView.addSubview(self.joinBtn)

        self.contentView.addSubview(self.inputIdView)
        self.contentView.addSubview(self.showIdLabel)
        self.contentView.addSubview(self.splitView1)
        self.contentView.addSubview(self.audioSwitchBar)
        self.contentView.addSubview(self.videoSwitchBar)
        self.contentView.addSubview(self.speakerSwitchBar)
//        self.contentView.addSubview(self.beautySwitchBar)
        self.contentView.addSubview(self.splitView2)
    }
    
    lazy var inputIdView: AUICallInputView = {
        let view = AUICallInputView(frame: CGRect(x: 16.0, y: 32.0, width: self.contentView.av_width - 32.0, height: 70.0))
        view.titleLabel.text = "加入房间号"
        view.placeLabel.text = "请输入字母、数字、中划线"
        view.maxInputCount = 40
        view.inputTextChanged = {[weak self] inputView in
            self?.joinBtn.isEnabled = !inputView.inputText.isEmpty
        }
        return view
    }()
    
    lazy var showIdLabel: UILabel = {
        let label = UILabel(frame: CGRect(x: 20.0, y: self.inputIdView.av_bottom, width: self.contentView.av_width, height: 44.0))
        label.textColor = AVTheme.text_weak
        label.font = AVTheme.regularFont(12)
        label.text = "我的ID:" + (AUICallNVNManager.defaultManager.me?.userId ?? "unknown")
        return label
    }()
    
    lazy var splitView1: UIView = {
        let view = UIView(frame: CGRect(x: 0, y: self.showIdLabel.av_bottom, width: self.contentView.av_width, height: 40))
        view.backgroundColor = AVTheme.bg_medium
        return view
    }()
    
    lazy var audioSwitchBar: AUICallSwitchBar = {
        let bar = AUICallSwitchBar(frame: CGRect(x: 0, y: self.splitView1.av_bottom, width: self.contentView.av_width, height: 45))
        bar.titleLabel.text = "麦克风"
        bar.switchBtn.isOn = true
        return bar
    }()
    
    lazy var videoSwitchBar: AUICallSwitchBar = {
        let bar = AUICallSwitchBar(frame: CGRect(x: 0, y: self.audioSwitchBar.av_bottom, width: self.contentView.av_width, height: 45))
        bar.titleLabel.text = "摄像头"
        bar.switchBtn.isOn = true
        return bar
    }()
    
    lazy var speakerSwitchBar: AUICallSwitchBar = {
        let bar = AUICallSwitchBar(frame: CGRect(x: 0, y: self.videoSwitchBar.av_bottom, width: self.contentView.av_width, height: 45))
        bar.titleLabel.text = "扬声器"
        bar.switchBtn.isOn = true
        bar.lineView.isHidden = true
        return bar
    }()
    
//    lazy var beautySwitchBar: AUICallSwitchBar = {
//        let bar = AUICallSwitchBar(frame: CGRect(x: 0, y: self.speakerSwitchBar.av_bottom, width: self.contentView.av_width, height: 45))
//        bar.titleLabel.text = "美颜"
//        bar.switchBtn.isOn = false
//        bar.lineView.isHidden = true
//        return bar
//    }()
    
    lazy var splitView2: UIView = {
        let view = UIView(frame: CGRect(x: 0, y: self.speakerSwitchBar.av_bottom, width: self.contentView.av_width, height: self.joinBtn.av_top - 10 - self.speakerSwitchBar.av_bottom))
        view.backgroundColor = AVTheme.bg_medium
        return view
    }()
    
    lazy var joinBtn: AVBlockButton = {
        let btn = AVBlockButton(frame: CGRect(x: 20.0, y: self.contentView.av_height - UIView.av_safeBottom - 44.0, width: self.contentView.av_width - 40.0, height: 44.0))
        btn.layer.cornerRadius = 22.0
        btn.layer.masksToBounds = true
        btn.isEnabled = false
        btn.setTitle("加入房间", for: .normal)
        btn.setBackgroundColor(AVTheme.colourful_fill_strong, for: .normal)
        btn.setBackgroundColor(AVTheme.colourful_fill_disabled, for: .disabled)
        btn.setTitleColor(AVTheme.text_strong, for: .normal)
        btn.titleLabel?.font = AVTheme.regularFont(16)
        btn.addTarget(self, action: #selector(onJoinBtnClicked), for: .touchUpInside)
        return btn
    }()
    
    @objc func onJoinBtnClicked() {
        let roomId = self.inputIdView.inputText
        if roomId.isEmpty { return }
        
        if !AUICallRoom.validateRoomId(roomId) {
            AVAlertController.show("用户ID仅支持字母、数字和中划线", vc: self)
            return
        }
        
        let userConfig = AUICallRoomUserConfig()
        userConfig.muteAudio = !self.audioSwitchBar.switchBtn.isOn
        userConfig.muteVideo = !self.videoSwitchBar.switchBtn.isOn
        userConfig.disabledSpeaker = !self.speakerSwitchBar.switchBtn.isOn
//        userConfig.enableBeauty = self.beautySwitchBar.switchBtn.isOn
        
        let room = AUICallRoom(roomId, AUIRoomUser(""))
        AUICallNVNManager.defaultManager.joinCall(room: room, userConfig: userConfig, currVC: self) {
            DispatchQueue.main.asyncAfter(deadline: DispatchTime.now() + DispatchTimeInterval.milliseconds(500)) {
                self.removeFromParent()
            }
        }
    }
}
