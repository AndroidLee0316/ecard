package com.pasc.business.ecardbag.utils;

/**
 * 文件描述：电子卡证路由信息 + 路由参数信息
 * 作者：zhangqiurui188
 * 创建时间：2019/8/7
 * 更改时间：2019/8/7
 */
public class ArouterPath {
    /**
     * 电子证照列表
     */
    public final static String ECARD_LIST_MAIN = "/ecard/list/main";
    /**
     * 电子证照列表排序
     */
    public final static String ECARD_LIST_SORT = "/ecard/list/sort";
    /**
     * 电子证照列表解绑
     */
    public final static String ECARD_LIST_UNBIND = "/ecard/info/unbind";

    /**
     * 电子证照列表添加
     */
    public final static String ECARD_LIST_ADD = "/ecard/list/add";
    /**
     * 电子证照添加详情
     */
    public final static String ECARD_INFO_ADD = "/ecard/info/add";
    /**
     *
     * 电子证照详情
     */
    public final static String ECARD_INFO_MAIN = "/ecard/info/main";

    /**
     * 电子证照关联列表
     */
    public final static String ECARD_LIST_RELEVANCY = "/ecard/list/relevancy";


    /**
     * 进入电子证照列表页面，需要显示的默认选中显示全部信息的卡片ID
     */
    public final static String ECARD_LIST_MAIN_PARAM_SHOW_ECARD_ID = "showEcardID";

}
