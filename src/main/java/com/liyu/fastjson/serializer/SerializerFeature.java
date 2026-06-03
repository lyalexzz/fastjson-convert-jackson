package com.liyu.fastjson.serializer;

/**
 * JSON 序列化特性枚举，用于控制对象写出 JSON 时的格式与行为。
 * <p>
 * 对应 fastjson {@code com.alibaba.fastjson.serializer.SerializerFeature}，
 * 在 {@link com.liyu.fastjson.JSON#toJSONString(Object, SerializerFeature...)} 等方法中传入。
 * </p>
 *
 * <p>常用特性示例：</p>
 * <ul>
 *   <li>{@link #WriteMapNullValue} — 输出值为 {@code null} 的字段</li>
 *   <li>{@link #PrettyFormat} — 格式化缩进输出</li>
 *   <li>{@link #WriteDateUseDateFormat} — 日期按格式字符串输出，而非时间戳</li>
 *   <li>{@link #SortField} — 字段按字母序排列</li>
 * </ul>
 *
 * @see com.liyu.fastjson.JSON#toJSONString(Object, SerializerFeature...)
 */
public enum SerializerFeature {

    /**
     * 字段名加双引号（默认行为）
     */
    QuoteFieldNames,
    /**
     * 使用单引号包裹字符串和字段名
     */
    UseSingleQuotes,
    /**
     * 输出 Map 中值为 null 的键
     */
    WriteMapNullValue,
    /**
     * 枚举序列化调用 toString()
     */
    WriteEnumUsingToString,
    /**
     * 枚举序列化使用 name()
     */
    WriteEnumUsingName,
    /**
     * 日期使用 ISO-8601 格式
     */
    UseISO8601DateFormat,
    /**
     * null 的 List 输出为 []（保留兼容）
     */
    WriteNullListAsEmpty,
    /**
     * null 的 String 输出为 ""（保留兼容）
     */
    WriteNullStringAsEmpty,
    /**
     * null 的 Number 输出为 0（保留兼容）
     */
    WriteNullNumberAsZero,
    /**
     * null 的 Boolean 输出为 false（保留兼容）
     */
    WriteNullBooleanAsFalse,
    /**
     * 跳过 transient 字段
     */
    SkipTransientField,
    /**
     * 字段按字母序排列
     */
    SortField,
    /**
     * 制表符特殊处理（保留兼容）
     */
    WriteTabAsSpecial,
    /**
     * 格式化输出（缩进换行）
     */
    PrettyFormat,
    /**
     * 输出 @type 类型信息（保留兼容，本库不支持）
     */
    WriteClassName,
    /**
     * 禁用循环引用检测（保留兼容）
     */
    DisableCircularReferenceDetect,
    /**
     * 斜杠特殊处理（保留兼容）
     */
    WriteSlashAsSpecial,
    /**
     * 浏览器兼容模式（保留兼容）
     */
    BrowserCompatible,
    /**
     * 日期使用 dateFormat 格式化输出
     */
    WriteDateUseDateFormat,
    /**
     * 根对象不输出类名（保留兼容）
     */
    NotWriteRootClassName,
    /**
     * 禁用特殊字符检查（保留兼容）
     */
    DisableCheckSpecialChar,
    /**
     * Bean 序列化为数组（保留兼容）
     */
    BeanToArray,
    /**
     * 非字符串 Map 键加引号
     */
    WriteNonStringKeyAsString,
    /**
     * 不输出默认值字段（保留兼容）
     */
    NotWriteDefaultValue,
    /**
     * 浏览器安全模式（保留兼容）
     */
    BrowserSecure,
    /**
     * 忽略非字段 getter（保留兼容）
     */
    IgnoreNonFieldGetter,
    /**
     * 非字符串值加引号（保留兼容）
     */
    WriteNonStringValueAsString,
    /**
     * 忽略 getter 异常（保留兼容）
     */
    IgnoreErrorGetter,
    /**
     * BigDecimal 以 plain string 输出
     */
    WriteBigDecimalAsPlain,
    /**
     * Map 字段按字母序排列
     */
    MapSortField;

    /**
     * 将多个特性合并为位掩码整数，与 fastjson 行为保持一致。
     *
     * @param features 特性数组，可为 {@code null} 或空
     * @return 位掩码；无特性时返回 {@code 0}
     */
    public static int of(SerializerFeature... features) {
        if (features == null || features.length == 0) {
            return 0;
        }
        int value = 0;
        for (SerializerFeature feature : features) {
            value |= 1 << feature.ordinal();
        }
        return value;
    }
}
