//
//  AUICallSwitchBar.swift
//  AUICallExample
//
//  Created by Bingo on 2023/7/6.
//

import UIKit
import AUIFoundation


class AUICallSwitchBar: UIView {

    override init(frame: CGRect) {
        super.init(frame: frame)
        
        self.backgroundColor = AVTheme.bg_weak
        self.addSubview(self.titleLabel)
        self.addSubview(self.switchBtn)
        self.addSubview(self.lineView)
    }
    
    required init?(coder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }
    
    override func layoutSubviews() {
        super.layoutSubviews()
        
        self.switchBtn.center = CGPoint(x: self.av_width - self.switchBtn.av_width / 2.0 - 20, y: self.av_height / 2.0)
        self.titleLabel.frame = CGRect(x: 20, y: 0, width: self.switchBtn.av_left - 16 - 20, height: self.av_height)
        self.lineView.frame = CGRect(x: 20, y: self.av_height - 1, width: self.av_width - 40, height: 1)
    }
    
    lazy var titleLabel: UILabel = {
        let label = UILabel()
        label.textColor = AVTheme.text_strong
        label.font = AVTheme.regularFont(15)
        return label
    }()
    
    lazy var switchBtn: UISwitch = {
        let btn = UISwitch()
        btn.onTintColor = AVTheme.colourful_fg_strong
        btn.tintColor = AVTheme.fill_weak
        return btn
    }()
    
    lazy var lineView: UIView = {
        let view = UIView()
        view.backgroundColor = AVTheme.border_weak
        return view
    }()
}
