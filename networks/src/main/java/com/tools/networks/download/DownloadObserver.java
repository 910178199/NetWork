package com.tools.networks.download;




import com.tools.networks.manager.RxHttpManager;

import java.io.IOException;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import okhttp3.ResponseBody;

public abstract class DownloadObserver extends BaseDownloadObserver {

    private String fileName;

    public DownloadObserver(String fileName) {
        this.fileName = fileName;
    }

    protected abstract void onError(String errerMsg);

    /**
     * 设置TAG之后  可以取消请求
     */
    protected String setTag() {
        return null;
    }

    /**
     * 成功回调
     *
     * @param byteRead
     * @param contentLength
     * @param progress
     * @param done
     * @param filePath
     */
    protected abstract void onSuccess(long byteRead, long contentLength, int progress, boolean done, String filePath);

    @Override
    protected void doOnError(String error) {
        onError(error);
    }

    @Override
    public void onSubscribe(Disposable d) {
        RxHttpManager.getInstance().add(setTag(), d);
    }

    @Override
    public void onNext(ResponseBody responseBody) {
        Observable.just(responseBody)
                .subscribeOn(Schedulers.io())
                .subscribe(new Observer<ResponseBody>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(ResponseBody responseBody) {
                        try {
                            new DownloadManager().saveFile(responseBody, fileName, new ProgressListener() {
                                @Override
                                public void onResponseProgress(final long byteRead, final long contentLength, final int progress, final boolean done, final String filePath) {
                                    Observable.just(progress)
                                            .distinctUntilChanged()
                                            .observeOn(AndroidSchedulers.mainThread())
                                            .subscribe(new Consumer<Integer>() {
                                                @Override
                                                public void accept(Integer integer) throws Exception {
                                                    onSuccess(byteRead, contentLength, progress, done, filePath);
                                                }
                                            });
                                }
                            });
                        } catch (IOException e) {
                            e.printStackTrace();
                            Observable.just(e.getMessage())
                                    .observeOn(AndroidSchedulers.mainThread())
                                    .subscribe(new Consumer<String>() {
                                        @Override
                                        public void accept(String s) throws Exception {
                                            doOnError(s);
                                        }
                                    });
                        }
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }
}
