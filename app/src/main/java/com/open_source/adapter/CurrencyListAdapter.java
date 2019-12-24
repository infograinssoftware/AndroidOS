package com.open_source.adapter;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.open_source.R;
import com.open_source.modal.RetroObject;

import java.util.ArrayList;

public class CurrencyListAdapter extends RecyclerView.Adapter<CurrencyListAdapter.MyViewHolder> implements Filterable {
    private Context context;
    public String selectedCurrency = "", currencySymbol = "", flagUrl = "";
    private RecyclerView mRecyclerView;
    private ArrayList<RetroObject> currencyList;
    private ArrayList<RetroObject> commonCurrencyList;

    public CurrencyListAdapter(Context context, ArrayList<RetroObject> currencyList, String flagUrl) {
        this.context = context;
        this.currencyList = currencyList;
        this.commonCurrencyList = currencyList;
        this.flagUrl = flagUrl;
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        mRecyclerView = recyclerView;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(context).inflate(R.layout.row_currency_list, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, final int position) {

        holder.txvCountryName.setText(currencyList.get(position).getName());
        holder.txvCurrencyName.setText(currencyList.get(position).getCurrency_name() +" ("+currencyList.get(position).getCurrency_code()+")");

        if (currencyList.get(position).getCurrency_code().equalsIgnoreCase(selectedCurrency)){
            holder.cbSelectedCurrency.setChecked(true);
        } else {
            holder.cbSelectedCurrency.setChecked(false);
        }

        Glide.with(context).load(flagUrl+currencyList.get(position).getCode().toLowerCase()+".png").into(holder.imgCountryFlag);

        holder.cbSelectedCurrency.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    selectedCurrency = currencyList.get(position).getCurrency_code();
                    Log.e(CurrencyListAdapter.class.getSimpleName(), "onCheckedChanged: " + selectedCurrency);
                    currencySymbol = currencyList.get(position).getCurrency_symbol();
                    mRecyclerView.post(new Runnable() {
                        @Override
                        public void run() {
                            notifyDataSetChanged();
                        }
                    });
                }
            }
        });
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return currencyList.size();
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String charString = charSequence.toString();
                if (charString.isEmpty()) {
                    currencyList = commonCurrencyList;
                } else {
                    ArrayList<RetroObject> filteredList = new ArrayList<>();
                    for (RetroObject row : commonCurrencyList) {

                        String countryName = row.getName().toLowerCase();
                        String countryCurrency = row.getCurrency_name().toLowerCase();
                        String countryCode = row.getCurrency_code().toLowerCase();

                        if (countryCurrency.contains(charString.toLowerCase()) || countryCurrency.contains(charSequence)) {
                            filteredList.add(row);
                        } else if (countryCode.contains(charString.toLowerCase()) || countryCode.contains(charSequence)) {
                            filteredList.add(row);
                        } else if (countryName.contains(charString.toLowerCase()) || countryName.contains(charSequence)) {
                            filteredList.add(row);
                        }
                    }
                    currencyList = filteredList;
                }
                FilterResults filterResults = new FilterResults();
                filterResults.values = currencyList;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                currencyList = (ArrayList<RetroObject>) results.values;
                notifyDataSetChanged();
            }
        };
    }


    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView txvCurrencyName, txvCountryName;
        RadioButton cbSelectedCurrency;
        ImageView imgCountryFlag;

        public MyViewHolder(View itemView) {
            super(itemView);
            imgCountryFlag = itemView.findViewById(R.id.imgCountryFlag);
            txvCountryName = itemView.findViewById(R.id.txvCountryName);
            txvCurrencyName = itemView.findViewById(R.id.txvCurrencyName);
            cbSelectedCurrency = itemView.findViewById(R.id.rbSelectedCurrency);
        }
    }
}
