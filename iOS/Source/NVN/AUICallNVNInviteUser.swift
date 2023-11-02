//
//  AUICallNVNInviteUser.swift
//  AUICallExample
//
//  Created by Bingo on 2023/7/27.
//

import UIKit
import AUIRoomEngineSDK

public class AUICallNVNInviteUser: NSObject {
    
    public enum State {
        case waiting
        case over
    }
    
    init(_ user: AUIRoomUser) {
        self.user = user
    }
    
    public let user: AUIRoomUser
    public private(set) var state: State = .waiting
    public private(set) var timestamp: TimeInterval = 0
    
    public func checkTimeout() -> Bool {
        if self.state == .waiting {
            if Date().timeIntervalSince1970 - self.timestamp >= 60 {
                self.state = .over
                return true
            }
        }
        return false
    }
    
    public func startInvite() {
        self.state = .waiting
        self.timestamp = Date().timeIntervalSince1970
    }
    
    public func reject() {
        self.state = .over
    }
}
