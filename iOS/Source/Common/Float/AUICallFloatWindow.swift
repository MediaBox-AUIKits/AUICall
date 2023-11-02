//
//  AUICallFloatWindow.swift
//  AUICallExample
//
//  Created by Bingo on 2023/7/3.
//

import UIKit

open class AUICallFloatWindow: NSObject {

    private static var floatWindow: UIWindow? = nil
    
    public static func startFloatWindow(floatVC: AUICallFloatViewController) {
        if self.floatWindow != nil {
            return
        }
        
        let win = UIWindow()
        win.layer.masksToBounds = true
        win.windowLevel = UIWindow.Level.alert + 1
        win.rootViewController = floatVC
        floatVC.addGestureRecognizer(win: win)

        self.floatWindow = win
        self.floatWindow!.isHidden = false
        
        floatVC.startFloating(win: win) { curWindow, curVC in
            
        }
    }
    
    public static func exitFloatWindow(close: Bool) {
        if let vc = self.floatWindow?.rootViewController as? AUICallFloatViewController {
            vc.exitFloating(close:close, win: self.floatWindow!) { curWindow, curVC in
                self.floatWindow?.rootViewController = nil
                self.floatWindow?.isHidden = false
                self.floatWindow = nil
            }
        }
    }
}
