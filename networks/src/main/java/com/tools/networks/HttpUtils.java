package com.tools.networks;

import android.app.Application;
import android.content.Context;








import com.tools.networks.config.OkHttpConfig;
import com.tools.networks.cookie.CookieJarImpl;
import com.tools.networks.cookie.store.CookieStore;
import com.tools.networks.download.DownloadRetrofit;
import com.tools.networks.http.GlobalRxHttp;
import com.tools.networks.manager.RxHttpManager;
import com.tools.networks.upload.UploadRetrofit;

import java.util.List;
import java.util.Map;

import io.reactivex.Observable;
import okhttp3.Cookie;
import okhttp3.HttpUrl;
import okhttp3.ResponseBody;

public class HttpUtils {

    private static HttpUtils instance;
    private static Application context;

    public static HttpUtils getInstance() {
        if (instance == null) {
            synchronized (HttpUtils.class) {
                if (instance == null) {
                    instance = new HttpUtils();
                }
            }
        }
        return instance;
    }

    public HttpUtils init(Application context) {
        HttpUtils.context = context;
        return this;
    }

    public static Context getContext() {
        checkInitialize();
        return context;
    }

    private static void checkInitialize() {
        if (context == null) {
            new ExceptionInInitializerError("在Application中调用 HttpUtils.getInstance().init(this) 进行初始化操作");
        }
    }

    /**
     * 配置
     *
     * @return
     */
    public GlobalRxHttp config() {
        checkInitialize();
        return GlobalRxHttp.getInstance();
    }

    /**
     * 创建请求
     *
     * @param cls
     * @param <K>
     * @return
     */
    public static <K> K createApi(Class<K> cls) {
        return GlobalRxHttp.creatApi(cls);
    }

    /**
     * 下载文件
     */
    public static Observable<ResponseBody> downloadFile(String fileUrl) {
        return DownloadRetrofit.downloadFile(fileUrl);
    }

    /**
     * 上传单张图片
     */
    public static Observable<ResponseBody> uploadImg(String uploadUrl, String filePath) {
        return UploadRetrofit.uploadImage(uploadUrl, filePath);
    }


    /**
     * 上传多张图片
     */
    public static Observable<ResponseBody> uploadImgs(String uploadUrl, List<String> filePaths) {
        return UploadRetrofit.uploadImages(uploadUrl, filePaths);
    }

    /**
     * 上传多张图片多个参数
     */
    public static Observable<ResponseBody> uploadFilesWithParams(String uploadUrl, String fileName,
                                                                 Map<String, Object> paramsMap, List<String> filePath) {
        return UploadRetrofit.uploadFilesWithParams(uploadUrl, fileName, paramsMap, filePath);
    }

    /**
     * 获取全局的CookieJarImpl实例
     */
    private static CookieJarImpl getCookieJar() {
        return (CookieJarImpl) OkHttpConfig.getOkHttpClient().cookieJar();
    }

    /**
     * 获取全局的CookieStore实例
     */
    private static CookieStore getCookieStore() {
        return getCookieJar().getCookieStore();
    }

    /**
     * 获取所有cookie
     */
    public static List<Cookie> getAllCookie() {
        CookieStore cookieStore = getCookieStore();
        List<Cookie> allCookie = cookieStore.getAllCookie();
        return allCookie;
    }

    /**
     * 获取某个url所对应的全部cookie
     */
    public static List<Cookie> getCookieByUrl(String url) {
        CookieStore cookieStore = getCookieStore();
        HttpUrl httpUrl = HttpUrl.parse(url);
        List<Cookie> cookies = cookieStore.getCookie(httpUrl);
        return cookies;
    }


    /**
     * 移除全部cookie
     */
    public static void removeAllCookie() {
        CookieStore cookieStore = getCookieStore();
        cookieStore.removeAllCookie();
    }

    /**
     * 移除某个url下的全部cookie
     */
    public static void removeCookieByUrl(String url) {
        HttpUrl httpUrl = HttpUrl.parse(url);
        CookieStore cookieStore = getCookieStore();
        cookieStore.removeCookie(httpUrl);
    }

    /**
     * 取消所有请求
     */
    public static void cancelAll() {
        RxHttpManager.getInstance().cancelAll();
    }

    /**
     * 取消某个或某些请求
     */
    public static void cancel(Object... tag) {
        RxHttpManager.getInstance().cancel(tag);
    }

}
