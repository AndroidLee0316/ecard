package com.pasc.lib.ecardbag.net.resq;

import com.google.gson.annotations.SerializedName;

import java.util.List;
/**
 * 功能：添加返回
 * <p>
 * @author zoujianbo
 * email : ZOUJIANBO345@pingan.com.cn
 * date : 2020/01/09
 */
public class AddListResq {
    @SerializedName("notBindList")
    public List<AddListBean> notBindList;

    public static class AddListBean {
        @SerializedName("name")
        public String name;
        @SerializedName("identifier")
        public String identifier;
        @SerializedName("bgimgUrl")
        public BgUrlBean bgimgUrl;
    }
}
