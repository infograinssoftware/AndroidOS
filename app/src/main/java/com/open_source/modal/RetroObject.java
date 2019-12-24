package com.open_source.modal;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.open_source.retrofitPack.Constants;

import java.io.Serializable;
import java.util.ArrayList;


public class RetroObject implements Serializable{
    public String getTotal_bid() {
        return total_bid;
    }

    public String getService_id() {
        return service_id;
    }

    public String getProposal() {
        return proposal;
    }

    public String getStatus() {
        return status;
    }

    public String getFile() {
        return file;
    }

    @SerializedName(Constants.ID)
    @Expose
    private String id;

    @SerializedName(Constants.CURRENCY_NAME)
    @Expose
    private String currency_name;

    @SerializedName(Constants.CURRENCY_CODE)
    @Expose
    private String currency_code;

    @SerializedName(Constants.CURRENCY_SYMBOL)
    @Expose
    private String currency_symbol;

    @SerializedName(Constants.COUNTRY_CURRENCY)
    @Expose
    private String country_currency;

    @SerializedName(Constants.ISO)
    @Expose
    private String ISO;

    @SerializedName(Constants.CODE)
    @Expose
    private String code;

    @SerializedName(Constants.SYMBOL)
    @Expose
    private String symbol;

    public String getFollower_id() {
        return follower_id;
    }

    public String getCalendar_type() {
        return calendar_type;
    }

    @SerializedName(Constants.TOTAL_BID)
    @Expose
    private String total_bid;

    @SerializedName(Constants.CALENDAR_TYPE)
    @Expose
    private String calendar_type;




    public String getOffer_status() {
        return offer_status;
    }

    @SerializedName(Constants.FILE)
    @Expose

    private String file;

    @SerializedName(Constants.FOLLOWER_ID)
    @Expose
    private String follower_id;

    public String getMake_offer() {
        return make_offer;
    }

    public String getWalkthrough_id() {
        return walkthrough_id;
    }

    @SerializedName(Constants.PRAPOSAL)
    @Expose

    private String proposal;

    @SerializedName(Constants.MAKE_OFFER)
    @Expose
    private String make_offer;


    @SerializedName(Constants.QUESTION_ID)
    @Expose
    private String question_id;


    @SerializedName(Constants.QUESTION)
    @Expose
    private String question;

    @SerializedName(Constants.ANSWER)
    @Expose
    private String answer;

    public String getAnswer() {
        return answer;
    }

    public String getOffer_id() {
        return offer_id;
    }

    @SerializedName(Constants.OFFER_STATUS)
    @Expose
    private String offer_status;

    @SerializedName(Constants.Walk_Through_Id)
    @Expose
    private String walkthrough_id;


    @SerializedName(Constants.STATUS)
    @Expose
    private String status;

    @SerializedName(Constants.OFFER_ID)
    @Expose
    private String offer_id;

    public String getDays() { return days; }

    @SerializedName(Constants.DAYS)
    @Expose
    private String days;




    public String getHigh_bid() {
        return high_bid;
    }

    public String getPost_id() {
        return post_id;
    }

    @SerializedName(Constants.HIGH_BID)

    @Expose
    private String high_bid;

    @SerializedName(Constants.POST_ID)
    @Expose
    private String post_id;

    public String getIcon() {
        return icon;
    }

    @SerializedName(Constants.SERVICE_ID)
    @Expose
    private String service_id;

    @SerializedName(Constants.ICON)
    @Expose
    private String icon;

    @SerializedName(Constants.LATITUDE)
    @Expose
    private String latitude;

    @SerializedName(Constants.LONGITUDE)
    @Expose
    private String longitude;

    @SerializedName(Constants.TYPE)
    @Expose
    private String type;

    @SerializedName(Constants.LOCATION)
    @Expose
    private String location;

    @SerializedName(Constants.PROPERTY_IMG)
    @Expose
    private ArrayList<PropertyImg> property_img;

    @SerializedName(Constants.SELLER_FIRST_NAME)
    @Expose
    private String seller_firstname;
    @SerializedName(Constants.SELLER_LAST_NAME)
    @Expose
    private String seller_lastname;
    @SerializedName(Constants.FIRST_NAME)
    @Expose
    private String first_name;
    @SerializedName(Constants.LAST_NAME)
    @Expose
    private String last_name;
    @SerializedName(Constants.EMAIL)
    @Expose
    private String email;
    @SerializedName(Constants.NOTIFICATION_TYPE)
    @Expose
    private String notification_type;
    @SerializedName(Constants.PROFILE_IMAGE)
    @Expose
    private String profileImage;
    @SerializedName(Constants.CITY)
    @Expose
    private String city;
    @SerializedName(Constants.FAVOURITE_STATUS)
    @Expose
    private String favourite_status;
    @SerializedName(Constants.POST)
    @Expose
    private String post;
    @SerializedName(Constants.REQUEST_DATE)
    @Expose
    private String request_date;
    @SerializedName(Constants.REQUEST_TIME)
    @Expose
    private String request_time;
    @SerializedName(Constants.TONAME)
    @Expose
    private String to_name;
    @SerializedName(Constants.URL)
    @Expose
    private String url;
    @SerializedName(Constants.USERNAME)
    @Expose
    private String user_name;
    @SerializedName(Constants.BIDAMOUNT)
    @Expose
    private String bid_amount;
    @SerializedName(Constants.FROMNAME)
    @Expose
    private String from_name;
    @SerializedName(Constants.CHAT_ID)
    @Expose
    private String chat_id;
    @SerializedName(Constants.TO_ID)
    @Expose
    private String to_id;
    @SerializedName(Constants.FROM_ID)

    @Expose
    private String from_id;
    @SerializedName(Constants.TO_IMAGE)
    @Expose
    private String to_user_profile_img;
    @SerializedName(Constants.TO_PROFILE_IMAGE)
    @Expose
    private String to_profile_img;
    @SerializedName(Constants.LAST_MSG)
    @Expose
    private String last_msg;

    public String getContent_type() {
        return content_type;
    }

    @SerializedName(Constants.MESSAGE)

    @Expose
    private String message;
    @SerializedName(Constants.PROPERTY_URL)
    @Expose
    private String property_url;
    @SerializedName(Constants.CONTENT_TYPE)
    @Expose
    private String content_type;
    @SerializedName(Constants.MOBILE_NUMBER)
    @Expose
    private String mobileNumber;
    @SerializedName(Constants.MOBILE)
    @Expose
    private String mobile;
    @SerializedName(Constants.AMOUNT)
    @Expose
    private String amount;
    @SerializedName(Constants.MESSAGE_TYPE)
    @Expose

    private String message_type;
    @SerializedName(Constants.PROPERTY_ID)
    @Expose
    private String property_id;
    @SerializedName(Constants.REQUEST_ID)
    @Expose
    private String request_id;
    @SerializedName(Constants.PROPERTYIMG)
    @Expose
    private String propertyImg;
    @SerializedName(Constants.PROPERTY_IMGES)
    @Expose
    private String property_images;
    @SerializedName(Constants.DATE)
    @Expose
    private String date;
    @SerializedName(Constants.PURPOSE)
    @Expose
    private String purpose;
    @SerializedName(Constants.CHAT_TIME)
    @Expose
    private String time;
    @SerializedName(Constants.USERID)
    @Expose
    private String userid;
    @SerializedName(Constants.OWNER_IMAGE)
    @Expose
    private String owner_image;

    public String getImage() {
        return image;
    }

    @SerializedName(Constants.PROPERTY_TYPE)
    @Expose

    private String property_type;

    @SerializedName(Constants.IMAGE)
    @Expose
    private String image;


    public String getLike_count() {
        return like_count;
    }

    public String getCategory() {
        return category;
    }

    @SerializedName(Constants.TOTAL_LIKES)

    @Expose

    private int total_like;

    @SerializedName(Constants.LIKE_COUNT)
    @Expose
    private String like_count;

    @SerializedName(Constants.Task_Date)
    @Expose
    private String task_dates;


    @SerializedName(Constants.CATEGORY)
    @Expose
    private String category;

    public String getName() {
        return name;
    }

    public String getDiscription() {
        return discription;
    }

    @SerializedName(Constants.TOTAL_COMMENTS)


    @Expose
    private int total_comments;

    @SerializedName(Constants.NAME)
    @Expose
    private String name;

    public String getUser_id() {
        return user_id;
    }

    public String getRequest_status() {
        return request_status;
    }

    @SerializedName(Constants.USER_ID)
    @Expose
    private String user_id;
    @SerializedName(Constants.DESCRIPTION)
    @Expose
    private String discription;
    @SerializedName(Constants.REQUEST_STATUS)
    @Expose
    private String request_status;
    @SerializedName(Constants.CAPTION)
    @Expose
    private String caption;

    public String getProblem() {
        return problem;
    }

    @SerializedName(Constants.LIKE_STATUS)
    @Expose

    private String like_status;
    @SerializedName(Constants.SAVE_STATUE)
    @Expose
    private String save_status;
    @SerializedName(Constants.CREATED_AT)
    @Expose
    private String created_at;

    public String getService_date() {
        return service_date;
    }

    public String getSp_id() {
        return sp_id;
    }

    public String getDescription() {
        return description;
    }

    public String getTotal_rate() {
        return total_rate;
    }

    @SerializedName(Constants.DISTANCE)
    @Expose

    private String distance_sql;

    @SerializedName(Constants.TOTAL_RATE)
    @Expose
    private String total_rate;


    public String getRating() {
        return rating;
    }

    @SerializedName(Constants.RATTING)

    @Expose
    private String rating;

    @SerializedName(Constants.DESCRIPTION_ABOUT)
    @Expose
    private String description;

    public String getCompleted_at() {
        return completed_at;
    }

    public String getAccepted_at() {
        return accepted_at;
    }

    public String getRenter_type() {
        return renter_type;
    }

    @SerializedName(Constants.PROBLEM)
    @Expose
    private String problem;

    @SerializedName(Constants.SERVICE_PROVIDER_ID)
    @Expose
    private String sp_id;

    @SerializedName(Constants.SERVICE_DATE)
    @Expose
    private String service_date;

    @SerializedName(Constants.COMPLETE_AT)
    @Expose
    private String completed_at;


    @SerializedName(Constants.ACCEPT_AT)
    @Expose
    private String accepted_at;

    @SerializedName(Constants.RENTER_TYPE)
    @Expose
    private String renter_type;

    @SerializedName(Constants.IMAGE_URL)
    @Expose
    private String image_url;


    @SerializedName(Constants.IMAGE_ID)
    @Expose
    private String image_id;

    public String getSymbol() {
        return symbol;
    }

    public String getFirst_name() {
        return first_name;
    }

    public void setFirst_name(String first_name) {
        this.first_name = first_name;
    }

    public String getLast_name() {
        return last_name;
    }

    public void setLast_name(String last_name) {
        this.last_name = last_name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getProfileImage() {
        return profileImage;
    }

    public void setProfileImage(String profileImage) {
        this.profileImage = profileImage;
    }

    public String getRequest_date() {
        return request_date;
    }

    public void setRequest_date(String request_date) {
        this.request_date = request_date;
    }

    public String getRequest_time() {
        return request_time;
    }

    public void setRequest_time(String request_time) {
        this.request_time = request_time;
    }

    public String getTo_name() {
        return to_name;
    }

    public String getLast_msg() {
        return last_msg;
    }

    public String getFrom_name() {
        return from_name;
    }

    public String getChat_id() {
        return chat_id;
    }

    public String getUrl() {
        return url;
    }

    public String getTo_id() {
        return to_id;
    }

    public String getFrom_id() {
        return from_id;
    }

    public String getTo_user_profile_img() {
        return to_user_profile_img;
    }

    public String getMessage() {
        return message;
    }

    public String getMessage_type() {
        return message_type;
    }

    public String getProperty_url() {
        return property_url;
    }

    public String getMobile() {
        return mobile;
    }

    public String getDistance_sql() {
        return distance_sql;
    }

    public String getTime() {
        return time;
    }

    public String getMobileNumber() {
        return mobileNumber;
    }

    public String getAmount() {
        return amount;
    }

    public String getBid_amount() {
        return bid_amount;
    }

    public String getUser_name() {
        return user_name;
    }

    public String getProperty_id() {
        return property_id;
    }

    public String getDate() {
        return date;
    }

    public String getRequest_id() {
        return request_id;
    }

    public String getPropertyImg() {
        return propertyImg;
    }

    public String getProperty_images() {
        return property_images;
    }

    public String getPurpose() {
        return purpose;
    }

    public String getUserid() {
        return userid;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public ArrayList<PropertyImg> getProperty_img() {
        return property_img;
    }

    public void setProperty_img(ArrayList<PropertyImg> property_img) {
        this.property_img = property_img;
    }

    public String getSeller_firstname() {
        return seller_firstname;
    }

    public void setSeller_firstname(String seller_firstname) {
        this.seller_firstname = seller_firstname;
    }

    public String getTask_dates() {
        return task_dates;
    }

    public String getSeller_lastname() {
        return seller_lastname;
    }

    public void setSeller_lastname(String seller_lastname) {
        this.seller_lastname = seller_lastname;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getFavourite_status() {
        return favourite_status;
    }

    public void setFavourite_status(String favourite_status) {
        this.favourite_status = favourite_status;
    }

    public String getISO() {
        return ISO;
    }

    /*    @SerializedName(Constants.LAST_COMMENTS)
    @Expose
    private String last_comments;*/

    public String getPost() {
        return post;
    }

    public void setPost(String post) {
        this.post = post;
    }

    public String getNotification_type() {
        return notification_type;
    }

    public String getTo_profile_img() {
        return to_profile_img;
    }

    public void setSave_status(String save_status) {
        this.save_status = save_status;
    }

    public String getCaption() {
        return caption;
    }

    public String getLike_status() {
        return like_status;
    }

    public void setLike_status(String like_status) {
        this.like_status = like_status;
    }

    public String getOwner_image() {
        return owner_image;
    }

    public String getProperty_type() {
        return property_type;
    }

    public String getCreated_at() {
        return created_at;
    }

 /*   public String getLast_comments() {
        return last_comments;

    }*/

    public String getQuestion_id() {
        return question_id;
    }

    public String getQuestion() {
        return question;
    }

    public int getTotal_like() {
        return total_like;
    }

    public void setTotal_like(int total_like) {
        this.total_like = total_like;
    }

    public int getTotal_comments() {
        return total_comments;
    }

    public String getSave_status() {
        return save_status;
    }


    public String getImage_url() {
        return image_url;
    }

    public String getImage_id() {
        return image_id;
    }

    public String getCountry_currency() {
        return country_currency;
    }

    public String getCode() {
        return code;
    }

    public String getCurrency_name() {
        return currency_name;
    }

    public String getCurrency_code() {
        return currency_code;
    }

    public String getCurrency_symbol() {
        return currency_symbol;
    }
}
