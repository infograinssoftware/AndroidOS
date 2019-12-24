package com.open_source.modal;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.open_source.retrofitPack.Constants;

public class PropertyData {

    @SerializedName(Constants.ID)
    @Expose
    private String id;

    @SerializedName(Constants.SOCIETY)
    @Expose
    private String society;

    @SerializedName(Constants.CITY)
    @Expose
    private String city;

    @SerializedName(Constants.LOCATION)
    @Expose
    private String location;


    @SerializedName(Constants.AREA)
    @Expose
    private String area_square;

    @SerializedName(Constants.BADROOM)
    @Expose
    private String badroom;

    @SerializedName(Constants.BATHROOM)
    @Expose
    private String bathroom;

    @SerializedName(Constants.PROPERTY_IMGES)
    @Expose
    private String property_images;

    @SerializedName(Constants.URL)
    @Expose
    private String url;

    public String getId() {
        return id;
    }

    public String getSociety() {
        return society;
    }

    public String getCity() {
        return city;
    }

    public String getLocation() {
        return location;
    }

    public String getUrl() {
        return url;
    }

    public String getArea_square() {
        return area_square;
    }

    public String getBadroom() {
        return badroom;
    }

    public String getBathroom() {
        return bathroom;
    }

    public String getProperty_images() {
        return property_images;
    }



}
