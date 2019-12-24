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

import java.util.List;

/**
 * Created by and-02 on 15/6/18.
 */

public class ProImgAdapter extends RecyclerView.Adapter<ProImgAdapter.MyViewHolder> {
    List<PropertyImg> arrayList;
    Context context;

    public ProImgAdapter(Context context, List<PropertyImg> proList) {
        this.context = context;
        arrayList = proList;
    }


    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.row_img, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull final ProImgAdapter.MyViewHolder holder, final int position) {
        if (arrayList.size() != 0) {
            Glide.with(context).load(arrayList.get(position).getFile_name()).
                    into(holder.item_image);
        }
           holder.item_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                context.startActivity(new Intent(context, ChatImageview.class).putExtra(Constants.IMAGE,arrayList.get(position).getFile_name()));
             /*   final Dialog dialog = new Dialog(context);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setCancelable(false);
                dialog.setContentView(R.layout.fullview_img);
                WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
                Window window = dialog.getWindow();
                layoutParams.copyFrom(window.getAttributes());
                layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;
                layoutParams.height = WindowManager.LayoutParams.MATCH_PARENT;
                layoutParams.gravity = Gravity.CENTER;
                window.setAttributes(layoutParams);
                //dialog.getWindow().setBackgroundDrawable(new ColorDrawable(context.getResources().getColor(R.color.transparent)));
                ImageView img=(ImageView)dialog.findViewById(R.id.img);
                ImageView cross=(ImageView)dialog.findViewById(R.id.cross);
                Glide.with(context).load(arrayList.get(position).getFile_name())
                        .into(img);
                cross.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });
                dialog.show();*/
            }
        });

    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        ImageView item_image;

        public MyViewHolder(View itemView) {
            super(itemView);
            item_image = itemView.findViewById(R.id.img);

        }
    }
}
