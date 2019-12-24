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
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.open_source.R;
import com.open_source.activity.WelcomeActivity;
import com.open_source.modal.RetrofitUserData;
import com.open_source.progressHud.ProgressHUD;
import com.open_source.retrofitPack.Constants;
import com.open_source.retrofitPack.RetrofitClient;
import com.open_source.services.DownloadTask;
import com.open_source.util.SharedPref;
import com.open_source.util.Utility;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.Manifest.permission.CAMERA;
import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

public class ManageDocFragment extends Fragment {
    String property_id,str_file,str_name;
    View view;
    Context context;
    LinearLayout lay_send, lay_recieve;
    ProgressHUD progressHUD;
    String[] requestedPermissions = {WRITE_EXTERNAL_STORAGE, READ_EXTERNAL_STORAGE};
    final int PERMISSION_REQUEST_CODE = 101;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        property_id = getArguments().getString(Constants.PROPERTY_ID);
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragement_manage_doc, container, false);
        context = getActivity();
        bindview();
        return view;
    }

    private void bindview() {
        lay_recieve = view.findViewById(R.id.lay_recieve);
        lay_send = view.findViewById(R.id.lay_send);
        GetDoc();
    }

    private void GetDoc() {
        if (Utility.isConnectingToInternet(context)) {
            progressHUD = ProgressHUD.show(context, true, false, null);
            RetrofitClient.getAPIService().GetProDoc(SharedPref.getSharedPreferences(context, Constants.TOKEN), property_id).enqueue(new Callback<RetrofitUserData>() {
                @Override
                public void onResponse(Call<RetrofitUserData> call, final Response<RetrofitUserData> response) {
                    if (progressHUD != null && progressHUD.isShowing()) {
                        progressHUD.dismiss();
                    }
                    try {
                        if (response.body().getStatus() == 200) {
                            if (response.body().getRecieve().size() != 0) {
                                ((CardView) view.findViewById(R.id.card_recieve)).setVisibility(View.VISIBLE);
                                ((TextView) view.findViewById(R.id.txt_recieve)).setVisibility(View.VISIBLE);
                                for (int i = 0; i < response.body().getRecieve().size(); i++) {
                                    View view = getActivity().getLayoutInflater().inflate(R.layout.row_pro_doc, lay_recieve, false);
                                    ((TextView) view.findViewById(R.id.txt_name)).setText(response.body().getRecieve().get(i).getName());
                                    Button btn_downalod = view.findViewById(R.id.btn_download);
                                    final int id=i;
                                    btn_downalod.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                                if (ContextCompat.checkSelfPermission(context, WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(context, READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                                                    str_name=response.body().getRecieve().get(id).getName();
                                                    str_file=response.body().getRecieve().get(id).getFile();
                                                    final DownloadTask downloadTask = new DownloadTask(context,str_name);
                                                    downloadTask.execute(str_file);
                                                } else {
                                                    requestPermission();
                                                }
                                            } else {
                                                final DownloadTask downloadTask = new DownloadTask(context,response.body().getRecieve().get(id).getName());
                                                downloadTask.execute(response.body().getRecieve().get(id).getFile());
                                            }

                                        }
                                    });
                                    lay_recieve.addView(view);
                                }

                            }
                            if (response.body().getSend().size() != 0) {
                                ((CardView) view.findViewById(R.id.card_send)).setVisibility(View.VISIBLE);
                                ((TextView) view.findViewById(R.id.txt_send)).setVisibility(View.VISIBLE);
                                for (int i = 0; i < response.body().getSend().size(); i++) {
                                    View view = getActivity().getLayoutInflater().inflate(R.layout.row_pro_doc, lay_send, false);
                                    ((TextView) view.findViewById(R.id.txt_name)).setText(response.body().getSend().get(i).getName());
                                    ((Button) view.findViewById(R.id.btn_download)).setVisibility(View.GONE);
                                    lay_send.addView(view);
                                }
                            }


                        } else if (response.body().getStatus() == 401) {
                            SharedPref.clearPreference(context);
                            startActivity(new Intent(context, WelcomeActivity.class)
                                    .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK));
                            getActivity().finish();
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
                final DownloadTask downloadTask = new DownloadTask(context,str_name);
                downloadTask.execute(str_file);


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


}
