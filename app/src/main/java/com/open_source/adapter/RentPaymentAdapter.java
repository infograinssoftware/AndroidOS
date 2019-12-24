package com.open_source.adapter;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.open_source.R;
import com.open_source.modal.RetroObject;

import java.util.List;

public class RentPaymentAdapter extends RecyclerView.Adapter<RentPaymentAdapter.MyViewHolder> {
    private static final String TAG = RentPaymentAdapter.class.getSimpleName();
    Context context;
    List<RetroObject> List;

    public RentPaymentAdapter(Context context, List<RetroObject> favouritesList) {
        this.context = context;
        this.List = favouritesList;
        //Log.e(TAG, String.valueOf(favouritesList.size()));
    }

    @NonNull
    @Override
    public RentPaymentAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new RentPaymentAdapter.MyViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.row_rwnt_payment, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RentPaymentAdapter.MyViewHolder holder, final int position) {
        holder.txt_date.setText(List.get(position).getDate());
        holder.txt_amount.setText(List.get(position).getAmount());

    }

    @Override
    public int getItemCount() {
        return 10;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView txt_date,txt_amount;


        public MyViewHolder(View itemView) {
            super(itemView);
            txt_date=itemView.findViewById(R.id.date);
            txt_amount=itemView.findViewById(R.id.txt_amount);
        }
    }
}

