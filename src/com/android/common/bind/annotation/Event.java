package com.android.common.bind.annotation;

import com.android.common.bind.constant.EventName;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * User: Gyb
 * Date: 13-9-26
 * Time: 下午2:17
 */

@Retention(RetentionPolicy.RUNTIME)
@Target(value = {ElementType.TYPE, ElementType.METHOD})
public @interface Event {

    /**
     *
     */
    public String source();

    /**
     * @return 绑定事件名称
     */
    public EventName name();
}
