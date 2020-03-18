package com.misterymatch.app.utils;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.ProgressBar;


public class CustomDialog extends Dialog {

/*    public CustomDialog(Context context) {
        super(context);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.custom_dialog);
        setCancelable(false);
    }*/

    public CustomDialog(Context context) throws NullPointerException {
        super(context);
        createProgressBar(context);
    }

    private void createProgressBar(Context context) throws NullPointerException {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(getSmallProgressbar(context));
        Window window = getWindow();
        if (window != null) {
            window.setLayout(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            window.setBackgroundDrawableResource(android.R.color.transparent);
            setCanceledOnTouchOutside(false);
            setCancelable(false);
        } else {
            throw new NullPointerException("window not fount exception!");
        }
    }

    private View getSmallProgressbar(Context context) {
        ProgressBar progressBar = new ProgressBar(context, null,
                android.R.attr.progressBarStyleLarge);
        progressBar.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT));
        return progressBar;
    }

    public void dismissProgress() throws Exception {
        super.dismiss();
    }

    @Override
    public void dismiss() {
        super.dismiss();
    }
}
