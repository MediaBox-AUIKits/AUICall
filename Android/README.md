# AUICall
阿里云 · AUI Kits 通话场景集成工具

## 介绍
AUI Kits 通话场景集成工具是阿里云提供的跨平台音视频实时通信服务，为业务方提供通话、会议、语聊等场景的能力，借助通信的稳定、流畅、灵活的产品能力，以低代码的方式助力业务方快速发布应用。


## 源码说明

### 源码下载
下载地址[请参见](https://github.com/MediaBox-AUIKits/AUICall/tree/main/iOS)

### 源码结构
```
├── Android
│   ├── AUIBaseKits                     //基础组件
│   ├── AUICall                         //场景方案源代码
│   ├── README.md                       // README
│   ├── app                             //主工程模块入口
│   ├── build.gradle                    //工程配置文件
│   ├── config.gradle                   //工程配置文件
│   └── settings.gradle                 //工程配置文件
```

### 环境要求
- Android Studio 插件版本4.1.2
- Gradle 6.5
- Android Studio自带 jdk11

### 前提条件
- 开通互动直播应用，并在应用服务端集成互动直播服务，提供获取入会TokenAPI
- 开通互动消息应用，并在应用服务端集成互动消息服务，提供获取登录TokenAPI


## 跑通demo

- 源码下载后，进入`AUICall`目录下的java源代码文件夹，找到`com.aliyun.auikits.auicall.util.AUICallConfig`类，修改几个必填字段值

```java
public final class AUICallConfig {
    public static final String APP_ID = "xxx"; //你的AppId, 必填
    public static final String APP_GROUP = "xxx"; //你的AppGroup, 必填
    public static final String HOST = "xxx"; //你的服务器域名地址, 必填
    ...
}

``` 
- Android Studio 菜单导入工程: File -> New -> Import Project，选择工程根目录Android文件夹导入
- 等待gradle同步完成，编译运行工程

## 快速开发自己的通话功能
可通过以下几个步骤快速集成AUICall到你的APP中，让你的APP具备通话功能

### 集成源码
1. 导入AUICall：仓库代码下载后，Android Studio菜单选择: File -> New -> Import Module，导入选择AUICall文件夹
2. 修改AUICall文件夹下的build.gradle的三方库依赖项

```gradle
dependencies {

    implementation 'androidx.appcompat:appcompat:x.x.x'                     //修改x.x.x为你工程适配的版本
    implementation 'com.google.android.material:material:x.x.x'             //修改x.x.x为你工程适配的版本
    androidTestImplementation 'androidx.test.espresso:espresso-core:x.x.x'  //修改x.x.x为你工程适配的版本
    implementation 'com.aliyun.aio:AliVCSDK_Premium:x.x.x'                  //修改x.x.x为你工程适配的版本
}
```
3. 等待gradle同步完成，完成源码集成

### 源码配置

- 进入`AUICall`模块下的java源代码文件夹，找到`com.aliyun.auikits.auicall.util.AUICallConfig`类，修改几个必填字段值

```java
public final class AUICallConfig {
    public static final String APP_ID = "xxx"; //你的AppId, 必填
    public static final String APP_GROUP = "xxx"; //你的AppGroup, 必填
    public static final String HOST = "xxx"; //你的服务器域名地址, 必填
    ...
}
```

### 调用API
前面工作完成后，接下来可以根据自身的业务场景和交互，可以在你APP其他模块或主页上通过通话组件接口快速实现会议通话功能，也可以根据自身的需求修改源码。

- 1V1通话场景
```java
AUICall1V1Model model = new AUICall1V1ModelImpl(); //构造单人通话业务接口实例
...
model.call("xxx", AUICall1V1Mode.VIDEO); //视频通话呼叫对方
...
model.accept("xxx"); //接受通话呼叫
...
model.hangup(false); //挂断通话

```

- 多人通话/会议场景
```java
AUICallNVNModel model = new AUICallNVNModelImpl(); //构造多人音视频业务接口实例
...
model.create(new CreateRoomCallback(){ //创建多人音视频通话房间
    public void OnSuccess(String roomId){
        //房间创建成功
    }

    public void onError(int code, String msg){
        ...
    }
});
...
model.join("xxx", true, true); //加入多人音视频通话房间
...
model.invite("xxx"); //邀请用户加入多人音视频通话房间
...
model.leave(); //离开多人音视频通话房间
```

### 基于AUIRoomEngine开发高级功能
AUIRoomEngine是一套通用业务解决方案的模型与API，主要针对的是基于音视频在线实时通信的业务场景，如会议、课堂、通话等。主要目的是为了降低在对接上述业务场景开发时的复杂度，沉淀通用业务技术接口，提升业务开发效率。

AUIRoomEngine基于通话会议场景提供的是无UI细粒度的接口，AUICall基于AUIRoomEngine的API实现包含UI交互的通话功能，如果有更多高级功能的开发，可以基于AUICall进行二开，直接调用AUIRoomEngine的API来完成。

附：AUIRoomEngine更多信息及API文档

### 运行结果
参考Demo

## 常见问题
更多AUIKits问题咨询及使用说明，请搜索钉钉群（35685013712）加入AUI客户支持群联系我们。