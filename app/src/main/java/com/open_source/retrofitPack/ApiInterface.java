package com.open_source.retrofitPack;

import com.open_source.modal.RetrofitUserData;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface ApiInterface {

    @POST(Constants.LOGIN_URL)
    @FormUrlEncoded
    Call<RetrofitUserData> getLoginAPI(@Field(Constants.EMAIL) String email,
                                       @Field(Constants.PASSWORD) String password,
                                       @Field(Constants.DEVICE_ID) String deviceId,
                                       @Field(Constants.DEVICE_TYPE) String deviceType, @Field(Constants.USER_TYPE) String userType);

    @Multipart
    @POST(Constants.SIGNUP_URL)
    Call<RetrofitUserData> getSignUpAPI(@Part MultipartBody.Part profileImage,
                                        @Part(Constants.FIRST_NAME) RequestBody first_name,
                                        @Part(Constants.LAST_NAME) RequestBody last_name,
                                        @Part(Constants.USER_TYPE) RequestBody user_type,
                                        @Part(Constants.MOBILE_NUMBER) RequestBody mobileNumber,
                                        @Part(Constants.EMAIL) RequestBody email,
                                        @Part(Constants.PASSWORD) RequestBody password,
                                        @Part(Constants.DEVICE_ID) RequestBody deviceId,
                                        @Part(Constants.DEVICE_TYPE) RequestBody deviceType,
                                        @Part(Constants.AVAILABILITY) RequestBody availability,
                                        @Part(Constants.QUESTION_ID) RequestBody question_id,
                                        @Part(Constants.ANSWER) RequestBody answer);

    @POST(Constants.LOGOUT_URL)
    @FormUrlEncoded
    Call<RetrofitUserData> getLogoutAPI(@Header(Constants.TOKEN) String token,
                                        @Field(Constants.DEVICE_ID) String deviceId,
                                        @Field(Constants.DEVICE_TYPE) String deviceType);

    @Multipart
    @POST(Constants.UPDATE_PROFILE_URL)
    Call<RetrofitUserData> getUpdateProfileAPI(@Header(Constants.TOKEN) String token,
                                               @Part MultipartBody.Part profileImage,
                                               @Part(Constants.FIRST_NAME) RequestBody first_name,
                                               @Part(Constants.LAST_NAME) RequestBody last_name,
                                               @Part(Constants.MOBILE_NUMBER) RequestBody mobileNumber,
                                               @Part(Constants.EMAIL) RequestBody email,
                                               @Part(Constants.USERNAME_PRO) RequestBody username,
                                               @Part(Constants.WEBSITE) RequestBody website,
                                               @Part(Constants.BIO) RequestBody bio,
                                               @Part(Constants.PAGE) RequestBody page,
                                               @Part(Constants.CATEGORY) RequestBody category,
                                               @Part(Constants.GENDER) RequestBody gender,
                                               @Part(Constants.PROFILE_STATUS) RequestBody profile_status,
                                               @Part(Constants.BUSINESS_NAME) RequestBody business_name,
                                               @Part(Constants.AVAILABILITY) RequestBody availability,
                                               @Part(Constants.QUESTION_ID) RequestBody question_id,
                                               @Part(Constants.ANSWER) RequestBody answer);

    @GET(Constants.GET_PROFILE_URL)
    Call<RetrofitUserData> getProfileAPI(@Header(Constants.TOKEN) String token);

    @POST(Constants.CHANGE_PASSWORD_URL)
    @FormUrlEncoded
    Call<RetrofitUserData> getChangePasswordAPI(@Header(Constants.TOKEN) String token,
                                                @Field(Constants.OLD_PASSWORD) String oldPassword,
                                                @Field(Constants.NEW_PASSWORD) String newPassword);

    @POST(Constants.FORGET_PASSWORD_URL)
    @FormUrlEncoded
    Call<RetrofitUserData> getForgetPasswordAPI(@Field(Constants.EMAIL) String email);

    @POST(Constants.SOCIAL_LOGIN_URL)
    @FormUrlEncoded
    Call<RetrofitUserData> getSocialLoginAPI(@Field(Constants.FLAG) String flag,
                                             @Field(Constants.PROFILE_IMAGE) String profileImage,
                                             @Field(Constants.AUTH_ID) String authId,
                                             @Field(Constants.FIRST_NAME) String first_name,
                                             @Field(Constants.LAST_NAME) String last_name,
                                             @Field(Constants.EMAIL) String email,
                                             @Field(Constants.USER_TYPE) String user_type,
                                             @Field(Constants.DEVICE_ID) String deviceId,
                                             @Field(Constants.DEVICE_TYPE) String deviceType);

    @POST(Constants.NEAR_BY_SELL_URL)
    @FormUrlEncoded
    Call<RetrofitUserData> getNearBySellAPI(@Header(Constants.TOKEN) String token,
                                            @Field(Constants.LATITUDE) String latitude,
                                            @Field(Constants.LONGITUDE) String longitude,
                                            @Field(Constants.FILTER) String filter,
                                            @Field(Constants.RADIUS) String radius);

    @POST(Constants.PROPERTY_SEARCH_URL)
    @FormUrlEncoded
    Call<RetrofitUserData> getPropertySearchAPI(@Header(Constants.TOKEN) String token,
                                                @Field(Constants.LATITUDE) String latitude,
                                                @Field(Constants.LONGITUDE) String longitude,
                                                @Field(Constants.SEARCH_KEY) String search_key,
                                                @Field(Constants.RADIUS) String radius);

    @GET(Constants.FAVOURITE_LIST_URL)
    Call<RetrofitUserData> getFavouriteListAPI(@Header(Constants.TOKEN) String token);


    @POST(Constants.SELL_PROPERTY_URL)
    Call<RetrofitUserData> Upload_Property(@Header(Constants.TOKEN) String token,
                                           @Body RequestBody file);


    @POST(Constants.ADD_VIDEO)
    Call<RetrofitUserData> Upload_Video(@Header(Constants.TOKEN) String token, @Body RequestBody file);

    @POST(Constants.SELL_LIST)
    @FormUrlEncoded
    Call<RetrofitUserData> GetSellList(@Header(Constants.TOKEN) String token,
                                       @Field(Constants.PAGE) String page);


    @POST(Constants.MAKEOFFER_LIST)
    @FormUrlEncoded
    Call<RetrofitUserData> GetSellOfferList(@Header(Constants.TOKEN) String token,
                                            @Field(Constants.PAGE) String page);


    @POST(Constants.SELLDETAIL)
    @FormUrlEncoded
    Call<RetrofitUserData> SellDetail(@Header(Constants.TOKEN) String token,
                                      @Field(Constants.PROPERTYID) String id);


    @POST(Constants.USERLIST)
    @FormUrlEncoded
    Call<RetrofitUserData> USERLIST(@Header(Constants.TOKEN) String token,
                                    @Field(Constants.KEY) String key,
                                    @Field(Constants.PAGE) String page, @Field(Constants.USER_TYPE) String user_type);


    @POST(Constants.ADD_FAVOURITE_URL)
    @FormUrlEncoded
    Call<RetrofitUserData> getAddFavouriteAPI(@Header(Constants.TOKEN) String token,
                                              @Field(Constants.PROPERTY_ID) String property_id);


    @POST(Constants.CONTACT_US_URL)
    @FormUrlEncoded
    Call<RetrofitUserData> getContactUsAPI(@Header(Constants.TOKEN) String token,
                                           @Field(Constants.FIRST_NAME) String first_name,
                                           @Field(Constants.LAST_NAME) String last_name,
                                           @Field(Constants.EMAIL) String email,
                                           @Field(Constants.MESSAGE) String message);


    @GET(Constants.ABOUT_US_URL)
    Call<RetrofitUserData> getAboutUsAPI();


    @GET(Constants.PRIVACY_POLICY_URL)
    Call<RetrofitUserData> getPrivacyPolicyAPI();


    @POST(Constants.MAKE_OFFER__URL)
    @FormUrlEncoded
    Call<RetrofitUserData> getMakeOfferAPI(@Header(Constants.TOKEN) String token,
                                           @Field(Constants.PROPERTY_ID) String property_id,
                                           @Field(Constants.OFFER_PRICE) String offer_price);


    @POST(Constants.SELL_FILTER)
    @FormUrlEncoded
    Call<RetrofitUserData> getSellFilter(@Header(Constants.TOKEN) String token,
                                         @Field(Constants.PURPOSE) String purpose,
                                         @Field(Constants.PROPERTY_TYPE) String protype,
                                         @Field(Constants.BADROOM) String badroom,
                                         @Field(Constants.BATHROOM) String bathroom,
                                         @Field(Constants.LOCATION_LATITUDE) String lat,
                                         @Field(Constants.LOCATION_LONGITUDE) String lng,
                                         @Field(Constants.POST) String post,
                                         @Field(Constants.RENTER_TYPE) String renter_type,
                                         @Field(Constants.PROPERTY_FOR) String property_for,
                                         @Field(Constants.MIN_BUDGET) String max_budget,
                                         @Field(Constants.MAX_BUDGET) String min_budget,
                                         @Field(Constants.RADIUS) String radius);


    @POST(Constants.SEARCH_HISTORY)
    @FormUrlEncoded
    Call<RetrofitUserData> Search_History(@Header(Constants.TOKEN) String token,
                                          @Field(Constants.PAGE) String page);


    @POST(Constants.CHAT_LOAD)
    @FormUrlEncoded
    Call<RetrofitUserData> CHAT_History(@Header(Constants.TOKEN) String token,
                                        @Field(Constants.TO_ID) String to_user,
                                        @Field(Constants.CHAT_ID) String chat_id);


    @POST(Constants.SEND_MESSAGE)
    @FormUrlEncoded
    Call<RetrofitUserData> Send_Message(@Header(Constants.TOKEN) String token,
                                        @Field(Constants.TO_ID) String to_user,
                                        @Field(Constants.CHAT_STATUS) String status,
                                        @Field(Constants.PROPERTY_ID) String property_id,
                                        @Field(Constants.MESSAGE_TYPE) String message_type,
                                        @Field(Constants.MESSAGE) String message);


    @Multipart
    @POST(Constants.SEND_MESSAGE)
    Call<RetrofitUserData> Send_Chat_Message(@Header(Constants.TOKEN) String token,
                                             @Part MultipartBody.Part chatImage,
                                             @Part(Constants.TO_ID) RequestBody to_user,
                                             @Part(Constants.CHAT_STATUS) RequestBody chat_status,
                                             @Part(Constants.PROPERTY_ID) RequestBody property_id,
                                             @Part(Constants.MESSAGE_TYPE) RequestBody message_type);

    @POST(Constants.PROPERTY_LIST)
    @FormUrlEncoded
    Call<RetrofitUserData> GetPropertyList(@Header(Constants.TOKEN) String token,
                                           @Field(Constants.STATUS) String status);


    @GET(Constants.INBOX_LIST)
    Call<ResponseBody> GetLocalInboxlist(@Header(Constants.TOKEN) String token);


    @POST(Constants.CLEAR_CHAT)
    @FormUrlEncoded
    Call<RetrofitUserData> Clear_Chat(@Header(Constants.TOKEN) String token,
                                      @Field(Constants.TO_ID) String to_user);


    @POST(Constants.PROPERTY_BID)
    @FormUrlEncoded
    Call<RetrofitUserData> Fun_BID(@Header(Constants.TOKEN) String token,
                                   @Field(Constants.PROPERTY_ID) String pro_id,
                                   @Field(Constants.PROPERTY_AMOUNT) String property_amount);


    @POST(Constants.BIDING_LIST)
    @FormUrlEncoded
    Call<RetrofitUserData> Biding_list(@Header(Constants.TOKEN) String token,
                                       @Field(Constants.PROPERTY_ID) String pro_id);

    @POST(Constants.OFFER_LIST)
    @FormUrlEncoded
    Call<RetrofitUserData> Offer_list(@Header(Constants.TOKEN) String token,
                                      @Field(Constants.PROPERTY_ID) String pro_id, @Field(Constants.PAGE) String page);


    @POST(Constants.NOTIFICATION_LIST)
    @FormUrlEncoded
    Call<RetrofitUserData> Notification_List(@Header(Constants.TOKEN) String token,
                                             @Field(Constants.PAGE) String page);


    @POST(Constants.BUY_PROPERTY)
    @FormUrlEncoded
    Call<RetrofitUserData> Buy(@Header(Constants.TOKEN) String token,
                               @Field(Constants.TRANSECTION_ID) String transaction_id,
                               @Field(Constants.PROPERTY_ID) String property_id,
                               @Field(Constants.PAYMENT_METHOD) String method_type,
                               @Field(Constants.PROPERTY_AMOUNT) String intial_amount,
                               @Field(Constants.REMAINING_AMOUNT) String remaing_amount,
                               @Field(Constants.CLOSING_DATE) String closing_date,
                               @Field(Constants.AGREEMENT_ID) String agreement_id,
                               @Field(Constants.CONTRACT_ID) String contract_id);


    @GET(Constants.BUY_HISTORY)
    Call<RetrofitUserData> BUY_HISTORY(@Header(Constants.TOKEN) String token);


    @POST(Constants.BID_INVITATION)
    @FormUrlEncoded
    Call<RetrofitUserData> BID_INVITATION(@Header(Constants.TOKEN) String token,
                                          @Field(Constants.PROPERTY_ID) String pro_id,
                                          @Field(Constants.USER_ID) String user_id,
                                          @Field(Constants.TYPE) String type);


    @POST(Constants.UPDATESELL)
    Call<RetrofitUserData> Update_Property(@Header(Constants.TOKEN) String token,
                                           @Body RequestBody file);

    @Multipart
    @POST(Constants.DisclosureUpload)
    Call<RetrofitUserData> Disclosure_Upload(@Header(Constants.TOKEN) String token,
                                             @Part MultipartBody.Part file,
                                             @Part(Constants.NAME) RequestBody name,
                                             @Part(Constants.TYPE) RequestBody type);


    @POST(Constants.DisclosureDelete)
    @FormUrlEncoded
    Call<RetrofitUserData> DiSclosure_Delete(@Header(Constants.TOKEN) String token,
                                             @Field(Constants.DISCLOSURE_ID) String disclosure_id);

    @POST(Constants.DELETE_VIDEO)
    @FormUrlEncoded
    Call<RetrofitUserData> Delete_Video(@Header(Constants.TOKEN) String token,
                                        @Field(Constants.VIDEO_ID) String disclosure_id);

    @POST(Constants.RENTAL)
    Call<RetrofitUserData> RENTAL_APPLICANT(@Header(Constants.TOKEN) String token,
                                            @Body RequestBody file);


    @POST(Constants.PAYMENTCONFIRM)
    @FormUrlEncoded
    Call<RetrofitUserData> PAYMENT_CONFIRMATION(@Header(Constants.TOKEN) String token,
                                                @Field(Constants.PROPERTY_ID) String propert_id, @Field(Constants.BUY_TYPE) String buy_type);

    @POST(Constants.PAYMENT_CHECK)
    @FormUrlEncoded
    Call<RetrofitUserData> PAYMENT_CHECK(@Header(Constants.TOKEN) String token,
                                         @Field(Constants.PROPERTY_ID) String propert_id, @Field(Constants.PROPERTY_STATUS) String pro_status);


    @POST(Constants.RENTAL_PAY)
    @FormUrlEncoded
    Call<RetrofitUserData> RentPayment(@Header(Constants.TOKEN) String token, @Field(Constants.PROPERTY_ID) String property_id,
                                       @Field(Constants.TRANSECTION_ID) String transection_id, @Field(Constants.PAYMENT_METHOD) String payment_method, @Field(Constants.AMOUNT) String amount);


    @POST(Constants.RENTER_DETAIL)
    @FormUrlEncoded
    Call<RetrofitUserData> GetRentDetail(@Header(Constants.TOKEN) String token, @Field(Constants.REQUEST_ID) String request_id, @Field(Constants.PROPERTY_ID) String property_id);


    @POST(Constants.UPDATE_REQUEST)
    @FormUrlEncoded
    Call<RetrofitUserData> UpdateRequest(@Header(Constants.TOKEN) String token, @Field(Constants.REQUEST_ID) String request_id,
                                         @Field(Constants.PROPERTY_ID) String property_id, @Field(Constants.CONFIRMATION) String confirmation);

    @POST(Constants.YOUR_RENTAL_LIST)
    Call<RetrofitUserData> YourRenter(@Header(Constants.TOKEN) String token);


    @POST(Constants.RENTAL_LIST)
    Call<RetrofitUserData> Renter(@Header(Constants.TOKEN) String token);


    @POST(Constants.TOUR_REQUEST)
    @FormUrlEncoded
    Call<RetrofitUserData> TourRequest(@Header(Constants.TOKEN) String token, @Field(Constants.REQUEST_DATE) String request_date,
                                       @Field(Constants.REQUEST_TIME) String request_time, @Field(Constants.FIRST_NAME) String name,
                                       @Field(Constants.EMAIL) String email, @Field(Constants.MOBILE) String mobile,
                                       @Field(Constants.PROPERTY_ID) String property_id);

    @POST(Constants.SEND_NOTIFICATION)
    @FormUrlEncoded
    Call<RetrofitUserData> FunNotification(@Header(Constants.TOKEN) String token, @Field(Constants.PROPERTY_ID) String property_id);

    @POST(Constants.GET_ALL_NEWS_FEEDS)
    @FormUrlEncoded
    Call<RetrofitUserData> getNewFeed(@Header(Constants.TOKEN) String token,
                                      @Field(Constants.PAGE) String page,
                                      @Field(Constants.TYPE) String type,
                                      @Field(Constants.PURPOSE) String purpose,
                                      @Field(Constants.LATITUDE) String latitute,
                                      @Field(Constants.LONGITUDE) String longitute,
                                      @Field(Constants.RADIUS) String radius);


    @POST(Constants.FEED_LIKES)
    @FormUrlEncoded
    Call<RetrofitUserData> Feed_Likes(@Header(Constants.TOKEN) String token, @Field(Constants.PROPERTY_ID) String property_id, @Field(Constants.LIKE_STATUS) String like_status);


    @POST(Constants.FEED_SAVED)
    @FormUrlEncoded
    Call<RetrofitUserData> Feed_SAVED(@Header(Constants.TOKEN) String token, @Field(Constants.PROPERTY_ID) String property_id, @Field(Constants.SAVE_STATUE) String save_status);


    @POST(Constants.FEED_POST_ALLCOMMENT)
    @FormUrlEncoded
    Call<RetrofitUserData> getCommentAPI(@Header(Constants.TOKEN) String token, @Field(Constants.PROPERTY_ID) String property_id);

    @POST(Constants.FEED_POST_ALLLIKE)
    @FormUrlEncoded
    Call<RetrofitUserData> getLikeAPI(@Header(Constants.TOKEN) String token, @Field(Constants.PROPERTY_ID) String property_id);

    @GET(Constants.Get_ALL_SAVED)
    Call<RetrofitUserData> GetSavedFeed(@Header(Constants.TOKEN) String token);

    @POST(Constants.FEED_POST_COMMENT)
    @FormUrlEncoded
    Call<RetrofitUserData> PostComment(@Header(Constants.TOKEN) String token, @Field(Constants.PROPERTY_ID) String property_id, @Field(Constants.TYPE) String type, @Field(Constants.MESSAGE) String message);

    @Multipart
    @POST(Constants.SP_SIGNUP)
    Call<RetrofitUserData> getSignUpSP(@Part MultipartBody.Part profileImage,
                                       @Part MultipartBody.Part business_logo,
                                       @Part MultipartBody.Part id_proof,
                                       @Part(Constants.ID_PROOF) RequestBody str_id_proof,
                                       @Part(Constants.FIRST_NAME) RequestBody first_name,
                                       @Part(Constants.LAST_NAME) RequestBody last_name,
                                       @Part(Constants.USER_TYPE) RequestBody user_type,
                                       @Part(Constants.MOBILE_NUMBER) RequestBody mobileNumber,
                                       @Part(Constants.EMAIL) RequestBody email,
                                       @Part(Constants.PASSWORD) RequestBody password,
                                       @Part(Constants.DEVICE_ID) RequestBody deviceId,
                                       @Part(Constants.DEVICE_TYPE) RequestBody deviceType,
                                       @Part(Constants.BUSS_SERVICE_ID) RequestBody buss_name,
                                       @Part(Constants.BUSINESS_TYPE) RequestBody buss_type,
                                       @Part(Constants.BUSINESS_SINCE) RequestBody buss_since,
                                       @Part(Constants.BUSINESS_DESC) RequestBody buss_desc,
                                       @Part(Constants.STARTED_DESC) RequestBody buss_start,
                                       @Part(Constants.SPECILITY) RequestBody buss_spec,
                                       @Part(Constants.REQUEST_LOCATION) RequestBody buss_req_dist,
                                       @Part(Constants.NOTIFICATION_PREFERENCE) RequestBody noti_pref,
                                       @Part(Constants.ADDRESS) RequestBody address,
                                       @Part(Constants.LATITUDE) RequestBody latitute,
                                       @Part(Constants.LONGITUDE) RequestBody lng,
                                       @Part(Constants.ZIPCODE) RequestBody zip_code,
                                       @Part(Constants.AVAILABILITY) RequestBody availability);

    @Multipart
    @POST(Constants.getUpdateProfileUrl)
    Call<RetrofitUserData> UpdateProfile(@Header(Constants.TOKEN) String token, @Part MultipartBody.Part profileImage,
                                         @Part MultipartBody.Part business_logo,
                                         @Part MultipartBody.Part id_proof,
                                         @Part(Constants.ID_PROOF) RequestBody str_id_proof,
                                         @Part(Constants.FIRST_NAME) RequestBody first_name,
                                         @Part(Constants.LAST_NAME) RequestBody last_name,
                                         @Part(Constants.MOBILE_NUMBER) RequestBody mobileNumber,
                                         @Part(Constants.EMAIL) RequestBody email,
                                         @Part(Constants.BUSS_SERVICE_ID) RequestBody buss_name,
                                         @Part(Constants.BUSINESS_TYPE) RequestBody buss_type,
                                         @Part(Constants.BUSINESS_SINCE) RequestBody buss_since,
                                         @Part(Constants.BUSINESS_DESC) RequestBody buss_desc,
                                         @Part(Constants.STARTED_DESC) RequestBody buss_start,
                                         @Part(Constants.SPECILITY) RequestBody buss_spec,
                                         @Part(Constants.REQUEST_LOCATION) RequestBody buss_req_dist,
                                         @Part(Constants.NOTIFICATION_PREFERENCE) RequestBody noti_pref,
                                         @Part(Constants.ADDRESS) RequestBody address,
                                         @Part(Constants.LATITUDE) RequestBody latitute,
                                         @Part(Constants.LONGITUDE) RequestBody lng,
                                         @Part(Constants.ZIPCODE) RequestBody zip_code, @Part(Constants.AVAILABILITY) RequestBody availability);


    @GET(Constants.SP_SERVICE)
    Call<RetrofitUserData> GetAllService(@Header(Constants.TOKEN) String token);


    @Multipart
    @POST(Constants.Upload_Image)
    Call<RetrofitUserData> UploadVideo(@Part MultipartBody.Part profileImage,
                                       @Header(Constants.TOKEN) String token, @Part(Constants.TYPE) String type);

    @Multipart
    @POST(Constants.Upload_Image)
    Call<RetrofitUserData> UploadImage(@Part MultipartBody.Part profileImage, @Header(Constants.TOKEN) String token);

    @POST(Constants.Delete_Image)
    @FormUrlEncoded
    Call<RetrofitUserData> DeleteImage(@Header(Constants.TOKEN) String token, @Field(Constants.IMAGE_ID) String image_id);


    @POST(Constants.ADD_SERVICE)
    @FormUrlEncoded
    Call<RetrofitUserData> UploadWork(@Header(Constants.TOKEN) String token,
                                      @Field(Constants.SERVICE_ID) String catogery_id,
                                      @Field(Constants.SUB_CAT_NAME) String sub_cat_name,
                                      @Field(Constants.SUB_CAT_PRICE) String sab_cat_price,
                                      @Field(Constants.WORKING_DAYS) String working_days,
                                      @Field(Constants.WORKING_HOURS) String working_hours,
                                      @Field(Constants.WORK_DESCRIPTION) String work_description,
                                      @Field(Constants.IMAGES) String Images);

    @POST(Constants.SP_DETAIL)
    @FormUrlEncoded
    Call<RetrofitUserData> SPDetail(@Header(Constants.TOKEN) String token,
                                    @Field(Constants.SERVICE_PROVIDER_ID) String sp_id,
                                    @Field(Constants.SERVICE_ID) String service_id);


    @POST(Constants.SP_LIST_BY_SERVICE_ID)
    @FormUrlEncoded
    Call<RetrofitUserData> SP_List_By_Service_id(@Header(Constants.TOKEN) String token,
                                                 @Field(Constants.SERVICE_ID) String user_id, @Field(Constants.LATITUDE) String lat, @Field(Constants.LONGITUDE) String longi);


    @POST(Constants.SP_Service_Apply)
    @FormUrlEncoded
    Call<RetrofitUserData> SP_Service_Apply(@Header(Constants.TOKEN) String token, @Field(Constants.SERVICE_DATE) String date,
                                            @Field(Constants.ADDRESS) String address, @Field(Constants.PINCODE) String pincode,
                                            @Field(Constants.USER_ID) String user_id, @Field(Constants.PROBLEM) String problem,
                                            @Field(Constants.PROBLEM_IMAGE) String problem_image);


    @GET(Constants.SP_WORK_List)
    Call<RetrofitUserData> GetMyWorkList(@Header(Constants.TOKEN) String token);


    @GET(Constants.SP_ALL_REQUEST)
    Call<RetrofitUserData> GetSPRequest(@Header(Constants.TOKEN) String token);


    @POST(Constants.SP_USER_REQUEST)
    Call<RetrofitUserData> GetUserRequest(@Header(Constants.TOKEN) String token);


    @POST(Constants.SP_ACCEPT_REQUEST)
    @FormUrlEncoded
    Call<RetrofitUserData> SPAcceptRequest(@Header(Constants.TOKEN) String token,
                                           @Field(Constants.REQUEST_ID) String request_id,
                                           @Field(Constants.REQUEST_STATUS) String request_status);


    @POST(Constants.SP_RATTING_REVIEW)
    @FormUrlEncoded
    Call<RetrofitUserData> FunRatting(@Header(Constants.TOKEN) String token,
                                      @Field(Constants.SERVICE_PROVIDER_ID) String sp_id,
                                      @Field(Constants.REQUEST_ID) String request_id,
                                      @Field(Constants.STATUS) String status,
                                      @Field(Constants.RATE) String rate,
                                      @Field(Constants.COMMENT) String comment);

    @POST(Constants.SPPayment)
    @FormUrlEncoded
    Call<RetrofitUserData> FunPayment(@Header(Constants.TOKEN) String token,
                                      @Field(Constants.TRANSECTION_ID) String transection_id,
                                      @Field(Constants.PAYMENT_METHOD) String payment_method,
                                      @Field(Constants.AMOUNT) String amount);

    @POST(Constants.UserAddPost)
    @FormUrlEncoded
    Call<RetrofitUserData> FunAddPost(@Header(Constants.TOKEN) String token,
                                      @Field(Constants.CATEGORY) String catogery,
                                      @Field(Constants.MIN_BUDGET) String min_budget,
                                      @Field(Constants.MAX_BUDGET) String max_budget,
                                      @Field(Constants.LOCATION) String location,
                                      @Field(Constants.LATITUDE) String latitude,
                                      @Field(Constants.LONGITUDE) String longitude,
                                      @Field(Constants.DESCRIPTION_ABOUT) String description,
                                      @Field(Constants.FILES) String files,
                                      @Field(Constants.SERVICE_DATE) String service_date
            , @Field(Constants.VIDEO_ID) String video_id);


    @GET(Constants.SP_SERVICE_LIST)
    Call<RetrofitUserData> GetServicePost(@Header(Constants.TOKEN) String token);

    @POST(Constants.SP_POST_LIST_DETAIL)
    @FormUrlEncoded
    Call<RetrofitUserData> GetServicePostDetail(@Header(Constants.TOKEN) String token,
                                                @Field(Constants.ID) String id);


    @POST(Constants.SP_BID_POST)
    @FormUrlEncoded
    Call<RetrofitUserData> FunPostBid(@Header(Constants.TOKEN) String token, @Field(Constants.PRAPOSAL) String praposal,
                                      @Field(Constants.AMOUNT) String amount,
                                      @Field(Constants.POST_ID) String post_id,
                                      @Field(Constants.DAYS) String days);

    @GET(Constants.USER_MY_POST)
    Call<RetrofitUserData> GetMyPost(@Header(Constants.TOKEN) String token);

    @POST(Constants.USER_MY_POST_BIDDER)
    @FormUrlEncoded
    Call<RetrofitUserData> GetMyPostBidder(@Header(Constants.TOKEN) String token, @Field(Constants.POST_ID) String post_id);

    @POST(Constants.USER_MY_POST_BIDDER_DETAIL)
    @FormUrlEncoded
    Call<RetrofitUserData> GetMyPostBidderDetail(@Header(Constants.TOKEN) String token,
                                                 @Field(Constants.POST_ID) String post_id,
                                                 @Field(Constants.USERID) String userid);


    @POST(Constants.USER_HIRE_SP)
    @FormUrlEncoded
    Call<RetrofitUserData> FunSpHire(@Header(Constants.TOKEN) String token,
                                     @Field(Constants.POST_ID) String post_id,
                                     @Field(Constants.USERID) String userid);


    //1=awarded , 2=running , 3=completed
    @POST(Constants.SP_AWARTED)
    @FormUrlEncoded
    Call<RetrofitUserData> FunSpAwarted(@Header(Constants.TOKEN) String token,
                                        @Field(Constants.PROJECT_STATUS) String project_status);

    //1=awarded , 2=running , 3=completed
    @POST(Constants.USER_POST_STATUS)
    @FormUrlEncoded
    Call<RetrofitUserData> FunUserAwarted(@Header(Constants.TOKEN) String token,
                                          @Field(Constants.PROJECT_STATUS) String project_status);


    @POST(Constants.SP_AWARTED_STATUS)
    @FormUrlEncoded
    Call<RetrofitUserData> SP_AWARTED_STATUS(@Header(Constants.TOKEN) String token,
                                             @Field(Constants.POST_ID) String post_id,
                                             @Field(Constants.STATUS) String status);

    @POST(Constants.SP_REQUEST_MILESTONE)
    @FormUrlEncoded
    Call<RetrofitUserData> SP_REQUEST_MILESTONE(@Header(Constants.TOKEN) String token,
                                                @Field(Constants.POST_ID) String post_id,
                                                @Field(Constants.AMOUNT) String amount,
                                                @Field(Constants.DESCRIPTION_ABOUT) String description,
                                                @Field(Constants.USERID) String userid,
                                                @Field(Constants.MILESTONE_TYPE) String milestone_type);


    @POST(Constants.PROJECT_DESCRIPTION)
    @FormUrlEncoded
    Call<RetrofitUserData> GetProjectDetail(@Header(Constants.TOKEN) String token, @Field(Constants.POST_ID) String post_id);


    @GET(Constants.SP_POST_BID)
    Call<RetrofitUserData> SpPostBid(@Header(Constants.TOKEN) String token);


    @POST(Constants.CLOSE_PROJECT)
    @FormUrlEncoded
    Call<RetrofitUserData> FunClose(@Header(Constants.TOKEN) String token, @Field(Constants.POST_ID) String post_id);


    @POST(Constants.MILESTONE_PAYMENT)
    @FormUrlEncoded
    Call<RetrofitUserData> FunMilestonePay(@Header(Constants.TOKEN) String token,
                                           @Field(Constants.POST_ID) String post_id,
                                           @Field(Constants.SERVICE_PROVIDER_ID) String sp_id,
                                           @Field(Constants.MILESTONE_ID) String milestone_id,
                                           @Field(Constants.PAYMENT_METHOD) String payment_method,
                                           @Field(Constants.AMOUNT) String amount,
                                           @Field(Constants.TRANSECTION_ID) String transection_id,
                                           @Field(Constants.PAYMENT_FOR) String payment_for);


    @POST(Constants.GET_SP_REQUEST_DETAIL)
    @FormUrlEncoded
    Call<RetrofitUserData> FunGetRequestData(@Header(Constants.TOKEN) String token,
                                             @Field(Constants.REQUEST_ID) String request_id);

    @POST(Constants.RentDisclaimer)
    Call<RetrofitUserData> RentDisclaimer(@Header(Constants.TOKEN) String token,
                                          @Body RequestBody file);


    @POST(Constants.GetRentDisclaimer)
    @FormUrlEncoded
    Call<RetrofitUserData> GetRentDisclaimer(@Header(Constants.TOKEN) String token,
                                             @Field(Constants.REQUEST_ID) String request_id);

    @POST(Constants.DISCLAIMERAPPROVAL)
    @FormUrlEncoded
    Call<RetrofitUserData> FunApprove(@Header(Constants.TOKEN) String token,
                                      @Field(Constants.REQUEST_ID) String request_id, @Field(Constants.STATUS) String status);


    @POST(Constants.INVESTOR_QUES)
    Call<RetrofitUserData> InvestorQues(@Header(Constants.TOKEN) String token, @Body RequestBody file);


    @POST(Constants.UserProfileDetail)
    @FormUrlEncoded
    Call<RetrofitUserData> ViewUserProfile(@Header(Constants.TOKEN) String token,
                                           @Field(Constants.USER_ID) String user_id);

    @GET(Constants.DOCUMENTS_LIST)
    Call<RetrofitUserData> GetDocList(@Header(Constants.TOKEN) String token);

    @Multipart
    @POST(Constants.UploadProDoc)
    Call<RetrofitUserData> UploadProDoc(@Header(Constants.TOKEN) String token,
                                        @Part MultipartBody.Part file,
                                        @Part(Constants.NAME) RequestBody name,
                                        @Part(Constants.PROPERTY_ID) RequestBody property_id,
                                        @Part(Constants.BUYER_ID) RequestBody buyer_id);

    @POST(Constants.GetProDoc)
    @FormUrlEncoded
    Call<RetrofitUserData> GetProDoc(@Header(Constants.TOKEN) String token,
                                     @Field(Constants.PROPERTY_ID) String property_id);

    @POST(Constants.FOLLOW_REQUEST)
    @FormUrlEncoded
    Call<RetrofitUserData> FollowReq(@Header(Constants.TOKEN) String token,
                                     @Field(Constants.USERID) String userid);

    @GET(Constants.FOLLOWERS_REQUEST_LIST)
    Call<RetrofitUserData> GetReqFollow(@Header(Constants.TOKEN) String token);


    @POST(Constants.FOLLOW_ACCEPT_REJECT)
    @FormUrlEncoded
    Call<RetrofitUserData> FollowReqAccept(@Header(Constants.TOKEN) String token,
                                           @Field(Constants.FOLLOWER_ID) String follower_id,
                                           @Field(Constants.STATUS) String status);


    @POST(Constants.Advertizement)
    Call<RetrofitUserData> Advertizement(@Header(Constants.TOKEN) String token);

    @POST(Constants.All_Offer)
    @FormUrlEncoded
    Call<RetrofitUserData> AllOffer(@Header(Constants.TOKEN) String token, @Field(Constants.PROPERTY_ID) String property_id,
                                    @Field(Constants.USER_ID) String user_id,
                                    @Field(Constants.PAGE) String page);

    @POST(Constants.Accept_Offer)
    @FormUrlEncoded
    Call<RetrofitUserData> Accept_Offer(@Header(Constants.TOKEN) String token, @Field(Constants.PROPERTY_ID) String property_id,
                                        @Field(Constants.OFFER_ID) String offer_id, @Field(Constants.OFFER_STATUS) String status);

    @POST(Constants.COUNTER_OFFER)
    @FormUrlEncoded
    Call<RetrofitUserData> CounterOffer(@Header(Constants.TOKEN) String token, @Field(Constants.PROPERTY_ID) String property_id,
                                        @Field(Constants.OFFER_ID) String offer_id,
                                        @Field(Constants.USER_ID) String user_id,
                                        @Field(Constants.OFFER_AMOUNT) String offer_amount);


    @POST(Constants.BUYER_ALL_OFFER)
    Call<RetrofitUserData> GetAllOffer(@Header(Constants.TOKEN) String token);


    @POST(Constants.Walk_Through_detail)
    @FormUrlEncoded
    Call<RetrofitUserData> WalkDetail(@Header(Constants.TOKEN) String token,
                                      @Field(Constants.PROPERTYID) String property_id,
                                      @Field(Constants.Walk_Through_Id) String walk_through_id);

    @POST(Constants.Hs_Agree)
    @FormUrlEncoded
    Call<RetrofitUserData> Fun_Agree(@Header(Constants.TOKEN) String token,
                                     @Field(Constants.Walk_Through_Id) String walk_through_id);


    @POST(Constants.Hs_DisAgree)
    @FormUrlEncoded
    Call<RetrofitUserData> HS_Disagree(@Header(Constants.TOKEN) String token,
                                       @Field(Constants.DATE) String date,
                                       @Field(Constants.TIME) String time,
                                       @Field(Constants.Walk_Through_Id) String walk_through_id,
                                       @Field(Constants.PROPERTY_ID) String property_id,
                                       @Field(Constants.REASON) String reason);

    @GET(Constants.Task_list)
    Call<RetrofitUserData> GetTaskList(@Header(Constants.TOKEN) String token);


    @POST(Constants.Submit_cal)
    @FormUrlEncoded
    Call<RetrofitUserData> SubmitCal(@Header(Constants.TOKEN) String token,
                                     @Field(Constants.TASK_ID) String task_ids,
                                     @Field(Constants.Task_Date) String task_dates,
                                     @Field(Constants.PROPERTY_ID) String property_id,
                                     @Field(Constants.OFFER_ID) String offer_id,
                                     @Field(Constants.CALENDAR_TYPE) String calendar_type);

    @POST(Constants.GetCalTask)
    @FormUrlEncoded
    Call<RetrofitUserData> GetCalTaks(@Header(Constants.TOKEN) String token,
                                      @Field(Constants.PROPERTYID) String property_id);


    @POST(Constants.FavFilter)
    @FormUrlEncoded
    Call<RetrofitUserData> GetFavFilter(@Header(Constants.TOKEN) String token,
                                        @Field(Constants.PROPERTY_TYPE) String protype,
                                        @Field(Constants.PURPOSE) String purpose,
                                        @Field(Constants.LOCATION_LATITUDE) String lat,
                                        @Field(Constants.LOCATION_LONGITUDE) String lng,
                                        @Field(Constants.BADROOM) String badroom,
                                        @Field(Constants.BATHROOM) String bathroom,
                                        @Field(Constants.RADIUS) String radius);


    @POST(Constants.Add_Mutti_image)
    Call<RetrofitUserData> UploadMultiImage(@Header(Constants.TOKEN) String token, @Body RequestBody file);


    @POST(Constants.RESEND_EMAIL)
    @FormUrlEncoded
    Call<RetrofitUserData> ResendEmail(@Header(Constants.TOKEN) String token, @Field(Constants.EMAIL) String email);


    @GET(Constants.UNREAD_NOTI)
    Call<RetrofitUserData> UnreadNoti(@Header(Constants.TOKEN) String token);

    @POST(Constants.READ_NOTIFICATION)
    @FormUrlEncoded
    Call<RetrofitUserData> ReadNoti(@Header(Constants.TOKEN) String token,
                                    @Field(Constants.NOTI_TYPE) String noti_type);


    @GET(Constants.QUESTION_lIST)
    Call<RetrofitUserData> QUESTION_lIST();

    @GET(Constants.CURRENCY_LIST_URL)
    Call<RetrofitUserData> getCurrencyList();


    @GET(Constants.FAQ_LIST)
    Call<RetrofitUserData> FaqList(@Header(Constants.TOKEN) String token);

    @POST(Constants.MAKE_OFFER__URL)
    @FormUrlEncoded
    Call<RetrofitUserData> PlaidRequest(@Header(Constants.TOKEN) String token,
                                        @Field(Constants.PROPERTY_ID) String property_id,
                                        @Field(Constants.OFFER_PRICE) String offer_price,
                                        @Field(Constants.PAYMENT_METHOD) String payment_method,
                                        @Field(Constants.TRANSECTION_ID) String transaction_id,
                                        @Field(Constants.ACCOUNT_ID) String account_id,
                                        @Field(Constants.PUBLIC_TOKEN) String public_token,
                                        @Field(Constants.TOKEN) String stripe_token);
    @POST(Constants.UPDATE_CURRENCY_URL)
    @FormUrlEncoded
    Call<RetrofitUserData> getUpdateCurrencyAPI(@Header(Constants.TOKEN) String token,
                                            @Field(Constants.CODE) String code);
}