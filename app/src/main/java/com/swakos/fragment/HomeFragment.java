package com.swakos.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;
import com.swakos.R;
import com.swakos.adapter.CategoryAdapter;
import com.swakos.adapter.SliderAdapter;
import com.swakos.adapter.SliderAdapterNew;
import com.swakos.categoriesActivity.RestaurantActivity;
import com.swakos.helper.HelperMethods;
import com.swakos.model.Category;
import com.swakos.model.Update;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;

public class HomeFragment extends Fragment {
    private ViewPager mViewPager;
    private ArrayList<Update> updateList, mViewPagerList;
    private TextView tvLocation;
    private Thread thread;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onPause() {
        super.onPause();
        thread.interrupt();
    }


    @Override
    public void onResume() {
        super.onResume();
        thread = new Thread() {
            @Override
            public void run() {
                try {
                    while (!isInterrupted()) {
                        if (HelperMethods.LOCATION != null)
                            if (HelperMethods.LOCATION.isEmpty()) Thread.sleep(1000);
                            else thread.interrupt();
                        if (getActivity() != null) {
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    // update TextView here!
                                    tvLocation.setText(HelperMethods.LOCATION);
                                    if (HelperMethods.LOCATION != null) Log.d("location", HelperMethods.LOCATION);
                                    // giving a blink animation on TextView
                                    tvLocation.setAlpha(0);
                                    tvLocation.animate().alpha(1);
                                }
                            });
                        }
                    }
                } catch (InterruptedException e) {
                    Log.e("interruptedException", e.toString());
                }
            }
        };
        thread.start();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        tvLocation = view.findViewById(R.id.tv_city);

        RecyclerView recyclerView = view.findViewById(R.id.recyclerView_updates);
        recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext(), RecyclerView.HORIZONTAL, false));

        RecyclerView mCategoryRecyclerView = view.findViewById(R.id.recyclerView_categories);
        mCategoryRecyclerView.setLayoutManager(new GridLayoutManager(view.getContext(), 4, RecyclerView.VERTICAL, false));

        RecyclerView recyclerViewHome = view.findViewById(R.id.recyclerView_home);
        recyclerViewHome.setLayoutManager(new LinearLayoutManager(view.getContext(), RecyclerView.VERTICAL, false));

        updateList = new ArrayList<>();
        updateList.add(new Update("First Item", R.drawable.offer_one));
        updateList.add(new Update("Second Item", R.drawable.offer_two));
        updateList.add(new Update("First Item", R.drawable.offer_three));
        updateList.add(new Update("Second Item", R.drawable.offer_four));
        updateList.add(new Update("First Item", R.drawable.offer_five));

        SliderAdapterNew adapter = new SliderAdapterNew(updateList, view.getContext());
        recyclerView.setAdapter(adapter);

        mViewPagerList = new ArrayList<>();
        mViewPagerList.add(new Update("First Slider Item", R.drawable.update_one));
        mViewPagerList.add(new Update("Second Slider Item", R.drawable.update_two));
        mViewPagerList.add(new Update("Third Slider Item", R.drawable.update_three));

        SliderAdapter sliderAdapter = new SliderAdapter(view.getContext(), mViewPagerList);
        mViewPager = view.findViewById(R.id.viewPager);
        mViewPager.setPageMargin(HelperMethods.pix(getContext(), 12));
        mViewPager.setAdapter(sliderAdapter);
        TabLayout indicator = view.findViewById(R.id.indicator);
        indicator.setupWithViewPager(mViewPager, true);
        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new SliderTimer(), 3000, 6000);

        ArrayList<Category> categoryList = new ArrayList<>();
        categoryList.add(new Category(R.drawable.ic_scissor, "Salon"));
        categoryList.add(new Category(R.drawable.ic_kitchen, "Restaurant"));
        categoryList.add(new Category(R.drawable.ic_gamepad, "Activity"));
        categoryList.add(new Category(R.drawable.ic_luggage, "Hotel"));
        categoryList.add(new Category(R.drawable.ic_running, "Fitness"));
        categoryList.add(new Category(R.drawable.ic_discount, "Hot Deals"));

        CategoryAdapter adapter1 = new CategoryAdapter(categoryList, view.getContext());
        mCategoryRecyclerView.setAdapter(adapter1);

        mCategoryRecyclerView.addOnItemTouchListener(new HelperMethods.RecyclerTouchListener(getContext(), position -> {
            Intent intent;
            switch (position){
                case 0:
                    intent = new Intent(getContext(), RestaurantActivity.class);
                    intent.putExtra("title", "salon");
                    startActivity(intent);
                    break;
                case 1:
                    intent = new Intent(getContext(), RestaurantActivity.class);
                    intent.putExtra("title", "restaurant");
                    startActivity(intent);
                    break;
                case 2:
                    intent = new Intent(getContext(), RestaurantActivity.class);
                    intent.putExtra("title", "activity");
                    startActivity(intent);
                    break;
                case 3:
                    intent = new Intent(getContext(), RestaurantActivity.class);
                    intent.putExtra("title", "hotel");
                    startActivity(intent);
                    break;
                case 4:
                    intent = new Intent(getContext(), RestaurantActivity.class);
                    intent.putExtra("title", "fitness");
                    startActivity(intent);
                    break;
                case 5:
                    intent = new Intent(getContext(), RestaurantActivity.class);
                    intent.putExtra("title", "hot_deals");
                    startActivity(intent);
                    break;
            }
        }));

        //ClientAdapter clientAdapter = new ClientAdapter(mDealsList, view.getContext());
        //recyclerViewHome.setAdapter(clientAdapter);

    }

    private class SliderTimer extends TimerTask {
        @Override
        public void run() {
            if (getActivity() != null){
                getActivity().runOnUiThread(() -> {
                    if (mViewPager.getCurrentItem() < mViewPagerList.size() - 1) {
                        mViewPager.setCurrentItem(mViewPager.getCurrentItem() + 1);
                    } else {
                        mViewPager.setCurrentItem(0);
                    }
                });
            }
        }
    }
}
