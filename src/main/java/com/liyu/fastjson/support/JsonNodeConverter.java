package com.liyu.fastjson.support;

import com.fasterxml.jackson.databind.JsonNode;
import com.liyu.fastjson.JSONArray;
import com.liyu.fastjson.JSONObject;

import java.util.Iterator;
import java.util.Map;

/**
 * Jackson {@link JsonNode} 与 fastjson 容器类型（{@link JSONObject}/{@link JSONArray}）的互转工具。
 */
public final class JsonNodeConverter {

    private JsonNodeConverter() {
    }

    /**
     * 将 JSON 对象节点转换为 {@link JSONObject}。
     *
     * @param node JSON 对象节点；非对象节点时返回空 JSONObject
     * @return JSONObject
     */
    public static JSONObject toJSONObject(JsonNode node) {
        JSONObject object = new JSONObject();
        if (node == null || !node.isObject()) {
            return object;
        }
        Iterator<Map.Entry<String, JsonNode>> fields = node.fields();
        while (fields.hasNext()) {
            Map.Entry<String, JsonNode> entry = fields.next();
            object.put(entry.getKey(), JacksonSupport.toJavaValue(entry.getValue()));
        }
        return object;
    }

    /**
     * 将 JSON 数组节点转换为 {@link JSONArray}。
     *
     * @param node JSON 数组节点；非数组节点时返回空 JSONArray
     * @return JSONArray
     */
    public static JSONArray toJSONArray(JsonNode node) {
        JSONArray array = new JSONArray();
        if (node == null || !node.isArray()) {
            return array;
        }
        for (JsonNode item : node) {
            array.add(JacksonSupport.toJavaValue(item));
        }
        return array;
    }
}
