package com.misterymatch.app.activity;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.widget.TextView;

import com.misterymatch.app.R;
import com.misterymatch.app.model.Profile;
import com.misterymatch.app.utils.GlobalData;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by CSS03 on 17-01-2018.
 */

public class InviteFriends extends BaseActivity {

    @BindView(R.id.title)
    TextView title;
    @BindView(R.id.invite_code)
    TextView inviteCode;
    ClipboardManager clipboard;
    ClipData clip;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        title.setText(getString(R.string.invite_friends));

        if (GlobalData.PROFILE != null) {
            Profile profile = GlobalData.PROFILE;
            if (profile.getUser() != null) {
                inviteCode.setText(profile.getUser().getInviteCode());
            }
        }
    }

    @OnClick(R.id.invite_code)
    void copyLink() {
        clipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
        clip = ClipData.newPlainText("Copied to Clipboard!", inviteCode.getText().toString());
        if (clip.getItemCount() > 0) {
            showSnackBar("Copied to Clipboard!");
        }
        if (clipboard != null) {
            clipboard.setPrimaryClip(clip);
        }
    }

    @OnClick(R.id.done_invite)
    void share() {
        String playUrl = "https://play.google.com/store/apps/details?id=" + getApplicationContext().getPackageName();
        String appName = getString(R.string.app_name);
        String content = getString(R.string.invite_description, appName, playUrl, inviteCode.getText().toString());

        Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
        sharingIntent.setType("text/plain");
        sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Subject");
        sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, content);
        startActivity(Intent.createChooser(sharingIntent, "Share via"));
    }

    @OnClick(R.id.back)
    void back() {
        onBackPressed();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_invite_friends;
    }
}