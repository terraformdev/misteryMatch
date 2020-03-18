package com.misterymatch.app.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.AppCompatEditText;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.misterymatch.app.R;
import com.misterymatch.app.model.Message;
import com.misterymatch.app.utils.CustomDialog;
import com.misterymatch.app.utils.SharedHelper;
import com.misterymatch.app.webservice.APIError;
import com.misterymatch.app.webservice.ErrorUtils;

import java.util.HashMap;

import butterknife.BindView;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


import static com.misterymatch.app.utils.GlobalData.api;

public class NewPasswordActivity extends BaseActivity {

    @BindView(R.id.back)
    ImageView back;
    @BindView(R.id.title)
    TextView title;
    @BindView(R.id.new_password)
    AppCompatEditText mEdNewPassword;
    @BindView(R.id.confirm_password)
    AppCompatEditText mEdConfirmPassword;
    @BindView(R.id.submit)
    Button submit;
    CustomDialog customDialog;
    String otp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        customDialog = new CustomDialog(this);
        otp =SharedHelper.getKey(this, "otp", null);
    }

    @OnClick(R.id.back)
    void back() {
        finish();
    }

    @OnClick(R.id.submit)
    void submit() {
        if (isValidCredentials()) {
            customDialog.show();
            if (mCodeSnippet.isConnectingToInternet()) {
                final HashMap<String, Object> map = new HashMap<>();
                String email = SharedHelper.getKey(this, "changeEmail");
                map.put("otp", otp);
                map.put("password", mEdNewPassword.getText().toString());
                map.put("password_confirmation", mEdConfirmPassword.getText().toString());
                map.put("email", email);
                Call<Message> call = api.resetPassword(map);
                call.enqueue(new Callback<Message>() {
                    @Override
                    public void onResponse(@NonNull Call<Message> call, @NonNull Response<Message> response) {
                        customDialog.dismiss();
                        if (response.isSuccessful()) {
                            Toast.makeText(NewPasswordActivity.this, getString(R.string.password_updated_successfully), Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(NewPasswordActivity.this, SignInActivity.class));
                            finish();
                        } else {
                            APIError error = ErrorUtils.parseError(response);
                            showMessage(error.getError());
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<Message> call, @NonNull Throwable t) {
                        customDialog.dismiss();
                        showMessage("Internal Server Error! Please try again");
                    }
                });
            } else {
                showSnackBar("No Network Found");
            }
        }
    }

    private boolean isValidCredentials() {
        if (TextUtils.isEmpty(mEdNewPassword.getText().toString())) {
            showMessage("New password required");
            return false;
        }
        if (TextUtils.isEmpty(mEdConfirmPassword.getText().toString())) {
            showMessage("Confirm password required");
            return false;
        }
        return true;
    }


    @Override
    protected int getLayoutId() {
        return R.layout.activity_new_password;
    }
}
