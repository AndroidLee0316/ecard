package com.pasc.business.ecardbag.presenter;

import android.util.Log;

import com.pasc.business.ecardbag.iview.EcardUnBIndView;
import com.pasc.business.ecardbag.utils.EventBusUtils;
import com.pasc.lib.ecardbag.EcardDataManager;
import com.pasc.lib.ecardbag.net.EcardBiz;
import com.pasc.lib.ecardbag.net.pamars.EcardUnbindPamars;
import com.pasc.lib.ecardbag.net.resq.EcardInfoResq;
import com.pasc.lib.net.ApiV2Error;

import java.util.List;

import io.reactivex.functions.Consumer;

public class EcardUnbindPresenter implements IPresenter {
    private EcardUnBIndView iview;

    public EcardUnbindPresenter(EcardUnBIndView iview) {
        this.iview = iview;
    }

    /**
     * 卡证解绑
     **/
    public void unBind(String identifier, String credential, int pos) {
        iview.showLoadings();
        EcardUnbindPamars pamars = new EcardUnbindPamars();
        pamars.identifier = identifier;
        pamars.credential = credential;
        disposables.add(EcardBiz.ecrdUnbind(pamars).subscribe(new Consumer<Object>() {
            @Override
            public void accept(Object o) throws Exception {
                iview.dismissLoadings();
                //解绑成功，删除本地缓存的卡证列表中的该卡证，不需要从服务器重新获取
                if (EcardDataManager.getInstance().getEcardListFromCache()!=null && EcardDataManager.getInstance().getEcardListFromCache().size()>pos){
                    EcardDataManager.getInstance().getEcardListFromCache().remove(pos);
                    EventBusUtils.postEcardListUnbind();
                    iview.unBind(o);
                }else {
                    Log.e(getClass().getSimpleName(),"unBind ecard failed");
                }
            }
        }, new Consumer<Throwable>() {
            @Override
            public void accept(Throwable throwable) throws Exception {
                iview.dismissLoadings();
                if (throwable instanceof ApiV2Error) {
                    ApiV2Error apiV2Error = (ApiV2Error) throwable;
                    iview.onError(apiV2Error.getCode(), apiV2Error.getMsg());
                } else {
                    iview.showServiceError(throwable.getMessage());

                }
            }
        }));

    }

    @Override
    public void onDestroy() {
        disposables.clear();
    }
}
