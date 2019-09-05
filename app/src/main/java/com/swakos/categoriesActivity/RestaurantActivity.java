package com.swakos.categoriesActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.swakos.ClientActivity;
import com.swakos.R;
import com.swakos.adapter.ClientAdapter;
import com.swakos.helper.HelperMethods;
import com.swakos.model.Client;

import java.util.ArrayList;

public class RestaurantActivity extends AppCompatActivity {
    private FirebaseFirestore db;
    private ArrayList<Client> mClientsList;
    private ClientAdapter clientAdapter;
    private ProgressBar progressBar;
    private String mClientType;
    private String clientId;
    private RecyclerView clientRecyclerView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restaurant);

        setSupportActionBar(findViewById(R.id.toolbar_main)); //set toolbar as actionBar

        if (getSupportActionBar()!= null) {
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_back);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true); //Set up action in the action bar
            getSupportActionBar().setTitle(getIntent().getStringExtra("title"));
        }

        db = FirebaseFirestore.getInstance();
        mClientsList = new ArrayList<>();
        progressBar = findViewById(R.id.progress_bar);
        mClientType = getIntent().getStringExtra("title");

        if (mClientType != null)
            Log.d("client_type", mClientType);


        clientRecyclerView = findViewById(R.id.recV_client_details);
        clientRecyclerView.setLayoutManager(new LinearLayoutManager(RestaurantActivity.this, RecyclerView.VERTICAL, false));

        clientAdapter = new ClientAdapter(mClientsList, RestaurantActivity.this);
        clientRecyclerView.setAdapter(clientAdapter);

        clientRecyclerView.addOnItemTouchListener(new HelperMethods.RecyclerTouchListener(this, new HelperMethods.ClickListener() {
            @Override
            public void onClick(int position) {
                Intent intent = new Intent(RestaurantActivity.this, ClientActivity.class);
                if (mClientsList.get(position).getBanner_url() != null) intent.putExtra("banner_url", mClientsList.get(position).getBanner_url());
                intent.putExtra("client_name", mClientsList.get(position).getName());
                intent.putExtra("client_type", mClientType);
                intent.putExtra("client_address", mClientsList.get(position).getAddress());
                intent.putExtra("client_id", mClientsList.get(position).getId());
                intent.putExtra("client_doc_id", mClientsList.get(position).getDocumentID());
                intent.putExtra("client_desc", mClientsList.get(position).getClient_desc());
                intent.putExtra("standard_terms", mClientsList.get(position).getStandard_terms());
                //Log.d("desc", mClientsList.get(position).getClient_desc());
                startActivity(intent);
            }
        }));

        getClients();
    }
    private void  getClients() {
        db.collection("cities")
                .document("meerut")
                .collection(mClientType)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            progressBar.setVisibility(View.GONE);
                            Client client;
                            if (task.getResult() != null) {
                                for (QueryDocumentSnapshot document : task.getResult()) {
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
