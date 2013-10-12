package com.android.common.http;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.HttpRequestRetryHandler;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.AbstractHttpClient;
import org.apache.http.protocol.HttpContext;

import java.io.IOException;
import java.net.ConnectException;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.nio.channels.ConnectionPendingException;
import java.util.concurrent.TimeoutException;

/**
 * User: Gyb
 * Date: 13-9-27
 * Time: 上午11:29
 */
public class HttpRequestHandler implements Runnable {
    private final AbstractHttpClient httpClient;
    private final HttpContext httpContext;
    private final HttpUriRequest request;
    private final HttpResponseHandler handler;

    private int retryCount = 0;

    public HttpRequestHandler(AbstractHttpClient httpClient, HttpContext httpContext, HttpUriRequest request, HttpResponseHandler handler) {
        this.httpClient = httpClient;
        this.httpContext = httpContext;
        this.request = request;
        this.handler = handler;
        if (handler == null) {
            throw new IllegalArgumentException("HttpResponseHandler was null");
        }
    }

    @Override
    public void run() {
        try {
            handler.sendStartMessage();
            requestWithRetries();
            handler.sendFinishMessage();
        } catch (Exception e) {
            handler.sendFinishMessage();
            handler.sendFailureMessage(e);
        }
    }

    private void requestWithRetries() throws ConnectException {
        boolean retry = true;
        HttpRequestRetryHandler retryHandler = httpClient.getHttpRequestRetryHandler();
        IOException exception = null;

        while (retry) {
            try {
                makeRequest();
                return;
            } catch (UnknownHostException e) {
                handler.sendFailureMessage(e);
                return;
            } catch (SocketTimeoutException e) {
                handler.sendFailureMessage(e);
                return;
            } catch (SocketException e) {
                handler.sendFailureMessage(e);
                return;
            } catch (IOException e) {
                e.printStackTrace();
                exception = e;
                retry = retryHandler.retryRequest(exception, ++retryCount, httpContext);
            }
        }

        ConnectException ex = new ConnectException();
        ex.initCause(exception);
        throw ex;
    }

    private void makeRequest() throws IOException {
        if (!Thread.currentThread().isInterrupted()) {
            try {
                HttpResponse response = httpClient.execute(request, httpContext);
                if (!Thread.currentThread().isInterrupted()) {
                    handler.sendResponse(response);
                }
            } catch (IOException e) {
                if (!Thread.currentThread().isInterrupted()) {
                    handler.sendFailureMessage(e);
                    throw e;
                }
            }
        }
    }
}
