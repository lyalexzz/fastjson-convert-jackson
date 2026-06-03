package com.liyu.fastjson.support;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.liyu.fastjson.JSONArray;
import com.liyu.fastjson.JSONObject;
import com.liyu.fastjson.parser.Feature;
import com.liyu.fastjson.serializer.SerializerFeature;

import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.EnumSet;
import java.util.TimeZone;

/**
 * Jackson {@link ObjectMapper} 内部封装，为 fastjson 公开 API 提供统一的读写能力。
 * <p>
 * 本类位于 {@code support} 包，属于内部实现，业务代码请勿直接依赖。
 * </p>
 */
public final class JacksonSupport {

    private static final ObjectMapper DEFAULT_MAPPER = createDefaultMapper();

    private JacksonSupport() {
    }

    /**
     * 返回全局共享的默认 {@link ObjectMapper} 实例。
     *
     * @return 默认 ObjectMapper
     */
    public static ObjectMapper defaultMapper() {
        return DEFAULT_MAPPER;
    }

    /**
     * 复制默认 Mapper 并应用解析特性，用于单次解析调用。
     *
     * @param features 解析特性
     * @return 配置了特性的 ObjectMapper 副本
     */
    public static ObjectMapper copyMapper(Feature... features) {
        ObjectMapper mapper = DEFAULT_MAPPER.copy();
        applyParserFeatures(mapper, features);
        return mapper;
    }

    /**
     * 获取配置了序列化特性的 {@link ObjectWriter}。
     *
     * @param features 序列化特性
     * @return ObjectWriter
     */
    public static ObjectWriter writer(SerializerFeature... features) {
        return createWriter(DEFAULT_MAPPER, features);
    }

    /**
     * 基于指定 Mapper 获取配置了序列化特性的 {@link ObjectWriter}。
     *
     * @param mapper   基础 ObjectMapper
     * @param features 序列化特性
     * @return ObjectWriter
     */
    public static ObjectWriter writer(ObjectMapper mapper, SerializerFeature... features) {
        return createWriter(mapper, features);
    }

    private static ObjectWriter createWriter(ObjectMapper baseMapper, SerializerFeature... features) {
        if (features == null || features.length == 0) {
            return baseMapper.writer();
        }
        EnumSet<SerializerFeature> set = EnumSet.noneOf(SerializerFeature.class);
        for (SerializerFeature feature : features) {
            set.add(feature);
        }

        ObjectMapper mapper = baseMapper;
        boolean needMapperCopy = set.contains(SerializerFeature.WriteMapNullValue)
                || set.contains(SerializerFeature.SortField)
                || set.contains(SerializerFeature.MapSortField);
        if (needMapperCopy) {
            mapper = baseMapper.copy();
            if (set.contains(SerializerFeature.WriteMapNullValue)) {
                mapper.setSerializationInclusion(JsonInclude.Include.ALWAYS);
            }
            if (set.contains(SerializerFeature.SortField) || set.contains(SerializerFeature.MapSortField)) {
                mapper.configure(MapperFeature.SORT_PROPERTIES_ALPHABETICALLY, true);
            }
        }

        ObjectWriter writer = mapper.writer();

        if (set.contains(SerializerFeature.PrettyFormat)) {
            writer = writer.with(SerializationFeature.INDENT_OUTPUT);
        }
        if (set.contains(SerializerFeature.WriteDateUseDateFormat)) {
            writer = writer.without(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        }
        if (set.contains(SerializerFeature.UseSingleQuotes)) {
            writer = writer.without(com.fasterxml.jackson.core.JsonGenerator.Feature.QUOTE_FIELD_NAMES);
        }
        return writer;
    }

    private static ObjectMapper createDefaultMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        mapper.registerModule(createContainerModule());
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        mapper.disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);
        mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        mapper.enable(DeserializationFeature.USE_BIG_DECIMAL_FOR_FLOATS);
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        mapper.setAnnotationIntrospector(new FastjsonAnnotationIntrospector());
        mapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"));
        mapper.setTimeZone(TimeZone.getDefault());
        return mapper;
    }

    private static SimpleModule createContainerModule() {
        SimpleModule module = new SimpleModule("FastjsonContainerModule");
        module.addDeserializer(JSONObject.class, new JSONObjectDeserializer());
        module.addDeserializer(JSONArray.class, new JSONArrayDeserializer());
        return module;
    }

    /**
     * 将 fastjson {@link Feature} 映射为 Jackson 解析配置。
     *
     * @param mapper   目标 ObjectMapper
     * @param features 解析特性
     */
    public static void applyParserFeatures(ObjectMapper mapper, Feature... features) {
        if (features == null || features.length == 0) {
            return;
        }
        EnumSet<Feature> set = EnumSet.noneOf(Feature.class);
        Collections.addAll(set, features);
        mapper.configure(JsonParser.Feature.ALLOW_COMMENTS, set.contains(Feature.AllowComment));
        mapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, set.contains(Feature.AllowUnQuotedFieldNames));
        mapper.configure(JsonParser.Feature.ALLOW_SINGLE_QUOTES, set.contains(Feature.AllowSingleQuotes));
        mapper.configure(JsonParser.Feature.ALLOW_TRAILING_COMMA, set.contains(Feature.AllowArbitraryCommas));
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, !set.contains(Feature.IgnoreNotMatch));
        mapper.configure(MapperFeature.SORT_PROPERTIES_ALPHABETICALLY, set.contains(Feature.OrderedField));
    }

    /**
     * 将 Jackson {@link JsonNode} 转换为 Java 基本类型或 fastjson 容器类型。
     *
     * @param node JsonNode，可为 {@code null}
     * @return Java 值；{@code node} 为 {@code null} 或 null 节点时返回 {@code null}
     */
    public static Object toJavaValue(JsonNode node) {
        if (node == null || node.isNull()) {
            return null;
        }
        if (node.isObject()) {
            return JsonNodeConverter.toJSONObject(node);
        }
        if (node.isArray()) {
            return JsonNodeConverter.toJSONArray(node);
        }
        if (node.isBoolean()) {
            return node.booleanValue();
        }
        if (node.isIntegralNumber()) {
            if (node.canConvertToLong() && !node.canConvertToInt()) {
                return node.longValue();
            }
            return node.intValue();
        }
        if (node.isFloatingPointNumber()) {
            return node.decimalValue();
        }
        return node.asText();
    }
}
