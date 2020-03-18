package com.misterymatch.app.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.misterymatch.app.R;
import com.misterymatch.app.adapter.InterestGridAdapter;
import com.misterymatch.app.model.Interest;
import com.misterymatch.app.model.Profile;
import com.misterymatch.app.model.UserInterest;
import com.misterymatch.app.utils.GlobalData;
import com.misterymatch.app.utils.SharedHelper;
import com.misterymatch.app.webservice.APIError;
import com.misterymatch.app.webservice.ErrorUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


import static com.misterymatch.app.utils.GlobalData.api;

public class ChooseInterestsActivity extends AppCompatActivity {

    @BindView(R.id.back)
    ImageView back;
    @BindView(R.id.title)
    TextView title;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.interest_gridview)
    GridView interestGridview;
    @BindView(R.id.done)
    Button done;

    InterestGridAdapter adapter;
    private int CHOOSE_INTEREST_REQUEST = 3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_interests);
        ButterKnife.bind(this);
        title.setText(getString(R.string.choose_your_interests));
        List<Interest> list = new ArrayList<>();

        adapter = new InterestGridAdapter(this, list, new ArrayList<Interest>());
        interestGridview.setAdapter(adapter);

        getProfile();
    }

    private void getProfile() {
        String accessToken = SharedHelper.getKey(this, "access_token");
        Call<Profile> call = GlobalData.api.getProfile(accessToken, new HashMap<String, Object>());
        call.enqueue(new Callback<Profile>() {
            @Override
            public void onResponse(@NonNull Call<Profile> call, @NonNull Response<Profile> response) {
                if (response.isSuccessful()) {
                    GlobalData.PROFILE = response.body();
                    List<Interest> list = GlobalData.PROFILE.getInterest();
                    List<Interest> userInterest = new ArrayList<>();
                    for (UserInterest objs : GlobalData.PROFILE.getUser().getUserInterest()) {
                        userInterest.add(objs.getInterest());
                    }
                    adapter = new InterestGridAdapter(ChooseInterestsActivity.this, list, userInterest);
                    interestGridview.setAdapter(adapter);
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
            public void onFailure(@NonNull Call<Profile> call, @NonNull Throwable t) {
                Toast.makeText(getApplicationContext(), "500 Internal Server Error", Toast.LENGTH_LONG).show();
            }
        });
    }

    private void updateInterest(HashMap<String, String> map) {

        System.out.println("FF MAP " + map.toString());

        String accessToken = SharedHelper.getKey(this, "access_token");
        Call<Profile> call = api.updateInterest(accessToken, map);
        call.enqueue(new Callback<Profile>() {
            @Override
            public void onResponse(@NonNull Call<Profile> call, @NonNull Response<Profile> response) {
                if (response.isSuccessful()) {
                    GlobalData.PROFILE = response.body();
                    Toast.makeText(getApplicationContext(), getString(R.string.success), Toast.LENGTH_SHORT).show();
                    setResult(CHOOSE_INTEREST_REQUEST);
                    finish();
                } else {
                    APIError error = ErrorUtils.parseError(response);
                    Toast.makeText(getApplicationContext(), error.getError(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<Profile> call, @NonNull Throwable t) {
                Toast.makeText(getApplicationContext(), "updateProfile Something Went Wrong" + t.toString(), Toast.LENGTH_LONG).show();
            }
        });
    }

    @OnClick({R.id.back, R.id.done})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.back:
                onBackPressed();
                break;
            case R.id.done:
                List<Interest> selectedInterest = adapter.getSelectedInterests();
                HashMap<String, String> map = new HashMap<>();
                for (Interest obj : selectedInterest) {
                    map.put("interest[" + obj.getId() + "]", String.valueOf(obj.getId()));
                    System.out.println(obj.getId() + " " + obj.getName());
                }
                updateInterest(map);
                break;
        }
    }
}
