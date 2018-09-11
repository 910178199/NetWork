package com.tools.networks.manager;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;

public class RxHttpManager implements IRxHttpManager<Object> {

    private static RxHttpManager instance = null;
    private HashMap<Object, CompositeDisposable> hashMap;

    public RxHttpManager() {
        hashMap = new HashMap<>();
    }

    public static RxHttpManager getInstance() {
        if (instance == null) {
            synchronized (RxHttpManager.class) {
                if (instance == null) {
                    instance = new RxHttpManager();
                }
            }
        }
        return instance;
    }


    @Override
    public void add(Object tag, Disposable dispatcher) {
        if (null == tag) {
            return;
        }
        /**
         *tag下的一组或一个请求，用来处理一个页面的所以请求或者某个请求
         *设置一个相同的tag就行就可以取消当前页面所有请求或者某个请求了
         */
        CompositeDisposable compositeDisposable = hashMap.get(tag);
        if (null == compositeDisposable) {
            CompositeDisposable cd = new CompositeDisposable();
            cd.add(dispatcher);
            hashMap.put(tag, cd);
        } else {
            compositeDisposable.add(dispatcher);
        }
    }

    @Override
    public void remove(Object tag) {
        if (null == tag) {
            return;
        }
        if (!hashMap.isEmpty()) {
            hashMap.remove(tag);
        }
    }

    @Override
    public void cancel(Object tag) {
        if (null == tag) {
            return;
        }
        if (hashMap.isEmpty()) {
            return;
        }
        if (null == hashMap.get(tag)) {
            return;
        }
        if (!hashMap.get(tag).isDisposed()) {
            hashMap.get(tag).dispose();
            hashMap.remove(tag);
        }
    }

    @Override
    public void cancel(Object... tag) {
        if (null == tag) {
            return;
        }
        for (Object ob : tag) {
            cancel(ob);
        }
    }

    @Override
    public void cancelAll() {
        if (hashMap.isEmpty()) {
            return;
        }
        Iterator<Map.Entry<Object, CompositeDisposable>> iterator = hashMap.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<Object, CompositeDisposable> next = iterator.next();
            Object key = next.getKey();
            cancel(key);
        }
    }
}
