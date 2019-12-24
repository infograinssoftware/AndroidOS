package com.open_source.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.Nullable;

import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;

import com.open_source.R;
import com.open_source.modal.RetrofitUserData;
import com.open_source.progressHud.ProgressHUD;
import com.open_source.retrofitPack.Constants;
import com.open_source.retrofitPack.RetrofitClient;
import com.open_source.util.SharedPref;
import com.open_source.util.Utility;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ContactUsActivity extends BaseActivity implements View.OnClickListener{
    private static final String TAG = ContactUsActivity.class.getSimpleName();
    private Context context;
    private EditText cet_first_name,cet_last_name,cet_email,cet_message;
    private RelativeLayout btn_send;
    private ProgressHUD progressHUD;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_us);
        context=this;
        init();
    }

    public void init(){
        cet_first_name=(EditText)findViewById(R.id.cet_first_name);
        cet_last_name=(EditText)findViewById(R.id.cet_last_name);
        cet_email=(EditText)findViewById(R.id.cet_email);
        cet_message=(EditText)findViewById(R.id.cet_message);
        btn_send=(RelativeLayout)findViewById(R.id.btn_send);
        btn_send.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_send:
                if(cet_first_name.getText().toString().isEmpty()){
                    Utility.ShowToastMessage(context,R.string.msg_error_fname);
                }else if(cet_last_name.getText().toString().isEmpty()){
                    Utility.ShowToastMessage(context,R.string.msg_error_lname);
                }else if(!Utility.isValidEmail(cet_email.getText().toString())){
                    Utility.ShowToastMessage(context,R.string.msg_error_email);
                }else  if(cet_message.getText().toString().isEmpty()){
                    Utility.ShowToastMessage(context,R.string.msg_error_message);
                }else {
                    callContactUsAPI();
                }
                break;
        }
    }

    //------------------------- Contact Us ---------------------------
    public void callContactUsAPI(){
        progressHUD=ProgressHUD.show(context,true,false,null);
        RetrofitClient.getAPIService().getContactUsAPI(SharedPref.getSharedPreferences(context, Constants.TOKEN),
                cet_first_name.getText().toString(),
                cet_last_name.getText().toString(),
                cet_email.getText().toString(),
                cet_message.getText().toString()).enqueue(new Callback<RetrofitUserData>() {
            @Override
            public void onResponse(Call<RetrofitUserData> call, Response<RetrofitUserData> response) {
                if(progressHUD!=null&&progressHUD.isShowing()){
                    progressHUD.dismiss();
                }

                try{
                    if(response.body().getStatus()==200){
                        Utility.ShowToastMessage(context,response.body().getMessage());
                        cet_first_name.setText("");
                        cet_last_name.setText("");
                        cet_email.setText("");
                        cet_message.setText("");
                        finish();
                    } else if (response.body().getStatus() == 401) {
                        SharedPref.clearPreference(context);
                        startActivity(new Intent(context, WelcomeActivity.class)
                                .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK));
                        finish();
                    } else {
                        Utility.ShowToastMessage(context,response.body().getMessage());
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<RetrofitUserData> call, Throwable t) {
                if(progressHUD!=null&&progressHUD.isShowing()){
                    progressHUD.dismiss();
                }
                Log.e(TAG, "onFailure: "+t.toString() );
            }
        });
    }
}
