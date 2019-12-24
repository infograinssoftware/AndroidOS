package com.open_source.adapter;

import android.content.Context;
import android.content.Intent;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.open_source.R;
import com.open_source.ServiceProvider.MyPostBidderDetail;
import com.open_source.modal.RetroObject;
import com.open_source.retrofitPack.Constants;
import com.open_source.util.SharedPref;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import de.hdodenhof.circleimageview.CircleImageView;


public class ServiceRattingAdapter extends RecyclerView.Adapter<ServiceRattingAdapter.MyViewHolder> {
    Context context;
    List<RetroObject> postList;
    String key;


    public ServiceRattingAdapter(Context context, List<RetroObject> postList, String key) {
        this.context = context;
        this.postList = postList;
        this.key = key;

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
        return new MyViewHolder(LayoutInflater.from(context).inflate(R.layout.layout_ratting_review, parent, false));

    }


    @Override
    public void onBindViewHolder(final MyViewHolder viewHolder, final int position) {
        viewHolder.amount.setVisibility(View.VISIBLE);
        viewHolder.time.setVisibility(View.VISIBLE);
        if (key.equals("my_post_bidder")) {
            viewHolder.name.setTextColor(context.getResources().getColor(R.color.colorPrimaryDark));
        }
        viewHolder.days.setText(context.getString(R.string.in) + postList.get(position).getDays() + context.getString(R.string.days));
        viewHolder.total_rate.setText(postList.get(position).getTotal_rate());
        viewHolder.ratingBar.setRating(Float.parseFloat(postList.get(position).getRating()));
        Glide.with(context).load(postList.get(position).getProfileImage()).into(viewHolder.user_img);
        String name = postList.get(position).getFirst_name() + " " + postList.get(position).getLast_name();
        viewHolder.name.setText(name.substring(0, 1).toUpperCase() + name.substring(1));
        viewHolder.desc.setText(postList.get(position).getProposal());
        try {
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
           /* Log.e( "==============: ",days+"");
            Log.e( "==============: ",hours+"");
            Log.e( "==============: ",minutes+"");
            Log.e( "==============: ",seconds+"");*/
            if (days != 0) {
                formattedDiff = String.format(Locale.ENGLISH, "%d days", days);
                formattedDiff = formattedDiff.replace("-", "");
                viewHolder.time.setText(formattedDiff + " " + R.string.ago);
            } else if (hours != 0) {
                formattedDiff = String.format(Locale.ENGLISH, "%d hours ", hours);
                formattedDiff = formattedDiff.replace("-", "");
                viewHolder.time.setText(formattedDiff + " " + R.string.ago);
            } else if (minutes != 0) {
                formattedDiff = String.format(Locale.ENGLISH, "%d minutes", minutes);
                formattedDiff = formattedDiff.replace("-", "");
                viewHolder.time.setText(formattedDiff + " " + R.string.ago);
            } else if (seconds != 0) {
                viewHolder.time.setText(R.string.just_now);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        viewHolder.amount.setText("$" + postList.get(position).getBid_amount() + " USD");
//        viewHolder.amount.setText(SharedPref.getSharedPreferences(context, Constants.CURRENCY_SYMBOL)+ " " + postList.get(position).getBid_amount());

        viewHolder.root_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (key.equals("my_post_bidder")) {
                    context.startActivity(new Intent(context, MyPostBidderDetail.class).
                            putExtra(Constants.POST_ID, postList.get(position).getPost_id()).
                            putExtra(Constants.USERID, postList.get(position).getUserid()).
                            putExtra(Constants.BIDAMOUNT, postList.get(position).getBid_amount()).
                            putExtra(Constants.DAYS, postList.get(position).getDays()));
                } else if (key.equals("jobpost")) {

                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return postList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        CircleImageView user_img;
        TextView name, desc, amount, time, days, total_rate;
        RelativeLayout root_view;
        RatingBar ratingBar;

        public MyViewHolder(View convertView) {
            super(convertView);
            user_img = convertView.findViewById(R.id.user_img);
            name = convertView.findViewById(R.id.name);
            desc = convertView.findViewById(R.id.desc);
            amount = convertView.findViewById(R.id.amount);
            time = convertView.findViewById(R.id.time);
            days = convertView.findViewById(R.id.days);
            root_view = convertView.findViewById(R.id.root_view);
            ratingBar = convertView.findViewById(R.id.rattingbar);
            total_rate = convertView.findViewById(R.id.total_rate);
        }
    }
}




