package com.android.common.http;

import android.os.SystemClock;
import org.apache.http.NoHttpResponseException;
import org.apache.http.client.HttpRequestRetryHandler;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.protocol.ExecutionContext;
import org.apache.http.protocol.HttpContext;

import java.io.IOException;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeoutException;

/**
 * User: Gyb
 * Date: 13-9-27
 * Time: 上午10:56
 */
public class HttpRetryHandler implements HttpRequestRetryHandler {
    private int maxRetries;
    private static final int RETRY_SLEEP_TIME_MILLIS = 1500;
    private static List<Class<?>> exceptions = new ArrayList<Class<?>>();


    static {
        exceptions.add(NoHttpResponseException.class);
        exceptions.add(UnknownHostException.class);
        exceptions.add(SocketException.class);
    }

    public HttpRetryHandler(int maxRetries) {
        this.maxRetries = maxRetries;
    }

    @Override
    public boolean retryRequest(IOException e, int i, HttpContext httpContext) {
        boolean retry = false;
        Boolean b = (Boolean) httpContext.getAttribute(ExecutionContext.HTTP_REQ_SENT);
        boolean isSent = (b != null && b.booleanValue());
        if (i > maxRetries) {
            retry = false;
        } else if (isInExceptionList(e)) {
            retry = true;
        } else if (!isSent) {
            retry = true;
        }

        if (retry) {
            HttpUriRequest currentReq = (HttpUriRequest) httpContext.getAttribute(ExecutionContext.HTTP_REQUEST);
            String requestType = currentReq.getMethod();
            retry = !requestType.equals("POST");
        }

        if (retry) {
            SystemClock.sleep(RETRY_SLEEP_TIME_MILLIS);
        } else {
            e.printStackTrace();
        }
        return retry;
    }

    private boolean isInExceptionList(Throwable tr) {
        for (Class<?> cl : exceptions) {
            if (cl.isInstance(tr)) {
                return true;
            }
        }
        return false;
    }
}
