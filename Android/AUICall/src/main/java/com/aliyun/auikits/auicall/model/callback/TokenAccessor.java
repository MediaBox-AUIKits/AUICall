package com.aliyun.auikits.auicall.model.callback;

public interface TokenAccessor {
    String getIMToken(String str);

    Long getRtcTimestamp();

    String getRtcToken(String str, String str2);
}
