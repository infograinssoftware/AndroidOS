package com.open_source.adapter;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.open_source.R;
import com.open_source.ServiceProvider.AddWork;
import com.open_source.modal.Subcatogery;
import com.open_source.retrofitPack.Constants;
import com.open_source.util.SharedPref;

import java.util.ArrayList;


public class SubCatogeryAdapter extends RecyclerView.Adapter<SubCatogeryAdapter.MyViewHolder> {
    Context context;
    ArrayList<Subcatogery> List;

    public SubCatogeryAdapter(Context context, ArrayList<Subcatogery> List) {
        this.context = context;
        this.List = List;
    }

    @NonNull
    @Override
    public SubCatogeryAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.row_sub_catogery, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, final int position) {
        holder.name.setText(List.get(position).getName());
        holder.price.setText("$"+List.get(position).getPrice());
//        holder.price.setText(SharedPref.getSharedPreferences(context, Constants.CURRENCY_SYMBOL)+ " "+List.get(position).getPrice());
        holder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               AddWork.DeleteView(position);

            }
        });

        
    }
    @Override
    public int getItemCount() {
        return List.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView name,price;
        ImageView delete;

        public MyViewHolder(View itemView) {
            super(itemView);
            name=itemView.findViewById(R.id.name);
            price=itemView.findViewById(R.id.price);
            delete=itemView.findViewById(R.id.delete);
            

        }
    }
}
