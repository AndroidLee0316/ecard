package com.pasc.lib.ecardbag.net.resq;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

import javax.crypto.SealedObject;

/**
 * 功能：卡证详情
 * <p>
 *
 * @author zoujianbo
 * email : ZOUJIANBO345@pingan.com.cn
 * date : 2020/01/09
 */
public class EcardDetailResq implements Serializable {
    public static final int STATUE_NOMARL = 1;
    public static final int STATUE_UNENABLE = 0;
    public static final int STATUE_UNKNOW = -1;
    @SerializedName("cardStatus")
    public int cardStatus = STATUE_UNKNOW;//默认为-1表示状态未知，不能使用0失效
    @SerializedName("ecardMetaDataVOList")
    public List<EcardDetailInfo> ecardMetaDataVOList;
    @SerializedName("bgimgUrl")
    public BgUrlBean bgimgUrl;

    public static class EcardDetailInfo implements Serializable {
        @SerializedName("name")
        public String name;
        @SerializedName("value")
        public String value;

    }

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

}
