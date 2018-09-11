package com.tools.networks.interceptor;

import java.io.IOException;

import okhttp3.CacheControl;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

import static com.tools.networks.utils.NetWorkUtils.isNetWorkConnected;


public class NoNetCacheInterceptor implements Interceptor {
    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();
        boolean isNetWorkConnected = isNetWorkConnected();
        //无网络
        if (!isNetWorkConnected) {
            //使用缓存
            request = request.newBuilder()
                    .cacheControl(CacheControl.FORCE_CACHE)
                    .build();
            Response response = chain.proceed(request);

            //没网络 没缓存 走网络
            if (response.code() == 504) {
                request.newBuilder()
                        .cacheControl(CacheControl.FORCE_NETWORK)
                        .build();
                return chain.proceed(request);
            }

            return response.newBuilder()
                    .header("Cache-Control", "public, only-if-cached, max-stale=3600")
                    .removeHeader("Pragma")
                    .build();

        }
        return chain.proceed(request);
    }
}
