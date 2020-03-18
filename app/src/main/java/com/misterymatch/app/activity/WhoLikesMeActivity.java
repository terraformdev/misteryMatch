package com.misterymatch.app.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.misterymatch.app.R;
import com.misterymatch.app.adapter.WhoLikesMeAdapter;
import com.misterymatch.app.model.WhoLikesMe;
import com.misterymatch.app.utils.GlobalData;
import com.misterymatch.app.utils.SharedHelper;
import com.misterymatch.app.webservice.APIError;
import com.misterymatch.app.webservice.ErrorUtils;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.misterymatch.app.utils.GlobalData.api;

public class WhoLikesMeActivity extends BaseActivity {

    @BindView(R.id.back)
    ImageView back;
    @BindView(R.id.title)
    TextView title;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.error_layout)
    LinearLayout errorLayout;

    List<WhoLikesMe> list;
    @BindView(R.id.who_likes_me)
    GridView whoLikesMe;

    WhoLikesMeAdapter adapter;
    @BindView(R.id.progress_bar)
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        title.setText(getString(R.string.who_likes_me));

        list = new ArrayList<>();
        adapter = new WhoLikesMeAdapter(this, list);
        whoLikesMe.setAdapter(adapter);
        whoLikesMe.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                GlobalData.USER = list.get(position).getUser();
                Intent intent = new Intent(WhoLikesMeActivity.this, ProfileDetailedActivity.class);
                intent.putExtra("whoLikesMe", true);
                intent.putExtra("isBottomEnabled", true);
                startActivity(intent);
            }
        });

    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_who_likes_me;
    }

    @Override
    public void onResume() {
        super.onResume();
        getWhoLikesMe();
    }

    @OnClick(R.id.back)
    void back() {
        onBackPressed();
    }

    private void getWhoLikesMe() {

        progressBar.setVisibility(View.VISIBLE);
        String accessToken = SharedHelper.getKey(this, "access_token");
        Call<List<WhoLikesMe>> call = api.findWhoLikesMe(accessToken);
        call.enqueue(new Callback<List<WhoLikesMe>>() {
            @Override
            public void onResponse(@NonNull Call<List<WhoLikesMe>> call, @NonNull Response<List<WhoLikesMe>> response) {
                progressBar.setVisibility(View.GONE);
                if (response.isSuccessful()) {
                    list.clear();
                    list.addAll(response.body());
                    adapter = new WhoLikesMeAdapter(WhoLikesMeActivity.this, list);
                    whoLikesMe.setAdapter(adapter);
                    if (list.size() > 0) {
                        errorLayout.setVisibility(View.GONE);
                    } else {
                        errorLayout.setVisibility(View.VISIBLE);
                    }

                } else {
                    if (response.code() == 401) {
                        SharedHelper.putKey(getApplicationContext(), "logged_in", false);
                        startActivity(new Intent(getApplicationContext(), SignInActivity.class));
                        finish();
                    } else {
                        APIError error = ErrorUtils.parseError(response);
                        showMessage(error.getError());
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<WhoLikesMe>> call, @NonNull Throwable t) {

            }
        });
    }

}
