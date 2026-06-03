package com.liyu.fastjson.support;

import com.liyu.fastjson.JSONArray;
import com.liyu.fastjson.JSONObject;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 类型转换工具，对应 fastjson {@code com.alibaba.fastjson.util.TypeUtils} 的常用方法。
 * <p>
 * 供 {@link JSONObject} 和 {@link JSONArray} 的 {@code getXxx} 方法内部调用。
 * </p>
 */
public final class TypeUtils {

    private TypeUtils() {
    }

    /**
     * 将值转换为字符串。
     *
     * @param value 原始值
     * @return 字符串；{@code value} 为 {@code null} 时返回 {@code null}
     */
    public static String castToString(Object value) {
        if (value == null) {
            return null;
        }
        return String.valueOf(value);
    }

    /**
     * 将值转换为 {@link Integer}。
     *
     * @param value 原始值
     * @return Integer；无法转换时抛出 {@link IllegalArgumentException}
     */
    public static Integer castToInt(Object value) {
        if (value == null) {
            return null;
        }
        if (value instanceof Integer) {
            return (Integer) value;
        }
        if (value instanceof Number) {
            return ((Number) value).intValue();
        }
        if (value instanceof String) {
            String text = ((String) value).trim();
            if (text.length() == 0) {
                return null;
            }
            return Integer.parseInt(text);
        }
        if (value instanceof Boolean) {
            return ((Boolean) value) ? 1 : 0;
        }
        throw new IllegalArgumentException("Can not cast to int, value : " + value);
    }

    /**
     * 将值转换为 int 基本类型，{@code null} 时返回 {@code 0}。
     *
     * @param value 原始值
     * @return int 值
     */
    public static int castToIntValue(Object value) {
        Integer result = castToInt(value);
        return result == null ? 0 : result;
    }

    /**
     * 将值转换为 {@link Long}。
     *
     * @param value 原始值
     * @return Long；无法转换时抛出 {@link IllegalArgumentException}
     */
    public static Long castToLong(Object value) {
        if (value == null) {
            return null;
        }
        if (value instanceof Long) {
            return (Long) value;
        }
        if (value instanceof Number) {
            return ((Number) value).longValue();
        }
        if (value instanceof String) {
            String text = ((String) value).trim();
            if (text.length() == 0) {
                return null;
            }
            return Long.parseLong(text);
        }
        if (value instanceof Boolean) {
            return ((Boolean) value) ? 1L : 0L;
        }
        throw new IllegalArgumentException("Can not cast to long, value : " + value);
    }

    /**
     * 将值转换为 long 基本类型，{@code null} 时返回 {@code 0L}。
     *
     * @param value 原始值
     * @return long 值
     */
    public static long castToLongValue(Object value) {
        Long result = castToLong(value);
        return result == null ? 0L : result;
    }

    /**
     * 将值转换为 {@link Boolean}。
     *
     * @param value 原始值
     * @return Boolean；无法转换时抛出 {@link IllegalArgumentException}
     */
    public static Boolean castToBoolean(Object value) {
        if (value == null) {
            return null;
        }
        if (value instanceof Boolean) {
            return (Boolean) value;
        }
        if (value instanceof Number) {
            return ((Number) value).intValue() != 0;
        }
        if (value instanceof String) {
            String text = ((String) value).trim();
            if (text.length() == 0) {
                return null;
            }
            return "true".equalsIgnoreCase(text) || "1".equals(text);
        }
        throw new IllegalArgumentException("Can not cast to boolean, value : " + value);
    }

    /**
     * 将值转换为 boolean 基本类型，{@code null} 时返回 {@code false}。
     *
     * @param value 原始值
     * @return boolean 值
     */
    public static boolean castToBooleanValue(Object value) {
        Boolean result = castToBoolean(value);
        return result != null && result;
    }

    /**
     * 将值转换为 {@link BigDecimal}。
     *
     * @param value 原始值
     * @return BigDecimal；无法转换时抛出 {@link IllegalArgumentException}
     */
    public static BigDecimal castToBigDecimal(Object value) {
        if (value == null) {
            return null;
        }
        if (value instanceof BigDecimal) {
            return (BigDecimal) value;
        }
        if (value instanceof BigInteger) {
            return new BigDecimal((BigInteger) value);
        }
        if (value instanceof Number) {
            return new BigDecimal(value.toString());
        }
        if (value instanceof String) {
            String text = ((String) value).trim();
            if (text.length() == 0) {
                return null;
            }
            return new BigDecimal(text);
        }
        throw new IllegalArgumentException("Can not cast to BigDecimal, value : " + value);
    }

    /**
     * 将值转换为 {@link Float}。
     *
     * @param value 原始值
     * @return Float；无法转换时抛出 {@link IllegalArgumentException}
     */
    public static Float castToFloat(Object value) {
        if (value == null) {
            return null;
        }
        if (value instanceof Float) {
            return (Float) value;
        }
        if (value instanceof Number) {
            return ((Number) value).floatValue();
        }
        if (value instanceof String) {
            String text = ((String) value).trim();
            if (text.length() == 0) {
                return null;
            }
            return Float.parseFloat(text);
        }
        throw new IllegalArgumentException("Can not cast to float, value : " + value);
    }

    /**
     * 将值转换为 {@link Double}。
     *
     * @param value 原始值
     * @return Double；无法转换时抛出 {@link IllegalArgumentException}
     */
    public static Double castToDouble(Object value) {
        if (value == null) {
            return null;
        }
        if (value instanceof Double) {
            return (Double) value;
        }
        if (value instanceof Number) {
            return ((Number) value).doubleValue();
        }
        if (value instanceof String) {
            String text = ((String) value).trim();
            if (text.length() == 0) {
                return null;
            }
            return Double.parseDouble(text);
        }
        throw new IllegalArgumentException("Can not cast to double, value : " + value);
    }

    /**
     * 将值转换为 double 基本类型，{@code null} 时返回 {@code 0.0}。
     *
     * @param value 原始值
     * @return double 值
     */
    public static double castToDoubleValue(Object value) {
        Double result = castToDouble(value);
        return result == null ? 0D : result;
    }

    /**
     * 将值转换为 {@link Date}。
     *
     * @param value 原始值，支持 Date、时间戳（Number）、日期字符串
     * @return Date；无法转换时抛出 {@link IllegalArgumentException}
     */
    public static Date castToDate(Object value) {
        if (value == null) {
            return null;
        }
        if (value instanceof Date) {
            return (Date) value;
        }
        if (value instanceof Number) {
            return new Date(((Number) value).longValue());
        }
        if (value instanceof String) {
            return com.liyu.fastjson.JSON.parseObject("\"" + value + "\"", Date.class);
        }
        throw new IllegalArgumentException("Can not cast to Date, value : " + value);
    }

    /**
     * 将值转换为 {@link JSONObject}。
     *
     * @param value 原始值，支持 JSONObject、Map、JSON 字符串
     * @return JSONObject；{@code value} 为 {@code null} 时返回 {@code null}
     */
    public static JSONObject castToJSONObject(Object value) {
        if (value == null) {
            return null;
        }
        if (value instanceof JSONObject) {
            return (JSONObject) value;
        }
        if (value instanceof Map) {
            return new JSONObject((Map<String, Object>) value);
        }
        if (value instanceof String) {
            return JSONObject.parseObject((String) value);
        }
        return (JSONObject) com.liyu.fastjson.JSON.toJSON(value);
    }

    /**
     * 将值转换为 {@link JSONArray}。
     *
     * @param value 原始值，支持 JSONArray、List、Collection、JSON 字符串
     * @return JSONArray；{@code value} 为 {@code null} 时返回 {@code null}
     */
    public static JSONArray castToJSONArray(Object value) {
        if (value == null) {
            return null;
        }
        if (value instanceof JSONArray) {
            return (JSONArray) value;
        }
        if (value instanceof List) {
            return new JSONArray((List<Object>) value);
        }
        if (value instanceof Collection) {
            return new JSONArray((Collection<Object>) value);
        }
        if (value instanceof String) {
            return JSONArray.parseArray((String) value);
        }
        return (JSONArray) com.liyu.fastjson.JSON.toJSON(value);
    }
}
