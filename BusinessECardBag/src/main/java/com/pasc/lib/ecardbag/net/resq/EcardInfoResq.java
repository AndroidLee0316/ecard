package com.pasc.lib.ecardbag.net.resq;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

/**
 * 服务器返回的卡证列表数据
 */
public class EcardInfoResq implements Serializable {
    /**
     * 卡证状态：可用
     */
    public static final int STATUS_USEABLE = 1;
    /**
     * 卡证状态：不可用
     */
    public static final int STATUS_UN_USEABLE = 0;

    /**
     * 属性状态是否显示：显示
     */
    public static final String IS_SHOW_SHOW = "1";

    /**
     * 属性状态是否显示：不显示
     */
    public static final String IS_SHOW_UNSHOW = "0";

    /**
     * 卡证列表
     */
    @SerializedName("bindCardList")
    public List<EcardInfoBean> bindCardList;

    public static class EcardInfoBean implements Serializable{

        /**
         * 卡证号码是否可见
         **/
        @SerializedName("isVisible")
        public String isVisible;
        /**
         * 证照id
         **/
        @SerializedName("id")
        public String id;
        /**
         * 用户姓名
         **/
        @SerializedName("userName")
        public String userName;
        /**
         * 配置value（照面号码）
         **/

        @SerializedName("configValue")
        public String configValue;
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
         * 排序）
         **/
        @SerializedName("sequence")
        public int sequence;
        /**
         * 证照状态 1可用，0不可以
         **/
        @SerializedName("cardStatus")
        public int cardStatus;
        /**
         * 显示详情入口
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
         * 服务列表
         **/
        @SerializedName("applicationVOList")
        public List<ApplicationInfo> applicationVOList;

    }

    public static class ApplicationInfo  implements Serializable {
        /**
         * 跳h5
         */
        public static final String PAGE_TYPE_H5 = "1";
        /**
         * 不跳转
         */
        public static final String PAGE_TYPE_NO = "0";

        /**
         *跳原生
         */
        public static final String PAGE_TYPE_NATIVE = "2";

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
