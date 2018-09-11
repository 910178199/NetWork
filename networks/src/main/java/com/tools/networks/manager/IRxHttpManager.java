package com.tools.networks.manager;

import io.reactivex.disposables.Disposable;
import okhttp3.Dispatcher;

public interface IRxHttpManager<T> {

    /**
     * 添加
     */
    void add(T tag, Disposable disposable);

    /**
     * 移除
     */
    void remove(T tag);

    /**
     * 取消某个
     */
    void cancel(T tag);

    /**
     * 取消某些
     */
    void cancel(T... tag);

    /**
     * 取消所有
     */
    void cancelAll();
}
