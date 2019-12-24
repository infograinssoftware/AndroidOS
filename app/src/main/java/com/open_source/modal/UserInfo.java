package com.open_source.modal;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.open_source.retrofitPack.Constants;

public class UserInfo {

    public String getProfile_status() {
        return profile_status;
    }

    public String getFollower_status() {
        return follower_status;
    }

    @SerializedName(Constants.FIRST_NAME)
    @Expose

    private String first_name;

    @SerializedName(Constants.LAST_NAME)
    @Expose
    private String last_name;

    @SerializedName(Constants.MOBILE)
    @Expose
    private String mobile;

    @SerializedName(Constants.PROFILE_STATUS)
    @Expose
    private String profile_status;

    @SerializedName(Constants.FOLLOWERS_STATUS)
    @Expose
    private String follower_status;

    public String getFirst_name() {
        return first_name;
    }

    public String getLast_name() {
        return last_name;
    }

    public String getMobile() {
        return mobile;
    }

    public String getEmail() {
        return email;
    }

    public String getProperty_count() {
        return property_count;
    }

    public String getUrl() {
        return url;
    }

    public String getProfileImage() {
        return profileImage;
    }

    @SerializedName(Constants.EMAIL)
    @Expose

    private String email;

    @SerializedName(Constants.PROPERTY_COUNT)
    @Expose
    private String property_count;

    @SerializedName(Constants.URL)
    @Expose
    private String url;

    @SerializedName(Constants.PROFILE_IMAGE)
    @Expose
    private String profileImage;
}
