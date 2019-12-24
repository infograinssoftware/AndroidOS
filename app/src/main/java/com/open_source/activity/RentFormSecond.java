package com.open_source.activity;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import androidx.annotation.Nullable;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.open_source.Listener.Onpaymentrecieve;
import com.open_source.R;
import com.open_source.modal.RetrofitUserData;
import com.open_source.progressHud.ProgressHUD;
import com.open_source.retrofitPack.CommanApiCall;
import com.open_source.retrofitPack.Constants;
import com.open_source.retrofitPack.Payment;
import com.open_source.retrofitPack.RetrofitClient;
import com.open_source.util.PhoneNumberTextWatcher;
import com.open_source.util.SharedPref;
import com.open_source.util.Utility;
import com.paypal.android.sdk.payments.PayPalConfiguration;
import com.paypal.android.sdk.payments.PayPalPayment;
import com.paypal.android.sdk.payments.PayPalService;
import com.paypal.android.sdk.payments.PaymentActivity;
import com.stripe.android.model.Card;
import com.stripe.android.view.CardMultilineWidget;

import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import fr.ganfra.materialspinner.MaterialSpinner;
import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RentFormSecond extends BaseActivity implements View.OnClickListener , Onpaymentrecieve {
    public static final int PAYPAL_REQUEST_CODE = 101;
    private static final String TAG = RentFormSecond.class.getSimpleName();
    private Context context;
    private CheckBox checkbox;
    private String str_bedroom = "", str_type = "", method_type = "",cardExpiry="";
    private MaterialSpinner spn_type, spn_bedroom;
    private LinearLayout layout_start_date, layout_end_date;
    private EditText sqarefeed, rent_amount, stree_address, city, state, zip, pref_first_name, pref_first_realtion, pref_first_email,
            pref_first_contact, pref_second_name, pref_second_realtion, pref_second_email, pref_second_contact, pref_third_name,
            pref_third_realtion, pref_third_email, pref_third_contact, additional_detail, pre_land_name, pre_land_email, pre_land_phone, pre_land_address;
    private TextView start_date, end_date;
    private ProgressHUD progressHUD;
    private String MobilePattern = "[0-9]{10}";
    private LinearLayout lay_credit;
    private Button btn_paypal,btn_credit,pay_paynow;
    private Dialog dialog;
    private ImageView cross;
    private Card cardToSave;
    private CardMultilineWidget cardMultilineWidget;
    private PayPalConfiguration config = new PayPalConfiguration().environment(PayPalConfiguration.ENVIRONMENT_NO_NETWORK).clientId(Constants.PAYPAL_CLIENT_ID);


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_rent_second);
        context = RentFormSecond.this;
        init();
    }

    private void init() {
        spn_type = findViewById(R.id.spn_property_type);
        spn_bedroom = findViewById(R.id.spn_bedroom);
        sqarefeed = findViewById(R.id.sqare_feed);
        rent_amount = findViewById(R.id.rent_amount);
        stree_address = findViewById(R.id.street_address);
        city = findViewById(R.id.city);
        state = findViewById(R.id.state);
        zip = findViewById(R.id.zip);
        layout_start_date = findViewById(R.id.layout_start_date);
        layout_end_date = findViewById(R.id.layout_end_date);
        start_date = findViewById(R.id.start_date);
        end_date = findViewById(R.id.end_date);
        pref_first_name = findViewById(R.id.pref_first_name);
        pref_first_realtion = findViewById(R.id.pref_first_relation);
        pref_first_email = findViewById(R.id.pref_first_email);
        pref_first_contact = findViewById(R.id.pref_first_contact);
        pref_first_contact.addTextChangedListener(new PhoneNumberTextWatcher(pref_first_contact));
        pref_second_name = findViewById(R.id.pref_second_name);
        pref_second_realtion = findViewById(R.id.pref_second_relation);
        pref_second_contact = findViewById(R.id.pref_second_phone);
        pref_second_contact.addTextChangedListener(new PhoneNumberTextWatcher(pref_second_contact));
        pref_second_email = findViewById(R.id.pref_second_email);
        pref_third_name = findViewById(R.id.pref_third_name);
        pref_third_contact = findViewById(R.id.pref_third_contact);
        pref_third_contact.addTextChangedListener(new PhoneNumberTextWatcher(pref_third_contact));
        pref_third_email = findViewById(R.id.pref_third_email);
        pref_third_realtion = findViewById(R.id.pref_third_relation);
        pre_land_name = findViewById(R.id.pre_land_name);
        pre_land_email = findViewById(R.id.pre_land_email);
        pre_land_address = findViewById(R.id.pre_land_address);
        pre_land_phone = findViewById(R.id.pre_land_phone);
        pre_land_phone.addTextChangedListener(new PhoneNumberTextWatcher(pre_land_phone));
        additional_detail = findViewById(R.id.additional_detail);
        checkbox = findViewById(R.id.checkbox);
        start_date.setOnClickListener(this);
        end_date.setOnClickListener(this);
        ((Button) findViewById(R.id.btn_next)).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_next:
                if (spn_type.getSelectedItem() != null) {
                    if (spn_type.getSelectedItem().toString().equalsIgnoreCase("Type")) {
                        str_type = "";
                    } else {
                        str_type = spn_type.getSelectedItem().toString();
                    }
                }
                if (spn_bedroom.getSelectedItem() != null) {
                    if (spn_bedroom.getSelectedItem().toString().equalsIgnoreCase("Select No. of bathrooms")) {
                        str_bedroom = "";
                    } else {
                        str_bedroom = spn_bedroom.getSelectedItem().toString();
                    }
                }
                if (!zip.getText().toString().isEmpty() && zip.getText().toString().length() < 6)
                    Utility.ShowToastMessage(context, getString(R.string.zip_code_validation));
                else if (pre_land_phone.getText().toString().isEmpty() && pre_land_phone.getText().toString().length()<12)
                    Utility.ShowToastMessage(context, getString(R.string.val_landload_contact));
                else if (!pre_land_email.getText().toString().isEmpty() && !Utility.isValidEmail(pre_land_email.getText().toString()))
                    Utility.ShowToastMessage(context, getString(R.string.val_landloard_email));
                else if (pref_first_contact.getText().toString().isEmpty() && pref_first_contact.getText().toString().length()<10)
                    Utility.ShowToastMessage(context, R.string.msg_error_contact_length);
                else if (pref_second_contact.getText().toString().isEmpty() && pref_second_contact.getText().toString().length()<10)
                    Utility.ShowToastMessage(context, R.string.msg_error_contact_length);
                else if (pref_third_contact.getText().toString().isEmpty() && pref_third_contact.getText().toString().length()<10)
                    Utility.ShowToastMessage(context, R.string.msg_error_contact_length);
                else if (!pref_first_email.getText().toString().isEmpty() && !Utility.isValidEmail(pref_first_email.getText().toString()))
                    Utility.ShowToastMessage(context, getString(R.string.val_rent_reference_email));
                else if (!pref_second_email.getText().toString().isEmpty() && !Utility.isValidEmail(pref_second_email.getText().toString()))
                    Utility.ShowToastMessage(context, getString(R.string.val_rent_ref_email));
                else if (!pref_third_email.getText().toString().isEmpty() && !Utility.isValidEmail(pref_third_email.getText().toString()))
                    Utility.ShowToastMessage(context, getString(R.string.val_rent_ref_third));
                else
                    FunPay();
                break;

            case R.id.start_date:
                PickDate("start_date");
                break;

            case R.id.end_date:
                PickDate("end_date");
                break;

            case R.id.btn_paypal:
                dialog.dismiss();
                method_type = Payment.PAYPAL;
                config = config.acceptCreditCards(false);
                PayPalPayment payment = new PayPalPayment(new BigDecimal(Payment.RENT_AMOUNT), "USD",
                        "Total Amount", PayPalPayment.PAYMENT_INTENT_SALE);
                Intent intent = new Intent(context, com.paypal.android.sdk.payments.PaymentActivity.class);
                intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, config);
                intent.putExtra(PaymentActivity.EXTRA_PAYMENT, payment);
                Log.e(TAG, "Intent=========================> " + intent);
                startActivityForResult(intent, PAYPAL_REQUEST_CODE);
                break;

            case R.id.cross:
                dialog.dismiss();
                break;

            case R.id.btn_credit:
                dialog.dismiss();
                startActivity(new Intent(context,StripeWeb.class));
                //lay_credit.setVisibility(View.VISIBLE);
                break;

            case R.id.paynow:
                if (lay_credit.getVisibility() == View.VISIBLE) {
                    cardToSave = cardMultilineWidget.getCard();
                    if (cardToSave == null) {
                        Utility.ShowToastMessage(context, getString(R.string.invalidata));
                    } else {
                        if (cardToSave.getNumber().equals("")) {
                            Utility.ShowToastMessage(context, getString(R.string.card_number));
                        } else if (cardToSave.getExpMonth() == 0) {
                            Utility.ShowToastMessage(context, getString(R.string.cardexpirymonth));
                        } else if (cardToSave.getExpYear() == 0) {
                            Utility.ShowToastMessage(context, getString(R.string.cardyear));
                        } else if (cardToSave.getCVC().isEmpty()) {
                            Utility.ShowToastMessage(context, getString(R.string.cradcvv));
                        } else {
                            cardExpiry = cardToSave.getExpMonth() + "/" + cardToSave.getExpYear();
                            CommanApiCall.GetStripToken(context,cardMultilineWidget.getCard().getNumber().toString(),
                                    cardMultilineWidget.getCard().getExpMonth(), cardMultilineWidget.getCard().getExpYear(),
                                    cardMultilineWidget.getCard().getCVC().toString(),Payment.RENT_AMOUNT,"rent_form");
                        }
                    }
                }
                break;

        }
    }

    private void FunPay() {
        dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(true);
        dialog.setContentView(R.layout.dialog_payment);
        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
        Window window = dialog.getWindow();
        layoutParams.copyFrom(window.getAttributes());
        layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;
        layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
        layoutParams.gravity = Gravity.CENTER;
        window.setAttributes(layoutParams);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(context.getResources().getColor(R.color.transparent)));
        cardMultilineWidget = (CardMultilineWidget)dialog.findViewById(R.id.card_multiline_widget);
        lay_credit = dialog.findViewById(R.id.lay_credit);
        btn_paypal = dialog.findViewById(R.id.btn_paypal);
        btn_credit = dialog.findViewById(R.id.btn_credit);
        pay_paynow=dialog.findViewById(R.id.paynow);
        cross = dialog.findViewById(R.id.cross);
        btn_paypal.setOnClickListener(this);
        btn_credit.setOnClickListener(this);
        pay_paynow.setOnClickListener(this);
        cross.setOnClickListener(this);
        dialog.show();
    }

    private void Fun_Upload(String transection_id) {
        if (Utility.isConnectingToInternet(context)) {
            progressHUD = ProgressHUD.show(context, true, false, null);
            MultipartBody.Builder builder = new MultipartBody.Builder();
            builder.setType(MultipartBody.FORM);
            builder.addFormDataPart(Constants.APP_FULL_NAME, getIntent().getExtras().getString(Constants.APP_FULL_NAME, ""));
            builder.addFormDataPart(Constants.TRANSECTION_ID, transection_id);
//            builder.addFormDataPart(Constants.PROPERTY_FEE, "$"+Payment.RENT_AMOUNT);
            builder.addFormDataPart(Constants.PROPERTY_FEE, Payment.RENT_AMOUNT);
            builder.addFormDataPart(Constants.PROPERTY_TYPE, getIntent().getExtras().getString(Constants.PROPERTY_TYPE, ""));
            builder.addFormDataPart(Constants.PREF_LOCATION, getIntent().getExtras().getString(Constants.PREF_LOCATION, ""));
            builder.addFormDataPart(Constants.MAX_BUDGET, getIntent().getExtras().getString(Constants.MAX_BUDGET, ""));
            builder.addFormDataPart(Constants.PREF_LAT, getIntent().getExtras().getString(Constants.PREF_LAT, ""));
            builder.addFormDataPart(Constants.PREF_LONGI, getIntent().getExtras().getString(Constants.PREF_LONGI, ""));
            builder.addFormDataPart(Constants.APP_PHONE, getIntent().getExtras().getString(Constants.APP_PHONE, "").replace("-", ""));
            builder.addFormDataPart(Constants.APP_DOB, getIntent().getExtras().getString(Constants.APP_DOB, ""));
            builder.addFormDataPart(Constants.APP_DRIVER_LICENCE, getIntent().getExtras().getString(Constants.APP_DRIVER_LICENCE, ""));
            builder.addFormDataPart(Constants.APP_OCCUPATION, getIntent().getExtras().getString(Constants.APP_OCCUPATION, ""));
            builder.addFormDataPart(Constants.APP_OCCUPATION_DETAIL, getIntent().getExtras().getString(Constants.APP_OCCUPATION_DETAIL, ""));
            builder.addFormDataPart(Constants.APP_PETS, getIntent().getExtras().getString(Constants.APP_PETS, ""));
            builder.addFormDataPart(Constants.APP_PETS_DETAIL, getIntent().getExtras().getString(Constants.APP_PETS_DETAIL, ""));
            builder.addFormDataPart(Constants.APP_VEHICLES, getIntent().getExtras().getString(Constants.APP_VEHICLES, ""));
            builder.addFormDataPart(Constants.APP_VEHICLES_DETAIL, getIntent().getExtras().getString(Constants.APP_VEHICLES_DETAIL, ""));
            builder.addFormDataPart(Constants.APP_CRIME, getIntent().getExtras().getString(Constants.APP_CRIME, ""));
            builder.addFormDataPart(Constants.APP_CRIME_DETAIL, getIntent().getExtras().getString(Constants.APP_CRIME_DETAIL, ""));
            builder.addFormDataPart(Constants.APP_BANKRUPTCY, getIntent().getExtras().getString(Constants.APP_BANKRUPTCY, ""));
            builder.addFormDataPart(Constants.APP_BANKRUPTCY_DETAIL, getIntent().getExtras().getString(Constants.APP_BANKRUPTCY_DETAIL, ""));
            builder.addFormDataPart(Constants.APP_EVICTED, getIntent().getExtras().getString(Constants.APP_EVICTED, ""));
            builder.addFormDataPart(Constants.APP_EVICTED_DETAIL, getIntent().getExtras().getString(Constants.APP_EVICTED_DETAIL, ""));
            builder.addFormDataPart(Constants.CURR_EMP_COMP, getIntent().getExtras().getString(Constants.CURR_EMP_COMP, ""));
            builder.addFormDataPart(Constants.CURR_EMP_OCCUP, getIntent().getExtras().getString(Constants.CURR_EMP_OCCUP, ""));
            builder.addFormDataPart(Constants.CURR_EMP_HOW_LONG, getIntent().getExtras().getString(Constants.CURR_EMP_HOW_LONG, ""));
            builder.addFormDataPart(Constants.CURR_EMP_INCOME, getIntent().getExtras().getString(Constants.CURR_EMP_INCOME, ""));
            builder.addFormDataPart(Constants.CURR_EMP_ADDRESS, getIntent().getExtras().getString(Constants.CURR_EMP_ADDRESS, ""));
            builder.addFormDataPart(Constants.CURR_EMP_STATE, getIntent().getExtras().getString(Constants.CURR_EMP_STATE, ""));
            builder.addFormDataPart(Constants.CURR_EMP_CITY, getIntent().getExtras().getString(Constants.CURR_EMP_CITY, ""));
            builder.addFormDataPart(Constants.CURR_EMP_ZIP, getIntent().getExtras().getString(Constants.CURR_EMP_ZIP, ""));
            builder.addFormDataPart(Constants.PREV_EMP_COMP, getIntent().getExtras().getString(Constants.PREV_EMP_COMP, ""));
            builder.addFormDataPart(Constants.PREV_EMP_OCCUP, getIntent().getExtras().getString(Constants.PREV_EMP_OCCUP, ""));
            builder.addFormDataPart(Constants.PREV_EMP_HOW_LONG, getIntent().getExtras().getString(Constants.PREV_EMP_HOW_LONG, ""));
            builder.addFormDataPart(Constants.PREV_EMP_INCOME, getIntent().getExtras().getString(Constants.PREV_EMP_INCOME, ""));
            builder.addFormDataPart(Constants.PREV_EMP_ADDRESS, getIntent().getExtras().getString(Constants.PREV_EMP_ADDRESS, ""));
            builder.addFormDataPart(Constants.PREV_EMP_STATE, getIntent().getExtras().getString(Constants.PREV_EMP_STATE, ""));
            builder.addFormDataPart(Constants.PREV_EMP_CITY, getIntent().getExtras().getString(Constants.PREV_EMP_CITY, ""));
            builder.addFormDataPart(Constants.PREV_EMP_ZIP, getIntent().getExtras().getString(Constants.PREV_EMP_ZIP, ""));
            builder.addFormDataPart(Constants.CURR_LAND_NAME, getIntent().getExtras().getString(Constants.CURR_LAND_NAME, ""));
            builder.addFormDataPart(Constants.CURR_LAND_ADDRESS, getIntent().getExtras().getString(Constants.CURR_LAND_ADDRESS, ""));
            builder.addFormDataPart(Constants.CURR_LAND_PHONE, getIntent().getExtras().getString(Constants.CURR_LAND_PHONE, "").replace("-","" ));
            builder.addFormDataPart(Constants.CURR_LAND_EMAIL, getIntent().getExtras().getString(Constants.CURR_LAND_EMAIL, ""));
            builder.addFormDataPart(Constants.CURR_RES_TYPE, getIntent().getExtras().getString(Constants.CURR_RES_TYPE, ""));
            builder.addFormDataPart(Constants.CURR_RES_AREA, getIntent().getExtras().getString(Constants.CURR_RES_AREA, ""));
            builder.addFormDataPart(Constants.CURR_RES_BEDROOM, getIntent().getExtras().getString(Constants.CURR_RES_BEDROOM, ""));
            builder.addFormDataPart(Constants.CURR_RES_RENT, getIntent().getExtras().getString(Constants.CURR_RES_RENT, ""));
            builder.addFormDataPart(Constants.CURR_RES_ADDRESS, getIntent().getExtras().getString(Constants.CURR_RES_ADDRESS, ""));
            builder.addFormDataPart(Constants.CURR_RES_CITY, getIntent().getExtras().getString(Constants.CURR_RES_CITY, ""));
            builder.addFormDataPart(Constants.CURR_RES_STATE, getIntent().getExtras().getString(Constants.CURR_RES_STATE, ""));
            builder.addFormDataPart(Constants.CURR_RES_ZIP, getIntent().getExtras().getString(Constants.CURR_RES_ZIP, ""));
            builder.addFormDataPart(Constants.CURR_RES_HOW_LONG, getIntent().getExtras().getString(Constants.CURR_RES_HOW_LONG, ""));
            builder.addFormDataPart(Constants.CURR_RES_EXP_DATE, getIntent().getExtras().getString(Constants.CURR_RES_EXP_DATE, ""));

            builder.addFormDataPart(Constants.PREF_NAME_FIRST, pref_first_name.getText().toString());
            builder.addFormDataPart(Constants.PREF_EMIAL_FIRST, pref_first_email.getText().toString());
            builder.addFormDataPart(Constants.PREF_PHONE_FIRST, pref_first_contact.getText().toString().replace("-","" ));
            builder.addFormDataPart(Constants.PREF_RELATION_FIRST, pref_first_realtion.getText().toString());
            builder.addFormDataPart(Constants.PREF_NAME_SECOND, pref_second_name.getText().toString());
            builder.addFormDataPart(Constants.PREF_EMIAL_SECOND, pref_second_email.getText().toString());
            builder.addFormDataPart(Constants.PREF_PHONE_SECOND, pref_second_contact.getText().toString().replace("-","" ));
            builder.addFormDataPart(Constants.PREF_RELATION_SECOND, pref_second_realtion.getText().toString());
            builder.addFormDataPart(Constants.PREF_NAME_THIRD, pref_third_name.getText().toString());
            builder.addFormDataPart(Constants.PREF_EMIAL_THIRD, pref_third_email.getText().toString());
            builder.addFormDataPart(Constants.PREF_PHONE_THIRD, pref_third_contact.getText().toString().replace("-","" ));
            builder.addFormDataPart(Constants.PREF_RELATION_THIRD, pref_third_realtion.getText().toString());

            builder.addFormDataPart(Constants.PREV_LAND_NAME, pre_land_name.getText().toString());
            builder.addFormDataPart(Constants.PREV_LAND_ADDRESS, pre_land_address.getText().toString());
            builder.addFormDataPart(Constants.PREV_LAND_PHONE, pre_land_phone.getText().toString().replace("-","" ));
            builder.addFormDataPart(Constants.PREV_LAND_EMAIL, pre_land_email.getText().toString());

            builder.addFormDataPart(Constants.ADDITIONAL_DETAIL, additional_detail.getText().toString());

            builder.addFormDataPart(Constants.PREV_RES_TYPE, str_type);
            builder.addFormDataPart(Constants.PREV_RES_AREA, sqarefeed.getText().toString());
            builder.addFormDataPart(Constants.PREV_RES_BEDROOM, str_bedroom);
            builder.addFormDataPart(Constants.PREV_RES_RENT, rent_amount.getText().toString());
            builder.addFormDataPart(Constants.PREV_RES_ADDRESS, stree_address.getText().toString());
            builder.addFormDataPart(Constants.PREV_RES_CITY, city.getText().toString());
            builder.addFormDataPart(Constants.PREV_RES_STATE, state.getText().toString());
            builder.addFormDataPart(Constants.PREV_RES_ZIP, zip.getText().toString());
            builder.addFormDataPart(Constants.PREV_RES_STARTDATE, start_date.getText().toString());
            builder.addFormDataPart(Constants.CURR_RES_ENDDATE, end_date.getText().toString());
            builder.addFormDataPart(Constants.OWNER_ID, getIntent().getExtras().getString(Constants.USER_ID));
            builder.addFormDataPart(Constants.PROPERTY_ID,getIntent().getExtras().getString(Constants.PROPERTY_ID,""));
            MultipartBody requestBody = builder.build();
            RetrofitClient.getAPIService().RENTAL_APPLICANT(SharedPref.getSharedPreferences(context, Constants.TOKEN), requestBody).enqueue(new Callback<RetrofitUserData>() {
                @Override
                public void onResponse(Call<RetrofitUserData> call, Response<RetrofitUserData> response) {
                    if (progressHUD != null && progressHUD.isShowing()) {
                        progressHUD.dismiss();
                    }
                    try {
                        if (response.body().getStatus() == 200) {
                            startActivity(new Intent(context, ConfirmPaymentMessage.class).
                                    putExtra(Constants.MESSAGE, "success").putExtra(Constants.TYPE,"user"));
                            finishAffinity();

                        } else if (response.body().getStatus() == 401) {
                            SharedPref.clearPreference(context);
                            startActivity(new Intent(context, WelcomeActivity.class)
                                    .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK));
                            finish();
                        } else {
                            Utility.ShowToastMessage(context, response.body().getMessage());
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(Call<RetrofitUserData> call, Throwable t) {
                    if (progressHUD != null && progressHUD.isShowing()) {
                        progressHUD.dismiss();
                    }
                }
            });
        } else {
            Utility.ShowToastMessage(context, getString(R.string.internet_connection));
        }

    }

    private void PickDate(final String type) {
        final Calendar c = Calendar.getInstance();
        int mYear = c.get(Calendar.YEAR);
        int mMonth = c.get(Calendar.MONTH);
        int mDay = c.get(Calendar.DAY_OF_MONTH);
        DatePickerDialog datePickerDialog = new DatePickerDialog(context,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        String formatted_date = "";
                        formatted_date = fun_dateFormat(dayOfMonth + "-" + (monthOfYear + 1) + "-" + year);
                        if (type.equalsIgnoreCase("start_date"))
                            start_date.setText(formatted_date);
                        else if (type.equalsIgnoreCase("end_date"))
                            end_date.setText(formatted_date);
                    }
                }, mYear, mMonth, mDay);
        datePickerDialog.show();
    }

    private String fun_dateFormat(String date) {
        String formatted_date = "";
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
        try {
            Date d = dateFormat.parse(date);
            formatted_date = dateFormat.format(d);
            if (formatted_date.isEmpty()) {
                formatted_date = date;
            }
            //System.out.println("=====date"+d);
            //System.out.println("======Formated"+);
        } catch (Exception e) {
            formatted_date = date;
            //java.text.ParseException: Unparseable date: Geting error
            System.out.println("Excep" + e);
        }
        return formatted_date;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PAYPAL_REQUEST_CODE) {
            if (resultCode == AppCompatActivity.RESULT_OK) {
                com.paypal.android.sdk.payments.PaymentConfirmation confirm = data.getParcelableExtra(PaymentActivity.EXTRA_RESULT_CONFIRMATION);
                if (confirm != null) {
                    try {
                        String paymentDetails = confirm.toJSONObject().toString(4);
                        Log.i("=====transaction_id", paymentDetails);
                        JSONObject jsonObject = new JSONObject(paymentDetails);
                        String transaction_id = jsonObject.getJSONObject("response").getString("id");
                        Log.e(TAG, "====transaction_id: " + transaction_id);
                        Fun_Upload(transaction_id);

                    } catch (JSONException e) {
                        Log.e("=======paymentExample", getString(R.string.paypla_failure), e);
                    }
                }
            } else if (resultCode == AppCompatActivity.RESULT_CANCELED) {
                Log.i("=======paymentExample", getString(R.string.paypal_cancelled));
                startActivity(new Intent(context, ConfirmPaymentMessage.class).putExtra(Constants.MESSAGE, "failed").putExtra(Constants.TYPE,"user"));
            } else if (resultCode == PaymentActivity.RESULT_EXTRAS_INVALID) {
                Log.i("=======paymentExample", getString(R.string.invalid_paypal_detail));

            }
        }
    }

    @Override
    public void onrecieve(String token) {
        if (dialog!=null && dialog.isShowing())
        dialog.dismiss();
        //Fun_Upload(token);

    }
}
