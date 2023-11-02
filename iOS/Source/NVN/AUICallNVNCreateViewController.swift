//
//  AUICallNVNCreateViewController.swift
//  AUICallExample
//
//  Created by Bingo on 2023/7/6.
//

import UIKit
import AUIFoundation

public class AUICallNVNCreateViewController: AVBaseViewController {
    
    public override func viewDidLoad() {
        super.viewDidLoad()
        
        self.titleView.text = "创建房间"
        self.menuButton.isHidden = true
        
        self.contentView.addSubview(self.startBtn)

        self.contentView.addSubview(self.titleLabel)
        self.contentView.addSubview(self.roomIdLabel)
        self.contentView.addSubview(self.copyIdBtn)
        self.contentView.addSubview(self.lineView)

        self.contentView.addSubview(self.showIdLabel)
        self.contentView.addSubview(self.splitView1)
        self.contentView.addSubview(self.audioSwitchBar)
        self.contentView.addSubview(self.videoSwitchBar)
        self.contentView.addSubview(self.speakerSwitchBar)
//        self.contentView.addSubview(self.beautySwitchBar)
        self.contentView.addSubview(self.splitView2)
        
        self.isLoaded = true
        self.updateRoomInfo()
    }
    
    var isLoaded = false
    
    var room: AUICallRoom? = nil {
        didSet {
            self.updateRoomInfo()
        }
    }
    
    func updateRoomInfo() {
        if !self.isLoaded {
            return
        }
        if let room = self.room {
            self.roomIdLabel.text = room.roomId
            self.copyIdBtn.isEnabled = true
        }
        else {
            self.roomIdLabel.text = ""
            self.copyIdBtn.isEnabled = false
        }
    }
    
    lazy var titleLabel: UILabel = {
        let label = UILabel(frame: CGRect(x: 20.0, y: 32, width: self.contentView.av_width - 40.0, height: 24.0))
        label.text = "房间号"
        label.textColor = AVTheme.text_strong
        label.font = AVTheme.mediumFont(16)
        return label
    }()
    
    lazy var roomIdLabel: UILabel = {
        let label = UILabel(frame: CGRect(x: 20.0, y: self.titleLabel.av_bottom + 12, width: self.contentView.av_width - 40.0 - 68.0, height: 22.0))
        label.textColor = AVTheme.text_strong
        label.font = AVTheme.mediumFont(14)
        return label
    }()
    
    lazy var copyIdBtn: UIButton = {
        let btn = UIButton(frame: CGRect(x: self.contentView.av_width - 14 - 40, y: self.roomIdLabel.av_centerY - 16.0, width: 40.0, height: 32.0))
        btn.setTitle("复制", for: .normal)
        btn.setTitleColor(AVTheme.colourful_fill_strong, for: .normal)
        btn.setTitleColor(AVTheme.colourful_fill_disabled, for: .disabled)
        btn.titleLabel?.font = AVTheme.regularFont(14)
        btn.addTarget(self, action: #selector(onCopyIdBtnClicked), for: .touchUpInside)
        return btn
    }()
    
    lazy var lineView: UIView = {
        let view = UIView(frame: CGRect(x: 20.0, y: self.roomIdLabel.av_bottom + 11, width: self.contentView.av_width - 40.0, height: 1.0))
        view.backgroundColor = AVTheme.border_weak
        return view
    }()

    lazy var showIdLabel: UILabel = {
        let label = UILabel(frame: CGRect(x: 20.0, y: self.lineView.av_bottom, width: self.contentView.av_width, height: 44.0))
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
        let view = UIView(frame: CGRect(x: 0, y: self.speakerSwitchBar.av_bottom, width: self.contentView.av_width, height: self.startBtn.av_top - 10 - self.speakerSwitchBar.av_bottom))
        view.backgroundColor = AVTheme.bg_medium
        return view
    }()
    
    lazy var startBtn: AVBlockButton = {
        let btn = AVBlockButton(frame: CGRect(x: 20.0, y: self.contentView.av_height - UIView.av_safeBottom - 44.0, width: self.contentView.av_width - 40.0, height: 44.0))
        btn.layer.cornerRadius = 22.0
        btn.layer.masksToBounds = true
        btn.isEnabled = true
        btn.setTitle("创建房间", for: .normal)
        btn.setBackgroundColor(AVTheme.colourful_fill_strong, for: .normal)
        btn.setBackgroundColor(AVTheme.colourful_fill_disabled, for: .disabled)
        btn.setTitleColor(AVTheme.text_strong, for: .normal)
        btn.titleLabel?.font = AVTheme.regularFont(16)
        btn.addTarget(self, action: #selector(onStartBtnClicked), for: .touchUpInside)
        return btn
    }()
    
    @objc func onCopyIdBtnClicked() {
        guard let room = self.room else { return }

        let pasteboard = UIPasteboard.general
        pasteboard.string = room.roomId
        
        AVToastView.show("已复制房间号", view: self.contentView, position: .mid)
    }
    
    @objc func onStartBtnClicked() {
        let userConfig = AUICallRoomUserConfig()
        userConfig.muteAudio = !self.audioSwitchBar.switchBtn.isOn
        userConfig.muteVideo = !self.videoSwitchBar.switchBtn.isOn
        userConfig.disabledSpeaker = !self.speakerSwitchBar.switchBtn.isOn
//        userConfig.enableBeauty = self.beautySwitchBar.switchBtn.isOn
        
        if let room = self.room {
            AUICallNVNManager.defaultManager.joinCall(room: room, userConfig: userConfig, currVC: self) {
                DispatchQueue.main.asyncAfter(deadline: DispatchTime.now() + DispatchTimeInterval.milliseconds(500)) {
                    self.removeFromParent()
                }
            }
        }
        else {
            AUICallNVNManager.defaultManager.startCall(userConfig: userConfig, roomName: nil, currVC: self) {
                DispatchQueue.main.asyncAfter(deadline: DispatchTime.now() + DispatchTimeInterval.milliseconds(500)) {
                    self.removeFromParent()
                }
            }
        }
    }
}
