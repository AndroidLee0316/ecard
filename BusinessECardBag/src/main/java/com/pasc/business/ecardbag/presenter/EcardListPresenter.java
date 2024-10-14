package com.pasc.business.ecardbag.presenter;

import android.util.Log;

import com.pasc.business.ecardbag.iview.EcardListView;
import com.pasc.business.user.PascUserManager;
import com.pasc.lib.ecardbag.EcardDataManager;
import com.pasc.lib.ecardbag.net.resq.EcardInfoResq;

import java.util.List;

public class EcardListPresenter implements IPresenter {
    private EcardListView iview;

    public EcardListPresenter(EcardListView iview) {
        this.iview = iview;
    }

    /**
     * 从服务器拉取最新的证照列表数据
     * @param isBtnClick    是否是点击开通卡证调用的该函数
     */
    public void getEcardListFromNet(boolean isBtnClick) {

        if (PascUserManager.getInstance().isLogin() && PascUserManager.getInstance().isCertification()){

            iview.showLoadings();

            EcardDataManager.getInstance().getEcardListFromNet(500, new EcardDataManager.GetEcardListCallBack() {
                @Override
                public void onSuccess(List<EcardInfoResq.EcardInfoBean> bindCardList) {
                    iview.dismissLoadings();
                    iview.onEcardListSuccess(isBtnClick, bindCardList);
                }

                @Override
                public void onFailed(int code, String msg) {
                    iview.dismissLoadings();
                    iview.onEcardListError(isBtnClick,code, msg);
                }
            });

        }else {
            Log.i(getClass().getSimpleName(),"unlogin or uncert, show ecard desc");
            //未登陆或者未认证显示证照介绍页面
            iview.onEcardListSuccess(isBtnClick,null);
        }

    }

    @Override
    public void onDestroy() {
        disposables.clear();
        EcardDataManager.getInstance().disposableGetEcardList();
    }
}
