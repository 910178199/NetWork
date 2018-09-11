package com.tools.networks.download;



import com.tools.networks.exception.ApiException;

import io.reactivex.Observer;
import okhttp3.ResponseBody;

import static com.tools.networks.utils.ToastUtils.showToast;


public abstract class BaseDownloadObserver implements Observer<ResponseBody> {

    protected abstract void doOnError(String error);

    @Override
    public void onError(Throwable e) {
        setError(ApiException.handleException(e).getMessage());
    }

    private void setError(String errorMsg) {
        showToast(errorMsg);
        doOnError(errorMsg);
    }

}
