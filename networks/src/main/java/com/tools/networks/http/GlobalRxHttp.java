package com.tools.networks.http;

import java.util.HashMap;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;

public class GlobalRxHttp {

    private static GlobalRxHttp instance;

    private static HashMap<String, Object> retrofitServiceCache;

    public GlobalRxHttp() {
        retrofitServiceCache = new HashMap<>();
    }

    public static GlobalRxHttp getInstance() {
        if (instance == null) {
            synchronized (GlobalRxHttp.class) {
                if (instance == null) {
                    instance = new GlobalRxHttp();
                }
            }
        }
        return instance;
    }

    /**
     * 设置基础Url
     *
     * @param baseUrl
     * @return
     */
    public GlobalRxHttp setBaseUrl(String baseUrl) {
        getGlobalRetrofitBuilder().baseUrl(baseUrl);
        return this;
    }


    /**
     * 设置Client
     *
     * @param okClient
     * @return
     */
    public GlobalRxHttp setOkClient(OkHttpClient okClient) {
        getGlobalRetrofitBuilder().client(okClient);
        return this;
    }


    /**
     * 全局Retrofit
     *
     * @return
     */
    public static Retrofit getGlobalRetrofit() {
        return RetrofitClient.getInstance().getRetrofit();
    }

    /**
     * 全局 Retrofig.Builder
     *
     * @return
     */
    private Retrofit.Builder getGlobalRetrofitBuilder() {
        return RetrofitClient.getInstance().getRetrofitBuilder();
    }

    /**
     * 全局变量请求统一管理
     */
    public static <K> K creatApi(final Class<K> kClass) {
        if (null == retrofitServiceCache) {
            retrofitServiceCache = new HashMap<>();
        }
        K retrofitService = (K) retrofitServiceCache.get(kClass.getCanonicalName());
        if (retrofitService == null) {
            retrofitService = getGlobalRetrofit().create(kClass);
            retrofitServiceCache.put(kClass.getCanonicalName(), retrofitService);
        }
        return retrofitService;
    }

}
