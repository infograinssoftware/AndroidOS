package com.open_source.modal;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.open_source.retrofitPack.Constants;

public class MilesTone {

    @SerializedName(Constants.CREATED_AT)
    @Expose
    private String created_at;

    public String getDescription() {
        return description;
    }

    @SerializedName(Constants.DESCRIPTION_ABOUT)

    @Expose
    private String description;

    public String getCreated_at() {
        return created_at;
    }

    public String getAmount() {
        return amount;
    }

    public String getId() {
        return id;
    }

    public String getStatus() {
        return status;

    }

    @SerializedName(Constants.AMOUNT)
    @Expose
    private String amount;


    @SerializedName(Constants.STATUS)
    @Expose
    private String status;

    @SerializedName(Constants.ID)
    @Expose
    private String id;


}
