package com.alibaba.fastjson.util;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author LiYu
 * @ClassName JsonUtil.java
 * @Description json工具类
 * @createTime 2024年05月30日 11:46:00
 */
public class JsonUtil {

    /**
     * 判断是否为JSONObject格式
     *
     * @param jsonStr json字符串
     * @return boolean
     */
    public static boolean isJSONObject(String jsonStr) {
        return !isEmpty(jsonStr) && jsonStr.startsWith("{") && jsonStr.endsWith("}");
    }

    /**
     * 判断是否为JSONArray格式
     *
     * @param jsonStr json字符串
     * @return boolean
     */
    public static boolean isJSONArray(String jsonStr) {
        return !isEmpty(jsonStr) && jsonStr.startsWith("[{") && jsonStr.endsWith("}]");
    }

    /**
     * 判断是否为json
     *
     * @param jsonStr json字符串
     * @return boolean
     */
    public static boolean isJson(String jsonStr) {
        return isJSONObject(jsonStr) || isJSONArray(jsonStr);
    }

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
     * map转jsonObject
     *
     * @param map map
     * @return JSONObject
     */
    public static JSONObject mapToJsonObject(Map<String, ?> map) {
        JSONObject jsonObject = new JSONObject();
        map.forEach((key, value) -> jsonObject.put(key, resolveObjectToJson(value)));
        return jsonObject;
    }

    /**
     * 解析对象为json
     *
     * @param value 对象
     * @return Object
     */
    private static Object resolveObjectToJson(Object value) {
        if (value instanceof Map) {
            return mapToJsonObject((Map<String, ?>) value);
        } else if (value instanceof List) {
            return listToJsonArray((List<?>) value);
        } else {
            return value;
        }
    }

    /**
     * list转jsonArray
     *
     * @param list list
     * @return JSONArray
     */
    private static JSONArray listToJsonArray(List<?> list) {
        JSONArray jsonArray = new JSONArray();
        list.forEach(item -> jsonArray.add(resolveObjectToJson(item)));
        return jsonArray;
    }

    /**
     * list转jsonArray
     *
     * @param list list
     * @return JSONArray
     */
    public static JSONArray listConvertToJsonArray(List list) {
        List<Object> jsonObjects = new ArrayList<>(list.size());
        for (Object obj : list) {
            jsonObjects.add(mapToJsonObject((Map<String, Object>) obj));
        }
        return new JSONArray(jsonObjects);
    }
}
