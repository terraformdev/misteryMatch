package com.misterymatch.app.activity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.widget.Toast;

import com.misterymatch.app.BuildConfig;
import com.misterymatch.app.HomeActivity;
import com.misterymatch.app.R;
import com.misterymatch.app.model.Profile;
import com.misterymatch.app.utils.CodeSnippet;
import com.misterymatch.app.utils.GlobalData;
import com.misterymatch.app.utils.SharedHelper;
import com.misterymatch.app.webservice.APIError;
import com.misterymatch.app.webservice.ErrorUtils;
import com.google.firebase.iid.FirebaseInstanceId;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class SplashActivity extends AppCompatActivity {
    CodeSnippet helper;
    AlertDialog alert;
    Boolean isNotification = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        helper = new CodeSnippet(this);
        getDeviceToken();

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            String value1 = extras.getString("Notification");
            if (value1 != null) {
                isNotification = true;
            }
        }

        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                //Do something after 3000ms
                if (SharedHelper.getBoolKey(getApplicationContext(), "logged_in")) {
                    if (helper.isConnectingToInternet()) {
                        getProfile();
                    } else {
                        showDialog();
                    }
                } else {
                    startActivity(new Intent(SplashActivity.this, WalkthroughActivity.class));
                    finish();
                }

            }
        }, 3000);

        try {
            @SuppressLint("PackageManagerGetSignatures") PackageInfo info = getPackageManager().getPackageInfo(
                    "com.firstflirts.apps",
                    PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.d("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT));
            }
        } catch (PackageManager.NameNotFoundException | NoSuchAlgorithmException ignored) {

        }
    }

    private void getProfile(){

        HashMap<String, Object> map = new HashMap<>();
        if(!SharedHelper.getKey(this, "device_token").isEmpty()){
            map.put("device_token", SharedHelper.getKey(this, "device_token"));
        }if(!SharedHelper.getKey(this, "device_id").isEmpty()){
            map.put("device_id", SharedHelper.getKey(this, "device_id"));
        }
        map.put("device_type", BuildConfig.DEVICE_TYPE);

        String accessToken = SharedHelper.getKey(this, "access_token");
        Call<Profile> call = GlobalData.api.getProfile(accessToken, map);
        call.enqueue(new Callback<Profile>() {
            @Override
            public void onResponse(@NonNull Call<Profile> call, @NonNull Response<Profile> response) {
                if (response.isSuccessful()) {
                    GlobalData.PROFILE = response.body();
                    Log.d("getProfile SPLASH", GlobalData.PROFILE.toString());
                    SharedHelper.putKey(SplashActivity.this, "logged_in", true);
                    SharedHelper.putKey(SplashActivity.this, "user_id", String.valueOf(GlobalData.PROFILE.getUser().getId()));

                    if(isNotification)
                        startActivity(new Intent(SplashActivity.this, NotificationActivity.class));
                    else
                        startActivity(new Intent(SplashActivity.this, HomeActivity.class));

                    finish();
                } else {
                    if (response.code() == 401) {
                        SharedHelper.putKey(SplashActivity.this, "logged_in", false);
                        startActivity(new Intent(SplashActivity.this, SignInActivity.class));
                        finish();
                    }else{
                        APIError error = ErrorUtils.parseError(response);
                        Toast.makeText(getApplicationContext(), error.getError(), Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<Profile> call, @NonNull Throwable t) {
                Toast.makeText(getApplicationContext(), "Internal Server Error", Toast.LENGTH_LONG).show();
            }
        });
    }

    private void getDeviceToken() {

        String TAG = "FCM";
        try {
            if (!SharedHelper.getKey(this, "device_token").equals("") && SharedHelper.getKey(this, "device_token") != null) {
                String device_token = SharedHelper.getKey(this, "device_token");
                Log.d(TAG, "GCM Registration Token: " + device_token);
            } else {
                String device_token = FirebaseInstanceId.getInstance().getToken();
                SharedHelper.putKey(this, "device_token", FirebaseInstanceId.getInstance().getToken());
                Log.d(TAG, "Failed to complete token: " + device_token);
            }
        } catch (Exception e) {
            String device_token = "COULD NOT GET FCM TOKEN";
            Log.d(TAG, "Failed to complete token refresh" + e.toString());
        }

        try {
            String device_UDID = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
            Log.d(TAG, "Device UDID:" + device_UDID);
            SharedHelper.putKey(this, "device_id", "" + device_UDID);
        } catch (Exception e) {
            String device_UDID = "COULD NOT GET UDID";
            e.printStackTrace();
            Log.d(TAG, "Failed to complete device UDID");
        }
    }

    private void showDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(getString(R.string.connect_to_network))
                .setCancelable(false)
                .setPositiveButton(getString(R.string.connect_to_wifi), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        alert.dismiss();
                        startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));
                    }
                })
                .setNegativeButton(getString(R.string.quit), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        alert.dismiss();
                        finish();
                    }
                });
        if (alert == null) {
            alert = builder.create();
            alert.show();
        }
    }

}
