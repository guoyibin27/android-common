package com.android.common.bind.binder;

import android.app.Activity;
import android.view.View;
import android.widget.AdapterView;
import com.android.common.bind.annotation.Component;
import com.android.common.bind.binder.EventFactory;
import com.android.common.utils.StringUtils;

import java.lang.reflect.Field;

/**
 * User: Gyb
 * Date: 13-9-26
 * Time: 下午2:24
 */
public class ComponentBinder {

    public static void bind(Activity activity) throws IllegalAccessException {
        Class<?> bindClass = activity.getClass();
        bindComponent(activity, bindClass);
        bindEvent();
    }

    public static void bind(Activity activity, int layoutId) throws IllegalAccessException {
        activity.setContentView(layoutId);
        bind(activity);
    }

    private static void bindComponent(Activity activity, Class<?> bindClass) throws IllegalAccessException {
        Field[] fields = bindClass.getDeclaredFields();
        for (Field field : fields) {
            if (field.isAnnotationPresent(Component.class)) {
                Component id = field.getAnnotation(Component.class);
                if (id != null) {
                    field.setAccessible(true);
                    initComponent(field, activity, id.id());
                    registerEvent(field, activity, id);
                }
            }
        }
    }

    private static void registerEvent(Field field, Object object, Component id) throws IllegalAccessException {
        Object o = field.get(object);
        EventFactory factory = EventFactory.getEventFactory();
        if (o instanceof View) {
            View view = (View) o;
            String method = id.onClick();
            if (!StringUtils.isEmpty(method)) {
                view.setOnClickListener(factory.getOnClickListener(object, method));
            }
            method = id.onCreateContextMenu();
            if (!StringUtils.isEmpty(method)) {
                view.setOnCreateContextMenuListener(factory.getOnCreateContextMenuListener(object, method));
            }

            method = id.onLongClick();
            if (!StringUtils.isEmpty(method)) {
                view.setOnLongClickListener(factory.getOnLongClickListener(object, method));
            }

            method = id.onTouch();
            if (!StringUtils.isEmpty(method)) {
                view.setOnTouchListener(factory.getOnTouchListener(object, method));
            }
        } else if (o instanceof AdapterView<?>) {
            AdapterView view = (AdapterView) o;
            String method = id.onItemClick();
            if (!StringUtils.isEmpty(method)) {
                view.setOnItemClickListener(factory.getOnItemClickListener(object, method));
            }

            method = id.onItemLongClick();
            if (!StringUtils.isEmpty(method)) {
                view.setOnItemLongClickListener(factory.getOnItemLongClickListener(object, method));
            }
        }
    }

    private static void bindEvent() {

    }

    private static void initComponent(Field field, Activity activity, int id) throws IllegalAccessException {
        field.set(activity, activity.findViewById(id));
    }
}
