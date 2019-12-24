package com.open_source.adapter;

import android.content.Context;
import android.content.Intent;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import com.open_source.R;
import com.open_source.ServiceProvider.SelectServiceActivity;
import com.open_source.modal.RetroObject;
import com.open_source.retrofitPack.Constants;
import java.util.List;
import de.hdodenhof.circleimageview.CircleImageView;


public class CatogeryAdapter extends RecyclerView.Adapter<CatogeryAdapter.MyViewHolder> {
    Context context;
    List<RetroObject> catogeryList;
    String type;

    public CatogeryAdapter(Context context, List<RetroObject> catogeryList, String type) {
        this.context = context;
        this.catogeryList = catogeryList;
        this.type = type;
    }

    @NonNull
    @Override
    public CatogeryAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.row_catogery, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull CatogeryAdapter.MyViewHolder holder, final int position) {
        Glide.with(context).load(catogeryList.get(position).getIcon()).into(holder.catogery_img);
        holder.catogery_name.setText(catogeryList.get(position).getName());
        holder.rootview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (type.equalsIgnoreCase("my_work")) {

                } else if (type.equalsIgnoreCase("service")) {
                    context.startActivity(new Intent(context, SelectServiceActivity.class).
                            putExtra(Constants.SERVICE_ID, catogeryList.get(position).getService_id()).
                            putExtra(Constants.SUB_CAT_NAME, catogeryList.get(position).getName()));
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return catogeryList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        CardView rootview;
        private CircleImageView catogery_img;
        private TextView catogery_name;

        public MyViewHolder(View itemView) {
            super(itemView);
            catogery_img = itemView.findViewById(R.id.catogery_img);
            catogery_name = itemView.findViewById(R.id.catogery_name);
            rootview = itemView.findViewById(R.id.rootview);

        }
    }
}



