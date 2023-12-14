package com.aliyun.auikits.auicall.model.callback;

import com.alivc.auimessage.model.token.IMNewToken;

public interface TokenAccessor {
    IMNewToken getIMToken(String str);

    Long getRtcTimestamp();

    String getRtcToken(String str, String str2);
}
