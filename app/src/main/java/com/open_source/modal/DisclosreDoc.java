package com.open_source.modal;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.open_source.retrofitPack.Constants;

import java.io.Serializable;

public class DisclosreDoc implements Serializable{

    @SerializedName(Constants.DISCLOSURE_ID)
    @Expose
    private String discloser_id;

    @SerializedName(Constants.TYPE)
    @Expose
    private String type;

    @SerializedName(Constants.NAME)
    @Expose
    private String name;

    @SerializedName(Constants.FILE)
    @Expose
    private String file;

    public String getDiscloser_id() {
        return discloser_id;
    }

    public String getType() {
        return type;
    }

    public String getName() {
        return name;
    }

    public String getFile() {
        return file;
    }

}
