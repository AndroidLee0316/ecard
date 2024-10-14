package com.pasc.business.ecardbag.presenter;

import com.pasc.business.ecardbag.iview.EcardSortView;
import com.pasc.lib.ecardbag.EcardDataManager;
import com.pasc.lib.ecardbag.net.EcardBiz;
import com.pasc.lib.ecardbag.net.pamars.SortPamars;
import com.pasc.lib.ecardbag.net.resq.EcardInfoResq;
import com.pasc.lib.net.ApiV2Error;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.functions.Consumer;

/**
 * 功能：卡证排序
 *
 * @author zoujianbo
 * email : ZOUJIANBO345@pingan.com.cn
 * date : 2020/01/09
 */
public class EcardSortPresenter implements IPresenter {
    private EcardSortView iview;

    public EcardSortPresenter(EcardSortView iview) {
        this.iview = iview;
    }

    /**
     * 卡证排序
     **/
    public void sort(List<EcardInfoResq.EcardInfoBean> datas) {
        iview.showLoadings();
        SortPamars pamars = new SortPamars();
        pamars.identifierList = new ArrayList<>();
        for (EcardInfoResq.EcardInfoBean bean : datas) {
            SortPamars.SortBean sortBean = new SortPamars.SortBean(bean.identifier);
            pamars.identifierList.add(sortBean);

        }
        disposables.add(EcardBiz.ecrdSort(pamars).subscribe(new Consumer<Object>() {
            @Override
            public void accept(Object o) throws Exception {

                EcardDataManager.getInstance().updataEcardListCacheFromSorted(datas);
                iview.dismissLoadings();
                iview.onSort(o);
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
