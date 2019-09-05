package com.swakos.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.bumptech.glide.Glide;
import com.swakos.model.Update;

import java.util.ArrayList;
import com.swakos.R;

public class SliderAdapter extends PagerAdapter {

    private final Context mContext;
    private final ArrayList<Update> mUpdateList;

    public SliderAdapter(Context context, ArrayList<Update> updateList) {
        this.mContext = context;
        this.mUpdateList = updateList;
    }

    @Override
    public int getCount() {
        return mUpdateList.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        Update update = mUpdateList.get(position);
        View view = null;
        if (mContext != null){
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            if (inflater != null){
                view = inflater.inflate(R.layout.slider_item, container, false);
                ImageView imageView = view.findViewById(R.id.slider_imageView);
                Glide.with(mContext)
                        .load(update.getmImageResourceId())
                        .into(imageView);
            }
            ViewPager viewPager = (ViewPager) container;
            viewPager.addView(view, 0);
        }
        return view;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        ViewPager viewPager = (ViewPager) container;
        View view = (View) object;
        viewPager.removeView(view);
    }
}