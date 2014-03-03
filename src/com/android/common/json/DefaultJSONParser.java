package com.android.common.json;

import com.android.common.utils.StringUtils;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * User: Sylar
 * Date: 13-10-11
 * Time: 下午3:48
 */
public class DefaultJSONParser<T> implements JSONParser {
    private String responseMessage;
    private boolean responseCode;

    public void parse(String jsonString, JSONHandler handler) throws JSONException {
        if (StringUtils.isEmpty(jsonString)) {
            return;
        }
        JSONObject object = new JSONObject(jsonString);
        responseMessage = object.getString("responseMessage");
        responseCode = object.getBoolean("success");
        if (responseCode) {
            if (handler instanceof JSONObjectHandler) {
                ((JSONObjectHandler) handler).parse(object.optJSONObject("data"));
            } else if (handler instanceof JSONArrayHandler) {
                ((JSONArrayHandler) handler).parse(object.optJSONArray("data"));
            }
        }
    }

    public String getResponseMessage() {
        return responseMessage;
    }

    public boolean isResponseCode() {
        return responseCode;
    }
}
