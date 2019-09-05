package com.swakos;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;
import com.swakos.adapter.DealSliderAdapter;
import com.swakos.helper.HelperMethods;

public class DealActivity extends AppCompatActivity {
    private ViewPager dealViewPager;
    private TabLayout tabLayout;
    private DealSliderAdapter adapter;
    private TextView tvItemCounter;
    private ImageView imgClose;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.viewpager_item);

        tabLayout = findViewById(R.id.indicator);
        tvItemCounter = findViewById(R.id.tv_item_counter);
        dealViewPager = findViewById(R.id.viewPager);
        imgClose = findViewById(R.id.img_close);
        dealViewPager.setPageMargin(HelperMethods.pix(DealActivity.this, 20));
        dealViewPager.setPadding(HelperMethods.pix(DealActivity.this, 20), HelperMethods.pix(DealActivity.this, 0),
                HelperMethods.pix(DealActivity.this, 20),HelperMethods.pix(DealActivity.this, 20));

        adapter = new DealSliderAdapter(DealActivity.this, ClientActivity.mDealsList); //Initialize the DealSliderAdapter
        dealViewPager.setAdapter(adapter); //Set the DealSliderAdapter with Viewpager;

        tabLayout.setupWithViewPager(dealViewPager);

        imgClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        int currentPage = dealViewPager.getCurrentItem() + 1 ;

        tvItemCounter.setText(getString(R.string.counter_text, String.valueOf(currentPage), String.valueOf(ClientActivity.mDealsList.size())));

        dealViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                tvItemCounter.setText(getString(R.string.counter_text, String.valueOf(position + 1), String.valueOf(ClientActivity.mDealsList.size())));

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
















    }
}
