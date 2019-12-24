package com.open_source.adapter;

import android.content.Context;
import android.os.Parcelable;
import androidx.viewpager.widget.PagerAdapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import com.bogdwellers.pinchtozoom.ImageMatrixTouchHandler;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.open_source.R;
import java.util.ArrayList;

public class PagerAdpter extends PagerAdapter {
    private static final String TAG = PagerAdpter.class.getSimpleName();
    ArrayList<String> IMAGES = new ArrayList<>();
    private LayoutInflater inflater;
    private Context context;


    public PagerAdpter(Context context, ArrayList<String> IMAGES) {
        this.context = context;
        this.IMAGES = IMAGES;
        inflater = LayoutInflater.from(context);
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
        View imageLayout = inflater.inflate(R.layout.chat_image_view, view, false);
        final ImageView imageView = (ImageView) imageLayout.findViewById(R.id.img_chat);
        Glide.with(context).load(IMAGES.get(position)).apply(requestOptions).into(imageView);
        view.addView(imageLayout, 0);
        ImageMatrixTouchHandler imageMatrixTouchHandler = new ImageMatrixTouchHandler(context);
        imageView.setOnTouchListener(imageMatrixTouchHandler);
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
