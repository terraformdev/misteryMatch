package com.misterymatch.app.activity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
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

import com.misterymatch.app.R;
import com.misterymatch.app.model.Card;
import com.misterymatch.app.model.CardList;
import com.misterymatch.app.model.Message;
import com.misterymatch.app.utils.GlobalData;
import com.misterymatch.app.utils.SharedHelper;
import com.misterymatch.app.webservice.APIError;
import com.misterymatch.app.webservice.ErrorUtils;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.misterymatch.app.utils.GlobalData.api;

public class WalletCardActivity extends BaseActivity {

    @BindView(R.id.back)
    ImageView back;
    @BindView(R.id.title)
    TextView title;
    @BindView(R.id.cards_rv)
    RecyclerView cardsRv;
    @BindView(R.id.add_card)
    Button addCard;
    @BindView(R.id.error_layout)
    LinearLayout mErrorLayout;

    List<Card> cards;
    CardsAdapter cardsAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        title.setText(getString(R.string.select_card));

        cards = new ArrayList<>();
        cardsAdapter = new CardsAdapter(this, cards);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        cardsRv.setLayoutManager(mLayoutManager);
        cardsRv.setItemAnimator(new DefaultItemAnimator());
        cardsRv.setAdapter(cardsAdapter);
    }

    @Override
    public void onResume() {
        super.onResume();
        getCards();
    }


    private void getCards() {
        String accessToken = SharedHelper.getKey(this, "access_token");
        Call<CardList> call = GlobalData.api.getCards(accessToken);
        call.enqueue(new Callback<CardList>() {
            @Override
            public void onResponse(@NonNull Call<CardList> call, @NonNull Response<CardList> response) {
                if (response.isSuccessful()) {
                    if (response.body().getCards().size() > 0) {
                        cards.clear();
                        cards.addAll(response.body().getCards());
                        cardsAdapter.notifyDataSetChanged();
                        mErrorLayout.setVisibility(View.GONE);
                    } else {
                        mErrorLayout.setVisibility(View.VISIBLE);
                    }
                } else {
                    APIError error = ErrorUtils.parseError(response);
                    showMessage(error.getError());
                }
            }

            @Override
            public void onFailure(@NonNull Call<CardList> call, @NonNull Throwable t) {
            }
        });

    }

    private void DeleteCard(String cardId) {

        final HashMap<String, String> map = new HashMap<>();
        map.put("card_id", cardId);
        map.put("_method", "DELETE");

        System.out.println(map);
        String accessToken = SharedHelper.getKey(this, "access_token");
        Call<Message> call = api.deleteCard(accessToken, map);
        call.enqueue(new Callback<Message>() {
            @Override
            public void onResponse(@NonNull Call<Message> call, @NonNull Response<Message> response) {

                if (response.isSuccessful()) {
                    Message message = response.body();
                    showMessage(getString(R.string.card_deleted_successfully));
                } else {
                    try {
                        JSONObject jObjError = new JSONObject(response.errorBody().string());
                        if (jObjError.has("card_id"))
                            showMessage(jObjError.optString("card_id"));
                        else
                            Toast.makeText(getApplicationContext(), "ERROR", Toast.LENGTH_LONG).show();
                    } catch (Exception e) {
                        Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<Message> call, @NonNull Throwable t) {
                Toast.makeText(getApplicationContext(), "DeleteCard Something Went Wrong" + t.toString(), Toast.LENGTH_LONG).show();
            }
        });

    }

    @OnClick({R.id.back, R.id.add_card})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.back:
                onBackPressed();
                break;
            case R.id.add_card:
                startActivity(new Intent(this, AddCardActivity.class));
                break;
        }
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_wallet_card;
    }


    private class CardsAdapter extends RecyclerView.Adapter<CardsAdapter.MyViewHolder> {

        private List<Card> list;
        private Context context;

        public CardsAdapter(Context context, List<Card> list) {
            this.context = context;
            this.list = list;
        }

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.list_item_wallet_card, parent, false);

            return new MyViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(MyViewHolder holder, int position) {
            Card obj = list.get(position);
            holder.cardNumber.setText(String.format("XXXX-XXXX-XXXX-%s", obj.getLastFour()));
        }

        @Override
        public int getItemCount() {
            return list.size();
        }

        public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
            private TextView cardNumber;
            private ImageView deleteCard;

            public MyViewHolder(View view) {
                super(view);
                cardNumber = (TextView) view.findViewById(R.id.card_number);
                deleteCard = (ImageView) view.findViewById(R.id.delete_card);
                cardNumber.setOnClickListener(this);
                deleteCard.setOnClickListener(this);

            }

            @Override
            public void onClick(View view) {
                final int position = getAdapterPosition();
                final Card card = list.get(position);
                if (view.getId() == R.id.card_number) {
                    Intent intent = new Intent();
                    intent.putExtra("card_id", card.getCardId());
                    setResult(PICK_CARD_REQUEST, intent);
                    finish();
                } else if (view.getId() == R.id.delete_card) {

                    new AlertDialog.Builder(WalletCardActivity.this)
                            .setMessage(getString(R.string.are_you_sure_you_want_to_delete))
                            .setPositiveButton(getString(R.string.delete), new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int whichButton) {
                                    cards.remove(position);
                                    cardsAdapter.notifyItemRemoved(position);
                                    DeleteCard(card.getCardId());
                                }
                            })
                            .setNegativeButton(getString(R.string.close), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            }).show();
                }

            }
        }
    }
}
