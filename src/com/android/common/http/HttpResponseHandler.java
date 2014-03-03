package com.android.common.http;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpResponseException;
import org.apache.http.entity.BufferedHttpEntity;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.util.ArrayList;
import java.util.List;

/**
 * User: Gyb
 * Date: 13-9-27
 * Time: 上午10:23
 */
public class HttpResponseHandler {

    private static final int SUCCESS = 1;
    private static final int FAILURE = 2;
    private static final int START = 3;
    private static final int FINISH = 4;

    private Handler handler;
    private final HttpResponseCallback callback;

    public HttpResponseHandler(HttpResponseCallback callback) {
        this.callback = callback;
        if (Looper.myLooper() != null) {
            this.handler = new Handler() {
                @Override
                public void handleMessage(Message msg) {
                    HttpResponseHandler.this.handleMessage(msg);
                }
            };
        }
    }


    private void handleMessage(Message msg) {
        final int what = msg.what;
        switch (what) {
            case SUCCESS:
                handleSuccessMessage(msg);
                break;
            case FAILURE:
                callback.onFailure((Throwable) msg.obj);
                break;
            case START:
                callback.onStart();
                break;
            case FINISH:
                callback.onFinish();
                break;
        }
    }

    private void handleSuccessMessage(Message msg) {
        List response = (List) msg.obj;
        int statusCode = ((Integer) response.get(0)).intValue();
        Header[] headers = (Header[]) response.get(1);
        Object responseBody = response.get(2);
        if (isJsonResponseCallback()) {
            if (responseBody instanceof JSONObject) {
                callback.onSuccess(statusCode, headers, (JSONObject) responseBody);
            } else if (responseBody instanceof JSONArray) {
                callback.onSuccess(statusCode, headers, (JSONArray) responseBody);
            }
        } else if (isBinaryResponseCallback()) {
            callback.onSuccess(statusCode, headers, (byte[]) responseBody);
        } else {
            callback.onSuccess(statusCode, headers, (String) responseBody);
        }
    }

    public void sendStartMessage() {
        handler.sendMessage(handler.obtainMessage(START));
    }

    public void sendFinishMessage() {
        handler.sendMessage(handler.obtainMessage(FINISH));
    }

    public void sendFailureMessage(Throwable tr) {
        handler.sendMessage(handler.obtainMessage(FAILURE, tr));
    }

    public void sendSuccessMessage(int statusCode, Header[] headers, Object responseBody) {
        List response = new ArrayList(3);
        response.add(statusCode);
        response.add(headers);
        response.add(responseBody);
        handler.sendMessage(handler.obtainMessage(SUCCESS, response));
    }


    protected void sendResponse(HttpResponse response) {
        StatusLine statusLine = response.getStatusLine();
        Object responseBody = null;
        try {
            HttpEntity entity;
            HttpEntity temp = response.getEntity();
            if (temp != null) {
                entity = new BufferedHttpEntity(temp);
                if (isJsonResponseCallback()) {
                    String body = EntityUtils.toString(entity, "UTF-8");
                    responseBody = parseResponseBodyToJSON(body);
                } else if (isBinaryResponseCallback()) {
                    responseBody = EntityUtils.toByteArray(entity);
                } else {
                    responseBody = EntityUtils.toString(entity, "UTF-8");
                }
            }

        } catch (Exception ex) {
            sendFailureMessage(ex);
        }

        if (statusLine.getStatusCode() > 300) {
            sendFailureMessage(new HttpResponseException(statusLine.getStatusCode(), statusLine.getReasonPhrase()));
        } else {
            sendSuccessMessage(statusLine.getStatusCode(), response.getAllHeaders(), responseBody);
        }
    }

    protected Object parseResponseBodyToJSON(String responseBody) throws JSONException {
        Object result = null;
        responseBody = responseBody.trim();
        if (responseBody.startsWith("{") || responseBody.startsWith("[")) {
            result = new JSONTokener(responseBody).nextValue();
        }
        if (result == null) {
            result = responseBody;
        }
        return result;
    }

    private boolean isJsonResponseCallback() {
        return callback instanceof JsonResponseCallback;
    }

    private boolean isBinaryResponseCallback() {
        return callback instanceof BinaryResponseCallback;
    }
}
