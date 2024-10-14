package com.pasc.business.ecardbag.iview;

import com.pasc.lib.ecardbag.net.resq.EcardInfoResq;

import java.util.List;


/**
 *  功能：卡证列表view
 *
 *  @author zoujianbo
 *  email : ZOUJIANBO345@pingan.com.cn
 *  date : 2020/01/09
 */
public interface EcardListView extends IBaseView{

    /**
     * 获取卡证列表
     * **/
    void onEcardListSuccess(boolean isBtnClick, List<EcardInfoResq.EcardInfoBean> resq);


    /**
     * 获取失败
     * **/
    void onEcardListError(boolean isBtnClick, int code, String error);


}
