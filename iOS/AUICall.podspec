#
# Be sure to run `pod lib lint AUICall.podspec' to ensure this is a
# valid spec before submitting.
#
# Any lines starting with a # are optional, but their use is encouraged
# To learn more about a Podspec see https://guides.cocoapods.org/syntax/podspec.html
#

Pod::Spec.new do |s|
  s.name             = 'AUICall'
  s.version          = '1.0.0'
  s.summary          = 'A short description of AUICall.'

# This description is used to generate tags and improve search results.
#   * Think: What does it do? Why did you write it? What is the focus?
#   * Try to keep it short, snappy and to the point.
#   * Write the description between the DESC delimiters below.
#   * Finally, don't worry about the indent, CocoaPods strips it!

  s.description      = <<-DESC
TODO: Add long description of the pod here.
                       DESC

  s.homepage         = 'https://github.com/MediaBox-AUIKits/AUICall'
  # s.screenshots     = 'www.example.com/screenshots_1', 'www.example.com/screenshots_2'
  s.license          = { :type => 'MIT', :text => 'LICENSE' }
  s.author           = { 'aliyunvideo' => 'videosdk@service.aliyun.com' }
  s.source           = { :git => 'https://github.com/MediaBox-AUIKits/AUICall', :tag =>"v#{s.version}" }
  # s.social_media_url = 'https://twitter.com/<TWITTER_USERNAME>'

  s.ios.deployment_target = '10.0'
  s.static_framework = true
  s.swift_version = '5.0'
  s.default_subspecs='1V1'
  s.pod_target_xcconfig = {'GCC_PREPROCESSOR_DEFINITIONS' => '$(inherited) COCOAPODS=1'}

  s.subspec 'Common' do |ss|
    ss.resource = 'Resources/AUICallBase.bundle'
    ss.source_files = 'Source/Common/**/*.{swift,h,m,mm}', 'Source/AUICallLoginViewController.swift'
    ss.dependency 'AUIFoundation/All'
    ss.dependency 'SDWebImage'
  end
  
  s.subspec '1V1' do |ss|
    ss.dependency 'AUICall/Common'
    ss.resource = 'Resources/AUICall1V1.bundle'
    ss.source_files = 'Source/1V1/**/*.{swift,h,m,mm}', 'Source/AUICall1V1MainViewController.swift'
  end
  
  s.subspec 'NVN' do |ss|
    ss.dependency 'AUICall/Common'
    ss.resource = 'Resources/AUICallNVN.bundle'
    ss.source_files = 'Source/NVN/**/*.{swift,h,m,mm}', 'Source/AUICallNVNMainViewController.swift'
  end
  
  ########################### RoomEngine开源集成模式  ###########################
  s.subspec 'RoomEngine_Source' do |ss|
    ss.dependency 'AUIRoomEngineSDK'
  end
  
  
  ########################### RoomEngine不开源集成模式  ###########################
  s.subspec 'RoomEngine_Lib' do |ss|
    ss.subspec 'AliVCSDK_Premium' do |sss|
      sss.vendored_frameworks = 'Frameworks/AliVCSDK_Premium/AUIRoomEngineSDK.framework'
      sss.dependency 'AUIMessage'
      sss.dependency 'AliVCSDK_Premium'
    end
    
    ss.subspec 'AliVCSDK_PremiumLive' do |sss|
      sss.vendored_frameworks = 'Frameworks/AliVCSDK_PremiumLive/AUIRoomEngineSDK.framework'
      sss.dependency 'AUIMessage'
      sss.dependency 'AliVCSDK_PremiumLive'
    end
    
    ss.subspec 'AliVCSDK_InteractiveLive' do |sss|
      sss.vendored_frameworks = 'Frameworks/AliVCSDK_InteractiveLive/AUIRoomEngineSDK.framework'
      sss.dependency 'AUIMessage'
      sss.dependency 'AliVCSDK_InteractiveLive'
    end
    
    ss.subspec 'AliVCSDK_Standard' do |sss|
      sss.vendored_frameworks = 'Frameworks/AliVCSDK_Standard/AUIRoomEngineSDK.framework'
      sss.dependency 'AUIMessage'
      sss.dependency 'AliVCSDK_Standard'
    end
  end
  
end
