package com.android.common.bind.binder;

import android.content.Context;
import android.content.res.Resources;
import com.android.common.bind.annotation.Resource;

import java.lang.reflect.Field;

/**
 * User: Gyb
 * Date: 13-9-26
 * Time: 下午5:57
 */
public class ResourceBinder {

    public static void bind(Context context) throws IllegalAccessException {
        Resources resources = context.getResources();
        Class<?> cl = context.getClass();
        Field[] fields = cl.getDeclaredFields();
        for (Field f : fields) {
            Resource resource = f.getAnnotation(Resource.class);
            if (resource != null) {
                f.setAccessible(true);
                if (resource.type() == Resource.ResourceType.STRING) {
                    f.set(context, resources.getString(resource.id()));
                } else if (resource.type() == Resource.ResourceType.BOOLEAN) {
                    f.set(context, resources.getBoolean(resource.id()));
                } else if (resource.type() == Resource.ResourceType.INT) {
                    f.set(context, resources.getInteger(resource.id()));
                } else if (resource.type() == Resource.ResourceType.STRING_ARRAY) {
                    f.set(context, resources.getStringArray(resource.id()));
                } else if (resource.type() == Resource.ResourceType.INT_ARRAY) {
                    f.set(context, resources.getIntArray(resource.id()));
                } else if (resource.type() == Resource.ResourceType.COLOR) {
                    f.set(context, resources.getColor(resource.id()));
                } else if (resource.type() == Resource.ResourceType.DIMEN) {
                    f.set(context, resources.getDimension(resource.id()));
                } else if (resource.type() == Resource.ResourceType.DRAWABLE) {
                    f.set(context, resources.getDrawable(resource.id()));
                } else if (resource.type() == Resource.ResourceType.TEXT) {
                    f.set(context, resources.getText(resource.id()));
                }
            }
        }
    }
}
