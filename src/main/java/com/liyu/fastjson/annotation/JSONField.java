package com.liyu.fastjson.annotation;

import com.liyu.fastjson.parser.Feature;
import com.liyu.fastjson.serializer.SerializerFeature;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 标注 JavaBean 字段或 getter/setter 的 JSON 序列化与反序列化行为。
 * <p>
 * 对应 fastjson {@code @JSONField}，底层通过 Jackson 注解内省实现字段重命名及读写控制。
 * </p>
 *
 * <pre>{@code
 * public class User {
 *     @JSONField(name = "user_name")
 *     private String userName;
 *
 *     @JSONField(serialize = false)
 *     private String password;
 * }
 * }</pre>
 *
 * @see com.liyu.fastjson.support.FastjsonAnnotationIntrospector
 */
@Target({ElementType.METHOD, ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface JSONField {

    /**
     * JSON 字段名；为空时使用 Java 属性名。
     */
    String name() default "";

    /**
     * 日期等类型的格式化模板，如 {@code yyyy-MM-dd HH:mm:ss}。
     */
    String format() default "";

    /**
     * 是否参与序列化（写出 JSON）。
     */
    boolean serialize() default true;

    /**
     * 是否参与反序列化（读取 JSON）。
     */
    boolean deserialize() default true;

    /**
     * 字段输出顺序，值越小越靠前。
     */
    int ordinal() default 0;

    /**
     * 该字段序列化时启用的特性（保留兼容，部分特性由 Jackson 统一处理）。
     */
    SerializerFeature[] serialzeFeatures() default {};

    /**
     * 该字段反序列化时启用的解析特性（保留兼容）。
     */
    Feature[] parseFeatures() default {};

    /**
     * 字段标签，用于分组序列化（保留兼容）。
     */
    String label() default "";

    /**
     * 字段值是否已是 JSON 字符串，无需再次转义（保留兼容）。
     */
    boolean jsonDirect() default false;

    /**
     * 自定义序列化器类型（保留兼容，暂未实现）。
     */
    Class<?> serializeUsing() default Void.class;

    /**
     * 自定义反序列化器类型（保留兼容，暂未实现）。
     */
    Class<?> deserializeUsing() default Void.class;

    /**
     * 反序列化时的字段别名列表。
     */
    String[] alternateNames() default {};
}
