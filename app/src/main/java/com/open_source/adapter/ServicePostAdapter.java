package com.open_source.adapter;

import android.content.Context;
import android.content.Intent;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.open_source.R;
import com.open_source.ServiceProvider.JobPostDetail;
import com.open_source.ServiceProvider.MyPostBidderList;
import com.open_source.ServiceProvider.PostBidActivity;
import com.open_source.modal.RetroObject;
import com.open_source.retrofitPack.Constants;
import com.open_source.util.Utility;

import java.util.List;


public class ServicePostAdapter extends RecyclerView.Adapter<ServicePostAdapter.MyViewHolder> {
    Context context;
    List<RetroObject> postList;
    String key;


    public ServicePostAdapter(Context context, List<RetroObject> postList, String key) {
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
        return new MyViewHolder(LayoutInflater.from(context).inflate(R.layout.row_service_post, parent, false));
    }

    @Override
    public void onBindViewHolder(final MyViewHolder viewHolder, final int position) {
        if (key.equals("my_post")) {
            viewHolder.bid.setVisibility(View.GONE);
        } else if (key.equals("my_bid")) {
            viewHolder.bid.setVisibility(View.GONE);
            if (postList.get(position).getStatus().equals("0")) {
                viewHolder.pending.setVisibility(View.VISIBLE);
                viewHolder.awarded.setVisibility(View.GONE);
            } else if (postList.get(position).getStatus().equals("1")) {
                viewHolder.pending.setVisibility(View.GONE);
                viewHolder.awarded.setVisibility(View.VISIBLE);
            } else if (postList.get(position).getStatus().equals("2")) {
                viewHolder.pending.setVisibility(View.GONE);
                viewHolder.awarded.setVisibility(View.VISIBLE);
                viewHolder.awarded.setText(R.string.running);
            } else if (postList.get(position).getStatus().equals("3")) {
                viewHolder.pending.setVisibility(View.GONE);
                viewHolder.awarded.setVisibility(View.VISIBLE);
                viewHolder.awarded.setText(R.string.completed);
            }

        }
        viewHolder.bid_status.setText("("+context.getString(R.string.avg_rate) + postList.get(position).getHigh_bid() + " - " + postList.get(position).getTotal_bid() + context.getString(R.string.bid)+")");
        viewHolder.service_name.setText(postList.get(position).getCategory());
        viewHolder.date_time.setText(Utility.parseDateToddMMyyyy(postList.get(position).getCreated_at()));
        viewHolder.description.setText(postList.get(position).getDescription());
        viewHolder.root_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (key.equals("joblist") || key.equals("my_bid")) {
                    context.startActivity(new Intent(context, JobPostDetail.class).
                            putExtra(Constants.ID, postList.get(position).getId()).putExtra(Constants.KEY, key));
                } else if (key.equals("my_post")) {
                    context.startActivity(new Intent(context, MyPostBidderList.class).
                            putExtra(Constants.POST_ID, postList.get(position).getPost_id()));
                }

            }
        });
        viewHolder.bid.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (key.equals("joblist")) {
                    context.startActivity(new Intent(context, PostBidActivity.class).
                            putExtra(Constants.ID, postList.get(position).getId()));
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return postList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView service_name, date_time, description, bid_status;
        Button bid, pending, awarded;
        RelativeLayout root_view;

        public MyViewHolder(View convertView) {
            super(convertView);
            service_name = convertView.findViewById(R.id.service_name);
            date_time = convertView.findViewById(R.id.date_time);
            description = convertView.findViewById(R.id.description);
            bid_status = convertView.findViewById(R.id.bid_status);
            bid = itemView.findViewById(R.id.bid);
            pending = itemView.findViewById(R.id.pending);
            awarded = itemView.findViewById(R.id.awarded);
            root_view = itemView.findViewById(R.id.root_view);
        }
    }
}



