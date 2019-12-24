package com.open_source.activity;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.ContentUris;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.provider.Settings;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AbsListView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.bumptech.glide.Glide;
import com.open_source.BuildConfig;
import com.open_source.R;
import com.open_source.SQLiteHelper.DbHandler;
import com.open_source.adapter.ChatAdapter;
import com.open_source.modal.ChatMsgModel;
import com.open_source.modal.RetrofitUserData;
import com.open_source.progressHud.ProgressHUD;
import com.open_source.retrofitPack.Constants;
import com.open_source.retrofitPack.RetrofitClient;
import com.open_source.util.SharedPref;
import com.open_source.util.Utility;
import com.soundcloud.android.crop.Crop;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;
import id.zelory.compressor.Compressor;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.Manifest.permission.CAMERA;
import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;


public class ChatActivity extends BaseActivity implements View.OnClickListener {
    private static final String TAG = ChatActivity.class.getSimpleName();
    public static String chat_id = "0";
    final int PERMISSION_REQUEST_CODE = 101;
    private Context context;
    private Toolbar toolbar;
    private TextView toolbar_title,user_name;
    private ImageView camera,img_send;
    private EditText cet_chatbox;
    private boolean imgflag=false;
    private ListView recyclerView;
    private File file,photoFile=null;
    private ProgressHUD progressHUD;
    private String user_id = "", property_id = "",imageFilePath="",chat_status="";
    private DbHandler mydb;
    private  ChatAdapter chatListAdapter;
    private CircleImageView user_profile;
    private String[] requestedPermissions = {CAMERA, WRITE_EXTERNAL_STORAGE, READ_EXTERNAL_STORAGE};
    private ArrayList<ChatMsgModel> array_chat = new ArrayList<>();
    public static boolean isAppRunning;
    private BroadcastReceiver broadcastReceiver;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        context = ChatActivity.this;
        mydb = new DbHandler(context, null);
        init();
    }

    public void init() {
        toolbar = (Toolbar) findViewById(R.id.container_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.back_arrow);
        getSupportActionBar().setDisplayShowHomeEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        toolbar_title = (TextView) findViewById(R.id.toolbar_title);
        user_profile = toolbar.findViewById(R.id.user_profile);
        user_name = toolbar.findViewById(R.id.user_name);


        Bundle b=getIntent().getExtras();
        if (b.containsKey(Constants.NOTIFICATION_ID)) {
            Bundle bundle = b.getBundle("payload_bundle");
            user_id = bundle.getString(Constants.FROM_ID);
            chat_status=bundle.getString(Constants.CHAT_STATUS);
            //Log.e(TAG, "========if"+user_id);
           // Glide.with(context).load(bundle.getString(Constants.PROFILE_IMAGE)).into(user_profile);
            user_name.setText(bundle.getString(Constants.FROMNAME));
            property_id = bundle.getString(Constants.PROPERTY_ID);

        }
        //else15
        //fromid14
        //15,70ffc63e6bf5dd976a6f2519347228cf,58,text,
        else {
            user_id = b.getString(Constants.USER_ID);
            chat_status=b.getString(Constants.CHAT_STATUS);
            //Log.e(TAG, "========else"+user_id);
            Glide.with(context).load(b.getString(Constants.PROFILE_IMAGE)).into(user_profile);
            user_name.setText(b.getString(Constants.TONAME));
            property_id = b.getString(Constants.PROPERTY_ID);
        }

       /* Log.e(TAG, "========fromid"+SharedPref.getSharedPreferences(context,Constants.USER_ID));
        Log.e(TAG, "========user_id"+user_id);*/
        array_chat = mydb.GetChat(SharedPref.getSharedPreferences(context,Constants.USER_ID),user_id);
        //Log.e(TAG, "============:chat_id"+chat_id);
        camera = (ImageView) findViewById(R.id.camera);
        cet_chatbox = (EditText) findViewById(R.id.chatbox);
        img_send = (ImageView) findViewById(R.id.img_send);
        recyclerView = findViewById(R.id.recyclerView);
        img_send.setOnClickListener(this);
        camera.setOnClickListener(this);
        toolbar_title.setText(R.string.chat);
        chatListAdapter = new ChatAdapter(context, array_chat);
        recyclerView.setAdapter(chatListAdapter);
        recyclerView.setTranscriptMode(AbsListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);
        recyclerView.setSelection(recyclerView.getAdapter().getCount() - 1);


        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.getAction().equals(Constants.PUSH_NOTIFICATION)) {
                    GetData();
                    Log.e(TAG, "======="+"broadcastReceiver" );

                }
            }
        };


    }

 /*   private void LoadOfflineChat() {
        array_chat = mydb.GetChat(SharedPref.getSharedPreferences(context, Constants.USER_ID), user_id);
        chatListAdapter = new ChatAdapter(context, array_chat);
        recyclerView.setAdapter(chatListAdapter);
    }*/

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.camera:
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (ContextCompat.checkSelfPermission(context, CAMERA) == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(context, WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(context, READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                        selectImage();
                    } else {
                        requestCameraPermission();
                    }
                } else {
                    selectImage();
                }
                break;

            case R.id.img_send:
                if (cet_chatbox.getText().toString().trim().isEmpty()) {

                } else {
                    SendMessage("text");
                }

                break;
        }
    }

    private void SendMessage(String msg_type) {
        if (Utility.isConnectingToInternet(context))
        {
            progressHUD = ProgressHUD.show(context, true, false, null);
            //Log.e(TAG + "=======request", user_id + "," + SharedPref.getSharedPreferences(context, Constants.TOKEN) + "," + property_id + "," + msg_type + "," + cet_chatbox.getText().toString());
            RetrofitClient.getAPIService().Send_Message(SharedPref.getSharedPreferences(context, Constants.TOKEN), user_id,
                    chat_status,property_id, msg_type, cet_chatbox.getText().toString()).enqueue(new Callback<RetrofitUserData>() {
                @Override
                public void onResponse(Call<RetrofitUserData> call, Response<RetrofitUserData> response) {
                    if (progressHUD != null && progressHUD.isShowing()) {
                        progressHUD.dismiss();
                    }
                    try {
                        if (response.body() != null) {
                            if (response.body().getStatus() == 200) {
                              //  Utility.ShowToastMessage(context, response.body().getMessage());
                                cet_chatbox.setText("");
                                GetData();
                            } else if (response.body().getStatus() == 401) {
                                SharedPref.clearPreference(context);
                                startActivity(new Intent(context, WelcomeActivity.class)
                                        .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK));
                                finish();
                            } else {
                                Utility.ShowToastMessage(context, response.body().getMessage());
                            }
                        } else {
                            // Utility.ShowToastMessage(context,response.body().getMessage());
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
                    Log.e(TAG, "onFailure: " + t.toString());
                }
            });

        }
        else {
            Utility.ShowToastMessage(context,getString(R.string.internet_connection));
        }

    }

    private void SendImage(String msg_type, File file) {
        if (Utility.isConnectingToInternet(context)) {
            MultipartBody.Part chat_img = null;
            if (file != null) {
                RequestBody reqFile = RequestBody.create(MediaType.parse("image/*"), file);
                chat_img = MultipartBody.Part.createFormData(Constants.MESSAGE_IMG, file.getName(), reqFile);
            }
            RequestBody chatstatus=RequestBody.create(MediaType.parse("text/plain"), chat_status);
            RequestBody userid = RequestBody.create(MediaType.parse("text/plain"), user_id);
            RequestBody propertyid = RequestBody.create(MediaType.parse("text/plain"), property_id);
            RequestBody type = RequestBody.create(MediaType.parse("text/plain"), msg_type);

            RetrofitClient.getAPIService().Send_Chat_Message(SharedPref.getSharedPreferences(context, Constants.TOKEN), chat_img, userid
                    ,chatstatus,propertyid,type).enqueue(new Callback<RetrofitUserData>() {
                @Override
                public void onResponse(Call<RetrofitUserData> call, Response<RetrofitUserData> response) {
                    try {
                        if (response.body() != null) {
                            if (response.body().getStatus() == 200) {
                                //Utility.ShowToastMessage(context, response.body().getMessage());
                                GetData();
                            } else {
                                Utility.ShowToastMessage(context, response.body().getMessage());
                            }
                        } else if (response.body().getStatus() == 401) {
                            SharedPref.clearPreference(context);
                            startActivity(new Intent(context, WelcomeActivity.class)
                                    .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK));
                            finish();
                        } else {
                            // Utility.ShowToastMessage(context,response.body().getMessage());
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
                    Log.e(TAG, "onFailure: " + t.toString());
                }
            });

        } else {
            Utility.ShowToastMessage(context, getString(R.string.internet_connection));
        }

    }

    private void GetData() {
        if (Utility.isConnectingToInternet(context))
        {
            RetrofitClient.getAPIService().CHAT_History(SharedPref.getSharedPreferences(context, Constants.TOKEN),
                    user_id, chat_id).enqueue(new Callback<RetrofitUserData>() {
                @Override
                public void onResponse(Call<RetrofitUserData> call, Response<RetrofitUserData> response) {
                    try {
                        if (response.body().getStatus() == 200) {
                            if (response.body().getObject().size() > 0) {
                                mydb.addItemDb(response.body().getObject());
                            }
                            array_chat = mydb.GetChat(SharedPref.getSharedPreferences(context, Constants.USER_ID), user_id);
                            chatListAdapter.setItems(array_chat);
                            chatListAdapter.notifyDataSetChanged();
                        } else if (response.body().getStatus() == 401) {
                            SharedPref.clearPreference(context);
                            startActivity(new Intent(context, WelcomeActivity.class)
                                    .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK));
                            finish();
                        } else {
                           // Utility.ShowToastMessage(context, response.body().getMessage());
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
                    Log.e(TAG, "onFailure: " + t.toString());
                }
            });

        }
        else {
            Utility.ShowToastMessage(context,getString(R.string.internet_connection));
        }
        //array_chat.clear();

    }

    private void ClearChat() {
        if (Utility.isConnectingToInternet(context))
        {
            progressHUD = ProgressHUD.show(context, true, false, null);
            //Log.e(TAG + "=======chatid", user_id);
            //Log.e(TAG + "=======token", SharedPref.getSharedPreferences(context, Constants.TOKEN));
            RetrofitClient.getAPIService().Clear_Chat(SharedPref.getSharedPreferences(context, Constants.TOKEN),user_id).enqueue(new Callback<RetrofitUserData>() {
                @Override
                public void onResponse(Call<RetrofitUserData> call, Response<RetrofitUserData> response) {
                    if (progressHUD != null && progressHUD.isShowing()) {
                        progressHUD.dismiss();
                    }
                    try {
                        if (response.body().getStatus() == 200) {
                            Boolean staus=mydb.deleteChat(SharedPref.getSharedPreferences(context, Constants.USER_ID), user_id);
                            if (staus.equals(true))
                            {
                                array_chat=new ArrayList<>();
                                chatListAdapter.setItems(array_chat);
                                chatListAdapter.notifyDataSetChanged();
                            }

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
                    Log.e(TAG, "onFailure: " + t.toString());
                }
            });

        }
        else {
            Utility.ShowToastMessage(context,getString(R.string.internet_connection));
        }

    }

    private void selectImage() {
        final CharSequence[] options = {getString(R.string.from_camera), getString(R.string.from_gallery), getString(R.string.close)};
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(R.string.add_photo);
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (options[item].equals(getString(R.string.from_camera))) {
                    Intent pictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    if (pictureIntent.resolveActivity(getPackageManager()) != null) {
                        try {
                            photoFile = createImageFile();
                        } catch (IOException e) {
                            e.printStackTrace();
                            return;
                        }
                        Uri photoUri = FileProvider.getUriForFile(context, getPackageName() + ".provider", photoFile);
                        pictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
                        startActivityForResult(pictureIntent, 1);
                    }
                } else if (options[item].equals(getString(R.string.from_gallery))) {
                    Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(intent, 2);
                } else if (options[item].equals(getString(R.string.close))) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }

    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        String imageFileName = "IMG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(imageFileName, ".png", storageDir);
        imageFilePath = image.getAbsolutePath();
        return image;
    }

    private Uri getCaptureImageOutputUri() {
        Uri outputFileUri = null;
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.M) {
            File getImage = getExternalCacheDir();
            if (getImage != null) {
                outputFileUri = Uri.fromFile(new File(getImage.getPath(), "pickImageResult.jpeg"));
            }
        } else {
            File getImage = getExternalCacheDir();
            if (getImage != null) {
                outputFileUri = FileProvider.getUriForFile(context, BuildConfig.APPLICATION_ID + ".provider",
                        new File(getImage.getPath(), "pickImageResult.jpeg"));
            }
        }
        return outputFileUri;
    }

    public Uri getPickImageResultUri(Intent data) {
        boolean isCamera = true;
        if (data != null && data.getData() != null) {
            String action = data.getAction();
            isCamera = action != null && action.equals(MediaStore.ACTION_IMAGE_CAPTURE);
        }
        return isCamera ? getCaptureImageOutputUri() : data.getData();
    }

    private void beginCrop(Uri source) {
        File pro = new File(Utility.MakeDir(Constants.SDCARD_FOLDER_PATH, context), System.currentTimeMillis() + ".jpg");
        Uri destination1 = Uri.fromFile(pro);
        Crop.of(source, destination1).asSquare().withAspect(200, 200).start(ChatActivity.this);
    }

    private void handleCrop(int resultCode, Intent result) {
        if (resultCode == RESULT_OK) {
            String imagepath = Crop.getOutput(result).getPath();
            File file1 = new File(imagepath);
            try {
                file = new Compressor(this).compressToFile(file1);
            } catch (IOException e) {
                e.printStackTrace();
            }
            //SendImage("image", file);
            showImageDialog(imagepath);
            //Glide.with(context).load(imagepath).into();

        } else if (resultCode == Crop.RESULT_ERROR) {
            Toast.makeText(this, Crop.getError(result).getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

     private void showImageDialog(String imagepath) {
        imgflag=false;
        ImageView img, cross, send;
        final Dialog dialog = new Dialog(context,android.R.style.Theme_Black_NoTitleBar_Fullscreen);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.image_confirm_dialog);
        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
        Window window = dialog.getWindow();
        layoutParams.copyFrom(window.getAttributes());
        layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;
        layoutParams.height = WindowManager.LayoutParams.MATCH_PARENT;
        layoutParams.gravity = Gravity.CENTER;
        window.setAttributes(layoutParams);
        //dialog.getWindow().setBackgroundDrawable(new ColorDrawable(context.getResources().getColor(R.color.transparent)));
        img = (ImageView) dialog.findViewById(R.id.img);
        cross = (ImageView) dialog.findViewById(R.id.cross);
        send = (ImageView) dialog.findViewById(R.id.img_send);
        Glide.with(context).load(imagepath).into(img);

        cross.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                SendImage("image", file);
              /*  if (!imgflag)
                {

                    imgflag=true;
                }*/

            }
        });
        dialog.show();
    }

    private void requestCameraPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(ChatActivity.this, CAMERA)
                | ActivityCompat.shouldShowRequestPermissionRationale(ChatActivity.this, WRITE_EXTERNAL_STORAGE)
                | ActivityCompat.shouldShowRequestPermissionRationale(ChatActivity.this, READ_EXTERNAL_STORAGE)) {
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setMessage(getString(R.string.camera_permission_needed));
            builder.setPositiveButton(R.string.grant, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    ActivityCompat.requestPermissions(ChatActivity.this, requestedPermissions, PERMISSION_REQUEST_CODE);

                }
            }).create().show();

        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{CAMERA, WRITE_EXTERNAL_STORAGE, READ_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
            }
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        //Checking the request code of our request
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                selectImage();
            } else {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setMessage(getString(R.string.camera_permission_needed));
                builder.setPositiveButton(R.string.enable, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Intent intent = new Intent();
                        intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                        intent.addCategory(Intent.CATEGORY_DEFAULT);
                        intent.setData(Uri.parse("package:" + context.getPackageName()));
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                        intent.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
                        startActivity(intent);
                    }
                }).create().show();
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.e(TAG, "onActivityResult:========"+requestCode );
        if (resultCode == AppCompatActivity.RESULT_OK) {
            if (requestCode == 1) {
                Log.e(TAG, "============: " + imageFilePath);
                Bitmap b = decodeFile(photoFile);
                if (b != null) {
                    imageFilePath = photoFile.getAbsolutePath();
                    Log.e(TAG, "============: " + imageFilePath);
                    file = new File(imageFilePath);
                    showImageDialog(imageFilePath);
                } else {
                    showImageDialog(imageFilePath);
                }

            } else if (requestCode == 2) {
                Uri selectedImage = data.getData();
                String path = null;
                try {
                    path = getFilePath(context, selectedImage);
                    file = new File(path);
                    showImageDialog(path);
                    //file1 = new File(path);
                    //Glide.with(context).load(path).into(camera_1);
                } catch (URISyntaxException e) {
                    e.printStackTrace();
                }

            }
        }
    }
    @SuppressLint("NewApi")
    public static String getFilePath(Context context, Uri uri) throws URISyntaxException {
        String selection = null;
        String[] selectionArgs = null;
        // Uri is different in versions after KITKAT (Android 4.4), we need to
        if (Build.VERSION.SDK_INT >= 19 && DocumentsContract.isDocumentUri(context.getApplicationContext(), uri)) {
            if (isExternalStorageDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                return Environment.getExternalStorageDirectory() + "/" + split[1];
            } else if (isDownloadsDocument(uri)) {
                final String id = DocumentsContract.getDocumentId(uri);
                uri = ContentUris.withAppendedId(
                        Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));
            } else if (isMediaDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];
                if ("image".equals(type)) {
                    uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    uri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }
                selection = "_id=?";
                selectionArgs = new String[]{
                        split[1]
                };
            }
        }
        if ("content".equalsIgnoreCase(uri.getScheme())) {
            String[] projection = {
                    MediaStore.Images.Media.DATA
            };
            Cursor cursor = null;
            try {
                cursor = context.getContentResolver()
                        .query(uri, projection, selection, selectionArgs, null);
                int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                if (cursor.moveToFirst()) {
                    return cursor.getString(column_index);
                }
            } catch (Exception e) {
            }
        } else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }
        return null;
    }
    public static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    public static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    public static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }
    //-------------------------- Back Pressed -----------------------------
    @Override
    public void onBackPressed() {
        finish();
        super.onBackPressed();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;

            case R.id.clear_chat:
                ClearChat();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.chat, menu);//Menu Resource, Menu
        return true;
    }
    private Bitmap decodeFile(File f) {
        Bitmap b = null;
        //Decode image size
        BitmapFactory.Options o = new BitmapFactory.Options();
        o.inJustDecodeBounds = true;
        FileInputStream fis = null;
        try {
            fis = new FileInputStream(f);
            BitmapFactory.decodeStream(fis, null, o);
            fis.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        int IMAGE_MAX_SIZE = 1024;
        int scale = 1;
        if (o.outHeight > IMAGE_MAX_SIZE || o.outWidth > IMAGE_MAX_SIZE) {
            scale = (int) Math.pow(2, (int) Math.ceil(Math.log(IMAGE_MAX_SIZE /
                    (double) Math.max(o.outHeight, o.outWidth)) / Math.log(0.5)));
        }
        //Decode with inSampleSize
        BitmapFactory.Options o2 = new BitmapFactory.Options();
        o2.inSampleSize = scale;
        try {
            fis = new FileInputStream(f);
            b = BitmapFactory.decodeStream(fis, null, o2);
            fis.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Log.d(TAG, "Width :" + b.getWidth() + " Height :" + b.getHeight());
        // photoFile = new File(photoFile);
        try {
            FileOutputStream out = new FileOutputStream(photoFile);
            b.compress(Bitmap.CompressFormat.PNG, 100, out);
            out.flush();
            out.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return b;
    }

    //--------------------------- Status Bar ------------------------------
    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                getWindow().setStatusBarColor(ContextCompat.getColor(context, R.color.colorPrimary));
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        GetData();
        isAppRunning = true;
        LocalBroadcastManager.getInstance(context).registerReceiver(broadcastReceiver, new IntentFilter(Constants.PUSH_NOTIFICATION));
    }

    @Override
    protected void onStart() {
        super.onStart();
        isAppRunning = true;
    }

    @Override
    protected void onPause() {
        isAppRunning = false;
        LocalBroadcastManager.getInstance(context).unregisterReceiver(broadcastReceiver);
        super.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
        isAppRunning = false;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        isAppRunning = false;
    }
}
