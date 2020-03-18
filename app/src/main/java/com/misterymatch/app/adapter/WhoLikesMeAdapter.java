package com.misterymatch.app.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.misterymatch.app.R;
import com.misterymatch.app.model.User;
import com.misterymatch.app.model.WhoLikesMe;

import java.util.List;

/**
 * Created by santhosh@appoets.com on 04-12-2017.
 */

public class WhoLikesMeAdapter extends BaseAdapter {
    private Context mContext;
    private List<WhoLikesMe> list;

    public WhoLikesMeAdapter(Context c, List<WhoLikesMe> list) {
        mContext = c;
        this.list = list;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public WhoLikesMe getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub
        final User obj = list.get(position).getUser();
        View grid;
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            grid = new View(mContext);
            grid = inflater.inflate(R.layout.who_likes_me_grid_item, null);
        } else {
            grid = convertView;
        }

        TextView name = (TextView) grid.findViewById(R.id.name);
        ImageView picture = (ImageView) grid.findViewById(R.id.picture);
        if (obj != null) {
            name.setText(obj.getFirstName());
            Glide.with(mContext.getApplicationContext()).load(obj.getPicture()).apply(new RequestOptions().placeholder(R.drawable.user).error(R.drawable.user).fitCenter().dontAnimate()).into(picture);
        }

        return grid;
    }
}
