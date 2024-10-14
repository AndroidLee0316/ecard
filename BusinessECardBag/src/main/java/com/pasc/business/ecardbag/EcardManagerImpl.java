package com.pasc.business.ecardbag;


import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;

import com.pasc.business.bike.R;
import com.pasc.business.ecardbag.utils.ArouterPath;
import com.pasc.business.ecardbag.utils.EventBusUtils;
import com.pasc.business.ecardbag.utils.StatisticsConstants;
import com.pasc.business.ecardbag.view.SimpleCardOutView;
import com.pasc.business.ecardbag.view.SimpleCardView;
import com.pasc.business.user.PascUserLoginListener;
import com.pasc.business.user.PascUserManager;
import com.pasc.lib.ecardbag.EcardDataManager;
import com.pasc.lib.ecardbag.net.resq.EcardInfoResq;
import com.pasc.lib.ecardbag.net.resq.UnLoginEcardInfoResq;
import com.pasc.lib.ecardbag.out.EcardOutInfo;
import com.pasc.lib.ecardbag.out.PascEcardConfig;
import com.pasc.lib.ecardbag.out.EcardManagerInter;
import com.pasc.lib.router.BaseJumper;
import com.pasc.lib.statistics.StatisticsManager;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.pasc.lib.ecardbag.out.EcardManagerInter.ADD_ECARD_VIEW_LIST_TYPE.TYPE_SIMPLE_TOP_VIEW;
import static com.pasc.lib.ecardbag.out.EcardManagerInter.GetEcardListCallBack.ERROR_CODE_UN_LOGIN_CARD_LIST_EMPTY;

/**
 * 功能：电子卡证的默认对外接口实现类，项目如果不自定义实现，需要在调用 {@link com.pasc.lib.ecardbag.out.PascEcardManager#init(EcardManagerInter, PascEcardConfig)} 初始化函数的时候 new EcardManagerImpl()传入
 *
 * @author lichangbao702
 * @email : lichangbao702@pingan.com.cn
 * @date : 2020/1/10
 */
public class EcardManagerImpl implements EcardManagerInter {

    /**
     * 长期的证照更新监听，需要手动通过调用 remove 或者 removeAll 函数移除，不然会有内存泄漏的风险
     */
    private HashMap<String, EcardUpdateListener> ecardUpdateListenerMap = new HashMap<>(4);

    /** 临时的证照更新监听，{@link com.pasc.business.ecardbag.EcardManagerImpl#toMyEcardList} 函数设置，获取到监听时会自动设置为null */
    private EcardUpdateListener tmpEcardUpdateListener;

    private NormalCallBack normalCallBack;

    private static final String faceCheckAppID = "base_ecard";
    //配置
    private PascEcardConfig mConfig = new PascEcardConfig();

    /**
     * 初始化
     * @param ecardManagerInter
     * @param config
     */
    @Override
    public void init(EcardManagerInter ecardManagerInter, PascEcardConfig config) {

        if (config != null){
            mConfig = config;
        }

        if (TextUtils.isEmpty(mConfig.getFaceCheckAppID())){
            mConfig.setFaceCheckAppID(faceCheckAppID);
        }

        if (!EventBus.getDefault().isRegistered(this)){
            EventBus.getDefault().register(this);
        }

        PascUserManager.getInstance().setLoginListener(new PascUserLoginListener() {
            @Override
            public void onLoginSuccess() {
                EventBusUtils.postEcardListUpdated();
            }

            @Override
            public void onLoginFailed() {

            }

            @Override
            public void onLoginCancled() {

            }
        });
    }

    @Override
    public PascEcardConfig getConfig() {
        return null;
    }


    /**
     * eventbus通知监听
     * @param msg
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEven(String msg) {
        if (EventBusUtils.EVENT_ECARD_LIST_UPDATED.equals(msg)) {
            if (tmpEcardUpdateListener != null){
                tmpEcardUpdateListener.onUpdate();
                tmpEcardUpdateListener = null;
            }
            if (ecardUpdateListenerMap != null && ecardUpdateListenerMap.size() > 0){
                Set<Map.Entry<String, EcardUpdateListener>> entries = ecardUpdateListenerMap.entrySet();

                for (Map.Entry<String, EcardUpdateListener> entry : entries){
                    if (entry != null){
                        entry.getValue().onUpdate();
                    }
                }

            }
        }

    }


    /**
     * 跳转到电子证照列表页面
     * @param showEcardID 需要默认显示的卡片ID
     * @param listener  电子卡证数据更新监听回调
     */
    @Override
    public void toMyEcardList(String showEcardID, EcardUpdateListener listener) {
        tmpEcardUpdateListener = listener;

        Bundle bundle = new Bundle();
        bundle.putString(ArouterPath.ECARD_LIST_MAIN_PARAM_SHOW_ECARD_ID,showEcardID);
        BaseJumper.jumpBundleARouter(ArouterPath.ECARD_LIST_MAIN,bundle);

    }


    @Override
    public void toEcardAuth(EcardAuthListener listener) {

    }


    /**
     * 获取卡片列表，如果用户登陆了，但是获取到卡片列表为空，需要展示默认到配置列表到卡片
     * @param callBack  结果回调
     */
    @Override
    public void getEcardList(GetEcardListCallBack callBack) {

        if (PascUserManager.getInstance().isLogin() && PascUserManager.getInstance().isCertification()){
            EcardDataManager.getInstance().getEcardList(new EcardDataManager.GetEcardListCallBack() {
                @Override
                public void onSuccess(List<EcardInfoResq.EcardInfoBean> bindCardList) {

                    if (bindCardList!=null && bindCardList.size() > 0){
                        exchangeEcardList(bindCardList, callBack);
                    }else {
                        //获取到列表为空，需要获取显示默认配置列表
                        EcardDataManager.getInstance().getUnLoginEcardList(new EcardDataManager.GetUnLoginEcardListCallBack() {
                            @Override
                            public void onSuccess(List<UnLoginEcardInfoResq.UnLoginEcardInfoBean> bindCardList) {
                                exchangeUnLoginEcardList(bindCardList, callBack);
                            }

                            @Override
                            public void onFailed(int eCode, String eMsg) {
                                if (callBack != null){
                                    callBack.onFailed(eCode,eMsg);
                                }
                            }
                        });
                    }

                }

                @Override
                public void onFailed(int eCode, String eMsg) {
                    if (callBack != null){
                        callBack.onFailed(eCode,eMsg);
                    }
                }
            });
        }else {
            EcardDataManager.getInstance().getUnLoginEcardList(new EcardDataManager.GetUnLoginEcardListCallBack() {
                @Override
                public void onSuccess(List<UnLoginEcardInfoResq.UnLoginEcardInfoBean> bindCardList) {
                    exchangeUnLoginEcardList(bindCardList, callBack);
                }

                @Override
                public void onFailed(int eCode, String eMsg) {
                    if (callBack != null){
                        callBack.onFailed(eCode,eMsg);
                    }
                }
            });

        }


    }


    /**
     * 登陆情况下获取的卡片列表转换
     * @param ecardInfoList 卡片列表
     * @param callBack  转换结果回调
     */
    private void exchangeEcardList(List<EcardInfoResq.EcardInfoBean> ecardInfoList, GetEcardListCallBack callBack){

        if (ecardInfoList != null){
            List<EcardOutInfo> ecardOutInfoList = new ArrayList<>();
            for (EcardInfoResq.EcardInfoBean cardMainInfo : ecardInfoList){
                EcardOutInfo ecardOutInfo = new EcardOutInfo();
                ecardOutInfo.setEcardID(cardMainInfo.id);
                ecardOutInfo.setEcardName(cardMainInfo.name);
                ecardOutInfo.setEcardDesc(cardMainInfo.deptName);
                if (cardMainInfo.bgimgUrl != null){
                    ecardOutInfo.setEcardBgUrl(cardMainInfo.bgimgUrl.p5);
                }
                ecardOutInfo.setEcardStatus(cardMainInfo.cardStatus);
                ecardOutInfoList.add(ecardOutInfo);
            }
            if (callBack != null){
                callBack.onSuccess(ecardOutInfoList);
            }
        }
    }

    /**
     * 未登陆的配置卡片数据转换
     * @param ecardInfoList 配置卡片列表
     * @param callBack  转换回调
     */
    private void exchangeUnLoginEcardList(List<UnLoginEcardInfoResq.UnLoginEcardInfoBean> ecardInfoList, GetEcardListCallBack callBack){

        if (ecardInfoList != null && ecardInfoList.size() >0){
            List<EcardOutInfo> ecardOutInfoList = new ArrayList<>();
            for (UnLoginEcardInfoResq.UnLoginEcardInfoBean cardMainInfo : ecardInfoList){
                EcardOutInfo ecardOutInfo = new EcardOutInfo();
                ecardOutInfo.setEcardName(cardMainInfo.name);
                ecardOutInfo.setEcardDesc(cardMainInfo.deptName);
                if (cardMainInfo.bgimgUrl != null){
                    ecardOutInfo.setEcardBgUrl(cardMainInfo.bgimgUrl.p5);
                }
                ecardOutInfoList.add(ecardOutInfo);
            }
            if (callBack != null){
                callBack.onSuccess(ecardOutInfoList);
            }
        }else {
            if (callBack != null){
                callBack.onFailed(ERROR_CODE_UN_LOGIN_CARD_LIST_EMPTY,"default config ecard list is empty");
            }
        }
    }

    /**
     * 给传入的ViewGroup添加证照列表，注意退出的时候需要调用removeAllEcardUpdateListener删除监听列表
     * @param context   上下文
     * @param containerVG   容器
     * @param type  证照列表类型
     * @param listener  添加电子卡证view状态监听
     */
    @Override
    public void addEcardViewList(Context context, ViewGroup containerVG, ADD_ECARD_VIEW_LIST_TYPE type, EcardAddViewListener listener) {

        getEcardList( new GetEcardListCallBack() {
            @Override
            public void onSuccess(List<EcardOutInfo> bindCardList) {
                if (bindCardList != null && bindCardList.size() > 0){
                    createEcardSimpleViewList(context, containerVG, bindCardList, type, listener);
                }else {
                    Log.e(getClass().getSimpleName(),"ecard is empty");
                    containerVG.removeAllViews();
                }
                registerEcardUpdateListener(type.name(), new EcardUpdateListener() {
                    @Override
                    public void onUpdate() {
                        addEcardViewList(context,containerVG,type,listener);
                    }
                });
                if (listener != null){
                    listener.onScucess();
                }
            }

            @Override
            public void onFailed(int eCode, String eMsg) {

                registerEcardUpdateListener(type.name(), new EcardUpdateListener() {
                    @Override
                    public void onUpdate() {
                        addEcardViewList(context,containerVG,type,listener);
                    }
                });

                if (listener != null){
                    listener.onFailed(eCode,eMsg);
                }
            }
        });

    }

    /**
     * 注册证照更新监听
     * @param key   证照更新监听的key
     * @param listener  证照列表更新监听
     */
    @Override
    public void registerEcardUpdateListener(String key, EcardUpdateListener listener) {
        ecardUpdateListenerMap.put(key,listener);
    }

    /**
     * 移除证照更新监听
     * @param key   证照更新监听的key
     */
    @Override
    public void removeEcardUpdateListener(String key) {
        ecardUpdateListenerMap.remove(key);
    }

    /**
     * 删除所有的证照更新监听
     */
    @Override
    public void removeAllEcardUpdateListener() {
        ecardUpdateListenerMap.clear();
        ecardUpdateListenerMap = new HashMap<>();
    }

    @Override
    public void registerNormalCallback(NormalCallBack normalCallBack) {
        this.normalCallBack = normalCallBack;
    }

    @Override
    public NormalCallBack getNormalCallback() {
        return normalCallBack;
    }

    @Override
    public void clearDatas() {
        EcardDataManager.getInstance().clearDatas();
    }

    /**
     * 移除rxjava的关联
     */
    @Override
    public void clearDisposes() {
        EcardDataManager.getInstance().disposableGetEcardList();
        EcardDataManager.getInstance().diapoableUnLoginGetEcardList();
    }

    /**
     * 注销组件
     */
    @Override
    public void onDestroy() {
        if (EventBus.getDefault().isRegistered(this)){
            EventBus.getDefault().unregister(this);
        }
        PascUserManager.getInstance().setLoginListener(null);
        clearDisposes();
    }

    /**
     *
     * 根据传入的卡证列表，生成简单卡证列表ViewList，放入一个 HorizontalScrollView ，再把 HorizontalScrollView 添加到ViewGroup
     * @param context   上下文
     * @param containerVG   卡证列表需要添加到的ViewGroup
     * @param ecardOutInfos  证照列表数据
     * @param type  卡证列表view类型
     */
    private void createEcardSimpleViewList(Context context, ViewGroup containerVG , List<EcardOutInfo> ecardOutInfos, ADD_ECARD_VIEW_LIST_TYPE type, EcardAddViewListener listener) {
        HorizontalScrollView horizontalScrollView = new HorizontalScrollView(context);
        LinearLayout ll = new LinearLayout(context);
        for (int i = 0; i < ecardOutInfos.size(); i++) {
            //如果超过设置的最大显示数量，则不显示后面的数据了
            if (i>=mConfig.getOutCardViewListMaxSize()){
                break;
            }

            View view = null;
            if (type == TYPE_SIMPLE_TOP_VIEW){

                SimpleCardOutView simpleCardView = new SimpleCardOutView(context,null);
                simpleCardView.setDefaultBg(R.drawable.pasc_ecard_default);
                simpleCardView.updateData(ecardOutInfos.get(i));
                simpleCardView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        StatisticsManager.getInstance().onEvent(context.getString(R.string.pasc_ecard_event_id_enter_sigle),
                                StatisticsConstants.EVENT_ID,
                                String.format(context.getString(R.string.pasc_ecard_event_lable_enter_sigle_click), simpleCardView.getEcardOutInfo().getEcardName()),
                                StatisticsConstants.PAGE_TYPE,
                                null);
                        if (listener != null && listener.interruptItemClick(simpleCardView.getEcardOutInfo())){
                            return;
                        }

                        toMyEcardList(simpleCardView.getEcardID(), null);
                    }
                });
                view = simpleCardView;

            }else {

                SimpleCardView simpleCardView = new SimpleCardView(context,null);
                simpleCardView.setDefaultBg(R.drawable.pasc_ecard_default);
                simpleCardView.setNameTextSize(context.getResources().getDimensionPixelSize(R.dimen.text_size_17));
                simpleCardView.setDescTextSize(context.getResources().getDimensionPixelSize(R.dimen.text_size_13));
                simpleCardView.updateData(ecardOutInfos.get(i));
                simpleCardView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        StatisticsManager.getInstance().onEvent(context.getString(R.string.pasc_ecard_event_id_enter_sigle),
                                StatisticsConstants.EVENT_ID,
                                String.format(context.getString(R.string.pasc_ecard_event_lable_enter_sigle_click), simpleCardView.getEcardOutInfo().getEcardName()),
                                StatisticsConstants.PAGE_TYPE,
                                null);

                        if (listener != null && listener.interruptItemClick(simpleCardView.getEcardOutInfo())){
                            return;
                        }

                        toMyEcardList(simpleCardView.getEcardID(), null);
                    }
                });
                view = simpleCardView;

            }
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            params.width = (int) context.getResources().getDimensionPixelSize(R.dimen.pasc_ecard_hsv_item_width);
            if (i != 0){
                params.leftMargin = (int) context.getResources().getDimensionPixelSize(R.dimen.pasc_ecard_hsv_item_space);
            }else {
                params.leftMargin = (int) context.getResources().getDimensionPixelSize(R.dimen.pasc_ecard_hsv_margin_left_right);
            }
            if ( i == ecardOutInfos.size() -1 || i == mConfig.getOutCardViewListMaxSize() -1 ){
                params.rightMargin = (int) context.getResources().getDimensionPixelSize(R.dimen.pasc_ecard_hsv_margin_left_right);
            }

            ll.addView(view,params);
        }
        //horizontalScrollView 只能包含一个View，所以需要套一层
        horizontalScrollView.addView(ll);
        //隐藏侧边栏
        horizontalScrollView.setHorizontalScrollBarEnabled(false);
        horizontalScrollView.setOverScrollMode(View.OVER_SCROLL_NEVER);
        //需要先清除所有的view，防止重复添加
        containerVG.removeAllViews();
        //添加
        containerVG.addView(horizontalScrollView);
    }

}
