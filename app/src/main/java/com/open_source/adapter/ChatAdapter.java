package com.open_source.adapter;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.cardview.widget.CardView;

import com.bumptech.glide.Glide;
import com.open_source.R;
import com.open_source.activity.ChatImageview;
import com.open_source.activity.LocationInfoActivity;
import com.open_source.modal.ChatMsgModel;
import com.open_source.retrofitPack.Constants;
import com.open_source.util.CircleImageView;
import com.open_source.util.SharedPref;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by and-02 on 25/6/18.
 */

public class ChatAdapter extends ArrayAdapter<ChatMsgModel> {
    Context context;
    List<ChatMsgModel> chatList;
    private String TAG = ChatAdapter.class.getSimpleName();
    private LayoutInflater inflater;

    public ChatAdapter(Context context, ArrayList<ChatMsgModel> array_chat) {
        super(context, 0);
        this.context = context;
        this.chatList = array_chat;
        Log.e(TAG, "============: " + chatList.size());
        this.inflater = LayoutInflater.from(context);
    }

   /* @Override
    public void add(ChatMsgModel object) {
        super.add(object);
    }*/

    public void clear() {
        chatList.clear();
    }

    @Override
    public int getCount() {
        return this.chatList.size();
    }


    public ChatMsgModel getItem(int index) {
        return this.chatList.get(index);
    }

    public void setItems(ArrayList<ChatMsgModel> myList) {
        this.chatList.clear();
        this.chatList.addAll(myList);
        notifyDataSetChanged();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        try {
            final ChatMsgModel listchat = chatList.get(position);
            try {
                if (chatList.size() > 0) {
                    String value=chatList.get(position).getContent_type();
                    if (value!= null) {
                        if (chatList.get(position).getContent_type().equalsIgnoreCase("shared")) {
                            // Log.e(TAG, "=======: " + chatList.get(position).getFromid());
                            //Log.e(TAG, "=======: " + SharedPref.getSharedPreferences(context, Constants.USER_ID));
                            if (chatList.get(position).getFromid().equalsIgnoreCase(SharedPref.getSharedPreferences(context, Constants.USER_ID))) {
                                convertView = inflater.inflate(R.layout.layout_shared_send, null);
                                new ViewHolder(convertView);
                                final ViewHolder holder = (ViewHolder) convertView.getTag();
                                final String str[] = listchat.getMassage().split(",");
                                Glide.with(context).load(str[1]).into(holder.shared_user_image);
                                Glide.with(context).load(str[2]).into(holder.shared_image);
                                holder.shared_name.setText(str[0]);
                                String a[] = listchat.getTime().split(" ");
                                holder.chat_time.setText(a[1] + " " + a[2]);
                                holder.shared_address.setText(str[4]);
                                holder.shared_type.setText(str[5]);
                                holder.lay_shared.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        if (!str[3].isEmpty())
                                            context.startActivity(new Intent(context, LocationInfoActivity.class).putExtra(Constants.PROPERTY_ID, str[3]));
                                    }
                                });
                            } else {
                                convertView = inflater.inflate(R.layout.layout_shared_recieve, null);
                                new ViewHolder(convertView);
                                final ViewHolder holder = (ViewHolder) convertView.getTag();
                                final String str[] = listchat.getMassage().split(",");
                                Glide.with(context).load(str[1]).into(holder.shared_user_image);
                                Glide.with(context).load(str[2]).into(holder.shared_image);
                                holder.shared_name.setText(str[0]);
                                String a[] = listchat.getTime().split(" ");
                                holder.chat_time.setText(a[1] + " " + a[2]);
                                holder.shared_address.setText(str[4]);
                                holder.shared_type.setText(str[5]);
                                holder.lay_shared.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        if (!str[3].isEmpty())
                                            context.startActivity(new Intent(context, LocationInfoActivity.class).putExtra(Constants.PROPERTY_ID, str[3]));
                                    }
                                });
                            }

                        }
                        else {
                            if (chatList.get(position).getMsg_type().equalsIgnoreCase("text")) {
                                if (chatList.get(position).getFromid().equalsIgnoreCase(SharedPref.getSharedPreferences(context, Constants.USER_ID))) {
                                    convertView = inflater.inflate(R.layout.layout_send, null);
                                    new ViewHolder(convertView);
                                    final ViewHolder holder = (ViewHolder) convertView.getTag();
                                    holder.chat_msg.setText(listchat.getMassage());
                                    String a[] = listchat.getTime().split(" ");
                                    holder.chat_time.setText(a[1] + " " + a[2]);
                                } else {
                                    convertView = inflater.inflate(R.layout.layout_recieve, null);
                                    new ViewHolder(convertView);
                                    final ViewHolder holder = (ViewHolder) convertView.getTag();
                                    holder.chat_msg.setText(listchat.getMassage());
                                    String a[] = listchat.getTime().split(" ");
                                    holder.chat_time.setText(a[1] + " " + a[2]);
                                }

                            } else if (chatList.get(position).getMsg_type().equalsIgnoreCase("image")) {
                                if (chatList.get(position).getFromid().equalsIgnoreCase(SharedPref.getSharedPreferences(context, Constants.USER_ID))) {
                                    convertView = inflater.inflate(R.layout.layout_send_image, null);
                                    new ViewHolder(convertView);
                                    final ViewHolder holder = (ViewHolder) convertView.getTag();
                                    Glide.with(context).load(listchat.getMassage()).
                                            into(holder.chat_img);
                                    String a[] = listchat.getTime().split(" ");
                                    holder.imgtime.setText(a[1] + " " + a[2]);
                                    holder.card_send.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            context.startActivity(new Intent(context, ChatImageview.class).putExtra(Constants.IMAGE, listchat.getMassage()));
                                        }
                                    });

                                } else {
                                    convertView = inflater.inflate(R.layout.layout_recieve_image, null);
                                    new ViewHolder(convertView);
                                    final ViewHolder holder = (ViewHolder) convertView.getTag();
                                    Glide.with(context).load(listchat.getMassage()).
                                            into(holder.chat_img);
                                    String a[] = listchat.getTime().split(" ");
                                    holder.imgtime.setText(a[1] + " " + a[2]);
                                    holder.card_recieve.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            context.startActivity(new Intent(context, ChatImageview.class).putExtra(Constants.IMAGE, listchat.getMassage()));
                                        }
                                    });
                                }
                            }
                        }
                    }
                    else {

                    }
                }


            } catch (Exception e) {
                e.printStackTrace();
            }
        } catch (ArrayIndexOutOfBoundsException e) {

        }
        return convertView;
    }


    private class ViewHolder {
        public TextView chat_msg, chat_time, imgtime, shared_name, shared_address, shared_type;
        ImageView chat_img, shared_image;
        CircleImageView shared_user_image;
        CardView card_recieve, card_send;
        LinearLayout lay_shared;

        public ViewHolder(View convertView) {
            chat_msg = convertView.findViewById(R.id.msgr);
            chat_time = convertView.findViewById(R.id.time);
            chat_img = convertView.findViewById(R.id.chatimg);
            imgtime = convertView.findViewById(R.id.imgtime);
            card_recieve = convertView.findViewById(R.id.card_recieve);
            card_send = convertView.findViewById(R.id.card_send);
            shared_name = convertView.findViewById(R.id.shared_name);
            shared_image = convertView.findViewById(R.id.shared_image);
            shared_user_image = convertView.findViewById(R.id.shared_user_image);
            lay_shared = convertView.findViewById(R.id.lay_shared);
            shared_address = convertView.findViewById(R.id.shared_address);
            shared_type = convertView.findViewById(R.id.shared_type);
            convertView.setTag(this);
        }
    }
}
