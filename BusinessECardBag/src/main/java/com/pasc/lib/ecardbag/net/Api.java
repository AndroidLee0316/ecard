package com.pasc.lib.ecardbag.net;

import com.pasc.lib.ecardbag.net.pamars.EcardBindPamars;
import com.pasc.lib.ecardbag.net.pamars.EcardUnbindPamars;
import com.pasc.lib.ecardbag.net.pamars.SortPamars;
import com.pasc.lib.ecardbag.net.resq.AddListResq;
import com.pasc.lib.ecardbag.net.resq.EcardDetailResq;
import com.pasc.lib.ecardbag.net.resq.EcardInfoResq;
import com.pasc.lib.ecardbag.net.resq.EcardRelationResq;
import com.pasc.lib.ecardbag.net.resq.UnLoginEcardInfoResq;
import com.pasc.lib.net.resp.BaseV2Resp;

import java.util.List;

import io.reactivex.Single;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.POST;

/**
 * 功能：api
 * <p>
 * @author zoujianbo
 * email : ZOUJIANBO345@pingan.com.cn
 * date : 2020/01/09
 */
public interface Api {
    /**
     * 卡证关联
     * **/
    @POST(UrlMannager.URL_ECARD_AUTHORIZE)
    Single<BaseV2Resp<Object>> ecardBind(@Header("token") String token, @Body EcardBindPamars pamars);

    /**
     * 获取关联列表
     * **/
    @POST(UrlMannager.URL_ECARD_RELATIONLIST)
    Single<BaseV2Resp<EcardRelationResq>> ecardRelation(@Header("token") String token);
    /**
     * 已绑定的卡证列表
     * **/
    @POST(UrlMannager.URL_ECARD_LIST_MAIN)
    Single<BaseV2Resp<EcardInfoResq>> ecardList(@Header("token") String token);
    /**
     * 解绑
     * **/
    @POST(UrlMannager.URL_ECARD_UNBIND)
    Single<BaseV2Resp<Object>> ecardUnbind(@Header("token") String token, @Body EcardUnbindPamars pamars);
    /**
     * 排序
     * **/
    @POST(UrlMannager.URL_ECARD_SORT)
    Single<BaseV2Resp<Object>> ecardSort(@Header("token") String token, @Body SortPamars pamars);
    /**
     * 详情
     * **/
    @POST(UrlMannager.URL_ECARD_DETAIL)
    Single<BaseV2Resp<EcardDetailResq>> ecardDetial(@Header("token") String token, @Body SortPamars.SortBean pamars);
    /**
     * 添加卡证
     * **/
    @POST(UrlMannager.URL_ECARD_ADD_LIST)
    Single<BaseV2Resp<AddListResq>> ecardAddList(@Header("token") String token);
    /**
     * 移除
     * **/
    @POST(UrlMannager.URL_ECARD_LIST_DELECT)
    Single<BaseV2Resp<Object>> ecardDelect(@Header("token") String token,@Body SortPamars.SortBean pamars);
    /**
     * 未登陆查询运营后台配置的证照列表
     * **/
    @POST(UrlMannager.URL_ECARD_CONFIG_LIST)
    Single<BaseV2Resp<UnLoginEcardInfoResq>> configList();

}
