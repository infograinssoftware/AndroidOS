package com.open_source.adapter;

import android.content.Context;
import android.content.Intent;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import com.bumptech.glide.Glide;
import com.open_source.R;
import com.open_source.activity.ChatImageview;
import com.open_source.modal.PropertyImg;
import com.open_source.retrofitPack.Constants;

import java.util.ArrayList;

public class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.MyViewHolder> {
    Context context;
    ArrayList<PropertyImg> List;

    public ImageAdapter(Context context, ArrayList<PropertyImg> List) {
        this.context = context;
        this.List = List;
    }

    @Override
    public ImageAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ImageAdapter.MyViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_img, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ImageAdapter.MyViewHolder holder, final int position) {
        holder.img_delete.setVisibility(View.GONE);
        Glide.with(context).load(List.get(position).getFile_name()).into(holder.img_work);
        holder.img_work.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                context.startActivity(new Intent(context, ChatImageview.class).putExtra(Constants.IMAGE, List.get(position).getFile_name()));
            }
        });

    }

    @Override
    public int getItemCount() {
        return List.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        ImageView img_work, img_delete;

        public MyViewHolder(View itemView) {
            super(itemView);
            img_work = itemView.findViewById(R.id.img_work);
            img_delete = itemView.findViewById(R.id.img_delete);
        }
    }

}

