package com.open_source.adapter;

import android.content.Context;
import android.content.Intent;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.open_source.R;
import com.open_source.activity.ChatActivity;
import com.open_source.retrofitPack.Constants;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatInboxAdapter extends RecyclerView.Adapter<ChatInboxAdapter.MyViewHolder> {
    Context context;
    List<JSONObject> inboxList;

    public ChatInboxAdapter(Context context, List<JSONObject> inboxList) {
        this.context = context;
        this.inboxList = inboxList;
    }

    @NonNull
    @Override
    public ChatInboxAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.row_chat_inbox, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ChatInboxAdapter.MyViewHolder holder, final int position) {
        try {
            holder.chat_user_name.setText(inboxList.get(position).getString(Constants.TONAME));
            if (inboxList.get(position).getString(Constants.MESSAGE_TYPE).equalsIgnoreCase("text")) {
                holder.chat_user_msg.setText(inboxList.get(position).getString(Constants.LAST_MSG));
            } else if (inboxList.get(position).getString(Constants.MESSAGE_TYPE).equalsIgnoreCase("image")) {
                holder.chat_user_msg.setText("image");
            }
           // Log.e( "=======images",inboxList.get(position).getString(Constants.TO_PROFILE_IMAGE));
            Glide.with(context).load(inboxList.get(position).getString(Constants.TO_PROFILE_IMAGE)).
                      into(holder.chat_user_img);
            holder.root.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        context.startActivity(new Intent(context, ChatActivity.class).
                                putExtra(Constants.PROPERTY_ID, inboxList.get(position).getString(Constants.PROPERTY_ID)).
                                putExtra(Constants.USER_ID, inboxList.get(position).getString(Constants.TO_ID)).
                                putExtra(Constants.TONAME, inboxList.get(position).getString(Constants.TONAME)).
                                putExtra(Constants.CHAT_STATUS,inboxList.get(position).getString(Constants.CHAT_STATUS)).
                                putExtra(Constants.PROFILE_IMAGE,inboxList.get(position).getString(Constants.TO_PROFILE_IMAGE)));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }


    }

    @Override
    public int getItemCount() {
        return inboxList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        RelativeLayout root;
        private CircleImageView chat_user_img;
        private TextView chat_user_name, chat_user_msg;

        public MyViewHolder(View itemView) {
            super(itemView);
            chat_user_img = itemView.findViewById(R.id.chat_user_img);
            chat_user_name = itemView.findViewById(R.id.chat_user_name);
            chat_user_msg = itemView.findViewById(R.id.chat_user_msg);
            root = itemView.findViewById(R.id.rel_root);
        }
    }
}
