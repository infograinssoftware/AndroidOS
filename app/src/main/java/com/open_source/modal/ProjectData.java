package com.open_source.modal;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.open_source.retrofitPack.Constants;

public class ProjectData {

    public String getProposal() {
        return proposal;
    }

    public String getDays() {
        return days;
    }

    public String getCreated_at() {
        return created_at;
    }

    public String getAmount() {
        return amount;
    }

    @SerializedName(Constants.PRAPOSAL)

    @Expose
    private String proposal;

    public String getDescription() {
        return description;
    }

    @SerializedName(Constants.DAYS)
    @Expose
    private String days;

    @SerializedName(Constants.DESCRIPTION_ABOUT)
    @Expose
    private String description;

    @SerializedName(Constants.CREATED_AT)
    @Expose
    private String created_at;

    @SerializedName(Constants.AMOUNT)
    @Expose
    private String amount;


}
