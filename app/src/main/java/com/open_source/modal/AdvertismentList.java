package com.open_source.modal;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.open_source.retrofitPack.Constants;

public class AdvertismentList {

    @SerializedName(Constants.IMAGE)
    @Expose
    private String image;

    public String getImage() {
        return image;
    }
}
