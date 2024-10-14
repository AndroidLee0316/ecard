package com.pasc.lib.ecardbag.net.resq;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * 功能：背景图片
 * <p>
 * @author zoujianbo
 * email : ZOUJIANBO345@pingan.com.cn
 * date : 2020/01/09
 */
public class BgUrlBean implements Serializable {
    /**
     * 卡包列表证照背景图
     **/
    @SerializedName("p1")
    public String p1;
    /**
     * 一键绑定页证照背景图
     **/
    @SerializedName("p2")
    public String p2;
    /**
     * 添加/解绑/排序页证照图标
     **/
    @SerializedName("p3")
    public String p3;
    /**
     * 证照绑定页证照背景图
     **/
    @SerializedName("p4")
    public String p4;
    /**
     * 个人中心证照背景图
     **/
    @SerializedName("p5")
    public String p5;

}
