package com.misterymatch.app.fragment;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.CardView;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.misterymatch.app.R;
import com.misterymatch.app.activity.TwoCheckoutFlowPaymentActivity;
import com.misterymatch.app.activity.TwoCheckoutPaymentActivity;
import com.misterymatch.app.library.Log;
import com.misterymatch.app.model.ExpiryDate;
import com.misterymatch.app.model.Premium;
import com.misterymatch.app.model.PremiumRes;
import com.misterymatch.app.model.SendPremiumRes;
import com.misterymatch.app.model.UserPremiums;
import com.misterymatch.app.model.Walkthrough;
import com.misterymatch.app.utils.SharedHelper;
import com.misterymatch.app.webservice.APIError;
import com.misterymatch.app.webservice.ErrorUtils;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.facebook.FacebookSdk.getApplicationContext;
import static com.misterymatch.app.utils.GlobalData.api;

/**
 * A simple {@link Fragment} subclass.
 */
public class PremiumFragment extends Fragment implements ViewPager.OnPageChangeListener {

    private static final int TWO_CHECKOUT_CODE = 5534;
    @BindView(R.id.premium_rv)
    RecyclerView premiumRv;
    @BindView(R.id.get_pro)
    Button getPro;
    Unbinder unbinder;
    int selectedId;
    List<Premium> list;
    PremiumAdapter adapter;
    MyViewPagerAdapter adsAdapter;
    @BindView(R.id.view_pager)
    ViewPager viewPager;
    @BindView(R.id.layoutDots)
    LinearLayout layoutDots;
    @BindView(R.id.tv_description)
    AppCompatTextView mDescription;
    @BindView(R.id.expire_date)
    TextView expire_date;
    @BindView(R.id.pro_layout)
    LinearLayout proLayout;
    private ImageView[] dots;

    public PremiumFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_premium, container, false);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        list = new ArrayList<>();
        premiumRv.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        premiumRv.setItemAnimator(new DefaultItemAnimator());
        List<Walkthrough> list = new ArrayList<>();
        list.add(new Walkthrough(R.drawable.premium_ad_block, "Say goodbye to ads", getString(R.string.walk_1_description)));
        list.add(new Walkthrough(R.drawable.premium_super_likes, "You can do 5 superlike per day.", getString(R.string.walk_2_description)));
        list.add(new Walkthrough(R.drawable.premium_likes, "You can do N number of likes per day", getString(R.string.walk_3_description)));
        adsAdapter = new MyViewPagerAdapter(getContext(), list);
        viewPager.setAdapter(adsAdapter);
        viewPager.setCurrentItem(0);
        viewPager.addOnPageChangeListener(this);
        addBottomDots();
    }

    @Override
    public void onResume() {
        super.onResume();
        getPremiums();
    }

    private void getPremiums() {
        String accessToken = SharedHelper.getKey(getContext(), "access_token");
        Call<PremiumRes> call = api.getPremium(accessToken);
        call.enqueue(new Callback<PremiumRes>() {

            Integer selectedId = -1;

            @Override
            public void onResponse(@NonNull Call<PremiumRes> call, @NonNull Response<PremiumRes> response) {
                if (response.isSuccessful()) {
                    if (response.body().getUserPremiums() != null) {
                        UserPremiums userPremiums = response.body().getUserPremiums();
                        this.selectedId = userPremiums.getUserPremium().getId();
                        ExpiryDate expiryDate = response.body().getExpiryDate();
                        expire_date.setText(expiryDate.getDate());
                        proLayout.setVisibility(View.VISIBLE);
                    } else {
                        selectedId = 0;
                        list.clear();
                        list.addAll(response.body().getPremiums());
                        setAdapter(list, selectedId);
                        getPro.setEnabled(true);
                        proLayout.setVisibility(View.GONE);
                    }
                } else {
                    APIError error = ErrorUtils.parseError(response);
                    Toast.makeText(getContext(), error.getError(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<PremiumRes> call, @NonNull Throwable t) {
                Log.e("Premium Fragment", t.toString());
            }
        });
    }

    public void setmDescription(String value) {
        mDescription.setText(value);
    }

    private void setAdapter(List<Premium> list, int id) {
        adapter = new PremiumAdapter(getContext(), list, id);
        premiumRv.setAdapter(adapter);
    }

    private void addPremium(Integer premiumId) {
        final ProgressDialog dialog = new ProgressDialog(getContext());
        dialog.setMessage(getString(R.string.please_wait));
        dialog.setCancelable(false);
        dialog.show();
        String accessToken = SharedHelper.getKey(getContext(), "access_token");
        Call<SendPremiumRes> call = api.addPremium(accessToken, premiumId);
        call.enqueue(new Callback<SendPremiumRes>() {
            @Override
            public void onResponse(@NonNull Call<SendPremiumRes> call, @NonNull Response<SendPremiumRes> response) {
                dialog.dismiss();
                if (response.isSuccessful()) {
                    SendPremiumRes sendPremiumRes = response.body();
                    getPremiums();
                } else {
                    try {
                        JSONObject jObjError = new JSONObject(response.errorBody().string());
                        if (jObjError.has("premium_id"))
                            Toast.makeText(getContext(), jObjError.optString("premium_id"), Toast.LENGTH_LONG).show();
                        else if (jObjError.has("message")) {
                            if (jObjError.optString("message").equals("BOOST_WALLET")) {
                                Toast.makeText(getContext(), getString(R.string.insufficient_wallet_balance), Toast.LENGTH_LONG).show();
                            } else {
                                Toast.makeText(getContext(), jObjError.optString("message"), Toast.LENGTH_LONG).show();
                            }
                        }
                    } catch (Exception e) {
                        Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<SendPremiumRes> call, @NonNull Throwable t) {
                dialog.dismiss();
                Toast.makeText(getContext(), "Something Went Wrong", Toast.LENGTH_LONG).show();
            }
        });
    }

    @OnClick(R.id.get_pro)
    public void onViewClicked() {
//        if (adapter.getLastSelectedItem() != null) {
            Premium premium = adapter.getLastSelectedItem();
//            if (premium != null) {
//                addPremium();
//            }
//        }
        Intent intent = new Intent(getContext(), TwoCheckoutFlowPaymentActivity.class);
        intent.putExtra("AMOUNT", premium.getPrice());
        intent.putExtra("ID", premium.getId());
        startActivityForResult(intent, TWO_CHECKOUT_CODE);
    }

    private void addBottomDots() {
        int dotsCount = adsAdapter.getCount();
        dots = new ImageView[dotsCount];
        if (dotsCount == 0)
            return;
        layoutDots.removeAllViews();
        for (int i = 0; i < dotsCount; i++) {
            dots[i] = new ImageView(getContext());
            dots[i].setImageDrawable(getResources().getDrawable(R.drawable.non_selected_item_dot));
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );
            params.setMargins(4, 4, 4, 4);
            layoutDots.addView(dots[i], params);
        }
        dots[0].setImageDrawable(getResources().getDrawable(R.drawable.selected_item_dot));
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        for (ImageView dot : dots) {
            dot.setImageDrawable(getResources().getDrawable(R.drawable.non_selected_item_dot));
        }
        dots[position].setImageDrawable(getResources().getDrawable(R.drawable.selected_item_dot));
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    public class MyViewPagerAdapter extends PagerAdapter {

        List<Walkthrough> list;
        Context context;

        MyViewPagerAdapter(Context context, List<Walkthrough> list) {
            this.list = list;
            this.context = context;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            View itemView = LayoutInflater.from(getApplicationContext()).inflate(R.layout.ads_pager_item, container, false);
            Walkthrough walk = list.get(position);
            TextView title = itemView.findViewById(R.id.title);
            ImageView imageView = itemView.findViewById(R.id.img_pager_item);
            title.setText(walk.title);
            Glide.with(getApplicationContext()).load(walk.drawable).into(imageView);
            container.addView(itemView);
            return itemView;
        }

        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object obj) {
            return view == obj;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            View view = (View) object;
            container.removeView(view);
        }
    }

    public class PremiumAdapter extends RecyclerView.Adapter<PremiumAdapter.MyViewHolder> {

        private List<Premium> list;
        private int selectedPlan;
        private Context context;
        private int lastCheckedPosition = 0;

        public PremiumAdapter(Context context, List<Premium> list, int selectedPlan) {
            this.context = context;
            this.list = list;
            this.lastCheckedPosition = selectedPlan;
        }

        public int getSelectedPlan() {
            return selectedPlan;
        }

        public void setSelectedPlan(int selectedPlan) {
            this.selectedPlan = selectedPlan;
        }

        public Premium getLastSelectedItem() {
            if (list.size() > 0) {
                return list.get(lastCheckedPosition);
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
//            holder.duration.setText(String.valueOf(obj.getDuration()));
            holder.period.setText(String.valueOf(obj.getGem()));
            Log.d("PremiumAdapter", "Adapter Position : " + holder.getAdapterPosition());
            Log.d("PremiumAdapter", "Position : " + position);
            Log.d("PremiumAdapter", "Selected Plan : " + selectedPlan);
            if (position == lastCheckedPosition) {
                holder.item_view.setCardBackgroundColor(context.getResources().getColor(R.color.colorAccent));
                holder.plan_name.setBackground(context.getResources().getDrawable(R.drawable.rounded_btn2));
                holder.duration.setTextColor(Color.WHITE);
                holder.period.setTextColor(Color.WHITE);
//                String va = "Your " + obj.getGem() + " " + obj.getDuration() + " Rate is " + MyApplication.numberFormat.format(obj.getPrice());
//                setmDescription(va);
                mDescription.setText(obj.getDescription());
            } else {
                holder.item_view.setCardBackgroundColor(Color.TRANSPARENT);
                holder.plan_name.setBackground(context.getResources().getDrawable(R.drawable.rounded_btn1));
                holder.duration.setTextColor(Color.BLACK);
                holder.period.setTextColor(Color.BLACK);
            }
        }

        @Override
        public int getItemCount() {
            return list.size();
        }

        public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
            private TextView period, duration, plan_name;
            private CardView item_view;

            MyViewHolder(View view) {
                super(view);
                period = view.findViewById(R.id.period);
                duration = view.findViewById(R.id.duration);
                plan_name = view.findViewById(R.id.plan_name);
                item_view = view.findViewById(R.id.item_view);
                item_view.setOnClickListener(this);
            }

            @Override
            public void onClick(View view) {
                int position = getAdapterPosition();
                Premium obj = list.get(position);
                if (view.getId() == R.id.item_view) {
                    lastCheckedPosition = position;
                    notifyDataSetChanged();
                }
            }
        }
    }
}