package com.misterymatch.app.utils;

import com.misterymatch.app.BuildConfig;
import com.misterymatch.app.model.Profile;
import com.misterymatch.app.model.Token;
import com.misterymatch.app.model.User;
import com.misterymatch.app.webservice.ApiClient;
import com.misterymatch.app.webservice.ApiInterface;

/**
 * Created by santhosh@appoets.com on 23-11-2017.
 */
public class GlobalData {
    public static final String ACTION_MATCH_ACCEPTED = BuildConfig.APPLICATION_ID
            + ".ACTION_MATCH_ACCEPTED";
    public static final String ACTION_MATCH_CANCELLED = BuildConfig.APPLICATION_ID
            + ".ACTION_MATCH_CANCELLED";
    public static final int INCOMING_ACTIVITY_REQUEST_CODE = 101;
    public static ApiInterface api = ApiClient.getRetrofit().create(ApiInterface.class);
    public static String avatar = "";
    public static Profile PROFILE = null;
    public static Token TOKEN = null;
    public static User USER = null;
}
