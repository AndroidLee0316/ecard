package com.pasc.lib.ecardbag.net.resq;

import com.google.gson.annotations.SerializedName;

import java.util.List;
/**
 * 功能：关联卡证
 * <p>
 * @author zoujianbo
 * email : ZOUJIANBO345@pingan.com.cn
 * date : 2020/01/09
 */
public class EcardRelationResq {
    @SerializedName("relationList")
    public List<EcardRelationInfo> relationList;

    public static class EcardRelationInfo {
        /**
         * 证照标识符
         **/
        @SerializedName("select")
        public boolean select = true;
        /**
         * 证照标识符
         **/
        @SerializedName("identifier")
        public String identifier;
        /**
         * 证照标识符
         **/
        @SerializedName("cardStatus")
        public int cardStatus;
        /**
         * 排序号
         **/
        @SerializedName("sequence")
        public int sequence;
        /**
         * 证照标识符
         **/
        @SerializedName("configValue")
        public String configValue;
        /**
         * 卡类型名（例：住房公积金卡）
         **/
        @SerializedName("name")
        public String name;
        /**
         * 部门名称
         **/
        @SerializedName("deptName")
        public String deptName;
        @SerializedName("bgimgUrl")
        public BgUrlBean bgimgUrl;
    }

}
