package com.pasc.lib.ecardbag.out;

import java.util.HashMap;

/**
 * 功能：电子证照对外实体类
 * <p>
 * @author lichangbao702
 * email : lichangbao702@pingan.com.cn
 * date : 2019/12/31
 */
public class EcardOutInfo {

    /**
     * 卡片ID
     */
    private String ecardID;

    /**
     * 卡片名称
     */
    private String ecardName;

    /**
     * 卡片描述
     */
    private String ecardDesc;

    /**
     * 卡片图标
     */
    private String ecardIconUrl;

    /**
     * 卡片背景
     */
    private String ecardBgUrl;

    /**
     * 卡片状态
     */
    private int ecardStatus;

    /**
     * 卡片扩展字段
     * 因为对外的接口不方便频繁修改，所以设置一个扩展字段方便后续适配新增功能
     */
    private HashMap<String,String> extra;


    public EcardOutInfo() {
    }

    /**
     * 获取卡证ID
     * @return
     */
    public String getEcardID() {
        return ecardID;
    }

    public void setEcardID(String ecardID) {
        this.ecardID = ecardID;
    }

    /**
     * 获取卡证名称
     * @return
     */
    public String getEcardName() {
        return ecardName;
    }

    public void setEcardName(String ecardName) {
        this.ecardName = ecardName;
    }

    /**
     * 获取卡证描述
     * @return
     */
    public String getEcardDesc() {
        return ecardDesc;
    }

    public void setEcardDesc(String ecardDesc) {
        this.ecardDesc = ecardDesc;
    }

    /**
     * 获取卡证图标URL
     * @return
     */
    public String getEcardIconUrl() {
        return ecardIconUrl;
    }

    public void setEcardIconUrl(String ecardIconUrl) {
        this.ecardIconUrl = ecardIconUrl;
    }

    /**
     * 获取卡证背景图片URL
     * @return
     */
    public String getEcardBgUrl() {
        return ecardBgUrl;
    }

    public void setEcardBgUrl(String ecardBgUrl) {
        this.ecardBgUrl = ecardBgUrl;
    }


    public int getEcardStatus() {
        return ecardStatus;
    }

    public void setEcardStatus(int ecardStatus) {
        this.ecardStatus = ecardStatus;
    }

    /**
     * 获取扩展字段
     * @return
     */
    public HashMap<String, String> getExtra() {
        return extra;
    }

    public void setExtra(HashMap<String, String> extra) {
        this.extra = extra;
    }
}
