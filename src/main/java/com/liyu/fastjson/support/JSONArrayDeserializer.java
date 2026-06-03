package com.liyu.fastjson.support;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.liyu.fastjson.JSONArray;

import java.io.IOException;

/**
 * Jackson 自定义反序列化器，将 JSON 数组直接反序列化为 {@link JSONArray}。
 */
final class JSONArrayDeserializer extends JsonDeserializer<JSONArray> {

    /**
     * {@inheritDoc}
     */
    @Override
    public JSONArray deserialize(JsonParser parser, DeserializationContext context) throws IOException {
        JsonNode node = parser.getCodec().readTree(parser);
        return JsonNodeConverter.toJSONArray(node);
    }
}
