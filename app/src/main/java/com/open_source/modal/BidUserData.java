package com.open_source.modal;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.open_source.retrofitPack.Constants;

public class BidUserData {
    @SerializedName(Constants.PROFILE_IMAGE)
    @Expose
    private String profileImage;

    public String getRating() {
        return rating;
    }

    @SerializedName(Constants.PRAPOSAL)
    @Expose
    private String proposal;

    public String getDays() { return days; }

    @SerializedName(Constants.DAYS)
    @Expose
    private String days;


    @SerializedName(Constants.RATTING)
    @Expose
    private String rating;


    @SerializedName(Constants.FIRST_NAME)
    @Expose
    private String first_name;

    @SerializedName(Constants.LAST_NAME)
    @Expose
    private String last_name;


    @SerializedName(Constants.CREATED_AT)
    @Expose
    private String created_at;

    @SerializedName(Constants.BIDAMOUNT)
    @Expose
    private String bid_amount;

    @SerializedName(Constants.POST_ID)
    @Expose
    private String post_id;

    public String getUserid() {
        return userid;
    }

    @SerializedName(Constants.USERID)
    @Expose

    private String userid;

    public String getProfileImage() {
        return profileImage;
    }

    public String getProposal() {
        return proposal;
    }

    public String getFirst_name() {
        return first_name;
    }

    public String getPost_id() { return post_id; }

    public String getLast_name() { return last_name; }

    public String getCreated_at() {
        return created_at;
    }

    public String getBid_amount() {
        return bid_amount;
    }
}
