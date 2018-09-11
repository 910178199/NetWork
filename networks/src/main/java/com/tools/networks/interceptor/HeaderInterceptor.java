package com.tools.networks.interceptor;

import java.io.IOException;
import java.util.Map;
import java.util.TreeMap;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public class HeaderInterceptor implements Interceptor {
    private Map<String, Object> headers = new TreeMap<>();

    public HeaderInterceptor(Map<String, Object> headers) {
        this.headers = headers;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request.Builder request = chain.request().newBuilder();
        if (headers != null && headers.size() > 0) {
            for (Map.Entry<String, Object> map : headers.entrySet()) {
                request.header(map.getKey(), String.valueOf(map.getValue()));
            }
        }
        return chain.proceed(request.build());
    }
}
