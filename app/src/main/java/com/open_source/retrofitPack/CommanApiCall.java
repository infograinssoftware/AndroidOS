package com.open_source.retrofitPack;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.open_source.Interface.SecurityQuestion;
import com.open_source.Listener.Onpaymentrecieve;
import com.open_source.ServiceProvider.SPSignUpActivity;
import com.open_source.activity.EditProfileActivity;
import com.open_source.activity.PaymentConfirmation;
import com.open_source.activity.RentFormSecond;
import com.open_source.activity.SignUpActivity;
import com.open_source.activity.WelcomeActivity;
import com.open_source.modal.RetrofitUserData;
import com.open_source.util.SharedPref;
import com.open_source.util.Utility;
import com.stripe.android.Stripe;
import com.stripe.android.TokenCallback;
import com.stripe.android.model.Card;
import com.stripe.android.model.Token;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CommanApiCall {
    private static final String TAG = "Utility";

    public static void GetStripToken(final Context context, String cardNumber, int cardExpMonth, int cardExpYear, String cardCVC, String amount, final String type) {
        Card card = new Card(cardNumber, cardExpMonth, cardExpYear, cardCVC);
        card.validateNumber();
        card.validateCVC();
        Stripe stripe = new Stripe(context, Constants.STRIPEAPIKEY);
        stripe.createToken(card,
                new TokenCallback() {
                    public void onSuccess(Token token) {
                        // Log.e(TAG, "========" + token.toString());
                        String strip_token = token.getId();
                        Log.e(TAG, "========" + strip_token);
                        Onpaymentrecieve onpaymentrecieve = null;
                        if (type.equals("buy")) {
                            onpaymentrecieve = (PaymentConfirmation) context;
                        } else if (type.equals("rent_form")) {
                            onpaymentrecieve = (RentFormSecond) context;
                        } else if (type.equals("sp")) {
                            onpaymentrecieve = (SPSignUpActivity) context;
                        }
                        onpaymentrecieve.onrecieve(strip_token);
                    }

                    public void onError(Exception error) {
                        Utility.ShowToastMessage(context, error.getMessage());
                    }
                }
        );
    }

    public static void LoadQuestionData(final Context context, final String type) {
        if (Utility.isConnectingToInternet(context)) {
            RetrofitClient.getAPIService().QUESTION_lIST().enqueue(new Callback<RetrofitUserData>() {
                @Override
                public void onResponse(Call<RetrofitUserData> call, Response<RetrofitUserData> response) {
                    try {
                        if (response.body().getStatus() == 200) {
                            SecurityQuestion securityQuestion = null;
                            if (type.equals("signup")) {
                                securityQuestion = (SignUpActivity) context;
                            } else if (type.equals("getprofile")) {
                                securityQuestion = (EditProfileActivity) context;
                            }
                            securityQuestion.onrecieve(response.body().getObject());

                        } else if (response.body().getStatus() == 401) {
                            SharedPref.clearPreference(context);
                            context.startActivity(new Intent(context, WelcomeActivity.class)
                                    .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK));
                        } else {
                            Utility.ShowToastMessage(context, response.body().getMessage());
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(Call<RetrofitUserData> call, Throwable t) {
                    //Log.e(TAG, "onFailure: " + t.toString());
                }
            });
        } else {

        }
    }
}
