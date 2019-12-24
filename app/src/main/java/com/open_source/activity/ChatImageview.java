package com.open_source.activity;

import android.os.Bundle;
import androidx.annotation.Nullable;

import android.util.Log;
import android.widget.ImageView;

import com.bogdwellers.pinchtozoom.ImageMatrixTouchHandler;
import com.bumptech.glide.Glide;
import com.open_source.R;
import com.open_source.retrofitPack.Constants;


public class ChatImageview extends BaseActivity {
    private ImageView img_chat;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chat_image_view);
        img_chat = findViewById(R.id.img_chat);
        String str = getIntent().getExtras().getString(Constants.IMAGE, "");
        Log.e("========: ",str );
        Glide.with(ChatImageview.this).load(str)
                .into(img_chat);
        ImageMatrixTouchHandler imageMatrixTouchHandler = new ImageMatrixTouchHandler(ChatImageview.this);
        img_chat.setOnTouchListener(imageMatrixTouchHandler);

    }
}
