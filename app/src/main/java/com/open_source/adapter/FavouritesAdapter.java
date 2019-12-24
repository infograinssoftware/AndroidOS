package com.open_source.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.open_source.R;
import com.open_source.ServiceProvider.SPServiceRequest;
import com.open_source.activity.LocationInfoActivity;
import com.open_source.activity.OfferConversationActivity;
import com.open_source.activity.OfferListActivtiy;
import com.open_source.modal.PropertyImg;
import com.open_source.modal.RetroObject;
import com.open_source.retrofitPack.Constants;

import java.util.ArrayList;
import java.util.List;

public class FavouritesAdapter extends RecyclerView.Adapter<FavouritesAdapter.MyViewHolder> {

    private static final String TAG = FavouritesAdapter.class.getSimpleName();
    Context context;
    List<RetroObject> favouritesList;
    ArrayList<PropertyImg> arrayList = new ArrayList<>();
    String key;

    public FavouritesAdapter(Context context, List<RetroObject> favouritesList, String key) {
        this.context = context;
        this.favouritesList = favouritesList;
        Log.e(TAG, String.valueOf(favouritesList.size()));
        this.key = key;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.row_saved_search_list, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, final int position) {
        if (key.equalsIgnoreCase("Owner") || key.equalsIgnoreCase("Renter")) {
            Glide.with(context).load(favouritesList.get(position).getUrl() + favouritesList.get(position).getProperty_images()).
                    into(holder.item_image);
            holder.ctv_title.setText(favouritesList.get(position).getType());
            holder.ctv_contact.setVisibility(View.VISIBLE);
            if (key.equalsIgnoreCase("Owner")) {
                holder.ctv_sub_title.setText(context.getString(R.string.renter_name) + favouritesList.get(position).getFirst_name() + " " + favouritesList.get(position).getLast_name());
                holder.ctv_contact.setText(context.getString(R.string.renter_contact) + favouritesList.get(position).getMobileNumber());

            } else if (key.equalsIgnoreCase("Renter")) {
                holder.ctv_sub_title.setText(context.getString(R.string.landloard_name) + favouritesList.get(position).getFirst_name() + " " + favouritesList.get(position).getLast_name());
                holder.ctv_contact.setText(context.getString(R.string.landlaord_contact) + favouritesList.get(position).getMobileNumber());
            }

        } else if (key.equalsIgnoreCase("sp_request") || key.equalsIgnoreCase("user_request")) {
            Glide.with(context).load(favouritesList.get(position).getProfileImage()).into(holder.item_image);
            holder.ctv_title.setText(favouritesList.get(position).getFirst_name() + " " + favouritesList.get(position).getLast_name());
            holder.ctv_sub_title.setText(favouritesList.get(position).getLocation());

        } else {
            if (key.equalsIgnoreCase("offer"))
                holder.detail.setVisibility(View.VISIBLE);
            if (favouritesList.get(position).getProperty_img().size() != 0) {
                RequestOptions requestOptions = new RequestOptions();
                requestOptions.placeholder(R.drawable.default_property);
                requestOptions.error(R.drawable.default_property);
                requestOptions.timeout(30000);
                Glide.with(context).load(favouritesList.get(position).getProperty_img().get(0).getFile_name()).apply(requestOptions).
                        into(holder.item_image);
            }
            holder.ctv_title.setText(favouritesList.get(position).getType());
            holder.ctv_sub_title.setText(favouritesList.get(position).getLocation());
        }

        holder.card_fav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (key.equalsIgnoreCase("sp_request") || key.equalsIgnoreCase("user_request")) {
                    context.startActivity(new Intent(context, SPServiceRequest.class).
                            putExtra(Constants.REQUEST_ID, favouritesList.get(position).getRequest_id()));

                } else if (key.equalsIgnoreCase("offer")) {
                    context.startActivity(new Intent(context, OfferConversationActivity.class).
                            putExtra(Constants.PROPERTY_ID, favouritesList.get(position).getId()));
                }
                else if (key.equalsIgnoreCase("my_pro")) {
                    context.startActivity(new Intent(context, OfferListActivtiy.class).
                            putExtra(Constants.PROPERTY_ID, favouritesList.get(position).getId()));
                } else if (key.equalsIgnoreCase("Owner") || key.equalsIgnoreCase("Renter")) {
                    context.startActivity(new Intent(context, LocationInfoActivity.class).
                            putExtra(Constants.PROPERTY_ID, favouritesList.get(position).getProperty_id()));
                } else {
                    context.startActivity(new Intent(context, LocationInfoActivity.class).
                            putExtra(Constants.PROPERTY_ID, favouritesList.get(position).getId()));
                    ((Activity) context).finish();
                }

            }
        });
        holder.detail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(key.equalsIgnoreCase("offer"))
                context.startActivity(new Intent(context, LocationInfoActivity.class).
                        putExtra(Constants.PROPERTY_ID,favouritesList.get(position).getId()));
            }
        });


    }

    @Override
    public int getItemCount() {
        return favouritesList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        ImageView item_image;
        TextView ctv_title, ctv_sub_title, ctv_contact, detail;
        RelativeLayout card_fav;

        public MyViewHolder(View itemView) {
            super(itemView);

            item_image = itemView.findViewById(R.id.item_image);
            ctv_title = itemView.findViewById(R.id.ctv_title);
            ctv_sub_title = itemView.findViewById(R.id.ctv_sub_title);
            card_fav = itemView.findViewById(R.id.relview);
            ctv_contact = itemView.findViewById(R.id.ctv_contact);
            detail = itemView.findViewById(R.id.detail);
        }
    }
}
