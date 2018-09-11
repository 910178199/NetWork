package com.tools.networks.upload;




import com.tools.networks.http.RetrofitClient;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import io.reactivex.Observable;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Multipart;

public class UploadRetrofit {
    private static UploadRetrofit instance;
    private Retrofit mRetrofit;

    private static String baseUrl = "http://api.github.com/";

    public UploadRetrofit() {
        mRetrofit = new Retrofit.Builder()
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(baseUrl)
                .build();
    }

    public static UploadRetrofit getInstance() {
        if (instance == null) {
            synchronized (RetrofitClient.class) {
                if (instance == null) {
                    instance = new UploadRetrofit();
                }
            }
        }
        return instance;
    }

    public Retrofit getRetrofit() {
        return mRetrofit;
    }

    /**
     * 上传一张图片
     *
     * @param uploadUrl 上传图片的服务器url
     * @param filePath  图片路径
     * @return Observable
     */
    public static Observable<ResponseBody> uploadImage(String uploadUrl, String filePath) {
        List<String> filePaths = new ArrayList<>();
        filePaths.add(filePath);
        return uploadFilesWithParams(uploadUrl, "uploaded_file", null, filePaths);
    }

    /**
     * 只上传多张图片
     *
     * @param uploadUrl 上传图片的服务器url
     * @param filePaths 图片路径
     * @return Observable
     */
    public static Observable<ResponseBody> uploadImages(String uploadUrl, List<String> filePaths) {
        return uploadFilesWithParams(uploadUrl, "uploaded_file", null, filePaths);
    }

    /**
     * 图片和参数同时上传
     *
     * @param uploadUrl 上传服务器URl
     * @param fileName  文件名称
     * @param paramsMap 普通参数
     * @param filePath  图片路径
     * @return
     */
    public static Observable<ResponseBody> uploadFilesWithParams(String uploadUrl, String fileName,
                                                                 Map<String, Object> paramsMap, List<String> filePath) {
        MultipartBody.Builder builder = new MultipartBody.Builder()
                .setType(MultipartBody.FORM);

        if (null != paramsMap) {
            for (String key : paramsMap.keySet()) {
                builder.addFormDataPart(key, (String) paramsMap.get(key));
            }
        }

        for (int i = 0; i < filePath.size(); i++) {
            File file = new File(filePath.get(i));
            RequestBody imageBody = RequestBody.create(MediaType.parse("multipart/form-data"), file);
            builder.addFormDataPart(fileName, file.getName(), imageBody);
        }

        List<MultipartBody.Part> parts = builder.build().parts();
        return UploadRetrofit.getInstance()
                .getRetrofit()
                .create(UploadFileApi.class)
                .uploadFiles(uploadUrl, parts);
    }
}
