package com.misterymatch.app.fragment;
import android.app.Dialog;
import android.content.Context;
import android.view.Window;

import com.misterymatch.app.R;

/**
 * Created by CSS12 on 31-01-2018.
 */

class PremiumDialog extends Dialog {

    PremiumDialog(Context context) {
        super(context);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.premium_dialog);
        setCancelable(true);
    }

}
