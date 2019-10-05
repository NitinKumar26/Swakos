package com.swakos;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.swakos.adapter.OrdersAdapter;
import com.swakos.helper.PrefManager;
import com.swakos.model.ActivateDeal;

import java.util.ArrayList;

public class AllOrdersActivity extends AppCompatActivity {
    String type;
    TextView tvTitle;
    private String userDocId;
    private String userId;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private PrefManager prefManager;
    private ProgressBar progressBar;
    private ArrayList<ActivateDeal> dealsArrayList;
    private OrdersAdapter clientAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_orders);

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        if (mAuth.getCurrentUser() != null) userDocId = mAuth.getCurrentUser().getUid();
        prefManager = new PrefManager(getApplicationContext());
        userId = prefManager.getUserId();

        ImageView imgUp = findViewById(R.id.ic_up);

        imgUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        dealsArrayList = new ArrayList<>();
        clientAdapter = new OrdersAdapter(AllOrdersActivity.this, dealsArrayList);
        tvTitle = findViewById(R.id.tv_title);
        progressBar = findViewById(R.id.progress_bar);
        RecyclerView recyclerView = findViewById(R.id.recyclerView_orders);
        recyclerView.setLayoutManager(new LinearLayoutManager(AllOrdersActivity.this, RecyclerView.VERTICAL, false));
        recyclerView.setAdapter(clientAdapter);
        type = getIntent().getStringExtra("status");

        //Set Title
        if (type != null) {
            switch (type) {
                case "all":
                    tvTitle.setText("All My Orders");
                    getClients(type);
                    break;
                case "pending":
                    tvTitle.setText("Pending Orders");
                    getClients(type);
                    break;
                case "finished":
                    tvTitle.setText("Finished Deals");
                    getClients(type);
                    break;
            }
        }


    }

    private void  getClients(String type) {
        if (type.equals("pending") || type.equals("finished")) {
            db.collection("users")
                    .document(userDocId)
                    .collection("activated_deals").whereEqualTo("status", type).get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                progressBar.setVisibility(View.GONE);
                                ActivateDeal deal;
                                if (task.getResult() != null) {
                                    for (QueryDocumentSnapshot document : task.getResult()) {
                                        deal = document.toObject(ActivateDeal.class);
                                        dealsArrayList.add(deal);
                                        clientAdapter.setItems(dealsArrayList);
                                        clientAdapter.notifyDataSetChanged();
                                    /*
                                    if (dealsArrayList.isEmpty()){
                                        tvEmpty.setVisibility(View.VISIBLE);
                                    }

                                     */
                                    }
                                }
                            }
                        }
                    });
        }else{

            db.collection("users")
                    .document(userDocId)
                    .collection("activated_deals").orderBy("created_at", Query.Direction.DESCENDING).get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                progressBar.setVisibility(View.GONE);
                                ActivateDeal deal;
                                if (task.getResult() != null) {
                                    for (QueryDocumentSnapshot document : task.getResult()) {
                                        deal = document.toObject(ActivateDeal.class);
                                        dealsArrayList.add(deal);
                                        clientAdapter.setItems(dealsArrayList);
                                        clientAdapter.notifyDataSetChanged();
                                    /*
                                    if (dealsArrayList.isEmpty()){
                                        tvEmpty.setVisibility(View.VISIBLE);
                                    }

                                     */
                                    }
                                }
                            }
                        }
                    });

        }
    }
}
