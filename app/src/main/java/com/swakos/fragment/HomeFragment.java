package com.swakos.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.swakos.ClientActivity;
import com.swakos.R;
import com.swakos.adapter.CategoryAdapter;
import com.swakos.adapter.ClientAdapter;
import com.swakos.adapter.SliderAdapter;
import com.swakos.adapter.SliderAdapterNew;
import com.swakos.categoriesActivity.RestaurantActivity;
import com.swakos.helper.HelperMethods;
import com.swakos.model.Category;
import com.swakos.model.Client;
import com.swakos.model.Update;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;
import butterknife.ButterKnife;

public class HomeFragment extends Fragment {
    private ArrayList<Update> updateList, mViewPagerList;
    private Thread thread, threadDbOp;
    private FirebaseFirestore db;
    private SliderAdapter sliderAdapter;
    private SliderAdapterNew sliderAdapterNew;
    private ArrayList<Client> mClientsList;
    private ClientAdapter clientAdapter;

    @BindView(R.id.tv_city) TextView tvLocation;
    @BindView(R.id.recyclerView_trendings) RecyclerView recyclerViewTrending;
    @BindView(R.id.recyclerView_categories) RecyclerView mCategoryRecyclerView;
    @BindView(R.id.recyclerView_updates) RecyclerView recyclerView;
    @BindView(R.id.viewPager) ViewPager mViewPager;
    @BindView(R.id.indicator) TabLayout indicator;
    @BindView(R.id.shimmer_view_container)
    ShimmerFrameLayout shimmerFrameLayout;
    @BindView(R.id.linear)
    LinearLayout linearLayout;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        //thread.interrupt();
        //threadDbOp.interrupt();
    }

    @Override
    public void onStart() {
        /*
        thread = new Thread() {
            @Override
            public void run() {
                try {
                    while (!isInterrupted()) {
                        if (HelperMethods.LOCATION != null)
                        if (HelperMethods.LOCATION.isEmpty()) Thread.sleep(1000);
                        else Thread.sleep(10000);
                        if (getActivity() != null) {
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    // update TextView here!
                                    tvLocation.setText(HelperMethods.LOCATION);
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

        if (HelperMethods.LOCATION == null || HelperMethods.LOCATION.isEmpty()) thread.start();
        else thread.interrupt();

         */
        if (HelperMethods.LOCATION != null) tvLocation.setText(HelperMethods.LOCATION);
        super.onStart();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        db = FirebaseFirestore.getInstance();

        mClientsList = new ArrayList<>();
        recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext(), RecyclerView.HORIZONTAL, false));
        mCategoryRecyclerView.setLayoutManager(new GridLayoutManager(view.getContext(), 4, RecyclerView.VERTICAL, false));

        recyclerViewTrending.setLayoutManager(new LinearLayoutManager(view.getContext(), RecyclerView.VERTICAL, false));
        clientAdapter = new ClientAdapter(mClientsList, getContext());
        recyclerViewTrending.setAdapter(clientAdapter);

        getTrendings();

        recyclerViewTrending.addOnItemTouchListener(new HelperMethods.RecyclerTouchListener(getContext(), new HelperMethods.ClickListener() {
            @Override
            public void onClick(int position) {
                Client client = mClientsList.get(position);
                Intent intent = new Intent(getContext(), ClientActivity.class);
                intent.putExtra("banner_url", client.getBanner_url());
                intent.putExtra("client_name", client.getName());
                intent.putExtra("client_address", client.getAddress());
                intent.putExtra("client_desc", client.getClient_desc());
                intent.putExtra("client_type", client.getType());
                intent.putExtra("client_id", client.getId());
                intent.putExtra("client_doc_id", client.getDocumentID());
                intent.putExtra("client_desc", client.getClient_desc());
                intent.putExtra("standard_terms", client.getStandard_terms());
                startActivity(intent);
            }
        }));


        updateList = new ArrayList<>();
        sliderAdapterNew = new SliderAdapterNew(updateList, view.getContext());
        recyclerView.setAdapter(sliderAdapterNew);
        getFlex();

        recyclerView.addOnItemTouchListener(new HelperMethods.RecyclerTouchListener(getContext(), new HelperMethods.ClickListener() {
            @Override
            public void onClick(int position) {
                Update update = updateList.get(position);
                Intent intent = new Intent(getContext(), RestaurantActivity.class);
                if (!update.getCategory().isEmpty()){
                    intent.putExtra("category", update.getCategory());
                    intent.putExtra("title", update.getCategory());
                    startActivity(intent);
                }
            }
        }));


        mViewPagerList = new ArrayList<>();
        sliderAdapter = new SliderAdapter(view.getContext(), mViewPagerList);
        mViewPager.setPageMargin(HelperMethods.pix(getContext(), 12));
        mViewPager.setAdapter(sliderAdapter);
        indicator.setupWithViewPager(mViewPager, true);
        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new SliderTimer(), 3000, 6000);

        getUpdates();

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
            Intent intent = new Intent(getContext(), RestaurantActivity.class);
            switch (position){
                case 0:
                    intent.putExtra("title", "Salon");
                    intent.putExtra("category", "salon");
                    startActivity(intent);
                    break;
                case 1:
                    intent.putExtra("title", "Restaurant");
                    intent.putExtra("category", "restaurant");
                    startActivity(intent);
                    break;
                case 2:
                    intent.putExtra("title", "Activity");
                    intent.putExtra("category", "activity");
                    startActivity(intent);
                    break;
                case 3:
                    intent.putExtra("title", "Hotel");
                    intent.putExtra("category", "hotel");
                    startActivity(intent);
                    break;
                case 4:
                    intent.putExtra("title", "Fitness");
                    intent.putExtra("category", "fitness");
                    startActivity(intent);
                    break;
                case 5:
                    intent.putExtra("title", "Hot Deals");
                    intent.putExtra("category", "hot_deals");
                    startActivity(intent);
                    break;
            }
        }));

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

    private void getUpdates(){
        db.collection("updates").whereEqualTo("type", "banner")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()){
                    shimmerFrameLayout.stopShimmer();
                    shimmerFrameLayout.setVisibility(View.GONE);
                    linearLayout.setVisibility(View.VISIBLE);
                    Update update;
                    if (task.getResult() != null){
                        for (QueryDocumentSnapshot document : task.getResult()){
                            update = document.toObject(Update.class);
                            mViewPagerList.add(update);
                            sliderAdapter.setItems(mViewPagerList);
                            sliderAdapter.notifyDataSetChanged();
                        }
                    }
                }
            }
        });
    }

    private void getFlex(){
        db.collection("updates").whereEqualTo("type", "flex").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()){
                    //progressBarFlex.setVisibility(View.GONE);
                    Update update;
                    if (task.getResult() != null){
                        for (QueryDocumentSnapshot document : task.getResult()){
                            update = document.toObject(Update.class);
                            updateList.add(update);
                            sliderAdapterNew.setItems(updateList);
                            sliderAdapterNew.notifyDataSetChanged();
                        }
                    }
                }
            }
        });
    }

    private void getTrendings(){
        db.collectionGroup("clients_list").orderBy("bought", Query.Direction.DESCENDING).limit(20).get()
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e("exception", e.toString());

                    }
                }).addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()){
                    Client client;
                    Log.e("task", task.getResult().toString());
                    if (task.getResult() != null){
                        Log.e("taskREsult", task.getResult().toString());
                        for (QueryDocumentSnapshot document: task.getResult()){
                            client = document.toObject(Client.class);
                            client.setDocumentID(document.getId());
                            mClientsList.add(client);
                            clientAdapter.setItems(mClientsList);
                            clientAdapter.notifyDataSetChanged();
                        }
                    }
                }
            }
        });
    }

}

