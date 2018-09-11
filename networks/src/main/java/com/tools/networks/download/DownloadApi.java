package com.tools.networks.download;

import io.reactivex.Observable;
import okhttp3.ResponseBody;
import retrofit2.http.GET;
import retrofit2.http.Streaming;
import retrofit2.http.Url;

public interface DownloadApi {

    /**
     * 下载接口
     * 大文件官方建议用 @Streaming 来进行注解，不然会出现IO异常，小文件可以忽略不注入
     */
    @Streaming
    @GET
    Observable<ResponseBody> downloadFile(@Url String url);

}
