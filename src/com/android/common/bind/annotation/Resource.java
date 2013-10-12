package com.android.common.bind.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * User: Gyb
 * Date: 13-9-26
 * Time: 下午3:25
 */

@Retention(RetentionPolicy.RUNTIME)
@Target(value = {ElementType.FIELD, ElementType.TYPE})
public @interface Resource {

    /**
     * @return 资源ID
     */
    public int id();

    /**
     * @return 资源类型
     */
    public ResourceType type();

    public enum ResourceType {
        BOOLEAN, STRING, DRAWABLE, STRING_ARRAY, INT, INT_ARRAY, COLOR, TEXT, DIMEN;
    }
}
