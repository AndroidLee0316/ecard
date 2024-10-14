package com.pasc.business.ecardbag.iview;

/**
 * 文件描述：
 * 作者：zoujianbo345
 * 创建时间：2019/10/29
 * 更改时间：2019/10/29
 */
public interface IBaseView {
    /**
     * 获取失败
     **/
    void onError(String code, String error);

    /**
     * 显示加载中
     **/
    void showLoadings();

    /**
     * 取消加载
     **/

    void dismissLoadings();

    void showServiceError(String error);
}
