package com.open_source.modal;

/**
 * Created by and-02 on 19/6/18.
 */

public class UserList {

    String id,img,email,fname,lname,type,location,purpose,radius="";

    public UserList(String id, String img, String type, String location,String purpose) {
        this.id = id;
        this.img = img;
        this.type = type;
        this.location = location;
        this.purpose = purpose;
    }

    public UserList() {
    }


    public UserList(String id, String img, String email, String fname, String lname, String purpose) {
        this.id = id;
        this.img = img;
        this.email = email;
        this.fname = fname;
        this.lname = lname;
        this.purpose = purpose;
    }


    public String getPurpose() {
        return purpose;
    }
    public String getFname() {
        return fname;
    }
    public String getType() {
        return type;
    }
    public String getLocation() {
        return location;
    }
    public String getLname() {
        return lname;
    }
    public String getId() {
        return id;
    }

    public String getRadius() {
        return radius;
    }

    public void setRadius(String radius) {
        this.radius = radius;
    }

    public String getImg() {
        return img;
    }
    public String getEmail() {
        return email;
    }

}
