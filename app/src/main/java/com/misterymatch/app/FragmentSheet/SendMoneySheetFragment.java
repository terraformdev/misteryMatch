package com.misterymatch.app.FragmentSheet;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.widget.AppCompatButton;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.misterymatch.app.R;
import com.misterymatch.app.activity.ShareMoneyActivity;
import com.misterymatch.app.model.User;
import com.misterymatch.app.model.Wallet;
import com.misterymatch.app.utils.GlobalData;
import com.misterymatch.app.utils.SharedHelper;

import org.json.JSONObject;

import java.util.HashMap;

import butterknife.BindView;
import butterknife.OnClick;
import butterknife.Unbinder;
import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.misterymatch.app.utils.GlobalData.api;

/**
 * Created by douglas on 9/6/2017.
 * Updated by santhosh@appoets.com 22/01/2018.
 */

public class SendMoneySheetFragment extends BaseBottomSheetDialogFragment {


    @BindView(R.id.sender)
    CircleImageView sender;
    @BindView(R.id.receiver)
    CircleImageView receiver;
    @BindView(R.id.status)
    TextView status;
    @BindView(R.id.amount)
    EditText amount;
    @BindView(R.id.amount50)
    TextView amount50;
    @BindView(R.id.amount100)
    TextView amount100;
    @BindView(R.id.amount500)
    TextView amount500;
    @BindView(R.id.send)
    AppCompatButton send;
    Unbinder unbinder;

    String receiverId = "";

    public static SendMoneySheetFragment newInstance(Bundle bundle) {
        final SendMoneySheetFragment fragment = new SendMoneySheetFragment();
        fragment.setArguments(bundle);

        return fragment;
    }

    @Override
    public void setupDialog(Dialog dialog, int style) {
        super.setupDialog(dialog, style);
        amount.setSelection(amount.getText().toString().length());
        Bundle bundle = this.getArguments();
        if (bundle != null) {
            receiverId = bundle.getString("receiver_id", "");
            String firstName = bundle.getString("first_name", "");
            String picture = bundle.getString("picture", "");
            status.setText(getString(R.string.send_amount_to, firstName));
            Glide.with(getContext()).load(picture).apply(new RequestOptions().placeholder(R.drawable.user).error(R.drawable.user).dontAnimate()).into(receiver);
            if(GlobalData.PROFILE != null){
                User user = GlobalData.PROFILE.getUser();
                Glide.with(getContext()).load(user.getPicture()).apply(new RequestOptions().placeholder(R.drawable.user).error(R.drawable.user).dontAnimate()).into(sender);
            }

        }


        CoordinatorLayout.LayoutParams layoutParams = (CoordinatorLayout.LayoutParams) ((View) contentView.getParent()).getLayoutParams();
        CoordinatorLayout.Behavior behavior = layoutParams.getBehavior();
        //((View) contentView.getParent()).setBackgroundColor(ContextCompat.getColor(getContext(), android.R.color.transparent));
        if (behavior != null && behavior instanceof BottomSheetBehavior) {
            ((BottomSheetBehavior) behavior).setState(BottomSheetBehavior.STATE_SETTLING);
        }
    }

    @Override
    protected int getLayoutId() {
        return R.layout.sheet_send_money;
    }

    @OnClick({R.id.amount50, R.id.amount100, R.id.amount500, R.id.send})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.amount50:
                amount.setText("50");
                amount.setSelection(amount.getText().toString().length());
                break;
            case R.id.amount100:
                amount.setText("100");
                amount.setSelection(amount.getText().toString().length());
                break;
            case R.id.amount500:
                amount.setText("500");
                amount.setSelection(amount.getText().toString().length());
                break;
            case R.id.send:
                PaymentProcess(receiverId);
                break;
        }
    }

    private void PaymentProcess(String receiverId) {

        if (amount.getText().toString().isEmpty()) {
            Toast.makeText(getActivity(), getString(R.string.invalid_amount), Toast.LENGTH_SHORT).show();
            return;
        }

        final HashMap<String, String> map = new HashMap<>();
        map.put("status", "PAID");
        map.put("amount", amount.getText().toString());
        map.put("receiver_id", receiverId);

        System.out.println(map);
        String accessToken = SharedHelper.getKey(getActivity(), "access_token");
        Call<Wallet> call = api.paymentProcess(accessToken, map);
        call.enqueue(new Callback<Wallet>() {
            @Override
            public void onResponse(@NonNull Call<Wallet> call, @NonNull Response<Wallet> response) {

                if (response.isSuccessful()) {
                    Wallet wallet = response.body();
                    Toast.makeText(getActivity(), wallet.getMessage(), Toast.LENGTH_SHORT).show();
                    GlobalData.PROFILE.getUser().setWalletBalance(wallet.getUser().getWalletBalance());
                    ((ShareMoneyActivity)getActivity()).onResume();
                    dismiss();
                } else {
                    try {
                        JSONObject jObjError = new JSONObject(response.errorBody().string());
                        if (jObjError.has("amount"))
                            Toast.makeText(getActivity(), jObjError.optString("amount"), Toast.LENGTH_LONG).show();
                        else if (jObjError.has("status"))
                            Toast.makeText(getActivity(), jObjError.optString("status"), Toast.LENGTH_LONG).show();
                        else if (jObjError.has("receiver_id"))
                            Toast.makeText(getActivity(), jObjError.optString("receiver_id"), Toast.LENGTH_LONG).show();
                        else
                            Toast.makeText(getActivity(), "ERROR", Toast.LENGTH_LONG).show();
                    } catch (Exception e) {
                        Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<Wallet> call, @NonNull Throwable t) {
                Toast.makeText(getActivity(), "PaymentProcess Something Went Wrong" + t.toString(), Toast.LENGTH_LONG).show();
            }
        });

    }
}
