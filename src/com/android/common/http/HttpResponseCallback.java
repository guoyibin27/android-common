package com.android.common.http;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 * User: Gyb
 * Date: 13-9-27
 * Time: 下午12:16
 */
public interface HttpResponseCallback {

    public void onFailure(Throwable tr);

    public void onStart();

    public void onFinish();

    public void onSuccess(int statusCode, Header[] headers, String response);

    public void onSuccess(int statusCode, Header[] headers, JSONObject response);

    public void onSuccess(int statusCode, Header[] headers, JSONArray response);

    public void onSuccess(int statusCode, Header[] headers, byte[] response);
}
