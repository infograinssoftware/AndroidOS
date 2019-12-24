package com.open_source.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.appcompat.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.open_source.R;
import com.open_source.modal.DisclosreDoc;
import com.open_source.retrofitPack.Constants;
import com.open_source.util.Utility;
import com.silencedut.expandablelayout.ExpandableLayout;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DisclosureActivity extends BaseActivity implements View.OnClickListener {
    private static final String TAG = DisclosureActivity.class.getSimpleName();
    private Context context;
    private Toolbar toolbar;
    private TextView toolbar_title, ctv_agreement;
    private  ExpandableLayout expand_question, expand_doc, expand_fixture, expand_important;
    private LinearLayout recycle_question, recycle_doc, recycle_fixture;
    private List<String> array_question;
    private List<String> array_fixtures = new ArrayList<>();
    private ArrayList<String> array_fixtures1 = new ArrayList<>();
    private LayoutInflater layoutInflater;
    private ImageView img_expand_doc, img_expand_question, img_expand_fixture, img_expand_agreement;
    private ArrayList<DisclosreDoc> array_doc;
    private String status = "", agreement_file = "", property_id = "", property_type = "";
    private CheckBox check_agreement;
    private Button buy_now;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_documents);
        context = this;
        layoutInflater = getLayoutInflater();
        init();
    }

    public void init() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.back_arrow);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        toolbar_title = (TextView) findViewById(R.id.toolbar_title);
        toolbar_title.setText(R.string.property_detail);
        recycle_question = findViewById(R.id.que_list);
        recycle_doc = findViewById(R.id.documents_list);
        recycle_fixture = findViewById(R.id.recycle_fixture);

        img_expand_doc = findViewById(R.id.img_expand_documents);
        img_expand_question = findViewById(R.id.img_expand_questionnaires);
        img_expand_fixture = findViewById(R.id.img_expand_fixture);
        img_expand_agreement = findViewById(R.id.img_expand_agreement);
        expand_important = findViewById(R.id.expand_important);

        expand_question = findViewById(R.id.expandable_questionnaires);
        expand_doc = findViewById(R.id.expandable_doc);
        expand_fixture = findViewById(R.id.expand_fixture);
        check_agreement = findViewById(R.id.check_agreement);
        ctv_agreement = findViewById(R.id.ctv_agreement);
        buy_now = findViewById(R.id.buy_now);
        buy_now.setOnClickListener(this);
        //callPropertyDetail();
        ctv_agreement.setOnClickListener(this);

        // Intent intent = getIntent();
        Bundle bdl = getIntent().getExtras();
        if (getIntent().hasExtra("ARRAYLIST"))
        {
            array_question = Arrays.asList(bdl.getString(Constants.DISCLOSER).split(","));
            array_doc = (ArrayList<DisclosreDoc>) bdl.getSerializable("ARRAYLIST");
            if (array_doc.size() <= 0) {
                expand_doc.setVisibility(View.GONE);
            }
            expand_fixture.setVisibility(View.GONE);
            expand_question.setVisibility(View.GONE);
        }
        else {
            expand_doc.setVisibility(View.GONE);
            expand_question.setVisibility(View.GONE);
            array_fixtures = Arrays.asList(bdl.getString(Constants.FIXTURES).split(","));
            array_fixtures1.addAll(array_fixtures);
            if (!bdl.getString(Constants.COUNTERSTOP, "").isEmpty())
                array_fixtures1.add("CounterTop :  " + bdl.getString(Constants.COUNTERSTOP, ""));

            if (!bdl.getString(Constants.FIRE_PLACE, "").isEmpty())
                array_fixtures1.add("Fireplace :  " + bdl.getString(Constants.FIRE_PLACE, ""));

            if (!bdl.getString(Constants.PATIOS, "").isEmpty())
                array_fixtures1.add("Private Patios :  " + bdl.getString(Constants.PATIOS, ""));

            if (!bdl.getString(Constants.CARPET_FLORING, "").isEmpty())
                array_fixtures1.add("Carpet Flooring :  " + bdl.getString(Constants.CARPET_FLORING, ""));

            if (!bdl.getString(Constants.CEILING_FAN, "").isEmpty())
                array_fixtures1.add("Ceiling Fans :  " + bdl.getString(Constants.CEILING_FAN, ""));

            if (!bdl.getString(Constants.WASHER_DRAWER, "").isEmpty())
                array_fixtures1.add("Washers and Dryers Connection :  " + bdl.getString(Constants.WASHER_DRAWER, ""));

            if (!bdl.getString(Constants.WOOD_FLORING, "").isEmpty())
                array_fixtures1.add("Wood Flooring :  " + bdl.getString(Constants.WOOD_FLORING, ""));
            if (array_fixtures1.size() <= 1) {
                expand_fixture.setVisibility(View.GONE);
            }
            if (array_question!=null) {
                if (array_question.size() <= 1) {
                    expand_question.setVisibility(View.GONE);
                }
            }
            if (agreement_file.isEmpty()) {
                expand_important.setVisibility(View.GONE);
            }
        }


        agreement_file = bdl.getString(Constants.AGREEMENT_FILE, "");
        property_id = getIntent().getExtras().getString(Constants.PROPERTY_ID, "");
        property_type = getIntent().getExtras().getString(Constants.TYPE, "");
        status = bdl.getString(Constants.STATUS, status);

        if (status.equalsIgnoreCase("AsIs")) {
            ((LinearLayout) findViewById(R.id.agreement_layout)).setVisibility(View.GONE);
        } else if (status.equalsIgnoreCase("View")) {
            ((LinearLayout) findViewById(R.id.agreement_layout)).setVisibility(View.GONE);
            buy_now.setVisibility(View.GONE);
        }

        if (array_question!= null) {
            for (int i = 0; i < array_question.size(); i++) {
                View view = layoutInflater.inflate(R.layout.row_disclosure_layout, recycle_question, false);
                ((TextView) view.findViewById(R.id.ctv_name)).setText(String.valueOf(i + 1) + ". " + array_question.get(i));
                recycle_question.addView(view);
            }
        }
        for (int i = 0; i < array_fixtures1.size(); i++) {
            View view = layoutInflater.inflate(R.layout.row_disclosure_layout, recycle_fixture, false);
            ((TextView) view.findViewById(R.id.ctv_name)).setText(String.valueOf(i + 1) + ". " + array_fixtures1.get(i));
            recycle_fixture.addView(view);
        }
        if (array_doc!= null) {
            for (int i = 0; i < array_doc.size(); i++) {
                View view = layoutInflater.inflate(R.layout.row_disclosure_buy_doc, recycle_doc, false);
                TextView txt = view.findViewById(R.id.ctv_name);
                if (array_doc.get(i).getType().equalsIgnoreCase("pdf") || array_doc.get(i).getType().equalsIgnoreCase(".pdf")) {
                    ((ImageView) view.findViewById(R.id.doc_img)).setBackground(getResources().getDrawable(R.drawable.icon_pdf));
                } else if (array_doc.get(i).getType().equalsIgnoreCase("doc") || array_doc.get(i).getType().equalsIgnoreCase(".docx")) {
                    ((ImageView) view.findViewById(R.id.doc_img)).setBackground(getResources().getDrawable(R.drawable.icon_doc));
                } else if (array_doc.get(i).getType().equalsIgnoreCase("image")) {
                    ((ImageView) view.findViewById(R.id.doc_img)).setBackground(getResources().getDrawable(R.drawable.icon_picture));
                } else {
                    ((ImageView) view.findViewById(R.id.doc_img)).setBackground(getResources().getDrawable(R.drawable.icon_doc));
                }
                txt.setText(array_doc.get(i).getName());
                final TextView text = new TextView(context);
                final TextView text1 = new TextView(context);
                text.setTag(i);
                text1.setTag(array_doc.get(i).getType());
                // str_file = array_doc.get(i).getFile();
                view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String id = String.valueOf(text.getTag());
                        String type = String.valueOf(text1.getTag());
                        if (type.equalsIgnoreCase("image")) {
                            context.startActivity(new Intent(context, ChatImageview.class).putExtra(Constants.IMAGE, array_doc.get(Integer.valueOf(id)).getFile()));
                        } else {
                            startActivity(new Intent(DisclosureActivity.this, PdfViewActivity.class).
                                    putExtra(Constants.DISCLOSURE_DOC, array_doc.get(Integer.valueOf(id)).getFile()).putExtra(Constants.TYPE, type));
                        }

                    }
                });
                recycle_doc.addView(view);
            }
        }

        expand_question.setOnExpandListener(new ExpandableLayout.OnExpandListener() {
            @Override
            public void onExpand(boolean expanded) {
                if (expanded) {
                    img_expand_question.setBackground(getResources().getDrawable(R.drawable.minus));
                } else {
                    img_expand_question.setBackground(getResources().getDrawable(R.drawable.plus));
                }
            }
        });
        expand_doc.setOnExpandListener(new ExpandableLayout.OnExpandListener() {
            @Override
            public void onExpand(boolean expanded) {
                if (expanded) {
                    img_expand_doc.setBackground(getResources().getDrawable(R.drawable.minus));
                } else {
                    img_expand_doc.setBackground(getResources().getDrawable(R.drawable.plus));
                }
            }
        });
        expand_fixture.setOnExpandListener(new ExpandableLayout.OnExpandListener() {
            @Override
            public void onExpand(boolean expanded) {
                if (expanded) {
                    img_expand_fixture.setBackground(getResources().getDrawable(R.drawable.minus));
                } else {
                    img_expand_fixture.setBackground(getResources().getDrawable(R.drawable.plus));
                }
            }
        });
        expand_important.setOnExpandListener(new ExpandableLayout.OnExpandListener() {
            @Override
            public void onExpand(boolean expanded) {
                if (expanded) {
                    img_expand_agreement.setBackground(getResources().getDrawable(R.drawable.minus));
                } else {
                    img_expand_agreement.setBackground(getResources().getDrawable(R.drawable.plus));
                }
            }
        });
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

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.buy_now:
                if (!check_agreement.isChecked() && status.equalsIgnoreCase("BuyNow")) {
                    Utility.ShowToastMessage(context, getString(R.string.msg_agree_validation));
                } else {
                    if (!property_id.isEmpty() && !property_type.isEmpty()) {
                        startActivity(new Intent(context, PaymentConfirmation.class).putExtra(Constants.PROPERTY_ID, property_id).putExtra(Constants.TYPE, property_type));
                    }
                }
                break;
            case R.id.ctv_agreement:
                startActivity(new Intent(context, PdfViewActivity.class).putExtra(Constants.DISCLOSURE_DOC, agreement_file));
                break;
        }
    }

}
