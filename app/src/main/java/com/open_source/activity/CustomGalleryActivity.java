package com.open_source.activity;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;


import com.open_source.R;
import com.open_source.adapter.ImageListRecyclerAdapter;
import com.open_source.modal.CustomGallery;
import com.open_source.util.Action;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;


public class CustomGalleryActivity extends BaseActivity {
    RecyclerView recyclerView;
    ImageView imgNoMedia;
    Button btnGalleryOk;

//    @BindView(R.id.gridGallery)
//    GridView gridGallery;


    String action;
    Handler handler;
    //GalleryAdapter adapter;
    ImageListRecyclerAdapter imageListRecyclerAdapter;
    private HashMap<String,CustomGallery> imagesUri;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.gallery);
        recyclerView=findViewById(R.id.recyclerView);
        imgNoMedia=findViewById(R.id.imgNoMedia);
        btnGalleryOk=findViewById(R.id.btnGalleryOk);

        action = getIntent().getAction();
        if (action == null) {
            finish();
        }
        //initImageLoader();
        init();
    }


 /*   public void initImageLoader() {

            imageLoader = Utils.initImageLoader(getActivity());

    }*/

    private void init() {
        handler = new Handler();
        imgNoMedia.setVisibility(View.GONE);
        recyclerView.setLayoutManager(new GridLayoutManager(getApplicationContext(), 3));
        imageListRecyclerAdapter = new ImageListRecyclerAdapter(getApplicationContext());

        recyclerView.setAdapter(imageListRecyclerAdapter);
        if (action.equalsIgnoreCase(Action.ACTION_MULTIPLE_PICK)) {
            findViewById(R.id.llBottomContainer).setVisibility(View.VISIBLE);
            imageListRecyclerAdapter.setMultiplePick(true);
        } else {
            findViewById(R.id.llBottomContainer).setVisibility(View.GONE);
        }


        imageListRecyclerAdapter.setEventListner(new ImageListRecyclerAdapter.EventListener() {
            @Override
            public void onItemClickListener(int position, ImageListRecyclerAdapter.VerticalItemHolder holder) {
                if (imageListRecyclerAdapter.isMultiSelected()) {
                    imageListRecyclerAdapter.changeSelection(holder, position);
                } else {
                    CustomGallery customGallery = imageListRecyclerAdapter.getItem(position);
                    Intent intent = new Intent();
                    intent.putExtra("single_path", customGallery.sdcardPath);
                    setResult(RESULT_OK, intent);
                    finish();
                }

            }
        });


//		btnGalleryOk = (Button) findViewById(R.id.btnGalleryOk);
        btnGalleryOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ArrayList<CustomGallery> selected = imageListRecyclerAdapter.getSelected();

                String[] allPath = new String[selected.size()];
                for (int i = 0; i < allPath.length; i++) {
                    allPath[i] = selected.get(i).sdcardPath;
                }

                Intent data = new Intent().putExtra("all_path", allPath);
                setResult(RESULT_OK, data);
                finish();
            }
        });

        new Thread() {

            @Override
            public void run() {
                Looper.prepare();
                handler.post(new Runnable() {

                    @Override
                    public void run() {
                        imageListRecyclerAdapter.addAll(getGalleryPhotos());
                        //checkImageStatus();
                    }
                });
                Looper.loop();
            }


        }.start();

    }

    private void checkImageStatus() {
        if (recyclerView.getAdapter().getItemCount() > 0) {
            imgNoMedia.setVisibility(View.VISIBLE);
        } else {
            imgNoMedia.setVisibility(View.GONE);
        }
    }


    private ArrayList<CustomGallery> getGalleryPhotos() {
        ArrayList<CustomGallery> galleryList = new ArrayList<CustomGallery>();

        try {
            final String[] columns = {MediaStore.Images.Media.DATA,
                    MediaStore.Images.Media._ID};
            final String orderBy = MediaStore.Images.Media._ID;

            Cursor imagecursor = managedQuery(
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI, columns,
                    null, null, orderBy);

            if (imagecursor != null && imagecursor.getCount() > 0) {

                while (imagecursor.moveToNext()) {
                    CustomGallery item = new CustomGallery();

                    int dataColumnIndex = imagecursor
                            .getColumnIndex(MediaStore.Images.Media.DATA);

                    item.sdcardPath = imagecursor.getString(dataColumnIndex);

                    galleryList.add(item);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        // show newest photo at beginning of the list
        Collections.reverse(galleryList);
        return galleryList;
    }

}
