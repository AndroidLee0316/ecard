package com.pasc.business.ecardbag.utils;

import android.text.TextUtils;

/**
 * 功能：
 * <p>
 *
 * @author zoujianbo345
 * email : ZOUJIANBO345@pingan.com.cn
 * date : 2020-01-11
 */
public class StringUtils {
    public static final int STR_LENGTH_START = 1;
    public static final int STR_LENGTH_MIN = 2;
    public static final String TAG_RESULT = "*";

    /***
     * 字符串脱敏处理
     * **/
    public static String getString(String tag) {
        String result = "";
        if (TextUtils.isEmpty(tag)) {
            return result;
        } else if (tag.length() == STR_LENGTH_START) {
            return TAG_RESULT;
        } else if (tag.length() == STR_LENGTH_MIN) {
            return TAG_RESULT + tag.substring(STR_LENGTH_START, tag.length());
        } else {
            for (int i = 0; i < (tag.length() - STR_LENGTH_MIN); i++) {
                result = result + "*";
            }
        }
        if (result.length() > 18) {
            result = result.substring(0, 18);
            return tag.substring(0, STR_LENGTH_START) + result + tag.substring(tag.length() - 1,
                    tag.length());
        } else {
            return tag.substring(0, STR_LENGTH_START) + result + tag.substring(result.length() + STR_LENGTH_START,
                    tag.length());
        }
    }

    public static String getNameString(String tag) {
        String result = "";
        if (TextUtils.isEmpty(tag)) {
        } else if (tag.length() == STR_LENGTH_START) {
            return TAG_RESULT;
        } else if (tag.length() == STR_LENGTH_MIN) {
            return TAG_RESULT + tag.substring(0, STR_LENGTH_START);
        } else {
            for (int i = 0; i < (tag.length() - STR_LENGTH_MIN); i++) {
                result = result + "*";
            }

        }
        return tag.substring(0, STR_LENGTH_START) + result + tag.substring(result.length() - STR_LENGTH_START,
                tag.length());
    }

    /**
     * 处理文本，将文本位数限制为maxLen，中文两个字符，英文一个字符
     */
    public static String handleText(String content, int maxLen) {
        if (TextUtils.isEmpty(content)) {
            return content;
        }
        int count = 0;
        int endIndex = 0;
        for (int i = 0; i < content.length(); i++) {
            char item = content.charAt(i);
            if (item < 128) {
                count = count + 1;
            } else {
                count = count + 2;
            }
            if (maxLen == count || (item >= 128 && maxLen + 1 == count)) {
                endIndex = i;
            }
        }
        if (count <= maxLen) {
            return content;
        } else {
            return content.substring(0, endIndex + 1);
        }
    }
}
