package com.android.common.json;

import org.json.JSONObject;

/**
 * User: Sylar
 * Date: 13-10-11
 * Time: 下午4:01
 */
public interface JSONObjectHandler<T> extends JSONHandler {

    public T parse(JSONObject object);
}
