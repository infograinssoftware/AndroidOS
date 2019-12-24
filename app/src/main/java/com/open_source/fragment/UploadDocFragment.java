package com.open_source.fragment;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.open_source.R;
import com.open_source.activity.WelcomeActivity;
import com.open_source.modal.RetrofitUserData;
import com.open_source.progressHud.ProgressHUD;
import com.open_source.retrofitPack.Constants;
import com.open_source.retrofitPack.RetrofitClient;
import com.open_source.services.DownloadTask;
import com.open_source.util.SharedPref;
import com.open_source.util.Utility;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.Manifest.permission.CAMERA;
import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;


public class UploadDocFragment extends Fragment implements View.OnClickListener {
    final int PERMISSION_REQUEST_CODE = 101;
    View view;
    Context context;
    Spinner spn_doc;
    ProgressHUD progressHUD;
    String str_id, str_name, str_file_path, str_type, property_id = "", buyerid = "";
    ArrayList<String> arrayList = new ArrayList<>();
    HashMap<String, String> hashMap = new HashMap<>();
    String[] requestedPermissions = {WRITE_EXTERNAL_STORAGE, READ_EXTERNAL_STORAGE};

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        property_id = getArguments().getString(Constants.PROPERTY_ID);
        buyerid = getArguments().getString(Constants.BUYER_ID);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragement_upload_doc, container, false);
        context = getActivity();
        bindview();
        return view;
    }

    private void bindview() {
        ((Button) view.findViewById(R.id.btn_upload)).setOnClickListener(this);
        ((Button) view.findViewById(R.id.btn_download)).setOnClickListener(this);
        spn_doc = view.findViewById(R.id.spn_doc);
        GetDocList();
        spn_doc.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                str_name = spn_doc.getSelectedItem().toString();
                if (!str_name.equals(getString(R.string.select_documents))) {
                    String str = hashMap.get(str_name);
                    String str_array[] = str.split(",");
                    str_id = str_array[0];
                    str_file_path = str_array[1];
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void GetDocList() {
        if (Utility.isConnectingToInternet(context)) {
            RetrofitClient.getAPIService().GetDocList(SharedPref.getSharedPreferences(context, Constants.TOKEN)).enqueue(new Callback<RetrofitUserData>() {
                @Override
                public void onResponse(Call<RetrofitUserData> call, Response<RetrofitUserData> response) {
                    try {
                        if (response.body().getStatus() == 200) {
                            arrayList.add(getString(R.string.select_documents));
                            hashMap.put(getString(R.string.select_documents), "");
                            for (int i = 0; i < response.body().getObject().size(); i++) {
                                arrayList.add(response.body().getObject().get(i).getName());
                                hashMap.put(response.body().getObject().get(i).getName(), response.body().getObject().get(i).getId() + "," + response.body().getObject().get(i).getFile());
                            }
                            ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>
                                    (context, android.R.layout.simple_spinner_item,
                                            arrayList);
                            spinnerArrayAdapter.setDropDownViewResource(android.R.layout
                                    .simple_spinner_dropdown_item);
                            spn_doc.setAdapter(spinnerArrayAdapter);

                        } else if (response.body().getStatus() == 401) {
                            SharedPref.clearPreference(context);
                            startActivity(new Intent(context, WelcomeActivity.class)
                                    .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK));
                            getActivity().finish();
                        } else {
                            //Utility.ShowToastMessage(context, response.body().getMessage());
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(Call<RetrofitUserData> call, Throwable t) {

                }
            });
        } else {
            Utility.ShowToastMessage(context, getString(R.string.internet_connection));
        }

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_download:
                Log.e("onClick: ", str_name);
                if (str_name.equals(getString(R.string.select_documents))) {
                    Utility.ShowToastMessage(context, getString(R.string.val_doc_type));
                } else {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        if (ContextCompat.checkSelfPermission(context, WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(context, READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                            download();
                        } else {
                            str_type = "";
                            requestPermission();
                        }
                    } else {
                        download();
                    }
                }

                break;
            case R.id.btn_upload:
                Log.e("onClick: ", str_name);
                if (str_name.equals(getString(R.string.select_documents))) {
                    Utility.ShowToastMessage(context, getString(R.string.val_doc_type));

                } else {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        if (ContextCompat.checkSelfPermission(context, WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(context, READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                            getdoc();
                        } else {
                            str_type = "upload";
                            requestPermission();
                        }
                    } else {
                        getdoc();
                    }
                }

                break;
        }

    }

    private void download() {
        Log.e("=========: ", str_file_path);
        if (str_file_path.isEmpty() || str_file_path != null) {
            final DownloadTask downloadTask = new DownloadTask(context, str_name);
            downloadTask.execute(str_file_path);
        }

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == AppCompatActivity.RESULT_OK && requestCode == 1) {
            Uri fileuri = data.getData();
            String docFilePath = Utility.getFileNameByUri(getActivity(), fileuri);
            File file = new File(docFilePath);
            String extention = docFilePath.substring(docFilePath.lastIndexOf("."));
            if (extention.contains(".pdf") || extention.equalsIgnoreCase(".docx") || extention.contains(".xls")) {
                UploadDocuments(file, str_name);
            } else {
                Utility.ShowToastMessage(context, getString(R.string.invalid_file));
            }

        }
    }

    public void UploadDocuments(File file, String str_name) {
        if (Utility.isConnectingToInternet(context)) {
            RequestBody pro_id = null, buyer_id = null;
            progressHUD = ProgressHUD.show(context, true, false, null);
            RequestBody reqFile = RequestBody.create(MediaType.parse("application/pdf"), file);
            MultipartBody.Part doc_file = MultipartBody.Part.createFormData(Constants.FILE, file.getName(), reqFile);
            RequestBody filename = RequestBody.create(MediaType.parse("text/plain"), str_name);
            pro_id = RequestBody.create(MediaType.parse("text/plain"), property_id);
            if (!SharedPref.getSharedPreferences(context, Constants.USER_ID).equals(buyerid)) {
                buyer_id = RequestBody.create(MediaType.parse("text/plain"), buyerid);
            }
            RetrofitClient.getAPIService().UploadProDoc(SharedPref.getSharedPreferences(context, Constants.TOKEN), doc_file, filename, pro_id, buyer_id).enqueue(new Callback<RetrofitUserData>() {
                @Override
                public void onResponse(Call<RetrofitUserData> call, Response<RetrofitUserData> response) {
                    if (progressHUD != null && progressHUD.isShowing()) {
                        progressHUD.dismiss();
                    }
                    try {
                        if (response.body().getStatus() == 200) {

                        } else if (response.body().getStatus() == 401) {
                            SharedPref.clearPreference(context);
                            startActivity(new Intent(context, WelcomeActivity.class)
                                    .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK));
                            getActivity().finish();
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

    private void requestPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), WRITE_EXTERNAL_STORAGE)
                | ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), READ_EXTERNAL_STORAGE)) {
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setMessage(getString(R.string.camera_permission_needed));
            builder.setPositiveButton(R.string.grant, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    ActivityCompat.requestPermissions(getActivity(), requestedPermissions, PERMISSION_REQUEST_CODE);
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
                if (str_type.equals("upload")) {
                    getdoc();
                } else {
                    download();
                }


            } else {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setMessage(getString(R.string.permission_needed));
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


    public void getdoc() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("application/msword,application/pdf");
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        startActivityForResult(intent, 1);
    }
}
