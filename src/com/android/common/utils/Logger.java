package com.android.common.utils;

import android.util.Log;

/**
 * User: Gyb
 * Date: 13-9-26
 * Time: 下午4:41
 */
public class Logger {

    private static final String DEFAULT_TAG = "android-log";
    private static String TAG = DEFAULT_TAG;

    private static Logger instance;

    private Logger() {
    }

    public static synchronized Logger getLogger(Class<?> clazz) {
        TAG = TAG + " / " + clazz.getSimpleName();
        if (instance == null) {
            instance = new Logger();
        }
        return instance;
    }


    public void debug(String message) {
        debug(message, null);
    }

    public void debug(String message, Throwable throwable) {
        Log.d(TAG, message, throwable);
    }

    public void error(String message, Throwable throwable) {
        Log.e(TAG, message, throwable);
        Log.getStackTraceString(throwable);
    }

    public void error(String message) {
        error(message, null);
    }

    public void getStackTrace(Throwable tr) {
        Log.getStackTraceString(tr);
    }

    public void warn(String message, Throwable tr) {
        Log.w(message, tr);
    }

    public void warn(String message) {
        warn(message, null);
    }
}
