package com.misterymatch.app.FragmentSheet;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.misterymatch.app.R;

import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * A simple {@link Fragment} subclass.
 */
public class LikeDislikeFragment extends BottomSheetDialogFragment {

    private OnDismissListener listener;

    public LikeDislikeFragment() {
        // Required empty public constructor
    }

    public static LikeDislikeFragment newInstance(Integer requestId, Integer participantId) {
        LikeDislikeFragment fragment = new LikeDislikeFragment();
        Bundle bundle = new Bundle();
        bundle.putInt("request_id", requestId);
        bundle.putInt("participant_id", participantId);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_like_dislike, container, false);
        listener = (OnDismissListener) getActivity();
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();
        if (dialog != null) {
            dialog.setCancelable(false);
            dialog.setCanceledOnTouchOutside(false);
        }
    }

    @OnClick({R.id.tv_dislike, R.id.tv_like})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_dislike:
                updateMatchInterest(3);
                break;

            case R.id.tv_like:
                updateMatchInterest(2);
                break;

            default:
                break;
        }
    }

    private void updateMatchInterest(int interest) {
        if (getArguments() != null)
            listener.onDismiss(getArguments().getInt("request_id"),
                    getArguments().getInt("participant_id"), interest);
    }

    public interface OnDismissListener {
        void onDismiss(int requestId, int participantId, int interest);
    }
}
