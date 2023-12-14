package com.aliyun.auikits.auicall.util;

public final class AUICallConfig {
    public static final String APP_ID = "xxx"; //你的AppId, 必填
    public static final String HOST = "xxx"; //你的服务器域名地址, 必填
    public static final String LOGIN_URL = HOST + "/api/v1/live/login";
    public static final String REQ_IM_TOKEN_URL = HOST + "/api/v2/live/token";
    public static final String REQ_RTC_TOKEN_URL = HOST + "/api/v1/live/getRtcAuthToken";
    public static final String GSLB = "https://gw.rtn.aliyuncs.com";
    public static final long CALLING_TIME_OUT_MILLISECONDS = 60000;
    public static final int ERROR_HEART_BEAT_TIMEOUT = 16908812;
    public static final int MAX_ID_LEN = 15;
    public static final String AUI_CALL = "aui-call";
}
