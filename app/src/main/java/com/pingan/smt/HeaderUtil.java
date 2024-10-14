package com.pingan.smt;

import android.content.Context;
import android.content.pm.PackageManager;

import com.pasc.lib.base.AppProxy;

import java.util.HashMap;
import java.util.Map;

/**
 * Copyright (C) 2016 pasc Licensed under the Apache License, Version 2.0 (the "License");
 *
 * @author yangzijian
 * date 2018/7/16
 * des 公共头信息
 * modify
 **/
public class HeaderUtil {

    public static Map<String, String> getHeaders(boolean isDebug,Map<String, String> headers) {
        Map<String, String> commonHeaders = new HashMap<>();


        // 渠道号
        commonHeaders.put ("CHID",getAppMetaData (AppProxy.getInstance ().getContext (),"NT_CHANNEL","product"));
        /***后台版本***/
        commonHeaders.put("x-api-version", "1.2.0");
        if (headers!=null){
            commonHeaders.putAll (headers);
        }
        commonHeaders.put("Content-Type", "application/json");

        return commonHeaders;

    }
    //不同的类型要区别获取，以下是布尔类型的
    public static String getAppMetaData(Context context, String metaName, String defaultValue) {
        try {
            //application标签下用getApplicationinfo，如果是activity下的用getActivityInfo
            String value = context.getPackageManager()
                    .getApplicationInfo(context.getPackageName(), PackageManager.GET_META_DATA)
                    .metaData.getString (metaName, defaultValue);
            return value;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return defaultValue;
        }
    }
}
