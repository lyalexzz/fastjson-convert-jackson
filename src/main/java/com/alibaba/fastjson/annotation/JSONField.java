package com.alibaba.fastjson.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author liYu
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.FIELD, ElementType.PARAMETER})
public @interface JSONField {
    /**
     * 配置序列化和反序列化的顺序
     *
     * @return int
     */
    int ordinal() default 0;

    /**
     * 配置字段的名称
     *
     * @return String
     */
    String name() default "";

    /**
     * 配置字段的格式
     *
     * @return String
     */
    String format() default "";

    /**
     * 默认值
     *
     * @return Class<?>
     */
    String defaultValue() default "";
}
