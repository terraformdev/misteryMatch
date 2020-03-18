package com.misterymatch.app.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.misterymatch.app.MyApplication;
import com.misterymatch.app.R;
import com.misterymatch.app.model.Notification;
import com.misterymatch.app.model.Notifications;
import com.misterymatch.app.model.User;
import com.misterymatch.app.utils.GlobalData;
import com.misterymatch.app.utils.SharedHelper;
import com.misterymatch.app.webservice.APIError;
import com.misterymatch.app.webservice.ErrorUtils;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


import static com.misterymatch.app.utils.GlobalData.api;

public class NotificationActivity extends AppCompatActivity {

    @BindView(R.id.back)
    ImageView back;
    @BindView(R.id.title)
    TextView title;
    @BindView(R.id.error_layout)
    LinearLayout errorLayout;
    @BindView(R.id.notification_rv)
    RecyclerView notificationRv;

    NotificationAdapter adapter;
    List<Notification> list;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);
        ButterKnife.bind(this);

        title.setText(getString(R.string.notifications));

        list = new ArrayList<>();
        adapter = new NotificationAdapter(this, list);
        notificationRv.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        notificationRv.setItemAnimator(new DefaultItemAnimator());
        notificationRv.setAdapter(adapter);

        getNotifications();

    }

    private void getNotifications() {
        System.out.println("CALLED getFindMatch");
        String accessToken = SharedHelper.getKey(this, "access_token");
        Call<Notifications> call = api.getNotification(accessToken);
        call.enqueue(new Callback<Notifications>() {
            @Override
            public void onResponse(@NonNull Call<Notifications> call, @NonNull Response<Notifications> response) {
                if (response.isSuccessful()) {
                    list.clear();
                    list.addAll(response.body().getNotifications());
                    adapter.notifyDataSetChanged();
                    if (list.size() > 0) {
                        errorLayout.setVisibility(View.GONE);
                    } else {
                        errorLayout.setVisibility(View.VISIBLE);
                    }
                } else {
                    APIError error = ErrorUtils.parseError(response);
                    Toast.makeText(getApplicationContext(), error.getError(), Toast.LENGTH_SHORT).show();

                }
            }

            @Override
            public void onFailure(@NonNull Call<Notifications> call, @NonNull Throwable t) {
            }
        });
    }

    @OnClick(R.id.back)
    public void onViewClicked() {
        finish();
    }

    private class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.MyViewHolder> {

        private List<Notification> list;
        private Context context;

        public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
            private TextView status, dateTime;
            private ImageView avatar;
            private LinearLayout itemView;

            private MyViewHolder(View view) {
                super(view);
                status = (TextView) view.findViewById(R.id.status);
                dateTime = (TextView) view.findViewById(R.id.date_time);
                avatar = (ImageView) view.findViewById(R.id.avatar);
                itemView = (LinearLayout) view.findViewById(R.id.item_view);
                itemView.setOnClickListener(this);
            }

            @Override
            public void onClick(View v) {
                Notification obj = list.get(getAdapterPosition());
                if(obj.getStatus().equals("like") ||obj.getStatus().equals("dislike")||obj.getStatus().equals("superlike")){
                    GlobalData.USER = obj.getUser();
                    Intent intent = new Intent(NotificationActivity.this, ProfileDetailedActivity.class);
                    intent.putExtra("isBottomEnabled", true);
                    intent.putExtra("whoLikesMe", true);
                    startActivity(intent);
                }
            }
        }


        private NotificationAdapter(Context context, List<Notification> list) {
            this.context = context;
            this.list = list;
        }

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.list_item_notification, parent, false);

            return new MyViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(MyViewHolder holder, int position) {
            Notification obj = list.get(position);

            User user = obj.getUser();
            Glide.with(context).load(user.getPicture()).apply(new RequestOptions().placeholder(R.drawable.user).error(R.drawable.user).dontAnimate()).into(holder.avatar);
            holder.dateTime.setText(MyApplication.getDisplayableTime(obj.getCreatedAt()));
            assert obj.getStatus() != null;
            switch (obj.getStatus()) {
                case "like":
                    holder.status.setText(context.getString(R.string._liked_your_profile, user.getFirstName()));
                    break;
                case "superlike":
                    holder.status.setText(context.getString(R.string._superliked_your_profile, user.getFirstName()));
                    break;
                case "dislike":
                    holder.status.setText(context.getString(R.string._disliked_your_profile, user.getFirstName()));
                    break;
                case "friend":
                    holder.status.setText(context.getString(R.string._added_you_as_friend, user.getFirstName()));
                    break;
                case "recommend":
                    holder.status.setText(context.getString(R.string._recommended_your_profile, user.getFirstName()));
                    break;
                case "wallet":
                    holder.status.setText(context.getString(R.string._sent_the_amount_to_your_wallet, user.getFirstName()));
                    break;
                default:
                    break;
            }

        }

        @Override
        public int getItemCount() {
            return list.size();
        }
    }
}
