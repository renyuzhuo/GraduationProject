package com.renyuzhuo.chat.util;

/**
 * 字符串匹配工具类
 * Created by RENYUZHUO on 2016/3/4.
 */
public class TextUtils {

    public static boolean isMobilNumber(String phoneNumber) {
        if (phoneNumber != null && phoneNumber.length() == 11) {
            return true;
        }
        return false;
    }
}