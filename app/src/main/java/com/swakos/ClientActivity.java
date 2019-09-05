package com.swakos;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.swakos.adapter.ClientsDealAdapter;
import com.swakos.adapter.TermAdapter;
import com.swakos.helper.HelperMethods;
import com.swakos.model.Deal;
import com.swakos.model.Term;

import java.util.ArrayList;
import java.util.HashMap;

public class ClientActivity extends AppCompatActivity {
    private ImageView banner;
    private TextView tvClientName,  tvClientAddress, tvClientDesc;
    private RecyclerView recyclerViewDeals;
    private String clientDocId, clientId;
    private FirebaseFirestore db;
    private ProgressBar progressBar;
    public static ArrayList<Deal> mDealsList;
    private ClientsDealAdapter dealAdapter;
    private Term termItem;
    private ArrayList<Term> mTermsList;
    private TextView tvDealTitle;
    private String mClientType;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_client);
        banner = findViewById(R.id.banner);

        RecyclerView mTermsRecyclerView = findViewById(R.id.recyclerView_terms);
        tvDealTitle = findViewById(R.id.tv_deal_title);

        db = FirebaseFirestore.getInstance();
        mDealsList = new ArrayList<>();

        String bannerURL = getIntent().getStringExtra("banner_url");
        String clientName = getIntent().getStringExtra("client_name");
        String clientAddress = getIntent().getStringExtra("client_address");
        String clientDesc = getIntent().getStringExtra("client_desc");
        mClientType = getIntent().getStringExtra("client_type");
        clientId = getIntent().getStringExtra("client_id");
        HashMap<String, String> standardTerms = (HashMap<String, String>) getIntent().getSerializableExtra("standard_terms");

        //Log.d("string_desc", clientDesc);

        TextView tvStandardTermsHeading = findViewById(R.id.tv_term_heading);
        mTermsList = new ArrayList<>();

        mTermsRecyclerView.setLayoutManager(new LinearLayoutManager(ClientActivity.this, RecyclerView.VERTICAL, false));
        TermAdapter termAdapter = new TermAdapter(mTermsList, ClientActivity.this);
        mTermsRecyclerView.setAdapter(termAdapter);

        for (String term : standardTerms.values()){
            termItem = new Term();
            termItem.setTerm(term);
            mTermsList.add(termItem);
            termAdapter.setItems(mTermsList);
            termAdapter.notifyDataSetChanged();
        }

        clientDocId = getIntent().getStringExtra("client_doc_id");
        progressBar = findViewById(R.id.progress_bar);

        tvClientAddress = findViewById(R.id.tv_client_address);
        tvClientName = findViewById(R.id.tv_client);
        tvClientDesc = findViewById(R.id.tv_client_desc);

        recyclerViewDeals = findViewById(R.id.recyclerView_client_deals); //Find RecyclerView;
        recyclerViewDeals.setLayoutManager(new LinearLayoutManager(ClientActivity.this, RecyclerView.VERTICAL, false));  //Set Layout Manager

        dealAdapter = new ClientsDealAdapter(ClientActivity.this, mDealsList);
        recyclerViewDeals.setAdapter(dealAdapter);

        recyclerViewDeals.addOnItemTouchListener(new HelperMethods.RecyclerTouchListener(ClientActivity.this, new HelperMethods.ClickListener() {
            @Override
            public void onClick(int position) {
                Intent intent = new Intent(ClientActivity.this, DealActivity.class);
                intent.putExtra("deal_title", mDealsList.get(position).getDeal_title());
                intent.putExtra("deal_document_id", mDealsList.get(position).getDeal_doc_id());
                startActivity(intent);
            }
        }));

        tvClientName.setText(clientName);
        tvClientAddress.setText(clientAddress);
        tvClientDesc.setText(clientDesc);

        if (bannerURL != null) Glide.with(this).load(bannerURL).into(banner);

        getDeals();
    }

    private void getDeals(){
        db.collection("cities")
                .document("meerut")
                .collection(mClientType)
                .document(clientDocId)
                .collection("deals_" + clientId)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()){
                            progressBar.setVisibility(View.GONE);
                            Deal deal;
                            if (task.getResult() != null){
                                for (QueryDocumentSnapshot document: task.getResult()){
                                    deal = document.toObject(Deal.class);
                                    deal.setDeal_doc_id(document.getId());
                                    mDealsList.add(deal);
                                    dealAdapter.setItems(mDealsList);
                                    dealAdapter.notifyDataSetChanged();
                                }
                            }
                        }
                    }
                });
    }
}
