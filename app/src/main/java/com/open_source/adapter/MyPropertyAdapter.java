package com.open_source.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.open_source.R;
import com.open_source.activity.LocationInfoActivity;
import com.open_source.activity.SellDetailActivity;
import com.open_source.modal.UserList;
import com.open_source.retrofitPack.Constants;

import java.util.List;

public class MyPropertyAdapter extends RecyclerView.Adapter<MyPropertyAdapter.MyViewHolder> {
    Context context;
    List<UserList> postListList;
    String type;


    public MyPropertyAdapter(Context context, List<UserList> userList, String type) {
        this.context = context;
        postListList = userList;
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
        return new MyPropertyAdapter.MyViewHolder(LayoutInflater.from(context).inflate(R.layout.row_my_property, parent, false));
    }

    @Override
    public void onBindViewHolder(final MyViewHolder viewHolder, final int position) {
        viewHolder.ctv_title.setText(postListList.get(position).getType());
        viewHolder.ctv_place.setText(postListList.get(position).getLocation());
        if (!postListList.get(position).getImg().isEmpty()) {
            Glide.with(context).load(postListList.get(position).getImg())
                    .into(viewHolder.pro_img);
        } else {
            viewHolder.pro_img.setImageResource(R.drawable.detail_bg);
        }
        viewHolder.relview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (type.equals("other")) {
                    context.startActivity(new Intent(context, LocationInfoActivity.class).putExtra(Constants.PROPERTY_ID, postListList.get(position).getId()));
                } else {
                    context.startActivity(new Intent(context, SellDetailActivity.class).putExtra(Constants.ID, postListList.get(position).getId()));
                }


            }
        });
        if (postListList.get(position).getPurpose().equalsIgnoreCase("For Rent")) {
            viewHolder.img_rent.setVisibility(View.VISIBLE);
            viewHolder.img_sell.setVisibility(View.GONE);
        } else {
            viewHolder.img_sell.setVisibility(View.VISIBLE);
            viewHolder.img_rent.setVisibility(View.GONE);
        }

    }

    @Override
    public int getItemCount() {
        return postListList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        ImageView pro_img;
        RelativeLayout img_rent, img_sell;
        TextView ctv_title, ctv_place;
        FrameLayout relview;

        public MyViewHolder(View convertView) {
            super(convertView);
            pro_img = convertView.findViewById(R.id.pro_img);
            ctv_title = convertView.findViewById(R.id.ctv_title);
            ctv_place = convertView.findViewById(R.id.ctv_place);
            relview = convertView.findViewById(R.id.relview);
            img_rent = convertView.findViewById(R.id.img_rent);
            img_sell = convertView.findViewById(R.id.img_sell);

        }


    }
}



