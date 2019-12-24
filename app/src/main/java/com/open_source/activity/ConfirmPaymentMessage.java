package com.open_source.activity;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.Nullable;

import android.view.View;
import android.widget.TextView;

import com.open_source.R;
import com.open_source.ServiceProvider.ServiceProviderHome;
import com.open_source.retrofitPack.Constants;

public class ConfirmPaymentMessage extends BaseActivity {
    private TextView btn_continue;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getIntent().getExtras().getString(Constants.MESSAGE, "").equalsIgnoreCase("success")) {
            setContentView(R.layout.confirm_payment_message);
        } else if (getIntent().getExtras().getString(Constants.MESSAGE, "").equalsIgnoreCase("failed")) {
            setContentView(R.layout.confirmation_payment_failed);
        }
        if (getIntent().hasExtra(Constants.PURPOSE))
            ((TextView) findViewById(R.id.txt_msg)).setText(R.string.sp_payment);

        btn_continue = findViewById(R.id.btn_continue);

        btn_continue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getIntent().getExtras().getString(Constants.TYPE) != null) {
                    if (getIntent().getExtras().getString(Constants.TYPE).equals("add_work"))
                        startActivity(new Intent(ConfirmPaymentMessage.this, ServiceProviderHome.class));
                    else if(getIntent().getExtras().getString(Constants.TYPE).equals("sell"))
                        startActivity(new Intent(ConfirmPaymentMessage.this, MainActivity.class));
                    else if (getIntent().getExtras().getString(Constants.TYPE).equals("user"))
                        startActivity(new Intent(ConfirmPaymentMessage.this, MainActivity.class));
                    else if (getIntent().getExtras().getString(Constants.TYPE).equals("sp"))
                        startActivity(new Intent(ConfirmPaymentMessage.this, LoginActivity.class));
                    else if (getIntent().getExtras().getString(Constants.TYPE).equals("milestone"))
                        startActivity(new Intent(ConfirmPaymentMessage.this, MainActivity.class));
                    finish();
                } else {
                    finish();
                }

            }
        });

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (getIntent().getExtras().getString(Constants.TYPE).equals("add_work"))
            startActivity(new Intent(ConfirmPaymentMessage.this, ServiceProviderHome.class));
        else if(getIntent().getExtras().getString(Constants.TYPE).equals("sell"))
            startActivity(new Intent(ConfirmPaymentMessage.this, MainActivity.class));
        else if (getIntent().getExtras().getString(Constants.TYPE).equals("user"))
            startActivity(new Intent(ConfirmPaymentMessage.this, MainActivity.class));
        else if (getIntent().getExtras().getString(Constants.TYPE).equals("sp"))
            startActivity(new Intent(ConfirmPaymentMessage.this, LoginActivity.class));
        else if (getIntent().getExtras().getString(Constants.TYPE).equals("milestone"))
            startActivity(new Intent(ConfirmPaymentMessage.this, MainActivity.class));
        finish();
    }
}
