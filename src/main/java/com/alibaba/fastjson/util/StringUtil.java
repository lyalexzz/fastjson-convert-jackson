package com.alibaba.fastjson.util;

/**
 * @author LiYu
 * @ClassName StringUtil.java
 * @Description 字符串工具类
 * @createTime 2024年05月30日 15:32:00
 */
public class StringUtil {
    /**
     * 判断字符串是否为空
     *
     * @param str 字符串
     * @return boolean
     */
    public static boolean isEmpty(String str) {
        return str == null || str.isEmpty() || "null".equals(str) || "NULL".equals(str);
    }

    /**
     * 单引号转双引号
     * @param str 字符串
     * @return String
     */
    public static String singleQuotesToDoubleQuotes(String str) {
        return str.replace("'", "\"");
    }
}
