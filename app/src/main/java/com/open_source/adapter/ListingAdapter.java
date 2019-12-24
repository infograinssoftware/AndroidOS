package com.open_source.adapter;

import android.content.Context;
import android.content.Intent;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.open_source.R;
import com.open_source.activity.LocationInfoActivity;
import com.open_source.modal.RetroObject;
import com.open_source.retrofitPack.Constants;

import java.util.List;

public class ListingAdapter extends RecyclerView.Adapter<ListingAdapter.MyViewHolder> {

    private static final String TAG = ListingAdapter.class.getSimpleName();
    Context context;
    List<RetroObject> listingList;

    public ListingAdapter(Context context, List<RetroObject> listingList) {
        this.context = context;
        this.listingList = listingList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.row_listing, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, final int position) {
        RequestOptions requestOptions = new RequestOptions();
        requestOptions.placeholder(R.drawable.default_property);
        requestOptions.error(R.drawable.default_property);
        requestOptions.timeout(30000);
        if (listingList.get(position).getProperty_img().size() != 0) {
            Glide.with(context).load(listingList.get(position).getProperty_img().get(0).getFile_name()).apply(requestOptions).
                    into(holder.item_image);
        }
        holder.ctv_house_name.setText(listingList.get(position).getType());
        holder.ctv_more_info.setText(listingList.get(position).getLocation());
        if (listingList.get(position).getPurpose().equalsIgnoreCase("For Rent")) {
            holder.img_rent.setVisibility(View.VISIBLE);
            holder.img_sell.setVisibility(View.GONE);
        } else {
            holder.img_sell.setVisibility(View.VISIBLE);
            holder.img_rent.setVisibility(View.GONE);
        }

        holder.lay_root.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                context.startActivity(new Intent(context, LocationInfoActivity.class).
                        putExtra(Constants.PROPERTY_ID, listingList.get(position).getId()));
            }
        });




    }

    @Override
    public int getItemCount() {
        return listingList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        ImageView item_image;
        RelativeLayout img_rent, img_sell,lay_root;
        TextView ctv_house_name, ctv_more_info;
        Button btn_more_info;

        public MyViewHolder(View itemView) {
            super(itemView);

            item_image = itemView.findViewById(R.id.item_image);
            ctv_house_name = itemView.findViewById(R.id.ctv_house_name);
            ctv_more_info = itemView.findViewById(R.id.ctv_more_info);
            btn_more_info = itemView.findViewById(R.id.btn_more_info);
            img_rent = itemView.findViewById(R.id.img_rent);
            img_sell = itemView.findViewById(R.id.img_sell);
            lay_root=itemView.findViewById(R.id.lay_root);
        }
    }
}
