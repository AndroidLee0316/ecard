package com.pasc.lib.ecardbag.out;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

/**
 * 功能：电子证照统一对外接口
 * <p>
 * @author lichangbao702
 * email : lichangbao702@pingan.com.cn
 * date : 2019/12/31
 */
public interface EcardManagerInter {

    /**
     * 支持的卡证列表View类型,根据迭代慢慢添加支持的类型
     */
    public enum ADD_ECARD_VIEW_LIST_TYPE{
        /**
         * 简单的卡证列表view，文字左上角（横向排列+（卡证名称、描述、背景））
         */
        TYPE_SIMPLE_TOP_VIEW,
        /**
         * 简单的卡证列表view，文字居中（横向排列+（卡证名称、描述、背景））
         */
        TYPE_SIMPLE_CENTER_VIEW
    }

    /**
     * 电子证照更新监听
     */
    interface EcardUpdateListener{
        /**
         * 电子证照更新了
         */
        void onUpdate();
    }

    /**
     * 卡证授权监听
     */
    interface EcardAuthListener{
        /**
         * 授权成功
         */
        void onSuccess();

        /**
         * 授权失败
         */
        void onFailed();

        /**
         * 取消授权
         */
        void onCancel();
    }

    /**
     * 卡证给某个viewgroup添加卡证列表的回调
     */
    interface EcardAddViewListener{
        /**
         * 添加成功
         */
        void onScucess();

        /**
         * 添加失败
         * @param code  错误码
         * @param msg   错误信息
         */
        void onFailed(int code, String msg);

        boolean interruptItemClick(EcardOutInfo ecardOutInfo);
    }


    /**
     * 获取卡证列表数据的回调，供外部使用
     */
    public interface GetEcardListCallBack{

        /**
         * 错误码：
         * 未登陆情况下获取的卡证配置列表为空
         */
        public int ERROR_CODE_UN_LOGIN_CARD_LIST_EMPTY = -2;

        /**
         * 获取电子卡证列表成功
         * @param bindCardList  电子卡证列表
         */
        void onSuccess(List<EcardOutInfo> bindCardList);

        /**
         * 获取电子卡证列表失败
         * @param eCode 错误code
         * @param eMsg  错误码
         */
        void onFailed(int eCode, String eMsg);

    }

    /**
     * 通用的回调，组件无法或者不方便处理的东西都可以通过接口的形式曝露出去让外部实现
     */
    public interface NormalCallBack{
        /**
         * 路由点击，这个路由是跟IOS同时自定义的通用的路由，组件不方便写死跳转，让外部来实现
         */
        public void routerClick(String router);
    }

    /**
     * 初始化
     * @param ecardManagerInter 卡证管理实现类
     * @param config    卡证配置
     */
    void init(EcardManagerInter ecardManagerInter, PascEcardConfig config);

    /**
     * 获取设置的卡证配置
     * @return
     */
    PascEcardConfig getConfig();

    /**
     * 跳转到电子证照列表页面
     * @param showEcardID 需要默认显示的卡片ID
     * @param listener  电子卡证数据更新监听回调
     */
    void toMyEcardList(String showEcardID, EcardUpdateListener listener);

    /**
     * 跳转到电子证照授权页面
     * @param listener  电子证照授权监听
     */
    void toEcardAuth(EcardAuthListener listener);

    /**
     * 获取电子证照列表
     * @param callBack  结果回调
     */
    void getEcardList( GetEcardListCallBack callBack);

    /**
     *
     * 给传入的ViewGroup添加证照列表，注意退出的时候需要调用removeAllEcardUpdateListener删除监听列表
     * @param context   上下文
     * @param containerVG   容器
     * @param type  证照列表类型
     * @param listener  添加电子卡证view状态监听
     */
     void addEcardViewList(Context context, ViewGroup containerVG, ADD_ECARD_VIEW_LIST_TYPE type, EcardAddViewListener listener);

    /**
     * 注册证照更新监听
     * @param key   证照更新监听的key
     * @param listener  证照列表更新监听
     */
    void registerEcardUpdateListener(String key, EcardUpdateListener listener);

    /**
     * 移除证照更新监听
     * @param key   证照更新监听的key
     */
    void removeEcardUpdateListener(String key);

    /**
     * 删除所有的证照更新监听
     */
    void removeAllEcardUpdateListener();


    /**
     * 注册通用的对外曝露需要外部实现的回调接口
     * @param normalCallBack
     */
    void registerNormalCallback(NormalCallBack normalCallBack);

    NormalCallBack getNormalCallback();

    /**
     * 清除所有对卡证列表数据
     */
    void clearDatas();

    /**
     * 移除网络请求rxjava的关联，activity退出的时候可调用，防止内存泄漏
     */
    void clearDisposes();

    /**
     * 注销组件
     */
    void onDestroy();


}
