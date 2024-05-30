package com.alibaba.fastjson;

import com.alibaba.fastjson.util.JsonUtil;
import com.alibaba.fastjson.util.StringUtil;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.List;
import java.util.Map;

/**
 * @author LiYu
 * @ClassName JSON
 * @Description json
 * @createTime 2024年05月30日 11:38:00
 */
public class JSON {

    /**
     * Object转jsonString
     *
     * @param jsonString Object
     * @return String
     */
    public static String toJSONString(Object jsonString) {
        try {
            return new ObjectMapper().setSerializationInclusion(JsonInclude.Include.NON_NULL).writeValueAsString(jsonString);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * jsonString转Object
     *
     * @param jsonString jsonString
     * @return Object
     */
    public static Object parse(String jsonString) {
        jsonString = StringUtil.singleQuotesToDoubleQuotes(jsonString);
        if (JsonUtil.isJSONObject(jsonString)) {
            return parseObject(jsonString);
        }
        if (JsonUtil.isJSONArray(jsonString)) {
            return parseArray(jsonString);
        }
        try {
            return new ObjectMapper().readValue(jsonString, JsonNode.class);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * jsonString转JSONObject
     *
     * @param jsonString jsonString
     * @return JSONObject
     */
    public static JSONObject parseObject(String jsonString) {
        jsonString = StringUtil.singleQuotesToDoubleQuotes(jsonString);
        final ObjectMapper objectMapper = new ObjectMapper();
        try {
            final Map map = objectMapper.readValue(jsonString, Map.class);
            return JsonUtil.mapToJsonObject(map);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    /**
     * jsonString转Object
     *
     * @param jsonString jsonString
     * @param clazz      clazz
     * @param <T>        T
     * @return Object
     */
    public static <T> T parseObject(String jsonString, Class<T> clazz) {
        jsonString = StringUtil.singleQuotesToDoubleQuotes(jsonString);
        final ObjectMapper objectMapper = new ObjectMapper();
        try {
            return objectMapper.readValue(jsonString, clazz);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * jsonString转JSONArray
     *
     * @param jsonString jsonString
     * @return JSONArray
     */
    public static JSONArray parseArray(String jsonString) {
        jsonString = StringUtil.singleQuotesToDoubleQuotes(jsonString);
        final ObjectMapper objectMapper = new ObjectMapper();
        try {
            final List list = objectMapper.readValue(jsonString, List.class);
            return JsonUtil.listConvertToJsonArray(list);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * jsonString转List
     *
     * @param jsonString jsonString
     * @param clazz      clazz
     * @param <T>        T
     * @return List
     */
    public static <T> List<T> parseArray(String jsonString, Class<T> clazz) {
        final ObjectMapper objectMapper = new ObjectMapper();
        try {
            JavaType javaType = objectMapper.getTypeFactory().constructParametricType(List.class, clazz);
            return objectMapper.readValue(jsonString, javaType);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 是否为JSON字符串，首尾都为大括号或中括号判定为JSON字符串
     *
     * @param str 字符串
     * @return 是否为JSON字符串
     */
    public static boolean isJson(String str) {
        return isJsonObj(str) || isJsonArray(str);
    }

    /**
     * 是否为JSONObject字符串，首尾都为大括号或中括号判定为JSON字符串
     *
     * @param str 字符串
     * @return 是否为JSON字符串
     */
    public static boolean isJsonObj(String str) {

        if (StringUtil.isEmpty(str)) {
            return false;
        }
        return isWrap(str.trim(), '{', '}');
    }

    /**
     * 是否为JSONObject字符串，首尾都为大括号或中括号判定为JSON字符串
     *
     * @param str 字符串
     * @return 是否为JSON字符串
     */
    public static boolean isJsonArray(String str) {
        if (StringUtil.isEmpty(str)) {
            return false;
        }
        return isWrap(str.trim(), '[', ']');
    }

    /**
     * 是否为JSONObject字符串，首尾都为大括号或中括号判定为JSON字符串
     *
     * @param str   字符串
     * @param start 开始字符
     * @param end   结束字符
     * @return 是否为JSON字符串
     */
    private static boolean isWrap(String str, char start, char end) {
        if (StringUtil.isEmpty(str)) {
            return false;
        }
        return str.charAt(0) == start && str.charAt(str.length() - 1) == end;
    }

    /**
     * 获取JSONObject对应类型的集合
     *
     * @param jsonString json字符串
     * @param clazz      类型
     * @param <T>        泛型
     */
    public static <T> List<T> getTypeArray(String jsonString, Class<T> clazz) {
        final ObjectMapper objectMapper = new ObjectMapper();
        try {
            JavaType javaType = objectMapper.getTypeFactory().constructParametricType(List.class, clazz);
            return objectMapper.readValue(jsonString, javaType);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


}
