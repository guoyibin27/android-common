package com.android.common.bind.binder;

import android.view.ContextMenu;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import com.android.common.utils.Logger;

import java.lang.reflect.Method;

/**
 * User: Gyb
 * Date: 13-9-26
 * Time: 下午5:09
 */
public final class EventFactory {
    private static Logger log = Logger.getLogger(EventFactory.class);

    private static EventFactory factory = null;

    private EventFactory() {

    }

    public static synchronized EventFactory getEventFactory() {
        return factory == null ? new EventFactory() : factory;
    }

    public View.OnClickListener getOnClickListener(final Object object, final String method) {
        return new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                invoke(object, method, new Class<?>[]{View.class}, new Object[]{view});
            }
        };
    }

    public View.OnCreateContextMenuListener getOnCreateContextMenuListener(final Object obj, final String method) {
        return new View.OnCreateContextMenuListener() {

            @Override
            public void onCreateContextMenu(ContextMenu contextMenu, View view, ContextMenu.ContextMenuInfo contextMenuInfo) {
                invoke(obj, method,
                        new Class<?>[]{ContextMenu.class, View.class, ContextMenu.ContextMenuInfo.class},
                        new Object[]{contextMenu, view, contextMenuInfo});
            }
        };
    }

    public View.OnLongClickListener getOnLongClickListener(final Object obj, final String method) {
        return new View.OnLongClickListener() {

            @Override
            public boolean onLongClick(View view) {
                return invoke(obj, method, new Class<?>[]{View.class}, new Object[]{view});
            }
        };
    }

    public View.OnTouchListener getOnTouchListener(final Object obj, final String method) {
        return new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                return invoke(obj, method, new Class<?>[]{View.class, MotionEvent.class}, new Object[]{view, motionEvent});
            }
        };
    }

    public AdapterView.OnItemClickListener getOnItemClickListener(final Object obj, final String method) {
        return new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                invoke(obj, method, new Class<?>[]{AdapterView.class, View.class, int.class, long.class},
                        new Object[]{adapterView, view, i, l});
            }
        };
    }

    public AdapterView.OnItemLongClickListener getOnItemLongClickListener(final Object obj, final String method) {
        return new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                return invoke(obj, method,
                        new Class<?>[]{AdapterView.class, View.class, int.class, long.class},
                        new Object[]{adapterView, view, i, l});
            }
        };
    }

    private boolean invoke(Object object, String method, Class<?>[] classes, Object[] params) {
        if (object == null) {
            return false;
        }

        Class<?> clazz = object.getClass();
        try {
            Method m = clazz.getDeclaredMethod(method, classes);
            if (m != null) {
                m.invoke(object, params);
                return true;
            } else
                log.warn("no such method found " + method);
        } catch (Exception e) {
            log.warn(e.getMessage(), e);
        }
        return false;
    }
}
