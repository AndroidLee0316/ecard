package com.pasc.lib.ecardbag.out;

/**
 * 功能：电子卡证配置类
 * <p>
 * @author lichangbao702
 * email : lichangbao702@pingan.com.cn
 * date : 2019/12/31
 */
public class PascEcardConfig {

    /**
     * 默认的 给外部提供卡证列表view最大显示个数
     */
    private static final int DEFAULT_OUT_CARD_VIEW_LIST_MAX_SIZE = 3;

    /**
     * 给外部提供卡证列表view最大显示个数
     */
    private int outCardViewListMaxSize = DEFAULT_OUT_CARD_VIEW_LIST_MAX_SIZE;

    /**
     * 人脸核验所需的appID
     */
    private String faceCheckAppID;

    public PascEcardConfig() {
    }

    /**
     * 获取给外部提供卡证列表view最大显示个数
     * @return
     */
    public int getOutCardViewListMaxSize() {
        return outCardViewListMaxSize;
    }

    /**
     * 设置给外部提供卡证列表view最大显示个数
     * @param outCardViewListMaxSize    最大此案时个数
     */
    public void setOutCardViewListMaxSize(int outCardViewListMaxSize) {
        this.outCardViewListMaxSize = outCardViewListMaxSize;
    }

    /**
     * 获取人脸核验所需APPID
     * @return
     */
    public String getFaceCheckAppID() {
        return faceCheckAppID;
    }

    /**
     * 设置人脸核验所需APPID
     * @param faceCheckAppID
     */
    public void setFaceCheckAppID(String faceCheckAppID) {
        this.faceCheckAppID = faceCheckAppID;
    }
}
