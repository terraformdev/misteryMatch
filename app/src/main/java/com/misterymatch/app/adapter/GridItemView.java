package com.misterymatch.app.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.misterymatch.app.R;

/**
 * Created by santhosh@appoets.com on 05-12-2017.
 */

public class GridItemView  extends FrameLayout {
    private TextView textView;
    private ImageView grid_image;

    public GridItemView(Context context) {
        super(context);
        LayoutInflater.from(context).inflate(R.layout.interest_grid_item, this);
        textView = (TextView) getRootView().findViewById(R.id.grid_text);
        grid_image = (ImageView) getRootView().findViewById(R.id.grid_image);
    }

    public void display(String text, int drawable, boolean isSelected) {
        textView.setText(text);
        display(isSelected);
    }

    public void display(boolean isSelected) {
        //textView.setBackgroundResource(isSelected ? R.drawable.ic_back_arrow_white : R.drawable.ic_earth_globe);
        grid_image.setImageResource(isSelected ? R.drawable.ic_back_arrow_white : 0);
    }
}
