package com.android.common.http;

import android.content.Context;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.*;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.HttpContext;

import java.lang.ref.WeakReference;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * User: Gyb
 * Date: 13-9-27
 * Time: 上午11:16
 */
public final class AsyncHTTPClient extends HttpBase {


    public void get(String url, HttpResponseCallback callback) {
        get(url, null, callback);
    }

    public void get(String url, String contentType, HttpResponseCallback callback) {
        get(url, contentType, null, callback);
    }

    public void get(String url, String contentType, Context context, HttpResponseCallback callback) {
        get(url, contentType, context, null, callback);
    }

    public void get(String url, String contentType, Context context, Header[] headers, HttpResponseCallback callback) {
        final HttpGet request = new HttpGet(url);
        if (headers != null) {
            request.setHeaders(headers);
        }
        sendRequest(getHttpClient(), getHttpContext(), request, contentType, callback, context);
    }

    public void post(String url, HttpResponseCallback callback) {
        post(url, null, callback);
    }

    public void post(String url, String contentType, HttpResponseCallback callback) {
        post(url, contentType, null, callback);
    }

    public void post(String url, String contentType, Context context, HttpResponseCallback callback) {
        post(url, contentType, null, context, callback);
    }

    public void post(String url, String contentType, Header[] headers, Context context, HttpResponseCallback callback) {
        post(url, contentType, headers, null, context, callback);
    }


    public void post(String url, String contentType, Header[] headers, HttpEntity entity, Context context, HttpResponseCallback callback) {
        final HttpEntityEnclosingRequestBase request = addEntity(new HttpPost(url), entity);
        if (headers != null) {
            request.setHeaders(headers);
        }
        sendRequest(getHttpClient(), getHttpContext(), request, contentType, callback, context);
    }

    public void delete(String url, HttpResponseCallback callback) {
        delete(url, null, callback);
    }

    public void delete(String url, String contentType, HttpResponseCallback callback) {
        delete(url, contentType, null, callback);
    }

    public void delete(String url, String contentType, Context context, HttpResponseCallback callback) {
        delete(url, contentType, null, context, callback);
    }

    public void delete(String url, String contentType, Header[] headers, Context context, HttpResponseCallback callback) {
        final HttpDelete request = new HttpDelete(url);
        if (headers != null) {
            request.setHeaders(headers);
        }
        sendRequest(getHttpClient(), getHttpContext(), request, contentType, callback, context);
    }


    public void put(String url, HttpResponseCallback callback) {
        put(url, null, callback);
    }

    public void put(String url, String contentType, HttpResponseCallback callback) {
        put(url, contentType, null, callback);
    }

    public void put(String url, String contentType, Context context, HttpResponseCallback callback) {
        put(url, contentType, null, context, callback);
    }

    public void put(String url, String contentType, HttpEntity entity, Context context, HttpResponseCallback callback) {
        put(url, contentType, null, entity, context, callback);
    }

    public void put(String url, String contentType, Header[] headers, HttpEntity entity, Context context, HttpResponseCallback callback) {
        final HttpEntityEnclosingRequestBase request = addEntity(new HttpPut(url), entity);
        if (headers != null) {
            request.setHeaders(headers);
        }
        sendRequest(getHttpClient(), getHttpContext(), request, contentType, callback, context);
    }



    protected void sendRequest(DefaultHttpClient client,
                               HttpContext httpContext,
                               HttpUriRequest uriRequest,
                               String contentType,
                               HttpResponseCallback callback,
                               Context context) {
        if (contentType != null) {
            uriRequest.addHeader("Content-Type", contentType);
        }

        Future<?> request = getThreadPool().submit(new HttpRequestHandler(client, httpContext, uriRequest, new HttpResponseHandler(callback)));

        if (context != null) {
            List<WeakReference<Future<?>>> requestList = getRequestMap().get(context);
            if (requestList == null) {
                requestList = new LinkedList<WeakReference<Future<?>>>();
                getRequestMap().put(context, requestList);
            }
            requestList.add(new WeakReference<Future<?>>(request));
        }

    }

}
