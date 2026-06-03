package com.liyu.fastjson;

/**
 * JSON 解析或序列化过程中抛出的运行时异常。
 * <p>
 * 对应阿里巴巴 fastjson 的 {@code com.alibaba.fastjson.JSONException}，
 * 在底层 Jackson 抛出 {@link java.io.IOException} 等受检异常时，
 * 本库会统一包装为 {@code JSONException} 抛出。
 * </p>
 *
 * @see JSON
 */
public class JSONException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    /**
     * 构造不含详细消息的异常。
     */
    public JSONException() {
        super();
    }

    /**
     * 构造带错误消息的异常。
     *
     * @param message 错误描述
     */
    public JSONException(String message) {
        super(message);
    }

    /**
     * 构造带错误消息和原因的异常。
     *
     * @param message 错误描述
     * @param cause   原始异常
     */
    public JSONException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * 构造以原始异常为原因的异常。
     *
     * @param cause 原始异常
     */
    public JSONException(Throwable cause) {
        super(cause);
    }
}
