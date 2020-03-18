package com.misterymatch.app.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.widget.TextView;

import com.misterymatch.app.R;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by CSS03 on 07-02-2018.
 */

public class LanguageActivity extends BaseActivity {

    @BindView(R.id.title)
    TextView title;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        title.setText(getString(R.string.language));
    }

    @OnClick(R.id.eng)
    void eng() {
        showMessage("Oops! Language got already Selected");
    }

    @OnClick(R.id.back)
    void back() {
        onBackPressed();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_select_language;
    }
}
