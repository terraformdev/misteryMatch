package com.misterymatch.app.activity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.misterymatch.app.HomeActivity;
import com.misterymatch.app.R;
import com.misterymatch.app.model.Token;
import com.misterymatch.app.utils.CodeSnippet;
import com.misterymatch.app.utils.CountryPicker.Country;
import com.misterymatch.app.utils.CountryPicker.CountryPicker;
import com.misterymatch.app.utils.CountryPicker.CountryPickerListener;
import com.misterymatch.app.utils.CustomDialog;
import com.misterymatch.app.utils.GlobalData;
import com.misterymatch.app.utils.SharedHelper;
import com.google.firebase.FirebaseApp;

import org.json.JSONObject;

import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.misterymatch.app.utils.GlobalData.api;

public class SignUpActivity extends BaseActivity {

    private static final String TAG = "SignUp";
    @BindView(R.id.first_name)
    EditText firstName;
    @BindView(R.id.email)
    EditText email;
    @BindView(R.id.mobile)
    EditText mobile;
    @BindView(R.id.password)
    EditText password;
    @BindView(R.id.install_code)
    EditText installCode;
    @BindView(R.id.sign_up)
    Button signUp;
    @BindView(R.id.sign_in)
    TextView signIn;

    CustomDialog customDialog;
    CodeSnippet codeSnippet;
    @BindView(R.id.last_name)
    EditText lastName;
    @BindView(R.id.country_number)
    TextView countryNumber;
    @BindView(R.id.country_image)
    ImageView countryImage;
    @BindView(R.id.gender)
    Spinner gender;
    @BindView(R.id.dob)
    TextView vTvDob;
    String country_code;
    String country_name;
    private CountryPicker mCountryPicker;
    private String mSelectedDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FirebaseApp.initializeApp(this);
        customDialog = new CustomDialog(this);
        codeSnippet = new CodeSnippet(this);

        mCountryPicker = CountryPicker.newInstance("Select Country");
        List<Country> countryList = Country.getAllCountries();
        Collections.sort(countryList, new Comparator<Country>() {
            @Override
            public int compare(Country s1, Country s2) {
                return s1.getName().compareToIgnoreCase(s2.getName());
            }
        });
        mCountryPicker.setCountriesList(countryList);
        setListener();

    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_sign_up;
    }


    @OnClick({R.id.sign_up, R.id.sign_in, R.id.dob})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.sign_up:
                register();
                break;
            case R.id.sign_in:
                startActivity(new Intent(this, SignInActivity.class));
                break;
            case R.id.dob:
                openDatePicker();
                break;
        }
    }

    private boolean validate() {
        if (TextUtils.isEmpty(firstName.getText().toString())) {
            showMessage(R.string.invalid_name);
            return false;
        }
        if (TextUtils.isEmpty(lastName.getText().toString())) {
            showMessage(R.string.invalid_last_name);
            return false;
        }
        if (TextUtils.isEmpty(email.getText().toString())) {
            showMessage(R.string.invalid_email);
            return false;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email.getText().toString()).matches()) {
            Toast.makeText(this, getString(R.string.invalid_email), Toast.LENGTH_SHORT).show();
            return false;
        }
        if (TextUtils.isEmpty(mobile.getText().toString())) {
            showMessage(R.string.invalid_mobile);
            return false;
        }
        if (TextUtils.isEmpty(password.getText().toString())) {
            showMessage(R.string.invalid_password);
            return false;
        }
        return true;
    }


    private void register() {

        if (validate()) {

            final HashMap<String, String> map = new HashMap<>();
            map.put("device_type", "android");
            map.put("device_id", SharedHelper.getKey(this, "device_id"));
            map.put("device_token", SharedHelper.getKey(this, "device_token"));
            map.put("login_by", "manual");
            map.put("first_name", firstName.getText().toString());
            map.put("last_name", lastName.getText().toString());
            map.put("email", email.getText().toString());
            map.put("country", country_name);
            map.put("mobile", countryNumber.getText().toString() + mobile.getText().toString());
            map.put("gender", gender.getSelectedItem().toString());
            map.put("dob", vTvDob.getText().toString());
            map.put("password", password.getText().toString());
            map.put("install_code", installCode.getText().toString());
            Log.d("SIGN", map.toString());
            if (!codeSnippet.isConnectingToInternet()) {
                showMessage(R.string.check_your_internet_connection);
                return;
            }

            if (customDialog != null)
                customDialog.show();

            Call<Token> call = api.register(map);
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
                        startActivity(new Intent(SignUpActivity.this, HomeActivity.class));
                    } else {

                        try {
                            JSONObject jObjError = new JSONObject(response.errorBody().string());
                            if (jObjError.has("device_type"))
                                Toast.makeText(getApplicationContext(), jObjError.optString("device_type"), Toast.LENGTH_LONG).show();
                            else if (jObjError.has("device_token"))
                                Toast.makeText(getApplicationContext(), jObjError.optString("device_token"), Toast.LENGTH_LONG).show();
                            else if (jObjError.has("device_id"))
                                Toast.makeText(getApplicationContext(), jObjError.optString("device_id"), Toast.LENGTH_LONG).show();
                            else if (jObjError.has("login_by"))
                                Toast.makeText(getApplicationContext(), jObjError.optString("login_by"), Toast.LENGTH_LONG).show();
                            else if (jObjError.has("first_name"))
                                Toast.makeText(getApplicationContext(), jObjError.optString("first_name"), Toast.LENGTH_LONG).show();
                            else if (jObjError.has("last_name"))
                                Toast.makeText(getApplicationContext(), jObjError.optString("last_name"), Toast.LENGTH_LONG).show();
                            else if (jObjError.has("email"))
                                Toast.makeText(getApplicationContext(), jObjError.optString("email"), Toast.LENGTH_LONG).show();
                            else if (jObjError.has("mobile"))
                                Toast.makeText(getApplicationContext(), jObjError.optString("mobile"), Toast.LENGTH_LONG).show();
                            else if (jObjError.has("password"))
                                Toast.makeText(getApplicationContext(), jObjError.optString("password"), Toast.LENGTH_LONG).show();
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
                    Toast.makeText(getApplicationContext(), "Register Something Went Wrong" + t.toString(), Toast.LENGTH_LONG).show();
                }
            });


        }
    }


    private void setListener() {
        mCountryPicker.setListener(new CountryPickerListener() {
            @Override
            public void onSelectCountry(String name, String code, String dialCode, int flagDrawableResID) {
//                mCountryNameTextView.setText(name);
//                mCountryIsoCodeTextView.setText(code);
                countryNumber.setText(dialCode);
                country_name = name;
                countryImage.setImageResource(flagDrawableResID);
                mCountryPicker.dismiss();
            }
        });
        getUserCountryInfo();
    }

    @OnClick(R.id.country_number)
    void getCountryNumber() {
        mCountryPicker.show(getSupportFragmentManager(), "COUNTRY_PICKER");
    }

    @OnClick(R.id.country_image)
    void getCountryImage() {
        mCountryPicker.show(getSupportFragmentManager(), "COUNTRY_PICKER");
    }

    private void getUserCountryInfo() {
        Locale current = getResources().getConfiguration().locale;
        Country country = Country.getCountryFromSIM(this);
        if (country != null) {
            countryImage.setImageResource(country.getFlag());
            countryNumber.setText(country.getDialCode());
            country_code = country.getDialCode();
            country_name = country.getName();
        } else {
            Country us = new Country("US", "United States", "+1", R.drawable.flag_us);
            countryImage.setImageResource(us.getFlag());
            countryNumber.setText(us.getDialCode());
            country_code = us.getDialCode();
            country_name = us.getName();
            //Toast.makeText(Login.this, "Required Sim", Toast.LENGTH_SHORT).show();
        }
    }

    private void openDatePicker() {
        Calendar today = Calendar.getInstance();
        Calendar miniDate = Calendar.getInstance();
        DatePickerDialog datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                mSelectedDate =   (monthOfYear + 1) + "/" + dayOfMonth + "/" + year;
                vTvDob.setText(mSelectedDate);
            }
        }, Calendar.getInstance().get(Calendar.YEAR), Calendar.getInstance().get(Calendar.MONTH), Calendar.getInstance().get(Calendar.DAY_OF_MONTH));

        int year = today.get(Calendar.YEAR) - 18;
        int month = today.get(Calendar.MONTH);
        int day = today.get(Calendar.DAY_OF_MONTH);

        /*  miniDate.set(y, m, d);
        datePickerDialog.getDatePicker().setMinDate(miniDate.getTimeInMillis());*/

        miniDate.set(year, month, day);
        datePickerDialog.getDatePicker().setMaxDate(miniDate.getTimeInMillis());
        datePickerDialog.show();
    }

}
