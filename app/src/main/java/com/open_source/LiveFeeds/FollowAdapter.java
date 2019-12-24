package com.open_source.LiveFeeds;

import android.content.Context;
import android.content.Intent;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.open_source.R;
import com.open_source.activity.WelcomeActivity;
import com.open_source.modal.RetroObject;
import com.open_source.modal.RetrofitUserData;
import com.open_source.retrofitPack.Constants;
import com.open_source.retrofitPack.RetrofitClient;
import com.open_source.util.SharedPref;
import com.open_source.util.Utility;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class FollowAdapter extends RecyclerView.Adapter<FollowAdapter.MyViewHolder> {
    Context context;
    ArrayList<RetroObject> arrayList;
    String status;

    public FollowAdapter(Context context, ArrayList<RetroObject> arrayList, String status) {
        this.context = context;
        this.arrayList = arrayList;
        this.status = status;

    }


    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new FollowAdapter.MyViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.row_follow, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, final int position) {
        if (status.equals("follow"))
        holder.btn_accept.setVisibility(View.GONE);
        Glide.with(context).load(arrayList.get(position).getProfileImage()).into(holder.user_img);
        holder.txt_name.setText(arrayList.get(position).getFirst_name() + " " + arrayList.get(position).getLast_name());
        holder.btn_accept.setOnClickListener(new MyClick(position));
        holder.root_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                context.startActivity(new Intent(context,OtherUserProfile.class).putExtra(Constants.USER_ID,arrayList.get(position).getUser_id()).putExtra(Constants.NAME,arrayList.get(position).getFirst_name()+" "+arrayList.get(position).getLast_name()));
            }
        });

    }


    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        CircleImageView user_img;
        TextView txt_name;
        Button btn_accept;
        RelativeLayout root_view;

        public MyViewHolder(View itemView) {
            super(itemView);
            user_img = itemView.findViewById(R.id.user_img);
            txt_name = itemView.findViewById(R.id.txt_name);
            btn_accept = itemView.findViewById(R.id.btn_accept);
            root_view=itemView.findViewById(R.id.root_view);
        }
    }

    private class MyClick implements View.OnClickListener {
        int pos;

        public MyClick(int position) {
            pos = position;

        }

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.btn_accept:
                    if (Utility.isConnectingToInternet(context)) {
                        RetrofitClient.getAPIService().FollowReqAccept(SharedPref.getSharedPreferences(context, Constants.TOKEN), arrayList.get(pos).getFollower_id(), "2").enqueue(new Callback<RetrofitUserData>() {
                            @Override
                            public void onResponse(Call<RetrofitUserData> call, Response<RetrofitUserData> response) {
                                try {
                                    if (response.body().getStatus() == 200) {
                                        arrayList.remove(pos);
                                        notifyDataSetChanged();


                                    } else if (response.body().getStatus() == 401) {
                                        SharedPref.clearPreference(context);
                                        context.startActivity(new Intent(context, WelcomeActivity.class)
                                                .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK));
                                    }  else {
                                        Utility.ShowToastMessage(context, response.body().getMessage());
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }

                            @Override
                            public void onFailure(Call<RetrofitUserData> call, Throwable t) {

                            }
                        });
                    } else {

                    }

                    break;
            }

        }
    }
}
