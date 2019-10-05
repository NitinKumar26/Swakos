package com.swakos.helper;

import android.app.Activity;
import android.content.Context;
import android.util.DisplayMetrics;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.swakos.R;

public class HelperMethods {


    public static String LOCATION = "";
    public static String CURRENT_POSTAL_CODE = "";

    /**
     * This method can be used to load the fragment in the frameLayout
     * @param fragment the fragment which you want to load in the frame container
     * @param activity the activity in which the method is called
     */
    public static void loadFragment(Fragment fragment, @NonNull AppCompatActivity activity) {
        FragmentTransaction transaction = activity.getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.frame_container, fragment);
        transaction.commit();
    }

    public static void loadFragmentNew(Fragment fragment, FragmentActivity activity){
        FragmentTransaction transaction = activity.getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.frame_container, fragment);
        transaction.commit();
    }

    public static void showFragment(Fragment fragment, AppCompatActivity activity){
        FragmentTransaction transaction = activity.getSupportFragmentManager().beginTransaction();
        //transaction.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out);
        if (fragment.isAdded()) {
            transaction.show(fragment);
            //else transaction.hide(fragment);
        }else {
            transaction.add(R.id.frame_container, fragment);
            transaction.show(fragment);
        }
        transaction.commit();
    }

    public static void hideFragment(Fragment fragment, AppCompatActivity activity){
        FragmentTransaction transaction = activity.getSupportFragmentManager().beginTransaction();
        //transaction.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out);
        if (fragment.isAdded())
            transaction.hide(fragment);
        transaction.commit();
    }

    public static int pix(Context activity, int dp){
        DisplayMetrics metrics = new DisplayMetrics();
        AppCompatActivity appCompatActivity = (AppCompatActivity) activity;
        appCompatActivity.getWindowManager().getDefaultDisplay().getMetrics(metrics);
        float logincalDensity = metrics.density;
        return (int) Math.ceil(dp * logincalDensity);
    }

    public static int getPageMargin(@NonNull Activity activity){
        DisplayMetrics metrics = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(metrics);
        return ((metrics.widthPixels / 3) * 2);
    }



    public interface ClickListener {
        void onClick(int position);
    }

    public static class RecyclerTouchListener implements RecyclerView.OnItemTouchListener {
        private final GestureDetector gestureDetector;

        private final HelperMethods.ClickListener clickListener;

        public RecyclerTouchListener(Context context, final ClickListener clickListener){
            this.clickListener = clickListener;
            gestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener(){
                @Override
                public boolean onSingleTapUp(MotionEvent e) {
                    return true;
                }

            });
        }

        @Override
        public boolean onInterceptTouchEvent(@NonNull RecyclerView recyclerView, @NonNull MotionEvent motionEvent) {
            View child = recyclerView.findChildViewUnder(motionEvent.getX(),motionEvent.getY());
            if (child!=null && clickListener != null && gestureDetector.onTouchEvent(motionEvent)){
                clickListener.onClick(recyclerView.getChildAdapterPosition(child));
            }
            return false;
        }

        @Override
        public void onTouchEvent(@NonNull RecyclerView recyclerView, @NonNull MotionEvent motionEvent) {

        }

        @Override
        public void onRequestDisallowInterceptTouchEvent(boolean b) {

        }
    }



}
