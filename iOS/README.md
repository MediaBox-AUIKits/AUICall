# AUICall
阿里云 · AUI Kits 通话场景集成工具

## 介绍
AUI Kits 通话场景集成工具是阿里云提供的跨平台音视频实时通信服务，为业务方提供通话、会议、语聊等场景的能力，借助通信的稳定、流畅、灵活的产品能力，以低代码的方式助力业务方快速发布应用。


## 源码说明

### 源码下载
下载地址[请参见](https://github.com/MediaBox-AUIKits/AUICall/tree/main/iOS)

### 源码结构
```
├── iOS  // iOS平台的根目录
│   ├── AUICall.podspec                // pod描述文件
│   ├── Source                                    // 源代码文件
│   ├── Resources                                 // 资源文件
│   ├── Frameworks                                // 依赖AUIRoomEngine库
│   ├── Example                                   // Demo代码
│   ├── AUIBaseKits                               // 基础UI组件   
│   ├── README.md                                 // Readme  

```

### 环境要求
- Xcode 12.0 及以上版本，推荐使用最新正式版本
- CocoaPods 1.9.3 及以上版本
- 准备 iOS 10.0 及以上版本的真机

### 前提条件
- 开通互动直播应用，并在应用服务端集成互动直播服务，提供获取入会TokenAPI
- 开通互动消息应用，并在应用服务端集成互动消息服务，提供获取登录TokenAPI


## 跑通demo

- 源码下载后，进入Example目录
- 在Example目录里执行命令“pod install  --repo-update”，自动安装依赖SDK
- 打开工程文件“AUICallExample.xcworkspace”，修改包Id
- 完成前提条件后，进入文件AUICallAppServer.swift，修改服务端域名
```swift
// AUICallAppServer.swift
public class AUICallAppServer: NSObject {
    public static let serverDomain = "你的应用服务器域名"
    ...
}
```
- 完成前提条件后，进入文件AUICallGlobalConfig.swift，修改互动直播应用appID
```swift
// AUICallGlobalConfig.swift
public class AUICallGlobalConfig: NSObject {
    
    // 正式
    public static let appID = "你的appID"
    public static let gslb = "https://gw.rtn.aliyuncs.com"
    
    public static let dimensions = CGSize(width: 360, height: 640)
    public static let frameRate = 15
}
```

- 如果需要体验1v1通话，可以切换到”AUICall1V1Example“Target 进行编译运行
- 如果需要体验多人通话，可以切换到”AUICallNVNExample“Target 进行编译运行


## 快速开发自己的通话功能
可通过以下几个步骤快速集成AUICall到你的APP中，让你的APP具备通话功能

### 集成源码
- 导入AUICall：仓库代码下载后，拷贝iOS文件夹到你的APP代码目录下，改名为AUICall，与你的Podfile文件在同一层级，可以删除Example目录
- 修改你的Podfile，引入：
  - AliVCSDK_PremiumLive：适用于互动直播的音视频终端SDK，也可以使用：AliVCSDK_Premium/AliVCSDK_Standard/AliVCSDK_InteractiveLive，参考[快速集成](https://help.aliyun.com/document_detail/2412571.htm)
  - AUIFoundation：基础UI组件
  - AUIMessage：互动消息组件
  - AUICall：通话场景UI组件源码，根据自身的业务，可以选择1v1或NVN，也可以同时集成这2个模块
```ruby

#需要iOS10.0及以上才能支持
platform :ios, '10.0'

target '你的App target' do
    # 根据自己的业务场景，集成合适的音视频终端SDK，支持：AliVCSDK_Standard、AliVCSDK_InteractiveLive
    # 如果你的APP中还需要频短视频编辑功能，可以使用音视频终端全功能SDK（AliVCSDK_Standard），可以把本文件中的所有AliVCSDK_InteractiveLive替换为AliVCSDK_Standard
    pod 'AliVCSDK_InteractiveLive', '~> 6.7.0'

    # 基础UI组件
    pod 'AUIFoundation/All', :path => "./AUICall/AUIBaseKits/AUIFoundation/"

    # 互动消息组件
    pod 'AUIMessage/AliVCIM', :path => "./AUICall/AUIBaseKits/AUIMessage/"
    
    # 集成RoomEngineSDK
    pod 'AUICall/RoomEngine_Lib/AliVCSDK_InteractiveLive', :path => './AUICall/'
    # 通话组件，集成1v1通话模块
    pod 'AUICall/1V1',  :path => "./AUICall/"
    # 通话组件，集成多人通话模块
    pod 'AUICall/NVN',  :path => "./AUICall/"
end
```
- 执行“pod install --repo-update”
- 源码集成完成

### 工程配置
- 打开工程info.Plist，添加NSCameraUsageDescription和NSMicrophoneUsageDescription权限
- 打开工程设置，在”Signing & Capabilities“中开启“Background Modes”


### 源码配置
- 完成前提条件后，进入文件AUICallAppServer.swift，修改服务端域名
```swift
// AUICallAppServer.swift
public class AUICallAppServer: NSObject {
    public static let serverDomain = "你的应用服务器域名"
    ...
}
```
- 完成前提条件后，进入文件AUICallGlobalConfig.swift，修改互动直播应用appID
```swift
// AUICallGlobalConfig.swift
public class AUICallGlobalConfig: NSObject {
    
    // 正式
    public static let appID = "你的appID"
    public static let gslb = "https://gw.rtn.aliyuncs.com"
    
    public static let dimensions = CGSize(width: 360, height: 640)
    public static let frameRate = 15
}
```
### 调用API
前面工作完成后，接下来可以根据自身的业务场景和交互，可以在你APP其他模块或主页上通过通话组件接口快速实现会议通话功能，也可以根据自身的需求修改源码。

- 1V1通话场景
``` Swift
// 登录
AUICall1V1Manager.defaultManager.tryLogin(loginUser: user) { success in
    if success {
        // 成功
    }
    else {
        // 失败
    }
}

// 登出
AUICall1V1Manager.defaultManager.logout()

// 呼叫
AUICall1V1Manager.defaultManager.startCall(mode: .video, destUser: user)

```

- 多人通话/会议场景
``` Swift
// 登录
AUICallNVNManager.defaultManager.tryLogin(loginUser: user) { success in
    if success {
        // 成功
    }
    else {
        // 失败
    }
}

// 登出
AUICallNVNManager.defaultManager.logout()

// 创建通话房间
AUICallNVNManager.defaultManager.createCall(roomName: nil)

// 创建通话房间并进入
AUICallNVNManager.defaultManager.startCall(userConfig: userConfig, roomName: nil, currVC: self) {
  // 成功进入
}

// 进入通话房间
AUICallNVNManager.defaultManager.joinCall(room: room, userConfig: userConfig, currVC: self) {
  // 成功进入
}

```

### 基于AUIRoomEngine开发高级功能
AUIRoomEngine是一套通用业务解决方案的模型与API，主要针对的是基于音视频在线实时通信的业务场景，如会议、课堂、通话等。主要目的是为了降低在对接上述业务场景开发时的复杂度，沉淀通用业务技术接口，提升业务开发效率。

AUIRoomEngine基于通话会议场景提供的是无UI细粒度的接口，AUICall基于AUIRoomEngine的API实现包含UI交互的通话功能，如果有更多高级功能的开发，可以基于AUICall进行二开，直接调用AUIRoomEngine的API来完成。

附：AUIRoomEngine更多信息及API文档

### 运行结果
参考Demo

## 常见问题
更多AUIKits问题咨询及使用说明，请搜索钉钉群（35685013712）加入AUI客户支持群联系我们。
