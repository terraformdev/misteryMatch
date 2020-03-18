package com.misterymatch.app.chat;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.misterymatch.app.MyApplication;
import com.misterymatch.app.R;
import com.misterymatch.app.utils.SharedHelper;

import java.util.List;
import java.util.Objects;

/**
 * Created by santhosh@appoets.com on 23-01-2018.
 */

public class ChatMessageAdapter extends ArrayAdapter<Chat> {
    private static final int MY_MESSAGE = 0, OTHER_MESSAGE = 1, MY_IMAGE = 2, OTHER_IMAGE = 3;
    private Integer userId = -1;
    private Context context;
    String senderAvatar;

    public ChatMessageAdapter(Context context, List<Chat> data, String senderAvatar) {
        super(context, R.layout.item_mine_message, data);
        this.context = context;
        this.userId = Integer.parseInt(SharedHelper.getKey(context, "user_id", "-1"));
        this.senderAvatar = senderAvatar;
    }

    @Override
    public int getViewTypeCount() {
        // my message, other message, my image, other image
        return 2;
    }

    @Override
    public int getItemViewType(int position) {
        Chat item = getItem(position);

        if (Objects.equals(item.getUser(), userId)) {
            return MY_MESSAGE;
        } else
            return OTHER_MESSAGE;

        /*if (item.getIsMine()) return MY_MESSAGE;
        else return OTHER_MESSAGE;
*/
        /*if (item.isMine() && !item.isImage()) return MY_MESSAGE;
        else if (!item.isMine() && !item.isImage()) return OTHER_MESSAGE;
        else if (item.isMine() && item.isImage()) return MY_IMAGE;
        else return OTHER_IMAGE;*/
    }

    @SuppressLint("ClickableViewAccessibility")
    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        int viewType = getItemViewType(position);
        final Chat chat = getItem(position);
        if (viewType == MY_MESSAGE) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_mine_message, parent, false);
            if (chat.getType().equals("text")) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_mine_message, parent, false);
                TextView textView = (TextView) convertView.findViewById(R.id.text);
                textView.setText(getItem(position).getText());
                TextView timeStamp = (TextView) convertView.findViewById(R.id.time_stamp);
                timeStamp.setText(MyApplication.getDisplayableTime(chat.getTimestamp()));
                if (chat.getRead() == 1) {
                    timeStamp.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_double_tick, 0);
                }
            } else if (chat.getType().equals("image")) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_mine_image, parent, false);
                ImageView mine_image = (ImageView) convertView.findViewById(R.id.mine_image);
                mine_image.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        Intent i = new Intent(getContext(), PhotoViewActivity.class);
                        i.putExtra("url", chat.getUrl());
                        context.startActivity(i);
                    }
                });
                Glide.with(context).load(chat.getUrl()).apply(new RequestOptions().placeholder(R.drawable.user).error(R.drawable.user).centerCrop().dontAnimate()).into(mine_image);
                TextView timeStamp = (TextView) convertView.findViewById(R.id.time_stamp);
                timeStamp.setText(MyApplication.getDisplayableTime(chat.getTimestamp()));
                if (chat.getRead() == 1) {
                    timeStamp.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_double_tick, 0);
                }
            } else if (chat.getType().equals("video")) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_mine_video, parent, false);
                ImageView mine_video = (ImageView) convertView.findViewById(R.id.mine_video);
                final Uri uri = Uri.parse(chat.getUrl());
                Glide.with(context)
                        .load(chat.getUrl())
                        .into(mine_video);
                mine_video.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                        intent.setDataAndType(uri, "video/*");
                        context.startActivity(intent);
                    }
                });
                TextView timeStamp = (TextView) convertView.findViewById(R.id.time_stamp);
                timeStamp.setText(MyApplication.getDisplayableTime(chat.getTimestamp()));
                if (chat.getRead() == 1) {
                    timeStamp.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_double_tick, 0);
                }
            }
        } else {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_other_message, parent, false);
            if (chat.getType().equals("text")) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_other_message, parent, false);
                TextView textView = (TextView) convertView.findViewById(R.id.text);
                ImageView sender_avatar = (ImageView) convertView.findViewById(R.id.sender_avatar);
                textView.setText(getItem(position).getText());
                Glide.with(context).load(senderAvatar).apply(new RequestOptions().placeholder(R.drawable.user).error(R.drawable.user).centerCrop().dontAnimate()).into(sender_avatar);
                TextView timeStamp = (TextView) convertView.findViewById(R.id.time_stamp);
                timeStamp.setText(MyApplication.getDisplayableTime(chat.getTimestamp()));
            } else if (chat.getType().equals("image")) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_other_image, parent, false);
                ImageView other_image = (ImageView) convertView.findViewById(R.id.other_image);
                other_image.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        Intent i = new Intent(getContext(), PhotoViewActivity.class);
                        i.putExtra("url", chat.getUrl());
                        context.startActivity(i);
                    }
                });
                Glide.with(context).load(chat.getUrl()).apply(new RequestOptions().placeholder(R.drawable.user).error(R.drawable.user).centerCrop().dontAnimate()).into(other_image);
                ImageView sender_avatar = (ImageView) convertView.findViewById(R.id.sender_avatar);
                Glide.with(context).load(senderAvatar).apply(new RequestOptions().placeholder(R.drawable.user).error(R.drawable.user).centerCrop().dontAnimate()).into(sender_avatar);
                TextView timeStamp = (TextView) convertView.findViewById(R.id.time_stamp);
                timeStamp.setText(MyApplication.getDisplayableTime(chat.getTimestamp()));
            } else if (chat.getType().equals("video")) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_other_video, parent, false);
                ImageView sender_avatar = (ImageView) convertView.findViewById(R.id.sender_avatar);
                Glide.with(context).load(senderAvatar).apply(new RequestOptions().placeholder(R.drawable.user).error(R.drawable.user).centerCrop().dontAnimate()).into(sender_avatar);
                ImageView other_video = (ImageView) convertView.findViewById(R.id.other_video);
                final Uri uri = Uri.parse(chat.getUrl());
                Glide.with(context)
                        .load(chat.getUrl())
                        .into(other_video);
                other_video.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                        intent.setDataAndType(uri, "video/*");
                        context.startActivity(intent);
                    }
                });

                TextView timeStamp = (TextView) convertView.findViewById(R.id.time_stamp);
                timeStamp.setText(MyApplication.getDisplayableTime(chat.getTimestamp()));
            }
        }

        /* else if(viewType == OTHER_MESSAGE){
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_other_message, parent, false);

            TextView textView = (TextView) convertView.findViewById(R.id.text);
            //textView.setText(getItem(position).getContent());
        } else if (viewType == MY_IMAGE) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_mine_image, parent, false);
        } else {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_other_image, parent, false);
        }*/

        /*convertView.findViewById(R.id.chatMessageView).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getContext(), "onClick", Toast.LENGTH_LONG).show();
            }
        });*/


        return convertView;
    }
}
