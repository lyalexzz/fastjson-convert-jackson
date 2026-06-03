package com.liyu.fastjson;

/**
 * 表示当前对象可序列化为 JSON 字符串的标记接口。
 * <p>
 * 实现类通过 {@link #toJSONString()} 提供 JSON 文本表示，
 * 对应 fastjson 的 {@code com.alibaba.fastjson.JSONAware}。
 * </p>
 *
 * @see JSON#toJSONString(Object)
 */
public interface JSONAware {

    /**
     * 将当前对象序列化为 JSON 字符串。
     *
     * @return JSON 字符串；若无法序列化则抛出 {@link JSONException}
     */
    String toJSONString();
}
