package com.open_source.modal;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.open_source.retrofitPack.Constants;

import java.io.Serializable;

public class PropertyImg implements Serializable {

    @SerializedName(Constants.FILE_NAME)
    @Expose
    private String file_name;

    public String getImage_id() {
        return image_id;
    }

    @SerializedName(Constants.IMAGE_ID)
    @Expose
    private String image_id;

    public String getFile_name() {
        return file_name;
    }

    public void setFile_name(String file_name) {
        this.file_name = file_name;
    }
}
