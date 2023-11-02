//
//  AUICallRoom.swift
//  AUICallExample
//
//  Created by Bingo on 2023/7/5.
//

import UIKit
import AUIRoomEngineSDK

open class AUICallRoom: NSObject {

    init(_ roomId: String, _ anchor: AUIRoomUser) {
        self.roomId = roomId
        self.anchor = anchor
    }
    
    public let roomId: String
    public let anchor: AUIRoomUser
    open var roomName: String?
    open var isMuteAudioAll: Bool = false
    
    open func toData() -> [AnyHashable: Any] {
        return [
            "muteAll": self.isMuteAudioAll,
            "hostId": self.anchor.userId,
            "avatar": self.anchor.userAvatar,
            "nick": self.anchor.userNick,
        ]
    }
    
    open func update(data: [AnyHashable: Any]) -> Bool {
        let muteAll = data["muteAll"] as? Bool
        if let muteAll = muteAll {
            self.isMuteAudioAll = muteAll
        }
        
        var host: AUIRoomUser? = nil
        let hostId = data["hostId"] as? String
        if let hostId = hostId {
            host = AUIRoomUser(hostId)
        }
        let avatar = data["avatar"] as? String
        if let avatar = avatar {
            host?.userAvatar = avatar
        }
        let nick = data["nick"] as? String
        if let nick = nick {
            host?.userNick = nick
        }
        self.anchor.update(user: host, force: true)
        return true
    }
}

extension AUICallRoom {
    static func validateRoomId(_ roomId: String) -> Bool {
        let set = CharacterSet(charactersIn: "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz-")
        if roomId.rangeOfCharacter(from: set.inverted) == nil {
            return true
        }
        return false
    }
}


open class AUICallRoomUserConfig: NSObject {
    
    open var muteAudio: Bool = false
    open var muteVideo: Bool = false
    open var disabledSpeaker: Bool = false
    open var enableBeauty: Bool = false
}
