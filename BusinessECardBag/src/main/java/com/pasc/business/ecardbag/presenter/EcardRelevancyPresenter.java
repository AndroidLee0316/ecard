package com.pasc.business.ecardbag.presenter;

import com.pasc.business.ecardbag.iview.EcardRelevancyView;
import com.pasc.business.ecardbag.utils.EventBusUtils;
import com.pasc.lib.ecardbag.net.EcardBiz;
import com.pasc.lib.ecardbag.net.pamars.EcardBindPamars;
import com.pasc.lib.ecardbag.net.resq.EcardRelationResq;
import com.pasc.lib.net.ApiV2Error;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.functions.Consumer;

/**
 * 功能：关联卡证列表
 *
 * @author zoujianbo
 * email : ZOUJIANBO345@pingan.com.cn
 * date : 2020/01/09
 */
public class EcardRelevancyPresenter implements IPresenter {
    private EcardRelevancyView iview;

    public EcardRelevancyPresenter(EcardRelevancyView iview) {
        this.iview = iview;
    }

    /**
     * 获取未关联卡证
     **/
    public void getEcardRelation() {
        iview.showLoadings();
        disposables.add(EcardBiz.getEcardRelation().subscribe(new Consumer<EcardRelationResq>() {
            @Override
            public void accept(EcardRelationResq ecardRelationResq) throws Exception {
                iview.dismissLoadings();
                iview.getEcardRelation(ecardRelationResq);
            }
        }, new Consumer<Throwable>() {
            @Override
            public void accept(Throwable throwable) throws Exception {
                iview.dismissLoadings();
                if (throwable instanceof ApiV2Error) {
                    ApiV2Error apiV2Error = (ApiV2Error) throwable;
                    iview.onListError(apiV2Error.getCode(), apiV2Error.getMsg());
                } else {
                    iview.onListError("", "");

                }
            }
        }));
    }

    /**
     * 绑定卡证
     **/
    public void commitInfo(List<EcardRelationResq.EcardRelationInfo> datas) {
        EcardBindPamars pamars = new EcardBindPamars();
        pamars.bindCardList = new ArrayList<>();
        for (int i = 0; i < datas.size(); i++) {
            EcardBindPamars.EcardBindPamarsInfo info = new EcardBindPamars.EcardBindPamarsInfo();
            EcardRelationResq.EcardRelationInfo bean = datas.get(i);
            info.cardStatus = bean.cardStatus;
            info.configValue = bean.configValue;
            info.sequence = bean.sequence;
            info.identifier = bean.identifier;
            pamars.bindCardList.add(info);
        }
        iview.showLoadings();
        disposables.add(EcardBiz.ecrdBind(pamars).subscribe(new Consumer<Object>() {
            @Override
            public void accept(Object o) throws Exception {
                EventBusUtils.postEcardListAdded();
                iview.dismissLoadings();
                iview.bindALl(o);
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
