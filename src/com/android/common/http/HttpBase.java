package com.android.common.http;

import android.content.Context;
import org.apache.http.*;
import org.apache.http.client.CookieStore;
import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;
import org.apache.http.client.params.HttpClientParams;
import org.apache.http.client.protocol.ClientContext;
import org.apache.http.conn.params.ConnManagerParams;
import org.apache.http.conn.params.ConnPerRouteBean;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.entity.HttpEntityWrapper;
import org.apache.http.impl.client.AbstractHttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.apache.http.protocol.SyncBasicHttpContext;

import javax.xml.xpath.XPathFunctionResolver;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.zip.GZIPInputStream;

/**
 * User: Gyb
 * Date: 13-9-27
 * Time: 上午10:01
 */
public class HttpBase {

    private static final int DEFAULT_MAX_CONNECTIONS = 10;
    private static final int DEFAULT_SOCKET_TIMEOUT = 10 * 1000;
    private static final int DEFAULT_MAX_RETRIES = 5;
    private static final int DEFAULT_SOCKET_BUFFER_SIZE = 8192;
    private static final String HEADER_ACCEPT_ENCODING = "Accept-Encoding";
    private static final String ENCODING_GZIP = "gzip";

    private static int maxConnections = DEFAULT_MAX_CONNECTIONS;
    private static int socketTimeout = DEFAULT_SOCKET_TIMEOUT;

    private final DefaultHttpClient httpClient;
    private final HttpContext httpContext;
    private ThreadPoolExecutor threadPool;
    private final Map<Context, List<WeakReference<Future<?>>>> requestMap;
    private final Map<String, String> clientHeaderMap;


    protected HttpBase() {
        BasicHttpParams httpParams = new BasicHttpParams();

        ConnManagerParams.setTimeout(httpParams, socketTimeout);
        ConnManagerParams.setMaxConnectionsPerRoute(httpParams, new ConnPerRouteBean(maxConnections));
        ConnManagerParams.setMaxTotalConnections(httpParams, DEFAULT_MAX_CONNECTIONS);

        HttpConnectionParams.setSoTimeout(httpParams, socketTimeout);
        HttpConnectionParams.setConnectionTimeout(httpParams, socketTimeout);
        HttpConnectionParams.setTcpNoDelay(httpParams, true);
        HttpConnectionParams.setSocketBufferSize(httpParams, DEFAULT_SOCKET_BUFFER_SIZE);

        HttpProtocolParams.setVersion(httpParams, HttpVersion.HTTP_1_1);

        SchemeRegistry schemeRegistry = new SchemeRegistry();
        schemeRegistry.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
        schemeRegistry.register(new Scheme("https", SSLSocketFactory.getSocketFactory(), 443));
        ThreadSafeClientConnManager cm = new ThreadSafeClientConnManager(httpParams, schemeRegistry);

        httpContext = new SyncBasicHttpContext(new BasicHttpContext());
        httpClient = new DefaultHttpClient(cm, httpParams);
        httpClient.addRequestInterceptor(new HttpRequestInterceptor() {
            @Override
            public void process(HttpRequest request, HttpContext context) {
                if (!request.containsHeader(HEADER_ACCEPT_ENCODING)) {
                    request.addHeader(HEADER_ACCEPT_ENCODING, ENCODING_GZIP);
                }
                for (String header : clientHeaderMap.keySet()) {
                    request.addHeader(header, clientHeaderMap.get(header));
                }
            }
        });

        httpClient.addResponseInterceptor(new HttpResponseInterceptor() {
            @Override
            public void process(HttpResponse response, HttpContext context) {
                final HttpEntity entity = response.getEntity();
                if (entity == null) {
                    return;
                }
                final Header encoding = entity.getContentEncoding();
                if (encoding != null) {
                    for (HeaderElement element : encoding.getElements()) {
                        if (element.getName().equalsIgnoreCase(ENCODING_GZIP)) {
                            response.setEntity(new InflatingEntity(response.getEntity()));
                            break;
                        }
                    }
                }
            }
        });

        httpClient.setHttpRequestRetryHandler(new HttpRetryHandler(DEFAULT_MAX_RETRIES));

        threadPool = (ThreadPoolExecutor) Executors.newCachedThreadPool();

        requestMap = new WeakHashMap<Context, List<WeakReference<Future<?>>>>();
        clientHeaderMap = new HashMap<String, String>();
    }

    public Map<Context, List<WeakReference<Future<?>>>> getRequestMap() {
        return requestMap;
    }

    public void setCookie(CookieStore cookie) {
        this.httpContext.setAttribute(ClientContext.COOKIE_STORE, cookie);
    }

    public DefaultHttpClient getHttpClient() {
        return this.httpClient;
    }

    public ThreadPoolExecutor getThreadPool() {
        return this.threadPool;
    }


    public HttpContext getHttpContext() {
        return httpContext;
    }

    public void setTimeout(int timeout) {
        final HttpParams params = this.httpClient.getParams();
        ConnManagerParams.setTimeout(params, timeout);
        HttpConnectionParams.setConnectionTimeout(params, timeout);
        HttpConnectionParams.setSoTimeout(params, timeout);
    }


    public void setRedirecting(boolean isRedirecting) {
        final HttpParams params = this.httpClient.getParams();
        HttpClientParams.setRedirecting(params, isRedirecting);
    }

    public void setUserAgent(String agent) {
        HttpProtocolParams.setUserAgent(this.httpClient.getParams(), agent);
    }

    public void cancelRequest(Context context, boolean cancelIf) {
        List<WeakReference<Future<?>>> requestList = requestMap.get(context);
        if (requestList != null && requestList.size() > 0) {
            Future<?> requestRef = null;
            for (WeakReference<Future<?>> weakRef : requestList) {
                requestRef = weakRef.get();
                if (requestRef != null) {
                    requestRef.cancel(cancelIf);
                }
            }
        }
        requestMap.remove(context);
    }

    protected HttpEntityEnclosingRequestBase addEntity(HttpEntityEnclosingRequestBase base, HttpEntity entity) {
        if (entity != null) {
            base.setEntity(entity);
        }
        return base;
    }

    private static class InflatingEntity extends HttpEntityWrapper {
        public InflatingEntity(HttpEntity wrapped) {
            super(wrapped);
        }

        @Override
        public InputStream getContent() throws IOException {
            return new GZIPInputStream(wrappedEntity.getContent());
        }

        @Override
        public long getContentLength() {
            return -1;
        }
    }
}
