package com.pasc.lib.ecardbag.net;

import com.pasc.lib.base.AppProxy;
import com.pasc.lib.ecardbag.net.pamars.EcardBindPamars;
import com.pasc.lib.ecardbag.net.pamars.EcardUnbindPamars;
import com.pasc.lib.ecardbag.net.pamars.SortPamars;
import com.pasc.lib.ecardbag.net.resq.AddListResq;
import com.pasc.lib.ecardbag.net.resq.EcardDetailResq;
import com.pasc.lib.ecardbag.net.resq.EcardInfoResq;
import com.pasc.lib.ecardbag.net.resq.EcardRelationResq;
import com.pasc.lib.ecardbag.net.resq.UnLoginEcardInfoResq;
import com.pasc.lib.net.ApiGenerator;
import com.pasc.lib.net.transform.RespV2Transformer;

import java.util.List;

import io.reactivex.Single;
import io.reactivex.schedulers.Schedulers;

/**
 * 功能：网络处理
 * <p>
 * @author zoujianbo
 * email : ZOUJIANBO345@pingan.com.cn
 * date : 2020/01/09
 */
public class EcardBiz {
    /**
     * 获取关联列表
     */
    public static Single<EcardRelationResq> getEcardRelation() {

        RespV2Transformer<EcardRelationResq> respTransformer = RespV2Transformer.newInstance();
        return ApiGenerator.createApi(Api.class)
                .ecardRelation(AppProxy.getInstance().getUserManager().getToken())
                .compose(respTransformer)
                .subscribeOn(Schedulers.io());
    }

    /**
     * 绑定卡证
     */
    public static Single<Object> ecrdBind(EcardBindPamars pamars) {

        RespV2Transformer<Object> respTransformer = RespV2Transformer.newInstance();
        return ApiGenerator.createApi(Api.class)
                .ecardBind(AppProxy.getInstance().getUserManager().getToken(), pamars)
                .compose(respTransformer)
                .subscribeOn(Schedulers.io());
    }

    /**
     * 获取已绑定列表
     */
    public static Single<EcardInfoResq> ecrdList() {

        RespV2Transformer<EcardInfoResq> respTransformer = RespV2Transformer.newInstance();
        return ApiGenerator.createApi(Api.class)
                .ecardList(AppProxy.getInstance().getUserManager().getToken())
                .compose(respTransformer)
                .subscribeOn(Schedulers.io());
    }

    /**
     * 解绑卡证
     */
    public static Single<Object> ecrdUnbind(EcardUnbindPamars pamars) {

        RespV2Transformer<Object> respTransformer = RespV2Transformer.newInstance();
        return ApiGenerator.createApi(Api.class)
                .ecardUnbind(AppProxy.getInstance().getUserManager().getToken(), pamars)
                .compose(respTransformer)
                .subscribeOn(Schedulers.io());
    }

    /**
     * 排序
     */
    public static Single<Object> ecrdSort(SortPamars pamars) {

        RespV2Transformer<Object> respTransformer = RespV2Transformer.newInstance();
        return ApiGenerator.createApi(Api.class)
                .ecardSort(AppProxy.getInstance().getUserManager().getToken(), pamars)
                .compose(respTransformer)
                .subscribeOn(Schedulers.io());
    }

    /**
     * 详情
     */
    public static Single<EcardDetailResq> ecrdDetail(SortPamars.SortBean pamars) {

        RespV2Transformer<EcardDetailResq> respTransformer = RespV2Transformer.newInstance();
        return ApiGenerator.createApi(Api.class)
                .ecardDetial(AppProxy.getInstance().getUserManager().getToken(), pamars)
                .compose(respTransformer)
                .subscribeOn(Schedulers.io());
    }

    /**
     * 为加添列表
     */
    public static Single<AddListResq> ecrdAddList() {

        RespV2Transformer<AddListResq> respTransformer = RespV2Transformer.newInstance();
        return ApiGenerator.createApi(Api.class)
                .ecardAddList(AppProxy.getInstance().getUserManager().getToken())
                .compose(respTransformer)
                .subscribeOn(Schedulers.io());
    }

    /**
     * 移除
     */
    public static Single<Object> ecrdDelect(String identifier) {
        SortPamars.SortBean parmars = new SortPamars.SortBean(identifier);

        RespV2Transformer<Object> respTransformer = RespV2Transformer.newInstance();
        return ApiGenerator.createApi(Api.class)
                .ecardDelect(AppProxy.getInstance().getUserManager().getToken(),parmars)
                .compose(respTransformer)
                .subscribeOn(Schedulers.io());
    }


    /**
     * 未登陆查询运营后台配置的证照列表
     */
    public static Single<UnLoginEcardInfoResq> configList() {

        RespV2Transformer<UnLoginEcardInfoResq> respTransformer = RespV2Transformer.newInstance();
        return ApiGenerator.createApi(Api.class)
                .configList()
                .compose(respTransformer)
                .subscribeOn(Schedulers.io());
    }

}
