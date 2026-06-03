package com.liyu.fastjson;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * 泛型类型引用，用于在运行时保留泛型参数信息。
 * <p>
 * 由于 Java 泛型擦除，无法直接通过 {@code List&lt;User&gt;.class} 获取元素类型，
 * 需通过匿名子类捕获泛型参数。用法与 fastjson {@code TypeReference} 完全一致。
 * </p>
 *
 * <pre>{@code
 * List<User> users = JSON.parseObject(json, new TypeReference<List<User>>() {});
 * Map<String, User> map = JSON.parseObject(json, new TypeReference<Map<String, User>>() {});
 * }</pre>
 *
 * @param <T> 被引用的目标类型
 * @see JSON#parseObject(String, TypeReference)
 */
public abstract class TypeReference<T> {

    private final Type type;

    /**
     * 由子类匿名实例化时自动解析泛型参数，请勿直接调用。
     *
     * @throws JSONException 若子类未指定泛型参数
     */
    protected TypeReference() {
        Type superClass = getClass().getGenericSuperclass();
        if (superClass instanceof ParameterizedType) {
            this.type = ((ParameterizedType) superClass).getActualTypeArguments()[0];
        } else {
            throw new JSONException("TypeReference requires type parameter");
        }
    }

    /**
     * 返回捕获到的泛型 {@link Type}。
     *
     * @return 泛型类型，可用于 {@link JSON#parseObject(String, Type)}
     */
    public Type getType() {
        return type;
    }
}
