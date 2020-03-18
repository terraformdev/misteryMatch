package com.misterymatch.app.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.misterymatch.app.MyApplication;
import com.misterymatch.app.R;
import com.misterymatch.app.chat.Chat;
import com.misterymatch.app.chat.ChatViewActivity;
import com.misterymatch.app.library.Log;
import com.misterymatch.app.model.MatchList;
import com.misterymatch.app.model.UserLike;
import com.misterymatch.app.utils.SharedHelper;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.misterymatch.app.utils.GlobalData.api;

public class ChatFragment extends Fragment {

    Unbinder unbinder;
    @BindView(R.id.chat_rv)
    RecyclerView chatRv;

    ChatAdapter chatAdapter;
    List<MatchList> list;
    @BindView(R.id.error_layout)
    LinearLayout errorLayout;
    FirebaseDatabase database;

    int userId = -1;
    String accessToken;

    public ChatFragment() {
    }

    public static ChatFragment newInstance() {
        return new ChatFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chat, container, false);
        accessToken = SharedHelper.getKey(getContext(), "access_token");
        unbinder = ButterKnife.bind(this, view);
        database = FirebaseDatabase.getInstance();
        userId = Integer.parseInt(SharedHelper.getKey(getContext(), "user_id", "-1"));
        list = new ArrayList<>();
        chatAdapter = new ChatAdapter(getContext(), list);
        chatRv.setLayoutManager(new LinearLayoutManager(getContext(),
                LinearLayoutManager.VERTICAL, false));
        chatRv.setItemAnimator(new DefaultItemAnimator());
        chatRv.setAdapter(chatAdapter);

        getMatchList();
        return view;
    }

    private void getMatchList() {
        Log.d("ChatFragment", "getMatch");
        Call<List<MatchList>> call = api.getMatchList(accessToken);
        call.enqueue(new Callback<List<MatchList>>() {
            @Override
            public void onResponse(@NonNull Call<List<MatchList>> call,
                                   @NonNull Response<List<MatchList>> response) {
                if (response.isSuccessful()) {
                    list.clear();
                    list.addAll(response.body());
                    chatAdapter.notifyDataSetChanged();
                    if (list.size() > 0) {
                        errorLayout.setVisibility(View.GONE);
                        findNewMessages(list);
                    } else {
                        errorLayout.setVisibility(View.VISIBLE);
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<MatchList>> call, @NonNull Throwable t) {
                Log.e("ChatFragment", t.toString());
            }
        });
    }

    private void findNewMessages(final List<MatchList> mList) {
        String chatPath = "";
        for (final MatchList matchList : mList) {
            if (userId < matchList.getLikeId()) {
                chatPath = userId + "_chats_" + matchList.getLikeId();
            } else {
                chatPath = matchList.getLikeId() + "_chats_" + userId;
            }
            database.getReference().child(chatPath).limitToLast(1).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for (DataSnapshot messageSnapshot : dataSnapshot.getChildren()) {

                        Chat chat = null;
                        try {
                            chat = messageSnapshot.getValue(Chat.class);
                        } catch (Throwable e) {
                            e.printStackTrace();
                        }

                        //Chat chat = messageSnapshot.getValue(Chat.class);
                        if (chat != null && chat.getType() != null) {
                            if (chat.getType().equals("text")) {
                                matchList.setLastMessage(chat.getText());
                                Log.d("MESSAGE", chat.getText());
                            } else if (chat.getType().equals("image")) {
                                matchList.setLastMessage(getString(R.string.uploaded_image));
                            } else if (chat.getType().equals("video")) {
                                matchList.setLastMessage(getString(R.string.uploaded_video));
                            }
                            matchList.setTimestamp(chat.getTimestamp());

                            list = mList;
                            chatAdapter.notifyDataSetChanged();
                        }
                    }

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });


        }
    }


    private class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.MyViewHolder> {

        private List<MatchList> list;
        private Context context;

        private ChatAdapter(Context context, List<MatchList> list) {
            this.context = context;
            this.list = list;
        }

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.list_item_chat, parent, false);

            return new MyViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(MyViewHolder holder, int position) {
            MatchList obj = list.get(position);
            UserLike userLike = obj.getUserLike();
            holder.firstName.setText(userLike.getFirstName());
            holder.lastMessage.setText(obj.getLastMessage());
            Glide.with(context).load(userLike.getPicture()).apply(new RequestOptions().placeholder(R.drawable.user).error(R.drawable.user).dontAnimate()).into(holder.avatar);
            if (obj.getTimestamp() != -1) {
                holder.dateTime.setText(MyApplication.getDisplayableTime(obj.getTimestamp()));
            }
        }

        @Override
        public int getItemCount() {
            return list.size();
        }

        public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
            private TextView firstName, dateTime, lastMessage;
            private ImageView avatar;
            private LinearLayout itemView;

            private MyViewHolder(View view) {
                super(view);
                firstName = (TextView) view.findViewById(R.id.first_name);
                dateTime = (TextView) view.findViewById(R.id.date_time);
                lastMessage = (TextView) view.findViewById(R.id.last_message);
                avatar = (ImageView) view.findViewById(R.id.avatar);
                itemView = (LinearLayout) view.findViewById(R.id.item_view);
                itemView.setOnClickListener(this);
            }

            @Override
            public void onClick(View v) {
                MatchList matchList = list.get(getAdapterPosition());
                UserLike userLike = matchList.getUserLike();
                Intent intent = new Intent(getContext(), ChatViewActivity.class);
                intent.putExtra("sender", userLike.getId());
                intent.putExtra("sender_name", userLike.getFirstName());
                intent.putExtra("sender_avatar", userLike.getPicture());
                intent.putExtra("device_token", userLike.getDeviceToken());
                intent.putExtra("sender_mobile", userLike.getMobile());
                startActivity(intent);
            }
        }

    }
}
