package com.open_source.ServiceProvider;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;

import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.open_source.R;
import com.open_source.activity.InboxListActivity;
import com.open_source.retrofitPack.Constants;

public class SPDashboard extends AppCompatActivity implements View.OnClickListener {
    Context context;
    Toolbar toolbar;
    TextView toolbar_title;
    CardView card_request,card_job,card_chat_box;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sp_dashboard);
        context = SPDashboard.this;
        init_view();
    }

    private void init_view() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar_title = (TextView) findViewById(R.id.toolbar_title);
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.back_arrow);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        toolbar_title.setText(R.string.dashborad);
        card_request = findViewById(R.id.card_request);
        card_job=(CardView)findViewById(R.id.card_job);
        card_chat_box=(CardView) findViewById(R.id.card_chat_box);
        card_request.setOnClickListener(this);
        card_job.setOnClickListener(this);
        card_chat_box.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.card_request:
                ((LinearLayout)findViewById(R.id.lay_request)).setVisibility(View.GONE);
                ((LinearLayout)findViewById(R.id.lay_request_selcted)).setVisibility(View.VISIBLE);
                ((LinearLayout)findViewById(R.id.lay_payment)).setVisibility(View.VISIBLE);
                ((LinearLayout)findViewById(R.id.lay_payment_Select)).setVisibility(View.GONE);
                ((LinearLayout)findViewById(R.id.lay_job)).setVisibility(View.VISIBLE);
                ((LinearLayout)findViewById(R.id.lay_job_selcted)).setVisibility(View.GONE);
                startActivity(new Intent(context, SPRequest.class).putExtra(Constants.TYPE,"sp_request"));
                break;

            case R.id.card_job:
                ((LinearLayout)findViewById(R.id.lay_request)).setVisibility(View.VISIBLE);
                ((LinearLayout)findViewById(R.id.lay_request_selcted)).setVisibility(View.GONE);
                ((LinearLayout)findViewById(R.id.lay_payment)).setVisibility(View.VISIBLE);
                ((LinearLayout)findViewById(R.id.lay_payment_Select)).setVisibility(View.GONE);
                ((LinearLayout)findViewById(R.id.lay_job)).setVisibility(View.GONE);
                ((LinearLayout)findViewById(R.id.lay_job_selcted)).setVisibility(View.VISIBLE);
                startActivity(new Intent(context, SPJoBPostDashboard.class));
                break;

            case R.id.card_chat_box:
                startActivity(new Intent(context,InboxListActivity.class));
                break;
        }
    }

    //-------------------------- Back Pressed -----------------------------
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
}
