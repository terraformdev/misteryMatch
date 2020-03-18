package com.misterymatch.app.adapter;

import android.app.Activity;
import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.misterymatch.app.R;
import com.misterymatch.app.library.Log;
import com.misterymatch.app.model.User;

import java.util.List;
import java.util.Locale;

/**
 * Created by santhosh@appoets.com on 23-11-2017.
 */

public class CardsAdapter extends ArrayAdapter<User> {
    private final List<User> list;
    private final LayoutInflater layoutInflater;
    LinearLayout indicator;
    private Activity activity;
    private int img_position = 0;

    public CardsAdapter(Activity activity, List<User> cards) {
        super(activity, cards.size());
        this.activity = activity;
        this.list = cards;
        this.layoutInflater = LayoutInflater.from(activity);
    }

    @Override
    public View getView(final int position, final View convertView, ViewGroup parent) {

        if (list.size() == 0) {
            Log.d("CardsAdapter : ", "size : " + list.size());
        }

        final User card = list.get(position);

        img_position = 0;
        View view = convertView;
        LayoutInflater inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (view == null)
            view = inflater.inflate(R.layout.datesauce_card_view, parent, false);

        final ImageView avatar = view.findViewById(R.id.avatar);
        View previous = view.findViewById(R.id.previous);
        View next = view.findViewById(R.id.next);
        TextView name = view.findViewById(R.id.name);
        TextView age = view.findViewById(R.id.age);
        TextView distance = view.findViewById(R.id.distance);
        distance.setText(String.format(Locale.getDefault(), "%.0f Km Away", card.getDistance()));
        indicator = view.findViewById(R.id.indicator);
        final ImageView[] dots = new ImageView[card.getUserImages().size()];
        /*info.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                GlobalData.avatar = card.urls[0];
                Intent intent = new Intent(activity, ProfileDetailedActivity.class);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(activity, avatar, "avatar");
                    activity.startActivity(intent, options.toBundle());
                } else {
                    activity.startActivity(intent);
                }
            }
        });*/

        next.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (!card.getUserImages().isEmpty() && img_position < card.getUserImages().size() - 1) {
                    img_position++;
                    Glide.with(activity).load(card.getUserImages().get(img_position).getImage()).apply(new RequestOptions().diskCacheStrategy(DiskCacheStrategy.ALL)).into(avatar);
                    for (int i = 0; i < card.getUserImages().size(); i++) {
                        dots[i].setBackgroundColor(ContextCompat.getColor(activity, R.color.white));
                    }
                    dots[img_position].setBackgroundColor(ContextCompat.getColor(activity, R.color.colorAccent));
                }
            }
        });

        previous.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (!card.getUserImages().isEmpty() && img_position >= 1) {
                    img_position--;
                    Glide.with(activity).load(card.getUserImages().get(img_position).getImage()).apply(new RequestOptions().diskCacheStrategy(DiskCacheStrategy.ALL)).into(avatar);
                    for (int i = 0; i < card.getUserImages().size(); i++) {
                        System.out.println(dots[i].getBackground().toString());
                        dots[i].setBackgroundColor(ContextCompat.getColor(activity, R.color.white));
                    }
                    dots[img_position].setBackgroundColor(ContextCompat.getColor(activity, R.color.colorAccent));
                }
            }
        });

        addIndicator(dots, card.getUserImages().size());

        name.setText(card.getFirstName());
        if (!card.getUserImages().isEmpty()) {
            for (int i = 0; i < card.getUserImages().size(); i++) {
                Glide.with(activity).load(card.getUserImages().get(i).getImage()).apply(new RequestOptions().diskCacheStrategy(DiskCacheStrategy.ALL)).into(avatar);
            }
        }

        if (card.getAge() != null)
            age.setText(String.format(Locale.getDefault(), "%s %d", activity.getString(R.string.age), card.getAge()));
        else
            age.setText(activity.getString(R.string.age) + " " + "N/A");

        ImageView like = view.findViewById(R.id.like);
        ImageView dislike = view.findViewById(R.id.dislike);
        ImageView superlike = view.findViewById(R.id.superlike);
        like.setImageAlpha(0);
        dislike.setImageAlpha(0);
        superlike.setImageAlpha(0);

        return view;
    }

    private void addIndicator(ImageView[] dots, int dotsCount) {
        //dots = new ImageView[dotsCount];
        if (dotsCount == 0)
            return;

        indicator.removeAllViews();

        for (int i = 0; i < dotsCount; i++) {
            dots[i] = new ImageView(activity);
            float height = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 2, activity.getResources().getDisplayMetrics());
            dots[i].setBackgroundColor(activity.getResources().getColor(R.color.colorWhite));
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    (int) height
            );
            params.weight = 1;
            params.setMargins(4, 4, 4, 4);
            indicator.addView(dots[i], params);
        }

        dots[0].setBackgroundColor(activity.getResources().getColor(R.color.colorAccent));
    }

    @Override
    public User getItem(int position) {
        return list.get(position);
    }

    @Override
    public int getCount() {
        return list.size();
    }

}
