package com.misterymatch.app.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.misterymatch.app.HomeActivity;
import com.misterymatch.app.R;
import com.misterymatch.app.library.Log;
import com.misterymatch.app.model.FindMatchModel;
import com.misterymatch.app.utils.CustomDialog;
import com.misterymatch.app.utils.GlobalData;
import com.misterymatch.app.utils.SharedHelper;

import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 */
public class FindMatchFragment extends Fragment {

    private static final String TAG = FindMatchFragment.class.getSimpleName();
    private CustomDialog customDialog;

    public FindMatchFragment() {
        // Required empty public constructor
    }

    public static FindMatchFragment newInstance() {
        return new FindMatchFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_find_match, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @OnClick(R.id.iv_find_match)
    public void onClick(View view) {
        findMatches();
    }

    private void findMatches() {
        customDialog = new CustomDialog(requireContext());
        customDialog.show();
        String accessToken = SharedHelper.getKey(requireContext(), "access_token");
        Call<FindMatchModel> call = GlobalData.api.findMatches(accessToken);
        call.enqueue(new Callback<FindMatchModel>() {
            @Override
            public void onResponse(@NonNull Call<FindMatchModel> call,
                                   @NonNull Response<FindMatchModel> response) {
                customDialog.dismiss();
                if (response.code() == 200 && response.body() != null) {
                    if (getActivity() != null)
                        ((HomeActivity) getActivity()).setMatchStatus(HomeActivity.SEARCHING);
                    MatchingFragment matchingFragment = MatchingFragment
                            .newInstance(response.body().getRequestId());
                    matchingFragment.show(requireActivity().getSupportFragmentManager(),
                            matchingFragment.getClass().getSimpleName());
                }
            }

            @Override
            public void onFailure(@NonNull Call<FindMatchModel> call, @NonNull Throwable t) {
                customDialog.dismiss();
                Log.d(TAG, t.getMessage());
            }
        });
    }
}
