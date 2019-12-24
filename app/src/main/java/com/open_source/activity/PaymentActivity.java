package com.open_source.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.appcompat.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.open_source.R;
import com.open_source.progressHud.ProgressHUD;
import com.open_source.retrofitPack.Constants;
import com.open_source.util.Utility;
import com.stripe.android.Stripe;
import com.stripe.android.TokenCallback;
import com.stripe.android.model.Card;
import com.stripe.android.model.Token;
import com.stripe.android.view.CardMultilineWidget;

/**
 * Created by and-02 on 2/7/18.
 */

public class PaymentActivity extends BaseActivity implements View.OnClickListener {
    public static final String TAG = PaymentActivity.class.getSimpleName();
    CardMultilineWidget cardMultilineWidget;
    Button btn_pay;
    Context context;
    Card cardToSave;
    ProgressHUD progressHUD;
    Toolbar toolbar;
    String cardExpiry;
    TextView toolbar_title, amount;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.payment_activity);
        context = PaymentActivity.this;
        init();
    }

    private void init() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar_title = (TextView) findViewById(R.id.toolbar_title);
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.back_arrow);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        toolbar_title.setText(R.string.payment);
        btn_pay = (Button) findViewById(R.id.paynow);
        btn_pay.setOnClickListener(this);
        amount = findViewById(R.id.amount);
        //amount.setText("$" + getIntent().getExtras().getString(Constants.PROPERTY_AMOUNT));
        cardMultilineWidget = (CardMultilineWidget) findViewById(R.id.card_multiline_widget);

    }

    @SuppressLint("Range")
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.paynow:
                cardToSave = cardMultilineWidget.getCard();
                if (cardToSave == null) {
                    Utility.ShowToastMessage(context, getString(R.string.invalidata));
                } else {
                    if (cardToSave.getNumber().equals("")) {
                        Utility.ShowToastMessage(context, getString(R.string.card_number));
                    } else if (cardToSave.getExpMonth() == 0) {
                        Utility.ShowToastMessage(context, getString(R.string.cardexpirymonth));
                    } else if (cardToSave.getExpYear() == 0) {
                        Utility.ShowToastMessage(context, getString(R.string.cardyear));
                    } else if (cardToSave.getCVC().isEmpty()) {
                        Utility.ShowToastMessage(context, getString(R.string.cradcvv));
                    } else {
                        cardExpiry = cardToSave.getExpMonth() + "/" + cardToSave.getExpYear();
                       // Fun_Server_Payment();
                      GetStripToken(cardMultilineWidget.getCard().getNumber().toString(), cardMultilineWidget.getCard().getExpMonth(), cardMultilineWidget.getCard().getExpYear(), cardMultilineWidget.getCard().getCVC().toString());
                    }
                }
                break;
        }

    }

    public void GetStripToken(String cardNumber, int cardExpMonth, int cardExpYear, String cardCVC) {
        Card card = new Card(cardNumber, cardExpMonth, cardExpYear, cardCVC);
        card.validateNumber();
        card.validateCVC();
        Stripe stripe = new Stripe(context,Constants.STRIPEAPIKEY);
        stripe.createToken(card,
                new TokenCallback() {
                    public void onSuccess(Token token) {
                        Log.e(TAG, "========" + token.toString());
                        Intent returnIntent = new Intent();
                        returnIntent.putExtra(Constants.PUBLIC_TOKEN, token.getId());
                        setResult(Activity.RESULT_OK,returnIntent);
                        finish();
                    }

                    public void onError(Exception error) {
                        Utility.ShowToastMessage(context,error.getMessage());
                        Intent returnIntent = new Intent();
                        returnIntent.putExtra(Constants.MESSAGE, error.getMessage());
                        setResult(Activity.RESULT_CANCELED, returnIntent);
                        finish();
                    }
                }
        );
    }

    @Override
    public void onBackPressed() {
        finish();
        super.onBackPressed();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    //--------------------------- Status Bar ------------------------------
    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                getWindow().setStatusBarColor(ContextCompat.getColor(context, R.color.colorPrimary));
            }
        }
    }
}
