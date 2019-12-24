package com.open_source.modal;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.open_source.retrofitPack.Constants;

public class Fixsure_Array
{

    @SerializedName(Constants.FIXTURES)
    @Expose
    private String fixsure;

    @SerializedName(Constants.COUNTERSTOP)
    @Expose
    private String counterstop;

    public String getFixsure() {
        return fixsure;
    }

    public String getCounterstop() {
        return counterstop;
    }

    public String getPrivate_patios() {
        return private_patios;
    }

    public String getWashers_dryers() {
        return washers_dryers;
    }

    public String getWood_flooring() {
        return wood_flooring;
    }

    public String getCarpet_flooring() {
        return carpet_flooring;
    }

    public String getCeiling_fans() {
        return ceiling_fans;
    }

    public String getFireplace() {
        return fireplace;
    }

    @SerializedName(Constants.PATIOS)
    @Expose
    private String private_patios;

    @SerializedName(Constants.WASHER_DRAWER)
    @Expose
    private String washers_dryers;


    @SerializedName(Constants.WOOD_FLORING)
    @Expose
    private String wood_flooring;


    @SerializedName(Constants.CARPET_FLORING)
    @Expose
    private String carpet_flooring;

    @SerializedName(Constants.CEILING_FAN)
    @Expose
    private String ceiling_fans;

    @SerializedName(Constants.FIRE_PLACE)
    @Expose
    private String fireplace;


}
