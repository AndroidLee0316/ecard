package com.pasc.lib.ecardbag.net.pamars;

import com.google.gson.annotations.SerializedName;

import java.util.List;
/**
 * 功能：排序参数
 * <p>
 * @author zoujianbo
 * email : ZOUJIANBO345@pingan.com.cn
 * date : 2020/01/09
 */
public class SortPamars {

    @SerializedName("identifierList")
    public List<SortBean> identifierList;

    public static class SortBean {
        @SerializedName("identifier")
        public String identifier;

        public SortBean(String identifier) {
            this.identifier = identifier;
        }

    }
}
