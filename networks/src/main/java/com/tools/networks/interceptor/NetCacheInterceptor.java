package com.tools.networks.interceptor;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

import static com.tools.networks.utils.NetWorkUtils.isNetWorkConnected;


public class NetCacheInterceptor implements Interceptor {
    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();
        boolean connected = isNetWorkConnected();
        //有网络缓存60s
        if (connected) {
            return chain.proceed(request)
                    .newBuilder()
                    .removeHeader("Pragma")
                    .header("Cache-Control", "public, max-age=60")
                    .build();
        }
        return chain.proceed(request);
    }
}
