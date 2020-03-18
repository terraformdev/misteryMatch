package com.misterymatch.app.activity;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.misterymatch.app.FragmentSheet.SendMoneySheetFragment;
import com.misterymatch.app.MyApplication;
import com.misterymatch.app.R;
import com.misterymatch.app.model.MatchList;
import com.misterymatch.app.model.User;
import com.misterymatch.app.utils.GlobalData;
import com.misterymatch.app.utils.SharedHelper;
import com.misterymatch.app.webservice.APIError;
import com.misterymatch.app.webservice.ErrorUtils;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.misterymatch.app.utils.GlobalData.api;

/**
 * Created by santhosh@appoets.com on 18-01-2018.
 */

public class ShareMoneyActivity extends BaseActivity {

    @BindView(R.id.rv_member_list)
    RecyclerView mRvMemberList;

    List<MatchList> matchLists;

    SendMoneySheetFragment sendMoneySheetFragment = new SendMoneySheetFragment();
    @BindView(R.id.balance)
    TextView balance;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        matchLists = new ArrayList<>();
        MembersAdapter membersAdapter = new MembersAdapter(this, matchLists);
        mRvMemberList.setLayoutManager(new GridLayoutManager(this, 3));
        mRvMemberList.setItemAnimator(new DefaultItemAnimator());
        mRvMemberList.setAdapter(membersAdapter);

    }

    @Override
    public void onResume() {
        super.onResume();
        getMatchList();
        if (GlobalData.PROFILE != null) {
            User user = GlobalData.PROFILE.getUser();
            balance.setText(MyApplication.numberFormat.format(user.getWalletBalance()));
        }
    }

    private void getMatchList() {

        String accessToken = SharedHelper.getKey(this, "access_token");
        Call<List<MatchList>> call = api.getMatchList(accessToken);
        call.enqueue(new Callback<List<MatchList>>() {
            @Override
            public void onResponse(@NonNull Call<List<MatchList>> call, @NonNull Response<List<MatchList>> response) {
                if (response.isSuccessful()) {
                    matchLists.clear();
                    matchLists.addAll(response.body());
                    mRvMemberList.getAdapter().notifyDataSetChanged();
                } else {
                    APIError error = ErrorUtils.parseError(response);
                    showMessage(error.getError());
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<MatchList>> call, @NonNull Throwable t) {
                showMessage("Something wrong getMatchList");
            }
        });
    }

    /*@OnClick(R.id.select_member)
    void selectMember(){
        SendMoneySheetFragment sendMoneySheetFragment = new SendMoneySheetFragment();
        new SendMoneySheetFragment().show(getSupportFragmentManager(),sendMoneySheetFragment.getTag());
    }*/


    @Override
    protected int getLayoutId() {
        return R.layout.activity_share_money;
    }

    @OnClick(R.id.back)
    public void onViewClicked() {
        onBackPressed();
    }

    private class MembersAdapter extends RecyclerView.Adapter<MembersAdapter.MyViewHolder> {

        private List<MatchList> list;
        private Context context;

        public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
            private TextView grid_text;
            private LinearLayout itemView;
            private CircleImageView grid_image;

            public MyViewHolder(View view) {
                super(view);
                itemView = (LinearLayout) view.findViewById(R.id.item_view);
                grid_image = (CircleImageView) view.findViewById(R.id.grid_image);
                grid_text = (TextView) view.findViewById(R.id.grid_text);
                itemView.setOnClickListener(this);

            }

            @Override
            public void onClick(View view) {
                int position = getAdapterPosition();
                MatchList matchList =  list.get(position);
                Bundle bundle = new Bundle();
                bundle.putString("receiver_id", String.valueOf(matchList.getUserLike().getId()));
                bundle.putString("first_name", matchList.getUserLike().getFirstName());
                bundle.putString("picture", matchList.getUserLike().getPicture());
                SendMoneySheetFragment fragment = new SendMoneySheetFragment();
                fragment.setArguments(bundle);
                fragment.show(getSupportFragmentManager(), sendMoneySheetFragment.getTag());
                //new SendMoneySheetFragment().show(getSupportFragmentManager(), sendMoneySheetFragment.getTag());
            }
        }


        public MembersAdapter(Context context, List<MatchList> list) {
            this.context = context;
            this.list = list;
        }

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.list_item_grid_user, parent, false);

            return new MyViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(MyViewHolder holder, int position) {
            MatchList obj = list.get(position);
            holder.grid_text.setText(obj.getUserLike().getFirstName());
            Glide.with(getApplicationContext()).load(obj.getUserLike().getPicture()).apply(new RequestOptions().placeholder(R.drawable.user).error(R.drawable.user).dontAnimate()).into(holder.grid_image);
        }

        @Override
        public int getItemCount() {
            return list.size();
        }
    }
}
