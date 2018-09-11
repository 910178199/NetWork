# NetWork
封装Rxjava+Retrofit网络库
添加Gradle依赖
先在项目根目录的 build.gradle 的 repositories 添加:

     allprojects {
         repositories {
            ...
            maven { url "https://jitpack.io" }
        }
    }
然后在dependencies添加:

       dependencies {
       ...
       implementation 'com.github.910178199:NetWork:v1.0'
       }


使用说明
1、在application类里边进行初始化配置
在自己的Application的onCreate方法中进行初始化配置
public class MyApplication extends Application {

    Map<String, Object> headerMaps = new HashMap<>();

    @Override
    public void onCreate() {
        super.onCreate();

              OkHttpClient okHttpClient = new OkHttpConfig
                .Builder(this)
                //全局的请求头信息
                .setHeaders(headerMaps)
                //开启缓存策略(默认false)
                //1、在有网络的时候，先去读缓存，缓存时间到了，再去访问网络获取数据；
                //2、在没有网络的时候，去读缓存中的数据。
                .setCache(true)
                //全局持久话cookie,保存到内存（new MemoryCookieStore()）或者保存到本地（new SPCookieStore(this)）
                //不设置的话，默认不对cookie做处理
                .setCookieType(new SPCookieStore(this))
                //可以添加自己的拦截器(比如使用自己熟悉三方的缓存库等等)
                //.setAddInterceptor(null)
                //全局ssl证书认证
                //1、信任所有证书,不安全有风险（默认信任所有证书）
                //.setSslSocketFactory()
                //2、使用预埋证书，校验服务端证书（自签名证书）
                //.setSslSocketFactory(cerInputStream)
                //3、使用bks证书和密码管理客户端证书（双向认证），使用预埋证书，校验服务端证书（自签名证书）
                //.setSslSocketFactory(bksInputStream,"123456",cerInputStream)
                //全局超时配置
                .setReadTimeout(10)
                //全局超时配置
                .setWriteTimeout(10)
                //全局超时配置
                .setConnectTimeout(10)
                //全局是否打开请求log日志
                .setDebug(true)
                .build();

        RxHttpUtils
                .getInstance()
                .init(this)
                .config()
                //配置全局baseUrl
                .setBaseUrl("https://api.douban.com/")
                //开启全局配置
                .setOkClient(okHttpClient);
      
    }
}


2、默认已实现三种数据格式

1、CommonObserver （使用写自己的实体类即可，不用继承任何base）

2、StringObserver (直接String接收数据)

3、DataObserver (适合{"code":200,"msg":"描述",data:{}}这样的格式，需要使用BaseData<T> ,其中T为data中的数据模型)
如果以上三种不能满足你的需要，可以分别继承对应的baseObserver方法实现自己的逻辑


代码实例
使用Application里边的全局配置的参数
2.1、使用CommonObserver请求示例


a、   数据结构
     {
        "code": 0,
        "msg": "success",
        "username":"Allen",
        "job":"Android",
        ...
     }
      备注：TestBean为以上数据结构的模型
      
      

b、   @GET("api/test")
      Observable<TestBean> getTestData();
      
      
      

c、   RxHttpUtils
                 .createApi(ApiService.class)
                 .getTestData()
                 .compose(Transformer.<TestBean>switchSchedulers())
                 .subscribe(new CommonObserver<TestBean>() {

                     @Override
                     protected void onError(String errorMsg) {
                          //错误处理
                     }

                     @Override
                     protected void onSuccess(TestBean bookBean) {
                          //业务处理
                     }
                  });
                  
                  
                  
2.2、使用DataObserver请求示例


a、     数据结构
        {
            "code":0,
            "msg":"success",
            "data":{
                "username":"Allen",
                "job":"Android Dev"
                ...
            }
        }
        备注：TestBean为data中的数据模型
        


b、     @GET("api/test")
        Observable<BaseData<TestBean>> geTestData();



c、RxHttpUtils.createApi(ApiServer.class)
                .geTestData()
                .compose(Transformer.<BaseData<TestBean>>switchSchedulers())
                .subscribe(new DataObserver<TestBean>() {
                    @Override
                    protected void onError(String errorMsg) {
                        
                    }

                    @Override
                    protected void onSuccess(TestBean data) {

                    }
                });
                
                
2.3、使用StringObserver请求示例

a、     @GET("api/test")
        Observable<String> geTestData();
        

b、      RxHttpUtils.createApi(ApiServer.class)
                .geTestData()
                .compose(Transformer.<String>switchSchedulers())
                .subscribe(new DataObserver<String>() {
                    @Override
                    protected void onError(String errorMsg) {

                    }

                    @Override
                    protected void onSuccess(String data) {

                    }
                });
                
                
2.4、链式请求示例--请求参数是上个请求的结果

                RxHttpUtils
                        .createApi(ApiService.class)
                        .getBook()
                        .flatMap(new Function<BookBean, ObservableSource<Top250Bean>>() {
                            @Override
                            public ObservableSource<Top250Bean> apply(@NonNull BookBean bookBean) throws Exception {
                                return RxHttpUtils
                                        .createApi(ApiService.class)
                                        .getTop250(20);
                            }
                        })
                        .compose(Transformer.<Top250Bean>switchSchedulers(loading_dialog))
                        .subscribe(new CommonObserver<Top250Bean>() {

                            @Override
                            protected void onError(String errorMsg) {
                                //错误处理
                            }

                            @Override
                            protected void onSuccess(Top250Bean top250Bean) {
                               //业务处理
                            }
                        });
                        
                        
3、单个请求配置(即将废除)
温馨提示：针对某些请求有特殊要求的才建议使用此类方法，没特殊要求不建议使用


3.1、单个请求使用默认配置

                //单个请求使用默认配置的参数
                RxHttpUtils
                        .getSInstance()
                        .baseUrl("https://api.douban.com/")
                        .createSApi(ApiService.class)
                        .getTop250(10)
                        .compose(Transformer.<Top250Bean>switchSchedulers(loading_dialog))
                        .subscribe(new CommonObserver<Top250Bean>() {

                            @Override
                            protected void onError(String errorMsg) {
                                //错误处理
                            }

                            @Override
                            protected void onSuccess(Top250Bean top250Bean) {
                               //业务处理
                            }
                        });
                        

3.2、单个请求配置参数示例(可以根据需求选择性的配置)

                //单个请求自己配置相关参数
                RxHttpUtils
                        .getSInstance()
                        .baseUrl("https://api.douban.com/")
                        .addHeaders(headerMaps)
                        .cache(true)
                        .cachePath("cachePath", 1024 * 1024 * 100)
                        .sslSocketFactory()
                        .cookieType(new MemoryCookieStore())
                        .writeTimeout(10)
                        .readTimeout(10)
                        .connectTimeout(10)
                        .log(true)
                        .createSApi(ApiService.class)
                        .getTop250(10)
                        .compose(Transformer.<Top250Bean>switchSchedulers(loading_dialog))
                        .subscribe(new CommonObserver<Top250Bean>() {

                            @Override
                            protected void onError(String errorMsg) {
                                //错误处理
                            }

                            @Override
                            protected void onSuccess(Top250Bean top250Bean) {
                               //业务处理
                            }
                        });
                        
                        
3.3、单个请求返回string

                RxHttpUtils.getSInstance()
                        .baseUrl("https://api.douban.com/")
                        //注意这两个配置的顺序
                        .addConverterFactory(ScalarsConverterFactory.create())
                        .addConverterFactory(GsonConverterFactory.create())
                        .createSApi(ApiService.class)
                        .getBookString()
                        .compose(Transformer.<String>switchSchedulers(loading_dialog))
                        .subscribe(new StringObserver() {
                            @Override
                            protected void onError(String errorMsg) {

                            }

                            @Override
                            protected void onSuccess(String data) {
                                showToast(data);
                                responseTv.setText(data);
                            }
                        });
                        
4、文件下载 ----使用简单粗暴

                String url = "https://t.alipayobjects.com/L1/71/100/and/alipay_wap_main.apk";
                final String fileName = "alipay.apk";

                RxHttpUtils
                        .downloadFile(url)
                        .subscribe(new DownloadObserver(fileName) {
                            //可以通过配置tag用于取消下载请求
                            @Override
                            protected String setTag() {
                                return "download";
                            }

                            @Override
                            protected void onError(String errorMsg) {
                            }

                            @Override
                            protected void onSuccess(long bytesRead, long contentLength, float progress, boolean done, String filePath) {
                                download_http.setText("下 载中：" + progress + "%");
                                if (done) {
                                    responseTv.setText("下载文件路径：" + filePath);
                                }

                            }
                        });
                        
                        
5、上传图片
上传单张图片的接口

        RxHttpUtils.uploadImg(uploadUrl, uploadPath)
                .compose(Transformer.<ResponseBody>switchSchedulers(loading_dialog))
                .subscribe(new CommonObserver<ResponseBody>() {
                    @Override
                    protected void onError(String errorMsg) {
                        Log.e("allen", "上传失败: " + errorMsg);
                        showToast(errorMsg);
                    }

                    @Override
                    protected void onSuccess(ResponseBody responseBody) {
                        try {
                            showToast(responseBody.string());
                            Log.e("allen", "上传完毕: " + responseBody.string());
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                 
                });
                
                
上传多张图片的接口

        RxHttpUtils.uploadImgs("yourPicUrl", uploadPaths)
                .compose(Transformer.<ResponseBody>switchSchedulers(loading_dialog))
                .subscribe(new CommonObserver<ResponseBody>() {
                    @Override
                    protected void onError(String errorMsg) {
                    }

                    @Override
                    protected void onSuccess(ResponseBody responseBody) {
                    }
                });
                
                
6、取消请求 相关配置

                new XXXObserver<BookBean>() {

                    //重写setTag方法配置当前请求的tag
                    //温馨提示：可以多个请求设置相同的tag自动归为一组，可以一次取消相同tag的所有请求
                    //(适用于一个页面多个请求，配置相同tag，在页面销毁时一次性取消)
                    @Override
                    protected String setTag() {
                        return "yourTag";
                    }

                    @Override
                    protected void onError(String errorMsg) {

                    }

                    @Override
                    protected void onSuccess(BookBean bookBean) {

                    }
                }

                //调取如下方法取消某个或某组请求
                RxHttpUtils.cancel("yourTag");

                //调取如下方法取消多个或多组请求
                RxHttpUtils.cancel("yourTag1","yourTag2","yourTag3");
                
                
7、onError中默认Toast显示隐藏的配置

在CommonObserver或DataObserver或StringObserver中重写isHideToast方法，默认false显示toast

                            //默认false   隐藏onError的提示
                            @Override
                            protected boolean isHideToast() {
                                return true;
                            }
