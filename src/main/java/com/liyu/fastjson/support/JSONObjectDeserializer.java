package com.liyu.fastjson.support;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.liyu.fastjson.JSONObject;

import java.io.IOException;

/**
 * Jackson 自定义反序列化器，将 JSON 对象直接反序列化为 {@link JSONObject}。
 */
final class JSONObjectDeserializer extends JsonDeserializer<JSONObject> {

    /**
     * {@inheritDoc}
     */
    @Override
    public JSONObject deserialize(JsonParser parser, DeserializationContext context) throws IOException {
        JsonNode node = parser.getCodec().readTree(parser);
        return JsonNodeConverter.toJSONObject(node);
    }
}
