package com.misterymatch.app.adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.misterymatch.app.R;
import com.misterymatch.app.library.Log;
import com.misterymatch.app.model.Premium;

import java.util.List;

/**
 * Created by santhosh@appoets.com on 31-01-2018.
 */

public class PremiumAdapter extends RecyclerView.Adapter<PremiumAdapter.MyViewHolder> {

    private List<Premium> list;
    private int selectedPlan;
    private Context context;
    private int lastCheckedPosition = -1;

    public PremiumAdapter(Context context, List<Premium> list, int selectedPlan) {
        this.context = context;
        this.list = list;
        this.selectedPlan = selectedPlan;
    }

    public int getSelectedPlan() {
        return selectedPlan;
    }

    public void setSelectedPlan(int selectedPlan) {
        this.selectedPlan = selectedPlan;
    }

    public Premium getLastSelectedItem() {
        if (list.size() > 0) {
            if (lastCheckedPosition != -1) {
                return list.get(lastCheckedPosition);
            }
        }
        return null;
    }

    @Override
    public PremiumAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_premium, parent, false);

        return new PremiumAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(PremiumAdapter.MyViewHolder holder, int position) {
        Premium obj = list.get(position);
        holder.plan_name.setText(obj.getPlanName());
        holder.duration.setText(String.valueOf(obj.getDuration()));
        holder.period.setText(String.valueOf(obj.getGem()));
        Log.d("PremiumAdapter", "Adapter Position : " + holder.getAdapterPosition());
        Log.d("PremiumAdapter", "Position : " + position);
        Log.d("PremiumAdapter", "Selected Plan : " + selectedPlan);


        if (position == lastCheckedPosition && getSelectedPlan() == -1) {
            holder.item_view.setCardBackgroundColor(context.getResources().getColor(R.color.colorAccent));
            holder.plan_name.setBackground(context.getResources().getDrawable(R.drawable.rounded_btn2));
            holder.duration.setTextColor(Color.WHITE);
            holder.period.setTextColor(Color.WHITE);
        } else if (position != lastCheckedPosition && getSelectedPlan() == -1) {
            holder.item_view.setCardBackgroundColor(Color.TRANSPARENT);
            holder.plan_name.setBackground(context.getResources().getDrawable(R.drawable.rounded_btn1));
            holder.duration.setTextColor(Color.BLACK);
            holder.period.setTextColor(Color.BLACK);
        }

        if (position == (selectedPlan - 1)) {
            Log.d("PremiumAdapter", "got selected");
            holder.item_view.setCardBackgroundColor(context.getResources().getColor(R.color.colorAccent));
            holder.plan_name.setBackground(context.getResources().getDrawable(R.drawable.rounded_btn2));
            holder.duration.setTextColor(Color.WHITE);
            holder.period.setTextColor(Color.WHITE);
        }

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView period, duration, plan_name;
        private CardView item_view;

        public MyViewHolder(View view) {
            super(view);
            period = (TextView) view.findViewById(R.id.period);
            duration = (TextView) view.findViewById(R.id.duration);
            plan_name = (TextView) view.findViewById(R.id.plan_name);
            item_view = (CardView) view.findViewById(R.id.item_view);
            item_view.setOnClickListener(this);

        }

        @Override
        public void onClick(View view) {
            int position = getAdapterPosition();
            Premium obj = list.get(position);
            if (view.getId() == R.id.item_view) {
                lastCheckedPosition = position;
                setSelectedPlan(-1);
                notifyDataSetChanged();
            }

        }
    }
}
