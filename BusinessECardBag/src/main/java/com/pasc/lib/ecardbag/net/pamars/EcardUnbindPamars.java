package com.pasc.lib.ecardbag.net.pamars;

import com.google.gson.annotations.SerializedName;
/**
 * 功能：解绑参数
 * <p>
 * @author zoujianbo
 * email : ZOUJIANBO345@pingan.com.cn
 * date : 2020/01/09
 */
public class EcardUnbindPamars {
    @SerializedName("identifier")
    public String identifier;
    /**
     * 票据
     */
    @SerializedName("credential")
    public String credential;
}
