package com.open_source.adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.open_source.R;
import com.open_source.ServiceProvider.UserSPWorkDetail;
import com.open_source.activity.OfferConversationActivity;
import com.open_source.modal.RetroObject;
import com.open_source.retrofitPack.Constants;
import com.open_source.util.Utility;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by and-02 on 30/6/18.
 */
public class BidListAdapter extends RecyclerView.Adapter<BidListAdapter.MyViewHolder> {
    private static final int REQUEST_PHONE_CALL = 1;
    Context context;
    List<RetroObject> bidList;
    String type, service_id;

    public BidListAdapter(Context context, List<RetroObject> bidList, String type, String service_id) {
        this.context = context;
        this.bidList = bidList;
        this.type = type;
        this.service_id = service_id;
    }

    @NonNull
    @Override
    public BidListAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new BidListAdapter.MyViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.row_bid, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull final BidListAdapter.MyViewHolder holder, final int position) {
        RequestOptions requestOptions = new RequestOptions();
        requestOptions.placeholder(R.drawable.defaultuser);
        requestOptions.error(R.drawable.defaultuser);
        if (type.equals("bid")) {
            holder.lay_contact.setVisibility(View.GONE);
            holder.user_name.setText(bidList.get(position).getUser_name());
            holder.ct_bid_amount.setText(bidList.get(position).getBid_amount());
            Glide.with(context).load(bidList.get(position).getUrl() + "" + bidList.get(position).getProfileImage()).apply(requestOptions).
                    into(holder.user_img);
            holder.ct_user_call.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    context.startActivity(new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + holder.ct_user_call.getText().toString())));
                    /*if (ContextCompat.checkSelfPermission(context, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions((Activity) context, new String[]{Manifest.permission.CALL_PHONE}, REQUEST_PHONE_CALL);
                    } else {
                        context.startActivity(new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + holder.ct_user_call.getText().toString())));
                    }*/
                }
            });
        } else if (type.equals("offer")) {
            holder.ct_bid_title.setText(R.string.offer_amount);
            holder.lay_contact.setVisibility(View.GONE);
            holder.user_name.setText(bidList.get(position).getFirst_name() + " " + bidList.get(position).getLast_name());
            holder.ct_bid_amount.setText(bidList.get(position).getAmount());
            Glide.with(context).load(bidList.get(position).getUrl() + "" + bidList.get(position).getProfileImage()).apply(requestOptions).
                    into(holder.user_img);
            holder.lay_root.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    context.startActivity(new Intent(context,OfferConversationActivity.class).
                            putExtra(Constants.PROPERTY_ID, bidList.get(position).getProperty_id()).
                            putExtra(Constants.USER_ID,bidList.get(position).getUser_id()));
                }
            });


        } else {
            holder.user_name.setText(bidList.get(position).getFirst_name() + " " + bidList.get(position).getLast_name());
            if (bidList.get(position).getMobileNumber().length()==10)
            holder.ct_user_call.setText(Utility.formatNumbersAsCode(bidList.get(position).getMobileNumber()));
            else
                holder.ct_user_call.setText(bidList.get(position).getMobileNumber());
            holder.lay_bid.setVisibility(View.GONE);
            // holder.lay_distance.setVisibility(View.VISIBLE);
            // holder.ct_location.setText(bidList.get(position).getDistance_sql());
            Glide.with(context).load(bidList.get(position).getProfileImage()).apply(requestOptions).into(holder.user_img);
            holder.ct_user_call.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    context.startActivity(new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + holder.ct_user_call.getText().toString())));
                    /*if (ContextCompat.checkSelfPermission(context, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions((Activity) context, new String[]{Manifest.permission.CALL_PHONE}, REQUEST_PHONE_CALL);
                    } else {
                        context.startActivity(new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + holder.ct_user_call.getText().toString())));
                    }*/
                }
            });
            holder.lay_root.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    context.startActivity(new Intent(context, UserSPWorkDetail.class).
                            putExtra(Constants.USER_ID, bidList.get(position).getUser_id()).
                            putExtra(Constants.SERVICE_ID, service_id).putExtra(Constants.TYPE, type).putExtra(Constants.NAME, bidList.get(position).getFirst_name() + " " + bidList.get(position).getLast_name()));
                }
            });
        }

    }

    @Override
    public int getItemCount() {
        return bidList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        LinearLayout lay_distance, lay_bid, lay_contact;
        RelativeLayout lay_root;
        private CircleImageView user_img;
        private TextView user_name, ct_bid_amount, ct_user_call, ct_location, ct_bid_title;

        public MyViewHolder(View itemView) {
            super(itemView);
            user_img = itemView.findViewById(R.id.user_img);
            user_name = itemView.findViewById(R.id.user_name);
            ct_bid_amount = itemView.findViewById(R.id.ct_bid_amount);
            ct_user_call = itemView.findViewById(R.id.ct_user_call);
            lay_distance = itemView.findViewById(R.id.lay_distance);
            lay_bid = itemView.findViewById(R.id.lay_bid);
            lay_contact = itemView.findViewById(R.id.contact);
            ct_location = itemView.findViewById(R.id.ct_location);
            lay_root = itemView.findViewById(R.id.lay_root);
            ct_bid_title = itemView.findViewById(R.id.ct_bid_title);
        }
    }


}

