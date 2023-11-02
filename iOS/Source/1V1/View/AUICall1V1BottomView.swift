//
//  AUICall1V1BottomView.swift
//  AUICallExample
//
//  Created by Bingo on 2023/7/2.
//

import UIKit
import AUIFoundation

class AUICall1V1BottomView: UIView {

    init(controller: AUICall1V1Controller) {
        self.callController = controller
        super.init(frame: CGRect.zero)
        
        self.layer.addSublayer(self.gradientlayer)
    }
    
    required init?(coder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }
    
    override func layoutSubviews() {
        super.layoutSubviews()
        
        self.gradientlayer.frame = self.bounds
    }
    
    var callController: AUICall1V1Controller
    
    lazy var gradientlayer: CAGradientLayer = {
        let layer = CAGradientLayer()
        layer.startPoint = CGPoint(x: 0.5, y: 0.0)
        layer.endPoint = CGPoint(x: 0.5, y: 1.0)
        layer.colors = [UIColor.clear.cgColor, UIColor.black.withAlphaComponent(0.8).cgColor]
        return layer
    }()
    
    lazy var handupBtn: AUICallButton = {
        let btn = AUICallButton.create(title: "挂断", iconBgColor: AVTheme.danger_strong, normalIcon: AUICallBundle1V1.getCommonImage("ic_handup"))
        btn.av_size = CGSize(width: 68, height: 94)
        btn.tappedAction = {[weak self] btn in
            self?.onHandupBtnClicked()
        }
        self.addSubview(btn)
        return btn
    }()
    
    lazy var audioAcceptBtn: AUICallButton = {
        let btn = AUICallButton.create(title: "接听", iconBgColor: AVTheme.success_ultrastrong, normalIcon: AUICallBundle1V1.getCommonImage("ic_accept_audio"))
        btn.av_size = CGSize(width: 68, height: 94)
        btn.tappedAction = {[weak self] btn in
            self?.onAudioAcceptBtnClicked()
        }
        self.addSubview(btn)
        return btn
    }()
    
    lazy var videoAcceptBtn: AUICallButton = {
        let btn = AUICallButton.create(title: "接听", iconBgColor: AVTheme.success_ultrastrong, normalIcon: AUICallBundle1V1.getCommonImage("ic_accept_video"))
        btn.av_size = CGSize(width: 68, height: 94)
        btn.tappedAction = {[weak self] btn in
            self?.onVideoAcceptBtnClicked()
        }
        self.addSubview(btn)
        return btn
    }()
    
    lazy var changeToAudioStyleBtn: AUICallButton = {
        let btn = AUICallButton.create(title: "切换至语音", iconBgColor: AVTheme.tsp_fill_ultraweak, normalIcon: AUICallBundle1V1.getCommonImage("ic_switch_audio"))
        btn.av_size = CGSize(width: 52, height: 78)
        btn.tappedAction = {[weak self] btn in
            self?.onChangeToAudioStyleBtnClicked()
        }
        self.addSubview(btn)
        return btn
    }()
    
    lazy var muteAudioBtn: AUICallButton = {
        let btn = AUICallButton.create(title: "静音", iconBgColor: AVTheme.tsp_fill_ultraweak, normalIcon: AUICallBundle1V1.getCommonImage("ic_mute_audio"), selectedTitle:"取消静音", selectedIcon:AUICallBundle1V1.getCommonImage("ic_mute_audio_selected"))
        btn.av_size = CGSize(width: 52, height: 78)
        btn.tappedAction = {[weak self] btn in
            self?.onMuteAudioBtnClicked()
        }
        self.addSubview(btn)
        return btn
    }()
    
    lazy var muteVideoBtn: AUICallButton = {
        let btn = AUICallButton.create(title: "关摄像头", iconBgColor: AVTheme.tsp_fill_ultraweak, normalIcon: AUICallBundle1V1.getCommonImage("ic_mute_video"), selectedTitle:"开摄像头", selectedIcon:AUICallBundle1V1.getCommonImage("ic_mute_video_selected"))
        btn.av_size = CGSize(width: 52, height: 78)
        btn.tappedAction = {[weak self] btn in
            self?.onMuteVideoBtnClicked()
        }
        self.addSubview(btn)
        return btn
    }()
    
    lazy var switchSpeakerBtn: AUICallButton = {
        let btn = AUICallButton.create(title: "关扬声器", iconBgColor: AVTheme.tsp_fill_ultraweak, normalIcon: AUICallBundle1V1.getCommonImage("ic_speaker"), selectedTitle:"开扬声器", selectedIcon:AUICallBundle1V1.getCommonImage("ic_speaker_selected"))
        btn.av_size = CGSize(width: 52, height: 78)
        btn.tappedAction = {[weak self] btn in
            self?.onSwitchSpeakerBtnClicked()
        }
        self.addSubview(btn)
        return btn
    }()
    
    lazy var timeLabel: UILabel = {
        let label = UILabel()
        label.textColor = AVTheme.text_strong
        label.textAlignment = .center
        label.font = AVTheme.regularFont(14)
        self.addSubview(label)
        return label
    }()
    
    func refreshUI() {
        self.gradientlayer.isHidden = true
        self.handupBtn.isHidden = true
        self.audioAcceptBtn.isHidden = true
        self.videoAcceptBtn.isHidden = true
        self.muteAudioBtn.isHidden = true
        self.muteVideoBtn.isHidden = true
        self.switchSpeakerBtn.isHidden = true
        self.changeToAudioStyleBtn.isHidden = true
        
        self.timeLabel.isHidden = true

        let bot = self.av_height - UIView.av_safeBottom - 20
        let videoBot = bot - 110
        
        
        if self.callController.mode == .audio {  // 语音模式
            // 挂断
            self.handupBtn.av_centerX = self.av_width / 2.0
            self.handupBtn.av_centerY = bot - self.handupBtn.av_height / 2.0
            self.handupBtn.isHidden = false

            // 静音
            self.muteAudioBtn.av_centerX = 50 + self.muteAudioBtn.av_width / 2.0
            self.muteAudioBtn.av_centerY = bot - self.muteAudioBtn.av_height / 2.0

            // 扬声器
            self.switchSpeakerBtn.av_centerX = self.av_width - 50 - self.switchSpeakerBtn.av_width / 2.0
            self.switchSpeakerBtn.av_centerY = bot - self.switchSpeakerBtn.av_height / 2.0

            // 接听
            self.audioAcceptBtn.av_centerX = self.av_width - 72 - self.audioAcceptBtn.av_width / 2.0
            self.audioAcceptBtn.av_centerY = bot - self.audioAcceptBtn.av_height / 2.0

            // 通话时间
            self.timeLabel.frame = CGRect(x: 0, y: self.handupBtn.av_top - 22 - 12, width: self.av_width, height: 22)

            if self.callController.role == .caller {
                switch self.callController.state {
                case .none:
                    self.muteAudioBtn.isHidden = false
                    self.switchSpeakerBtn.isHidden = false
                case .connecting:
                    self.muteAudioBtn.isHidden = false
                    self.switchSpeakerBtn.isHidden = false
                case .waiting:
                    self.muteAudioBtn.isHidden = false
                    self.switchSpeakerBtn.isHidden = false
                case .connected:
                    self.muteAudioBtn.isHidden = false
                    self.switchSpeakerBtn.isHidden = false
                    self.timeLabel.isHidden = false
                default: break
                }
            }
            else {
                switch self.callController.state {
                case .none:
                    self.handupBtn.av_centerX = 72 + self.handupBtn.av_width / 2.0
                    self.audioAcceptBtn.isHidden = false
                case .connecting:
                    self.handupBtn.av_centerX = 72 + self.handupBtn.av_width / 2.0
                    self.audioAcceptBtn.isHidden = false
                case .waiting:
                    self.handupBtn.av_centerX = 72 + self.handupBtn.av_width / 2.0
                    self.audioAcceptBtn.isHidden = false
                case .connected:
                    self.muteAudioBtn.isHidden = false
                    self.switchSpeakerBtn.isHidden = false
                    self.timeLabel.isHidden = false
                default: break
                }
            }
        }
        else {    // 视频模式
            
            // 挂断
            self.handupBtn.av_centerX = self.av_width / 2.0
            self.handupBtn.av_centerY = bot - self.handupBtn.av_height / 2.0
            self.handupBtn.isHidden = false

            // 静音
            self.muteAudioBtn.av_centerX = 50 + self.muteAudioBtn.av_width / 2.0
            self.muteAudioBtn.av_centerY = videoBot - self.muteAudioBtn.av_height / 2.0

            // 摄像头
            self.muteVideoBtn.av_centerX = self.av_width - 50 - self.muteVideoBtn.av_width / 2.0
            self.muteVideoBtn.av_centerY = videoBot - self.muteVideoBtn.av_height / 2.0
            
            // 扬声器
            self.switchSpeakerBtn.av_centerX = self.av_width / 2.0
            self.switchSpeakerBtn.av_centerY = videoBot - self.switchSpeakerBtn.av_height / 2.0

            // 视频接听
            self.videoAcceptBtn.av_centerX = self.av_width - 72 - self.videoAcceptBtn.av_width / 2.0
            self.videoAcceptBtn.av_centerY = bot - self.videoAcceptBtn.av_height / 2.0

            // 切换到语音接听
            self.changeToAudioStyleBtn.av_centerX = self.av_width / 2.0
            self.changeToAudioStyleBtn.av_centerY = videoBot - self.changeToAudioStyleBtn.av_height / 2.0
            self.changeToAudioStyleBtn.isHidden = false
            
            // 通话时间
            self.timeLabel.frame = CGRect(x: 0, y: self.switchSpeakerBtn.av_top - 22 - 10, width: self.av_width, height: 22)
            
            if self.callController.role == .caller {
                switch self.callController.state {
                case .none: break
                case .connecting: break
                case .waiting: break
                case .connected:
                    self.gradientlayer.isHidden = false
                    self.muteAudioBtn.isHidden = false
                    self.switchSpeakerBtn.isHidden = false
                    self.muteVideoBtn.isHidden = false
                    self.timeLabel.isHidden = false
                    self.changeToAudioStyleBtn.av_centerX = self.av_width - 50 - self.changeToAudioStyleBtn.av_width / 2.0
                    self.changeToAudioStyleBtn.av_centerY = bot - self.changeToAudioStyleBtn.av_height / 2.0
                default: break
                }
            }
            else {
                switch self.callController.state {
                case .none:
                    self.handupBtn.av_centerX = 72 + self.handupBtn.av_width / 2.0
                    self.videoAcceptBtn.isHidden = false
                case .connecting:
                    self.handupBtn.av_centerX = 72 + self.handupBtn.av_width / 2.0
                    self.videoAcceptBtn.isHidden = false
                case .waiting:
                    self.handupBtn.av_centerX = 72 + self.handupBtn.av_width / 2.0
                    self.videoAcceptBtn.isHidden = false
                case .connected:
                    self.gradientlayer.isHidden = false
                    self.muteAudioBtn.isHidden = false
                    self.switchSpeakerBtn.isHidden = false
                    self.muteVideoBtn.isHidden = false
                    self.timeLabel.isHidden = false
                    self.changeToAudioStyleBtn.av_centerX = self.av_width - 50 - self.changeToAudioStyleBtn.av_width / 2.0
                    self.changeToAudioStyleBtn.av_centerY = bot - self.changeToAudioStyleBtn.av_height / 2.0
                default: break
                }
            }
        }
    }
}

extension AUICall1V1BottomView {
    
    @objc func onSwitchSpeakerBtnClicked() {
        self.callController.switchSpeaker(disabledSpeaker: !self.switchSpeakerBtn.isSelected)
    }
    
    @objc func onChangeToAudioStyleBtnClicked() {
        
        if self.callController.role == .callee && self.callController.state == .waiting {
            self.callController.accept(mode: .audio)
        }
        else {
            self.callController.switchToAudioMode(isMe: true)
        }
    }
    
    @objc func onHandupBtnClicked() {
        self.callController.handup()
    }
    
    @objc func onAudioAcceptBtnClicked() {
        self.callController.accept(mode: .audio)
    }
    
    @objc func onVideoAcceptBtnClicked() {
        self.callController.accept(mode: .video)
    }
    
    @objc func onMuteAudioBtnClicked() {
        self.callController.muteAudio(mute: !self.muteAudioBtn.isSelected)
    }
    
    @objc func onMuteVideoBtnClicked() {
        self.callController.muteVideo(mute: !self.muteVideoBtn.isSelected)
    }
}
