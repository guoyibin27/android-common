package com.android.common.bind.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * User: Gyb
 * Date: 13-9-26
 * Time: 下午2:14
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(value = {ElementType.FIELD, ElementType.TYPE})
public @interface Component {

    /**
     * @return 资源ID
     */
    public int id();

    /**
     * @return onClick回调方法名
     */
    public String onClick() default "";

    /**
     * @return onCreateContextMenu的回调方法名
     */
    public String onCreateContextMenu() default "";

    /**
     * @return onLongClick的回调方法名
     */
    public String onLongClick() default "";

    /**
     * @return onTouch的回调方法名
     */
    public String onTouch() default "";

    /**
     * @return onItemClick的回调方法名
     */
    public String onItemClick() default "";

    /**
     * @return onItemLongClick回调方法名
     */
    public String onItemLongClick() default "";
}
