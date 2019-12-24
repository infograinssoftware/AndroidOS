package com.open_source.activity;

import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import android.text.Html;
import android.widget.TextView;
import com.open_source.R;
import com.open_source.retrofitPack.Constants;

public class QuestionDetail extends BaseActivity {
    private Toolbar toolbar;
    private TextView toolbar_title;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.question_detail);
        bind_Id();
    }

    private void bind_Id() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar_title = (TextView) findViewById(R.id.toolbar_title);
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.back_arrow);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        toolbar_title.setText(R.string.faq);
        ((TextView) findViewById(R.id.txt_question)).setText(Html.fromHtml(getIntent().getExtras().getString(Constants.QUESTION)));
        ((TextView) findViewById(R.id.answer)).setText(Html.fromHtml(getIntent().getExtras().getString(Constants.ANSWER)));
    }
}
