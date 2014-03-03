package com.android.common.http;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.methods.*;
import org.apache.http.entity.BufferedHttpEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;

import java.io.IOException;

/**
 * User: Gyb
 * Date: 13-9-27
 * Time: 下午2:38
 */
public final class SyncHTTPClient extends HttpBase {

    private int responseCode;
    private Object responseBody;
    private Exception exception;
    private ResultType resultType;

    public int getResponseCode() {
        return this.responseCode;
    }

    public Object getResponseBody() {
        return this.responseBody;
    }

    private HttpResponseHandler responseHandler = new HttpResponseHandler(null) {
        @Override
        public void sendResponse(HttpResponse response) {
            StatusLine statusLine = response.getStatusLine();
            responseCode = statusLine.getStatusCode();
            try {
                HttpEntity entity;
                HttpEntity temp = response.getEntity();
                if (temp != null) {
                    entity = new BufferedHttpEntity(temp);
                    if (resultType == ResultType.BINARY) {
                        responseBody = EntityUtils.toByteArray(entity);
                    } else if (resultType == ResultType.JSON) {
                        parseResponseBodyToJSON(EntityUtils.toString(entity, "UTF-8"));
                    } else {
                        responseBody = EntityUtils.toString(entity, "UTF-8");
                    }

                }
            } catch (Exception ex) {
                exception = ex;
            }
        }
    };

    public Throwable getError() {
        return exception;
    }


    public void get(String url, ResultType resultType) {
        get(url, null, resultType);
    }

    public void get(String url, String contentType, ResultType resultType) {
        get(url, contentType, null, resultType);
    }

    public void get(String url, String contentType, Header[] headers, ResultType resultType) {
        this.resultType = resultType;
        final HttpGet request = new HttpGet(url);
        if (headers != null) {
            request.setHeaders(headers);
        }
        sendRequest(getHttpClient(), getHttpContext(), request, contentType);
    }


    public void post(String url, ResultType resultType) {
        post(url, null, resultType);
    }

    public void post(String url, String contentType, ResultType resultType) {
        post(url, contentType, null, resultType);
    }

    public void post(String url, String contentType, Header[] headers, ResultType resultType) {
        post(url, contentType, headers, null, resultType);
    }

    public void post(String url, String contentType, Header[] headers, HttpEntity entity, ResultType resultType) {
        this.resultType = resultType;
        final HttpEntityEnclosingRequestBase request = addEntity(new HttpPost(url), entity);
        if (headers != null) {
            request.setHeaders(headers);
        }
        sendRequest(getHttpClient(), getHttpContext(), request, contentType);
    }

    public void put(String url, ResultType resultType) {
        put(url, null, resultType);
    }

    public void put(String url, String contentType, ResultType resultType) {
        put(url, contentType, null, resultType);
    }

    public void put(String url, String contentType, Header[] headers, ResultType resultType) {
        put(url, contentType, headers, null, resultType);
    }

    public void put(String url, String contentType, Header[] headers, HttpEntity entity, ResultType resultType) {
        this.resultType = resultType;
        final HttpEntityEnclosingRequestBase request = addEntity(new HttpPut(url), entity);
        if (headers != null) request.setHeaders(headers);
        sendRequest(getHttpClient(), getHttpContext(), request, contentType);
    }

    public void delete(String url, ResultType resultType) {
        delete(url, null, resultType);
    }

    public void delete(String url, String contentType, ResultType resultType) {
        delete(url, contentType, null, resultType);
    }

    public void delete(String url, String contentType, Header[] headers, ResultType resultType) {
        this.resultType = resultType;
        final HttpDelete request = new HttpDelete(url);
        if (headers != null) request.setHeaders(headers);
        sendRequest(getHttpClient(), getHttpContext(), request, contentType);
    }


    private void sendRequest(DefaultHttpClient client,
                             HttpContext httpContext,
                             HttpUriRequest uriRequest,
                             String contentType) {
        if (contentType != null) {
            uriRequest.addHeader("Content-Type", contentType);
        }

        try {
            HttpResponse response = client.execute(uriRequest, httpContext);
            responseHandler.sendResponse(response);
        } catch (IOException e) {
            e.printStackTrace();
            responseHandler.sendFailureMessage(e);
        }

    }


}
