package com.misterymatch.app.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatEditText;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.misterymatch.app.R;
import com.misterymatch.app.model.dto.response.ForgotPasswordResponse;
import com.misterymatch.app.utils.CustomDialog;
import com.misterymatch.app.utils.SharedHelper;

import org.json.JSONObject;

import butterknife.BindView;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


import static com.misterymatch.app.utils.GlobalData.api;

public class ForgotPasswordActivity extends BaseActivity {

    @BindView(R.id.back)
    ImageView back;
    @BindView(R.id.title)
    TextView title;
    @BindView(R.id.email)
    AppCompatEditText email;
    @BindView(R.id.bt_reset)
    AppCompatButton mReset;
    CustomDialog customDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        customDialog = new CustomDialog(this);
    }

    @OnClick(R.id.back)
    void back() {
        finish();
    }

    @OnClick(R.id.bt_reset)
    void resetEmail() {
        if (isValidEmail()) {
            apiCallSendOtp(email.getText().toString());
        }
    }

    public void apiCallSendOtp(String email) {
        if (mCodeSnippet.isConnectingToInternet()) {
            customDialog.show();
            Call<ForgotPasswordResponse> call = api.forgotPwd(email);
            call.enqueue(new Callback<ForgotPasswordResponse>() {
                @Override
                public void onResponse(@NonNull Call<ForgotPasswordResponse> call, @NonNull Response<ForgotPasswordResponse> response) {
                    customDialog.dismiss();
                    if (response.isSuccessful()) {
                        SharedHelper.putKey(ForgotPasswordActivity.this, "changeEmail", response.body().getEmail());
                        showMessage(getString(R.string.otp_sent_to_your_, response.body().getEmail()));
                        SharedHelper.putKey(ForgotPasswordActivity.this, "otp", String.valueOf(response.body().getOtp()));
                        startActivity(new Intent(ForgotPasswordActivity.this, OtpActivity.class));
                        finish();

                    } else {
                        try {
                            JSONObject jObjError = new JSONObject(response.errorBody().string());
                            if (jObjError.has("email"))
                                Toast.makeText(getApplicationContext(), jObjError.optString("email"), Toast.LENGTH_LONG).show();
                        } catch (Exception e) {
                            Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }
                }

                @Override
                public void onFailure(@NonNull Call<ForgotPasswordResponse> call, @NonNull Throwable t) {
                    customDialog.dismiss();
                    Log.d("ForgotPassword", t.getMessage());
                    showMessage("Internal Server Error! Please try again" + t.getMessage());
                }
            });
        } else {
            showSnackBar("No Network Found");
        }
    }

    private boolean isValidEmail() {
        if (TextUtils.isEmpty(email.getText().toString())) {
            showMessage("Enter Email");
            return false;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email.getText().toString()).matches() && !TextUtils.isEmpty(email.getText().toString())) {
            showMessage("Enter Valid Email");
            return false;
        }
        return true;
    }


    @Override
    protected int getLayoutId() {
        return R.layout.activity_forgot_password;
    }
}
