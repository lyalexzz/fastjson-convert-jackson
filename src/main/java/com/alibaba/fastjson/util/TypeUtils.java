package com.alibaba.fastjson.util;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Iterator;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author LiYu
 * @ClassName TypeUtils.java
 * @Description 类型工具
 * @createTime 2024年05月30日 13:02:00
 */
public class TypeUtils {
    private static final Pattern NUMBER_WITH_TRAILING_ZEROS_PATTERN = Pattern.compile("(.+?)(?:\\.0+)?$");

    /**
     * 转换为字符串
     *
     * @param value 值
     * @return 字符串
     */
    public static String castToString(Object value) {
        if (value == null) {
            return null;
        }
        return value.toString();
    }

    /**
     * 转换为byte
     *
     * @param value 值
     * @return byte
     */
    public static Byte castToByte(Object value) {
        if (value == null) {
            return null;
        }

        if (value instanceof BigDecimal) {
            return byteValue((BigDecimal) value);
        }

        if (value instanceof Number) {
            return ((Number) value).byteValue();
        }

        if (value instanceof String) {
            String strVal = (String) value;
            if (JsonUtil.isEmpty(strVal)) {
                return null;
            }
            return Byte.parseByte(strVal);
        }

        if (value instanceof Boolean) {
            return (Boolean) value ? (byte) 1 : (byte) 0;
        }

        throw new RuntimeException("转换为byte失败, value : " + value);
    }

    /**
     * 转换为char
     *
     * @param value 值
     * @return char
     */
    public static Character castToChar(Object value) {
        if (value == null) {
            return null;
        }
        if (value instanceof Character) {
            return (Character) value;
        }
        if (value instanceof String) {
            String strVal = (String) value;
            if (strVal.isEmpty()) {
                return null;
            }
            if (strVal.length() != 1) {
                throw new RuntimeException("转换为char失败, value : " + value);
            }
            return strVal.charAt(0);
        }
        throw new RuntimeException("转换为char失败, value : " + value);
    }

    /**
     * 转换为short
     *
     * @param value 值
     * @return short
     */
    public static Short castToShort(Object value) {
        if (value == null) {
            return null;
        }
        if (value instanceof BigDecimal) {
            return shortValue((BigDecimal) value);
        }
        if (value instanceof Number) {
            return ((Number) value).shortValue();
        }
        if (value instanceof String) {
            String strVal = (String) value;
            if (JsonUtil.isEmpty(strVal)) {
                return null;
            }
            return Short.parseShort(strVal);
        }
        if (value instanceof Boolean) {
            return (Boolean) value ? (short) 1 : (short) 0;
        }
        throw new RuntimeException("转换为short失败, value : " + value);
    }

    /**
     * 转换为BigDecimal
     *
     * @param value 值
     * @return BigDecimal
     */
    public static BigDecimal castToBigDecimal(Object value) {
        if (value == null) {
            return null;
        }
        if (value instanceof Float) {
            if (Float.isNaN((Float) value) || Float.isInfinite((Float) value)) {
                return null;
            }
        } else if (value instanceof Double) {
            if (Double.isNaN((Double) value) || Double.isInfinite((Double) value)) {
                return null;
            }
        } else if (value instanceof BigDecimal) {
            return (BigDecimal) value;
        } else if (value instanceof BigInteger) {
            return new BigDecimal((BigInteger) value);
        } else if (value instanceof Map && ((Map<?, ?>) value).isEmpty()) {
            return null;
        }
        String strVal = value.toString();
        if (JsonUtil.isEmpty(strVal)) {
            return null;
        }
        if (strVal.length() > 65535) {
            throw new RuntimeException("decimal overflow");
        }
        return new BigDecimal(strVal);
    }

    /**
     * 转换为BigInteger
     *
     * @param value 值
     * @return BigInteger
     */
    public static BigInteger castToBigInteger(Object value) {
        if (value == null) {
            return null;
        }
        try {
            if (value instanceof Float) {
                Float floatValue = (Float) value;
                if (!Float.isNaN(floatValue) && !Float.isInfinite(floatValue)) {
                    return BigInteger.valueOf(floatValue.longValue());
                }
            } else if (value instanceof Double) {
                Double doubleValue = (Double) value;
                if (!Double.isNaN(doubleValue) && !Double.isInfinite(doubleValue)) {
                    return BigInteger.valueOf(doubleValue.longValue());
                }
            } else if (value instanceof BigInteger) {
                return (BigInteger) value;
            } else if (value instanceof BigDecimal) {
                BigDecimal decimal = (BigDecimal) value;
                if (decimal.scale() >= -1000 && decimal.scale() <= 1000) {
                    return decimal.toBigInteger();
                }
            } else {
                String strVal = value.toString();
                if (!JsonUtil.isEmpty(strVal)) {
                    if (strVal.length() <= 65535) {
                        return new BigInteger(strVal);
                    } else {
                        throw new ArithmeticException("Decimal overflow");
                    }
                }
            }
        } catch (NumberFormatException ex) {
            throw new IllegalArgumentException("Cannot cast to BigInteger, invalid format or value: " + value, ex);
        }
        return null;
    }

    /**
     * 转换为float
     *
     * @param value 值
     * @return float
     */
    public static Float castToFloat(Object value) {
        if (value == null) {
            return null;
        }
        if (value instanceof Number) {
            return ((Number) value).floatValue();
        }
        if (value instanceof String) {
            String strVal = value.toString();
            if (JsonUtil.isEmpty(strVal)) {
                return null;
            }
            if (strVal.indexOf(',') != -1) {
                strVal = strVal.replaceAll(",", "");
            }
            return Float.parseFloat(strVal);
        }

        if (value instanceof Boolean) {
            return (Boolean) value ? 1F : 0F;
        }

        throw new RuntimeException("can not cast to float, value : " + value);
    }

    /**
     * 转换为double
     *
     * @param value 值
     * @return double
     */
    public static Double castToDouble(Object value) {
        if (value == null) {
            return null;
        }
        if (value instanceof Number) {
            return ((Number) value).doubleValue();
        }
        if (value instanceof String) {
            String strVal = value.toString();
            if (JsonUtil.isEmpty(strVal)) {
                return null;
            }
            if (strVal.indexOf(',') != -1) {
                strVal = strVal.replaceAll(",", "");
            }
            return Double.parseDouble(strVal);
        }
        if (value instanceof Boolean) {
            return (Boolean) value ? 1D : 0D;
        }
        throw new RuntimeException("can not cast to double, value : " + value);
    }

    /**
     * 转换为int
     *
     * @param number 值
     * @return int
     */
    public static long longExtractValue(Number number) {
        if (number instanceof BigDecimal) {
            return ((BigDecimal) number).longValueExact();
        }
        return number.longValue();
    }

    /**
     * 是否为数字
     *
     * @param str 字符串
     * @return boolean
     */
    public static boolean isNumber(String str) {
        for (int i = 0; i < str.length(); ++i) {
            char ch = str.charAt(i);
            if (ch == '+' || ch == '-') {
                if (i != 0) {
                    return false;
                }
            } else if (ch < '0' || ch > '9') {
                return false;
            }
        }
        return true;
    }

    /**
     * 转换为long
     *
     * @param value 值
     * @return long
     */
    public static Long castToLong(Object value) {
        if (value == null) {
            return null;
        }
        if (value instanceof BigDecimal) {
            return ((BigDecimal) value).longValue();
        }
        if (value instanceof Number) {
            return ((Number) value).longValue();
        }
        if (value instanceof String) {
            String strVal = (String) value;
            if (JsonUtil.isEmpty(strVal)) {
                return null;
            }
            strVal = strVal.replace(",", "");
            return Long.parseLong(strVal);
        }
        if (value instanceof Map) {
            Map<?, ?> map = (Map<?, ?>) value;
            // 这里的逻辑保持不变，因为看起来是处理特定结构的Map
            if (map.size() == 2 && map.containsKey("andIncrement") && map.containsKey("andDecrement")) {
                Iterator<?> iter = map.values().iterator();
                iter.next();
                Object value2 = iter.next();
                return castToLong(value2);
            }
        }
        if (value instanceof Boolean) {
            return (Boolean) value ? 1L : 0L;
        }
        throw new IllegalArgumentException("Cannot cast to long, value: " + value);
    }

    /**
     * 转换为byte
     *
     * @param decimal 值
     * @return byte
     */
    public static byte byteValue(BigDecimal decimal) {
        if (decimal == null) {
            return 0;
        }

        int scale = decimal.scale();
        if (scale >= -100 && scale <= 100) {
            return decimal.byteValue();
        }

        return decimal.byteValueExact();
    }

    /**
     * 转换为short
     *
     * @param decimal 值
     * @return short
     */
    public static short shortValue(BigDecimal decimal) {
        if (decimal == null) {
            return 0;
        }
        int scale = decimal.scale();
        if (scale >= -100 && scale <= 100) {
            return decimal.shortValue();
        }
        return decimal.shortValueExact();
    }

    /**
     * 转换为int
     *
     * @param decimal 值
     * @return int
     */
    public static int intValue(BigDecimal decimal) {
        if (decimal == null) {
            return 0;
        }
        int scale = decimal.scale();
        if (scale >= -100 && scale <= 100) {
            return decimal.intValue();
        }
        return decimal.intValueExact();
    }

    /**
     * 转换为long
     *
     * @param decimal 值
     * @return long
     */
    public static long longValue(BigDecimal decimal) {
        if (decimal == null) {
            return 0;
        }
        int scale = decimal.scale();
        if (scale >= -100 && scale <= 100) {
            return decimal.longValue();
        }
        return decimal.longValueExact();
    }

    /**
     * 转换为int
     *
     * @param value 值
     * @return int
     */
    public static Integer castToInt(Object value) {
        if (value == null) {
            return null;
        } else if (value instanceof Integer) {
            return (Integer) value;
        } else if (value instanceof BigDecimal) {
            return ((BigDecimal) value).intValue();
        } else if (value instanceof Number) {
            return ((Number) value).intValue();
        } else if (value instanceof String) {
            return Integer.parseInt(normalizeNumberString((String) value));
        } else if (value instanceof Boolean) {
            return (Boolean) value ? 1 : 0;
        } else if (value instanceof Map) {
            Map<?, ?> map = (Map<?, ?>) value;
            if (map.containsKey("andIncrement") && map.containsKey("andDecrement")) {
                return Optional.ofNullable(castToInt(map.get("andIncrement"))).orElseGet(() -> castToInt(map.get("andDecrement")));
            }
        }
        throw new RuntimeException("Cannot cast to int, value: " + value);
    }

    /**
     * 删除字符串中的逗号
     *
     * @param input 值
     * @return String
     */
    private static String normalizeNumberString(String input) {
        return Optional.of(input).filter(str -> !str.isEmpty()).map(str -> str.indexOf(',') != -1 ? str.replace(",", "") : str).map(str -> {
            Matcher matcher = NUMBER_WITH_TRAILING_ZEROS_PATTERN.matcher(str);
            return matcher.find() ? matcher.replaceAll("") : str;
        }).orElseThrow(() -> new NumberFormatException("Invalid number format"));
    }

    /**
     * 转换为boolean
     *
     * @param value 值
     * @return boolean
     */
    public static Boolean castToBoolean(Object value) {
        if (value == null) {
            return null;
        }
        if (value instanceof Boolean) {
            return (Boolean) value;
        }

        if (value instanceof BigDecimal) {
            return intValue((BigDecimal) value) == 1;
        }

        if (value instanceof Number) {
            return ((Number) value).intValue() == 1;
        }

        if (value instanceof String) {
            String strVal = (String) value;
            if (JsonUtil.isEmpty(strVal)) {
                return null;
            }
            if ("true".equalsIgnoreCase(strVal) || "1".equals(strVal)) {
                return Boolean.TRUE;
            }
            if ("false".equalsIgnoreCase(strVal) || "0".equals(strVal)) {
                return Boolean.FALSE;
            }
            if ("Y".equalsIgnoreCase(strVal) || "T".equals(strVal)) {
                return Boolean.TRUE;
            }
            if ("F".equalsIgnoreCase(strVal) || "N".equals(strVal)) {
                return Boolean.FALSE;
            }
        }
        throw new RuntimeException("can not cast to boolean, value : " + value);
    }

}
