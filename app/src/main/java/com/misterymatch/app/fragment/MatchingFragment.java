package com.misterymatch.app.fragment;


import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.gson.internal.LinkedTreeMap;
import com.misterymatch.app.HomeActivity;
import com.misterymatch.app.R;
import com.misterymatch.app.utils.CustomDialog;
import com.misterymatch.app.utils.GlobalData;
import com.misterymatch.app.utils.SharedHelper;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import pl.bclogic.pulsator4droid.library.PulsatorLayout;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 */
public class MatchingFragment extends DialogFragment {

    @BindView(R.id.pl_pulse)
    PulsatorLayout pulseLayout;
    private CustomDialog customDialog;

    public MatchingFragment() {
        // Required empty public constructor
    }

    public static MatchingFragment newInstance(Integer requestId) {
        MatchingFragment fragment = new MatchingFragment();
        Bundle bundle = new Bundle();
        bundle.putInt("request_id", requestId);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, R.style.FullScreenDialogStyle);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_matching, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        pulseLayout.start();
    }

    @Override
    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();
        if (dialog != null && dialog.getWindow() != null) {
            int width = ViewGroup.LayoutParams.MATCH_PARENT;
            int height = ViewGroup.LayoutParams.MATCH_PARENT;
            dialog.getWindow().setLayout(width, height);
            dialog.setCancelable(false);
            dialog.setCanceledOnTouchOutside(false);
        }
    }

    @OnClick({R.id.tv_end_call})
    public void onClick(View view) {
        if (view.getId() == R.id.tv_end_call) {
            cancelMatchRequest();
        }
    }

    private void cancelMatchRequest() {
        customDialog = new CustomDialog(requireContext());
        customDialog.show();
        String accessToken = SharedHelper.getKey(requireContext(), "access_token");
        assert getArguments() != null;
        Call<Object> call = GlobalData.api.cancelMatchRequest(accessToken,
                getArguments().getInt("request_id"));
        call.enqueue(new Callback<Object>() {
            @Override
            public void onResponse(@NonNull Call<Object> call, @NonNull Response<Object> response) {
                customDialog.dismiss();
                if (response.code() == 200 && response.body() != null
                        && response.body() instanceof LinkedTreeMap) {
                    if (getActivity() != null)
                        ((HomeActivity) getActivity()).setMatchStatus(null);
                    Toast.makeText(
                            requireActivity().getApplicationContext(),
                            String.valueOf(((LinkedTreeMap) response.body()).get("message")),
                            Toast.LENGTH_SHORT
                    ).show();
                    dismiss();
                }
            }

            @Override
            public void onFailure(@NonNull Call<Object> call, @NonNull Throwable t) {
                customDialog.dismiss();
                Toast.makeText(requireContext(), t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
