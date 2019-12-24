package com.open_source.activity;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;

import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.open_source.LiveFeeds.NewsFeedsActivity;
import com.open_source.R;
import com.open_source.ServiceProvider.MyPostDashboard;
import com.open_source.ServiceProvider.SPRequest;
import com.open_source.ServiceProvider.SPSignUpActivity;
import com.open_source.ServiceProvider.UserService;
import com.open_source.modal.RetrofitUserData;
import com.open_source.retrofitPack.Constants;
import com.open_source.retrofitPack.NotificationConstant;
import com.open_source.retrofitPack.RetrofitClient;
import com.open_source.util.SharedPref;
import com.open_source.util.Utility;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DashBoardActivity extends BaseActivity implements View.OnClickListener {
    private static final String TAG = DashBoardActivity.class.getSimpleName();
    int chat = 0, offerMyProperty = 0, offer = 0, propOwner = 0, renter = 0, postService = 0, liveFeed = 0, service = 0;
    private Context context;
    private CardView card_post_list, card_payment_list, card_chat_box, card_search_history, card_bid, card_buy_property,
            card_rent, card_renter_property, card_request, card_job, card_offer, card_hs, card_sp, card_newsfeed,
            card_service,card_loan,card_FAQ;
    private Toolbar toolbar;
    private TextView toolbar_title, txt_chat, txt_offer, txt_bid, txt_owner, txt_renter, txt_my_post, txt_news_feed, txt_service;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        context = this;
        init();

        txt_chat = (TextView) findViewById(R.id.txt_chat);
        txt_offer = (TextView) findViewById(R.id.txt_offer);
        txt_bid = (TextView) findViewById(R.id.txt_bid);
        txt_owner = (TextView) findViewById(R.id.txt_owner);
        txt_renter = (TextView) findViewById(R.id.txt_renter);
        txt_my_post = (TextView) findViewById(R.id.txt_my_post);
        txt_news_feed = (TextView) findViewById(R.id.txt_news_feed);
        txt_service = (TextView) findViewById(R.id.txt_service);

    }

    public void init() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar_title = (TextView) findViewById(R.id.toolbar_title);
        card_post_list = (CardView) findViewById(R.id.card_post_list);
        card_payment_list = (CardView) findViewById(R.id.card_payment_list);
        card_chat_box = (CardView) findViewById(R.id.card_chat_box);
        card_search_history = (CardView) findViewById(R.id.card_search_history);
        card_offer = (CardView) findViewById(R.id.card_offer);
        card_hs = (CardView) findViewById(R.id.card_hs);
        card_sp = (CardView) findViewById(R.id.card_sp);
        card_newsfeed = (CardView) findViewById(R.id.card_news_feeds);
        card_service = (CardView) findViewById(R.id.card_find_service);
        card_bid = (CardView) findViewById(R.id.card_bid);
        card_buy_property = (CardView) findViewById(R.id.card_buy_property);
        card_rent = (CardView) findViewById(R.id.card_rent);
        card_renter_property = (CardView) findViewById(R.id.card_renter_property);
        card_request = (CardView) findViewById(R.id.card_request);
        card_job = (CardView) findViewById(R.id.card_job);
        card_loan=(CardView) findViewById(R.id.card_loan);
        card_FAQ=(CardView) findViewById(R.id.card_FAQ);
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.back_arrow);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        toolbar_title.setText(R.string.dashborad);
        card_post_list.setOnClickListener(this);
        card_payment_list.setOnClickListener(this);
        card_chat_box.setOnClickListener(this);
        card_search_history.setOnClickListener(this);
        card_bid.setOnClickListener(this);
        card_buy_property.setOnClickListener(this);
        card_rent.setOnClickListener(this);
        card_renter_property.setOnClickListener(this);
        card_request.setOnClickListener(this);
        card_job.setOnClickListener(this);
        card_offer.setOnClickListener(this);
        card_hs.setOnClickListener(this);
        card_sp.setOnClickListener(this);
        card_service.setOnClickListener(this);
        card_newsfeed.setOnClickListener(this);
        card_loan.setOnClickListener(this);
        card_FAQ.setOnClickListener(this);
        Fun_Unread_Noti();
    }

    private void Fun_Unread_Noti() {
        if (Utility.isConnectingToInternet(context)) {
            RetrofitClient.getAPIService().UnreadNoti(SharedPref.getSharedPreferences(context, Constants.TOKEN)).enqueue(new Callback<RetrofitUserData>() {
                @Override
                public void onResponse(Call<RetrofitUserData> call, Response<RetrofitUserData> response) {
                    try {
                        if (response.body().getStatus() == 200) {
                            chat = 0; offerMyProperty = 0; offer = 0; propOwner = 0; renter = 0;
                            postService = 0; liveFeed = 0; service = 0;
                            for (int i = 0; i < response.body().getUserData().getNotification_list().size(); i++) {
                                switch (response.body().getUserData().getNotification_list().get(i).getNoti_type()) {
                                    case NotificationConstant.GetChat:
                                        chat += response.body().getUserData().getNotification_list().get(i).getNotitype_count();
                                        break;

                                    case NotificationConstant.LikeNotification:
                                        liveFeed += response.body().getUserData().getNotification_list().get(i).getNotitype_count();
                                        break;

                                    case NotificationConstant.CommentNotification:
                                        liveFeed += response.body().getUserData().getNotification_list().get(i).getNotitype_count();
                                        break;

                                    case NotificationConstant.FollowRequest:
                                        liveFeed += response.body().getUserData().getNotification_list().get(i).getNotitype_count();
                                        break;

                                    case NotificationConstant.FollowRequestAccept:
                                        liveFeed += response.body().getUserData().getNotification_list().get(i).getNotitype_count();
                                        break;

                                    case NotificationConstant.BidOnProject:
                                        postService += response.body().getUserData().getNotification_list().get(i).getNotitype_count();
                                        service += response.body().getUserData().getNotification_list().get(i).getNotitype_count();
                                        break;

                                    case NotificationConstant.MilestoneRequest:
                                        postService += response.body().getUserData().getNotification_list().get(i).getNotitype_count();
                                        service += response.body().getUserData().getNotification_list().get(i).getNotitype_count();
                                        break;

                                    case NotificationConstant.RentApproved:
                                        renter += response.body().getUserData().getNotification_list().get(i).getNotitype_count();
                                        break;

                                    case NotificationConstant.RentDisclaimer:
                                        propOwner += response.body().getUserData().getNotification_list().get(i).getNotitype_count();
                                        break;

                                    case NotificationConstant.RentConfirmation:
                                        renter += response.body().getUserData().getNotification_list().get(i).getNotitype_count();
                                        break;

                                    case NotificationConstant.RentDisclaimerApproved:
                                        renter += response.body().getUserData().getNotification_list().get(i).getNotitype_count();
                                        break;

                                    case NotificationConstant.ReplyOfferByBuyerSeller:
                                        offer += response.body().getUserData().getNotification_list().get(i).getNotitype_count();
                                        break;

                                    case NotificationConstant.OfferAcceptByBuyerSeller:
                                        offerMyProperty += response.body().getUserData().getNotification_list().get(i).getNotitype_count();
                                        break;
                                    case NotificationConstant.Offer:
                                        offerMyProperty += response.body().getUserData().getNotification_list().get(i).getNotitype_count();
                                        break;

                                }
                                SetValue();
                            }
                        } else if (response.body().getStatus() == 401) {
                            SharedPref.clearPreference(context);
                            startActivity(new Intent(context, WelcomeActivity.class)
                                    .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK));
                            finish();
                        } else {
                            Utility.ShowToastMessage(context, response.body().getMessage());
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(Call<RetrofitUserData> call, Throwable t) {

                }
            });
        } else {

        }

    }

    private void SetValue() {
        if (chat > 0) {
            txt_chat.setVisibility(View.VISIBLE);
            txt_chat.setText(String.valueOf(chat));
        }
        if (offerMyProperty > 0) {
            txt_offer.setVisibility(View.VISIBLE);
            txt_offer.setText(String.valueOf(offerMyProperty));
        }
        if (offer > 0) {
            txt_bid.setVisibility(View.VISIBLE);
            txt_bid.setText(String.valueOf(offer));
        }
        if (propOwner > 0) {
            txt_owner.setVisibility(View.VISIBLE);
            txt_owner.setText(String.valueOf(propOwner));
        }
        if (renter > 0) {
            txt_renter.setVisibility(View.VISIBLE);
            txt_renter.setText(String.valueOf(renter));
        }
        if (postService > 0) {
            txt_my_post.setVisibility(View.VISIBLE);
            txt_my_post.setText(String.valueOf(postService));
        }
        if (liveFeed > 0) {
            txt_news_feed.setVisibility(View.VISIBLE);
            txt_news_feed.setText(String.valueOf(liveFeed));
        }
        if (service > 0) {
            txt_service.setVisibility(View.VISIBLE);
            txt_service.setText(String.valueOf(service));
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.card_post_list:
                ((LinearLayout) findViewById(R.id.lay_plan)).setVisibility(View.GONE);
                ((LinearLayout) findViewById(R.id.lay_plan_selcted)).setVisibility(View.VISIBLE);
                ((LinearLayout) findViewById(R.id.lay_payment)).setVisibility(View.VISIBLE);
                ((LinearLayout) findViewById(R.id.lay_payment_Select)).setVisibility(View.GONE);
                ((LinearLayout) findViewById(R.id.lay_chat)).setVisibility(View.VISIBLE);
                ((LinearLayout) findViewById(R.id.lay_chat_select)).setVisibility(View.GONE);
                ((LinearLayout) findViewById(R.id.lay_search)).setVisibility(View.VISIBLE);
                ((LinearLayout) findViewById(R.id.lay_search_select)).setVisibility(View.GONE);
                ((LinearLayout) findViewById(R.id.lay_bid)).setVisibility(View.VISIBLE);
                ((LinearLayout) findViewById(R.id.lay_bid_select)).setVisibility(View.GONE);
                ((LinearLayout) findViewById(R.id.lay_buy_property)).setVisibility(View.VISIBLE);
                ((LinearLayout) findViewById(R.id.lay_buy_property_select)).setVisibility(View.GONE);
                ((LinearLayout) findViewById(R.id.lay_rented)).setVisibility(View.VISIBLE);
                ((LinearLayout) findViewById(R.id.lay_rented_select)).setVisibility(View.GONE);
                ((LinearLayout) findViewById(R.id.lay_renter_property)).setVisibility(View.VISIBLE);
                ((LinearLayout) findViewById(R.id.lay_renter_property_select)).setVisibility(View.GONE);
                ((LinearLayout) findViewById(R.id.lay_request)).setVisibility(View.VISIBLE);
                ((LinearLayout) findViewById(R.id.lay_request_selcted)).setVisibility(View.GONE);
                ((LinearLayout) findViewById(R.id.lay_job)).setVisibility(View.VISIBLE);
                ((LinearLayout) findViewById(R.id.lay_job_selcted)).setVisibility(View.GONE);
                ((LinearLayout) findViewById(R.id.lay_news_feeds)).setVisibility(View.VISIBLE);
                ((LinearLayout) findViewById(R.id.lay_news_feeds_selcted)).setVisibility(View.GONE);
                ((LinearLayout) findViewById(R.id.lay_find_service)).setVisibility(View.VISIBLE);
                ((LinearLayout) findViewById(R.id.lay_find_service_select)).setVisibility(View.GONE);

                ((LinearLayout) findViewById(R.id.lay_sp)).setVisibility(View.VISIBLE);
                ((LinearLayout) findViewById(R.id.lay_sp_select)).setVisibility(View.GONE);
                ((LinearLayout) findViewById(R.id.lay_offer)).setVisibility(View.VISIBLE);
                ((LinearLayout) findViewById(R.id.lay_offer_select)).setVisibility(View.GONE);
                ((LinearLayout) findViewById(R.id.lay_hs)).setVisibility(View.VISIBLE);
                ((LinearLayout) findViewById(R.id.lay_hs_selcted)).setVisibility(View.GONE);
                ((LinearLayout) findViewById(R.id.lay_loan_select)).setVisibility(View.GONE);
                ((LinearLayout) findViewById(R.id.lay_loan)).setVisibility(View.VISIBLE);
                ((LinearLayout) findViewById(R.id.lay_faq)).setVisibility(View.VISIBLE);
                ((LinearLayout) findViewById(R.id.lay_faq_select)).setVisibility(View.GONE);
                /* startActivity(new Intent(context, PostMakeOfferListActivity.class));*/
                startActivity(new Intent(context, FavouritesActivity.class));
                break;
            case R.id.card_payment_list:
                ((LinearLayout) findViewById(R.id.lay_payment)).setVisibility(View.GONE);
                ((LinearLayout) findViewById(R.id.lay_payment_Select)).setVisibility(View.VISIBLE);
                ((LinearLayout) findViewById(R.id.lay_plan)).setVisibility(View.VISIBLE);
                ((LinearLayout) findViewById(R.id.lay_plan_selcted)).setVisibility(View.GONE);
                ((LinearLayout) findViewById(R.id.lay_chat)).setVisibility(View.VISIBLE);
                ((LinearLayout) findViewById(R.id.lay_chat_select)).setVisibility(View.GONE);
                ((LinearLayout) findViewById(R.id.lay_search)).setVisibility(View.VISIBLE);
                ((LinearLayout) findViewById(R.id.lay_search_select)).setVisibility(View.GONE);
                ((LinearLayout) findViewById(R.id.lay_bid)).setVisibility(View.VISIBLE);
                ((LinearLayout) findViewById(R.id.lay_bid_select)).setVisibility(View.GONE);
                ((LinearLayout) findViewById(R.id.lay_buy_property)).setVisibility(View.VISIBLE);
                ((LinearLayout) findViewById(R.id.lay_buy_property_select)).setVisibility(View.GONE);
                ((LinearLayout) findViewById(R.id.lay_rented)).setVisibility(View.VISIBLE);
                ((LinearLayout) findViewById(R.id.lay_rented_select)).setVisibility(View.GONE);
                ((LinearLayout) findViewById(R.id.lay_renter_property)).setVisibility(View.VISIBLE);
                ((LinearLayout) findViewById(R.id.lay_renter_property_select)).setVisibility(View.GONE);
                ((LinearLayout) findViewById(R.id.lay_request)).setVisibility(View.VISIBLE);
                ((LinearLayout) findViewById(R.id.lay_request_selcted)).setVisibility(View.GONE);
                ((LinearLayout) findViewById(R.id.lay_job)).setVisibility(View.VISIBLE);
                ((LinearLayout) findViewById(R.id.lay_job_selcted)).setVisibility(View.GONE);
                ((LinearLayout) findViewById(R.id.lay_news_feeds)).setVisibility(View.VISIBLE);
                ((LinearLayout) findViewById(R.id.lay_news_feeds_selcted)).setVisibility(View.GONE);
                ((LinearLayout) findViewById(R.id.lay_find_service)).setVisibility(View.VISIBLE);
                ((LinearLayout) findViewById(R.id.lay_find_service_select)).setVisibility(View.GONE);
                ((LinearLayout) findViewById(R.id.lay_sp)).setVisibility(View.VISIBLE);
                ((LinearLayout) findViewById(R.id.lay_sp_select)).setVisibility(View.GONE);
                ((LinearLayout) findViewById(R.id.lay_offer)).setVisibility(View.VISIBLE);
                ((LinearLayout) findViewById(R.id.lay_offer_select)).setVisibility(View.GONE);
                ((LinearLayout) findViewById(R.id.lay_hs)).setVisibility(View.VISIBLE);
                ((LinearLayout) findViewById(R.id.lay_hs_selcted)).setVisibility(View.GONE);
                ((LinearLayout) findViewById(R.id.lay_loan_select)).setVisibility(View.GONE);
                ((LinearLayout) findViewById(R.id.lay_loan)).setVisibility(View.VISIBLE);
                ((LinearLayout) findViewById(R.id.lay_faq)).setVisibility(View.VISIBLE);
                ((LinearLayout) findViewById(R.id.lay_faq_select)).setVisibility(View.GONE);
                startActivity(new Intent(context, BuyHistory.class));
                break;
            case R.id.card_chat_box:
                ((LinearLayout) findViewById(R.id.lay_chat)).setVisibility(View.GONE);
                ((LinearLayout) findViewById(R.id.lay_chat_select)).setVisibility(View.VISIBLE);
                ((LinearLayout) findViewById(R.id.lay_payment)).setVisibility(View.VISIBLE);
                ((LinearLayout) findViewById(R.id.lay_payment_Select)).setVisibility(View.GONE);
                ((LinearLayout) findViewById(R.id.lay_plan)).setVisibility(View.VISIBLE);
                ((LinearLayout) findViewById(R.id.lay_plan_selcted)).setVisibility(View.GONE);
                ((LinearLayout) findViewById(R.id.lay_search)).setVisibility(View.VISIBLE);
                ((LinearLayout) findViewById(R.id.lay_search_select)).setVisibility(View.GONE);
                ((LinearLayout) findViewById(R.id.lay_bid)).setVisibility(View.VISIBLE);
                ((LinearLayout) findViewById(R.id.lay_bid_select)).setVisibility(View.GONE);
                ((LinearLayout) findViewById(R.id.lay_buy_property)).setVisibility(View.VISIBLE);
                ((LinearLayout) findViewById(R.id.lay_buy_property_select)).setVisibility(View.GONE);
                ((LinearLayout) findViewById(R.id.lay_rented)).setVisibility(View.VISIBLE);
                ((LinearLayout) findViewById(R.id.lay_rented_select)).setVisibility(View.GONE);
                ((LinearLayout) findViewById(R.id.lay_renter_property)).setVisibility(View.VISIBLE);
                ((LinearLayout) findViewById(R.id.lay_renter_property_select)).setVisibility(View.GONE);
                ((LinearLayout) findViewById(R.id.lay_request)).setVisibility(View.VISIBLE);
                ((LinearLayout) findViewById(R.id.lay_request_selcted)).setVisibility(View.GONE);
                ((LinearLayout) findViewById(R.id.lay_job)).setVisibility(View.VISIBLE);
                ((LinearLayout) findViewById(R.id.lay_job_selcted)).setVisibility(View.GONE);
                ((LinearLayout) findViewById(R.id.lay_news_feeds)).setVisibility(View.VISIBLE);
                ((LinearLayout) findViewById(R.id.lay_news_feeds_selcted)).setVisibility(View.GONE);
                ((LinearLayout) findViewById(R.id.lay_find_service)).setVisibility(View.VISIBLE);
                ((LinearLayout) findViewById(R.id.lay_find_service_select)).setVisibility(View.GONE);
                ((LinearLayout) findViewById(R.id.lay_sp)).setVisibility(View.VISIBLE);
                ((LinearLayout) findViewById(R.id.lay_sp_select)).setVisibility(View.GONE);
                ((LinearLayout) findViewById(R.id.lay_offer)).setVisibility(View.VISIBLE);
                ((LinearLayout) findViewById(R.id.lay_offer_select)).setVisibility(View.GONE);
                ((LinearLayout) findViewById(R.id.lay_hs)).setVisibility(View.VISIBLE);
                ((LinearLayout) findViewById(R.id.lay_hs_selcted)).setVisibility(View.GONE);
                ((LinearLayout) findViewById(R.id.lay_loan_select)).setVisibility(View.GONE);
                ((LinearLayout) findViewById(R.id.lay_loan)).setVisibility(View.VISIBLE);
                ((LinearLayout) findViewById(R.id.lay_faq)).setVisibility(View.VISIBLE);
                ((LinearLayout) findViewById(R.id.lay_faq_select)).setVisibility(View.GONE);
                txt_chat.setVisibility(View.GONE);
                if (chat > 0)
                    Utility.ReadNoti(context, NotificationConstant.GetChat);
                startActivity(new Intent(context, InboxListActivity.class));
                break;
            case R.id.card_search_history:
                ((LinearLayout) findViewById(R.id.lay_search)).setVisibility(View.GONE);
                ((LinearLayout) findViewById(R.id.lay_search_select)).setVisibility(View.VISIBLE);
                ((LinearLayout) findViewById(R.id.lay_payment)).setVisibility(View.VISIBLE);
                ((LinearLayout) findViewById(R.id.lay_payment_Select)).setVisibility(View.GONE);
                ((LinearLayout) findViewById(R.id.lay_plan)).setVisibility(View.VISIBLE);
                ((LinearLayout) findViewById(R.id.lay_plan_selcted)).setVisibility(View.GONE);
                ((LinearLayout) findViewById(R.id.lay_chat)).setVisibility(View.VISIBLE);
                ((LinearLayout) findViewById(R.id.lay_chat_select)).setVisibility(View.GONE);
                ((LinearLayout) findViewById(R.id.lay_bid)).setVisibility(View.VISIBLE);
                ((LinearLayout) findViewById(R.id.lay_bid_select)).setVisibility(View.GONE);
                ((LinearLayout) findViewById(R.id.lay_buy_property)).setVisibility(View.VISIBLE);
                ((LinearLayout) findViewById(R.id.lay_buy_property_select)).setVisibility(View.GONE);
                ((LinearLayout) findViewById(R.id.lay_rented)).setVisibility(View.VISIBLE);
                ((LinearLayout) findViewById(R.id.lay_rented_select)).setVisibility(View.GONE);
                ((LinearLayout) findViewById(R.id.lay_renter_property)).setVisibility(View.VISIBLE);
                ((LinearLayout) findViewById(R.id.lay_renter_property_select)).setVisibility(View.GONE);
                ((LinearLayout) findViewById(R.id.lay_request)).setVisibility(View.VISIBLE);
                ((LinearLayout) findViewById(R.id.lay_request_selcted)).setVisibility(View.GONE);
                ((LinearLayout) findViewById(R.id.lay_job)).setVisibility(View.VISIBLE);
                ((LinearLayout) findViewById(R.id.lay_job_selcted)).setVisibility(View.GONE);
                ((LinearLayout) findViewById(R.id.lay_news_feeds)).setVisibility(View.VISIBLE);
                ((LinearLayout) findViewById(R.id.lay_news_feeds_selcted)).setVisibility(View.GONE);
                ((LinearLayout) findViewById(R.id.lay_find_service)).setVisibility(View.VISIBLE);
                ((LinearLayout) findViewById(R.id.lay_find_service_select)).setVisibility(View.GONE);
                ((LinearLayout) findViewById(R.id.lay_sp)).setVisibility(View.VISIBLE);
                ((LinearLayout) findViewById(R.id.lay_sp_select)).setVisibility(View.GONE);
                ((LinearLayout) findViewById(R.id.lay_offer)).setVisibility(View.VISIBLE);
                ((LinearLayout) findViewById(R.id.lay_offer_select)).setVisibility(View.GONE);
                ((LinearLayout) findViewById(R.id.lay_hs)).setVisibility(View.VISIBLE);
                ((LinearLayout) findViewById(R.id.lay_hs_selcted)).setVisibility(View.GONE);
                ((LinearLayout) findViewById(R.id.lay_loan_select)).setVisibility(View.GONE);
                ((LinearLayout) findViewById(R.id.lay_loan)).setVisibility(View.VISIBLE);
                ((LinearLayout) findViewById(R.id.lay_faq)).setVisibility(View.VISIBLE);
                ((LinearLayout) findViewById(R.id.lay_faq_select)).setVisibility(View.GONE);
                startActivity(new Intent(context, SavedSearchesActivity.class).putExtra(Constants.TYPE, "Search History"));
                break;
            case R.id.card_bid:
                ((LinearLayout) findViewById(R.id.lay_search)).setVisibility(View.VISIBLE);
                ((LinearLayout) findViewById(R.id.lay_search_select)).setVisibility(View.GONE);
                ((LinearLayout) findViewById(R.id.lay_payment)).setVisibility(View.VISIBLE);
                ((LinearLayout) findViewById(R.id.lay_payment_Select)).setVisibility(View.GONE);
                ((LinearLayout) findViewById(R.id.lay_plan)).setVisibility(View.VISIBLE);
                ((LinearLayout) findViewById(R.id.lay_plan_selcted)).setVisibility(View.GONE);
                ((LinearLayout) findViewById(R.id.lay_chat)).setVisibility(View.VISIBLE);
                ((LinearLayout) findViewById(R.id.lay_chat_select)).setVisibility(View.GONE);
                ((LinearLayout) findViewById(R.id.lay_bid)).setVisibility(View.GONE);
                ((LinearLayout) findViewById(R.id.lay_bid_select)).setVisibility(View.VISIBLE);
                ((LinearLayout) findViewById(R.id.lay_buy_property)).setVisibility(View.VISIBLE);
                ((LinearLayout) findViewById(R.id.lay_buy_property_select)).setVisibility(View.GONE);
                ((LinearLayout) findViewById(R.id.lay_rented)).setVisibility(View.VISIBLE);
                ((LinearLayout) findViewById(R.id.lay_rented_select)).setVisibility(View.GONE);
                ((LinearLayout) findViewById(R.id.lay_renter_property)).setVisibility(View.VISIBLE);
                ((LinearLayout) findViewById(R.id.lay_renter_property_select)).setVisibility(View.GONE);
                ((LinearLayout) findViewById(R.id.lay_request)).setVisibility(View.VISIBLE);
                ((LinearLayout) findViewById(R.id.lay_request_selcted)).setVisibility(View.GONE);
                ((LinearLayout) findViewById(R.id.lay_job)).setVisibility(View.VISIBLE);
                ((LinearLayout) findViewById(R.id.lay_job_selcted)).setVisibility(View.GONE);
                ((LinearLayout) findViewById(R.id.lay_news_feeds)).setVisibility(View.VISIBLE);
                ((LinearLayout) findViewById(R.id.lay_news_feeds_selcted)).setVisibility(View.GONE);
                ((LinearLayout) findViewById(R.id.lay_find_service)).setVisibility(View.VISIBLE);
                ((LinearLayout) findViewById(R.id.lay_find_service_select)).setVisibility(View.GONE);
                ((LinearLayout) findViewById(R.id.lay_sp)).setVisibility(View.VISIBLE);
                ((LinearLayout) findViewById(R.id.lay_sp_select)).setVisibility(View.GONE);
                ((LinearLayout) findViewById(R.id.lay_offer)).setVisibility(View.VISIBLE);
                ((LinearLayout) findViewById(R.id.lay_offer_select)).setVisibility(View.GONE);
                ((LinearLayout) findViewById(R.id.lay_hs)).setVisibility(View.VISIBLE);
                ((LinearLayout) findViewById(R.id.lay_hs_selcted)).setVisibility(View.GONE);
                ((LinearLayout) findViewById(R.id.lay_loan_select)).setVisibility(View.GONE);
                ((LinearLayout) findViewById(R.id.lay_loan)).setVisibility(View.VISIBLE);
                ((LinearLayout) findViewById(R.id.lay_faq)).setVisibility(View.VISIBLE);
                ((LinearLayout) findViewById(R.id.lay_faq_select)).setVisibility(View.GONE);
                txt_offer.setVisibility(View.GONE);
                if (offerMyProperty > 0)
                    Utility.ReadNoti(context, NotificationConstant.OfferAcceptByBuyerSeller+","+NotificationConstant.Offer);
                startActivity(new Intent(context, MyOfferList.class).putExtra(Constants.TYPE, "self"));
                break;
            case R.id.card_buy_property:
                ((LinearLayout) findViewById(R.id.lay_search)).setVisibility(View.VISIBLE);
                ((LinearLayout) findViewById(R.id.lay_search_select)).setVisibility(View.GONE);
                ((LinearLayout) findViewById(R.id.lay_payment)).setVisibility(View.VISIBLE);
                ((LinearLayout) findViewById(R.id.lay_payment_Select)).setVisibility(View.GONE);
                ((LinearLayout) findViewById(R.id.lay_plan)).setVisibility(View.VISIBLE);
                ((LinearLayout) findViewById(R.id.lay_plan_selcted)).setVisibility(View.GONE);
                ((LinearLayout) findViewById(R.id.lay_chat)).setVisibility(View.VISIBLE);
                ((LinearLayout) findViewById(R.id.lay_chat_select)).setVisibility(View.GONE);
                ((LinearLayout) findViewById(R.id.lay_bid)).setVisibility(View.VISIBLE);
                ((LinearLayout) findViewById(R.id.lay_bid_select)).setVisibility(View.GONE);
                ((LinearLayout) findViewById(R.id.lay_buy_property)).setVisibility(View.GONE);
                ((LinearLayout) findViewById(R.id.lay_buy_property_select)).setVisibility(View.VISIBLE);
                ((LinearLayout) findViewById(R.id.lay_rented)).setVisibility(View.VISIBLE);
                ((LinearLayout) findViewById(R.id.lay_rented_select)).setVisibility(View.GONE);
                ((LinearLayout) findViewById(R.id.lay_renter_property)).setVisibility(View.VISIBLE);
                ((LinearLayout) findViewById(R.id.lay_renter_property_select)).setVisibility(View.GONE);
                ((LinearLayout) findViewById(R.id.lay_request)).setVisibility(View.VISIBLE);
                ((LinearLayout) findViewById(R.id.lay_request_selcted)).setVisibility(View.GONE);
                ((LinearLayout) findViewById(R.id.lay_job)).setVisibility(View.VISIBLE);
                ((LinearLayout) findViewById(R.id.lay_job_selcted)).setVisibility(View.GONE);
                ((LinearLayout) findViewById(R.id.lay_news_feeds)).setVisibility(View.VISIBLE);
                ((LinearLayout) findViewById(R.id.lay_news_feeds_selcted)).setVisibility(View.GONE);
                ((LinearLayout) findViewById(R.id.lay_find_service)).setVisibility(View.VISIBLE);
                ((LinearLayout) findViewById(R.id.lay_find_service_select)).setVisibility(View.GONE);
                ((LinearLayout) findViewById(R.id.lay_sp)).setVisibility(View.VISIBLE);
                ((LinearLayout) findViewById(R.id.lay_sp_select)).setVisibility(View.GONE);
                ((LinearLayout) findViewById(R.id.lay_offer)).setVisibility(View.VISIBLE);
                ((LinearLayout) findViewById(R.id.lay_offer_select)).setVisibility(View.GONE);
                ((LinearLayout) findViewById(R.id.lay_hs)).setVisibility(View.VISIBLE);
                ((LinearLayout) findViewById(R.id.lay_hs_selcted)).setVisibility(View.GONE);
                ((LinearLayout) findViewById(R.id.lay_loan_select)).setVisibility(View.GONE);
                ((LinearLayout) findViewById(R.id.lay_loan)).setVisibility(View.VISIBLE);
                ((LinearLayout) findViewById(R.id.lay_faq)).setVisibility(View.VISIBLE);
                ((LinearLayout) findViewById(R.id.lay_faq_select)).setVisibility(View.GONE);
                startActivity(new Intent(context, PostMakeOfferListActivity.class));
                break;
            case R.id.card_rent:
                ((LinearLayout) findViewById(R.id.lay_search)).setVisibility(View.VISIBLE);
                ((LinearLayout) findViewById(R.id.lay_search_select)).setVisibility(View.GONE);
                ((LinearLayout) findViewById(R.id.lay_payment)).setVisibility(View.VISIBLE);
                ((LinearLayout) findViewById(R.id.lay_payment_Select)).setVisibility(View.GONE);
                ((LinearLayout) findViewById(R.id.lay_plan)).setVisibility(View.VISIBLE);
                ((LinearLayout) findViewById(R.id.lay_plan_selcted)).setVisibility(View.GONE);
                ((LinearLayout) findViewById(R.id.lay_chat)).setVisibility(View.VISIBLE);
                ((LinearLayout) findViewById(R.id.lay_chat_select)).setVisibility(View.GONE);
                ((LinearLayout) findViewById(R.id.lay_bid)).setVisibility(View.VISIBLE);
                ((LinearLayout) findViewById(R.id.lay_bid_select)).setVisibility(View.GONE);
                ((LinearLayout) findViewById(R.id.lay_buy_property)).setVisibility(View.VISIBLE);
                ((LinearLayout) findViewById(R.id.lay_buy_property_select)).setVisibility(View.GONE);
                ((LinearLayout) findViewById(R.id.lay_rented)).setVisibility(View.GONE);
                ((LinearLayout) findViewById(R.id.lay_rented_select)).setVisibility(View.VISIBLE);
                ((LinearLayout) findViewById(R.id.lay_renter_property)).setVisibility(View.VISIBLE);
                ((LinearLayout) findViewById(R.id.lay_renter_property_select)).setVisibility(View.GONE);
                ((LinearLayout) findViewById(R.id.lay_request)).setVisibility(View.VISIBLE);
                ((LinearLayout) findViewById(R.id.lay_request_selcted)).setVisibility(View.GONE);
                ((LinearLayout) findViewById(R.id.lay_job)).setVisibility(View.VISIBLE);
                ((LinearLayout) findViewById(R.id.lay_job_selcted)).setVisibility(View.GONE);
                ((LinearLayout) findViewById(R.id.lay_news_feeds)).setVisibility(View.VISIBLE);
                ((LinearLayout) findViewById(R.id.lay_news_feeds_selcted)).setVisibility(View.GONE);
                ((LinearLayout) findViewById(R.id.lay_find_service)).setVisibility(View.VISIBLE);
                ((LinearLayout) findViewById(R.id.lay_find_service_select)).setVisibility(View.GONE);
                ((LinearLayout) findViewById(R.id.lay_sp)).setVisibility(View.VISIBLE);
                ((LinearLayout) findViewById(R.id.lay_sp_select)).setVisibility(View.GONE);
                ((LinearLayout) findViewById(R.id.lay_offer)).setVisibility(View.VISIBLE);
                ((LinearLayout) findViewById(R.id.lay_offer_select)).setVisibility(View.GONE);
                ((LinearLayout) findViewById(R.id.lay_hs)).setVisibility(View.VISIBLE);
                ((LinearLayout) findViewById(R.id.lay_hs_selcted)).setVisibility(View.GONE);
                ((LinearLayout) findViewById(R.id.lay_loan_select)).setVisibility(View.GONE);
                ((LinearLayout) findViewById(R.id.lay_loan)).setVisibility(View.VISIBLE);
                ((LinearLayout) findViewById(R.id.lay_faq)).setVisibility(View.VISIBLE);
                ((LinearLayout) findViewById(R.id.lay_faq_select)).setVisibility(View.GONE);
                txt_owner.setVisibility(View.GONE);
                if (propOwner > 0)
                    Utility.ReadNoti(context, NotificationConstant.RentDisclaimer);
                startActivity(new Intent(context, RenterList.class).putExtra(Constants.TYPE, "Owner"));
                break;
            case R.id.card_renter_property:
                ((LinearLayout) findViewById(R.id.lay_search)).setVisibility(View.VISIBLE);
                ((LinearLayout) findViewById(R.id.lay_search_select)).setVisibility(View.GONE);
                ((LinearLayout) findViewById(R.id.lay_payment)).setVisibility(View.VISIBLE);
                ((LinearLayout) findViewById(R.id.lay_payment_Select)).setVisibility(View.GONE);
                ((LinearLayout) findViewById(R.id.lay_plan)).setVisibility(View.VISIBLE);
                ((LinearLayout) findViewById(R.id.lay_plan_selcted)).setVisibility(View.GONE);
                ((LinearLayout) findViewById(R.id.lay_chat)).setVisibility(View.VISIBLE);
                ((LinearLayout) findViewById(R.id.lay_chat_select)).setVisibility(View.GONE);
                ((LinearLayout) findViewById(R.id.lay_bid)).setVisibility(View.VISIBLE);
                ((LinearLayout) findViewById(R.id.lay_bid_select)).setVisibility(View.GONE);
                ((LinearLayout) findViewById(R.id.lay_buy_property)).setVisibility(View.VISIBLE);
                ((LinearLayout) findViewById(R.id.lay_buy_property_select)).setVisibility(View.GONE);
                ((LinearLayout) findViewById(R.id.lay_rented)).setVisibility(View.VISIBLE);
                ((LinearLayout) findViewById(R.id.lay_rented_select)).setVisibility(View.GONE);
                ((LinearLayout) findViewById(R.id.lay_rented)).setVisibility(View.VISIBLE);
                ((LinearLayout) findViewById(R.id.lay_rented_select)).setVisibility(View.GONE);
                ((LinearLayout) findViewById(R.id.lay_renter_property)).setVisibility(View.GONE);
                ((LinearLayout) findViewById(R.id.lay_renter_property_select)).setVisibility(View.VISIBLE);
                ((LinearLayout) findViewById(R.id.lay_request)).setVisibility(View.VISIBLE);
                ((LinearLayout) findViewById(R.id.lay_request_selcted)).setVisibility(View.GONE);
                ((LinearLayout) findViewById(R.id.lay_job)).setVisibility(View.VISIBLE);
                ((LinearLayout) findViewById(R.id.lay_job_selcted)).setVisibility(View.GONE);
                ((LinearLayout) findViewById(R.id.lay_news_feeds)).setVisibility(View.VISIBLE);
                ((LinearLayout) findViewById(R.id.lay_news_feeds_selcted)).setVisibility(View.GONE);
                ((LinearLayout) findViewById(R.id.lay_find_service)).setVisibility(View.VISIBLE);
                ((LinearLayout) findViewById(R.id.lay_find_service_select)).setVisibility(View.GONE);
                ((LinearLayout) findViewById(R.id.lay_sp)).setVisibility(View.VISIBLE);
                ((LinearLayout) findViewById(R.id.lay_sp_select)).setVisibility(View.GONE);
                ((LinearLayout) findViewById(R.id.lay_offer)).setVisibility(View.VISIBLE);
                ((LinearLayout) findViewById(R.id.lay_offer_select)).setVisibility(View.GONE);
                ((LinearLayout) findViewById(R.id.lay_hs)).setVisibility(View.VISIBLE);
                ((LinearLayout) findViewById(R.id.lay_hs_selcted)).setVisibility(View.GONE);
                ((LinearLayout) findViewById(R.id.lay_loan_select)).setVisibility(View.GONE);
                ((LinearLayout) findViewById(R.id.lay_loan)).setVisibility(View.VISIBLE);
                ((LinearLayout) findViewById(R.id.lay_faq)).setVisibility(View.VISIBLE);
                ((LinearLayout) findViewById(R.id.lay_faq_select)).setVisibility(View.GONE);
                txt_renter.setVisibility(View.GONE);
                if (renter > 0)
                    Utility.ReadNoti(context, NotificationConstant.RentApproved + "," + NotificationConstant.RentConfirmation + "," + NotificationConstant.RentDisclaimerApproved);
                startActivity(new Intent(context, RenterList.class).putExtra(Constants.TYPE, "Renter"));
                break;
            case R.id.card_request:
                ((LinearLayout) findViewById(R.id.lay_search)).setVisibility(View.VISIBLE);
                ((LinearLayout) findViewById(R.id.lay_search_select)).setVisibility(View.GONE);
                ((LinearLayout) findViewById(R.id.lay_payment)).setVisibility(View.VISIBLE);
                ((LinearLayout) findViewById(R.id.lay_payment_Select)).setVisibility(View.GONE);
                ((LinearLayout) findViewById(R.id.lay_plan)).setVisibility(View.VISIBLE);
                ((LinearLayout) findViewById(R.id.lay_plan_selcted)).setVisibility(View.GONE);
                ((LinearLayout) findViewById(R.id.lay_chat)).setVisibility(View.VISIBLE);
                ((LinearLayout) findViewById(R.id.lay_chat_select)).setVisibility(View.GONE);
                ((LinearLayout) findViewById(R.id.lay_bid)).setVisibility(View.VISIBLE);
                ((LinearLayout) findViewById(R.id.lay_bid_select)).setVisibility(View.GONE);
                ((LinearLayout) findViewById(R.id.lay_buy_property)).setVisibility(View.VISIBLE);
                ((LinearLayout) findViewById(R.id.lay_buy_property_select)).setVisibility(View.GONE);
                ((LinearLayout) findViewById(R.id.lay_rented)).setVisibility(View.VISIBLE);
                ((LinearLayout) findViewById(R.id.lay_rented_select)).setVisibility(View.GONE);
                ((LinearLayout) findViewById(R.id.lay_rented)).setVisibility(View.VISIBLE);
                ((LinearLayout) findViewById(R.id.lay_rented_select)).setVisibility(View.GONE);
                ((LinearLayout) findViewById(R.id.lay_renter_property)).setVisibility(View.VISIBLE);
                ((LinearLayout) findViewById(R.id.lay_renter_property_select)).setVisibility(View.GONE);
                ((LinearLayout) findViewById(R.id.lay_job)).setVisibility(View.VISIBLE);
                ((LinearLayout) findViewById(R.id.lay_job_selcted)).setVisibility(View.GONE);
                ((LinearLayout) findViewById(R.id.lay_request)).setVisibility(View.GONE);
                ((LinearLayout) findViewById(R.id.lay_request_selcted)).setVisibility(View.VISIBLE);
                ((LinearLayout) findViewById(R.id.lay_news_feeds)).setVisibility(View.VISIBLE);
                ((LinearLayout) findViewById(R.id.lay_news_feeds_selcted)).setVisibility(View.GONE);
                ((LinearLayout) findViewById(R.id.lay_find_service)).setVisibility(View.VISIBLE);
                ((LinearLayout) findViewById(R.id.lay_find_service_select)).setVisibility(View.GONE);
                ((LinearLayout) findViewById(R.id.lay_sp)).setVisibility(View.VISIBLE);
                ((LinearLayout) findViewById(R.id.lay_sp_select)).setVisibility(View.GONE);
                ((LinearLayout) findViewById(R.id.lay_offer)).setVisibility(View.VISIBLE);
                ((LinearLayout) findViewById(R.id.lay_offer_select)).setVisibility(View.GONE);
                ((LinearLayout) findViewById(R.id.lay_hs)).setVisibility(View.VISIBLE);
                ((LinearLayout) findViewById(R.id.lay_hs_selcted)).setVisibility(View.GONE);
                ((LinearLayout) findViewById(R.id.lay_loan_select)).setVisibility(View.GONE);
                ((LinearLayout) findViewById(R.id.lay_loan)).setVisibility(View.VISIBLE);
                ((LinearLayout) findViewById(R.id.lay_faq)).setVisibility(View.VISIBLE);
                ((LinearLayout) findViewById(R.id.lay_faq_select)).setVisibility(View.GONE);
                startActivity(new Intent(context, SPRequest.class).putExtra(Constants.TYPE, "user_request"));
                break;
            case R.id.card_job:
                ((LinearLayout) findViewById(R.id.lay_search)).setVisibility(View.VISIBLE);
                ((LinearLayout) findViewById(R.id.lay_search_select)).setVisibility(View.GONE);
                ((LinearLayout) findViewById(R.id.lay_payment)).setVisibility(View.VISIBLE);
                ((LinearLayout) findViewById(R.id.lay_payment_Select)).setVisibility(View.GONE);
                ((LinearLayout) findViewById(R.id.lay_plan)).setVisibility(View.VISIBLE);
                ((LinearLayout) findViewById(R.id.lay_plan_selcted)).setVisibility(View.GONE);
                ((LinearLayout) findViewById(R.id.lay_chat)).setVisibility(View.VISIBLE);
                ((LinearLayout) findViewById(R.id.lay_chat_select)).setVisibility(View.GONE);
                ((LinearLayout) findViewById(R.id.lay_bid)).setVisibility(View.VISIBLE);
                ((LinearLayout) findViewById(R.id.lay_bid_select)).setVisibility(View.GONE);
                ((LinearLayout) findViewById(R.id.lay_buy_property)).setVisibility(View.VISIBLE);
                ((LinearLayout) findViewById(R.id.lay_buy_property_select)).setVisibility(View.GONE);
                ((LinearLayout) findViewById(R.id.lay_rented)).setVisibility(View.VISIBLE);
                ((LinearLayout) findViewById(R.id.lay_rented_select)).setVisibility(View.GONE);
                ((LinearLayout) findViewById(R.id.lay_rented)).setVisibility(View.VISIBLE);
                ((LinearLayout) findViewById(R.id.lay_rented_select)).setVisibility(View.GONE);
                ((LinearLayout) findViewById(R.id.lay_renter_property)).setVisibility(View.VISIBLE);
                ((LinearLayout) findViewById(R.id.lay_renter_property_select)).setVisibility(View.GONE);
                ((LinearLayout) findViewById(R.id.lay_request)).setVisibility(View.VISIBLE);
                ((LinearLayout) findViewById(R.id.lay_request_selcted)).setVisibility(View.GONE);
                ((LinearLayout) findViewById(R.id.lay_job)).setVisibility(View.GONE);
                ((LinearLayout) findViewById(R.id.lay_job_selcted)).setVisibility(View.VISIBLE);
                ((LinearLayout) findViewById(R.id.lay_news_feeds)).setVisibility(View.VISIBLE);
                ((LinearLayout) findViewById(R.id.lay_news_feeds_selcted)).setVisibility(View.GONE);
                ((LinearLayout) findViewById(R.id.lay_find_service)).setVisibility(View.VISIBLE);
                ((LinearLayout) findViewById(R.id.lay_find_service_select)).setVisibility(View.GONE);
                ((LinearLayout) findViewById(R.id.lay_sp)).setVisibility(View.VISIBLE);
                ((LinearLayout) findViewById(R.id.lay_sp_select)).setVisibility(View.GONE);
                ((LinearLayout) findViewById(R.id.lay_offer)).setVisibility(View.VISIBLE);
                ((LinearLayout) findViewById(R.id.lay_offer_select)).setVisibility(View.GONE);
                ((LinearLayout) findViewById(R.id.lay_offer)).setVisibility(View.VISIBLE);
                ((LinearLayout) findViewById(R.id.lay_offer_select)).setVisibility(View.GONE);
                ((LinearLayout) findViewById(R.id.lay_hs)).setVisibility(View.VISIBLE);
                ((LinearLayout) findViewById(R.id.lay_hs_selcted)).setVisibility(View.GONE);
                ((LinearLayout) findViewById(R.id.lay_loan_select)).setVisibility(View.GONE);
                ((LinearLayout) findViewById(R.id.lay_loan)).setVisibility(View.VISIBLE);
                ((LinearLayout) findViewById(R.id.lay_faq)).setVisibility(View.VISIBLE);
                ((LinearLayout) findViewById(R.id.lay_faq_select)).setVisibility(View.GONE);
                txt_my_post.setVisibility(View.GONE);
                if (postService > 0)
                    Utility.ReadNoti(context, NotificationConstant.BidOnProject + "," + NotificationConstant.MilestoneRequest);
                startActivity(new Intent(context, MyPostDashboard.class));
                break;
            case R.id.card_offer:
                ((LinearLayout) findViewById(R.id.lay_search)).setVisibility(View.VISIBLE);
                ((LinearLayout) findViewById(R.id.lay_search_select)).setVisibility(View.GONE);
                ((LinearLayout) findViewById(R.id.lay_payment)).setVisibility(View.VISIBLE);
                ((LinearLayout) findViewById(R.id.lay_payment_Select)).setVisibility(View.GONE);
                ((LinearLayout) findViewById(R.id.lay_plan)).setVisibility(View.VISIBLE);
                ((LinearLayout) findViewById(R.id.lay_plan_selcted)).setVisibility(View.GONE);
                ((LinearLayout) findViewById(R.id.lay_chat)).setVisibility(View.VISIBLE);
                ((LinearLayout) findViewById(R.id.lay_chat_select)).setVisibility(View.GONE);
                ((LinearLayout) findViewById(R.id.lay_bid)).setVisibility(View.VISIBLE);
                ((LinearLayout) findViewById(R.id.lay_bid_select)).setVisibility(View.GONE);
                ((LinearLayout) findViewById(R.id.lay_buy_property)).setVisibility(View.VISIBLE);
                ((LinearLayout) findViewById(R.id.lay_buy_property_select)).setVisibility(View.GONE);
                ((LinearLayout) findViewById(R.id.lay_rented)).setVisibility(View.VISIBLE);
                ((LinearLayout) findViewById(R.id.lay_rented_select)).setVisibility(View.GONE);
                ((LinearLayout) findViewById(R.id.lay_rented)).setVisibility(View.VISIBLE);
                ((LinearLayout) findViewById(R.id.lay_rented_select)).setVisibility(View.GONE);
                ((LinearLayout) findViewById(R.id.lay_renter_property)).setVisibility(View.VISIBLE);
                ((LinearLayout) findViewById(R.id.lay_renter_property_select)).setVisibility(View.GONE);
                ((LinearLayout) findViewById(R.id.lay_request)).setVisibility(View.VISIBLE);
                ((LinearLayout) findViewById(R.id.lay_request_selcted)).setVisibility(View.GONE);
                ((LinearLayout) findViewById(R.id.lay_job)).setVisibility(View.VISIBLE);
                ((LinearLayout) findViewById(R.id.lay_job_selcted)).setVisibility(View.GONE);
                ((LinearLayout) findViewById(R.id.lay_news_feeds)).setVisibility(View.VISIBLE);
                ((LinearLayout) findViewById(R.id.lay_news_feeds_selcted)).setVisibility(View.GONE);
                ((LinearLayout) findViewById(R.id.lay_find_service)).setVisibility(View.VISIBLE);
                ((LinearLayout) findViewById(R.id.lay_find_service_select)).setVisibility(View.GONE);
                ((LinearLayout) findViewById(R.id.lay_sp)).setVisibility(View.VISIBLE);
                ((LinearLayout) findViewById(R.id.lay_sp_select)).setVisibility(View.GONE);
                ((LinearLayout) findViewById(R.id.lay_offer)).setVisibility(View.GONE);
                ((LinearLayout) findViewById(R.id.lay_offer_select)).setVisibility(View.VISIBLE);
                ((LinearLayout) findViewById(R.id.lay_hs)).setVisibility(View.VISIBLE);
                ((LinearLayout) findViewById(R.id.lay_hs_selcted)).setVisibility(View.GONE);
                ((LinearLayout) findViewById(R.id.lay_loan_select)).setVisibility(View.GONE);
                ((LinearLayout) findViewById(R.id.lay_loan)).setVisibility(View.VISIBLE);
                ((LinearLayout) findViewById(R.id.lay_faq)).setVisibility(View.VISIBLE);
                ((LinearLayout) findViewById(R.id.lay_faq_select)).setVisibility(View.GONE);
                txt_bid.setVisibility(View.GONE);
                if (offer > 0)
                    Utility.ReadNoti(context, NotificationConstant.ReplyOfferByBuyerSeller);
                startActivity(new Intent(context, MyOfferList.class).putExtra(Constants.TYPE, "other"));
                break;
            case R.id.card_hs:
                ((LinearLayout) findViewById(R.id.lay_search)).setVisibility(View.VISIBLE);
                ((LinearLayout) findViewById(R.id.lay_search_select)).setVisibility(View.GONE);
                ((LinearLayout) findViewById(R.id.lay_payment)).setVisibility(View.VISIBLE);
                ((LinearLayout) findViewById(R.id.lay_payment_Select)).setVisibility(View.GONE);
                ((LinearLayout) findViewById(R.id.lay_plan)).setVisibility(View.VISIBLE);
                ((LinearLayout) findViewById(R.id.lay_plan_selcted)).setVisibility(View.GONE);
                ((LinearLayout) findViewById(R.id.lay_chat)).setVisibility(View.VISIBLE);
                ((LinearLayout) findViewById(R.id.lay_chat_select)).setVisibility(View.GONE);
                ((LinearLayout) findViewById(R.id.lay_bid)).setVisibility(View.VISIBLE);
                ((LinearLayout) findViewById(R.id.lay_bid_select)).setVisibility(View.GONE);
                ((LinearLayout) findViewById(R.id.lay_buy_property)).setVisibility(View.VISIBLE);
                ((LinearLayout) findViewById(R.id.lay_buy_property_select)).setVisibility(View.GONE);
                ((LinearLayout) findViewById(R.id.lay_rented)).setVisibility(View.VISIBLE);
                ((LinearLayout) findViewById(R.id.lay_rented_select)).setVisibility(View.GONE);
                ((LinearLayout) findViewById(R.id.lay_rented)).setVisibility(View.VISIBLE);
                ((LinearLayout) findViewById(R.id.lay_rented_select)).setVisibility(View.GONE);
                ((LinearLayout) findViewById(R.id.lay_renter_property)).setVisibility(View.VISIBLE);
                ((LinearLayout) findViewById(R.id.lay_renter_property_select)).setVisibility(View.GONE);
                ((LinearLayout) findViewById(R.id.lay_request)).setVisibility(View.VISIBLE);
                ((LinearLayout) findViewById(R.id.lay_request_selcted)).setVisibility(View.GONE);
                ((LinearLayout) findViewById(R.id.lay_job)).setVisibility(View.VISIBLE);
                ((LinearLayout) findViewById(R.id.lay_job_selcted)).setVisibility(View.GONE);
                ((LinearLayout) findViewById(R.id.lay_news_feeds)).setVisibility(View.VISIBLE);
                ((LinearLayout) findViewById(R.id.lay_news_feeds_selcted)).setVisibility(View.GONE);
                ((LinearLayout) findViewById(R.id.lay_find_service)).setVisibility(View.VISIBLE);
                ((LinearLayout) findViewById(R.id.lay_find_service_select)).setVisibility(View.GONE);
                ((LinearLayout) findViewById(R.id.lay_sp)).setVisibility(View.VISIBLE);
                ((LinearLayout) findViewById(R.id.lay_sp_select)).setVisibility(View.GONE);
                ((LinearLayout) findViewById(R.id.lay_offer)).setVisibility(View.VISIBLE);
                ((LinearLayout) findViewById(R.id.lay_offer_select)).setVisibility(View.GONE);
                ((LinearLayout) findViewById(R.id.lay_hs)).setVisibility(View.GONE);
                ((LinearLayout) findViewById(R.id.lay_hs_selcted)).setVisibility(View.VISIBLE);
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(Constants.HS_WEB)));
                ((LinearLayout) findViewById(R.id.lay_loan_select)).setVisibility(View.GONE);
                ((LinearLayout) findViewById(R.id.lay_loan)).setVisibility(View.VISIBLE);
                ((LinearLayout) findViewById(R.id.lay_faq)).setVisibility(View.VISIBLE);
                ((LinearLayout) findViewById(R.id.lay_faq_select)).setVisibility(View.GONE);
                /*Intent launchIntent = getPackageManager().getLaunchIntentForPackage("com.open_space.hs");
                if (launchIntent != null) {
                    startActivity(launchIntent);//null pointer check in case package name was not found
                }*/
                // startActivity(new Intent(context, MyPostDashboard.class));
                break;
            case R.id.card_sp:
                ((LinearLayout) findViewById(R.id.lay_search)).setVisibility(View.VISIBLE);
                ((LinearLayout) findViewById(R.id.lay_search_select)).setVisibility(View.GONE);
                ((LinearLayout) findViewById(R.id.lay_payment)).setVisibility(View.VISIBLE);
                ((LinearLayout) findViewById(R.id.lay_payment_Select)).setVisibility(View.GONE);
                ((LinearLayout) findViewById(R.id.lay_plan)).setVisibility(View.VISIBLE);
                ((LinearLayout) findViewById(R.id.lay_plan_selcted)).setVisibility(View.GONE);
                ((LinearLayout) findViewById(R.id.lay_chat)).setVisibility(View.VISIBLE);
                ((LinearLayout) findViewById(R.id.lay_chat_select)).setVisibility(View.GONE);
                ((LinearLayout) findViewById(R.id.lay_bid)).setVisibility(View.VISIBLE);
                ((LinearLayout) findViewById(R.id.lay_bid_select)).setVisibility(View.GONE);
                ((LinearLayout) findViewById(R.id.lay_buy_property)).setVisibility(View.VISIBLE);
                ((LinearLayout) findViewById(R.id.lay_buy_property_select)).setVisibility(View.GONE);
                ((LinearLayout) findViewById(R.id.lay_rented)).setVisibility(View.VISIBLE);
                ((LinearLayout) findViewById(R.id.lay_rented_select)).setVisibility(View.GONE);
                ((LinearLayout) findViewById(R.id.lay_rented)).setVisibility(View.VISIBLE);
                ((LinearLayout) findViewById(R.id.lay_rented_select)).setVisibility(View.GONE);
                ((LinearLayout) findViewById(R.id.lay_renter_property)).setVisibility(View.VISIBLE);
                ((LinearLayout) findViewById(R.id.lay_renter_property_select)).setVisibility(View.GONE);
                ((LinearLayout) findViewById(R.id.lay_request)).setVisibility(View.VISIBLE);
                ((LinearLayout) findViewById(R.id.lay_request_selcted)).setVisibility(View.GONE);
                ((LinearLayout) findViewById(R.id.lay_job)).setVisibility(View.VISIBLE);
                ((LinearLayout) findViewById(R.id.lay_job_selcted)).setVisibility(View.GONE);
                ((LinearLayout) findViewById(R.id.lay_news_feeds)).setVisibility(View.VISIBLE);
                ((LinearLayout) findViewById(R.id.lay_news_feeds_selcted)).setVisibility(View.GONE);
                ((LinearLayout) findViewById(R.id.lay_find_service)).setVisibility(View.VISIBLE);
                ((LinearLayout) findViewById(R.id.lay_find_service_select)).setVisibility(View.GONE);
                ((LinearLayout) findViewById(R.id.lay_sp)).setVisibility(View.GONE);
                ((LinearLayout) findViewById(R.id.lay_sp_select)).setVisibility(View.VISIBLE);
                ((LinearLayout) findViewById(R.id.lay_offer)).setVisibility(View.VISIBLE);
                ((LinearLayout) findViewById(R.id.lay_offer_select)).setVisibility(View.GONE);
                ((LinearLayout) findViewById(R.id.lay_hs)).setVisibility(View.VISIBLE);
                ((LinearLayout) findViewById(R.id.lay_hs_selcted)).setVisibility(View.GONE);
                ((LinearLayout) findViewById(R.id.lay_loan_select)).setVisibility(View.GONE);
                ((LinearLayout) findViewById(R.id.lay_loan)).setVisibility(View.VISIBLE);
                ((LinearLayout) findViewById(R.id.lay_faq)).setVisibility(View.VISIBLE);
                ((LinearLayout) findViewById(R.id.lay_faq_select)).setVisibility(View.GONE);
                startActivity(new Intent(context, SPSignUpActivity.class));
                break;
            case R.id.card_news_feeds:
                ((LinearLayout) findViewById(R.id.lay_search)).setVisibility(View.VISIBLE);
                ((LinearLayout) findViewById(R.id.lay_search_select)).setVisibility(View.GONE);
                ((LinearLayout) findViewById(R.id.lay_payment)).setVisibility(View.VISIBLE);
                ((LinearLayout) findViewById(R.id.lay_payment_Select)).setVisibility(View.GONE);
                ((LinearLayout) findViewById(R.id.lay_plan)).setVisibility(View.VISIBLE);
                ((LinearLayout) findViewById(R.id.lay_plan_selcted)).setVisibility(View.GONE);
                ((LinearLayout) findViewById(R.id.lay_chat)).setVisibility(View.VISIBLE);
                ((LinearLayout) findViewById(R.id.lay_chat_select)).setVisibility(View.GONE);
                ((LinearLayout) findViewById(R.id.lay_bid)).setVisibility(View.VISIBLE);
                ((LinearLayout) findViewById(R.id.lay_bid_select)).setVisibility(View.GONE);
                ((LinearLayout) findViewById(R.id.lay_buy_property)).setVisibility(View.VISIBLE);
                ((LinearLayout) findViewById(R.id.lay_buy_property_select)).setVisibility(View.GONE);
                ((LinearLayout) findViewById(R.id.lay_rented)).setVisibility(View.VISIBLE);
                ((LinearLayout) findViewById(R.id.lay_rented_select)).setVisibility(View.GONE);
                ((LinearLayout) findViewById(R.id.lay_rented)).setVisibility(View.VISIBLE);
                ((LinearLayout) findViewById(R.id.lay_rented_select)).setVisibility(View.GONE);
                ((LinearLayout) findViewById(R.id.lay_renter_property)).setVisibility(View.VISIBLE);
                ((LinearLayout) findViewById(R.id.lay_renter_property_select)).setVisibility(View.GONE);
                ((LinearLayout) findViewById(R.id.lay_request)).setVisibility(View.VISIBLE);
                ((LinearLayout) findViewById(R.id.lay_request_selcted)).setVisibility(View.GONE);
                ((LinearLayout) findViewById(R.id.lay_job)).setVisibility(View.VISIBLE);
                ((LinearLayout) findViewById(R.id.lay_job_selcted)).setVisibility(View.GONE);
                ((LinearLayout) findViewById(R.id.lay_news_feeds)).setVisibility(View.GONE);
                ((LinearLayout) findViewById(R.id.lay_news_feeds_selcted)).setVisibility(View.VISIBLE);
                ((LinearLayout) findViewById(R.id.lay_sp)).setVisibility(View.VISIBLE);
                ((LinearLayout) findViewById(R.id.lay_sp_select)).setVisibility(View.GONE);
                ((LinearLayout) findViewById(R.id.lay_offer)).setVisibility(View.VISIBLE);
                ((LinearLayout) findViewById(R.id.lay_offer_select)).setVisibility(View.GONE);
                ((LinearLayout) findViewById(R.id.lay_hs)).setVisibility(View.VISIBLE);
                ((LinearLayout) findViewById(R.id.lay_hs_selcted)).setVisibility(View.GONE);
                ((LinearLayout) findViewById(R.id.lay_loan_select)).setVisibility(View.GONE);
                ((LinearLayout) findViewById(R.id.lay_loan)).setVisibility(View.VISIBLE);
                ((LinearLayout) findViewById(R.id.lay_faq)).setVisibility(View.VISIBLE);
                ((LinearLayout) findViewById(R.id.lay_faq_select)).setVisibility(View.GONE);
                txt_news_feed.setVisibility(View.GONE);
                if (liveFeed > 0)
                    Utility.ReadNoti(context, NotificationConstant.LikeNotification + "," +NotificationConstant.CommentNotification + "," + NotificationConstant.FollowRequest + "," + NotificationConstant.FollowRequestAccept);
                startActivity(new Intent(context, NewsFeedsActivity.class));
                break;
            case R.id.card_find_service:
                ((LinearLayout) findViewById(R.id.lay_find_service)).setVisibility(View.GONE);
                ((LinearLayout) findViewById(R.id.lay_find_service_select)).setVisibility(View.VISIBLE);
                ((LinearLayout) findViewById(R.id.lay_search)).setVisibility(View.VISIBLE);
                ((LinearLayout) findViewById(R.id.lay_search_select)).setVisibility(View.GONE);
                ((LinearLayout) findViewById(R.id.lay_payment)).setVisibility(View.VISIBLE);
                ((LinearLayout) findViewById(R.id.lay_payment_Select)).setVisibility(View.GONE);
                ((LinearLayout) findViewById(R.id.lay_plan)).setVisibility(View.VISIBLE);
                ((LinearLayout) findViewById(R.id.lay_plan_selcted)).setVisibility(View.GONE);
                ((LinearLayout) findViewById(R.id.lay_chat)).setVisibility(View.VISIBLE);
                ((LinearLayout) findViewById(R.id.lay_chat_select)).setVisibility(View.GONE);
                ((LinearLayout) findViewById(R.id.lay_bid)).setVisibility(View.VISIBLE);
                ((LinearLayout) findViewById(R.id.lay_bid_select)).setVisibility(View.GONE);
                ((LinearLayout) findViewById(R.id.lay_buy_property)).setVisibility(View.VISIBLE);
                ((LinearLayout) findViewById(R.id.lay_buy_property_select)).setVisibility(View.GONE);
                ((LinearLayout) findViewById(R.id.lay_rented)).setVisibility(View.VISIBLE);
                ((LinearLayout) findViewById(R.id.lay_rented_select)).setVisibility(View.GONE);
                ((LinearLayout) findViewById(R.id.lay_rented)).setVisibility(View.VISIBLE);
                ((LinearLayout) findViewById(R.id.lay_rented_select)).setVisibility(View.GONE);
                ((LinearLayout) findViewById(R.id.lay_renter_property)).setVisibility(View.VISIBLE);
                ((LinearLayout) findViewById(R.id.lay_renter_property_select)).setVisibility(View.GONE);
                ((LinearLayout) findViewById(R.id.lay_request)).setVisibility(View.VISIBLE);
                ((LinearLayout) findViewById(R.id.lay_request_selcted)).setVisibility(View.GONE);
                ((LinearLayout) findViewById(R.id.lay_job)).setVisibility(View.VISIBLE);
                ((LinearLayout) findViewById(R.id.lay_job_selcted)).setVisibility(View.GONE);
                ((LinearLayout) findViewById(R.id.lay_sp)).setVisibility(View.VISIBLE);
                ((LinearLayout) findViewById(R.id.lay_sp_select)).setVisibility(View.GONE);
                ((LinearLayout) findViewById(R.id.lay_offer)).setVisibility(View.VISIBLE);
                ((LinearLayout) findViewById(R.id.lay_offer_select)).setVisibility(View.GONE);
                ((LinearLayout) findViewById(R.id.lay_hs)).setVisibility(View.VISIBLE);
                ((LinearLayout) findViewById(R.id.lay_hs_selcted)).setVisibility(View.GONE);
                ((LinearLayout) findViewById(R.id.lay_loan_select)).setVisibility(View.GONE);
                ((LinearLayout) findViewById(R.id.lay_loan)).setVisibility(View.VISIBLE);
                ((LinearLayout) findViewById(R.id.lay_faq)).setVisibility(View.VISIBLE);
                ((LinearLayout) findViewById(R.id.lay_faq_select)).setVisibility(View.GONE);
                txt_service.setVisibility(View.GONE);
                if (service > 0)
                Utility.ReadNoti(context, NotificationConstant.BidOnProject + "," + NotificationConstant.MilestoneRequest);
                startActivity(new Intent(context, UserService.class));
                break;
            case R.id.card_loan:
                ((LinearLayout) findViewById(R.id.lay_loan_select)).setVisibility(View.VISIBLE);
                ((LinearLayout) findViewById(R.id.lay_loan)).setVisibility(View.GONE);
                ((LinearLayout) findViewById(R.id.lay_find_service)).setVisibility(View.VISIBLE);
                ((LinearLayout) findViewById(R.id.lay_find_service_select)).setVisibility(View.GONE);
                ((LinearLayout) findViewById(R.id.lay_search)).setVisibility(View.VISIBLE);
                ((LinearLayout) findViewById(R.id.lay_search_select)).setVisibility(View.GONE);
                ((LinearLayout) findViewById(R.id.lay_payment)).setVisibility(View.VISIBLE);
                ((LinearLayout) findViewById(R.id.lay_payment_Select)).setVisibility(View.GONE);
                ((LinearLayout) findViewById(R.id.lay_plan)).setVisibility(View.VISIBLE);
                ((LinearLayout) findViewById(R.id.lay_plan_selcted)).setVisibility(View.GONE);
                ((LinearLayout) findViewById(R.id.lay_chat)).setVisibility(View.VISIBLE);
                ((LinearLayout) findViewById(R.id.lay_chat_select)).setVisibility(View.GONE);
                ((LinearLayout) findViewById(R.id.lay_bid)).setVisibility(View.VISIBLE);
                ((LinearLayout) findViewById(R.id.lay_bid_select)).setVisibility(View.GONE);
                ((LinearLayout) findViewById(R.id.lay_buy_property)).setVisibility(View.VISIBLE);
                ((LinearLayout) findViewById(R.id.lay_buy_property_select)).setVisibility(View.GONE);
                ((LinearLayout) findViewById(R.id.lay_rented)).setVisibility(View.VISIBLE);
                ((LinearLayout) findViewById(R.id.lay_rented_select)).setVisibility(View.GONE);
                ((LinearLayout) findViewById(R.id.lay_rented)).setVisibility(View.VISIBLE);
                ((LinearLayout) findViewById(R.id.lay_rented_select)).setVisibility(View.GONE);
                ((LinearLayout) findViewById(R.id.lay_renter_property)).setVisibility(View.VISIBLE);
                ((LinearLayout) findViewById(R.id.lay_renter_property_select)).setVisibility(View.GONE);
                ((LinearLayout) findViewById(R.id.lay_request)).setVisibility(View.VISIBLE);
                ((LinearLayout) findViewById(R.id.lay_request_selcted)).setVisibility(View.GONE);
                ((LinearLayout) findViewById(R.id.lay_job)).setVisibility(View.VISIBLE);
                ((LinearLayout) findViewById(R.id.lay_job_selcted)).setVisibility(View.GONE);
                ((LinearLayout) findViewById(R.id.lay_sp)).setVisibility(View.VISIBLE);
                ((LinearLayout) findViewById(R.id.lay_sp_select)).setVisibility(View.GONE);
                ((LinearLayout) findViewById(R.id.lay_offer)).setVisibility(View.VISIBLE);
                ((LinearLayout) findViewById(R.id.lay_offer_select)).setVisibility(View.GONE);
                ((LinearLayout) findViewById(R.id.lay_hs)).setVisibility(View.VISIBLE);
                ((LinearLayout) findViewById(R.id.lay_hs_selcted)).setVisibility(View.GONE);
                ((LinearLayout) findViewById(R.id.lay_faq)).setVisibility(View.VISIBLE);
                ((LinearLayout) findViewById(R.id.lay_faq_select)).setVisibility(View.GONE);
                startActivity(new Intent(context, LoanActivity.class));
                break;
            case R.id.card_FAQ:
                ((LinearLayout) findViewById(R.id.lay_faq)).setVisibility(View.GONE);
                ((LinearLayout) findViewById(R.id.lay_faq_select)).setVisibility(View.VISIBLE);
                ((LinearLayout) findViewById(R.id.lay_loan_select)).setVisibility(View.GONE);
                ((LinearLayout) findViewById(R.id.lay_loan)).setVisibility(View.VISIBLE);
                ((LinearLayout) findViewById(R.id.lay_find_service)).setVisibility(View.VISIBLE);
                ((LinearLayout) findViewById(R.id.lay_find_service_select)).setVisibility(View.GONE);
                ((LinearLayout) findViewById(R.id.lay_search)).setVisibility(View.VISIBLE);
                ((LinearLayout) findViewById(R.id.lay_search_select)).setVisibility(View.GONE);
                ((LinearLayout) findViewById(R.id.lay_payment)).setVisibility(View.VISIBLE);
                ((LinearLayout) findViewById(R.id.lay_payment_Select)).setVisibility(View.GONE);
                ((LinearLayout) findViewById(R.id.lay_plan)).setVisibility(View.VISIBLE);
                ((LinearLayout) findViewById(R.id.lay_plan_selcted)).setVisibility(View.GONE);
                ((LinearLayout) findViewById(R.id.lay_chat)).setVisibility(View.VISIBLE);
                ((LinearLayout) findViewById(R.id.lay_chat_select)).setVisibility(View.GONE);
                ((LinearLayout) findViewById(R.id.lay_bid)).setVisibility(View.VISIBLE);
                ((LinearLayout) findViewById(R.id.lay_bid_select)).setVisibility(View.GONE);
                ((LinearLayout) findViewById(R.id.lay_buy_property)).setVisibility(View.VISIBLE);
                ((LinearLayout) findViewById(R.id.lay_buy_property_select)).setVisibility(View.GONE);
                ((LinearLayout) findViewById(R.id.lay_rented)).setVisibility(View.VISIBLE);
                ((LinearLayout) findViewById(R.id.lay_rented_select)).setVisibility(View.GONE);
                ((LinearLayout) findViewById(R.id.lay_rented)).setVisibility(View.VISIBLE);
                ((LinearLayout) findViewById(R.id.lay_rented_select)).setVisibility(View.GONE);
                ((LinearLayout) findViewById(R.id.lay_renter_property)).setVisibility(View.VISIBLE);
                ((LinearLayout) findViewById(R.id.lay_renter_property_select)).setVisibility(View.GONE);
                ((LinearLayout) findViewById(R.id.lay_request)).setVisibility(View.VISIBLE);
                ((LinearLayout) findViewById(R.id.lay_request_selcted)).setVisibility(View.GONE);
                ((LinearLayout) findViewById(R.id.lay_job)).setVisibility(View.VISIBLE);
                ((LinearLayout) findViewById(R.id.lay_job_selcted)).setVisibility(View.GONE);
                ((LinearLayout) findViewById(R.id.lay_sp)).setVisibility(View.VISIBLE);
                ((LinearLayout) findViewById(R.id.lay_sp_select)).setVisibility(View.GONE);
                ((LinearLayout) findViewById(R.id.lay_offer)).setVisibility(View.VISIBLE);
                ((LinearLayout) findViewById(R.id.lay_offer_select)).setVisibility(View.GONE);
                ((LinearLayout) findViewById(R.id.lay_hs)).setVisibility(View.VISIBLE);
                ((LinearLayout) findViewById(R.id.lay_hs_selcted)).setVisibility(View.GONE);
                startActivity(new Intent(context, ActivityFaq.class));
                break;


        }
    }

    @Override
    protected void onResume() {
        super.onResume();

    }
}
