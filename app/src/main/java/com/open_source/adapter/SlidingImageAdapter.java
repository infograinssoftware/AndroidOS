package com.open_source.adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Parcelable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.PagerAdapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.open_source.R;
import com.open_source.activity.SlideImageview;
import com.open_source.retrofitPack.Constants;

import java.util.ArrayList;

public class SlidingImageAdapter extends PagerAdapter {
    private static final String TAG = SlidingImageAdapter.class.getSimpleName();
    ArrayList<String> IMAGES = new ArrayList<>();
    private LayoutInflater inflater;
    private Context context;
    private String type;


    public SlidingImageAdapter(Context context, ArrayList<String> IMAGES, String type) {
        this.context = context;
        this.IMAGES = IMAGES;
        inflater = LayoutInflater.from(context);
        this.type = type;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

    @Override
    public int getCount() {
        return IMAGES.size();
    }

    @Override
    public Object instantiateItem(ViewGroup view, final int position) {
        RequestOptions requestOptions = new RequestOptions();
        requestOptions.placeholder(R.drawable.default_property);
        requestOptions.error(R.drawable.default_property);
        requestOptions.timeout(30000);
        View imageLayout = inflater.inflate(R.layout.slidingimages_layout, view, false);
        assert imageLayout != null;
        final ImageView imageView = (ImageView) imageLayout.findViewById(R.id.image);
        final ProgressBar progress = (ProgressBar) imageLayout.findViewById(R.id.progress);
        final FrameLayout frame_container = (FrameLayout) imageLayout.findViewById(R.id.frame_container);
        Glide.with(context).load(IMAGES.get(position)).apply(requestOptions).into(imageView);


//        Glide.with(context).load(IMAGES.get(position))
//                .listener(new RequestListener<String, GlideDrawable>() {
//                    @Override
//                    public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
//                        progress.setVisibility(View.GONE);
//                        return false;
//                    }
//
//                    @Override
//                    public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
//                        progress.setVisibility(View.GONE);
//                        return false;
//                    }
//                })
//                .into(imageView);

        view.addView(imageLayout, 0);
        frame_container.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (type.equals("advertizement")) {
                    context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://18.191.6.144/os-real-estate")));
                } else {
                    context.startActivity(new Intent(context, SlideImageview.class).
                            putStringArrayListExtra(Constants.IMAGE, IMAGES));
                }
            }
        });
        return imageLayout;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view.equals(object);
    }

    @Override
    public void restoreState(Parcelable state, ClassLoader loader) {
    }

    @Override
    public Parcelable saveState() {
        return null;
    }
}