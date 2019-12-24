package com.open_source.modal;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.open_source.retrofitPack.Constants;

public  class USER_DATA_RENT {

    @SerializedName(Constants.FIRST_NAME)
    @Expose
    private String first_name;

    @SerializedName(Constants.LAST_NAME)
    @Expose

    private String last_name;

    @SerializedName(Constants.EMAIL)
    @Expose
    private String email;

    @SerializedName(Constants.MOBILE_NUMBER)
    @Expose
    private String mobileNumber;

    public String getFirst_name() {
        return first_name;
    }

    public String getLast_name() {
        return last_name;
    }


    public String getEmail() {
        return email;
    }

    public String getMobileNumber() {
        return mobileNumber;

    }
}
