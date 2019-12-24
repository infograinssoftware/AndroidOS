package com.open_source.adapter;

import android.content.Context;
import android.content.Intent;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.open_source.R;
import com.open_source.activity.SellDetailActivity;
import com.open_source.modal.RetroObject;
import com.open_source.retrofitPack.Constants;

import java.util.List;

public class MakeOfferAdapter extends RecyclerView.Adapter<MakeOfferAdapter.MyViewHolder> {

    private static final String TAG = MakeOfferAdapter.class.getSimpleName();
    Context context;
    List<RetroObject> makeOrderList;

    public MakeOfferAdapter(Context context, List<RetroObject> makeOrderList) {
        this.context = context;
        this.makeOrderList = makeOrderList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.row_saved_search_list, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, final int position) {
        holder.ctv_title.setText(makeOrderList.get(position).getType());
        holder.ctv_sub_title.setText(makeOrderList.get(position).getLocation());
        if (makeOrderList.get(position).getProperty_img().size() != 0) {
            Glide.with(context).load(makeOrderList.get(position).getProperty_img().get(0).getFile_name()).into(holder.item_image);
        }
        holder.relview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                context.startActivity(new Intent(context, SellDetailActivity.class).putExtra(Constants.ID, makeOrderList.get(position).getId()));
            }
        });
    }

    @Override
    public int getItemCount() {
        return makeOrderList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        ImageView item_image;
        TextView ctv_title, ctv_sub_title;
        RelativeLayout relview;

        public MyViewHolder(View itemView) {
            super(itemView);

            item_image = itemView.findViewById(R.id.item_image);
            ctv_title = itemView.findViewById(R.id.ctv_title);
            ctv_sub_title = itemView.findViewById(R.id.ctv_sub_title);
            relview = itemView.findViewById(R.id.relview);
        }
    }
}
