package com.pasc.business.ecardbag.presenter;

import io.reactivex.disposables.CompositeDisposable;

public interface IPresenter {
    public CompositeDisposable disposables = new CompositeDisposable();

    /**
     * 销毁
     **/
    void onDestroy();

}
