package com.android.common;

import android.app.Activity;
import com.android.common.bind.binder.ComponentBinder;
import com.android.common.bind.binder.EventBinder;
import com.android.common.bind.binder.ResourceBinder;

/**
 * User: Gyb
 * Date: 13-9-26
 * Time: 下午2:13
 */
public final class UIBinder {

    public static void bind(Activity activity) throws Exception {
        ComponentBinder.bind(activity);
        ResourceBinder.bind(activity);
        EventBinder.bind(activity);
    }

    public static void bind(Activity activity, int layoutResourceId) throws Exception {
        ComponentBinder.bind(activity, layoutResourceId);
        ResourceBinder.bind(activity);
        EventBinder.bind(activity);
    }
}
