package com.pasc.business.ecardbag.iview;

import com.pasc.lib.ecardbag.net.resq.EcardRelationResq;
/**
 *  功能：关联列表view
 *
 *  @author zoujianbo
 *  email : ZOUJIANBO345@pingan.com.cn
 *  date : 2020/01/09
 */
public interface EcardRelevancyView extends IBaseView{
    /**
     * 获取关联列表
     * **/
    void getEcardRelation(EcardRelationResq relationResq);
    /**
     * 绑定
     * **/
    void bindALl(Object o);
    /**
     * 获取失败
     * **/
    void onListError(String code, String error);
}
