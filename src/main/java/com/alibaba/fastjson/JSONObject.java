package com.alibaba.fastjson;

import com.alibaba.fastjson.util.TypeUtils;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.*;

/**
 * @author LiYu
 * @ClassName JSONObject.java
 * @Description 代替fastjson的JSONObject
 * @createTime 2024年05月30日 12:58:00
 */
public class JSONObject extends JSON implements Map<String, Object> {

    private final Map<String, Object> map;

    /**
     * 构造方法
     *
     * @param map map
     */
    public JSONObject(Map<String, Object> map) {
        this.map = map;
    }

    /**
     * 构造方法
     */
    public JSONObject() {
        map = new HashMap<>();
    }

    /**
     * 构造方法
     *
     * @param size size
     */
    public JSONObject(int size) {
        map = new HashMap<>(size);
    }

    /**
     * JSONObject转jsonString
     *
     * @return JSONObject
     */
    public String toJSONString() {
        return toJSONString(map);
    }

    /**
     * 从json获取json
     *
     * @param key key
     * @return JSONObject
     */
    public JSONObject getJSONObject(String key) {
        Object value = map.get(key);
        if (value instanceof JSONObject) {
            return (JSONObject) value;
        }
        if (value instanceof Map) {
            return new JSONObject((Map) value);
        }
        if (value instanceof String) {
            return parseObject((String) value);
        }
        return parseObject(toJSONString(value));
    }

    /**
     * 从json获取jsonArray
     *
     * @param key key
     * @return JSONArray
     */
    public JSONArray getJSONArray(String key) {
        Object value = map.get(key);
        if (value == null) {
            return new JSONArray();
        }
        if (value instanceof JSONArray) {
            return (JSONArray) value;
        }
        if (value instanceof List) {
            return new JSONArray((List) value);
        }
        if (value instanceof String) {
            return parseArray((String) value);
        }
        return parseArray(toJSONString(value));
    }

    /**
     * 从json获取Object
     *
     * @param key   key
     * @param clazz clazz
     * @param <T>   T
     * @return T
     */
    public <T> T getObject(String key, Class<T> clazz) {
        Object obj = map.get(key);
        if (clazz == obj.getClass()) {
            return (T) obj;
        }
        if (clazz == String.class) {
            return parseObject((String) obj, clazz);
        }
        return parseObject(toJSONString(obj), clazz);
    }

    /**
     * 从json获取Boolean
     *
     * @param key key
     * @return Boolean
     */
    public Boolean getBoolean(String key) {
        Object value = get(key);
        if (value == null) {
            return null;
        }
        return TypeUtils.castToBoolean(value);
    }

    /**
     * 从json获取Boolean
     *
     * @param key key
     * @return boolean
     */
    public boolean getBooleanValue(String key) {
        Object value = get(key);
        Boolean booleanVal = TypeUtils.castToBoolean(value);
        if (booleanVal == null) {
            return false;
        }
        return booleanVal;
    }

    /**
     * 从json获取Byte
     *
     * @param key key
     * @return Byte
     */
    public Byte getByte(String key) {
        Object value = get(key);
        return TypeUtils.castToByte(value);
    }

    /**
     * 从json获取Byte
     *
     * @param key key
     * @return byte
     */
    public byte getByteValue(String key) {
        Object value = get(key);
        Byte byteVal = TypeUtils.castToByte(value);
        if (byteVal == null) {
            return 0;
        }
        return byteVal;
    }

    /**
     * 从json获取Short
     *
     * @param key key
     * @return Short
     */
    public Short getShort(String key) {
        Object value = get(key);
        return TypeUtils.castToShort(value);
    }

    /**
     * 从json获取Short
     *
     * @param key key
     * @return short
     */
    public short getShortValue(String key) {
        Object value = get(key);
        Short shortVal = TypeUtils.castToShort(value);
        if (shortVal == null) {
            return 0;
        }
        return shortVal;
    }

    /**
     * 从json获取Integer
     *
     * @param key key
     * @return Integer
     */
    public Integer getInteger(String key) {
        Object value = get(key);
        return TypeUtils.castToInt(value);
    }

    /**
     * 从json获取Integer
     *
     * @param key key
     * @return int
     */
    public int getIntValue(String key) {
        Object value = get(key);
        Integer intVal = TypeUtils.castToInt(value);
        if (intVal == null) {
            return 0;
        }
        return intVal;
    }

    /**
     * 从json获取Long
     *
     * @param key key
     * @return Long
     */
    public Long getLong(String key) {
        Object value = get(key);
        return TypeUtils.castToLong(value);
    }

    /**
     * 从json获取Long
     *
     * @param key key
     * @return long
     */
    public long getLongValue(String key) {
        Object value = get(key);
        Long longVal = TypeUtils.castToLong(value);
        if (longVal == null) {
            return 0L;
        }
        return longVal;
    }

    /**
     * 从json获取Float
     *
     * @param key key
     * @return Float
     */
    public Float getFloat(String key) {
        Object value = get(key);
        return TypeUtils.castToFloat(value);
    }

    /**
     * 从json获取Float
     *
     * @param key key
     * @return float
     */
    public float getFloatValue(String key) {
        Object value = get(key);
        Float floatValue = TypeUtils.castToFloat(value);
        if (floatValue == null) {
            return 0F;
        }
        return floatValue;
    }

    /**
     * 从json获取Double
     *
     * @param key key
     * @return Double
     */
    public Double getDouble(String key) {
        Object value = get(key);
        return TypeUtils.castToDouble(value);
    }

    /**
     * 从json获取Double
     *
     * @param key key
     * @return double
     */
    public double getDoubleValue(String key) {
        Object value = get(key);
        Double doubleValue = TypeUtils.castToDouble(value);
        if (doubleValue == null) {
            return 0D;
        }
        return doubleValue;
    }

    /**
     * 从json获取BigDecimal0
     *
     * @param key key
     * @return BigDecimal
     */
    public BigDecimal getBigDecimal(String key) {
        Object value = get(key);
        return TypeUtils.castToBigDecimal(value);
    }

    /**
     * 从json获取BigInteger
     *
     * @param key key
     * @return BigInteger
     */
    public BigInteger getBigInteger(String key) {
        Object value = get(key);
        return TypeUtils.castToBigInteger(value);
    }

    /**
     * 从json获取String
     *
     * @param key key
     * @return String
     */
    public String getString(String key) {
        Object value = get(key);
        if (value == null) {
            return null;
        }
        return value.toString();
    }

    /**
     * 链式put
     * @param key key
     * @param value value
     * @return Object value
     */
    public JSONObject add(String key, Object value) {
        put(key, value);
        return this;
    }

    /**
     * 从json获取Object
     *
     * @param clazz clazz
     * @param <T>   T
     * @return T
     */
    public <T> T toJavaObject(Class<T> clazz) {
        if (clazz == Map.class || clazz == JSONObject.class || clazz == JSONArray.class) {
            return (T) this;
        }
        return parseObject(toJSONString(), clazz);
    }

    /**
     * 获取JSONObject对应类型的集合
     *
     * @param jsonString json字符串
     * @param clazz      类型
     * @param <T>        泛型
     */
    public static <T> List<T> getTypeArray(String jsonString, Class<T> clazz) {
        return JSON.getTypeArray(jsonString, clazz);
    }

    @Override
    public String toString() {
        return toJSONString();
    }

    @Override
    public Object remove(Object key) {
        return map.remove(key);
    }

    @Override
    public int size() {
        return map.size();
    }

    @Override
    public boolean isEmpty() {
        return map.isEmpty();
    }

    @Override
    public boolean containsKey(Object key) {
        return map.containsKey(key);
    }

    @Override
    public boolean containsValue(Object value) {
        return map.containsValue(value);
    }

    @Override
    public Object get(Object key) {
        return map.get(key);
    }

    @Override
    public Object put(String key, Object value) {
        return map.put(key, value);
    }

    @Override
    public void putAll(Map<? extends String, ?> m) {
        map.putAll(m);
    }

    @Override
    public void clear() {
        map.clear();
    }

    @Override
    public Set<String> keySet() {
        return map.keySet();
    }

    @Override
    public Collection<Object> values() {
        return map.values();
    }

    @Override
    public Set<Entry<String, Object>> entrySet() {
        return map.entrySet();
    }

    @Override
    public boolean equals(Object o) {
        return map.equals(o);
    }

    @Override
    public int hashCode() {
        return map.hashCode();
    }
}
