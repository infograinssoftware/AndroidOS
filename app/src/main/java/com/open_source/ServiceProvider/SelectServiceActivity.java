package com.open_source.ServiceProvider;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import com.open_source.R;
import com.open_source.retrofitPack.Constants;

public class SelectServiceActivity extends AppCompatActivity implements View.OnClickListener {
    Toolbar toolbar;
    Context context;
    String service_id="";


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_service);
        context=SelectServiceActivity.this;
        init();
    }

    private void init() {
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ((TextView) findViewById(R.id.toolbar_title)).setText(R.string.post_project);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.back_arrow);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        ((TextView) findViewById(R.id.btn_post_job)).setOnClickListener(this);
        ((TextView) findViewById(R.id.btn_service_list)).setOnClickListener(this);
        service_id = getIntent().getExtras().getString(Constants.SERVICE_ID, "");

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_post_job:
                startActivity(new Intent(context,JobPostActivity.class).putExtra(Constants.SERVICE_ID,service_id).
                        putExtra(Constants.SUB_CAT_NAME,getIntent().getExtras().getString(Constants.SUB_CAT_NAME)));
                break;
            case R.id.btn_service_list:
                startActivity(new Intent(context,SpByServiceId.class).putExtra(Constants.SERVICE_ID,service_id));
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
