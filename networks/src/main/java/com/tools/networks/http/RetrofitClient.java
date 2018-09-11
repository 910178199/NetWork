package com.tools.networks.http;



import com.tools.networks.config.OkHttpConfig;
import com.tools.networks.gson.GsonAdapter;
import com.tools.networks.interceptor.HttpLogger;


import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

/**
 * RetrofitClient工具类
 */
public class RetrofitClient {

    private static RetrofitClient instance;

    private Retrofit.Builder mRetrofitBuilder;

    private OkHttpClient mOkhttpClient;

    public RetrofitClient() {
        initDefaultOkHttpClient();
        mRetrofitBuilder = new Retrofit.Builder()
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(ScalarsConverterFactory.create())
                .addConverterFactory(GsonConverterFactory.create(GsonAdapter.buildGson()));
    }

    private void initDefaultOkHttpClient() {
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        builder.readTimeout(10, TimeUnit.SECONDS);
        builder.writeTimeout(10, TimeUnit.SECONDS);
        builder.connectTimeout(10, TimeUnit.SECONDS);
        //ssl配置
//        SSLUtils.SSLParams sslParams = new SSLUtils.SSLParams();
//        builder.sslSocketFactory(sslParams.sSLSocketFactory, sslParams.trustManager);
        //拦截器配置
        HttpLoggingInterceptor httpLoggingInterceptor = new HttpLoggingInterceptor(new HttpLogger());
        httpLoggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        builder.addInterceptor(httpLoggingInterceptor);

        mOkhttpClient = builder.build();
    }

    public static RetrofitClient getInstance() {
        if (instance == null) {
            synchronized (RetrofitClient.class) {
                if (instance == null) {
                    instance = new RetrofitClient();
                }
            }
        }
        return instance;
    }

    public Retrofit.Builder getRetrofitBuilder() {
        return mRetrofitBuilder;
    }

    /**
     * 默认配置 或者 OkHttpConfig配置
     *
     * @return
     */
    public Retrofit getRetrofit() {
        if (null == OkHttpConfig.getOkHttpClient()) {
            return mRetrofitBuilder.client(mOkhttpClient).build();
        } else {
            return mRetrofitBuilder.client(OkHttpConfig.getOkHttpClient()).build();
        }
    }

}
