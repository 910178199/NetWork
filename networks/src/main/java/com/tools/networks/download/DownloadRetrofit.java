package com.tools.networks.download;




import com.tools.networks.interceptor.Transformer;

import io.reactivex.Observable;
import okhttp3.ResponseBody;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;


public class DownloadRetrofit {

    private static DownloadRetrofit instance;
    private Retrofit retrofit;
    private static String baseUrl = "http://www.github.com";

    public static DownloadRetrofit getInstance() {
        if (null == instance) {
            synchronized (DownloadRetrofit.class) {
                if (null == instance) {
                    instance = new DownloadRetrofit();
                }
            }
        }
        return instance;
    }

    public DownloadRetrofit() {
        retrofit = new Retrofit.Builder()
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(baseUrl)
                .build();
    }

    public Retrofit getRetrofit() {
        return retrofit;
    }

    public static Observable<ResponseBody> downloadFile(String downloadUrl) {
        return DownloadRetrofit.getInstance()
                .getRetrofit()
                .create(DownloadApi.class)
                .downloadFile(downloadUrl)
                .compose(Transformer.<ResponseBody>switchSchedulers());
    }

}
