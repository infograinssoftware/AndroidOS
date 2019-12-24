package com.open_source.adapter;

import android.content.Context;
import android.content.Intent;
import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.open_source.R;
import com.open_source.activity.LocationInfoActivity;
import com.open_source.modal.RetroObject;
import com.open_source.retrofitPack.Constants;
import com.open_source.util.SharedPref;

import java.util.List;

/**
 * Created by and-02 on 2/7/18.
 */

public class PaymentAdapter extends RecyclerView.Adapter<PaymentAdapter.MyViewHolder> {
    private static final String TAG = PaymentAdapter.class.getSimpleName();
    Context context;
    List<RetroObject> favouritesList;

    public PaymentAdapter(Context context, List<RetroObject> favouritesList) {
        this.context = context;
        this.favouritesList = favouritesList;
        //Log.e(TAG, String.valueOf(favouritesList.size()));
    }

    @NonNull
    @Override
    public PaymentAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new PaymentAdapter.MyViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.row_saved_search_list, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull PaymentAdapter.MyViewHolder holder, final int position) {
        holder.lay_time.setVisibility(View.VISIBLE);
        String array[] = favouritesList.get(position).getProperty_images().split(",");
        String str=favouritesList.get(position).getUrl() + array[0];
        Log.e(TAG, "onBindViewHolder:====== "+str );
        Glide.with(context).load(str).into(holder.item_image);
        holder.txt_datetime.setText(favouritesList.get(position).getTime());
        holder.ctv_title.setText(favouritesList.get(position).getType());

        String amount="$"+favouritesList.get(position).getAmount();
//        String amount= SharedPref.getSharedPreferences(context, Constants.CURRENCY_SYMBOL)+ " "+favouritesList.get(position).getAmount();

        amount=amount.replace("$$","$");
//        amount=amount.replace("$$",SharedPref.getSharedPreferences(context, Constants.CURRENCY_SYMBOL));
        holder.ctv_sub_title.setText(amount);
        holder.relview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                context.startActivity(new Intent(context, LocationInfoActivity.class).putExtra(Constants.PROPERTY_ID,favouritesList.get(position).getProperty_id()));
            }
        });
    }

    @Override
    public int getItemCount() {
        return favouritesList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        ImageView item_image;
        TextView ctv_title, ctv_sub_title,txt_datetime;
        RelativeLayout card_fav,relview;
        LinearLayout lay_time;

        public MyViewHolder(View itemView) {
            super(itemView);

            item_image = itemView.findViewById(R.id.item_image);
            ctv_title = itemView.findViewById(R.id.ctv_title);
            ctv_sub_title = itemView.findViewById(R.id.ctv_sub_title);
            card_fav = itemView.findViewById(R.id.relview);
            txt_datetime=itemView.findViewById(R.id.txt_datetime);
            relview=itemView.findViewById(R.id.relview);
            lay_time=itemView.findViewById(R.id.lay_time);
        }
    }
}
