package com.liyu.fastjson;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.liyu.fastjson.parser.Feature;
import com.liyu.fastjson.serializer.SerializerFeature;
import com.liyu.fastjson.support.JacksonSupport;
import com.liyu.fastjson.support.JsonNodeConverter;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;
import java.lang.reflect.Type;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

/**
 * Fastjson 主入口类，提供 JSON 与 Java 对象之间的相互转换。
 * <p>
 * API 设计与 {@code com.alibaba.fastjson.JSON} 保持一致，底层由 Jackson 驱动。
 * 最常用的两个方法为 {@link #toJSONString(Object)} 和 {@link #parseObject(String, Class)}。
 * </p>
 *
 * <pre>{@code
 * // 序列化
 * String json = JSON.toJSONString(user);
 *
 * // 反序列化为 JavaBean
 * User user = JSON.parseObject(json, User.class);
 *
 * // 反序列化为 JSONObject
 * JSONObject obj = JSON.parseObject(json);
 *
 * // 泛型 List
 * List<User> list = JSON.parseObject(json, new TypeReference<List<User>>() {});
 * }</pre>
 *
 * @see JSONObject
 * @see JSONArray
 * @see TypeReference
 */
public abstract class JSON implements JSONStreamAware, JSONAware {

    /**
     * 本库版本标识
     */
    public static final String VERSION = "1.0.0-jackson";

    /**
     * AutoType 类型键名（保留兼容，本库不支持 AutoType）
     */
    public static final String DEFAULT_TYPE_KEY = "@type";

    /**
     * 默认日期格式（与 fastjson 拼写保持一致）
     */
    public static final String DEFFAULT_DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";

    /**
     * 全局默认时区
     */
    public static TimeZone defaultTimeZone = TimeZone.getDefault();

    /**
     * 全局默认语言环境
     */
    public static Locale defaultLocale = Locale.getDefault();

    /**
     * 默认解析特性位掩码
     */
    public static int DEFAULT_PARSER_FEATURE = 0;

    /**
     * 默认序列化特性位掩码
     */
    public static int DEFAULT_GENERATE_FEATURE = 0;

    /**
     * 将当前对象以 JSON 字符串形式写入 {@link Writer}。
     *
     * @param out 输出目标
     * @throws IOException 写入失败时抛出
     */
    @Override
    public void writeJSONString(Writer out) throws IOException {
        out.write(toJSONString());
    }

    /**
     * 将当前对象序列化为 JSON 字符串。
     *
     * @return JSON 字符串
     */
    @Override
    public String toJSONString() {
        return JSON.toJSONString(this);
    }

    // ======================== 解析 ========================

    /**
     * 解析 JSON 文本，根据内容自动返回 {@link JSONObject}、{@link JSONArray} 或基本类型。
     *
     * @param text JSON 字符串
     * @return 解析结果；{@code text} 为 {@code null} 或空时返回 {@code null}
     * @throws JSONException 格式非法时抛出
     */
    public static Object parse(String text) {
        if (text == null || text.isEmpty()) {
            return null;
        }
        try {
            JsonNode node = JacksonSupport.defaultMapper().readTree(text);
            if (node.isObject()) {
                return JsonNodeConverter.toJSONObject(node);
            }
            if (node.isArray()) {
                return JsonNodeConverter.toJSONArray(node);
            }
            return JacksonSupport.toJavaValue(node);
        } catch (IOException e) {
            throw new JSONException(e.getMessage(), e);
        }
    }

    /**
     * 解析 JSON 文本为 {@link JSONObject}，支持指定解析特性。
     *
     * @param text     JSON 字符串
     * @param features 解析特性
     * @return {@link JSONObject}；{@code text} 为 {@code null} 或空时返回 {@code null}
     * @see #parseObject(String)
     */
    public static Object parse(String text, Feature... features) {
        return parseObject(text, features);
    }

    /**
     * 解析 JSON 字节数组，使用 UTF-8 编码。
     *
     * @param input    JSON 字节数组
     * @param features 解析特性
     * @return 解析结果；{@code input} 为 {@code null} 或空时返回 {@code null}
     */
    public static Object parse(byte[] input, Feature... features) {
        if (input == null || input.length == 0) {
            return null;
        }
        return parse(new String(input, StandardCharsets.UTF_8), features);
    }

    /**
     * 解析 JSON 字符串为 {@link JSONObject}。
     *
     * @param text JSON 对象字符串
     * @return {@link JSONObject}；{@code text} 为 {@code null} 或空时返回 {@code null}
     * @throws JSONException 内容不是 JSON 对象时抛出
     */
    public static JSONObject parseObject(String text) {
        if (text == null || text.isEmpty()) {
            return null;
        }
        try {
            JsonNode node = JacksonSupport.defaultMapper().readTree(text);
            if (!node.isObject()) {
                throw new JSONException("expect object, but " + node.getNodeType());
            }
            return JsonNodeConverter.toJSONObject(node);
        } catch (IOException e) {
            throw new JSONException(e.getMessage(), e);
        }
    }

    /**
     * 解析 JSON 字符串为 {@link JSONObject}，支持指定解析特性。
     *
     * @param text     JSON 对象字符串
     * @param features 解析特性，如 {@link Feature#AllowComment}
     * @return {@link JSONObject}；{@code text} 为 {@code null} 或空时返回 {@code null}
     * @throws JSONException 内容不是 JSON 对象或格式非法时抛出
     */
    public static JSONObject parseObject(String text, Feature... features) {
        if (text == null || text.isEmpty()) {
            return null;
        }
        try {
            ObjectMapper mapper = JacksonSupport.copyMapper(features);
            JsonNode node = mapper.readTree(text);
            if (!node.isObject()) {
                throw new JSONException("expect object, but " + node.getNodeType());
            }
            return JsonNodeConverter.toJSONObject(node);
        } catch (IOException e) {
            throw new JSONException(e.getMessage(), e);
        }
    }

    /**
     * 解析 JSON 字符串为指定类型的 Java 对象。
     *
     * @param text  JSON 字符串
     * @param clazz 目标类型
     * @param <T>   目标泛型
     * @return Java 对象；{@code text} 为 {@code null} 或空时返回 {@code null}
     * @throws JSONException 解析失败时抛出
     */
    public static <T> T parseObject(String text, Class<T> clazz) {
        return parseObject(text, clazz, new Feature[0]);
    }

    /**
     * 解析 JSON 字符串为指定类型的 Java 对象，支持解析特性。
     *
     * @param text     JSON 字符串
     * @param clazz    目标类型
     * @param features 解析特性
     * @param <T>      目标泛型
     * @return Java 对象；{@code text} 为 {@code null} 或空时返回 {@code null}
     * @throws JSONException 解析失败时抛出
     */
    public static <T> T parseObject(String text, Class<T> clazz, Feature... features) {
        if (text == null || text.isEmpty()) {
            return null;
        }
        try {
            ObjectMapper mapper = JacksonSupport.copyMapper(features);
            return mapper.readValue(text, clazz);
        } catch (IOException e) {
            throw new JSONException(e.getMessage(), e);
        }
    }

    /**
     * 解析 JSON 字符串为指定 {@link Type} 的对象，适用于泛型类型。
     *
     * @param text JSON 字符串
     * @param type 目标类型（可通过 {@link TypeReference#getType()} 获取）
     * @param <T>  目标泛型
     * @return Java 对象；{@code text} 为 {@code null} 或空时返回 {@code null}
     */
    public static <T> T parseObject(String text, Type type) {
        return parseObject(text, type, new Feature[0]);
    }

    /**
     * 解析 JSON 字符串为指定 {@link Type} 的对象，支持解析特性。
     *
     * @param text     JSON 字符串
     * @param type     目标类型
     * @param features 解析特性
     * @param <T>      目标泛型
     * @return Java 对象；{@code text} 为 {@code null} 或空时返回 {@code null}
     */
    public static <T> T parseObject(String text, Type type, Feature... features) {
        if (text == null || text.isEmpty()) {
            return null;
        }
        try {
            ObjectMapper mapper = JacksonSupport.copyMapper(features);
            JavaType javaType = mapper.getTypeFactory().constructType(type);
            return mapper.readValue(text, javaType);
        } catch (IOException e) {
            throw new JSONException(e.getMessage(), e);
        }
    }

    /**
     * 通过 {@link TypeReference} 解析 JSON 字符串，适用于 {@code List<User>} 等泛型场景。
     *
     * @param text          JSON 字符串
     * @param typeReference 泛型类型引用
     * @param <T>           目标泛型
     * @return Java 对象；{@code text} 为 {@code null} 或空时返回 {@code null}
     */
    public static <T> T parseObject(String text, TypeReference<T> typeReference) {
        return parseObject(text, typeReference, new Feature[0]);
    }

    /**
     * 通过 {@link TypeReference} 解析 JSON 字符串，支持解析特性。
     *
     * @param text          JSON 字符串
     * @param typeReference 泛型类型引用，不可为 {@code null}
     * @param features      解析特性
     * @param <T>           目标泛型
     * @return Java 对象；{@code text} 为 {@code null} 或空时返回 {@code null}
     * @throws JSONException {@code typeReference} 为 {@code null} 时抛出
     */
    public static <T> T parseObject(String text, TypeReference<T> typeReference, Feature... features) {
        if (typeReference == null) {
            throw new JSONException("typeReference is null");
        }
        return parseObject(text, typeReference.getType(), features);
    }

    /**
     * 解析 UTF-8 编码的 JSON 字节数组为 Java 对象。
     *
     * @param bytes JSON 字节数组
     * @param clazz 目标类型
     * @param <T>   目标泛型
     * @return Java 对象；{@code bytes} 为 {@code null} 或空时返回 {@code null}
     */
    public static <T> T parseObject(byte[] bytes, Class<T> clazz) {
        return parseObject(bytes, clazz, new Feature[0]);
    }

    /**
     * 解析 UTF-8 编码的 JSON 字节数组为 Java 对象，支持解析特性。
     *
     * @param bytes    JSON 字节数组
     * @param clazz    目标类型
     * @param features 解析特性
     * @param <T>      目标泛型
     * @return Java 对象；{@code bytes} 为 {@code null} 或空时返回 {@code null}
     */
    public static <T> T parseObject(byte[] bytes, Class<T> clazz, Feature... features) {
        if (bytes == null || bytes.length == 0) {
            return null;
        }
        return parseObject(new String(bytes, StandardCharsets.UTF_8), clazz, features);
    }

    /**
     * 解析 UTF-8 编码的 JSON 字节数组为指定 {@link Type} 的对象。
     *
     * @param bytes    JSON 字节数组
     * @param type     目标类型
     * @param features 解析特性
     * @param <T>      目标泛型
     * @return Java 对象；{@code bytes} 为 {@code null} 或空时返回 {@code null}
     */
    public static <T> T parseObject(byte[] bytes, Type type, Feature... features) {
        if (bytes == null || bytes.length == 0) {
            return null;
        }
        return parseObject(new String(bytes, StandardCharsets.UTF_8), type, features);
    }

    /**
     * 从 {@link InputStream} 读取并解析为 Java 对象。
     *
     * @param inputStream 输入流
     * @param clazz       目标类型
     * @param <T>         目标泛型
     * @return Java 对象；{@code inputStream} 为 {@code null} 时返回 {@code null}
     */
    public static <T> T parseObject(InputStream inputStream, Class<T> clazz) {
        return parseObject(inputStream, clazz, new Feature[0]);
    }

    /**
     * 从 {@link InputStream} 读取并解析为 Java 对象，支持解析特性。
     *
     * @param inputStream 输入流
     * @param clazz       目标类型
     * @param features    解析特性
     * @param <T>         目标泛型
     * @return Java 对象；{@code inputStream} 为 {@code null} 时返回 {@code null}
     */
    public static <T> T parseObject(InputStream inputStream, Class<T> clazz, Feature... features) {
        if (inputStream == null) {
            return null;
        }
        try {
            ObjectMapper mapper = JacksonSupport.copyMapper(features);
            return mapper.readValue(inputStream, clazz);
        } catch (IOException e) {
            throw new JSONException(e.getMessage(), e);
        }
    }

    /**
     * 从 {@link InputStream} 读取并解析为指定 {@link Type} 的对象。
     *
     * @param inputStream 输入流
     * @param type        目标类型
     * @param features    解析特性
     * @param <T>         目标泛型
     * @return Java 对象；{@code inputStream} 为 {@code null} 时返回 {@code null}
     */
    public static <T> T parseObject(InputStream inputStream, Type type, Feature... features) {
        if (inputStream == null) {
            return null;
        }
        try {
            ObjectMapper mapper = JacksonSupport.copyMapper(features);
            JavaType javaType = mapper.getTypeFactory().constructType(type);
            return mapper.readValue(inputStream, javaType);
        } catch (IOException e) {
            throw new JSONException(e.getMessage(), e);
        }
    }

    /**
     * 从 {@link Reader} 读取并解析为 Java 对象。
     *
     * @param reader 字符输入流
     * @param clazz  目标类型
     * @param <T>    目标泛型
     * @return Java 对象；{@code reader} 为 {@code null} 时返回 {@code null}
     */
    public static <T> T parseObject(Reader reader, Class<T> clazz) {
        return parseObject(reader, clazz, new Feature[0]);
    }

    /**
     * 从 {@link Reader} 读取并解析为 Java 对象，支持解析特性。
     *
     * @param reader   字符输入流
     * @param clazz    目标类型
     * @param features 解析特性
     * @param <T>      目标泛型
     * @return Java 对象；{@code reader} 为 {@code null} 时返回 {@code null}
     */
    public static <T> T parseObject(Reader reader, Class<T> clazz, Feature... features) {
        if (reader == null) {
            return null;
        }
        try {
            ObjectMapper mapper = JacksonSupport.copyMapper(features);
            return mapper.readValue(reader, clazz);
        } catch (IOException e) {
            throw new JSONException(e.getMessage(), e);
        }
    }

    /**
     * 解析 JSON 字符串为 {@link JSONArray}。
     *
     * @param text JSON 数组字符串
     * @return {@link JSONArray}；{@code text} 为 {@code null} 或空时返回 {@code null}
     * @throws JSONException 内容不是 JSON 数组时抛出
     */
    public static JSONArray parseArray(String text) {
        if (text == null || text.isEmpty()) {
            return null;
        }
        try {
            JsonNode node = JacksonSupport.defaultMapper().readTree(text);
            if (!node.isArray()) {
                throw new JSONException("expect array, but " + node.getNodeType());
            }
            return JsonNodeConverter.toJSONArray(node);
        } catch (IOException e) {
            throw new JSONException(e.getMessage(), e);
        }
    }

    /**
     * 解析 JSON 字符串为 {@link JSONArray}，支持解析特性。
     *
     * @param text     JSON 数组字符串
     * @param features 解析特性
     * @return {@link JSONArray}；{@code text} 为 {@code null} 或空时返回 {@code null}
     * @throws JSONException 内容不是 JSON 数组或格式非法时抛出
     */
    public static JSONArray parseArray(String text, Feature... features) {
        if (text == null || text.isEmpty()) {
            return null;
        }
        try {
            ObjectMapper mapper = JacksonSupport.copyMapper(features);
            JsonNode node = mapper.readTree(text);
            if (!node.isArray()) {
                throw new JSONException("expect array, but " + node.getNodeType());
            }
            return JsonNodeConverter.toJSONArray(node);
        } catch (IOException e) {
            throw new JSONException(e.getMessage(), e);
        }
    }

    /**
     * 解析 JSON 数组字符串为 {@code List<T>}。
     *
     * @param text  JSON 数组字符串
     * @param clazz 元素类型
     * @param <T>   元素泛型
     * @return 元素列表；{@code text} 为 {@code null} 或空时返回 {@code null}
     */
    public static <T> List<T> parseArray(String text, Class<T> clazz) {
        return parseArray(text, clazz, new Feature[0]);
    }

    /**
     * 解析 JSON 数组字符串为 {@code List<T>}，支持解析特性。
     *
     * @param text     JSON 数组字符串
     * @param clazz    元素类型
     * @param features 解析特性
     * @param <T>      元素泛型
     * @return 元素列表；{@code text} 为 {@code null} 或空时返回 {@code null}
     */
    public static <T> List<T> parseArray(String text, Class<T> clazz, Feature... features) {
        if (text == null || text.isEmpty()) {
            return null;
        }
        try {
            ObjectMapper mapper = JacksonSupport.copyMapper(features);
            JavaType javaType = mapper.getTypeFactory().constructCollectionType(List.class, clazz);
            return mapper.readValue(text, javaType);
        } catch (IOException e) {
            throw new JSONException(e.getMessage(), e);
        }
    }

    /**
     * 解析 UTF-8 编码的 JSON 字节数组为 {@code List<T>}。
     *
     * @param bytes JSON 字节数组
     * @param clazz 元素类型
     * @param <T>   元素泛型
     * @return 元素列表；{@code bytes} 为 {@code null} 或空时返回 {@code null}
     */
    public static <T> List<T> parseArray(byte[] bytes, Class<T> clazz) {
        if (bytes == null || bytes.length == 0) {
            return null;
        }
        return parseArray(new String(bytes, StandardCharsets.UTF_8), clazz);
    }

    // ======================== 序列化 ========================

    /**
     * 将 Java 对象序列化为 JSON 字符串。
     *
     * @param object 待序列化对象，可为 {@link JSONObject}、JavaBean、{@link List} 等
     * @return JSON 字符串；{@code object} 为 {@code null} 时返回 {@code null}
     */
    public static String toJSONString(Object object) {
        return toJSONString(object, new SerializerFeature[0]);
    }

    /**
     * 将 Java 对象序列化为 JSON 字符串，支持序列化特性。
     *
     * @param object   待序列化对象
     * @param features 序列化特性，如 {@link SerializerFeature#PrettyFormat}
     * @return JSON 字符串；{@code object} 为 {@code null} 时返回 {@code null}
     */
    public static String toJSONString(Object object, SerializerFeature... features) {
        if (object == null) {
            return null;
        }
        try {
            return JacksonSupport.writer(features).writeValueAsString(object);
        } catch (JsonProcessingException e) {
            throw new JSONException(e.getMessage(), e);
        }
    }

    /**
     * 使用指定日期格式将 Java 对象序列化为 JSON 字符串。
     *
     * @param object     待序列化对象
     * @param dateFormat 日期格式，如 {@code yyyy-MM-dd HH:mm:ss}
     * @param features   序列化特性
     * @return JSON 字符串；{@code object} 为 {@code null} 时返回 {@code null}
     */
    public static String toJSONStringWithDateFormat(Object object, String dateFormat, SerializerFeature... features) {
        if (object == null) {
            return null;
        }
        try {
            ObjectMapper mapper = JacksonSupport.defaultMapper().copy();
            mapper.setDateFormat(new java.text.SimpleDateFormat(dateFormat));
            return JacksonSupport.writer(mapper, features).writeValueAsString(object);
        } catch (JsonProcessingException e) {
            throw new JSONException(e.getMessage(), e);
        }
    }

    /**
     * 将 Java 对象序列化为 UTF-8 编码的 JSON 字节数组。
     *
     * @param object 待序列化对象
     * @return JSON 字节数组；{@code object} 为 {@code null} 时返回 {@code null}
     */
    public static byte[] toJSONBytes(Object object) {
        return toJSONBytes(object, new SerializerFeature[0]);
    }

    /**
     * 将 Java 对象序列化为 JSON 字节数组，支持序列化特性。
     *
     * @param object   待序列化对象
     * @param features 序列化特性
     * @return JSON 字节数组；{@code object} 为 {@code null} 时返回 {@code null}
     */
    public static byte[] toJSONBytes(Object object, SerializerFeature... features) {
        if (object == null) {
            return null;
        }
        try {
            return JacksonSupport.writer(features).writeValueAsBytes(object);
        } catch (JsonProcessingException e) {
            throw new JSONException(e.getMessage(), e);
        }
    }

    /**
     * 将 Java 对象序列化为指定字符集的 JSON 字节数组。
     *
     * @param object   待序列化对象
     * @param features 序列化特性
     * @param charset  字符集
     * @return JSON 字节数组；{@code object} 为 {@code null} 时返回 {@code null}
     */
    public static byte[] toJSONBytes(Object object, SerializerFeature[] features, Charset charset) {
        String json = toJSONString(object, features);
        return json == null ? null : json.getBytes(charset);
    }

    /**
     * 将 Java 对象序列化并写入 {@link Writer}。
     *
     * @param writer   输出目标
     * @param object   待序列化对象
     * @param features 序列化特性
     */
    public static void writeJSONString(Writer writer, Object object, SerializerFeature... features) {
        if (object == null) {
            return;
        }
        try {
            JacksonSupport.writer(features).writeValue(writer, object);
        } catch (IOException e) {
            throw new JSONException(e.getMessage(), e);
        }
    }

    /**
     * 将 Java 对象序列化并写入 {@link OutputStream}。
     *
     * @param outputStream 输出目标
     * @param object       待序列化对象
     * @param features     序列化特性
     */
    public static void writeJSONString(OutputStream outputStream, Object object, SerializerFeature... features) {
        if (object == null) {
            return;
        }
        try {
            JacksonSupport.writer(features).writeValue(outputStream, object);
        } catch (IOException e) {
            throw new JSONException(e.getMessage(), e);
        }
    }

    // ======================== 转换与校验 ========================

    /**
     * 将 Java 对象转换为 {@link JSONObject} 或 {@link JSONArray} 树结构。
     * <p>
     * 若入参本身已是 {@link JSONObject}/{@link JSONArray} 则直接返回；
     * {@link java.util.Map} 转为 {@link JSONObject}，{@link java.util.Collection} 转为 {@link JSONArray}；
     * 其他类型先序列化再解析为树结构。
     * </p>
     *
     * @param javaObject 待转换对象
     * @return JSON 树；{@code javaObject} 为 {@code null} 时返回 {@code null}
     */
    public static Object toJSON(Object javaObject) {
        if (javaObject == null) {
            return null;
        }
        if (javaObject instanceof JSONObject || javaObject instanceof JSONArray) {
            return javaObject;
        }
        if (javaObject instanceof java.util.Map) {
            return new JSONObject((java.util.Map<String, Object>) javaObject);
        }
        if (javaObject instanceof java.util.Collection) {
            return new JSONArray((java.util.Collection<Object>) javaObject);
        }
        return parse(toJSONString(javaObject));
    }

    /**
     * 将 {@link JSON} 子类（{@link JSONObject}/{@link JSONArray}）转换为指定类型的 Java 对象。
     *
     * @param json  JSON 容器
     * @param clazz 目标类型
     * @param <T>   目标泛型
     * @return Java 对象；{@code json} 为 {@code null} 时返回 {@code null}
     */
    public static <T> T toJavaObject(JSON json, Class<T> clazz) {
        if (json == null) {
            return null;
        }
        return parseObject(toJSONString(json), clazz);
    }

    /**
     * 校验字符串是否为合法 JSON（对象、数组或基本类型均可）。
     *
     * @param text 待校验字符串
     * @return 合法返回 {@code true}，否则返回 {@code false}
     */
    public static boolean isValid(String text) {
        if (text == null || text.isEmpty()) {
            return false;
        }
        try {
            JacksonSupport.defaultMapper().readTree(text);
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    /**
     * 校验字符串是否为合法 JSON 对象（{@code {...}}）。
     *
     * @param text 待校验字符串
     * @return 是 JSON 对象返回 {@code true}，否则返回 {@code false}
     */
    public static boolean isValidObject(String text) {
        if (text == null || text.isEmpty()) {
            return false;
        }
        try {
            JsonNode node = JacksonSupport.defaultMapper().readTree(text);
            return node.isObject();
        } catch (IOException e) {
            return false;
        }
    }

    /**
     * 校验字符串是否为合法 JSON 数组（{@code [...]}）。
     *
     * @param text 待校验字符串
     * @return 是 JSON 数组返回 {@code true}，否则返回 {@code false}
     */
    public static boolean isValidArray(String text) {
        if (text == null || text.isEmpty()) {
            return false;
        }
        try {
            JsonNode node = JacksonSupport.defaultMapper().readTree(text);
            return node.isArray();
        } catch (IOException e) {
            return false;
        }
    }
}
