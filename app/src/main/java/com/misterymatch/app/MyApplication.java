package com.misterymatch.app;

import android.app.Application;
import android.content.Context;

import com.bumptech.glide.Glide;
import com.crashlytics.android.Crashlytics;
import com.facebook.stetho.Stetho;
import com.misterymatch.app.model.Token;
import com.misterymatch.app.model.TransactionHistory;
import com.misterymatch.app.model.TransactionHistoryModel;
import com.misterymatch.app.utils.SharedHelper;
import com.google.firebase.FirebaseApp;

import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import io.fabric.sdk.android.Fabric;

public class MyApplication extends Application {

    public static NumberFormat numberFormat = NumberFormat.getCurrencyInstance(Locale.US);
    public static MyApplication myApplication = null;

    public static MyApplication getInstance() {
        if (myApplication == null)
            myApplication = new MyApplication();

        return myApplication;
    }

    public static boolean isValidEmail(String email) {
        String EMAIL_PATTERN = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
                + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";

        Pattern pattern = Pattern.compile(EMAIL_PATTERN);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }

    public static boolean isValidPassword(String pass) {
        return pass != null && pass.length() > 6;
    }

    public static boolean isEmpty(String value) {
        return value.trim().length() == 0;
    }

    public static List<TransactionHistoryModel> groupListByDate(List<TransactionHistory> activities) {
        List<TransactionHistoryModel> activityModelList = new ArrayList<>();
        List<String> keyList = new ArrayList<>();
        List<List<TransactionHistory>> list = new ArrayList<>();
        final SimpleDateFormat format2 = new SimpleDateFormat("MMMM, yyyy", Locale.getDefault());
        List<String> keyListSort = new ArrayList<>();
        List<List<TransactionHistory>> listsSort = new ArrayList<>();

        Map<String, List<TransactionHistory>> map = MyApplication.groupTransactionsByDate(activities);

        Iterator<Map.Entry<String, List<TransactionHistory>>> i = map.entrySet().iterator();
        while (i.hasNext()) {
            String key = i.next().getKey();
            keyList.add(key);
            list.add(map.get(key));
        }

        Collections.sort(keyList, (arg0, arg1) -> {
            int compareResult = 0;
            try {
                Date arg0Date = format2.parse(arg0);
                Date arg1Date = format2.parse(arg1);
                compareResult = arg0Date.compareTo(arg1Date);
            } catch (ParseException e) {
                e.printStackTrace();
                compareResult = arg0.compareTo(arg1);
            }
            return compareResult;
        });


        for (int l = keyList.size() - 1; l >= 0; l--) {
            keyListSort.add(keyList.get(l));
        }

        for (int n = 0; n < keyListSort.size(); n++) {
            listsSort.add(map.get(keyListSort.get(n)));
            TransactionHistoryModel model = new TransactionHistoryModel();
            model.setHeader(keyListSort.get(n));
            model.setTransactionHistory(map.get(keyListSort.get(n)));
            activityModelList.add(model);
        }

        return activityModelList;
    }

    public static Map<String, List<TransactionHistory>> groupTransactionsByDate(List<TransactionHistory> transactionHistories) {
        Map<String, List<TransactionHistory>> map = new HashMap<>();

        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        SimpleDateFormat format2 = new SimpleDateFormat("MMMM, yyyy", Locale.getDefault());

        for (TransactionHistory a : transactionHistories) {
            Date d = new Date();
            try {
                d = format.parse(a.getCreatedAt());
            } catch (ParseException e) {
                e.printStackTrace();
            }

            String date = format2.format(d);
            List<TransactionHistory> list = map.get(date);
            if (list == null) {
                list = new ArrayList<>();
            }
            list.add(a);
            map.put(date, list);

        }
        return map;
    }

    public static String getDisplayableTime(String value) {

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());

        Date convertedDate = new Date();
        try {
            convertedDate = dateFormat.parse(value);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        long delta = convertedDate.getTime();

        long difference = 0;
        Long mDate = java.lang.System.currentTimeMillis();

        if (mDate > delta) {
            difference = mDate - delta;
            final long seconds = difference / 1000;
            final long minutes = seconds / 60;
            final long hours = minutes / 60;
            final long days = hours / 24;
            final long months = days / 31;
            final long years = days / 365;

            if (seconds < 0) {
                return "not yet";
            } else if (seconds < 60) {
                return seconds == 1 ? "one second ago" : seconds + " seconds ago";
            } else if (seconds < 120) {
                return "a minute ago";
            } else if (seconds < 2700) // 45 * 60
            {
                return minutes + " minutes ago";
            } else if (seconds < 5400) // 90 * 60
            {
                return "an hour ago";
            } else if (seconds < 86400) // 24 * 60 * 60
            {
                return hours + " hours ago";
            } else if (seconds < 172800) // 48 * 60 * 60
            {
                return "yesterday";
            } else if (seconds < 2592000) // 30 * 24 * 60 * 60
            {
                return days + " days ago";
            } else if (seconds < 31104000) // 12 * 30 * 24 * 60 * 60
            {

                return months <= 1 ? "one month ago" : days + " months ago";
            } else {

                return years <= 1 ? "one year ago" : years + " years ago";
            }
        }
        return null;
    }


    public static String getDisplayableTime(long value) {

        long delta = value;

        long difference = 0;
        Long mDate = java.lang.System.currentTimeMillis();

        if (mDate > delta) {
            difference = mDate - delta;
            final long seconds = difference / 1000;
            final long minutes = seconds / 60;
            final long hours = minutes / 60;
            final long days = hours / 24;
            final long months = days / 31;
            final long years = days / 365;

            if (seconds < 86400) {
                SimpleDateFormat formatter = new SimpleDateFormat("hh:mm a", Locale.getDefault());
                return formatter.format(new Date(delta));
                //return "not yet";
            } else if (seconds < 172800) // 48 * 60 * 60
            {
                return "yesterday";
            } else if (seconds < 2592000) // 30 * 24 * 60 * 60
            {
                return days + " days ago";
            } else if (seconds < 31104000) // 12 * 30 * 24 * 60 * 60
            {

                return months <= 1 ? "one month ago" : days + " months ago";
            } else {

                return years <= 1 ? "one year ago" : years + " years ago";
            }
        }
        return null;
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
    }
/*
    public void clearApplicationData(Context context) {
        File cache = context.getCacheDir();
        File appDir = new File(cache.getParent());
        if(appDir.exists()){
            String[] children = appDir.list();
            for(String s : children){
                if(!s.equals("lib")){
                    deleteDir(new File(appDir, s));
                    Log.i("TAG", "File /data/data/APP_PACKAGE/" + s +" DELETED");
                }
            }
        }
    }

    public static boolean deleteDir(File dir) {
        if (dir != null && dir.isDirectory()) {
            String[] children = dir.list();
            for (int i = 0; i < children.length; i++) {
                boolean success = deleteDir(new File(dir, children[i]));
                if (!success) {
                    return false;
                }
            }
        }

        if (dir != null) {
            return dir.delete();
        }
        return false;
    }*/

    @Override
    public void onCreate() {
        super.onCreate();
        Fabric.with(this, new Crashlytics());
        FirebaseApp.initializeApp(getApplicationContext());
        Stetho.initializeWithDefaults(this);

    }

    public void SaveToken(Token token) {
        SharedHelper.putKey(getApplicationContext(), "access_token", token.getTokenType() + " " + token.getAccessToken());
        SharedHelper.putKey(getApplicationContext(), "refresh_token", token.getTokenType() + " " + token.getRefreshToken());
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        Glide.get(this).clearMemory();
    }

    @Override
    public void onTrimMemory(int level) {
        super.onTrimMemory(level);
        Glide.get(this).trimMemory(level);
    }
}
