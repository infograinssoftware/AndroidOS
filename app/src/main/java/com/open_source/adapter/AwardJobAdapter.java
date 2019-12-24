package com.open_source.adapter;

import android.content.Context;
import android.content.Intent;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.open_source.R;
import com.open_source.ServiceProvider.ProjectDescription;
import com.open_source.ServiceProvider.SpJobAwartedFragment;
import com.open_source.activity.WelcomeActivity;
import com.open_source.modal.RetroObject;
import com.open_source.modal.RetrofitUserData;
import com.open_source.retrofitPack.Constants;
import com.open_source.retrofitPack.RetrofitClient;
import com.open_source.util.SharedPref;
import com.open_source.util.Utility;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;
import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AwardJobAdapter extends RecyclerView.Adapter<AwardJobAdapter.MyViewHolder> {
    Context context;
    List<RetroObject> postList;
    String key, type;


    public AwardJobAdapter(Context context, List<RetroObject> postList, String key, String type) {
        this.context = context;
        this.postList = postList;
        this.key = key;
        this.type = type;

    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(context).inflate(R.layout.sp_job_awarted, parent, false));

    }

    @Override
    public void onBindViewHolder(final AwardJobAdapter.MyViewHolder viewHolder, final int position) {
        RequestOptions requestOptions = new RequestOptions();
        requestOptions.placeholder(R.drawable.defaultuser);
        requestOptions.error(R.drawable.defaultuser);
        if (key.equals("2") || key.equals("3") || key.equals("4"))
            viewHolder.btn_accept.setVisibility(View.GONE);
        viewHolder.service_name.setText(postList.get(position).getCategory());
        viewHolder.ratingBar.setRating(Float.parseFloat(postList.get(position).getRating()));
        viewHolder.txt_toal_rate.setText(postList.get(position).getTotal_rate());
        Glide.with(context).load(postList.get(position).getProfileImage()).apply(requestOptions).into(viewHolder.user_img);
        String name = postList.get(position).getFirst_name() + " " + postList.get(position).getLast_name();
        viewHolder.name.setText(name.substring(0, 1).toUpperCase() + name.substring(1));
        try {
            if (!postList.get(position).getCreated_at().isEmpty()) {
                String formattedDiff;
                DateFormat f1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH); //HH for hour of the day (0 - 23)
                f1.setTimeZone(TimeZone.getTimeZone("GMT+5:30"));
                Date d = f1.parse(postList.get(position).getCreated_at());
                String date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date());
                Date d2 = f1.parse(date);
                long diff = d.getTime() - d2.getTime();
                long seconds = diff / 1000;
                long minutes = seconds / 60;
                long hours = minutes / 60;
                long days = hours / 24;
                Log.e("==============:sec ", seconds + "");
                Log.e("==============:mint ", minutes + "");
                Log.e("==============:hour ", hours + "");
                Log.e("==============: days", days + "");
                if (days != 0) {
                    formattedDiff = String.format(Locale.ENGLISH, "%d days", days);
                    formattedDiff = formattedDiff.replace("-", "");
                    viewHolder.time.setText(formattedDiff + " "+context.getString(R.string.ago));
                } else if (hours != 0) {
                    formattedDiff = String.format(Locale.ENGLISH, "%d hours ", hours);
                    formattedDiff = formattedDiff.replace("-", "");
                    viewHolder.time.setText(formattedDiff +" "+context.getString(R.string.ago));
                } else if (seconds != 0) {
                    viewHolder.time.setText(R.string.just_now);
                }
                viewHolder.btn_accept.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        FunAccept(postList.get(position).getPost_id(), "2", position);
                    }
                });

            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        viewHolder.root_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (key.equals("2")) {
                    context.startActivity(new Intent(context, ProjectDescription.class).
                            putExtra(Constants.POST_ID, postList.get(position).getPost_id()).
                            putExtra(Constants.USERID, postList.get(position).getUserid()).
                            putExtra(Constants.TYPE,type).putExtra(Constants.KEY,key));
                }
                if (key.equals("3")) {
                    context.startActivity(new Intent(context, ProjectDescription.class).
                            putExtra(Constants.POST_ID, postList.get(position).getPost_id()).
                            putExtra(Constants.USERID, postList.get(position).getUserid()).
                            putExtra(Constants.TYPE,type).putExtra(Constants.KEY,key));
                }
            }
        });

    }

    private void FunAccept(String post_id, String s, final int position) {
        if (Utility.isConnectingToInternet(context)) {
            RetrofitClient.getAPIService().SP_AWARTED_STATUS(SharedPref.getSharedPreferences(context, Constants.TOKEN), post_id, "2").enqueue(new Callback<RetrofitUserData>() {
                @Override
                public void onResponse(Call<RetrofitUserData> call, Response<RetrofitUserData> response) {

                    try {
                        if (response.body().getStatus() == 200) {
                            Utility.ShowToastMessage(context, response.body().getMessage());
                            SpJobAwartedFragment.deletePos(position);

                        } else if (response.body().getStatus() == 401) {
                            SharedPref.clearPreference(context);
                            context.startActivity(new Intent(context, WelcomeActivity.class)
                                    .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK));
                        } else {
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

    }

    @Override
    public int getItemCount() {
        return postList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        CircleImageView user_img;
        TextView name, time, service_name,txt_toal_rate;
        Button btn_accept;
        RatingBar ratingBar;
        RelativeLayout root_view;

        public MyViewHolder(View convertView) {
            super(convertView);
            user_img = convertView.findViewById(R.id.user_img);
            name = convertView.findViewById(R.id.user_name);
            service_name = convertView.findViewById(R.id.service_name);
            time = convertView.findViewById(R.id.time);
            ratingBar = convertView.findViewById(R.id.rattingbar);
            btn_accept = convertView.findViewById(R.id.btn_accept);
            txt_toal_rate=convertView.findViewById(R.id.txt_toal_rate);
            root_view = convertView.findViewById(R.id.root_view);
        }
    }
}

