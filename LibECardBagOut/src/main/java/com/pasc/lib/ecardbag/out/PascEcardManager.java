package com.pasc.lib.ecardbag.out;

import android.content.Context;
import android.util.Log;
import android.view.ViewGroup;

/**
 * 功能：电子卡证统一对外的管理类
 * <p>
 * @author lichangbao702
 * email : lichangbao702@pingan.com.cn
 * date : 2019/12/31
 */
public class PascEcardManager implements EcardManagerInter {


    /**
     * 卡证管理类实现类
     */
    private EcardManagerInter ecardManagerImpl;
    /**
     * 卡证管理配置类
     */
    private PascEcardConfig config;


    static class Singleton{
        public static PascEcardManager pascEcardManager = new PascEcardManager();
    }

    private PascEcardManager(){

    }

    public static EcardManagerInter getInstance(){
        return Singleton.pascEcardManager;
    }

    /**
     * 初始化
     * @param ecardManagerInter 实现类的初始化
     * @param cf    配置类
     */
    @Override
    public void init(EcardManagerInter ecardManagerInter, PascEcardConfig cf) {
        ecardManagerImpl = ecardManagerInter;
        if (cf == null){
            this.config = new PascEcardConfig();
        }else {
            this.config = cf;
        }

        ecardManagerImpl.init(ecardManagerImpl,config);
    }

    @Override
    public PascEcardConfig getConfig() {
        return config;
    }

    /**
     * 跳转到电子证照列表页面
     * @param showEcardID 需要默认显示的卡片ID
     * @param listener  电子卡证数据更新监听回调
     */
    @Override
    public void toMyEcardList(String showEcardID, EcardUpdateListener listener) {
        if (ecardManagerImpl == null){
            Log.e(getClass().getSimpleName(),"PascEcardManager not init");
        }else {
            ecardManagerImpl.toMyEcardList(showEcardID, listener);
        }
    }

    /**
     * 跳转到电子证照授权页面
     * @param listener  电子证照授权监听
     */
    @Override
    public void toEcardAuth(EcardAuthListener listener) {
        if (ecardManagerImpl == null){
            Log.e(getClass().getSimpleName(),"PascEcardManager not init");
        }else {
            ecardManagerImpl.toEcardAuth(listener);
        }
    }

    /**
     * 获取电子证照列表
     * @param callBack  结果回调
     */
    @Override
    public void getEcardList(GetEcardListCallBack callBack) {
        if (ecardManagerImpl == null){
            Log.e(getClass().getSimpleName(),"PascEcardManager not init");
            return ;
        }
        ecardManagerImpl.getEcardList(callBack);
    }

    /**
     *
     * 给传入的ViewGroup添加证照列表
     * @param context   上下文
     * @param containerVG   容器
     * @param type  证照列表view类型
     * @param listener  添加电子卡证view状态监听
     */
    @Override
    public void addEcardViewList(Context context, ViewGroup containerVG, ADD_ECARD_VIEW_LIST_TYPE type, EcardAddViewListener listener) {
        if (ecardManagerImpl == null){
            Log.e(getClass().getSimpleName(),"PascEcardManager not init");
            return;
        }
        ecardManagerImpl.addEcardViewList(context,containerVG,type,listener);
    }

    /**
     * 注册证照更新监听
     * @param key   证照更新监听的key
     * @param listener  证照列表更新监听
     */
    @Override
    public void registerEcardUpdateListener(String key, EcardUpdateListener listener) {
        if (ecardManagerImpl == null){
            Log.e(getClass().getSimpleName(),"PascEcardManager not init");
            return ;
        }
        ecardManagerImpl.registerEcardUpdateListener(key, listener);
    }

    /**
     * 移除证照更新监听
     * @param key   证照更新监听的key
     */
    @Override
    public void removeEcardUpdateListener(String key) {
        if (ecardManagerImpl == null){
            Log.e(getClass().getSimpleName(),"PascEcardManager not init");
            return ;
        }
        ecardManagerImpl.removeEcardUpdateListener(key);
    }

    @Override
    public void removeAllEcardUpdateListener() {
        if (ecardManagerImpl == null){
            Log.e(getClass().getSimpleName(),"PascEcardManager not init");
            return ;
        }
        ecardManagerImpl.removeAllEcardUpdateListener();
    }

    @Override
    public void registerNormalCallback(NormalCallBack normalCallBack) {
        if (ecardManagerImpl == null){
            Log.e(getClass().getSimpleName(),"PascEcardManager not init");
            return ;
        }
        ecardManagerImpl.registerNormalCallback(normalCallBack);
    }

    @Override
    public NormalCallBack getNormalCallback() {
        if (ecardManagerImpl == null){
            Log.e(getClass().getSimpleName(),"PascEcardManager not init");
            return null;
        }
        return ecardManagerImpl.getNormalCallback();
    }

    @Override
    public void clearDatas() {
        if (ecardManagerImpl == null){
            Log.e(getClass().getSimpleName(),"PascEcardManager not init");
            return ;
        }
        ecardManagerImpl.clearDatas();
    }

    /**
     * 移除rxjava的关联
     */
    @Override
    public void clearDisposes() {
        if (ecardManagerImpl == null){
            Log.e(getClass().getSimpleName(),"PascEcardManager not init");
            return ;
        }
        ecardManagerImpl.clearDisposes();
    }

    /**
     * 注销组件
     */
    @Override
    public void onDestroy() {
        if (ecardManagerImpl == null){
            Log.e(getClass().getSimpleName(),"PascEcardManager not init");
            return ;
        }
        ecardManagerImpl.onDestroy();
    }
}
