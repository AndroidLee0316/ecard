package com.pasc.business.ecardbag.presenter;

import com.pasc.business.ecardbag.iview.EcardAddView;
import com.pasc.lib.base.util.ToastUtils;
import com.pasc.lib.ecardbag.net.EcardBiz;
import com.pasc.lib.ecardbag.net.pamars.EcardBindPamars;
import com.pasc.lib.ecardbag.net.pamars.SortPamars;
import com.pasc.lib.ecardbag.net.resq.AddListResq;
import com.pasc.lib.ecardbag.net.resq.EcardDetailResq;
import com.pasc.lib.ecardbag.net.resq.EcardRelationResq;
import com.pasc.lib.net.ApiV2Error;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.functions.Consumer;

/**
 * 功能：添加卡证
 * <p>
 *
 * @author zoujianbo
 * email : ZOUJIANBO345@pingan.com.cn
 * date : 2020/01/09
 */
public class EcardAddPresenter implements IPresenter {
    private EcardAddView iview;

    public EcardAddPresenter(EcardAddView iview) {
        this.iview = iview;
    }

    /**
     * 获取未绑定列表
     **/
    public void addList() {
        iview.showLoadings();
        disposables.add(EcardBiz.ecrdAddList().subscribe(new Consumer<AddListResq>() {
            @Override
            public void accept(AddListResq resq) throws Exception {
                iview.dismissLoadings();
                iview.addList(resq);

            }
        }, new Consumer<Throwable>() {
            @Override
            public void accept(Throwable throwable) throws Exception {
                iview.dismissLoadings();
                if (throwable instanceof ApiV2Error) {
                    ApiV2Error apiV2Error = (ApiV2Error) throwable;
                    iview.onListError(apiV2Error.getCode(), apiV2Error.getMsg());
                } else {
                    iview.onListError("","");

                }
            }
        }));

    }

    /**
     * 提交选择的卡证关联
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
                iview.dismissLoadings();
                iview.bindALl(o);
//                statusView.showContent();
//                EventBusUtils.postEcardListUpdated();
//                finish();
            }
        }, new Consumer<Throwable>() {
            @Override
            public void accept(Throwable throwable) throws Exception {
                iview.dismissLoadings();
                if (throwable instanceof ApiV2Error) {
                    ApiV2Error apiV2Error = (ApiV2Error) throwable;
                    iview.onDialogError(apiV2Error.getCode(), apiV2Error.getMsg());
                } else {
                    iview.showServiceError(throwable.getMessage());

                }
            }
        }));
    }

    /**
     * 获取卡证详情
     **/
    public void getDetial(String identifier, int pos) {
        iview.showLoadings();
        SortPamars.SortBean parmars = new SortPamars.SortBean(identifier);
        disposables.add(EcardBiz.ecrdDetail(parmars).subscribe(new Consumer<EcardDetailResq>() {
            @Override
            public void accept(EcardDetailResq resq) throws Exception {
                iview.dismissLoadings();
                iview.getDetial(resq, pos);

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
