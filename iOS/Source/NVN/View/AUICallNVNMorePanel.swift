//
//  AUICallNVNMorePanel.swift
//  AUICallExample
//
//  Created by Bingo on 2023/7/6.
//

import UIKit
import AUIFoundation

class AUICallNVNMorePanel: AVBaseControllPanel {
    
    override init(frame: CGRect) {
        super.init(frame: frame)
        
        self.headerView.isHidden = true
        
        self.addSubview(self.switchCameraBtn)
        self.switchCameraBtn.frame = CGRect(x: 20, y: 12, width: 84, height: 84)
        self.addSubview(self.switchMirrorBtn)
        self.switchMirrorBtn.frame = CGRect(x: 20+84, y: 12, width: 84, height: 84)
    }
    
    required init?(coder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }
    
    override class func panelHeight() -> CGFloat {
        return 142
    }
    
    lazy var switchCameraBtn: AVBaseButton = {
        let btn = AVBaseButton(type: .imageText, titlePos: .bottom)
        btn.image = AUICallBundleNVN.getCommonImage("ic_switch_camera")!
        btn.title = "翻转摄像头"
        btn.font = AVTheme.regularFont(12)
        btn.color = AVTheme.text_strong
        btn.insets = UIEdgeInsets(top: 19, left: 0, bottom: 9, right: 0)
        btn.spacing = 18
        return btn
    }()
    
    lazy var switchMirrorBtn: AVBaseButton = {
        let btn = AVBaseButton(type: .imageText, titlePos: .bottom)
        btn.image = AUICallBundleNVN.getCommonImage("ic_mirror")!
        btn.selectedImage = AUICallBundleNVN.getCommonImage("ic_mirror_selected")!
        btn.title = "开镜像"
        btn.selectedTitle = "关镜像"
        btn.font = AVTheme.regularFont(12)
        btn.color = AVTheme.text_strong
        btn.insets = UIEdgeInsets(top: 19, left: 0, bottom: 9, right: 0)
        btn.spacing = 18
        return btn
    }()
}
