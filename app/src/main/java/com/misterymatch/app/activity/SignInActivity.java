package com.misterymatch.app.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.misterymatch.app.BuildConfig;
import com.misterymatch.app.HomeActivity;
import com.misterymatch.app.R;
import com.misterymatch.app.model.Token;
import com.misterymatch.app.model.User;
import com.misterymatch.app.utils.CodeSnippet;
import com.misterymatch.app.utils.CustomDialog;
import com.misterymatch.app.utils.GlobalData;
import com.misterymatch.app.utils.LinkedinActivity;
import com.misterymatch.app.utils.SharedHelper;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookAuthorizationException;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;

import org.json.JSONObject;

import java.util.Arrays;
import java.util.HashMap;

import butterknife.BindView;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


import static com.misterymatch.app.utils.GlobalData.api;

public class SignInActivity extends BaseActivity {

    @BindView(R.id.email)
    EditText email;
    @BindView(R.id.password)
    EditText password;
    @BindView(R.id.sign_in)
    Button signIn;
    @BindView(R.id.sign_up)
    TextView signUp;

    CustomDialog customDialog;
    CodeSnippet codeSnippet;
    @BindView(R.id.forgot_password)
    TextView forgotPassword;

    /*----------Facebook Login---------------*/
    CallbackManager callbackManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        callbackManager = CallbackManager.Factory.create();
        if (Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }
        customDialog = new CustomDialog(this);
        codeSnippet = new CodeSnippet(this);

        if (GlobalData.PROFILE != null) {
            User user = GlobalData.PROFILE.getUser();
            email.setText(user.getEmail());
        }
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_sign_in;
    }

    @OnClick({R.id.sign_in, R.id.sign_up, R.id.forgot_password, R.id.facebook, R.id.linkedin})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.sign_in:
                login();
                break;
            case R.id.sign_up:
                startActivity(new Intent(this, SignUpActivity.class));
                break;
            case R.id.forgot_password:
                startActivity(new Intent(this, ForgotPasswordActivity.class));
                break;
            case R.id.facebook:
                fbLogin();
                break;
            case R.id.linkedin:
                Intent i = new Intent(this, LinkedinActivity.class);
                startActivityForResult(i, 1);
                break;
        }
    }

    private void login() {

        if (TextUtils.isEmpty(email.getText().toString())) {
            showMessage(R.string.invalid_email);
            return;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email.getText().toString()).matches()) {
            Toast.makeText(this, getString(R.string.invalid_email), Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(password.getText().toString())) {
            showMessage(R.string.invalid_password);
            return;
        }

        final HashMap<String, String> map = new HashMap<>();
        map.put("username", email.getText().toString());
        map.put("password", password.getText().toString());
        map.put("grant_type", "password");
        map.put("client_id", BuildConfig.CLIENT_ID);
        map.put("client_secret", BuildConfig.CLIENT_SECRET);
        map.put("device_type", BuildConfig.DEVICE_TYPE);
        map.put("device_id", SharedHelper.getKey(this, "device_id"));
        map.put("device_token", SharedHelper.getKey(this, "device_token"));

        if (!codeSnippet.isConnectingToInternet()) {
            Toast.makeText(this, getString(R.string.check_your_internet_connection), Toast.LENGTH_LONG).show();
            return;
        }

        if (customDialog != null)
            customDialog.show();

        Call<Token> call = api.login(map);
        call.enqueue(new Callback<Token>() {
            @Override
            public void onResponse(@NonNull Call<Token> call, @NonNull Response<Token> response) {
                customDialog.cancel();
                if (response.isSuccessful()) {
                    GlobalData.TOKEN = response.body();
                    SharedHelper.putKey(getApplicationContext(), "access_token", GlobalData.TOKEN.getTokenType() + " " + GlobalData.TOKEN.getAccessToken());
                    SharedHelper.putKey(getApplicationContext(), "refresh_token", GlobalData.TOKEN.getTokenType() + " " + GlobalData.TOKEN.getRefreshToken());
                    SharedHelper.putKey(getApplicationContext(), "logged_in", true);
                    finishAffinity();
                    startActivity(new Intent(getApplicationContext(), HomeActivity.class));
                } else {
                    try {
                        JSONObject jObjError = new JSONObject(response.errorBody().string());
                        if (jObjError.has("error"))
                            Toast.makeText(getApplicationContext(), jObjError.optString("error"), Toast.LENGTH_LONG).show();
                        else
                            System.out.println("");
                    } catch (Exception e) {
                        Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<Token> call, @NonNull Throwable t) {
                customDialog.cancel();
            }
        });


    }

    public void fbLogin() {

        LoginManager.getInstance().logInWithReadPermissions(this, Arrays.asList("email"));

        LoginManager.getInstance().registerCallback(callbackManager,
                new FacebookCallback<LoginResult>() {

                    public void onSuccess(LoginResult loginResult) {
                        if (AccessToken.getCurrentAccessToken() != null) {
                            Log.e("loginresult", "" + loginResult.getAccessToken().getToken());
                            //SharedHelper.putKey(getApplicationContext(), "access_token", loginResult.getAccessToken().getToken());
                            //GlobalData.access_token = loginResult.getAccessToken().getToken();
                            //GlobalData.loginBy = "facebook";
                            HashMap<String, String> map = new HashMap<>();
                            map.put("login_by", "facebook");
                            map.put("accessToken", loginResult.getAccessToken().getToken());
                            facebookLogin(map);
                        }

                    }

                    @Override
                    public void onCancel() {
                        // App code
                    }

                    @Override
                    public void onError(FacebookException exception) {
                        if (exception instanceof FacebookAuthorizationException) {
                            if (AccessToken.getCurrentAccessToken() != null) {
                                LoginManager.getInstance().logOut();
                            }
                        }
                        Log.e("Facebook", exception.getMessage());
                    }
                });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        callbackManager.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1) {
            if(resultCode == Activity.RESULT_OK){
                String accessToken =data.getStringExtra("linkedIn_access_token");
                linkedinLogin(accessToken);
            }
        }
    }

    private void facebookLogin(HashMap<String, String> map) {

        map.put("device_type", BuildConfig.DEVICE_TYPE);
        map.put("device_id", SharedHelper.getKey(this, "device_id"));
        map.put("device_token", SharedHelper.getKey(this, "device_token"));

        Log.d("MAP", map.toString());

        if (!codeSnippet.isConnectingToInternet()) {
            Toast.makeText(this, getString(R.string.check_your_internet_connection), Toast.LENGTH_LONG).show();
            return;
        }

        if (customDialog != null)
            customDialog.show();

        Call<Token> call = api.loginFacebook(map);
        call.enqueue(new Callback<Token>() {
            @Override
            public void onResponse(@NonNull Call<Token> call, @NonNull Response<Token> response) {
                customDialog.cancel();
                if (response.isSuccessful()) {
                    GlobalData.TOKEN = response.body();
                    SharedHelper.putKey(getApplicationContext(), "access_token", GlobalData.TOKEN.getTokenType() + " " + GlobalData.TOKEN.getAccessToken());
                    SharedHelper.putKey(getApplicationContext(), "refresh_token", GlobalData.TOKEN.getTokenType() + " " + GlobalData.TOKEN.getRefreshToken());
                    SharedHelper.putKey(getApplicationContext(), "logged_in", true);
                    finishAffinity();
                    startActivity(new Intent(getApplicationContext(), HomeActivity.class));
                } else {
                    try {
                        JSONObject jObjError = new JSONObject(response.errorBody().string());
                        if (jObjError.has("error"))
                            Toast.makeText(getApplicationContext(), jObjError.optString("error"), Toast.LENGTH_LONG).show();
                        else
                            System.out.println("");
                    } catch (Exception e) {
                        Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<Token> call, @NonNull Throwable t) {
                customDialog.cancel();
            }
        });

    }

    private void linkedinLogin(String token) {

        HashMap<String, String> map = new HashMap<>();
        map.put("login_by", "linkedin");
        map.put("accessToken", token);
        map.put("device_type", BuildConfig.DEVICE_TYPE);
        map.put("device_id", SharedHelper.getKey(this, "device_id"));
        map.put("device_token", SharedHelper.getKey(this, "device_token"));

        Log.d("MAP", map.toString());

        if (!codeSnippet.isConnectingToInternet()) {
            Toast.makeText(this, getString(R.string.check_your_internet_connection), Toast.LENGTH_LONG).show();
            return;
        }

        if (customDialog != null)
            customDialog.show();

        Call<Token> call = api.loginLinkedin(map);
        call.enqueue(new Callback<Token>() {
            @Override
            public void onResponse(@NonNull Call<Token> call, @NonNull Response<Token> response) {
                customDialog.cancel();
                if (response.isSuccessful()) {
                    GlobalData.TOKEN = response.body();
                    SharedHelper.putKey(getApplicationContext(), "access_token", GlobalData.TOKEN.getTokenType() + " " + GlobalData.TOKEN.getAccessToken());
                    SharedHelper.putKey(getApplicationContext(), "refresh_token", GlobalData.TOKEN.getTokenType() + " " + GlobalData.TOKEN.getRefreshToken());
                    SharedHelper.putKey(getApplicationContext(), "logged_in", true);
                    finishAffinity();
                    startActivity(new Intent(SignInActivity.this, HomeActivity.class));
                } else {
                    try {
                        JSONObject jObjError = new JSONObject(response.errorBody().string());
                        if (jObjError.has("error"))
                            Toast.makeText(getApplicationContext(), jObjError.optString("error"), Toast.LENGTH_LONG).show();
                        else
                            System.out.println("");
                    } catch (Exception e) {
                        Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<Token> call, @NonNull Throwable t) {
                customDialog.cancel();
            }
        });

    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
