package com.pasc.lib.ecardbag.net.resq;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

/**
 * 服务器返回的未登陆卡证列表数据
 */
public class UnLoginEcardInfoResq implements Serializable {

    /**
     * 卡证列表
     */
    @SerializedName("configList")
    public List<UnLoginEcardInfoBean> configList;

    public static class UnLoginEcardInfoBean {

        /**
         * 证照配置id
         **/
        @SerializedName("configId")
        public String configId;
        /**
         * 证照标识符（例：gjj）
         **/
        @SerializedName("identifier")
        public String identifier;
        /**
         * 证照名称
         **/
        @SerializedName("name")
        public String name;
        /**
         * 部门名称
         **/
        @SerializedName("deptName")
        public String deptName;

        /**
         * 详情入口配置，默认展示详情入口 0:不展示 1:展示
         **/
        @SerializedName("isShowData")
        public String isShowData;

        /**
         * 显示二维码
         **/
        @SerializedName("isShowQrcode")
        public String isShowQrcode;
        /**
         * 背景图
         **/
        @SerializedName("bgimgUrl")
        public BgUrlBean bgimgUrl;

        /**
         * 照面号码(传入元数据的key)
         **/
        @SerializedName("cardShow")
        public String cardShow;

        /**
         * 服务列表
         **/
        @SerializedName("applications")
        public List<ApplicationInfo> applications;

    }

    public static class ApplicationInfo {
        @SerializedName("name")
        public String name;
        @SerializedName("iconUrl")
        public String iconUrl;
        @SerializedName("applicationUrl")
        public String applicationUrl;
        @SerializedName("isProto")
        public String isProto;
    }
}
