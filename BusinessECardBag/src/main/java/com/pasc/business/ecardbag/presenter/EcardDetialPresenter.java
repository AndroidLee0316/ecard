package com.pasc.business.ecardbag.presenter;

import android.util.Log;

import com.pasc.business.ecardbag.iview.EcardDetailView;
import com.pasc.lib.ecardbag.net.EcardBiz;
import com.pasc.lib.ecardbag.net.pamars.SortPamars;
import com.pasc.lib.ecardbag.net.resq.EcardDetailResq;
import com.pasc.lib.net.ApiV2Error;

import java.util.concurrent.TimeUnit;

import io.reactivex.Scheduler;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

/**
 *  功能：卡证详情
 *
 *  @author zoujianbo
 *  email : ZOUJIANBO345@pingan.com.cn
 *  date : 2020/01/09
 */
public class EcardDetialPresenter implements IPresenter {
    private EcardDetailView iview;

    public EcardDetialPresenter(EcardDetailView iview) {
        this.iview = iview;
    }
    /**
     *获取卡证详情
     * **/
    public void getDetial(String identifier){
        iview.showLoadings();
        SortPamars.SortBean parmars = new SortPamars.SortBean(identifier);
        disposables.add(EcardBiz.ecrdDetail(parmars)
                .delay(500, TimeUnit.MILLISECONDS)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<EcardDetailResq>() {
            @Override
            public void accept(EcardDetailResq resq) throws Exception {

                    iview.getDetial(resq);
                    iview.dismissLoadings();

            }
        }, new Consumer<Throwable>() {
            @Override
            public void accept(Throwable throwable) throws Exception {

                if (throwable instanceof ApiV2Error) {
                    ApiV2Error apiV2Error = (ApiV2Error) throwable;
                    iview.onError(apiV2Error.getCode(), apiV2Error.getMsg());
                }else {
                    iview.showServiceError(throwable.getMessage());

                }
                iview.dismissLoadings();
            }
        }));

    }

    @Override
    public void onDestroy() {
        disposables.clear();
    }
}
