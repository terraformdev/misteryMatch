package com.misterymatch.app.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.CardView;
import android.support.v7.widget.SwitchCompat;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.misterymatch.app.BuildConfig;
import com.misterymatch.app.HomeActivity;
import com.misterymatch.app.R;
import com.misterymatch.app.model.Message;
import com.misterymatch.app.model.Profile;
import com.misterymatch.app.model.Setting;
import com.misterymatch.app.model.User;
import com.misterymatch.app.utils.GlobalData;
import com.misterymatch.app.utils.RangeSlider.RangeSeekBar;
import com.misterymatch.app.utils.SharedHelper;
import com.misterymatch.app.webservice.APIError;
import com.misterymatch.app.webservice.ErrorUtils;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.AutocompleteActivity;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


//import com.datesauce.app.model.UserUpdateSettings;

public class SettingsActivity extends AppCompatActivity {


    @BindView(R.id.back)
    ImageView back;
    @BindView(R.id.title)
    TextView title;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.premium_layout)
    LinearLayout premiumLayout;
    @BindView(R.id.premium)
    CardView premium;
    @BindView(R.id.view_profile)
    TextView viewProfile;
    @BindView(R.id.ed_user_name)
    EditText edUserName;
    @BindView(R.id.bt_save)
    Button btSave;
    @BindView(R.id.new_matches)
    SwitchCompat newMatches;
    @BindView(R.id.messages)
    SwitchCompat messages;
    @BindView(R.id.tv_myLocation)
    AppCompatTextView tvMyLocation;
    @BindView(R.id.layout_my_location)
    LinearLayout layoutMyLocation;
    @BindView(R.id.live_dist)
    TextView liveDist;
    @BindView(R.id.distance)
    SeekBar distance;
    @BindView(R.id.live_age_limit)
    TextView liveAgeLimit;
    @BindView(R.id.age_limit)
    RangeSeekBar ageLimit;
    @BindView(R.id.men)
    SwitchCompat men;
    @BindView(R.id.women)
    SwitchCompat women;
    @BindView(R.id.dnd)
    SwitchCompat dnd;
    @BindView(R.id.privacy_policy)
    TextView privacyPolicy;
    @BindView(R.id.app_version)
    TextView appVersion;
    @BindView(R.id.share)
    TextView share;
    @BindView(R.id.help)
    TextView help;
    @BindView(R.id.language)
    TextView language;
    @BindView(R.id.logout)
    TextView logout;
    @BindView(R.id.delete_account)
    TextView deleteAccount;
    @BindView(R.id.root)
    LinearLayout root;
    @BindView(R.id.browser)
    WebView browser;

    LatLng latLng = null;

    int PLACE_AUTOCOMPLETE_REQUEST_CODE = 1;
    private Place place;
    private Place place1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        ButterKnife.bind(this);
        HomeActivity.isgetFindMatch = true;
        title.setText(getString(R.string.settings));
        try {
            PackageInfo pInfo = this.getPackageManager().getPackageInfo(this.getPackageName(), 0);
            int version = pInfo.versionCode;
            appVersion.setText(getString(R.string.app_version, version));
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        distance.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int dist, boolean b) {
                liveDist.setText(getString(R.string._km, dist));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });


        ageLimit.setOnRangeSeekBarChangeListener(new RangeSeekBar.OnRangeSeekBarChangeListener() {
            @Override
            public void onRangeSeekBarValuesChanged(RangeSeekBar bar, Object minValue, Object maxValue) {
                liveAgeLimit.setText(String.valueOf(minValue + "-" + maxValue));
            }
        });

        if (GlobalData.PROFILE != null) {
            User user = GlobalData.PROFILE.getUser();
            if (!TextUtils.isEmpty(user.getUsername())) {
                edUserName.setText(user.getUsername());
                btSave.setVisibility(View.GONE);
                edUserName.setEnabled(false);
            } else {
                btSave.setVisibility(View.VISIBLE);
                edUserName.setEnabled(true);
            }
        }

        getSettings();

        // Initialize Places.
        Places.initialize(getApplicationContext(),"AIzaSyB5jh2beqDRhzO02qNos7b82rofxypy6r0");

        // Create a new Places client instance.
        PlacesClient placesClient = Places.createClient(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private void initView(Setting setting) {

        if(setting.getNotificationMatch() != null) {
            newMatches.setChecked(setting.getNotificationMatch() == 1);
        }
        if(setting.getNotificationMessage() != null) {
            messages.setChecked(setting.getNotificationMessage() == 1);
        }
        dnd.setChecked(setting.getDnd() == 1);
        distance.setProgress(setting.getDistance());
        tvMyLocation.setText(setting.getAddress());

        if (setting.getGender().equalsIgnoreCase("male")) {
            men.setChecked(true);
        } else if (setting.getGender().equalsIgnoreCase("female")) {
            women.setChecked(true);
        } else if (setting.getGender().equalsIgnoreCase("both")) {
            men.setChecked(true);
            women.setChecked(true);
        }

        List<String> ageLimits = Arrays.asList(setting.getAgeLimit().split(","));
        if (ageLimits.size() == 2) {
            ageLimit.setSelectedMinValue(Integer.parseInt(ageLimits.get(0)));
            ageLimit.setSelectedMaxValue(Integer.parseInt(ageLimits.get(1)));
            liveAgeLimit.setText(ageLimits.get(0) + " - " + ageLimits.get(1));
        }
    }

    @OnClick({R.id.back, R.id.privacy_policy, R.id.help, R.id.share, R.id.language, R.id.premium, R.id.view_profile, R.id.bt_save, R.id.tv_myLocation, R.id.logout, R.id.delete_account})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.back:
                onBackPressed();
                break;
            case R.id.privacy_policy:
                Intent privacy_policy = new Intent(Intent.ACTION_VIEW);
                privacy_policy.setData(Uri.parse(BuildConfig.BASE_URL + "privacy"));
                startActivity(privacy_policy);
                break;
            case R.id.help:
                Intent help = new Intent(Intent.ACTION_VIEW);
                help.setData(Uri.parse(BuildConfig.BASE_URL + "help"));
                startActivity(help);
                break;
            case R.id.share:
                String packageName = getApplicationContext().getPackageName();
                Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
                sharingIntent.setType("text/plain");
                String content = "Hey! Checkout this app at " + Uri.parse("https://play.google.com/store/apps/details?id=" + packageName);
                sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Link to Download");
                sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, content);
                startActivity(Intent.createChooser(sharingIntent, "Share via"));

                break;
            case R.id.language:
                startActivity(new Intent(this, LanguageActivity.class));
                break;
            case R.id.premium:
                startActivity(new Intent(this, PremiumActivity.class));
                break;
            case R.id.view_profile:
                startActivity(new Intent(this, EditProfileActivity.class));
                break;
            case R.id.bt_save:
                updateUsername();
                break;
            case R.id.tv_myLocation:
                List<Place.Field> fields = Arrays.asList(Place.Field.ADDRESS, Place.Field.LAT_LNG);
                Intent intent = new Autocomplete.IntentBuilder(
                        AutocompleteActivityMode.FULLSCREEN, fields)
                        .build(this);
                startActivityForResult(intent, PLACE_AUTOCOMPLETE_REQUEST_CODE);
                break;
            case R.id.logout:
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setMessage(getString(R.string.logout_confirmation));
                builder.setPositiveButton(getString(android.R.string.ok), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        logout();
                    }
                });
                builder.setNegativeButton(getString(android.R.string.cancel),
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });

                AlertDialog dialog = builder.create();
                dialog.show();
                break;
            case R.id.delete_account:
                AlertDialog.Builder builder1 = new AlertDialog.Builder(this);
                builder1.setMessage(getString(R.string.delete_confirm));
                builder1.setPositiveButton(getString(android.R.string.ok), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        deleteAccount();
                    }
                });
                builder1.setNegativeButton(getString(android.R.string.cancel),
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                AlertDialog dialog1 = builder1.create();
                dialog1.show();
                break;
        }
    }

    private void getSettings() {
        String accessToken = SharedHelper.getKey(this, "access_token");
        Call<Setting> call = GlobalData.api.getSettings(accessToken);
        call.enqueue(new Callback<Setting>() {
            @Override
            public void onResponse(@NonNull Call<Setting> call, @NonNull Response<Setting> response) {
                if (response.isSuccessful()) {
                    Setting setting = response.body();
                    initView(setting);
                } else {
                    if (response.code() == 401) {
                        SharedHelper.putKey(getApplicationContext(), "logged_in", false);
                        startActivity(new Intent(getApplicationContext(), SignInActivity.class));
                        finish();
                    } else {
                        APIError error = ErrorUtils.parseError(response);
                        Toast.makeText(getApplicationContext(), error.getError(), Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<Setting> call, @NonNull Throwable t) {
                Toast.makeText(getApplicationContext(), "500 Internal Server Error", Toast.LENGTH_LONG).show();
            }
        });
    }

    private void logout() {
        String accessToken = SharedHelper.getKey(this, "access_token");
        Call<Message> call = GlobalData.api.logout(accessToken);
        call.enqueue(new Callback<Message>() {
            @Override
            public void onResponse(@NonNull Call<Message> call, @NonNull Response<Message> response) {
                if (response.isSuccessful()) {
                    SharedHelper.clearSharedPreferences(getApplicationContext());
                    finishAffinity();
                    GlobalData.PROFILE = null;
                    startActivity(new Intent(SettingsActivity.this, SplashActivity.class));
                }
            }

            @Override
            public void onFailure(@NonNull Call<Message> call, @NonNull Throwable t) {
                Toast.makeText(getApplicationContext(), "logout Faild", Toast.LENGTH_LONG).show();
            }
        });
    }

    private void deleteAccount() {
        String accessToken = SharedHelper.getKey(this, "access_token");
        Call<Message> call = GlobalData.api.deleteAccount(accessToken);
        call.enqueue(new Callback<Message>() {
            @Override
            public void onResponse(@NonNull Call<Message> call, @NonNull Response<Message> response) {
                if (response.isSuccessful()) {
                    SharedHelper.clearSharedPreferences(getApplicationContext());
                    finishAffinity();
                    GlobalData.PROFILE = null;
                    startActivity(new Intent(SettingsActivity.this, SplashActivity.class));
                }
            }

            @Override
            public void onFailure(@NonNull Call<Message> call, @NonNull Throwable t) {
                Toast.makeText(getApplicationContext(), "deleteAccount Faild", Toast.LENGTH_LONG).show();
            }
        });
    }

    private void updateSetting() {
        String gender = "both";
        if (men.isChecked()) {
            gender = "male";
        }
        if (women.isChecked()) {
            gender = "female";
        }
        if (men.isChecked() && women.isChecked()) {
            gender = "both";
        }

        String ageLimitValue = ageLimit.getSelectedMinValue() + "," + ageLimit.getSelectedMaxValue();

        HashMap<String, Object> map = new HashMap<>();
        map.put("notification_message", messages.isChecked() ? "on" : "off");
        map.put("notification_match", newMatches.isChecked() ? "on" : "off");
        map.put("gender", gender);
        map.put("age_limit", ageLimitValue);
        map.put("distance", distance.getProgress());
        map.put("dnd", dnd.isChecked() ? "on" : "off");

        if (latLng != null) {
            map.put("latitude", latLng.latitude);
            map.put("longitude", latLng.longitude);
            map.put("address", tvMyLocation.getText().toString());
        }


        String accessToken = SharedHelper.getKey(this, "access_token");
        Call<Setting> call = GlobalData.api.updateSetting(accessToken, map);
        call.enqueue(new Callback<Setting>() {
            @Override
            public void onResponse(@NonNull Call<Setting> call, @NonNull Response<Setting> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(SettingsActivity.this, getString(R.string.updated_successfully), Toast.LENGTH_SHORT).show();
                    finish();
                }
            }

            @Override
            public void onFailure(@NonNull Call<Setting> call, @NonNull Throwable t) {
                Toast.makeText(getApplicationContext(), "Network error. Please try again lager" + t.toString(), Toast.LENGTH_LONG).show();
            }
        });


    }

    private void updateUsername() {

        if (edUserName.getText().toString().isEmpty()) {
            Toast.makeText(this, getString(R.string.please_enter_user_name), Toast.LENGTH_SHORT).show();
            return;
        }

        String accessToken = SharedHelper.getKey(this, "access_token");
        Call<Profile> call = GlobalData.api.updateUsername(accessToken, edUserName.getText().toString());
        call.enqueue(new Callback<Profile>() {
            @Override
            public void onResponse(@NonNull Call<Profile> call, @NonNull Response<Profile> response) {
                if (response.isSuccessful()) {
                    User user = response.body().getUser();
                    GlobalData.PROFILE.getUser().setUsername(user.getUsername());
                    btSave.setVisibility(View.GONE);
                    edUserName.setEnabled(false);
                    Toast.makeText(SettingsActivity.this, getString(R.string.updated_successfully), Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(SettingsActivity.this, getString(R.string.user_name_already_been_taken), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<Profile> call, @NonNull Throwable t) {
                Toast.makeText(getApplicationContext(), "updateProfile Something Went Wrong" + t.toString(), Toast.LENGTH_LONG).show();
            }
        });


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PLACE_AUTOCOMPLETE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                place1 = Autocomplete.getPlaceFromIntent(data);
                latLng = place1.getLatLng();
                tvMyLocation.setText(place1.getAddress());
            } else if (resultCode == AutocompleteActivity.RESULT_ERROR) {
                Status status = Autocomplete.getStatusFromIntent(data);
                Log.i("Settings Location fetch", status.getStatusMessage());
            } else if (resultCode == RESULT_CANCELED) {
                // The user canceled the operation.
            }
        }
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        updateSetting();
    }
}
