package com.open_source.modal;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

public class Subcatogery implements Parcelable {
    String id,name,price;

    public Subcatogery() {

    }

    protected Subcatogery(Parcel in) {
        id = in.readString();
        name = in.readString();
        price = in.readString();
    }

    public static final Creator<Subcatogery> CREATOR = new Creator<Subcatogery>() {
        @Override
        public Subcatogery createFromParcel(Parcel in) {
            return new Subcatogery(in);
        }

        @Override
        public Subcatogery[] newArray(int size) {
            return new Subcatogery[size];
        }
    };

    public String getName() {
        return name;
    }

    public String getId() {
        return id;
    }

    public Subcatogery(String id, String name) {
        this.id = id;
        this.name = name;

    }

    public void setId(String id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(name);
        dest.writeString(price);
    }
}
