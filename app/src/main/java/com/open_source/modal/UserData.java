package com.open_source.modal;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.open_source.retrofitPack.Constants;

import java.util.ArrayList;

/**
 * Created by and-02 on 13/6/18.
 */

public class UserData {

    public String getService_date() {
        return service_date;
    }

    @SerializedName(Constants.LOGIN_TYPE)
    @Expose
    private String login_type;

    public String getNo_of_request() {
        return no_of_request;
    }

    public String getProperty_count() {
        return property_count;
    }

    public String getProfile_status() {

        return profile_status;
    }

    @SerializedName(Constants.PROFILE_STATUS)
    @Expose
    private String profile_status;

    @SerializedName(Constants.PROPERTY_COUNT)
    @Expose
    private String property_count;

    public String getId_proof_file() {
        return id_proof_file;
    }

    @SerializedName(Constants.NO_OF_REQ)
    @Expose
    private String no_of_request;

    public String getId_proof() {
        return id_proof;
    }

    @SerializedName(Constants.ADDRESS)
    @Expose
    private String address;

    @SerializedName(Constants.ID_PROOF)
    @Expose
    private String id_proof;

    @SerializedName(Constants.QUESTION_ID)
    @Expose
    private String question_id;

    @SerializedName(Constants.ANSWER)
    @Expose
    private String answer;

    @SerializedName(Constants.ID_PROOF_FILE)
    @Expose
    private String id_proof_file;

    @SerializedName(Constants.TOTAL_NOTIFICATION)
    @Expose
    private String total_notification;

    public String getName() {
        return name;
    }

    public String getBuyer_id() {
        return buyer_id;
    }

    public String getMobile() {
        return mobile;
    }

    public String getOffer_status() {
        return offer_status;
    }

    public String getBusiness_since() {
        return business_since;
    }

    @SerializedName(Constants.TOTAL_CLIENT_RATE)

    @Expose

    private String total_client_rate;

    public String getRenter_type() {
        return renter_type;
    }

    @SerializedName(Constants.SERVICE_DATE)
    @Expose
    private String service_date;

    @SerializedName(Constants.BUSINESS_SINCE)
    @Expose
    private String business_since;

    @SerializedName(Constants.EMAIL_VARIFICATION)
    @Expose
    private String email_verification;

    @SerializedName(Constants.BUYER_ID)
    @Expose
    private String buyer_id;

    public String getWalk_time() {
        return walk_time;
    }

    public String getWalk_date() {
        return walk_date;
    }

    @SerializedName(Constants.OFFER_STATUS)
    @Expose
    private String offer_status;

    @SerializedName(Constants.WALK_TIME)
    @Expose
    private String walk_time;

    @SerializedName(Constants.WALK_DATE)
    @Expose
    private String walk_date;

    @SerializedName(Constants.SPECILITY)
    @Expose
    private String buss_specility;


    public String getQuestionaire_status() {
        return questionaire_status;
    }

    @SerializedName(Constants.QUESTIONARIRE_STATUS)
    @Expose
    private String questionaire_status;

    @SerializedName(Constants.RENTER_TYPE)
    @Expose
    private String renter_type;

    public String getProperty_images() {
        return property_images;
    }

    public String getSignature_image() {
        return signature_image;
    }

    @SerializedName(Constants.NAME)
    @Expose
    private String name;

    @SerializedName(Constants.MOBILE)
    @Expose
    private String mobile;

    @SerializedName(Constants.PETS)
    @Expose
    private String pets;

    @SerializedName(Constants.ZIPCODE)
    @Expose
    private String zip_code;

    @SerializedName(Constants.AVAILABILITY)
    @Expose
    private String availability;

    public String getPets() {
        return pets;
    }

    public String getPets_details() {
        return pets_details;
    }

    public String getSmoking_allowed() {
        return smoking_allowed;
    }

    public String getSmoking_details() {
        return smoking_details;
    }

    public String getParking() {
        return parking;
    }

    public String getParking_details() {
        return parking_details;
    }

    @SerializedName(Constants.PETS_DETAIL)

    @Expose
    private String pets_details;

    @SerializedName(Constants.SMOKING)
    @Expose
    private String smoking_allowed;

    @SerializedName(Constants.SMOKING_DETAIL)
    @Expose
    private String smoking_details;

    @SerializedName(Constants.PARKING)
    @Expose
    private String parking;

    @SerializedName(Constants.PARKING_DETAIL)
    @Expose
    private String parking_details;

    @SerializedName(Constants.SIGNATURE_IMAGE)
    @Expose
    private String signature_image;

    @SerializedName(Constants.PROPERTY_IMGES)
    @Expose
    private String property_images;

    public String getTotal_client_rate() {
        return total_client_rate;
    }

    public String getSp_id() {
        return sp_id;
    }

    public String getBidder_rating() {
        return bidder_rating;
    }

    public ArrayList<RetroObject> getObject() {
        return object;
    }

    public String getClient_rating() {
        return client_rating;
    }

    @SerializedName(Constants.USER_ID)
    @Expose
    private String user_id;

    @SerializedName(Constants.SERVICE_PROVIDER_ID)
    @Expose
    private String sp_id;

    public String getRate_status() {
        return rate_status;
    }

    @SerializedName(Constants.BIDDER_RATTING)
    @Expose
    private String bidder_rating;

    public String getBalance() {
        return balance;
    }

    @SerializedName(Constants.RATE_STATUS)

    @Expose
    private String rate_status;

    @SerializedName(Constants.BALANCE)
    @Expose
    private String balance;

    public String getSocial_security_number() {
        return social_security_number;
    }

    @SerializedName(Constants.OBJECT)
    @Expose
    private ArrayList<RetroObject> object;

    @SerializedName(Constants.MILESTONE_DATA)
    @Expose
    private ArrayList<MilesTone> milestoneData;

    public String getProblem() {
        return problem;
    }

    @SerializedName(Constants.PROBLEM)
    @Expose
    private String problem;

    @SerializedName(Constants.SOCIAL_SECURITY_NUMBER)
    @Expose
    private String social_security_number;

    public String getUserid() {
        return userid;
    }

    @SerializedName(Constants.USERID)
    @Expose
    private String userid;

    public String getRequest_status() {
        return request_status;
    }

    public String getCreated_at() {
        return created_at;
    }

    public String getAccepted_at() {
        return accepted_at;
    }

    public String getCompleted_at() {
        return completed_at;
    }

    @SerializedName(Constants.REQUEST_STATUS)
    @Expose
    private String request_status;

    @SerializedName(Constants.CREATED_AT)
    @Expose
    private String created_at;

    @SerializedName(Constants.ACCEPT_AT)
    @Expose
    private String accepted_at;

    @SerializedName(Constants.COMPLETE_AT)
    @Expose
    private String completed_at;


    @SerializedName(Constants.CLINT_RATTING)
    @Expose
    private String client_rating;

    public String getProposal() {
        return proposal;
    }

    @SerializedName(Constants.IMAGE_ID)
    @Expose
    private String image_id;

    @SerializedName(Constants.PRAPOSAL)
    @Expose
    private String proposal;

    public String getWork_description() {
        return work_description;
    }

    @SerializedName(Constants.IMAGE_URL)

    @Expose
    private String image_url;

    @SerializedName(Constants.TOKEN)
    @Expose
    private String token;

    @SerializedName(Constants.WORK_DESCRIPTION)
    @Expose
    private String work_description;

    @SerializedName(Constants.USER_TYPE)
    @Expose
    private String userType;
    @SerializedName(Constants.PROPERTY_ID)
    @Expose
    private String property_id;

    @SerializedName(Constants.IS_ACTIVE)
    @Expose
    private String is_active;

    public String getVideo_url() {
        return video_url;
    }

    @SerializedName(Constants.VIDEOTHUMB)
    @Expose

    private String video_thumbnail;

    @SerializedName(Constants.VIDEO_URL)
    @Expose
    private String video_url;

    public UserInfo getUserinfo() {
        return userinfo;
    }

    @SerializedName(Constants.PROPERTY_DATA)

    @Expose
    private PropertyData property_data;

    @SerializedName(Constants.USER_DATA_RENT)
    @Expose
    private USER_DATA_RENT user_data;


    @SerializedName(Constants.USERINFO)
    @Expose
    private UserInfo userinfo;

    public ArrayList<MilesTone> getMilestoneData() {
        return milestoneData;
    }

    @SerializedName(Constants.FIXTURE_ARRAY)
    @Expose

    private Fixsure_Array fixsure_arr;



    public ProjectData getProjectData() {
        return projectData;
    }

    @SerializedName(Constants.PROJECT_DATA)
    @Expose
    ProjectData projectData;


    @SerializedName(Constants.VIDEO_DESC)
    @Expose
    private String open_house_desc;

    @SerializedName(Constants.FIRST_NAME)
    @Expose
    private String first_name;

    @SerializedName(Constants.LAST_NAME)
    @Expose
    private String last_name;

    @SerializedName(Constants.LIKE_STATUS)
    @Expose
    private String like_status;

    public String getStatus() {
        return status;
    }

    @SerializedName(Constants.STATUS)
    @Expose
    private String status;

    @SerializedName(Constants.SAVE_STATUE)
    @Expose
    private String save_status;

    @SerializedName(Constants.EMAIL)
    @Expose
    private String email;

    @SerializedName(Constants.CURRENCY)
    @Expose
    private String currency;

    @SerializedName(Constants.CURRENCY_SYMBOL)
    @Expose
    private String currency_symbol;

    public String getLike_status() {
        return like_status;
    }

    public String getSave_status() {
        return save_status;
    }

    @SerializedName(Constants.MOBILE_NUMBER)
    @Expose
    private String mobileNumber;

    @SerializedName(Constants.PROFILE_IMAGE)
    @Expose
    private String profileImage;

    @SerializedName(Constants.URL)
    @Expose
    private String url;

    @SerializedName(Constants.SELLER_FIRST_NAME)
    @Expose
    private String seller_firstname;

    @SerializedName(Constants.SELLER_LAST_NAME)
    @Expose
    private String seller_lastname;

    @SerializedName(Constants.SELLER_CONTACT)
    @Expose
    private String seller_contact;

    @SerializedName(Constants.LOCATION_LATITUDE)
    @Expose
    private String location_latitude;

    @SerializedName(Constants.LOCATION_LONGITUDE)
    @Expose
    private String location_longitude;

    @SerializedName(Constants.SELL_TYPE)
    @Expose
    private String sell_type;

    @SerializedName(Constants.PURPOSE)
    @Expose
    private String purpose;

    @SerializedName(Constants.CONFIRMATION)
    @Expose
    private String confirmation;

    @SerializedName(Constants.TYPE)
    @Expose
    private String type;

    @SerializedName(Constants.LOCATION)
    @Expose
    private String location;

    @SerializedName(Constants.POST)
    @Expose
    private String post;

    @SerializedName(Constants.FIXED_PRICE)
    @Expose
    private String fixed_price;

    @SerializedName(Constants.RENT_AMOUNT)
    @Expose
    private String rent_amount;

    @SerializedName(Constants.RENT_AMOUNT_USD)
    @Expose
    private String rent_amount_usd;

    @SerializedName(Constants.AMOUNT)
    @Expose
    private String amount;

    @SerializedName(Constants.CURRENT_MAX_BID)
    @Expose
    private String current_max_bid;

    @SerializedName(Constants.FAVOURITE_STATUS)
    @Expose
    private String favourite_status;

    @SerializedName(Constants.AUCTION_TYPE)
    @Expose
    private String auction_type;

    @SerializedName(Constants.START_DATE)
    @Expose
    private String start_date;

    @SerializedName(Constants.END_DATE)
    @Expose
    private String end_date;

    @SerializedName(Constants.MINIMUM_PRICE)
    @Expose
    private String minimum_price;

    @SerializedName(Constants.BADROOM)
    @Expose
    private String badroom;

    @SerializedName(Constants.BATHROOM)
    @Expose
    private String bathroom;

    @SerializedName(Constants.SOLDSTATUS)
    @Expose
    private String sold_status;

    @SerializedName(Constants.AREA)
    @Expose
    private String area_square;

    @SerializedName(Constants.VIDEO)
    @Expose
    private String vedio;

    @SerializedName(Constants.TRANSACTION_HISTORY)
    @Expose
    private String transaction_history;

    @SerializedName(Constants.DESCRIPTION)
    @Expose
    private String discription;

    @SerializedName(Constants.DESCRIPTION_ABOUT)
    @Expose
    private String description;

    @SerializedName(Constants.SOCIETY)
    @Expose
    private String society;

    @SerializedName(Constants.CITY)
    @Expose
    private String city;

    @SerializedName(Constants.DISCLOSER)
    @Expose
    private String discloser;

    @SerializedName(Constants.START_TIME)
    @Expose
    private String start_time;

    public String getRent_amount_usd() {
        return rent_amount_usd;
    }

    @SerializedName(Constants.NOTIFICATION_LIST_ARRAY)
    @Expose
    private ArrayList<NotificationArray> notification_list;


    @SerializedName(Constants.ADVERTIZEMENT_LIST_ARRAY)
    @Expose
    private ArrayList<AdvertismentList> advertisment_list;

    public int getBid_status() {
        return bid_status;
    }

    public int getMin_budget() {
        return min_budget;
    }

    public int getMax_budget() {
        return max_budget;
    }

    public ArrayList<NotificationArray> getNotification_list() {
        return notification_list;
    }

    public ArrayList<AdvertismentList> getAdvertisment_list() {
        return advertisment_list;
    }

    @SerializedName(Constants.END_TIME)
    @Expose

    private String end_time;

    @SerializedName(Constants.BID_STATUS)
    @Expose
    private int bidStatus;

    @SerializedName(Constants.POST_BID_STATUS)
    @Expose
    private int bid_status;

    @SerializedName(Constants.MIN_BUDGET)
    @Expose
    private int min_budget;

    @SerializedName(Constants.MAX_BUDGET)
    @Expose
    private int max_budget;


    @SerializedName(Constants.VIDEO_DATE)
    @Expose
    private String vedio_date;

    @SerializedName(Constants.VIDEO_TIME)
    @Expose
    private String vedio_time;

    @SerializedName(Constants.IS_CERTIFIED)
    @Expose
    private String is_cetified;

    @SerializedName(Constants.IS_SOLD)
    @Expose
    private String is_sold;

    @SerializedName(Constants.DISCLOSURE_ID)
    @Expose
    private String discloser_id;

    @SerializedName(Constants.VIDEOID)
    @Expose
    private String vedio_id;
    /* @SerializedName(Constants.FIXTURES)
     @Expose
     private String fixsure;*/
    @SerializedName(Constants.DISCLOSURE_DOC)
    @Expose
    private ArrayList<DisclosreDoc> discloser_files;

   /* public ArrayList<BidUserData> getBid_userData() {
        return bid_userData;
    }*/

    @SerializedName(Constants.PROPERTY_IMG)

    @Expose
    private ArrayList<PropertyImg> property_img;

  /*  @SerializedName(Constants.BIDUSERDATA)
    @Expose
    private ArrayList<BidUserData> bid_userData;*/

    @SerializedName(Constants.USERNAME_PRO)
    @Expose
    private String username;

    @SerializedName(Constants.WEBSITE)
    @Expose
    private String website;

    @SerializedName(Constants.BIO)
    @Expose
    private String bio;

    @SerializedName(Constants.PAGE)
    @Expose
    private String page;

    @SerializedName(Constants.CATEGORY)
    @Expose
    private String category;

    @SerializedName(Constants.GENDER)
    @Expose
    private String gender;

    @SerializedName(Constants.PROPERTY_TYPE)
    @Expose
    private String property_type;

    @SerializedName(Constants.ACTUAL_PRICE)
    @Expose
    private int actual_price;

    @SerializedName(Constants.TITLE_FEE)
    @Expose
    private int title_fee;


    @SerializedName(Constants.PROCESSING_FEE)
    @Expose

    private int processing_fee;

    @SerializedName(Constants.BIDAMOUNT)
    @Expose
    private String bid_amount;

    @SerializedName("agreement_file")
    @Expose
    private String agreement_file;

    @SerializedName(Constants.INITIAL_AMOUNT)
    @Expose
    private String initial_amount;

    @SerializedName(Constants.INITIAL_AMOUNT_USD)
    @Expose
    private String initial_amount_usd;

    @SerializedName(Constants.REMAINING_AMOUNT)
    @Expose
    private String remaining_amount;

    @SerializedName(Constants.PROPERTY_FOR)
    @Expose
    private String property_for;

    public ArrayList<RateReview> getRate_review() {
        return rate_review;
    }

    @SerializedName(Constants.RENTAL_PAYMENT)
    @Expose
    private ArrayList<RetroObject> rental_payment;

    @SerializedName(Constants.SELL_PAYMENT)
    @Expose
    private ArrayList<RetroObject> sell_payment;

    @SerializedName(Constants.RATE_REVIEW_ARRAY)
    @Expose
    private ArrayList<RateReview> rate_review;

    public String getBusiness_name() {
        return business_name;
    }

    @SerializedName(Constants.BUSINESS_NAME)
    @Expose
    private String business_name;

    @SerializedName(Constants.BUSINESS_TYPE)
    @Expose
    private String business_type;

    @SerializedName(Constants.BUSINESS_DESC)
    @Expose
    private String business_description;

    @SerializedName(Constants.STARTED_DESC)
    @Expose
    private String started_description;

    @SerializedName(Constants.SERVICE_OFFER)
    @Expose
    private String discounts_special_offers;

    @SerializedName(Constants.BUSINESS_LOGO)
    @Expose
    private String bussiness_logo;

    @SerializedName(Constants.REQUEST_LOCATION)
    @Expose
    private String request_location;

    @SerializedName(Constants.NOTIFICATION_PREFERENCE)
    @Expose
    private String notification_preference;

    @SerializedName(Constants.BILLING_ADDRESS)
    @Expose
    private String billling_address;

    @SerializedName(Constants.BILLING_STREET)
    @Expose
    private String billing_street;

    public String getLatitude() {
        return latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    @SerializedName(Constants.LATITUDE)
    @Expose
    private String latitude;

    @SerializedName(Constants.LONGITUDE)
    @Expose
    private String longitude;

    public String getWorking_hours() {
        return working_hours;
    }

    public String getPost_id() {
        return post_id;
    }

    public String getWorking_days() {
        return working_days;
    }

    @SerializedName(Constants.BILLING_ZIP)
    @Expose
    private String billling_zip;

    public String getDisclaimer_status() {
        return disclaimer_status;
    }

    @SerializedName(Constants.POST_ID)
    @Expose
    private String post_id;

    @SerializedName(Constants.DISCLAIMER_STATUS)
    @Expose
    private String disclaimer_status;

    public String getSub_cat_name() {
        return sub_cat_name;
    }

    public String getSub_cat_price() {
        return sub_cat_price;
    }

    @SerializedName(Constants.WORKING_HOURS)
    @Expose
    private String working_hours;

    @SerializedName(Constants.WORKING_DAYS)
    @Expose
    private String working_days;

    @SerializedName(Constants.SUB_CAT_NAME)
    @Expose
    private String sub_cat_name;

    public String getBusiness_service_id() {
        return business_service_id;
    }

    @SerializedName(Constants.BUSS_SERVICE_ID)
    @Expose

    private String business_service_id;

    public String getPay_status() {
        return pay_status;
    }

    @SerializedName(Constants.SUB_CAT_PRICE)
    @Expose
    private String sub_cat_price;

    public ArrayList<RetroObject> getFollowers() {
        return followers;
    }

    public ArrayList<RetroObject> getFollowing() {
        return following;
    }

    @SerializedName(Constants.PAY_STATUS)
    @Expose

    private String pay_status;

    @SerializedName(Constants.FOLLOWERS)
    @Expose
    private ArrayList<RetroObject> followers;

    @SerializedName(Constants.FOLLOWING)
    @Expose
    private ArrayList<RetroObject> following;

    public String getProperty_for() {
        return property_for;
    }

    public PropertyData getProperty_data() {
        return property_data;
    }

    public USER_DATA_RENT getUser_data() {
        return user_data;
    }

    public String getConfirmation() {
        return confirmation;
    }

    public String getRent_amount() {
        return rent_amount;
    }

    public ArrayList<RetroObject> getRental_payment() {
        return rental_payment;
    }

    public ArrayList<RetroObject> getSell_payment() {
        return sell_payment;
    }

    public String getProperty_id() {
        return property_id;
    }

    public String getOpen_house_desc() {
        return open_house_desc;
    }

    public String getVideo_thumbnail() {
        return video_thumbnail;
    }

    public Fixsure_Array getFixsure_arr() {
        return fixsure_arr;
    }

    public String getIs_cetified() {
        return is_cetified;
    }

    public String getAgreement_file() {
        return agreement_file;
    }

    public String getInitial_amount() {
        return initial_amount;
    }

    public String getInitial_amount_usd() {
        return initial_amount_usd;
    }

    public String getRemaining_amount() {
        return remaining_amount;
    }

    public String getFavourite_status() {
        return favourite_status;
    }

    public String getVedio_id() {
        return vedio_id;
    }

    public String getStart_time() {
        return start_time;
    }

    public String getEnd_time() {
        return end_time;
    }

    public String getVedio_date() {
        return vedio_date;
    }

    public String getVedio_time() {
        return vedio_time;
    }

    public String getDiscloser_id() {
        return discloser_id;
    }

    public String getIs_sold() {
        return is_sold;
    }

 /*   public String getFixsure() {
        return fixsure;
    }*/

    public String getUsername() {
        return username;
    }

    public String getWebsite() {
        return website;
    }

    public String getBio() {
        return bio;
    }

    public String getPage() {
        return page;
    }

    public String getCategory() {
        return category;
    }

    public String getGender() {
        return gender;
    }

    public ArrayList<DisclosreDoc> getDiscloser_files() {
        return discloser_files;
    }

    public String getProperty_type() {
        return property_type;
    }

    public int getActual_price() {
        return actual_price;
    }

    public int getTitle_fee() {
        return title_fee;
    }

    public int getProcessing_fee() {
        return processing_fee;
    }

    public String getBid_amount() {
        return bid_amount;
    }

    public ArrayList<PropertyImg> getProperty_img() {
        return property_img;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getUserType() {
        return userType;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }

    public String getIs_active() {
        return is_active;
    }

    public void setIs_active(String is_active) {
        this.is_active = is_active;
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

    public String getMobileNumber() {
        return mobileNumber;
    }

    public void setMobileNumber(String mobileNumber) {
        this.mobileNumber = mobileNumber;
    }

    public String getProfileImage() {
        return profileImage;
    }

    public void setProfileImage(String profileImage) {
        this.profileImage = profileImage;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public int getBidStatus() {
        return bidStatus;
    }

    public String getSeller_firstname() {
        return seller_firstname;
    }

    public void setSeller_firstname(String seller_firstname) {
        this.seller_firstname = seller_firstname;
    }

    public String getSeller_lastname() {
        return seller_lastname;
    }

    public void setSeller_lastname(String seller_lastname) {
        this.seller_lastname = seller_lastname;
    }

    public String getSeller_contact() {
        return seller_contact;
    }

    public void setSeller_contact(String seller_contact) {
        this.seller_contact = seller_contact;
    }

    public String getDiscription() {
        return discription;
    }

    public void setDiscription(String discription) {
        this.discription = discription;
    }

    public String getArea_square() {
        return area_square;
    }

    public void setArea_square(String area_square) {
        this.area_square = area_square;
    }

    public String getSold_status() {
        return sold_status;
    }

    public void setSold_status(String sold_status) {
        this.sold_status = sold_status;
    }

    public String getTransaction_history() {
        return transaction_history;
    }

    public void setTransaction_history(String transaction_history) {
        this.transaction_history = transaction_history;
    }

    public String getLocation_latitude() {
        return location_latitude;
    }

    public void setLocation_latitude(String location_latitude) {
        this.location_latitude = location_latitude;
    }

    public String getLocation_longitude() {
        return location_longitude;
    }

    public void setLocation_longitude(String location_longitude) {
        this.location_longitude = location_longitude;
    }

    public String getSell_type() {
        return sell_type;
    }

    public void setSell_type(String sell_type) {
        this.sell_type = sell_type;
    }

    public String getPurpose() {
        return purpose;
    }

    public void setPurpose(String purpose) {
        this.purpose = purpose;
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

    public String getPost() {
        return post;
    }

    public void setPost(String post) {
        this.post = post;
    }

    public String getFixed_price() {
        return fixed_price;
    }

    public void setFixed_price(String fixed_price) {
        this.fixed_price = fixed_price;
    }

    public String getAuction_type() {
        return auction_type;
    }

    public void setAuction_type(String auction_type) {
        this.auction_type = auction_type;
    }

    public String getStart_date() {
        return start_date;
    }

    public void setStart_date(String start_date) {
        this.start_date = start_date;
    }

    public String getEnd_date() {
        return end_date;
    }

    public void setEnd_date(String end_date) {
        this.end_date = end_date;
    }

    public String getBadroom() {
        return badroom;
    }

    public void setBadroom(String badroom) {
        this.badroom = badroom;
    }

    public String getBathroom() {
        return bathroom;
    }

    public void setBathroom(String bathroom) {
        this.bathroom = bathroom;
    }

    public String getLogin_type() {
        return login_type;
    }

    public void setLogin_type(String login_type) {
        this.login_type = login_type;
    }

    public String getVedio() {
        return vedio;
    }

    public void setVedio(String vedio) {
        this.vedio = vedio;
    }

    public String getMinimum_price() {
        return minimum_price;
    }

    public void setMinimum_price(String minimum_price) {
        this.minimum_price = minimum_price;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getSociety() {
        return society;
    }

    public void setSociety(String society) {
        this.society = society;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getDiscloser() {
        return discloser;
    }

    public String getZip_code() {
        return zip_code;
    }

    public String getAddress() {
        return address;

    }

    public void setDiscloser(String discloser) {
        this.discloser = discloser;
    }

    public String getCurrent_max_bid() {
        return current_max_bid;
    }

    public String getImage_id() {
        return image_id;
    }

    public String getImage_url() {
        return image_url;
    }

    public String getBusiness_type() {
        return business_type;
    }

    public String getBusiness_description() {
        return business_description;
    }

    public String getStarted_description() {
        return started_description;
    }

    public String getDiscounts_special_offers() {
        return discounts_special_offers;
    }

    public String getBuss_specility() {
        return buss_specility;
    }

    public String getAvailability() {
        return availability;
    }

    public String getEmail_verification() {
        return email_verification;
    }

    public String getBussiness_logo() {

        return bussiness_logo;
    }

    public String getRequest_location() {
        return request_location;
    }

    public String getNotification_preference() {
        return notification_preference;
    }

    public String getTotal_notification() {
        return total_notification;
    }

    public String getQuestion_id() {
        return question_id;
    }

    public String getAnswer() {
        return answer;
    }

    public String getCurrency() {
        return currency;
    }

    public String getAmount() {
        return amount;
    }

    public String getCurrency_symbol() {
        return currency_symbol;
    }
}
