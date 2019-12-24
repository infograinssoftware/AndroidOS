package com.open_source.modal;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.open_source.retrofitPack.Constants;

import java.util.ArrayList;

public class RetrofitUserData {
    @SerializedName(Constants.MESSAGE)
    @Expose
    private String message;

    @SerializedName(Constants.STATUS)
    @Expose
    private int status;

    @SerializedName(Constants.PAYMENT_STATUS)
    @Expose
    private String payment_status;

    @SerializedName(Constants.USER_DATA)
    @Expose
    private UserData userData;

    @SerializedName(Constants.OBJECT)
    @Expose
    private ArrayList<RetroObject> object;


    @SerializedName(Constants.SEND)
    @Expose
    private ArrayList<Send> send;


    @SerializedName(Constants.RECIEVE)
    @Expose
    private ArrayList<Recieve> recieve;


    @SerializedName(Constants.FLAG_URL)
    @Expose
    private String flagUrl;

    public String getFlagUrl() {
        return flagUrl;
    }

    public String getPayment_status() {
        return payment_status;
    }

    public ArrayList<Send> getSend() {
        return send;
    }

    public ArrayList<Recieve> getRecieve() {
        return recieve;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public UserData getUserData() {
        return userData;
    }

    public void setUserData(UserData userData) {
        this.userData = userData;
    }

    public ArrayList<RetroObject> getObject() {
        return object;
    }

    public void setObject(ArrayList<RetroObject> object) {
        this.object = object;
    }

    public class Send {
        @SerializedName(Constants.NAME)
        @Expose

        private String name;
        @SerializedName(Constants.FILE)
        @Expose
        private String file;

        public String getName() {
            return name;
        }

        public String getFile() {
            return file;
        }
    }

    public class Recieve {
        @SerializedName(Constants.NAME)
        @Expose

        private String name;
        @SerializedName(Constants.FILE)
        @Expose
        private String file;

        public String getName() {
            return name;
        }

        public String getFile() {
            return file;
        }


    }



}