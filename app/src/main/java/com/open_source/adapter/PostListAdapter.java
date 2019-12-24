package com.open_source.adapter;

import android.content.Context;
import android.content.Intent;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.open_source.R;
import com.open_source.activity.LocationInfoActivity;
import com.open_source.activity.SellDetailActivity;
import com.open_source.modal.UserList;
import com.open_source.retrofitPack.Constants;

import java.util.List;


public class PostListAdapter extends RecyclerView.Adapter<PostListAdapter.MyViewHolder> {
    Context context;
    List<UserList> postListList;
    String key;


    public PostListAdapter(Context context, List<UserList> userList, String key) {
        this.context = context;
        postListList = userList;
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
    public PostListAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new PostListAdapter.MyViewHolder(LayoutInflater.from(context).inflate(R.layout.row_saved_search_list, parent, false));
    }

    @Override
    public void onBindViewHolder(final PostListAdapter.MyViewHolder viewHolder, final int position) {
        viewHolder.ctv_title.setText(postListList.get(position).getType());
        viewHolder.ctv_sub_title.setText(postListList.get(position).getLocation());
        if (!postListList.get(position).getImg().isEmpty()) {
            Glide.with(context).load(postListList.get(position).getImg()).into(viewHolder.item_image);
        } else {
            viewHolder.item_image.setImageResource(R.drawable.welcome_bg);
        }

        if (key.equalsIgnoreCase("Rent")) {

            viewHolder.img_rent.setVisibility(View.VISIBLE);
            viewHolder.img_sell.setVisibility(View.GONE);
            /*if (postListList.get(position).getPurpose().equalsIgnoreCase("For Rent")) {
                viewHolder.img_rent.setVisibility(View.VISIBLE);
                viewHolder.img_sell.setVisibility(View.GONE);
            } else {
                viewHolder.img_sell.setVisibility(View.VISIBLE);
                viewHolder.img_rent.setVisibility(View.GONE);
            }*/
        }

        viewHolder.relview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (key.equalsIgnoreCase("post")) {
                    context.startActivity(new Intent(context, SellDetailActivity.class).putExtra(Constants.ID, postListList.get(position).getId()));
                } else if (key.equalsIgnoreCase("Search History")) {
                    context.startActivity(new Intent(context, LocationInfoActivity.class).putExtra(Constants.PROPERTY_ID, postListList.get(position).getId()));
                } else if (key.equalsIgnoreCase("Rent")) {
                    context.startActivity(new Intent(context, LocationInfoActivity.class).putExtra(Constants.PROPERTY_ID, postListList.get(position).getId()));
                } else if (key.equalsIgnoreCase("under_cont")) {
                    context.startActivity(new Intent(context, LocationInfoActivity.class).putExtra(Constants.PROPERTY_ID, postListList.get(position).getId()));
                }
                /*else if (key.equalsIgnoreCase("Rental Property")) {
                    context.startActivity(new Intent(context, Renter_property_detail.class).putExtra(Constants.TYPE,"owner"));
                } else if (key.equalsIgnoreCase("Rented Property")) {
                    context.startActivity(new Intent(context, Renter_property_detail.class).putExtra(Constants.TYPE,"renter"));
                }*/

            }
        });
    }

    @Override
    public int getItemCount() {
        return postListList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        ImageView item_image;
        RelativeLayout img_rent, img_sell;
        TextView ctv_title, ctv_sub_title;
        RelativeLayout relview;

        public MyViewHolder(View convertView) {
            super(convertView);
            item_image = convertView.findViewById(R.id.item_image);
            ctv_title = convertView.findViewById(R.id.ctv_title);
            ctv_sub_title = convertView.findViewById(R.id.ctv_sub_title);
            relview = convertView.findViewById(R.id.relview);
            img_rent = itemView.findViewById(R.id.img_rent);
            img_sell = itemView.findViewById(R.id.img_sell);

        }
    }
}


