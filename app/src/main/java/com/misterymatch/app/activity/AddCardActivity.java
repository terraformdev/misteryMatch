package com.misterymatch.app.activity;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.braintreepayments.cardform.view.CardForm;
import com.misterymatch.app.BuildConfig;
import com.misterymatch.app.R;
import com.misterymatch.app.model.Message;
import com.misterymatch.app.utils.CustomDialog;
import com.misterymatch.app.utils.GlobalData;
import com.misterymatch.app.utils.SharedHelper;
import com.stripe.android.Stripe;
import com.stripe.android.TokenCallback;
import com.stripe.android.exception.AuthenticationException;
import com.stripe.android.model.Card;
import com.stripe.android.model.Token;

import org.json.JSONObject;

import butterknife.BindView;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddCardActivity extends BaseActivity {

    @BindView(R.id.back)
    ImageView back;
    @BindView(R.id.title)
    TextView title;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.card_form)
    CardForm cardForm;
    @BindView(R.id.save)
    Button save;

    CustomDialog customDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        customDialog = new CustomDialog(this);
        cardForm.cardRequired(true)
                .expirationRequired(true)
                .cvvRequired(true)
                .postalCodeRequired(false)
                .mobileNumberRequired(false)
                .actionLabel(getString(R.string.add_card_details))
                .setup(this);



    }

    @OnClick({R.id.back, R.id.title, R.id.save})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.back:
                onBackPressed();
                break;
            case R.id.title:
                break;
            case R.id.save:

                customDialog.setCancelable(false);
                if (customDialog != null)
                    customDialog.show();
                if (cardForm.getCardNumber() == null || cardForm.getExpirationMonth() == null || cardForm.getExpirationYear() == null || cardForm.getCvv() == null) {
                    if ((customDialog != null) && (customDialog.isShowing()))
                        customDialog.dismiss();
                    showSnackBar(getString(R.string.enter_card_details));
                } else {
                    if (cardForm.getCardNumber().equals("") || cardForm.getExpirationMonth().equals("") || cardForm.getExpirationYear().equals("") || cardForm.getCvv().equals("")) {
                        if ((customDialog != null) && (customDialog.isShowing()))
                            customDialog.dismiss();
                        showSnackBar(getString(R.string.enter_card_details));
                    } else {
                        String cardNumber = cardForm.getCardNumber();
                        int month = Integer.parseInt(cardForm.getExpirationMonth());
                        int year = Integer.parseInt(cardForm.getExpirationYear());
                        String cvv = cardForm.getCvv();
                        Log.d("CARD", "CardDetails Number: " + cardNumber + "Month: " + month + " Year: " + year);

                        Card card = new Card(cardNumber, month, year, cvv);
                        try {
                            Stripe stripe = new Stripe(BuildConfig.STRIPE_PK);
                            stripe.createToken(
                                    card,
                                    new TokenCallback() {
                                        public void onSuccess(Token token) {
                                            // Send token to your server
                                            Log.d("CARD:", " " + token.getId());
                                            Log.d("CARD:", " " + token.getCard().getLast4());
                                            String cardToken = token.getId();
                                            addCardToAccount(cardToken);
                                        }

                                        public void onError(Exception error) {
                                            showMessage(error.getMessage());
                                            if ((customDialog != null) && (customDialog.isShowing()))
                                                customDialog.dismiss();
                                        }
                                    }
                            );
                        } catch (AuthenticationException e) {
                            e.printStackTrace();
                            if ((customDialog != null) && (customDialog.isShowing()))
                                customDialog.dismiss();
                        }
                    }

                }

                break;
        }
    }

    public void addCardToAccount(final String cardToken) {
        Log.e("stripe_token", cardToken);
        String accessToken = SharedHelper.getKey(this, "access_token");
        Call<Message> call = GlobalData.api.addCard(accessToken, cardToken);
        call.enqueue(new Callback<Message>() {
            @Override
            public void onResponse(@NonNull Call<Message> call, @NonNull Response<Message> response) {
                customDialog.dismiss();
                if (response.isSuccessful()) {
                    Toast.makeText(getApplicationContext(), "" + response.body().getMessage(), Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    try {
                        JSONObject jObjError = new JSONObject(response.errorBody().string());
                        Toast.makeText(getApplicationContext(), jObjError.optString("error"), Toast.LENGTH_LONG).show();
                    } catch (Exception e) {
                        Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<Message> call, @NonNull Throwable t) {
                Toast.makeText(AddCardActivity.this, "Something went wrong", Toast.LENGTH_SHORT).show();
                customDialog.dismiss();
                Toast.makeText(getApplicationContext(), "Something went wrong", Toast.LENGTH_SHORT).show();
            }
        });

    }


    @Override
    protected int getLayoutId() {
        return R.layout.activity_add_card;
    }
}
