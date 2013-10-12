package com.android.common.bind.binder;

import android.app.Activity;
import android.view.View;
import android.widget.AdapterView;
import com.android.common.bind.annotation.Component;
import com.android.common.bind.annotation.Event;
import com.android.common.bind.constant.EventName;
import com.android.common.bind.binder.EventFactory;
import com.android.common.utils.StringUtils;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * User: Gyb
 * Date: 13-9-26
 * Time: 下午6:16
 */
public class EventBinder {

    public static void bind(final Activity activity) throws NoSuchFieldException, IllegalAccessException {
        Class<?> c = activity.getClass();
        Method[] methods = c.getDeclaredMethods();
        for (final Method method : methods) {
            Event e = method.getAnnotation(Event.class);
            if (e != null) {
                String source = e.source();
                Field field = c.getDeclaredField(source);
                if (field == null) {
                    throw new NoSuchFieldException(source + "not found");
                } else {
                    field.setAccessible(true);
                    registerEvent(method, field, activity, e);
                }
            }
        }
    }

    private static void registerEvent(Method method, Field field, Object object, Event event) throws IllegalAccessException {
        Object o = field.get(object);
        EventFactory factory = EventFactory.getEventFactory();
        if (o instanceof View) {
            View view = (View) o;
            EventName eventName = event.name();
            if (eventName == EventName.onClick) {
                view.setOnClickListener(factory.getOnClickListener(object, method.getName()));
            }
            if (eventName == EventName.onCreateContextMenu) {
                view.setOnCreateContextMenuListener(factory.getOnCreateContextMenuListener(object, method.getName()));
            }

            if (eventName == EventName.onLongClick) {
                view.setOnLongClickListener(factory.getOnLongClickListener(object, method.getName()));
            }

            if (eventName == EventName.onTouch) {
                view.setOnTouchListener(factory.getOnTouchListener(object, method.getName()));
            }
        } else if (o instanceof AdapterView<?>) {
            AdapterView view = (AdapterView) o;
            EventName eventName = event.name();
            if (eventName == EventName.onItemClick) {
                view.setOnItemClickListener(factory.getOnItemClickListener(object, method.getName()));
            }

            if (eventName == EventName.onItemLongClick) {
                view.setOnItemLongClickListener(factory.getOnItemLongClickListener(object, method.getName()));
            }
        }
    }

}
