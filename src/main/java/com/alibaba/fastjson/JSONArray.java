package com.alibaba.fastjson;

import com.alibaba.fastjson.util.TypeUtils;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.*;
import java.util.function.UnaryOperator;

/**
 * @author LiYu
 * @ClassName JSONArray.java
 * @Description 代替fastjson的JSONArray
 * @createTime 2024年05月30日 13:01:00
 */
public class JSONArray extends JSON implements List<Object> {
    private final List<Object> list;

    public JSONArray(List<Object> list) {
        this.list = list;
    }

    public JSONArray(int size) {
        list = new ArrayList<>(size);
    }


    public JSONArray() {
        list = new ArrayList<>(16);
    }

    public JSONObject getJSONObject(int index) {
        Object value = list.get(index);

        if (value instanceof JSONObject) {
            return (JSONObject) value;
        }

        if (value instanceof Map) {
            return new JSONObject((Map) value);
        }

        return null;
    }

    public JSONArray getJSONArray(int index) {
        Object value = list.get(index);

        if (value instanceof JSONArray) {
            return (JSONArray) value;
        }

        if (value instanceof List) {
            return new JSONArray((List) value);
        }

        return null;
    }


    public Boolean getBoolean(int index) {
        Object value = get(index);

        if (value == null) {
            return null;
        }

        return TypeUtils.castToBoolean(value);
    }

    public boolean getBooleanValue(int index) {
        Object value = get(index);

        if (value == null) {
            return false;
        }

        return TypeUtils.castToBoolean(value);
    }
    public <T> List<T> toJavaList(Class<T> clazz) {
        return parseArray(toJSONString(), clazz);
    }

    public Byte getByte(int index) {
        Object value = get(index);

        return TypeUtils.castToByte(value);
    }

    public byte getByteValue(int index) {
        Object value = get(index);

        Byte byteVal = TypeUtils.castToByte(value);
        if (byteVal == null) {
            return 0;
        }

        return byteVal;
    }

    public Short getShort(int index) {
        Object value = get(index);

        return TypeUtils.castToShort(value);
    }

    public short getShortValue(int index) {
        Object value = get(index);

        Short shortVal = TypeUtils.castToShort(value);
        if (shortVal == null) {
            return 0;
        }

        return shortVal;
    }

    public Integer getInteger(int index) {
        Object value = get(index);

        return TypeUtils.castToInt(value);
    }

    public int getIntValue(int index) {
        Object value = get(index);

        Integer intVal = TypeUtils.castToInt(value);
        if (intVal == null) {
            return 0;
        }

        return intVal;
    }

    public Long getLong(int index) {
        Object value = get(index);

        return TypeUtils.castToLong(value);
    }

    public long getLongValue(int index) {
        Object value = get(index);

        Long longVal = TypeUtils.castToLong(value);
        if (longVal == null) {
            return 0L;
        }

        return longVal;
    }

    public Float getFloat(int index) {
        Object value = get(index);

        return TypeUtils.castToFloat(value);
    }

    public float getFloatValue(int index) {
        Object value = get(index);

        Float floatValue = TypeUtils.castToFloat(value);
        if (floatValue == null) {
            return 0F;
        }

        return floatValue;
    }

    public Double getDouble(int index) {
        Object value = get(index);

        return TypeUtils.castToDouble(value);
    }

    public double getDoubleValue(int index) {
        Object value = get(index);

        Double doubleValue = TypeUtils.castToDouble(value);
        if (doubleValue == null) {
            return 0D;
        }
        return doubleValue;
    }

    public BigDecimal getBigDecimal(int index) {
        Object value = get(index);

        return TypeUtils.castToBigDecimal(value);
    }

    public BigInteger getBigInteger(int index) {
        Object value = get(index);

        return TypeUtils.castToBigInteger(value);
    }

    public String getString(int index) {
        Object value = get(index);

        return TypeUtils.castToString(value);
    }


    public <T> T getObject(int index, Class<T> clazz) {
        Object obj = list.get(index);
        if (obj.getClass() == clazz) {
            return (T) obj;
        }
        if (obj instanceof String) {
            return parseObject((String) obj, clazz);
        }
        return parseObject(toJSONString(obj), clazz);
    }

    public String toJSONString() {
        return toJSONString(list);
    }
    @Override
    public String toString() {
        return toJSONString();
    }
    @Override
    public int size() {
        return list.size();
    }

    @Override
    public boolean isEmpty() {
        return list.isEmpty();
    }

    @Override
    public boolean contains(Object o) {
        return list.contains(o);
    }

    @Override
    public Iterator iterator() {
        return list.iterator();
    }

    @Override
    public Object[] toArray() {
        return list.toArray();
    }

    @Override
    public Object[] toArray(Object[] a) {
        return list.toArray(a);
    }

    @Override
    public boolean add(Object o) {
        return list.add(o);
    }

    @Override
    public boolean remove(Object o) {
        return list.remove(o);
    }

    @Override
    public boolean containsAll(Collection c) {
        return list.containsAll(c);
    }

    @Override
    public boolean addAll(Collection c) {
        return list.addAll(c);
    }

    @Override
    public boolean addAll(int index, Collection c) {
        return list.addAll(index, c);
    }

    @Override
    public boolean removeAll(Collection c) {
        return list.removeAll(c);
    }

    @Override
    public boolean retainAll(Collection c) {
        return list.retainAll(c);
    }

    @Override
    public void clear() {
        list.clear();
    }

    @Override
    public boolean equals(Object o) {
        return list.equals(o);
    }

    @Override
    public int hashCode() {
        return list.hashCode();
    }

    @Override
    public Object get(int index) {
        return list.get(index);
    }

    @Override
    public Object set(int index, Object element) {
        return list.set(index, element);
    }

    @Override
    public void add(int index, Object element) {
        list.add(index, element);
    }

    @Override
    public Object remove(int index) {
        return list.remove(index);
    }

    @Override
    public int indexOf(Object o) {
        return list.indexOf(o);
    }

    @Override
    public int lastIndexOf(Object o) {
        return list.lastIndexOf(o);
    }

    @Override
    public ListIterator listIterator() {
        return list.listIterator();
    }

    @Override
    public ListIterator listIterator(int index) {
        return list.listIterator(index);
    }

    @Override
    public List subList(int fromIndex, int toIndex) {
        return list.subList(fromIndex, toIndex);
    }

    @Override
    public void replaceAll(UnaryOperator operator) {
        list.replaceAll(operator);
    }

    @Override
    public void sort(Comparator c) {
        list.sort(c);
    }

    @Override
    public Spliterator spliterator() {
        return list.spliterator();
    }
}
