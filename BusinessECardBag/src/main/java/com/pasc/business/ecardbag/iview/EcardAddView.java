package com.pasc.business.ecardbag.iview;

import com.pasc.lib.ecardbag.net.resq.AddListResq;
import com.pasc.lib.ecardbag.net.resq.EcardDetailResq;
/**
 *  功能：添加卡证view
 *
 *  @author zoujianbo
 *  email : ZOUJIANBO345@pingan.com.cn
 *  date : 2020/01/09
 */
public interface EcardAddView extends IBaseView{
    /**
     * 绑定卡证完成
     * **/
    void bindALl(Object o);
    /**
     * 获取未绑定卡证
     * **/
    void addList(AddListResq resq);
    /**
     * 获取卡证详情
     * **/
    void getDetial(EcardDetailResq resq,int pos);
    /**
     * 失败让提示
     * **/
    void onDialogError(String code, String error);

    /**
     * 失败让提示
     * **/
    void onListError(String code, String error);

}
