package com.android.common.json;

import org.json.JSONArray;

import java.util.List;

/**
 * User: Sylar
 * Date: 13-10-11
 * Time: 下午4:01
 */
public interface JSONArrayHandler<T> extends JSONHandler {
    public List<T> parse(JSONArray array);
}
