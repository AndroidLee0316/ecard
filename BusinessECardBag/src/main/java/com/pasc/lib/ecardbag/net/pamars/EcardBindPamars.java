package com.pasc.lib.ecardbag.net.pamars;

import com.google.gson.annotations.SerializedName;
import com.pasc.lib.ecardbag.net.resq.EcardRelationResq;

import java.util.List;

/**
 * 功能：绑定参数
 * <p>
 *
 * @author zoujianbo
 * email : ZOUJIANBO345@pingan.com.cn
 * date : 2020/01/09
 */
public class EcardBindPamars {
    //    @SerializedName("identifier")
//    public String identifier;
//    @SerializedName("cardStatus")
//    public String cardStatus;
//    @SerializedName("sequence")
//    public String sequence;
//    @SerializedName("configValue")
//    public String configValue;
    @SerializedName("bindCardList")
    public List<EcardBindPamarsInfo> bindCardList;

    public static class EcardBindPamarsInfo {
        @SerializedName("identifier")
        public String identifier;
        @SerializedName("cardStatus")
        public int cardStatus;
        @SerializedName("sequence")
        public int sequence;
        @SerializedName("configValue")
        public String configValue;
    }


}
