package com.open_source.adapter;

import android.content.Context;
import android.content.Intent;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.open_source.LiveFeeds.FeedLikeActivity;
import com.open_source.LiveFeeds.FolloReqActivity;
import com.open_source.R;
import com.open_source.ServiceProvider.MyPostBidderList;
import com.open_source.ServiceProvider.MyPostDashboard;
import com.open_source.ServiceProvider.ProjectDescription;
import com.open_source.ServiceProvider.SPJoBPostDashboard;
import com.open_source.ServiceProvider.SPServiceRequest;
import com.open_source.ServiceProvider.ServiceProviderHome;
import com.open_source.activity.Hs_Walk_Invitation;
import com.open_source.activity.LocationInfoActivity;
import com.open_source.activity.OfferConversationActivity;
import com.open_source.activity.PaymentConfirmation;
import com.open_source.activity.RenterReferenceApproval;
import com.open_source.activity.Renter_property_detail;
import com.open_source.activity.TourScheduleRequest;
import com.open_source.modal.RetroObject;
import com.open_source.retrofitPack.Constants;
import com.open_source.util.SharedPref;
import com.open_source.util.Utility;

import java.util.List;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.MyViewHolder> {

    private static final String TAG = NotificationAdapter.class.getSimpleName();
    Context context;
    List<RetroObject> notificationList;


    public NotificationAdapter(Context context, List<RetroObject> notificationList) {
        this.context = context;
        this.notificationList = notificationList;
    }

    @NonNull
    @Override
    public NotificationAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.row_saved_search_list, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull NotificationAdapter.MyViewHolder holder, final int position) {
        RequestOptions requestOptions = new RequestOptions();
        requestOptions.timeout(30000);
        holder.lay_time.setVisibility(View.VISIBLE);
        holder.ctv_title.setText(notificationList.get(position).getFirst_name() + " " + notificationList.get(position).getLast_name());
        if (notificationList.get(position).getNotification_type().equals("5") ||
                notificationList.get(position).getNotification_type().equals("7") ||
                notificationList.get(position).getNotification_type().equals("8") || notificationList.get(position).getNotification_type().equals("9") || notificationList.get(position).getNotification_type().equals("10")) {
            holder.ctv_sub_title.setText(notificationList.get(position).getMessage());
        } else if (notificationList.get(position).getNotification_type().equalsIgnoreCase("11")) {
            holder.ctv_title.setText(R.string.property_likes);
            holder.ctv_sub_title.setText(notificationList.get(position).getLike_count() + context.getString(R.string.user_like_property));
        } else {
            if (!notificationList.get(position).getBid_amount().isEmpty())
//                holder.ctv_sub_title.setText(notificationList.get(position).getMessage() + SharedPref.getSharedPreferences(context, Constants.CURRENCY_SYMBOL)+notificationList.get(position).getBid_amount());
                holder.ctv_sub_title.setText(notificationList.get(position).getMessage() + " $ "+notificationList.get(position).getBid_amount());
            else
                holder.ctv_sub_title.setText(notificationList.get(position).getMessage());
        }
        holder.txt_datetime.setText(notificationList.get(position).getDate() + "," + notificationList.get(position).getTime());
        if (!notificationList.get(position).getProperty_url().isEmpty() && !notificationList.get(position).getPropertyImg().isEmpty()) {
            Glide.with(context).load(notificationList.get(position).getProperty_url() + "" + notificationList.get(position).getPropertyImg()).
                    apply(requestOptions).into(holder.item_image);
        }
        holder.relview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (notificationList.get(position).getNotification_type().equals("5")) {
                    //Private property invitation=5
                    context.startActivity(new Intent(context, LocationInfoActivity.class).
                            putExtra(Constants.PROPERTY_ID, notificationList.get(position).getProperty_id()));
                } else if (notificationList.get(position).getNotification_type().equals("4")) {
                    //Biding property winner=4
                    if (!notificationList.get(position).getProperty_id().isEmpty()) {
                        if (!notificationList.get(position).getProperty_id().isEmpty()) {
                            context.startActivity(new Intent(context, PaymentConfirmation.class).
                                    putExtra(Constants.PROPERTY_ID, notificationList.get(position).getProperty_id()).putExtra(Constants.TYPE, "auction"));

                        }
                    } else {
                        Utility.ShowToastMessage(context, context.getString(R.string.property_not_exist));
                    }
                } else if (notificationList.get(position).getNotification_type().equals("7")) {
                    //rent_request=7
                    context.startActivity(new Intent(context, Renter_property_detail.class).
                            putExtra(Constants.STATUS, "request").
                            putExtra(Constants.PROPERTY_ID, notificationList.get(position).getProperty_id()).
                            putExtra(Constants.REQUEST_ID, notificationList.get(position).getRequest_id()));
                } else if (notificationList.get(position).getNotification_type().equals("8")) {
                    //Rent requested Approved=8
                    context.startActivity(new Intent(context, LocationInfoActivity.class).
                            putExtra(Constants.PROPERTY_ID, notificationList.get(position).getProperty_id()).putExtra(Constants.RENTAL_PAYMENT, "RentPay"));
                } else if (notificationList.get(position).getNotification_type().equals("9")) {
                    //Request Schedule Tour=9
                    context.startActivity(new Intent(context, TourScheduleRequest.class).putExtra(Constants.PROPERTY_ID,
                            notificationList.get(position).getProperty_id()).putExtra(Constants.REQUEST_DATE,
                            notificationList.get(position).getRequest_date()).
                            putExtra(Constants.REQUEST_TIME, notificationList.get(position).getRequest_time()).
                            putExtra(Constants.FIRST_NAME, notificationList.get(position).getFirst_name()).
                            putExtra(Constants.EMAIL, notificationList.get(position).getEmail()).
                            putExtra(Constants.MOBILE, notificationList.get(position).getMobile()));
                } else if (notificationList.get(position).getNotification_type().equals("10")) {
                    // NewProperty added in OS=10
                    context.startActivity(new Intent(context, LocationInfoActivity.class).putExtra(Constants.TYPE, "Rent").
                            putExtra(Constants.PROPERTY_ID, notificationList.get(position).getProperty_id()));
                } else if (notificationList.get(position).getNotification_type().equalsIgnoreCase("11")) {
                    //like new feed post=11 ,
                    context.startActivity(new Intent(context, FeedLikeActivity.class).
                            putExtra(Constants.PROPERTY_ID, notificationList.get(position).getProperty_id()));
                } else if (notificationList.get(position).getNotification_type().equals("18")) {
                    //post bid =18(user)
                    context.startActivity(new Intent(context, MyPostBidderList.class).
                            putExtra(Constants.POST_ID, notificationList.get(position).getProperty_id()));
                } else if (notificationList.get(position).getNotification_type().equals("15")) {
                    //award service by user to sp =15(sp)
                    context.startActivity(new Intent(context, SPJoBPostDashboard.class).
                            putExtra(Constants.KEY, notificationList.get(position).getNotification_type()));
                } else if (notificationList.get(position).getNotification_type().equals("16")) {
                    // accept award project by sp =16(user)
                    context.startActivity(new Intent(context, MyPostDashboard.class).
                            putExtra(Constants.KEY, notificationList.get(position).getNotification_type()));
                } else if (notificationList.get(position).getNotification_type().equals("14")) {
                    //milestone request  send sp to user =14(user)
                    context.startActivity(new Intent(context, ProjectDescription.class).
                            putExtra(Constants.TYPE, "user").
                            putExtra(Constants.KEY, "2").
                            putExtra(Constants.POST_ID, notificationList.get(position).getProperty_id()));
                } else if (notificationList.get(position).getNotification_type().equals("17")) {
                    // payment recieve by sp =17(sp)
                    context.startActivity(new Intent(context, ServiceProviderHome.class).
                            putExtra(Constants.KEY, notificationList.get(position).getNotification_type()));
                } else if (notificationList.get(position).getNotification_type().equals("19")) {
                    // payment request by sp to user =19(user job request)
                    context.startActivity(new Intent(context, SPServiceRequest.class).
                            putExtra(Constants.REQUEST_ID, notificationList.get(position).getProperty_id()));
                } else if (notificationList.get(position).getNotification_type().equals("20")) {
                    // payment request paid = 20(paid requested amount in direct hiring)
                    context.startActivity(new Intent(context, ServiceProviderHome.class).
                            putExtra(Constants.KEY, notificationList.get(position).getNotification_type()));
                } else if (notificationList.get(position).getNotification_type().equals("21")) {
                    context.startActivity(new Intent(context, RenterReferenceApproval.class).
                            putExtra(Constants.REQUEST_ID, notificationList.get(position).getRequest_id()));
                } else if (notificationList.get(position).getNotification_type().equals("22")) {
                    context.startActivity(new Intent(context, LocationInfoActivity.class).putExtra(Constants.TYPE, "Rent").
                            putExtra(Constants.PROPERTY_ID, notificationList.get(position).getProperty_id()));
                } else if (notificationList.get(position).getNotification_type().equals("24")) {
                    context.startActivity(new Intent(context, LocationInfoActivity.class).
                            putExtra(Constants.PROPERTY_ID, notificationList.get(position).getProperty_id()));
                } else if (notificationList.get(position).getNotification_type().equals("25")) {
                    context.startActivity(new Intent(context, FolloReqActivity.class));
                } else if (notificationList.get(position).getNotification_type().equals("32")) {
                    context.startActivity(new Intent(context, Hs_Walk_Invitation.class).
                            putExtra(Constants.Walk_Through_Id,notificationList.get(position).getWalkthrough_id()).
                            putExtra(Constants.PROPERTY_ID,notificationList.get(position).getProperty_id() ));
                }
                else if (notificationList.get(position).getNotification_type().equals("32")) {
                    context.startActivity(new Intent(context, Hs_Walk_Invitation.class).
                            putExtra(Constants.Walk_Through_Id,notificationList.get(position).getWalkthrough_id()).
                            putExtra(Constants.PROPERTY_ID,notificationList.get(position).getProperty_id() ));
                }
                else if (notificationList.get(position).getNotification_type().equals("34")) {
                    //walkthrough_agree_by_seller = 34
                    //context.startActivity(new Intent(context, Hs_Walk_Invitation.class));
                }
                else if (notificationList.get(position).getNotification_type().equals("36")) {
                   // walkthrough_agree_by_hs = 36
                   // context.startActivity(new Intent(context, Hs_Walk_Invitation.class));
                }
                else if (notificationList.get(position).getNotification_type().equals("37")|| notificationList.get(position).getNotification_type().equals("2")) {
                    context.startActivity(new Intent(context, OfferConversationActivity.class).
                            putExtra(Constants.PROPERTY_ID, notificationList.get(position).getProperty_id()).
                            putExtra(Constants.USER_ID,notificationList.get(position).getUser_id()));
                    // walkthrough_agree_by_hs = 36
                    // context.startActivity(new Intent(context, Hs_Walk_Invitation.class));
                }




                //Hs_Walk_Invitation
            }
        });

    }

    @Override
    public int getItemCount() {
        return notificationList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        ImageView item_image;
        TextView ctv_title, ctv_sub_title, txt_datetime;
        LinearLayout lay_time;
        RelativeLayout relview;

        public MyViewHolder(View itemView) {
            super(itemView);
            item_image = itemView.findViewById(R.id.item_image);
            ctv_title = itemView.findViewById(R.id.ctv_title);
            ctv_sub_title = itemView.findViewById(R.id.ctv_sub_title);
            txt_datetime = itemView.findViewById(R.id.txt_datetime);
            lay_time = itemView.findViewById(R.id.lay_time);
            relview = itemView.findViewById(R.id.relview);
        }
    }
}
