package com.open_source.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.open_source.R;
import com.open_source.modal.RetroObject;
import com.open_source.modal.RetrofitUserData;
import com.open_source.progressHud.ProgressHUD;
import com.open_source.retrofitPack.Constants;
import com.open_source.retrofitPack.RetrofitClient;
import com.open_source.util.ParallaxRecyclerView;
import com.open_source.util.SharedPref;
import com.open_source.util.Utility;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class ActivityFaq extends BaseActivity {
    private static final String TAG = MyOfferList.class.getSimpleName();
    private Context context;
    private ParallaxRecyclerView recyclerView;
    private Toolbar toolbar;
    private TextView toolbar_title;
    private ProgressHUD progressHUD;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_saved_searches);
        context = this;
        init_view();
    }

    private void init_view() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar_title = (TextView) findViewById(R.id.toolbar_title);
        recyclerView = (ParallaxRecyclerView) findViewById(R.id.recyclerView);
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.back_arrow);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        toolbar_title.setText(R.string.faq);
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        GetData();
    }

    private void GetData() {
        if (Utility.isConnectingToInternet(context)) {
            progressHUD = ProgressHUD.show(context, true, false, null);
            RetrofitClient.getAPIService().FaqList(SharedPref.getSharedPreferences(context, Constants.TOKEN)).enqueue(new Callback<RetrofitUserData>() {
                @Override
                public void onResponse(Call<RetrofitUserData> call, Response<RetrofitUserData> response) {
                    if (progressHUD != null && progressHUD.isShowing()) {
                        progressHUD.dismiss();
                    }
                    try {
                        if (response.body().getStatus() == 200) {
                            recyclerView.setAdapter(new FAQ(context, response.body().getObject()));

                        } else if (response.body().getStatus() == 401) {
                            SharedPref.clearPreference(context);
                            startActivity(new Intent(context, WelcomeActivity.class)
                                    .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK));
                            finish();
                        } else {
                            Utility.ShowToastMessage(context, response.body().getMessage());
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(Call<RetrofitUserData> call, Throwable t) {
                    if (progressHUD != null && progressHUD.isShowing()) {
                        progressHUD.dismiss();
                    }
                    Log.e(TAG, "onFailure: " + t.toString());
                }
            });
        } else {
            Utility.ShowToastMessage(context, getString(R.string.internet_connection));
        }

    }

    public class FAQ extends RecyclerView.Adapter<FAQ.MyViewHolder> {
        Context context;
        ArrayList<RetroObject> arrayList;


        public FAQ(Context context, ArrayList<RetroObject> arrayList) {
            this.context = context;
            this.arrayList = arrayList;
        }


        @NonNull
        @Override
        public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new FAQ.MyViewHolder(LayoutInflater.from(context).inflate(R.layout.item_question, parent, false));
        }

        @Override
        public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
            holder.txt_question.setText(arrayList.get(position).getQuestion());
            holder.root_view.setOnClickListener(new Myclick(position));

        }

        @Override
        public int getItemCount() {
            return arrayList.size();
        }

        public class MyViewHolder extends RecyclerView.ViewHolder {
            TextView txt_question;
            RelativeLayout root_view;

            public MyViewHolder(View itemView) {
                super(itemView);
                txt_question = itemView.findViewById(R.id.txt_question);
                root_view = itemView.findViewById(R.id.root_view);
            }
        }
        private class Myclick implements View.OnClickListener {
            int position;

            public Myclick(int pos) {
                position = pos;
            }

            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.root_view:
                        startActivity(new Intent(context, QuestionDetail.class).putExtra(Constants.QUESTION,arrayList.get(position).getQuestion()).
                                putExtra(Constants.ANSWER,arrayList.get(position).getAnswer()));
                        break;
                }
            }
        }
    }



}
