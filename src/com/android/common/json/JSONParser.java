package com.android.common.json;

import com.android.common.utils.StringUtils;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * User: sylar
 * Date: 13-10-24
 * Time: 0:28
 */
public interface JSONParser {

    public void parse(String jsonString, JSONHandler handler) throws JSONException;

}
