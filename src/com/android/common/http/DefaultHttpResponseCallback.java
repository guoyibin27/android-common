package com.android.common.http;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 * User: Gyb
 * Date: 13-9-28
 * Time: 下午12:54
 */
public class DefaultHttpResponseCallback implements HttpResponseCallback {
    @Override
    public void onFailure(Throwable tr) {
    }

    @Override
    public void onStart() {
    }

    @Override
    public void onFinish() {
    }

    @Override
    public void onSuccess(int statusCode, Header[] headers, String response) {
    }

    @Override
    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
    }

    @Override
    public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
    }

    @Override
    public void onSuccess(int statusCode, Header[] headers, byte[] response) {
    }
}
