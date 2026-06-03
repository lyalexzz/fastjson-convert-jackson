package com.liyu.fastjson.parser;

/**
 * JSON 解析特性枚举，用于控制反序列化时的宽松程度与行为。
 * <p>
 * 对应 fastjson {@code com.alibaba.fastjson.parser.Feature}，
 * 在 {@link com.liyu.fastjson.JSON#parseObject(String, Feature...)} 等方法中传入。
 * 底层映射为 Jackson {@link com.fasterxml.jackson.core.JsonParser.Feature} 等配置。
 * </p>
 *
 * <p>常用特性示例：</p>
 * <ul>
 *   <li>{@link #AllowComment} — 允许 JSON 中包含行注释或块注释</li>
 *   <li>{@link #AllowSingleQuotes} — 允许使用单引号包裹字符串</li>
 *   <li>{@link #AllowUnQuotedFieldNames} — 允许字段名不加引号</li>
 *   <li>{@link #IgnoreNotMatch} — 忽略 JavaBean 中不存在的 JSON 字段</li>
 * </ul>
 *
 * @see com.liyu.fastjson.JSON#parseObject(String, Feature...)
 * @see com.liyu.fastjson.JSON#parseArray(String, Feature...)
 */
public enum Feature {

    /**
     * 读取结束后自动关闭 InputStream/Reader 来源
     */
    AutoCloseSource,
    /**
     * 允许 JSON 注释
     */
    AllowComment,
    /**
     * 允许未加引号的字段名
     */
    AllowUnQuotedFieldNames,
    /**
     * 允许单引号字符串
     */
    AllowSingleQuotes,
    /**
     * 字符串 intern 优化（保留兼容）
     */
    InternFieldNames,
    /**
     * 允许 ISO-8601 日期格式
     */
    AllowISO8601DateFormat,
    /**
     * 允许尾随逗号
     */
    AllowArbitraryCommas,
    /**
     * 浮点数解析为 BigDecimal
     */
    UseBigDecimal,
    /**
     * 忽略 JavaBean 中不匹配的字段
     */
    IgnoreNotMatch,
    /**
     * 字段快速排序匹配（保留兼容）
     */
    SortFeidFastMatch,
    /**
     * 禁用 ASM 加速（保留兼容）
     */
    DisableASM,
    /**
     * 禁用循环引用检测（保留兼容）
     */
    DisableCircularReferenceDetect,
    /**
     * 字符串字段初始化为空串（保留兼容）
     */
    InitStringFieldAsEmpty,
    /**
     * 数组转 Bean（保留兼容）
     */
    SupportArrayToBean,
    /**
     * 输出字段按字母序排列
     */
    OrderedField,
    /**
     * 禁用特殊键检测（保留兼容）
     */
    DisableSpecialKeyDetect,
    /**
     * 使用 Object[] 存储数组元素（保留兼容）
     */
    UseObjectArray,
    /**
     * 支持非 public 字段（保留兼容）
     */
    SupportNonPublicField,
    /**
     * 忽略 AutoType（保留兼容，本库不支持 AutoType）
     */
    IgnoreAutoType,
    /**
     * 禁用字段智能匹配（保留兼容）
     */
    DisableFieldSmartMatch,
    /**
     * 支持 AutoType（保留兼容，本库不支持 AutoType）
     */
    SupportAutoType,
    /**
     * 非字符串 Map 键序列化为字符串
     */
    NonStringKeyAsString,
    /**
     * 自定义 Map 反序列化器（保留兼容）
     */
    CustomMapDeserializer,
    /**
     * 枚举值不匹配时抛错（保留兼容）
     */
    ErrorOnEnumNotMatch,
    /**
     * 安全模式，禁用 AutoType（保留兼容）
     */
    SafeMode,
    /**
     * 去除字符串字段首尾空白
     */
    TrimStringFieldValue,
    /**
     * 使用原生对象类型（保留兼容）
     */
    UseNativeObject,
    /**
     * 整型解析为 BigInteger（保留兼容）
     */
    UseBigIntegerForInts,
    /**
     * 整型解析为 Long（保留兼容）
     */
    UseLongForInts;

    /**
     * 将多个特性合并为位掩码整数，与 fastjson 行为保持一致。
     *
     * @param features 特性数组，可为 {@code null} 或空
     * @return 位掩码；无特性时返回 {@code 0}
     */
    public static int of(Feature... features) {
        if (features == null || features.length == 0) {
            return 0;
        }
        int value = 0;
        for (Feature feature : features) {
            value |= 1 << feature.ordinal();
        }
        return value;
    }
}
