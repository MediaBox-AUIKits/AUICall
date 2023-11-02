//
//  AUICallButton.swift
//  AUICallExample
//
//  Created by Bingo on 2023/6/19.
//

import UIKit
import AUIFoundation

class AUICallButton: UIView {

    override init(frame: CGRect) {
        self.isSelected = false
        super.init(frame: frame)
        
        self.addSubview(self.imageBgView)
        self.addSubview(self.imageView)
        self.addSubview(self.titleLabel)
        self.isSelected = false
        
        self.addGestureRecognizer(UITapGestureRecognizer(target: self, action: #selector(onTapped)))
    }
    
    required init?(coder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }
    
    override func layoutSubviews() {
        super.layoutSubviews()
        
        self.imageBgView.frame = CGRect(x: 0, y: 0, width: self.av_width, height: self.av_width)
        self.imageBgView.layer.cornerRadius = self.imageBgView.av_width / 2.0
        self.imageBgView.layer.masksToBounds = true
        self.imageView.frame = CGRect(x: 12, y: 12, width: self.av_width - 12 * 2, height: self.av_width - 12 * 2)
        
        self.titleLabel.sizeToFit()
        let width = max(self.av_width, self.titleLabel.av_width)
        self.titleLabel.frame = CGRect(x: (self.av_width - width) / 2.0, y: self.imageBgView.av_bottom + 8.0, width: width, height: 18.0)
    }
    
    var selectedTitle: String? = nil
    var normalTitle: String? = nil
    var selectedImage: UIImage? = nil
    var normalImage: UIImage? = nil
    var isSelected: Bool {
        didSet {
            self.imageView.image = self.isSelected ? self.selectedImage : self.normalImage
            self.titleLabel.text = self.isSelected ? self.selectedTitle : self.normalTitle
            self.setNeedsLayout()
        }
    }
    
    lazy var imageView: UIImageView = {
        let img = UIImageView()
        return img
    }()
    
    lazy var imageBgView: UIView = {
        let bg = UIView()
        return bg
    }()
    
    lazy var titleLabel: UILabel = {
        let label = UILabel()
        label.font = AVTheme.regularFont(12.0)
        label.textColor = AVTheme.text_strong
        label.textAlignment = .center
        return label
    }()
    
    var tappedAction: ((_ btn: AUICallButton)->Void)? = nil
    
    @objc func onTapped() {
        self.tappedAction?(self)
    }
}

extension AUICallButton {
    
    static func create(title: String, iconBgColor: UIColor?, normalIcon: UIImage?, selectedTitle: String? = nil, selectedIcon: UIImage? = nil) -> AUICallButton {
        let btn = AUICallButton()
        btn.imageBgView.backgroundColor = iconBgColor
        btn.normalTitle = title
        btn.selectedTitle = selectedTitle
        btn.normalImage = normalIcon
        btn.selectedImage = selectedIcon
        btn.isSelected = false
        return btn
    }
    
}
