package com.liyu.fastjson.support;

import com.fasterxml.jackson.databind.introspect.Annotated;
import com.fasterxml.jackson.databind.introspect.AnnotatedField;
import com.fasterxml.jackson.databind.introspect.AnnotatedMember;
import com.fasterxml.jackson.databind.introspect.JacksonAnnotationIntrospector;
import com.liyu.fastjson.annotation.JSONField;

/**
 * Jackson 注解内省器，将 fastjson {@link JSONField} 映射为 Jackson 属性名与读写可见性。
 * <p>
 * 支持字段重命名（{@link JSONField#name()}）以及序列化/反序列化开关
 * （{@link JSONField#serialize()} / {@link JSONField#deserialize()}）。
 * </p>
 */
public class FastjsonAnnotationIntrospector extends JacksonAnnotationIntrospector {

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean hasIgnoreMarker(AnnotatedMember member) {
        JSONField jsonField = findJSONField(member);
        if (jsonField != null && member instanceof AnnotatedField) {
            return !jsonField.serialize() && !jsonField.deserialize();
        }
        return super.hasIgnoreMarker(member);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public com.fasterxml.jackson.annotation.JsonProperty.Access findPropertyAccess(Annotated member) {
        JSONField jsonField = findJSONField(member);
        if (jsonField != null) {
            if (!jsonField.serialize() && !jsonField.deserialize()) {
                return com.fasterxml.jackson.annotation.JsonProperty.Access.READ_ONLY;
            }
            if (!jsonField.serialize()) {
                return com.fasterxml.jackson.annotation.JsonProperty.Access.READ_ONLY;
            }
            if (!jsonField.deserialize()) {
                return com.fasterxml.jackson.annotation.JsonProperty.Access.WRITE_ONLY;
            }
        }
        return super.findPropertyAccess(member);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public com.fasterxml.jackson.databind.PropertyName findNameForSerialization(Annotated member) {
        JSONField jsonField = findJSONField(member);
        if (jsonField != null && jsonField.name().length() > 0) {
            return com.fasterxml.jackson.databind.PropertyName.construct(jsonField.name());
        }
        return super.findNameForSerialization(member);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public com.fasterxml.jackson.databind.PropertyName findNameForDeserialization(Annotated member) {
        JSONField jsonField = findJSONField(member);
        if (jsonField != null && jsonField.name().length() > 0) {
            return com.fasterxml.jackson.databind.PropertyName.construct(jsonField.name());
        }
        return super.findNameForDeserialization(member);
    }

    private JSONField findJSONField(Annotated member) {
        return member.getAnnotation(JSONField.class);
    }
}
