//
//  AUICallFloatViewController.swift
//  AUICallExample
//
//  Created by Bingo on 2023/7/3.
//

import UIKit
import AUIFoundation

public protocol AUICallFloatTargetProtocol {
    var targetView: UIView? {
        get
    }
    
    var startFloatingFrame: CGRect{
        get
    }
    
    func startFloating(floatVC: AUICallFloatViewController) -> Void
    
    func exitFloating(floatVC: AUICallFloatViewController, close: Bool) ->Void
}

open class AUICallFloatViewController: UIViewController {
    
    init(target: AUICallFloatTargetProtocol) {
        self.target = target
        super.init(nibName: nil, bundle: nil)
    }
    
    public required init?(coder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }
    
    deinit {
        debugPrint("deinit: \(self)")
    }
    
    open override func viewDidLoad() {
        super.viewDidLoad()
        
        self.view.backgroundColor = AVTheme.fill_medium
        self.view.layer.cornerRadius = 4
        self.view.layer.borderColor = AVTheme.border_weak.cgColor
        self.view.layer.borderWidth = 1
        self.view.layer.masksToBounds = true
    }
    
    open override var preferredStatusBarStyle: UIStatusBarStyle {
        return .lightContent
    }
    
    open private(set) var target: AUICallFloatTargetProtocol
    
    private lazy var panGestureRecognizer: UIPanGestureRecognizer? = nil
    private lazy var tapGestureRecognizer: UITapGestureRecognizer? = nil

    func addGestureRecognizer(win: UIWindow) {
        if self.panGestureRecognizer == nil {
            self.panGestureRecognizer = UIPanGestureRecognizer()
            self.panGestureRecognizer!.addTarget(self, action: #selector(panGesture(recognizer:)))
        }
        win.addGestureRecognizer(self.panGestureRecognizer!)
        
        if self.tapGestureRecognizer == nil {
            self.tapGestureRecognizer = UITapGestureRecognizer()
            self.tapGestureRecognizer!.addTarget(self, action: #selector(tapGesture(recognizer:)))
        }
        win.addGestureRecognizer(self.tapGestureRecognizer!)
    }
    
    func removeGestureRecognizer(win: UIWindow) {
        if self.panGestureRecognizer != nil {
            win.removeGestureRecognizer(self.panGestureRecognizer!)
        }
        if self.tapGestureRecognizer != nil {
            win.removeGestureRecognizer(self.tapGestureRecognizer!)
        }
    }
    
    @objc func panGesture(recognizer: UIPanGestureRecognizer) {
        guard let view = recognizer.view else {
            return
        }
        let point: CGPoint = recognizer.translation(in: view)
        let center = CGPoint(x: view.center.x + point.x, y: view.center.y + point.y)
        view.center = center
        recognizer.setTranslation(CGPoint.zero, in: view)
        
        // 拖拽停止/取消/失败
        if recognizer.state == .ended || recognizer.state == .cancelled || recognizer.state == .failed {
            self.updateViewPosition(view: view)
        }
    }
    
    @objc func tapGesture(recognizer: UITapGestureRecognizer) {
        AUICallFloatWindow.exitFloatWindow(close: false)
    }
    
    open func startFloating(win: UIWindow, completed: (_ curWindow: UIWindow, _ curVC: UIViewController)->Void) {
        self.target.startFloating(floatVC: self)
        if let targetView = self.target.targetView {
            targetView.frame = CGRect(x: 0, y: 0, width: self.target.startFloatingFrame.width, height: self.target.startFloatingFrame.width)
            self.view.addSubview(targetView)
        }
        win.frame = self.target.startFloatingFrame
        completed(win, self)
    }
    
    open func exitFloating(close: Bool, win: UIWindow, completed: (_ curWindow: UIWindow, _ curVC: UIViewController)->Void) {
        self.target.targetView?.removeFromSuperview()
        self.target.exitFloating(floatVC: self, close: close)
        completed(win, self)
    }
    
    // 更新位置
    open func updateViewPosition(view: UIView) {
        
        let rect = UIScreen.main.bounds
        var frame = view.frame
        if frame.minX < 16 {
            frame.origin.x = 16
        }
        
        if frame.minY < UIView.av_safeTop {
            frame.origin.y = UIView.av_safeTop
        }
        
        if frame.maxX >= rect.maxX - 16 {
            frame.origin.x = rect.maxX - 16 - view.av_width
        }
        
        if frame.maxY >= rect.maxY - UIView.av_safeBottom {
            frame.origin.y = rect.maxY - UIView.av_safeBottom - view.av_height
        }
        
        UIView.animate(withDuration: 0.3) {
            view.frame = frame
        } completion: { success in
            
        }
    }
}
