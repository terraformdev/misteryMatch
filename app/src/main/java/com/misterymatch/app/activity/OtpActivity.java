package com.misterymatch.app.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.misterymatch.app.R;
import com.misterymatch.app.model.dto.response.ForgotPasswordResponse;
import com.misterymatch.app.utils.CustomDialog;
import com.misterymatch.app.utils.SharedHelper;
import com.misterymatch.app.webservice.APIError;
import com.misterymatch.app.webservice.ErrorUtils;
import com.misterymatch.app.widgets.Pinview;

import butterknife.BindView;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import android.widget.TextView;

import static com.misterymatch.app.utils.GlobalData.api;

/**
 * Created by CSS03 on 06-02-2018.
 */

public class OtpActivity extends BaseActivity {

    @BindView(R.id.pv_otp)
    Pinview mOtp;
    @BindView(R.id.email)
    TextView email;

    String otp = null;
    CustomDialog customDialog;
    private Pinview mEnteredPin;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        customDialog = new CustomDialog(this);
        email.setText(SharedHelper.getKey(this, "changeEmail"));
        otp =SharedHelper.getKey(this, "otp", null);
        mOtp.setPinViewEventListener(new Pinview.PinViewEventListener() {
            @Override
            public void onDataEntered(Pinview pinview, boolean fromUser) {
                mEnteredPin = pinview;
            }
        });

    }

    private void initVIew(){

    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_otp;
    }

    @OnClick(R.id.confirm_code)
    void confirm() {
        if (mEnteredPin.getValue().equals(otp)) {
            startActivity(new Intent(this, NewPasswordActivity.class));
            finish();
        } else {
            showMessage("Please enter correct otp");
        }

    }

    @OnClick(R.id.did_not_receive_code)
    void resendCode() {
        apiCallSendOtp(SharedHelper.getKey(this, "changeEmail"));
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
                        otp = String.valueOf(response.body().getOtp());
                        showMessage(getString(R.string.otp_sent_to_your_, response.body().getEmail()));
                    } else {
                        APIError error = ErrorUtils.parseError(response);
                        showMessage(error.getError());
                    }
                }

                @Override
                public void onFailure(@NonNull Call<ForgotPasswordResponse> call, @NonNull Throwable t) {
                    customDialog.dismiss();
                    showMessage("Internal Server Error! Please try again" + t.getMessage());
                }
            });
        }
    }
}
