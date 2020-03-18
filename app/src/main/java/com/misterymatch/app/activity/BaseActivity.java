package com.misterymatch.app.activity;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import com.misterymatch.app.utils.CodeSnippet;
import com.misterymatch.app.utils.CustomDialog;

import butterknife.ButterKnife;


/**
 * Created by CSS03 on 09-01-2018.
 */

public abstract class BaseActivity extends AppCompatActivity {

    protected View mParentView;
    CodeSnippet mCodeSnippet;
    int PICK_CARD_REQUEST = 11;
    CustomDialog mCustomDialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutId());
        mParentView = getWindow().getDecorView().findViewById(android.R.id.content);
        mCodeSnippet = new CodeSnippet(this);
        injectViews();
    }

    public void injectViews() {
        ButterKnife.bind(this);

    }

    public void showMessage(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    public void showMessage(int resId) {
        Toast.makeText(this, getString(resId), Toast.LENGTH_SHORT).show();
    }

    public void showSnackBar(String message) {
        if (mParentView != null) {
            Snackbar snackbar = Snackbar.make(mParentView, message, Snackbar.LENGTH_LONG);
            snackbar.setActionTextColor(Color.RED);
            snackbar.show();
        }
    }

    public FragmentActivity getActivity() {
        return this;
    }

    public void showProgressBar() {
        getProgressBar().show();
    }

    public void dismissProgressBar() {
        try {
            getProgressBar().dismissProgress();
        } catch (Exception e) {
            showMessage(e.getMessage());
        }
    }

    public CustomDialog getProgressBar() {
        if (mCustomDialog == null) {
            mCustomDialog = new CustomDialog(this);
        }
        return mCustomDialog;
    }

    protected abstract int getLayoutId();
}
