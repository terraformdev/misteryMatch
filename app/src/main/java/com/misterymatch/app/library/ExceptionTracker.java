package com.misterymatch.app.library;

/**
 * Created by CSS03 on 10-01-2018.
 */

public class ExceptionTracker {

    public static void track(Exception exception) {
        //Crashlytics.logException(exception);
        exception.printStackTrace();
    }

    public static void track(String message) {
        //Crashlytics.log(message);
        //Log.e();
        Log.e("ExceptionTracker",message);
    }
}