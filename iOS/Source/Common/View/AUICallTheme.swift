//
//  AUICallTheme.swift
//  AUICall
//
//  Created by Bingo on 2023/6/14.
//

import UIKit
import AUIFoundation

class AUICallTheme: NSObject {
    
    init(_ bundleName: String) {
        self.bundleName = bundleName
    }
    
    let bundleName: String
    
    func getImage(_ key: String?) -> UIImage? {
        guard let key = key else { return nil }
        return AVTheme.image(withNamed: key, withModule: self.bundleName)
    }
    
    func getCommonImage(_ key: String?) -> UIImage? {
        guard let key = key else { return nil }
        return AVTheme.image(withCommonNamed: key, withModule: self.bundleName)
    }
}

let AUICallBundle = AUICallTheme("AUICallBase")
let AUICallBundle1V1 = AUICallTheme("AUICall1V1")
let AUICallBundleNVN = AUICallTheme("AUICallNVN")

extension AVTheme {
    static var danger_strong: UIColor {
        return UIColor.av_color(withHexString: "F53F3FFF")
    }
    
    static var success_ultrastrong: UIColor {
        return UIColor.av_color(withHexString: "3BB346FF")
    }
}
