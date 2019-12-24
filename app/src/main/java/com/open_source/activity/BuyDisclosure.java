package com.open_source.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;

import com.open_source.R;
import com.open_source.retrofitPack.Constants;
import com.open_source.util.Utility;

public class BuyDisclosure extends BaseActivity implements View.OnClickListener {
    private Toolbar toolbar;
    private TextView txt_title;
    private Context context;
    private CheckBox check_agree;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.buy_disclosure);
        context = this;
        init_view();
    }

    private void init_view() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.back_arrow);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        txt_title = findViewById(R.id.toolbar_title);
        txt_title.setText(R.string.disclaimer_form);
        check_agree = findViewById(R.id.checkbox);
        ((Button) findViewById(R.id.button_submit)).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button_submit:
                if (!check_agree.isChecked())
                    Utility.ShowToastMessage(context, getString(R.string.check_disclaimer));
                else
                    startActivity(new Intent(context, PaymentConfirmation.class).
                            putExtra(Constants.PROPERTY_ID, getIntent().getExtras().getString(Constants.PROPERTY_ID)).
                            putExtra(Constants.TYPE, "fixed"));
                    break;
        }

    }
}
