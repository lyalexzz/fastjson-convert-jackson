package com.liyu.fastjson;

import com.liyu.fastjson.parser.Feature;
import com.liyu.fastjson.serializer.SerializerFeature;
import com.liyu.fastjson.support.TypeUtils;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

/**
 * JSON 数组，表示有序 JSON 元素列表。
 * <p>
 * 实现 {@link List}{@code <Object>} 接口，API 与
 * {@code com.alibaba.fastjson.JSONArray} 保持一致。
 * </p>
 *
 * <pre>{@code
 * JSONArray array = JSON.parseArray("[{\"id\":1},{\"id\":2}]");
 * JSONObject first = array.getJSONObject(0);
 * List<User> users = array.toJavaList(User.class);
 * array.fluentAdd(new JSONObject().fluentPut("id", 3));
 * }</pre>
 *
 * @see JSON#parseArray(String)
 * @see JSONObject
 */
public class JSONArray extends JSON implements List<Object>, Cloneable, Serializable {

    private static final long serialVersionUID = 1L;

    private final List<Object> list;

    /**
     * 创建空的 JSONArray。
     */
    public JSONArray() {
        this.list = new ArrayList<>();
    }

    /**
     * 根据已有 List 创建 JSONArray，嵌套 Map/List 会自动转换。
     *
     * @param list 源 List，可为 {@code null}（等同空数组）
     */
    public JSONArray(List<Object> list) {
        this.list = new ArrayList<>();
        if (list != null) {
            for (Object item : list) {
                this.list.add(convertValue(item));
            }
        }
    }

    /**
     * 创建指定初始容量的 JSONArray。
     *
     * @param initialCapacity 初始容量
     */
    public JSONArray(int initialCapacity) {
        this.list = new ArrayList<>(initialCapacity);
    }

    /**
     * 根据 Collection 创建 JSONArray。
     *
     * @param collection 源集合，可为 {@code null}（等同空数组）
     */
    public JSONArray(Collection<?> collection) {
        this.list = new ArrayList<>();
        if (collection != null) {
            for (Object item : collection) {
                this.list.add(convertValue(item));
            }
        }
    }

    /**
     * 解析 JSON 字符串为 {@link JSONArray}。
     *
     * @param text JSON 数组字符串
     * @return {@link JSONArray}
     * @see JSON#parseArray(String)
     */
    public static JSONArray parseArray(String text) {
        return JSON.parseArray(text);
    }

    /**
     * 解析 JSON 字符串为 {@link JSONArray}，支持解析特性。
     *
     * @param text     JSON 数组字符串
     * @param features 解析特性
     * @return {@link JSONArray}
     */
    public static JSONArray parseArray(String text, Feature... features) {
        return JSON.parseArray(text, features);
    }

    /**
     * 解析 JSON 数组字符串为 {@code List<T>}。
     *
     * @param text  JSON 数组字符串
     * @param clazz 元素类型
     * @param <T>   元素泛型
     * @return 元素列表
     */
    public static <T> List<T> parseArray(String text, Class<T> clazz) {
        return JSON.parseArray(text, clazz);
    }

    /**
     * 解析 JSON 数组字符串为 {@code List<T>}，支持解析特性。
     *
     * @param text     JSON 数组字符串
     * @param clazz    元素类型
     * @param features 解析特性
     * @param <T>      元素泛型
     * @return 元素列表
     */
    public static <T> List<T> parseArray(String text, Class<T> clazz, Feature... features) {
        return JSON.parseArray(text, clazz, features);
    }

    private static Object convertValue(Object value) {
        if (value instanceof Map && !(value instanceof JSONObject)) {
            return new JSONObject((java.util.Map<String, Object>) value);
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
        return list.size();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isEmpty() {
        return list.isEmpty();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean contains(Object o) {
        return list.contains(o);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Iterator<Object> iterator() {
        return list.iterator();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Object[] toArray() {
        return list.toArray();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <T> T[] toArray(T[] a) {
        return list.toArray(a);
    }

    /**
     * 追加元素，嵌套 Map/List 会自动包装。
     *
     * @param e 元素
     * @return 是否修改了列表
     */
    @Override
    public boolean add(Object e) {
        return list.add(convertValue(e));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean remove(Object o) {
        return list.remove(o);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean containsAll(Collection<?> c) {
        return list.containsAll(c);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean addAll(Collection<?> c) {
        boolean modified = false;
        for (Object item : c) {
            modified |= add(item);
        }
        return modified;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean addAll(int index, Collection<?> c) {
        int i = index;
        for (Object item : c) {
            add(i++, convertValue(item));
        }
        return !c.isEmpty();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean removeAll(Collection<?> c) {
        return list.removeAll(c);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean retainAll(Collection<?> c) {
        return list.retainAll(c);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void clear() {
        list.clear();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Object get(int index) {
        return list.get(index);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Object set(int index, Object element) {
        return list.set(index, convertValue(element));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void add(int index, Object element) {
        list.add(index, convertValue(element));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Object remove(int index) {
        return list.remove(index);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int indexOf(Object o) {
        return list.indexOf(o);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int lastIndexOf(Object o) {
        return list.lastIndexOf(o);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ListIterator<Object> listIterator() {
        return list.listIterator();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ListIterator<Object> listIterator(int index) {
        return list.listIterator(index);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Object> subList(int fromIndex, int toIndex) {
        return list.subList(fromIndex, toIndex);
    }

    /**
     * 链式追加元素，返回当前对象。
     *
     * @param e 元素
     * @return 当前 {@link JSONArray}
     */
    public JSONArray fluentAdd(Object e) {
        add(e);
        return this;
    }

    /**
     * 链式删除元素，返回当前对象。
     *
     * @param o 待删除元素
     * @return 当前 {@link JSONArray}
     */
    public JSONArray fluentRemove(Object o) {
        remove(o);
        return this;
    }

    /**
     * 链式清空，返回当前对象。
     *
     * @return 当前 {@link JSONArray}
     */
    public JSONArray fluentClear() {
        clear();
        return this;
    }

    /**
     * 链式批量追加，返回当前对象。
     *
     * @param c 源集合
     * @return 当前 {@link JSONArray}
     */
    public JSONArray fluentAddAll(Collection<?> c) {
        addAll(c);
        return this;
    }

    /**
     * 获取指定下标的字符串值。
     *
     * @param index 下标
     * @return 字符串值；越界或值为 {@code null} 时行为取决于底层值
     */
    public String getString(int index) {
        return TypeUtils.castToString(get(index));
    }

    /**
     * 获取指定下标的 {@link Integer} 值。
     *
     * @param index 下标
     * @return 整数值
     */
    public Integer getInteger(int index) {
        return TypeUtils.castToInt(get(index));
    }

    /**
     * 获取指定下标的 int 基本类型值，{@code null} 时返回 {@code 0}。
     *
     * @param index 下标
     * @return int 值
     */
    public int getIntValue(int index) {
        return TypeUtils.castToIntValue(get(index));
    }

    /**
     * 获取指定下标的 {@link Long} 值。
     *
     * @param index 下标
     * @return 长整型值
     */
    public Long getLong(int index) {
        return TypeUtils.castToLong(get(index));
    }

    /**
     * 获取指定下标的 long 基本类型值，{@code null} 时返回 {@code 0L}。
     *
     * @param index 下标
     * @return long 值
     */
    public long getLongValue(int index) {
        return TypeUtils.castToLongValue(get(index));
    }

    /**
     * 获取指定下标的 {@link Boolean} 值。
     *
     * @param index 下标
     * @return 布尔值
     */
    public Boolean getBoolean(int index) {
        return TypeUtils.castToBoolean(get(index));
    }

    /**
     * 获取指定下标的 boolean 基本类型值，{@code null} 时返回 {@code false}。
     *
     * @param index 下标
     * @return boolean 值
     */
    public boolean getBooleanValue(int index) {
        return TypeUtils.castToBooleanValue(get(index));
    }

    /**
     * 获取指定下标的 {@link Float} 值。
     *
     * @param index 下标
     * @return 浮点值
     */
    public Float getFloat(int index) {
        return TypeUtils.castToFloat(get(index));
    }

    /**
     * 获取指定下标的 float 基本类型值，{@code null} 时返回 {@code 0.0f}。
     *
     * @param index 下标
     * @return float 值
     */
    public float getFloatValue(int index) {
        Float value = getFloat(index);
        return value == null ? 0F : value;
    }

    /**
     * 获取指定下标的 {@link Double} 值。
     *
     * @param index 下标
     * @return 双精度值
     */
    public Double getDouble(int index) {
        return TypeUtils.castToDouble(get(index));
    }

    /**
     * 获取指定下标的 double 基本类型值，{@code null} 时返回 {@code 0.0}。
     *
     * @param index 下标
     * @return double 值
     */
    public double getDoubleValue(int index) {
        return TypeUtils.castToDoubleValue(get(index));
    }

    /**
     * 获取指定下标的 {@link BigDecimal} 值。
     *
     * @param index 下标
     * @return BigDecimal 值
     */
    public BigDecimal getBigDecimal(int index) {
        return TypeUtils.castToBigDecimal(get(index));
    }

    /**
     * 获取指定下标的 {@link BigInteger} 值。
     *
     * @param index 下标
     * @return BigInteger 值
     */
    public BigInteger getBigInteger(int index) {
        BigDecimal decimal = getBigDecimal(index);
        return decimal == null ? null : decimal.toBigInteger();
    }

    /**
     * 获取指定下标的 {@link Date} 值。
     *
     * @param index 下标
     * @return 日期值
     */
    public Date getDate(int index) {
        return TypeUtils.castToDate(get(index));
    }

    /**
     * 获取指定下标的 {@link JSONObject}。
     *
     * @param index 下标
     * @return {@link JSONObject}；无法转换时返回 {@code null}
     */
    public JSONObject getJSONObject(int index) {
        return TypeUtils.castToJSONObject(get(index));
    }

    /**
     * 获取指定下标的 {@link JSONArray}。
     *
     * @param index 下标
     * @return {@link JSONArray}；无法转换时返回 {@code null}
     */
    public JSONArray getJSONArray(int index) {
        return TypeUtils.castToJSONArray(get(index));
    }

    /**
     * 获取指定下标的元素并转换为目标类型 Java 对象。
     *
     * @param index 下标
     * @param clazz 目标类型
     * @param <T>   目标泛型
     * @return Java 对象；元素为 {@code null} 时返回 {@code null}
     */
    public <T> T getObject(int index, Class<T> clazz) {
        Object value = get(index);
        if (value == null) {
            return null;
        }
        if (clazz.isInstance(value)) {
            return clazz.cast(value);
        }
        return JSON.parseObject(JSON.toJSONString(value), clazz);
    }

    /**
     * 将整个 JSONArray 转换为指定类型的 JavaBean（适用于数组仅含一个对象等场景）。
     *
     * @param clazz 目标类型
     * @param <T>   目标泛型
     * @return Java 对象
     */
    public <T> T toJavaObject(Class<T> clazz) {
        return JSON.toJavaObject(this, clazz);
    }

    /**
     * 将 JSONArray 中每个元素转换为 {@code List<T>}。
     *
     * @param clazz 元素目标类型
     * @param <T>   元素泛型
     * @return 转换后的列表
     */
    public <T> List<T> toJavaList(Class<T> clazz) {
        List<T> result = new ArrayList<>(size());
        for (int i = 0; i < size(); i++) {
            result.add(getObject(i, clazz));
        }
        return result;
    }

    /**
     * 将当前 JSONArray 序列化为 JSON 字节数组。
     *
     * @param features 序列化特性
     * @return JSON 字节数组
     */
    public byte[] toJSONBytes(SerializerFeature... features) {
        return JSON.toJSONBytes(this, features);
    }

    /**
     * 深拷贝当前 JSONArray（嵌套对象递归克隆）。
     *
     * @return 克隆后的新数组
     */
    @Override
    public JSONArray clone() {
        JSONArray clone = new JSONArray();
        for (Object item : list) {
            if (item instanceof JSONObject) {
                clone.add(((JSONObject) item).clone());
            } else if (item instanceof JSONArray) {
                clone.add(((JSONArray) item).clone());
            } else {
                clone.add(item);
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
        if (obj instanceof JSONArray) {
            return list.equals(((JSONArray) obj).list);
        }
        if (obj instanceof List) {
            return list.equals(obj);
        }
        return false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        return list.hashCode();
    }
}
