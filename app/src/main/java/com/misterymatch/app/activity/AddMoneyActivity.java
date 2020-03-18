package com.misterymatch.app.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.misterymatch.app.MyApplication;
import com.misterymatch.app.R;
import com.misterymatch.app.model.User;
import com.misterymatch.app.model.Wallet;
import com.misterymatch.app.utils.GlobalData;
import com.misterymatch.app.utils.SharedHelper;

import org.json.JSONObject;

import java.util.HashMap;

import butterknife.BindView;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.misterymatch.app.utils.GlobalData.api;

public class AddMoneyActivity extends BaseActivity {

    @BindView(R.id.back)
    ImageView back;
    @BindView(R.id.balance)
    TextView balance;
    @BindView(R.id.amount)
    EditText amount;
    @BindView(R.id.amount199)
    Button amount199;
    @BindView(R.id.amount599)
    Button amount599;
    @BindView(R.id.amount1099)
    Button amount1099;
    @BindView(R.id.add_money)
    Button addMoney;
    private final int TWO_CHECKOUT_CODE = 5444;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        amount199.setText(MyApplication.numberFormat.format(199));
        amount599.setText(MyApplication.numberFormat.format(599));
        amount1099.setText(MyApplication.numberFormat.format(1099));

    }

    @Override
    public void onResume() {
        super.onResume();
        if (GlobalData.PROFILE != null) {
            User user = GlobalData.PROFILE.getUser();
            balance.setText(MyApplication.numberFormat.format(user.getWalletBalance()));
        }
    }


    @Override
    protected int getLayoutId() {
        return R.layout.activity_add_money;
    }

    @OnClick({R.id.back, R.id.amount199, R.id.amount599, R.id.amount1099, R.id.add_money})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.back:
                onBackPressed();
                break;
            case R.id.amount199:
                amount.setText("199");
                break;
            case R.id.amount599:
                amount.setText("599");
                break;
            case R.id.amount1099:
                amount.setText("1099");
                break;
            case R.id.add_money:
                if (!amount.getText().toString().isEmpty()) {
//                    startActivityForResult(new Intent(this, WalletCardActivity.class), PICK_CARD_REQUEST);
                    Intent intent = new Intent(this, TwoCheckoutPaymentActivity.class);
                    intent.putExtra("Amount", amount.getText().toString() + "");
                    intent.putExtra("Id", SharedHelper.getKey(this, "id") + "");
                    startActivityForResult(intent, TWO_CHECKOUT_CODE);
                } else {
                    showMessage(getString(R.string.invalid_amount));
                }
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PICK_CARD_REQUEST) {
            if (data != null) {
                String cardId = data.getStringExtra("card_id");
                System.out.println(cardId);
                addMoney.setEnabled(false);
                addMoney.setAlpha(0.5f);
                AddAmount(cardId);
            }
        }else if(requestCode == TWO_CHECKOUT_CODE){
            Toast.makeText(this, "Test", Toast.LENGTH_SHORT).show();
        }
    }

    private void AddAmount(String cardId) {

        if (amount.getText().toString().isEmpty()) {
            showMessage(getString(R.string.invalid_amount));
            return;
        }

        final HashMap<String, String> map = new HashMap<>();
        map.put("amount", amount.getText().toString());
        map.put("card_id", cardId);
        map.put("status", "ADDED");

        System.out.println(map);
        String accessToken = SharedHelper.getKey(getApplicationContext(), "access_token");
        Call<Wallet> call = api.addMoney(accessToken, map);
        call.enqueue(new Callback<Wallet>() {
            @Override
            public void onResponse(@NonNull Call<Wallet> call, @NonNull Response<Wallet> response) {

                if (response.isSuccessful()) {
                    Wallet wallet = response.body();
                    Toast.makeText(AddMoneyActivity.this, wallet.getMessage(), Toast.LENGTH_SHORT).show();
                    if (GlobalData.PROFILE != null) {
                        GlobalData.PROFILE.getUser().setWalletBalance(wallet.getUser().getWalletBalance());
                    }
                    finish();
                } else {
                    try {
                        JSONObject jObjError = new JSONObject(response.errorBody().string());
                        if (jObjError.has("amount"))
                            Toast.makeText(getApplicationContext(), jObjError.optString("amount"), Toast.LENGTH_LONG).show();
                        else if (jObjError.has("card_id"))
                            Toast.makeText(getApplicationContext(), jObjError.optString("card_id"), Toast.LENGTH_LONG).show();
                        else if (jObjError.has("status"))
                            Toast.makeText(getApplicationContext(), jObjError.optString("status"), Toast.LENGTH_LONG).show();
                        else
                            Toast.makeText(getApplicationContext(), "ERROR", Toast.LENGTH_LONG).show();
                    } catch (Exception e) {
                        Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<Wallet> call, @NonNull Throwable t) {
                Toast.makeText(getApplicationContext(), "AddAmount Something Went Wrong" + t.toString(), Toast.LENGTH_LONG).show();
            }
        });

    }
}
