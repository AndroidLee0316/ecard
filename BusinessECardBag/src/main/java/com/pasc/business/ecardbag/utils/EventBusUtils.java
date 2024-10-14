package com.pasc.business.ecardbag.utils;

import android.util.Log;

import org.greenrobot.eventbus.EventBus;

/**
 * 功能：eventBus工具类
 * <p>
 * @author lichangbao702
 * email : lichangbao702@pingan.com.cn
 * date : 2020/1/6
 */
public class EventBusUtils {

    /**证照列表已更新的通知*/
    public static final String EVENT_ECARD_LIST_UPDATED = "event_ecard_list_updated";

    /**证照列表已更新的通知*/
    public static final String EVENT_ECARD_LIST_SORTED = "event_ecard_list_sorted";

    /**添加某个证照/批量证照列表的通知*/
    public static final String EVENT_ECARD_LIST_ADDED = "event_ecard_list_added";

    /**解绑了某个证照的通知*/
    public static final String EVENT_ECARD_LIST_UNBIND = "event_ecard_list_unbind";

    /**
     * 通知添加某个证照/批量证照列
     */
    public static void postEcardListAdded(){
        EventBus.getDefault().post(EVENT_ECARD_LIST_ADDED);
    }
    /**
     * 通知更新了证照列表排序
     */
    public static void postEcardListSorted(){
        EventBus.getDefault().post(EVENT_ECARD_LIST_SORTED);
    }
    /**
     * 通知删除/解绑了某个证照
     */
    public static void postEcardListUnbind(){
        EventBus.getDefault().post(EVENT_ECARD_LIST_UNBIND);
    }
    /**
     * 通知更新了证照列表
     */
    public static void postEcardListUpdated(){
        EventBus.getDefault().post(EVENT_ECARD_LIST_UPDATED);
    }

}
