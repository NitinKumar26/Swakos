package com.swakos.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.swakos.categoriesActivity.RestaurantActivity;
import com.swakos.helper.GlideApp;
import com.swakos.model.Update;

import java.util.ArrayList;
import com.swakos.R;

public class SliderAdapter extends PagerAdapter {

    private final Context mContext;
    private ArrayList<Update> mUpdateList;

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
                GlideApp.with(mContext)
                        .load(update.getImage_address())
                        .placeholder(mContext.getResources().getDrawable(R.drawable.placeholder_gradient))
                        .into(imageView);

                view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(view.getContext(), RestaurantActivity.class);
                        intent.putExtra("category", update.getCategory());
                        intent.putExtra("title", update.getCategory());
                        view.getContext().startActivity(intent);
                    }
                });
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

    public void setItems(ArrayList<Update> updates){
        this.mUpdateList = updates;
    }
}