package com.open_source.modal;



import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.open_source.retrofitPack.Constants;

public  class RateReview {

    @SerializedName(Constants.NAME)
    @Expose
    private String name;

    @SerializedName(Constants.FIRST_NAME)
    @Expose
    private String first_name;

    @SerializedName(Constants.LAST_NAME)
    @Expose
    private String last_name;

    public String getName() {
        return name;
    }

    public String getFirst_name() {
        return first_name;
    }

    public String getLast_name() {
        return last_name;
    }

    public String getRate() {
        return rate;
    }

    public String getProfileImage() {
        return profileImage;
    }

    public String getComment() {
        return comment;
    }

    @SerializedName(Constants.RATE)
    @Expose
    private String rate;

    @SerializedName(Constants.PROFILE_IMAGE)
    @Expose
    private String profileImage;


    @SerializedName(Constants.COMMENT)
    @Expose
    private String comment;

}
