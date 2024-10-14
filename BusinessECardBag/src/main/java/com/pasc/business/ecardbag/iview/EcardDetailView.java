package com.pasc.business.ecardbag.iview;

import com.pasc.lib.ecardbag.net.resq.EcardDetailResq;
/**
 *  功能：卡证详情view
 *
 *  @author zoujianbo
 *  email : ZOUJIANBO345@pingan.com.cn
 *  date : 2020/01/09
 */
public interface EcardDetailView extends IBaseView {
    /**
     * 获取详情
     * **/
    void getDetial(EcardDetailResq resq);
}
