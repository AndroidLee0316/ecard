package com.pasc.lib.ecardbag;

import android.util.Log;

import com.pasc.business.ecardbag.utils.EventBusUtils;
import com.pasc.lib.base.util.NetworkUtils;
import com.pasc.lib.ecardbag.net.EcardBiz;
import com.pasc.lib.ecardbag.net.resq.EcardInfoResq;
import com.pasc.lib.ecardbag.net.resq.UnLoginEcardInfoResq;
import com.pasc.lib.net.ApiV2Error;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

/**
 * 电子卡证数据管理器
 */
public class EcardDataManager {

    /**
     * 获取未登陆情况下获取卡证配置列表的disposable
     */
    private Disposable getUnLoginEcardListDisposable;

    /**
     * 获取登陆情况下卡证列表的disposable
     */
    private Disposable getEcardListDisposable;
    /**
     * 电子卡证列表数据
     */
    private List<EcardInfoResq.EcardInfoBean> ecardList;

    /**
     * 未登陆的情况下获取的默认的电子卡证列表数据
     */
    private List<UnLoginEcardInfoResq.UnLoginEcardInfoBean> unLoginEcardList;

    /**
     * 获取卡证列表监听回调集合
     * 当用户未登陆进到卡证介绍页面后，点击开通卡证会调用登陆，但是可能有多处监听登陆回调，
     * 导致可能多处调用获取证照列表接口，使用这个 tmpGetEcardListCallBackList 可以解决
     * 多次调用接口的问题，当发现证照获取时，把callback加入到列表就行，无需重复获取
     */
    private List<GetEcardListCallBack> tmpGetEcardListCallBackList;

    /**
     * 未登陆获取卡证列表监听回调集合
     * 原理同 {@link com.pasc.lib.ecardbag.EcardDataManager#unLoginEcardList}
     */
    private List<GetUnLoginEcardListCallBack> tmpUnLoginGetEcardListCallBackList;


    private EcardDataManager() {
        tmpGetEcardListCallBackList = new ArrayList<>();
        tmpUnLoginGetEcardListCallBackList = new ArrayList<>();
    }

    public static EcardDataManager getInstance() {
        return SingletonHolder.instance;
    }

    private static class SingletonHolder {
        private static final EcardDataManager instance = new EcardDataManager();
    }

    /**
     * 是否有缓存的卡证列表数据
     *
     * @return
     */
    public boolean hasEcardListCached() {

        if (ecardList == null) {
            return false;
        }
        return true;
    }

    /**
     * 刷新卡证列表缓存，列表重新排序
     *
     * @param datas
     */
    public void updataEcardListCacheFromSorted(List<EcardInfoResq.EcardInfoBean> datas) {
        ecardList = new ArrayList<>();
        ecardList.addAll(datas);
        EventBusUtils.postEcardListSorted();
    }

    /**
     * 刷新卡证列表缓存，删除了某个卡证
     *
     * @param datas
     */
    public void updataEcardListCacheFromUnbind(List<EcardInfoResq.EcardInfoBean> datas) {
        ecardList = new ArrayList<>();
        ecardList.addAll(datas);
        EventBusUtils.postEcardListUnbind();
    }

    /**
     * 获取卡证列表
     * 首先获取缓存的卡证列表，如果没有获取到，则从服务器获取
     *
     * @param callBack
     */
    public void getEcardList(GetEcardListCallBack callBack) {
        if (ecardList == null) {
            getEcardListFromNet(0, callBack);
        } else {
            if (callBack != null) {
                callBack.onSuccess(ecardList);
            }
        }
    }

    /**
     * 获取缓存的卡证列表
     *
     * @return
     */
    public List<EcardInfoResq.EcardInfoBean> getEcardListFromCache() {
        return ecardList;
    }


    /**
     * 从服务器获取卡证列表<br>
     * 有两种情况：未登陆获取默认的未登陆卡证列表，用户已登陆或者用户的卡证列表
     *
     * @param delayMilliSeconds 延时时间
     * @param callBack          获取的数据回调
     */
    public void getEcardListFromNet(int delayMilliSeconds, GetEcardListCallBack callBack) {

        tmpGetEcardListCallBackList.add(callBack);
        if (getEcardListDisposable != null && !getEcardListDisposable.isDisposed()) {
            return;
        }

        getEcardListDisposable = EcardBiz.ecrdList()
                .delay(delayMilliSeconds, TimeUnit.MILLISECONDS)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<EcardInfoResq>() {
                    @Override
                    public void accept(EcardInfoResq ecardInfoResq) throws Exception {
                        //用户的卡证列表
                        ecardList = ecardInfoResq.bindCardList;
                        for (GetEcardListCallBack tmpCalBack : tmpGetEcardListCallBackList) {
                            tmpCalBack.onSuccess(ecardList);
                        }
                        tmpGetEcardListCallBackList.clear();

                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        ecardList = null;

                        for (GetEcardListCallBack tmpCalBack : tmpGetEcardListCallBackList) {
                            if (tmpCalBack != null) {
                                if (NetworkUtils.isNetworkAvailable()) {
                                    if (throwable instanceof ApiV2Error) {
                                        tmpCalBack.onFailed(GetEcardListCallBack.ERROR_SERVICE_ERROR, throwable.getMessage());
                                    } else {
                                        tmpCalBack.onFailed(GetEcardListCallBack.ERROR_NET_ERROR, throwable.getMessage());
                                    }
                                } else {
                                    tmpCalBack.onFailed(GetEcardListCallBack.ERROR_SERVICE_ERROR, throwable.getMessage());
                                }
                            }
                        }

                        tmpGetEcardListCallBackList.clear();

                    }
                });
    }


    /**
     * 获取未登陆的情况下的默认显示的配置卡证列表
     *
     * @param callBack 获取的数据回调
     */
    public void getUnLoginEcardList(GetUnLoginEcardListCallBack callBack) {

        if (unLoginEcardList == null || unLoginEcardList.size() == 0) {

            tmpUnLoginGetEcardListCallBackList.add(callBack);
            if (getUnLoginEcardListDisposable != null && !getUnLoginEcardListDisposable.isDisposed()) {
                return;
            }

            getUnLoginEcardListDisposable = EcardBiz.configList()
                    .subscribeOn(Schedulers.newThread())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Consumer<UnLoginEcardInfoResq>() {
                        @Override
                        public void accept(UnLoginEcardInfoResq ecardInfoResq) throws Exception {
                            //用户的卡证列表
                            unLoginEcardList = ecardInfoResq.configList;

                            for (GetUnLoginEcardListCallBack tmpCalBack : tmpUnLoginGetEcardListCallBackList) {
                                tmpCalBack.onSuccess(ecardInfoResq.configList);
                            }
                            tmpUnLoginGetEcardListCallBackList.clear();

                        }
                    }, new Consumer<Throwable>() {
                        @Override
                        public void accept(Throwable throwable) throws Exception {
                            unLoginEcardList = null;

                            for (GetUnLoginEcardListCallBack tmpCalBack : tmpUnLoginGetEcardListCallBackList) {
                                if (tmpCalBack != null) {
                                    if (NetworkUtils.isNetworkAvailable()) {
                                        tmpCalBack.onFailed(GetEcardListCallBack.ERROR_NET_ERROR, throwable.getMessage());
                                    } else {
                                        tmpCalBack.onFailed(GetEcardListCallBack.ERROR_SERVICE_ERROR, throwable.getMessage());
                                    }
                                }
                            }

                            tmpUnLoginGetEcardListCallBackList.clear();


                        }
                    });
        } else {
            if (callBack != null) {
                callBack.onSuccess(unLoginEcardList);
            }
        }
    }

    /**
     * 清除登陆情况下获取卡证列表的disposable
     */
    public void disposableGetEcardList() {
        if (getEcardListDisposable != null && !getEcardListDisposable.isDisposed()) {
            getEcardListDisposable.dispose();
        }
        getEcardListDisposable = null;
    }

    /**
     * 清除未登陆情况下获取卡证列表的disposable
     */
    public void diapoableUnLoginGetEcardList() {
        if (getUnLoginEcardListDisposable != null && !getUnLoginEcardListDisposable.isDisposed()) {
            getUnLoginEcardListDisposable.dispose();
        }
        getUnLoginEcardListDisposable = null;
    }

    /**
     * 清除数据，包括登陆获取对卡证列表以及默认配置列表
     */
    public void clearDatas() {
        ecardList = null;
        unLoginEcardList = null;
    }

    ;

    /**
     * 获取卡证列表数据的回调
     */
    public interface GetEcardListCallBack {

        public int ERROR_NET_ERROR = 1;

        public int ERROR_SERVICE_ERROR = 2;

        /**
         * 获取卡证列表成功
         *
         * @param bindCardList 获取到的卡证列表数据
         */
        void onSuccess(List<EcardInfoResq.EcardInfoBean> bindCardList);

        /**
         * 获取卡证列表失败
         *
         * @param eCode 错误code
         * @param eMsg  错误信息
         */
        void onFailed(int eCode, String eMsg);

    }


    /**
     * 未登陆情况下获取卡证列表数据的回调
     */
    public interface GetUnLoginEcardListCallBack {

        /**
         * 获取未登陆情况下的卡证配置列表成功
         *
         * @param bindCardList 卡证配置列表
         */
        void onSuccess(List<UnLoginEcardInfoResq.UnLoginEcardInfoBean> bindCardList);

        /**
         * 获取配置卡证列表失败
         *
         * @param eCode 错误code
         * @param eMsg  错误信息
         */
        void onFailed(int eCode, String eMsg);

    }

}
