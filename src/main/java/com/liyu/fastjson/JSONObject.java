package com.liyu.fastjson;

import com.liyu.fastjson.parser.Feature;
import com.liyu.fastjson.serializer.SerializerFeature;
import com.liyu.fastjson.support.TypeUtils;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Collection;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * JSON 对象，表示键值对结构的 JSON 数据容器。
 * <p>
 * 实现 {@link Map}{@code <String, Object>} 接口，API 与
 * {@code com.alibaba.fastjson.JSONObject} 保持一致。
 * 内部使用 {@link LinkedHashMap} 保持字段插入顺序。
 * </p>
 *
 * <pre>{@code
 * JSONObject obj = JSON.parseObject("{\"name\":\"张三\",\"age\":18}");
 * String name = obj.getString("name");
 * obj.fluentPut("city", "北京").fluentPut("age", 19);
 * User user = obj.toJavaObject(User.class);
 * }</pre>
 *
 * @see JSON#parseObject(String)
 * @see JSONArray
 */
public class JSONObject extends JSON implements Map<String, Object>, Cloneable, Serializable {

    private static final long serialVersionUID = 1L;

    private final Map<String, Object> map;

    /**
     * 创建空的 JSONObject。
     */
    public JSONObject() {
        this.map = new LinkedHashMap<>();
    }

    /**
     * 根据已有 Map 创建 JSONObject，嵌套 Map/List 会自动转换为 JSONObject/JSONArray。
     *
     * @param map 源 Map，可为 {@code null}（等同空对象）
     */
    public JSONObject(Map<String, Object> map) {
        this.map = new LinkedHashMap<>();
        if (map != null) {
            for (Map.Entry<String, Object> entry : map.entrySet()) {
                this.map.put(entry.getKey(), convertValue(entry.getValue()));
            }
        }
    }

    /**
     * 创建 JSONObject。
     *
     * @param ordered 是否保持有序（当前实现均使用 LinkedHashMap）
     */
    public JSONObject(boolean ordered) {
        this.map = ordered ? new LinkedHashMap<>() : new LinkedHashMap<>();
    }

    /**
     * 创建指定初始容量的 JSONObject。
     *
     * @param initialCapacity 初始容量
     */
    public JSONObject(int initialCapacity) {
        this.map = new LinkedHashMap<>(initialCapacity);
    }

    /**
     * 创建指定初始容量的 JSONObject。
     *
     * @param initialCapacity 初始容量
     * @param ordered         是否保持有序（当前实现均使用 LinkedHashMap）
     */
    public JSONObject(int initialCapacity, boolean ordered) {
        this.map = new LinkedHashMap<>(initialCapacity);
    }

    /**
     * 解析 JSON 字符串为 {@link JSONObject}。
     *
     * @param text JSON 对象字符串
     * @return {@link JSONObject}
     * @see JSON#parseObject(String)
     */
    public static JSONObject parseObject(String text) {
        return JSON.parseObject(text);
    }

    /**
     * 解析 JSON 字符串为 {@link JSONObject}，支持解析特性。
     *
     * @param text     JSON 对象字符串
     * @param features 解析特性
     * @return {@link JSONObject}
     */
    public static JSONObject parseObject(String text, Feature... features) {
        return JSON.parseObject(text, features);
    }

    /**
     * 解析 JSON 字符串为 JavaBean。
     *
     * @param text  JSON 字符串
     * @param clazz 目标类型
     * @param <T>   目标泛型
     * @return Java 对象
     */
    public static <T> T parseObject(String text, Class<T> clazz) {
        return JSON.parseObject(text, clazz);
    }

    /**
     * 解析 JSON 字符串为 JavaBean，支持解析特性。
     *
     * @param text     JSON 字符串
     * @param clazz    目标类型
     * @param features 解析特性
     * @param <T>      目标泛型
     * @return Java 对象
     */
    public static <T> T parseObject(String text, Class<T> clazz, Feature... features) {
        return JSON.parseObject(text, clazz, features);
    }

    private static Object convertValue(Object value) {
        if (value instanceof Map && !(value instanceof JSONObject)) {
            return new JSONObject((Map<String, Object>) value);
        }
        if (value instanceof List && !(value instanceof JSONArray)) {
            return new JSONArray((List<Object>) value);
        }
        return value;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int size() {
        return map.size();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isEmpty() {
        return map.isEmpty();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean containsKey(Object key) {
        return map.containsKey(key);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean containsValue(Object value) {
        return map.containsValue(value);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Object get(Object key) {
        return map.get(key);
    }

    /**
     * 写入键值对，嵌套 Map/List 会自动包装为 JSONObject/JSONArray。
     *
     * @param key   键
     * @param value 值
     * @return 先前关联的值，若无则返回 {@code null}
     */
    @Override
    public Object put(String key, Object value) {
        return map.put(key, convertValue(value));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Object remove(Object key) {
        return map.remove(key);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void putAll(Map<? extends String, ?> m) {
        if (m == null) {
            return;
        }
        for (Map.Entry<? extends String, ?> entry : m.entrySet()) {
            put(entry.getKey(), entry.getValue());
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void clear() {
        map.clear();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Set<String> keySet() {
        return map.keySet();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Collection<Object> values() {
        return map.values();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Set<Entry<String, Object>> entrySet() {
        return map.entrySet();
    }

    /**
     * 链式写入键值对，返回当前对象以便连续调用。
     *
     * @param key   键
     * @param value 值
     * @return 当前 {@link JSONObject}
     */
    public JSONObject fluentPut(String key, Object value) {
        put(key, value);
        return this;
    }

    /**
     * 链式删除键，返回当前对象。
     *
     * @param key 键
     * @return 当前 {@link JSONObject}
     */
    public JSONObject fluentRemove(String key) {
        remove(key);
        return this;
    }

    /**
     * 链式清空，返回当前对象。
     *
     * @return 当前 {@link JSONObject}
     */
    public JSONObject fluentClear() {
        clear();
        return this;
    }

    /**
     * 链式批量写入，返回当前对象。
     *
     * @param m 源 Map
     * @return 当前 {@link JSONObject}
     */
    public JSONObject fluentPutAll(Map<? extends String, ?> m) {
        putAll(m);
        return this;
    }

    /**
     * 获取字符串值。
     *
     * @param key 键
     * @return 字符串值；键不存在或值为 {@code null} 时返回 {@code null}
     */
    public String getString(String key) {
        return TypeUtils.castToString(get(key));
    }

    /**
     * 获取 {@link Integer} 值。
     *
     * @param key 键
     * @return 整数值；无法转换时返回 {@code null}
     */
    public Integer getInteger(String key) {
        return TypeUtils.castToInt(get(key));
    }

    /**
     * 获取 int 基本类型值，缺失或 {@code null} 时返回 {@code 0}。
     *
     * @param key 键
     * @return int 值
     */
    public int getIntValue(String key) {
        return TypeUtils.castToIntValue(get(key));
    }

    /**
     * 获取 {@link Long} 值。
     *
     * @param key 键
     * @return 长整型值；无法转换时返回 {@code null}
     */
    public Long getLong(String key) {
        return TypeUtils.castToLong(get(key));
    }

    /**
     * 获取 long 基本类型值，缺失或 {@code null} 时返回 {@code 0L}。
     *
     * @param key 键
     * @return long 值
     */
    public long getLongValue(String key) {
        return TypeUtils.castToLongValue(get(key));
    }

    /**
     * 获取 {@link Boolean} 值。
     *
     * @param key 键
     * @return 布尔值；无法转换时返回 {@code null}
     */
    public Boolean getBoolean(String key) {
        return TypeUtils.castToBoolean(get(key));
    }

    /**
     * 获取 boolean 基本类型值，缺失或 {@code null} 时返回 {@code false}。
     *
     * @param key 键
     * @return boolean 值
     */
    public boolean getBooleanValue(String key) {
        return TypeUtils.castToBooleanValue(get(key));
    }

    /**
     * 获取 {@link Float} 值。
     *
     * @param key 键
     * @return 浮点值；无法转换时返回 {@code null}
     */
    public Float getFloat(String key) {
        return TypeUtils.castToFloat(get(key));
    }

    /**
     * 获取 float 基本类型值，缺失或 {@code null} 时返回 {@code 0.0f}。
     *
     * @param key 键
     * @return float 值
     */
    public float getFloatValue(String key) {
        Float value = getFloat(key);
        return value == null ? 0F : value;
    }

    /**
     * 获取 {@link Double} 值。
     *
     * @param key 键
     * @return 双精度值；无法转换时返回 {@code null}
     */
    public Double getDouble(String key) {
        return TypeUtils.castToDouble(get(key));
    }

    /**
     * 获取 double 基本类型值，缺失或 {@code null} 时返回 {@code 0.0}。
     *
     * @param key 键
     * @return double 值
     */
    public double getDoubleValue(String key) {
        return TypeUtils.castToDoubleValue(get(key));
    }

    /**
     * 获取 {@link BigDecimal} 值。
     *
     * @param key 键
     * @return BigDecimal 值；无法转换时返回 {@code null}
     */
    public BigDecimal getBigDecimal(String key) {
        return TypeUtils.castToBigDecimal(get(key));
    }

    /**
     * 获取 {@link BigInteger} 值。
     *
     * @param key 键
     * @return BigInteger 值；无法转换时返回 {@code null}
     */
    public BigInteger getBigInteger(String key) {
        BigDecimal decimal = getBigDecimal(key);
        return decimal == null ? null : decimal.toBigInteger();
    }

    /**
     * 获取 {@link Date} 值。
     *
     * @param key 键
     * @return 日期值；无法转换时返回 {@code null}
     */
    public Date getDate(String key) {
        return TypeUtils.castToDate(get(key));
    }

    /**
     * 获取嵌套的 {@link JSONObject}。
     * <p>若值为 Map 或 JSON 字符串，会自动转换。</p>
     *
     * @param key 键
     * @return {@link JSONObject}；不存在或无法转换时返回 {@code null}
     */
    public JSONObject getJSONObject(String key) {
        return TypeUtils.castToJSONObject(get(key));
    }

    /**
     * 获取嵌套的 {@link JSONArray}。
     * <p>若值为 List 或 JSON 字符串，会自动转换。</p>
     *
     * @param key 键
     * @return {@link JSONArray}；不存在或无法转换时返回 {@code null}
     */
    public JSONArray getJSONArray(String key) {
        return TypeUtils.castToJSONArray(get(key));
    }

    /**
     * 获取指定键的值并转换为目标类型 Java 对象。
     *
     * @param key   键
     * @param clazz 目标类型
     * @param <T>   目标泛型
     * @return Java 对象；键不存在或值为 {@code null} 时返回 {@code null}
     */
    public <T> T getObject(String key, Class<T> clazz) {
        Object value = get(key);
        if (value == null) {
            return null;
        }
        if (clazz.isInstance(value)) {
            return clazz.cast(value);
        }
        return JSON.parseObject(JSON.toJSONString(value), clazz);
    }

    /**
     * 将当前 JSONObject 转换为指定类型的 JavaBean。
     *
     * @param clazz 目标类型
     * @param <T>   目标泛型
     * @return Java 对象
     */
    public <T> T toJavaObject(Class<T> clazz) {
        return JSON.toJavaObject(this, clazz);
    }

    /**
     * 将当前 JSONObject 序列化为 JSON 字节数组。
     *
     * @param features 序列化特性
     * @return JSON 字节数组
     */
    public byte[] toJSONBytes(SerializerFeature... features) {
        return JSON.toJSONBytes(this, features);
    }

    /**
     * 深拷贝当前 JSONObject（嵌套对象递归克隆）。
     *
     * @return 克隆后的新对象
     */
    @Override
    public JSONObject clone() {
        JSONObject clone = new JSONObject();
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            Object value = entry.getValue();
            if (value instanceof JSONObject) {
                clone.put(entry.getKey(), ((JSONObject) value).clone());
            } else if (value instanceof JSONArray) {
                clone.put(entry.getKey(), ((JSONArray) value).clone());
            } else {
                clone.put(entry.getKey(), value);
            }
        }
        return clone;
    }

    /**
     * 返回 JSON 字符串，等价于 {@link #toJSONString()}。
     */
    @Override
    public String toString() {
        return toJSONString();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj instanceof JSONObject) {
            return map.equals(((JSONObject) obj).map);
        }
        if (obj instanceof Map) {
            return map.equals(obj);
        }
        return false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        return map.hashCode();
    }
}
