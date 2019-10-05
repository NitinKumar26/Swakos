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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.swakos.adapter.DealFragmentPagerAdapter;
import com.swakos.helper.HelperMethods;

import butterknife.BindView;
import butterknife.ButterKnife;

public class DealActivity extends AppCompatActivity {
    //private DealSliderAdapter adapter;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    @BindView(R.id.indicator) TabLayout tabLayout;
    @BindView(R.id.tv_item_counter) TextView tvItemCounter;
    @BindView(R.id.viewPager) ViewPager dealViewPager;
    @BindView(R.id.img_close) ImageView imgClose;
    public static String clientName, clientAddress, clientPhoneNumber, client_doc_id;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.viewpager_item);

        ButterKnife.bind(this);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        dealViewPager.setPageMargin(HelperMethods.pix(DealActivity.this, 24));
        dealViewPager.setClipToPadding(false);
        dealViewPager.setPadding(HelperMethods.pix(DealActivity.this, 20), HelperMethods.pix(DealActivity.this, 0),
                HelperMethods.pix(DealActivity.this, 20),HelperMethods.pix(DealActivity.this, 20));

        //adapter = new DealSliderAdapter(DealActivity.this, ClientActivity.mDealsList, mAuth, db); //Initialize the DealSliderAdapter
        DealFragmentPagerAdapter dealSliderAdapter = new DealFragmentPagerAdapter(getSupportFragmentManager());
        dealViewPager.setAdapter(dealSliderAdapter); //Set the DealSliderAdapter with Viewpager;

        tabLayout.setupWithViewPager(dealViewPager);
        int currentPosition;
        String position = getIntent().getStringExtra("position");
        clientName = getIntent().getStringExtra("client_name");
        clientAddress = getIntent().getStringExtra("client_address");
        clientPhoneNumber = getIntent().getStringExtra("client_contact");
        client_doc_id = getIntent().getStringExtra("client_document_id");

        if (position != null) {
            currentPosition = Integer.parseInt(position);
            dealViewPager.setCurrentItem(currentPosition);
        }

        Log.e("client_name", clientName);

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

    private void getActivatedDeals(){
        String userId = null;
        if (mAuth.getCurrentUser() != null)
            userId = mAuth.getCurrentUser().getUid();
        if (userId != null)
            db.collection("users")
                    .document(userId)
                    .collection("activated_deals").document();
    }
}
