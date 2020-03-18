package com.misterymatch.app.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.misterymatch.app.R;
import com.misterymatch.app.model.Interest;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
/**
 * Created by santhosh@appoets.com on 04-12-2017.
 */

public class InterestGridAdapter extends BaseAdapter {
    private Context mContext;
    private final List<Interest> list;
    private TextView textView;
    public List<Interest> selectedInterest = new ArrayList<>();

    public InterestGridAdapter(Context c, List<Interest> list, List<Interest> userInterest) {
        mContext = c;
        this.list = list;
        this.selectedInterest.addAll(userInterest);
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return list.size();
    }

    @Override
    public Interest getItem(int position) {
        // TODO Auto-generated method stub
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return 0;
    }

    @SuppressLint("InflateParams")
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub
        final Interest interest = list.get(position);
        View grid;
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            grid = new View(mContext);
            grid = inflater.inflate(R.layout.interest_grid_item, null);

            textView = (TextView) grid.findViewById(R.id.grid_text);
            CircleImageView imageView = (CircleImageView) grid.findViewById(R.id.grid_image);
            final CircleImageView tick = (CircleImageView) grid.findViewById(R.id.tick);
            LinearLayout itemView = (LinearLayout) grid.findViewById(R.id.item_view);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (tick.getVisibility() == View.VISIBLE) {
                        System.out.println("selectedInterest SIZE "+selectedInterest.size());

                        if(selectedInterest.remove(getInterestById(interest.getId()))){
                            tick.setVisibility(View.INVISIBLE);
                            System.out.println("REMOVED "+selectedInterest.size());
                        }

                    } else if (tick.getVisibility() == View.INVISIBLE) {
                        if(selectedInterest.add(getItem(position)))
                            tick.setVisibility(View.VISIBLE);
                    }
                }

            });
            textView.setText(interest.getName());

            Glide.with(mContext).load(interest.getImage()).apply(new RequestOptions().placeholder(R.drawable.ic_landscape).error(R.drawable.ic_landscape).fitCenter().dontAnimate()).into(imageView);

            if(selectedInterest.contains(getInterestById(interest.getId()))){
                tick.setVisibility(View.VISIBLE);
            }

            /*for (Interest in: selectedInterest) {
                if(interest.getId().equals(in.getId())){
                    tick.setVisibility(View.VISIBLE);
                }
            }*/

        } else {
            grid = convertView;
        }



        /*if(selectedInterest.contains(list.get(position))){
            tick.setVisibility(View.VISIBLE);
        }else {
            tick.setVisibility(View.INVISIBLE);
        }*/


        //imageView.setImageResource(R.dra);
        return grid;
    }

    public List<Interest> getSelectedInterests() {
        return selectedInterest;
    }

    public Interest getInterestById(Integer id) {
        for (Interest in : selectedInterest) {
            if(in.getId().equals(id)){
                return in;
            }
        }
        return null;
    }

}
