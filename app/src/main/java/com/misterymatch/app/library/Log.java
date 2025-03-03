package com.misterymatch.app.library;

/**
 * Created by CSS03 on 10-01-2018.
 */


public class Log {

    public static void d(String name, String value) {
        android.util.Log.d(name, value);
    }

    public static void i(String name, String value) {
        android.util.Log.i(name, value);
    }

    public static void e(String name, String value) {
        android.util.Log.e(name, value);
    }

    public static void w(String name, String value) {
        android.util.Log.e(name, value);
    }
}
