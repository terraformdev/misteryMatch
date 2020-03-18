package com.misterymatch.app.fragment;


import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.misterymatch.app.R;
import com.misterymatch.app.activity.EditProfileActivity;
import com.misterymatch.app.activity.InviteFriends;
import com.misterymatch.app.activity.NotificationActivity;
import com.misterymatch.app.activity.PremiumActivity;
import com.misterymatch.app.activity.SettingsActivity;
import com.misterymatch.app.activity.WalletActivity;
import com.misterymatch.app.activity.WhoLikesMeActivity;
import com.misterymatch.app.model.Profile;
import com.misterymatch.app.model.User;
import com.misterymatch.app.utils.GlobalData;
import com.misterymatch.app.utils.SharedHelper;
import com.misterymatch.app.webservice.APIError;
import com.misterymatch.app.webservice.ErrorUtils;

import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfileFragment extends Fragment {

    @BindView(R.id.avatar)
    CircleImageView avatar;
    @BindView(R.id.settings)
    LinearLayout settings;
    @BindView(R.id.edit)
    LinearLayout edit;
    @BindView(R.id.wallet)
    LinearLayout wallet;
    @BindView(R.id.likes)
    LinearLayout likes;
    @BindView(R.id.invite)
    LinearLayout invite;
    @BindView(R.id.notification)
    LinearLayout notification;
    @BindView(R.id.premium)
    Button premium;
    Unbinder unbinder;
    @BindView(R.id.name)
    TextView name;
    @BindView(R.id.work)
    TextView work;
    @BindView(R.id.app_version)
    TextView appVersion;

    public ProfileFragment() {
    }

    public static ProfileFragment newInstance() {
        return new ProfileFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        unbinder = ButterKnife.bind(this, view);

        try {
            PackageInfo pInfo = getContext().getPackageManager().getPackageInfo(getContext().getPackageName(), 0);
            int version = pInfo.versionCode;
            appVersion.setText(getContext().getString(R.string.app_version, version));
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        initProfileView();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @OnClick({R.id.avatar, R.id.settings, R.id.edit, R.id.wallet, R.id.likes, R.id.invite, R.id.notification, R.id.premium})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.avatar:
                break;
            case R.id.settings:
                startActivity(new Intent(getContext(), SettingsActivity.class));
                break;
            case R.id.edit:
                startActivity(new Intent(getContext(), EditProfileActivity.class));
                break;
            case R.id.wallet:
                startActivity(new Intent(getContext(), WalletActivity.class));
                break;
            case R.id.likes:
                startActivity(new Intent(getContext(), WhoLikesMeActivity.class));
                break;
            case R.id.invite:
                startActivity(new Intent(getContext(), InviteFriends.class));
                break;
            case R.id.notification:
                startActivity(new Intent(getContext(), NotificationActivity.class));
                break;
            case R.id.premium:
                startActivity(new Intent(getContext(), PremiumActivity.class));
                break;
        }
    }

    public void initProfileView() {
        if (GlobalData.PROFILE != null) {
            User user = GlobalData.PROFILE.getUser();
            if (user != null) {
                name.setText(user.getFirstName());
                work.setText(user.getWork());
                Glide.with(this).load(user.getPicture()).apply(new RequestOptions().placeholder(R.drawable.user).error(R.drawable.user).dontAnimate()).into(avatar);
            }
        } else {
            getProfile();
        }
    }

    private void getProfile() {
        String accessToken = SharedHelper.getKey(getContext(), "access_token");
        System.out.println("getProfile Header " + accessToken);
        Call<Profile> call = GlobalData.api.getProfile(accessToken, new HashMap<String, Object>());
        call.enqueue(new Callback<Profile>() {
            @Override
            public void onResponse(@NonNull Call<Profile> call, @NonNull Response<Profile> response) {
                if (response.isSuccessful()) {
                    GlobalData.PROFILE = response.body();
                    Log.d("getProfile PRO_FRAG", GlobalData.PROFILE.toString());
                    initProfileView();
                } else {
                    APIError error = ErrorUtils.parseError(response);
                    Toast.makeText(getContext(), error.getError(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<Profile> call, @NonNull Throwable t) {
            }
        });
    }
}
