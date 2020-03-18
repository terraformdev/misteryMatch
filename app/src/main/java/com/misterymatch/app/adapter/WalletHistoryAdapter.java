package com.misterymatch.app.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.afollestad.sectionedrecyclerview.SectionedRecyclerViewAdapter;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.misterymatch.app.MyApplication;
import com.misterymatch.app.R;
import com.misterymatch.app.model.TransactionHistory;
import com.misterymatch.app.model.TransactionHistoryModel;
import com.misterymatch.app.utils.SharedHelper;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Created by santhosh@appoets.com on 22-01-2018.
 */

public class WalletHistoryAdapter extends SectionedRecyclerViewAdapter<WalletHistoryAdapter.ViewHolder> {

    private List<TransactionHistoryModel> list = new ArrayList<>();
    private LayoutInflater inflater;
    Context context;
    Integer userId = -1;
    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
    SimpleDateFormat format2 = new SimpleDateFormat("MMMM dd, hh:mm a", Locale.getDefault());

    public WalletHistoryAdapter(Context context, List<TransactionHistoryModel> list) {
        this.context = context;
        this.inflater = LayoutInflater.from(context);
        this.list = list;
        userId = Integer.parseInt(SharedHelper.getKey(context, "user_id", "-1"));

    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v;
        switch (viewType) {
            case VIEW_TYPE_HEADER:
                v = inflater.inflate(R.layout.header, parent, false);
                return new ViewHolder(v, true);
            case VIEW_TYPE_ITEM:
                v = inflater.inflate(R.layout.list_item_wallet_history, parent, false);
                return new ViewHolder(v, false);
            default:
                v = inflater.inflate(R.layout.list_item_wallet_history, parent, false);
                return new ViewHolder(v, false);
        }
    }

    @Override
    public int getSectionCount() {
        return list.size();
    }


    @Override
    public int getItemCount(int section) {
        return list.get(section).getTransactionHistory().size();
    }

    @Override
    public void onBindHeaderViewHolder(WalletHistoryAdapter.ViewHolder holder, final int section) {
        holder.header.setText(list.get(section).getHeader());
        holder.header.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println(list.get(section).getHeader());
            }
        });
    }

    @Override
    public void onBindViewHolder(WalletHistoryAdapter.ViewHolder holder, int section, int relativePosition, int absolutePosition) {
        final TransactionHistory object = list.get(section).getTransactionHistory().get(relativePosition);
        try {
            Date date = format.parse(object.getCreatedAt());
            holder.date_time.setText(format2.format(date));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        holder.amount.setText(MyApplication.numberFormat.format(object.getAmount()));

        if(object.getReceiverId().equals(userId)){
            object.setStatus("RECEIVED");
        }

        switch (object.getStatus()) {
            case "ADDED":
                holder.status.setText(context.getString(R.string.added_to_wallet));
                holder.description.setText("");
                holder.date_time.setCompoundDrawablesWithIntrinsicBounds( R.drawable.ic_up_arrow, 0, 0, 0);
                Glide.with(context).load(R.drawable.add_to_wallet).apply(new RequestOptions().dontAnimate()).into(holder.status_image);
                break;
            case "PAID":
                holder.status.setText(context.getString(R.string.cash_sent));
                holder.date_time.setCompoundDrawablesWithIntrinsicBounds( R.drawable.ic_down_arrow, 0, 0, 0);
                Glide.with(context).load(R.drawable.cash_sent).apply(new RequestOptions().dontAnimate()).into(holder.status_image);
                if(object.getReceiver()!=null){
                    holder.description.setText(context.getString(R.string.to_, object.getReceiver().getFirstName()));
                }
                break;
            case "RECEIVED":
                holder.status.setText(context.getString(R.string.cash_received));
                holder.date_time.setCompoundDrawablesWithIntrinsicBounds( R.drawable.ic_down_arrow, 0, 0, 0);
                Glide.with(context).load(R.drawable.amount_receive).apply(new RequestOptions().dontAnimate()).into(holder.status_image);
                if(object.getReceiver()!=null){
                    holder.description.setText(context.getString(R.string.to_, object.getReceiver().getFirstName()));
                }
                break;
            case "PREMIUM":
                holder.status.setText(context.getString(R.string.paid_for_subscription));
                holder.description.setText("");
                holder.date_time.setCompoundDrawablesWithIntrinsicBounds( R.drawable.ic_up_arrow, 0, 0, 0);
                Glide.with(context).load(R.drawable.cash_sent).apply(new RequestOptions().dontAnimate()).into(holder.status_image);
                break;
            default:
                break;
        }

        /*holder.itemLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                object.getShop();
            }
        });*/

    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView status, header;
        TextView description;
        TextView date_time;
        TextView amount;
        ImageView status_image;
        LinearLayout itemLayout;

        public ViewHolder(View itemView, boolean isHeader) {
            super(itemView);
            if (isHeader) {
                header = (TextView) itemView.findViewById(R.id.header);
            } else {
                status = (TextView) itemView.findViewById(R.id.status);
                description = (TextView) itemView.findViewById(R.id.description);
                date_time = (TextView) itemView.findViewById(R.id.date_time);
                amount = (TextView) itemView.findViewById(R.id.amount);
                status_image = (ImageView) itemView.findViewById(R.id.status_image);
                itemLayout = (LinearLayout) itemView.findViewById(R.id.item_view);
            }

        }

    }
}
