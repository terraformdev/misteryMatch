package com.misterymatch.app.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.misterymatch.app.MyApplication;
import com.misterymatch.app.R;
import com.misterymatch.app.adapter.WalletHistoryAdapter;
import com.misterymatch.app.model.TransactionHistoryModel;
import com.misterymatch.app.model.WalletHistory;
import com.misterymatch.app.utils.GlobalData;
import com.misterymatch.app.utils.SharedHelper;
import com.misterymatch.app.webservice.APIError;
import com.misterymatch.app.webservice.ErrorUtils;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by santhosh@appoets.com on 22-01-2018.
 */

public class WalletTransactionReceivedFragment extends Fragment {

    @BindView(R.id.wallet_history_rv)
    RecyclerView walletHistoryRv;
    @BindView(R.id.progress_bar)
    ProgressBar progressBar;

    Unbinder unbinder;

    List<TransactionHistoryModel> list;
    WalletHistoryAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_transaction_history, container, false);
        unbinder = ButterKnife.bind(this, view);

        list = new ArrayList<>();
        adapter = new WalletHistoryAdapter(getContext(), list);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        walletHistoryRv.setLayoutManager(mLayoutManager);
        walletHistoryRv.setItemAnimator(new DefaultItemAnimator());
        walletHistoryRv.setAdapter(adapter);

        getTransactionHistory("RECEIVED");

        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    private void getTransactionHistory(String status) {
        progressBar.setVisibility(View.VISIBLE);
        String accessToken = SharedHelper.getKey(getActivity(), "access_token");
        Call<WalletHistory> call = GlobalData.api.TransactionHistory(accessToken, status);
        call.enqueue(new Callback<WalletHistory>() {
            @Override
            public void onResponse(@NonNull Call<WalletHistory> call, @NonNull Response<WalletHistory> response) {
                progressBar.setVisibility(View.GONE);
                if (response.isSuccessful()) {
                    list.clear();
                    List<TransactionHistoryModel> transactionHistoryModels = MyApplication.groupListByDate(response.body().getTransactionHistory());
                    list.addAll(transactionHistoryModels);
                    adapter.notifyDataSetChanged();
                } else {
                    APIError error = ErrorUtils.parseError(response);
                    Toast.makeText(getActivity(), error.getError(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<WalletHistory> call, @NonNull Throwable t) {
                Toast.makeText(getActivity(), "getTransactionHistory Something Went Wrong" + t.toString(), Toast.LENGTH_LONG).show();
            }
        });

    }

}
