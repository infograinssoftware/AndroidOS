package com.open_source.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.open_source.R;
import com.open_source.modal.RetrofitUserData;
import com.open_source.progressHud.ProgressHUD;
import com.open_source.retrofitPack.RetrofitClient;
import com.open_source.util.SharedPref;
import com.open_source.util.Utility;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ForgetPasswordActivity extends BaseActivity implements View.OnClickListener {

    private static final String TAG = ForgetPasswordActivity.class.getSimpleName();
    Context context;
    TextView ctv_login;
    EditText cet_email;
    RelativeLayout btn_submit;
    ProgressHUD progressHUD;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget_pwd);
        context=this;
        init();
    }

    public void init(){
        ctv_login=(TextView)findViewById(R.id.ctv_login);
        cet_email=(EditText)findViewById(R.id.cet_email);
        btn_submit=(RelativeLayout)findViewById(R.id.btn_submit);

        btn_submit.setOnClickListener(this);
        ctv_login.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_submit:
                if(!Utility.isValidEmail(cet_email.getText().toString())){
                    Utility.ShowToastMessage(context,R.string.msg_error_email);
                }else {
                    callForgetPasswordAPI();
                }
                break;

            case R.id.ctv_login:
               // startActivity(new Intent(context,ForgetPasswordActivity.class));
                break;
        }
    }

    //----------------------- Forget Password ------------------------
    public void callForgetPasswordAPI(){
        if(Utility.isConnectingToInternet(context)){
            progressHUD=ProgressHUD.show(context,true,false,null);
            RetrofitClient.getAPIService().getForgetPasswordAPI(cet_email.getText().toString()).enqueue(new Callback<RetrofitUserData>() {
                @Override
                public void onResponse(Call<RetrofitUserData> call, Response<RetrofitUserData> response) {
                    if(progressHUD!=null&&progressHUD.isShowing()){
                        progressHUD.dismiss();
                    }

                    try{
                        if(response.body().getStatus()==200){
                            Utility.ShowToastMessage(context,response.body().getMessage());
                            finish();
                            //finish();
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
        }else {
            Utility.ShowToastMessage(context,R.string.internet_connection);
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
