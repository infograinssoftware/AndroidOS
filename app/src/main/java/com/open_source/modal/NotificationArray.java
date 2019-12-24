package com.open_source.modal;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.open_source.retrofitPack.Constants;

public class NotificationArray {
    @SerializedName(Constants.NAME)
    @Expose
    private String name;

    public String getName() {
        return name;
    }

    public String getNoti_type() {
        return noti_type;
    }

    public int getNotitype_count() {
        return notitype_count;
    }

    @SerializedName(Constants.NOTI_TYPE)
    @Expose
    private String noti_type;

    @SerializedName(Constants.NOTITYPE_COUNT)
    @Expose
    private int notitype_count;
}
