package com.open_source.services;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.util.Log;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.open_source.LiveFeeds.FeedCommentsActivity;
import com.open_source.LiveFeeds.FeedLikeActivity;
import com.open_source.R;
import com.open_source.SQLiteHelper.DbHandler;
import com.open_source.ServiceProvider.ServiceProviderHome;
import com.open_source.activity.BidListActivity;
import com.open_source.activity.ChatActivity;
import com.open_source.activity.LocationInfoActivity;
import com.open_source.activity.MyOfferList;
import com.open_source.fragment.NotificationFragment;
import com.open_source.modal.ChatMsgModel;
import com.open_source.retrofitPack.Constants;
import com.open_source.util.SharedPref;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;

public class MyFirebaseMessagingService extends FirebaseMessagingService {
    private static final String TAG = MyFirebaseMessagingService.class.getSimpleName();
    private static final String CHANNEL_OFFER_ID = "com.open_source.OFFER";
    private static final String CHANNEL_OFFER_NAME = "Offer";
    private static final String CHANNEL_CHAT_ID = "com.open_source.CHAT";
    private static final String CHANNEL_CHAT_NAME = "Chat";
    private static final String CHANNEL_BID_ID = "com.open_source.BID";
    private static final String CHANNEL_BID_NAME = "Bid";
    private static final String CHANNEL_Winner_ID = "com.open_source.Winner";
    private static final String CHANNEL_Winner_NAME = "Winner";
    private static final String CHANNEL_Invite_ID = "com.open_source.Invite";
    private static final String CHANNEL_Invite_NAME = "Invite";
    private static final String CHANNEL_initalpayment_ID = "com.open_source.payment";
    private static final String CHANNEL_initalpayment_NAME = "payment";
    private final int appIconAlpha = R.drawable.noti_icon;
    private final int appIconNoAlpha = R.drawable.noti_icon;
    NotificationManager notificationManager;

    @Override
    public void onNewToken(String s) {
        super.onNewToken(s);
        FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener(new OnSuccessListener<InstanceIdResult>() {
            @Override
            public void onSuccess(InstanceIdResult instanceIdResult) {
                SharedPref.setSharedPreference(getApplicationContext(), Constants.FIREBASE_TOKEN, instanceIdResult.getToken());
            }
        });
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        if (remoteMessage.getData().size() > 0) {
            //chat=1, offer=2 , bid=3 ,
            // Biding property winner=4 ,
            // Private property invitation=5 ,
            // paymentclosingsate=6,rent_request=7,
            //RentApproved=8 ,RequestScheduleTour=9,NewProperty added=10 , like post=11 , comment on post=12
            //milestone request  send sp to user =14(user)
            //award service by user to sp =15(sp)
            // accept award project by sp =16(user)
            // payment recieve by sp =17(sp)
            //post bid =18(user)
            //sp to user milestone request(direct hiring milestone request)=19
            //user pay to sp(direct hiring pay)=20

            // Message data payload:{msg=New doc., notitype=22}
            //{msg=New doc., notitype=24}
            //notitype=25 for  follow request
            //notitype=26 for accept follow request

            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                setupChannels();
            }
            Log.d(TAG, "Message data payload:" + remoteMessage.getData());
            Map<String, String> notification = remoteMessage.getData();
            try {
                if (notification.get("notitype").equals("1")) {
                    Bundle bundle = new Bundle();
                    for (Map.Entry<String, String> entry : notification.entrySet()) {
                        bundle.putString(entry.getKey(), entry.getValue());
                    }
                    if (notification.get("messageType").equals("image")) {
                        ChatNotification("image", bundle, CHANNEL_CHAT_ID);
                    } else if (notification.get("messageType").equals("text")) {
                        ChatNotification(notification.get(Constants.MESSAGE), bundle, CHANNEL_CHAT_ID);
                    }
                } else if (notification.get("notitype").equals("2")) {
                    String[] array = notification.get("propertyImg").split(",");
                    Bitmap bitmap = getBitmapfromUrl(notification.get("url")+array[0]);
                    OfferNotification(notification.get("property_id"), notification.get("notification"), "", bitmap, CHANNEL_OFFER_ID, "offer");
                } else if (notification.get("notitype").equals("3")) {
                    String[] array = notification.get("propertyImg").split(",");
                    Bitmap bitmap = getBitmapfromUrl(notification.get("property_url") + "" + array[0]);
                    OfferNotification(notification.get("property_id"), notification.get("notification"), "", bitmap, CHANNEL_BID_ID, "bid");
                } else if (notification.get("notitype").equals("4")) {
                    winnerNotification(notification.get("property_id"), notification.get("msg"), CHANNEL_Winner_ID, "4");
                } else if (notification.get("notitype").equals("5")) {
                    InviteNotification(notification.get("property_id"), notification.get("msg"), CHANNEL_Invite_ID, "5");
                } else if (notification.get("notitype").equals("6")) {
                    winnerNotification(notification.get("property_id"), notification.get("msg"), CHANNEL_initalpayment_ID, "6");
                } else if (notification.get("notitype").equals("7")) {
                    InviteNotification(notification.get("property_id"), notification.get("msg"), CHANNEL_initalpayment_ID, "7");
                    /*    {msg=New rent request arrived., request_id=2, property_id=2, notitype=7}*/
                } else if (notification.get("notitype").equals("8")) {
                    InviteNotification(notification.get("property_id"), notification.get("msg"), CHANNEL_initalpayment_ID, "8");
                    /*    {msg=New rent request arrived., request_id=2, property_id=2, notitype=7}*/
                } else if (notification.get("notitype").equals("9")) {
                    InviteNotification("", notification.get("msg"), CHANNEL_initalpayment_ID, "9");
                    /*    {msg=New rent request arrived., request_id=2, property_id=2, notitype=7}*/
                } else if (notification.get("notitype").equals("10")) {
                    String url = notification.get("property_url");
                    url = url.replaceAll(" ", "%20");
                    Bitmap bitmap = getBitmapfromUrl(url);
                    OfferNotification(notification.get("property_id"), notification.get("msg"), notification.get("location"), bitmap, CHANNEL_initalpayment_ID, "propertyadd");

                    /*    {msg=New rent request arrived., request_id=2, property_id=2, notitype=7}*/
                } else if (notification.get("notitype").equals("11")) {
                    InviteNotification(notification.get("property_id"), notification.get("msg"), CHANNEL_initalpayment_ID, "11");
                } else if (notification.get("notitype").equals("12")) {
                    FeedCommentNotification(notification.get("property_id"), notification.get("msg"), CHANNEL_initalpayment_ID);
                } else if (notification.get("notitype").equals("13")) {
                    winnerNotification("", notification.get("msg"), CHANNEL_Winner_ID, notification.get("notitype"));
                } else if (notification.get("notitype").equals("14") ||
                        notification.get("notitype").equals("15") ||
                        notification.get("notitype").equals("16") ||
                        notification.get("notitype").equals("17") ||
                        notification.get("notitype").equals("18") ||
                        notification.get("notitype").equals("19") ||
                        notification.get("notitype").equals("20") ||
                        notification.get("notitype").equals("24") ||
                        notification.get("notitype").equals("26") ||
                        notification.get("notitype").equals("25") ||
                        notification.get("notitype").equals("32") ||
                        notification.get("notitype").equals("36") ||
                        notification.get("notitype").equals("37")||
                        notification.get("notitype").equals("38")) {
                    SPNotification("", notification.get("msg"), CHANNEL_Winner_ID, notification.get("notitype"));
                }


            } catch (Exception e) {
                e.printStackTrace();
            }

        } else if (remoteMessage.getNotification() != null) {
            Log.d(TAG, "Message Notification Body: " + remoteMessage.getNotification().getBody());
        }
    }

    private void FeedCommentNotification(String property_id, String messageBody, String channel) {
        if (FeedCommentsActivity.isAppRunning) {
            Intent intent = new Intent(Constants.PUSH_NOTIFICATION);
            LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
        } else {
            Intent i = new Intent(this, FeedCommentsActivity.class).putExtra(Constants.PROPERTY_ID, property_id);
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, i, PendingIntent.FLAG_ONE_SHOT);
            Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, channel)
                    .setVibrate(new long[]{1000, 1000, 1000, 1000, 1000})
                    .setContentTitle(getString(R.string.app_name))
                    .setContentText(messageBody)
                    .setAutoCancel(true)
                    .setLargeIcon(BitmapFactory.decodeResource(this.getResources(), appIconAlpha))
                    .setSound(defaultSoundUri)
                    .setContentIntent(pendingIntent);
            if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                notificationBuilder.setSmallIcon(appIconNoAlpha);
                notificationBuilder.setColor(getResources().getColor(R.color.colorPrimary));
            } else {
                notificationBuilder.setSmallIcon(appIconNoAlpha);
            }
            getnotiManager().notify(0, notificationBuilder.build());
        }
    }


    private void OfferNotification(String property_id, String notification, String desctext, Bitmap bitmap, String channel, String type) {
        Intent i = null;
        String text;
        if (type.equalsIgnoreCase("propertyadd")) {
            text = desctext;
            i = new Intent(this, LocationInfoActivity.class).putExtra(Constants.PROPERTY_ID, property_id);
        } else if (type.equals("offer")) {
            text = notification;
            i = new Intent(this, LocationInfoActivity.class).putExtra(Constants.PROPERTY_ID, property_id);
        } else {
            text = notification;
            i = new Intent(this, BidListActivity.class).putExtra(Constants.PROPERTY_ID, property_id);
        }
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, i, PendingIntent.FLAG_ONE_SHOT);
        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, channel)
                .setVibrate(new long[]{1000, 1000, 1000, 1000, 1000})
                .setWhen(System.currentTimeMillis())
                .setStyle(new NotificationCompat.BigPictureStyle().bigPicture(bitmap))
                .setContentTitle(notification)
                .setContentText(text)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent)
                .setLargeIcon(BitmapFactory.decodeResource(this.getResources(), appIconAlpha))
                .setPriority(NotificationManager.IMPORTANCE_HIGH)
                .setSound(defaultSoundUri);
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            notificationBuilder.setSmallIcon(appIconNoAlpha);
            notificationBuilder.setColor(getResources().getColor(R.color.colorPrimary));
        } else {
            notificationBuilder.setSmallIcon(appIconNoAlpha);
        }
        getnotiManager().notify(0, notificationBuilder.build());
    }

    private void InviteNotification(String property_id, String notification, String channel, String type) {
        Intent i;
        if (type.equalsIgnoreCase("7") || type.equalsIgnoreCase("8") || type.equalsIgnoreCase("9")) {
            i = new Intent(this, NotificationFragment.class);
        } else if (type.equalsIgnoreCase("11")) {
            i = new Intent(this, FeedLikeActivity.class).putExtra(Constants.PROPERTY_ID, property_id);
        } else {
            i = new Intent(this, LocationInfoActivity.class).putExtra(Constants.PROPERTY_ID, property_id);
        }
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, i, PendingIntent.FLAG_ONE_SHOT);
        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, channel)
                .setVibrate(new long[]{1000, 1000, 1000, 1000, 1000})
                .setWhen(System.currentTimeMillis())
                .setContentTitle(getResources().getString(R.string.app_name))
                .setContentText(notification)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .setLargeIcon(BitmapFactory.decodeResource(this.getResources(), appIconAlpha))
                .setPriority(NotificationManager.IMPORTANCE_HIGH)
                .setSound(defaultSoundUri);
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            notificationBuilder.setSmallIcon(appIconNoAlpha);
            notificationBuilder.setColor(getResources().getColor(R.color.colorPrimary));
        } else {
            notificationBuilder.setSmallIcon(appIconNoAlpha);
        }
        getnotiManager().notify(0, notificationBuilder.build());
    }


    private void SPNotification(String property_id, String notification, String channel, String type) {
        Intent i = null;
        if (type.equals("14") || type.equals("16") || type.equals("18") || type.equals("19") || type.equals("24") || type.equals("26") || type.equals("25") || type.equals("32") || type.equals("36")||type.equals("37")||type.equals("38")) {
            i = new Intent(this, NotificationFragment.class);
        } else if (type.equals("15") || type.equals("17") || type.equals("20")) {
            i = new Intent(this, ServiceProviderHome.class).putExtra(Constants.KEY, "sp");
        } else if (type.equals("33")) {
            i = new Intent(this, MyOfferList.class);
        }
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, i, PendingIntent.FLAG_ONE_SHOT);
        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, channel)
                .setVibrate(new long[]{1000, 1000, 1000, 1000, 1000})
                .setWhen(System.currentTimeMillis())
                .setContentTitle(getResources().getString(R.string.app_name))
                .setContentText(notification)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent)
                .setLargeIcon(BitmapFactory.decodeResource(this.getResources(), appIconAlpha))
                .setPriority(NotificationManager.IMPORTANCE_HIGH)
                .setSound(defaultSoundUri);
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            notificationBuilder.setSmallIcon(appIconNoAlpha);
            notificationBuilder.setColor(getResources().getColor(R.color.colorPrimary));
        } else {
            notificationBuilder.setSmallIcon(appIconNoAlpha);
        }
        getnotiManager().notify(0, notificationBuilder.build());
    }

    private void winnerNotification(String property_id, String notification, String channel, String type) {
        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, channel)
                .setVibrate(new long[]{1000, 1000, 1000, 1000, 1000})
                .setWhen(System.currentTimeMillis())
                .setContentTitle(getResources().getString(R.string.app_name))
                .setContentText(notification)
                .setAutoCancel(true)
                .setLargeIcon(BitmapFactory.decodeResource(this.getResources(), appIconAlpha))
                .setPriority(NotificationManager.IMPORTANCE_HIGH)
                .setSound(defaultSoundUri);
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            notificationBuilder.setSmallIcon(appIconNoAlpha);
            notificationBuilder.setColor(getResources().getColor(R.color.colorPrimary));
        } else {
            notificationBuilder.setSmallIcon(appIconNoAlpha);
        }
        getnotiManager().notify(0, notificationBuilder.build());
    }

    private void ChatNotification(String messageBody, Bundle bundle, String channel) {
        if (ChatActivity.isAppRunning) {
            Intent intent = new Intent(Constants.PUSH_NOTIFICATION);
            LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
        } else {
            DbHandler dbHandler = new DbHandler(this, null);
            dbHandler.addNotiItemDb(new ChatMsgModel(bundle.getString(Constants.MESSAGE),
                    bundle.getString(Constants.FROM_ID),
                    bundle.getString(Constants.TO_ID),
                    bundle.getString(Constants.CHAT_ID),
                    bundle.getString("1"),
                    bundle.getString("messageType"),
                    bundle.getString(Constants.CHAT_TIME),
                    bundle.getString(Constants.CHAT_STATUS),
                    bundle.getString(Constants.TONAME), "normal",
                    bundle.getString(Constants.PROPERTY_ID)));
            Intent i = new Intent(this, ChatActivity.class).
                    putExtra(Constants.NOTIFICATION_ID, "1").putExtra("payload_bundle", bundle);
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, i, PendingIntent.FLAG_ONE_SHOT);
            Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, channel)
                    .setVibrate(new long[]{1000, 1000, 1000, 1000, 1000})
                    //.setSmallIcon(appIconNoAlpha)
                    .setContentTitle(bundle.getString("msg"))
                    .setContentText(messageBody)
                    .setAutoCancel(true)
                    .setLargeIcon(BitmapFactory.decodeResource(this.getResources(), appIconAlpha))
                    .setSound(defaultSoundUri)
                    .setContentIntent(pendingIntent);
            if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                notificationBuilder.setSmallIcon(appIconNoAlpha);
                notificationBuilder.setColor(getResources().getColor(R.color.colorPrimary));
            } else {
                notificationBuilder.setSmallIcon(appIconNoAlpha);
            }
            getnotiManager().notify(0, notificationBuilder.build());
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void setupChannels() {
        NotificationChannel adminChannel;
        adminChannel = new NotificationChannel(CHANNEL_OFFER_ID, CHANNEL_OFFER_NAME, NotificationManager.IMPORTANCE_HIGH);
        adminChannel.enableLights(true);
        adminChannel.setLightColor(Color.MAGENTA);
        adminChannel.enableVibration(true);
        getnotiManager().createNotificationChannel(adminChannel);

        NotificationChannel notificationChannel2 = new NotificationChannel(CHANNEL_CHAT_ID,
                CHANNEL_CHAT_NAME, NotificationManager.IMPORTANCE_HIGH);
        notificationChannel2.enableLights(false);
        notificationChannel2.enableVibration(true);
        notificationChannel2.setLightColor(Color.MAGENTA);
        notificationChannel2.setShowBadge(false);
        getnotiManager().createNotificationChannel(notificationChannel2);


        NotificationChannel notificationChannel3 = new NotificationChannel(CHANNEL_BID_ID,
                CHANNEL_BID_NAME, NotificationManager.IMPORTANCE_HIGH);
        notificationChannel3.enableLights(false);
        notificationChannel3.enableVibration(true);
        notificationChannel3.setLightColor(Color.MAGENTA);
        notificationChannel3.setShowBadge(false);
        getnotiManager().createNotificationChannel(notificationChannel3);


        NotificationChannel notificationChannel4 = new NotificationChannel(CHANNEL_Winner_ID,
                CHANNEL_Winner_NAME, NotificationManager.IMPORTANCE_HIGH);
        notificationChannel4.enableLights(false);
        notificationChannel4.enableVibration(true);
        notificationChannel4.setLightColor(Color.MAGENTA);
        notificationChannel4.setShowBadge(false);
        getnotiManager().createNotificationChannel(notificationChannel4);

        NotificationChannel notificationChannel5 = new NotificationChannel(CHANNEL_Invite_ID,
                CHANNEL_Invite_NAME, NotificationManager.IMPORTANCE_HIGH);
        notificationChannel5.enableLights(false);
        notificationChannel5.enableVibration(true);
        notificationChannel5.setLightColor(Color.MAGENTA);
        notificationChannel5.setShowBadge(false);
        getnotiManager().createNotificationChannel(notificationChannel5);

        NotificationChannel notificationChannel6 = new NotificationChannel(CHANNEL_initalpayment_ID,
                CHANNEL_initalpayment_NAME, NotificationManager.IMPORTANCE_HIGH);
        notificationChannel6.enableLights(false);
        notificationChannel6.enableVibration(true);
        notificationChannel6.setLightColor(Color.MAGENTA);
        notificationChannel6.setShowBadge(false);
        getnotiManager().createNotificationChannel(notificationChannel6);

    }

    private NotificationManager getnotiManager() {
        if (notificationManager == null) {
            notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        }
        return notificationManager;
    }

    public Bitmap getBitmapfromUrl(String imageUrl) {
        try {
            URL url = new URL(imageUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            Bitmap bitmap = BitmapFactory.decodeStream(input);
            return bitmap;

        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return null;

        }
    }

}
