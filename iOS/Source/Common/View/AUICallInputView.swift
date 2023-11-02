//
//  AUICallInputView.swift
//  AUICall
//
//  Created by Bingo on 2023/6/14.
//

import UIKit
import AUIFoundation

class AUICallInputView: UIView {
    
    override init(frame: CGRect) {
        super.init(frame: frame)
        
        self.addSubview(self.lineView)
        self.addSubview(self.inputTextView)
        self.addSubview(self.placeLabel)
        self.addSubview(self.inputCountLabel)
        self.addSubview(self.clearButton)
        self.addSubview(self.titleLabel)
        
        NotificationCenter.default.addObserver(self, selector: #selector(textFieldDidChange), name: UITextField.textDidChangeNotification, object: nil)
        self.updateInputState()
    }
    
    required init?(coder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }
    
    override func layoutSubviews() {
        super.layoutSubviews()
        
        self.titleLabel.frame = CGRect(x: 4.0, y: 0, width: self.av_width - 8.0, height: 24.0)
        let top = self.titleLabel.av_bottom + 4.0
        let height = self.av_height - top
        self.clearButton.frame = CGRect(x: self.av_width - 24.0 - 4.0, y: top + (height - 24.0) / 2.0, width: 24.0, height: 24.0)
        self.inputCountLabel.frame = CGRect(x: self.clearButton.av_left - 50.0, y: self.clearButton.av_top, width: 50, height: self.clearButton.av_height)
        self.inputTextView.frame = CGRect(x: 4.0, y: top, width: self.inputCountLabel.av_left, height: height)
        self.placeLabel.frame = self.inputTextView.frame
        self.placeLabel.av_width = self.placeLabel.av_width - 24.0
        self.lineView.frame = CGRect(x: 4.0, y: self.av_height - 1.0, width: self.av_width - 8.0, height: 1.0)
    }
    
    var maxInputCount: NSInteger = 15
    
    var inputTextChanged: ((_ inputView: AUICallInputView)->Void)?
    
    lazy var titleLabel: UILabel = {
        let label = UILabel()
        label.textColor = AVTheme.text_strong
        label.font = AVTheme.mediumFont(16)
        return label
    }()
    
    lazy var inputTextView: UITextField = {
        let view = UITextField()
        view.backgroundColor = .clear
        view.textColor = AVTheme.text_strong
        view.font = AVTheme.regularFont(14)
        view.keyboardType = .default
        view.returnKeyType = .done
        view.delegate = self
        return view
    }()
    
    lazy var placeLabel: UILabel = {
        let label = UILabel()
        label.textColor = AVTheme.text_ultraweak
        label.font = AVTheme.regularFont(16)
        return label
    }()
    
    lazy var inputCountLabel: UILabel = {
        let label = UILabel()
        label.textColor = AVTheme.text_ultraweak
        label.font = AVTheme.regularFont(12)
        label.textAlignment = .center
        return label
    }()
    
    lazy var lineView: UIView = {
        let view = UIView()
        view.backgroundColor = AVTheme.border_weak
        return view
    }()
    
    lazy var clearButton: UIButton = {
        let btn = UIButton()
        btn.setImage(AUICallBundle.getImage("ic_input_clear"), for: .normal)
        btn.imageEdgeInsets = UIEdgeInsets(top: 3, left: 3, bottom: 3, right: 3)
        btn.addTarget(self, action: #selector(onClearInput), for: .touchUpInside)
        return btn
    }()
    
    @objc func onClearInput() {
        self.inputTextView.text = ""
        self.updateInputState()
    }
    
    var inputText: String {
        get {
            return self.inputTextView.text ?? ""
        }
        set {
            if newValue.count <= self.maxInputCount {
                self.inputTextView.text = newValue
            }
            else {
                let index = newValue.index(newValue.startIndex, offsetBy: self.maxInputCount)
                self.inputTextView.text = String(newValue[..<index])
            }
            
            self .updateInputState()
        }
    }
    
    func updateInputState() {
        var count: NSInteger = 0
        if let inputText = self.inputTextView.text {
            count = inputText.count
        }
        self.clearButton.isHidden = count == 0
        self.placeLabel.isHidden = count != 0
        self.inputCountLabel.text = "\(count)/\(self.maxInputCount)"
        
        if let inputTextChanged = self.inputTextChanged {
            inputTextChanged(self)
        }
    }
}

extension AUICallInputView: UITextFieldDelegate {
    
    @objc func textFieldDidChange() {
        self.updateInputState()
//        debugPrint("AUICallInputView: textFieldDidChange")
    }
    
    func textField(_ textField: UITextField, shouldChangeCharactersIn range: NSRange, replacementString string: String) -> Bool {
//        debugPrint("AUICallInputView: shouldChangeCharactersIn")

        if string == "\n" {
            textField.resignFirstResponder()
            return false
        }

        if let inputText = textField.text {
            guard let rangeOfTextToReplace = Range(range, in: inputText) else {
                return false
            }
            let substringToReplace = inputText[rangeOfTextToReplace]
            let count = inputText.count - substringToReplace.count + string.count
            return count <= self.maxInputCount
        }
        
        return string.count <= self.maxInputCount
    }
    
    
}
