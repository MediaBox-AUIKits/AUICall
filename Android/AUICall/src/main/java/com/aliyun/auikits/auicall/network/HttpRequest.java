package com.aliyun.auikits.auicall.network;

import java.util.concurrent.TimeUnit;
import okhttp3.OkHttpClient;

public final class HttpRequest {
    private static HttpRequest instance;
    private OkHttpClient mOkHttpClient = new OkHttpClient.Builder().connectTimeout(15, TimeUnit.SECONDS).readTimeout(15, TimeUnit.SECONDS).build();
    private static Object mObj = new Object();

    public static HttpRequest getInstance() {
        if (HttpRequest.instance == null) {
            synchronized (mObj) {
                if (HttpRequest.instance == null) {
                    HttpRequest.instance = new HttpRequest();
                }
            }
        }
        return HttpRequest.instance;
    }

    public final OkHttpClient getClient() {
        return this.mOkHttpClient;
    }
}