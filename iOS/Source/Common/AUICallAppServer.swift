//
//  AUICallAppServer.swift
//  AUICallExample
//
//  Created by Bingo on 2023/6/16.
//

import UIKit
import AUIFoundation
import AUIMessage
import AUIRoomEngineSDK

public class AUICallAppServer: NSObject {
    public static let serverDomain = "你的AppServer域名"
    public static var serverAuth: String? = ""
    public static func request(path: String, body: [AnyHashable: Any]?, completed: @escaping (_ response: URLResponse?, _ data: [AnyHashable: Any]?, _ error: Error?) -> Void) -> Void {
        let urlString = "\(self.serverDomain)\(path)"
        let url = URL(string: urlString)
        guard let url = url else {
            completed(nil, nil, NSError(domain: "auicall", code: -1, userInfo: [NSLocalizedDescriptionKey: "path error"]))
            return
        }
        var urlRequest = URLRequest(url: url)
        urlRequest.setValue("application/json", forHTTPHeaderField: "accept")
        urlRequest.setValue("application/json", forHTTPHeaderField: "Content-Type")
        if self.serverAuth != nil {
            urlRequest.setValue("Bearer \(self.serverAuth!)", forHTTPHeaderField: "Authorization")
        }
        urlRequest.httpMethod = "POST"
        if let body = body {
            let bodyData = try? JSONSerialization.data(withJSONObject: body, options: .prettyPrinted)
            guard let bodyData = bodyData else {
                completed(nil, nil, NSError(domain: "auicall", code: -1, userInfo: [NSLocalizedDescriptionKey: "body error"]))
                return
            }
            urlRequest.httpBody = bodyData
        }
        
        let config = URLSessionConfiguration.default
        let session = URLSession.init(configuration: config)
        let task = session.dataTask(with: urlRequest) { data, rsp, error in
            DispatchQueue.main.async {
                if error != nil {
                    completed(rsp, nil, error)
                    return
                }
                
                if rsp is HTTPURLResponse {
                    let httpRsp = rsp as! HTTPURLResponse
                    if httpRsp.statusCode == 200 {
                        if let data = data {
                            let obj = try? JSONSerialization.jsonObject(with: data, options: .allowFragments)
                            completed(rsp, obj as? [AnyHashable : Any], nil)
                            return
                        }
                    }
                }
                completed(rsp, nil, NSError(domain: "auicall", code: -1, userInfo: [NSLocalizedDescriptionKey: "network error"]))
            }
        }
        task.resume()
    }
    
    private static func serverAuthValid() -> Bool {
        return self.serverAuth != nil && !(self.serverAuth!.isEmpty)
    }
    
    public static func fetchRoomEngineLoginToken(uid: String, completed: @escaping (_ tokenData: [String : Any]?, _ error: Error?) -> Void) {
        if !self.serverAuthValid() {
            completed(nil, NSError(domain: "auicall", code: -1, userInfo: [NSLocalizedDescriptionKey: "lack of auth token"]))
            return
        }
        
        let body :[String : Any] = [
            "device_id": AUIMessageConfig.deviceId,
            "device_type": "ios",
            "user_id": uid,
            "im_server":["aliyun_new"],
            "role":"admin",
        ]
        self.request(path: "/api/v2/live/token", body: body) { response, data, error in
            if error == nil {
                let tokenData = data?["aliyun_new_im"] as? Dictionary<String, Any>
                if let tokenData = tokenData {
                    var final = tokenData
                    final["source"] = "aui-call"
                    completed(final, nil)
                }
                else {
                    completed(nil, NSError(domain: "auicall", code: -1, userInfo: [NSLocalizedDescriptionKey: "fetch token failed"]))
                }
            }
            else {
                completed(nil, error)
            }
        }
    }
    
    public static func fetchRoomEngineConfig(uid: String, roomId: String, completed: @escaping (_ token: String?, _ timestamp: Int64?, _ error: Error?) -> Void) {
        
        if !self.serverAuthValid() {
            completed(nil, nil, NSError(domain: "auicall", code: -1, userInfo: [NSLocalizedDescriptionKey: "lack of auth token"]))
            return
        }
        
        let body = [
            "room_id": roomId,
            "user_id": uid
        ]
        self.request(path: "/api/v1/live/getRtcAuthToken", body: body) { response, data, error in
            if error == nil {
                let token = data?["auth_token"] as? String
                let timestamp = data?["timestamp"] as? Int64
                completed(token, timestamp, nil)
            }
            else {
                completed(nil, nil, error)
            }
        }
    }
    
    public static func updateUserInfo(user: AUIRoomUser, completed:((_ user: AUIRoomUser)->Void)? = nil) {
        // TODO: 此次为更新用户信息，在这里简单模拟实现，需要客户自身实现，实现方案：1、服务端提供更新用户接口，2、请求该接口并把最新数据更新给user对象  3、注意下qps，可以考虑合并请求
        DispatchQueue.main.asyncAfter(deadline: DispatchTime.now() + DispatchTimeInterval.milliseconds(500)) {
            let avatarList = [
                "https://img.alicdn.com/imgextra/i1/O1CN01chynzk1uKkiHiQIvE_!!6000000006019-2-tps-80-80.png",
                "https://img.alicdn.com/imgextra/i4/O1CN01kpUDlF1sEgEJMKHH8_!!6000000005735-2-tps-80-80.png",
                "https://img.alicdn.com/imgextra/i4/O1CN01ES6H0u21ObLta9mAF_!!6000000006975-2-tps-80-80.png",
                "https://img.alicdn.com/imgextra/i1/O1CN01KWVPkd1Q9omnAnzAL_!!6000000001934-2-tps-80-80.png",
                "https://img.alicdn.com/imgextra/i1/O1CN01P6zzLk1muv3zymjjD_!!6000000005015-2-tps-80-80.png",
                "https://img.alicdn.com/imgextra/i2/O1CN01ZDasLb1Ca0ogtITHO_!!6000000000096-2-tps-80-80.png",
            ]
            let userId = user.userId
            user.userNick = userId
            let first = userId[userId.startIndex]
            if let value = first.asciiValue {
                user.userAvatar = avatarList[Int(value) % avatarList.count]
            }
            completed?(user)
        }
    }
}
