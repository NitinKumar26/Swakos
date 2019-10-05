package com.swakos.adapter;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import com.swakos.ClientActivity;
import com.swakos.fragment.DealSliderFragment;

public class DealFragmentPagerAdapter extends FragmentStatePagerAdapter {

    public DealFragmentPagerAdapter(FragmentManager fm){
        super(fm);
    }

    @NonNull
    @Override
    public Fragment getItem(int position){
        Bundle bundle = new Bundle();
        bundle.putInt("position", position);
        DealSliderFragment dealSliderFragment = new DealSliderFragment();
        dealSliderFragment.setArguments(bundle);
        dealSliderFragment.setArguments(bundle);
        return dealSliderFragment;
    }

    @Override
    public int getCount() {
        return ClientActivity.mDealsList.size();
    }
}
