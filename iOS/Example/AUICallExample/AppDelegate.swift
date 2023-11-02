//
//  AppDelegate.swift
//  AUICall
//
//  Created by Bingo on 2023/6/14.
//

import UIKit
import AUIFoundation
import AUICall

@main
class AppDelegate: UIResponder, UIApplicationDelegate {

    lazy var window: UIWindow? = {
        return UIWindow(frame: UIScreen.main.bounds)
    }()

    func application(_ application: UIApplication, didFinishLaunchingWithOptions launchOptions: [UIApplication.LaunchOptionsKey: Any]?) -> Bool {
        // Override point for customization after application launch.
        
        // 仅支持暗黑模式
        AVTheme.supportsAutoMode = false;
        AVTheme.currentMode = .dark;
#if AUI_CALL_1V1
        let vc = AUICall1V1MainViewController()
        vc.hiddenBackButton = true
        let navi = AVNavigationController(rootViewController: vc)
#else
        let vc = AUICallNVNMainViewController()
        vc.hiddenBackButton = true
        let navi = AVNavigationController(rootViewController: vc)
#endif
        self.window?.rootViewController = navi
        self.window?.makeKeyAndVisible()
        
        return true
    }

}

