package com.swakos.categoriesActivity;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.swakos.ClientActivity;
import com.swakos.R;
import com.swakos.adapter.ClientAdapter;
import com.swakos.helper.HelperMethods;
import com.swakos.model.Deal;
import java.util.ArrayList;

public class SalonActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_salon);

        setSupportActionBar(findViewById(R.id.toolbar_main)); //set toolbar as actionBar

        if (getSupportActionBar()!= null) {
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_back);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true); //Set up action in the action bar
            getSupportActionBar().setTitle(getIntent().getStringExtra("title"));
        }

        RecyclerView clientRecyclerView = findViewById(R.id.recV_client_details);
        clientRecyclerView.setLayoutManager(new LinearLayoutManager(SalonActivity.this, RecyclerView.VERTICAL, false));

        ArrayList<Deal> mDealsList = new ArrayList<>();
        //ClientAdapter clientAdapter = new ClientAdapter(mDealsList, SalonActivity.this);
        //clientRecyclerView.setAdapter(clientAdapter);

        clientRecyclerView.addOnItemTouchListener(new HelperMethods.RecyclerTouchListener(SalonActivity.this, position ->
                startActivity(new Intent(SalonActivity.this, ClientActivity.class))));

    }
}
