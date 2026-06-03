package com.liyu.fastjson;

import java.io.IOException;
import java.io.Writer;

/**
 * 表示当前对象可流式写出 JSON 内容的标记接口。
 * <p>
 * 对应 fastjson 的 {@code com.alibaba.fastjson.JSONStreamAware}，
 * 适用于需要将 JSON 直接写入 {@link Writer} 的场景，避免中间字符串分配。
 * </p>
 */
public interface JSONStreamAware {

    /**
     * 将 JSON 内容写入指定 {@link Writer}。
     *
     * @param out 输出目标，不可为 {@code null}
     * @throws IOException 写入失败时抛出
     */
    void writeJSONString(Writer out) throws IOException;
}
