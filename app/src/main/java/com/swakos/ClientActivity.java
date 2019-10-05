package com.swakos;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.swakos.adapter.ClientsDealAdapter;
import com.swakos.adapter.TermAdapter;
import com.swakos.helper.HelperMethods;
import com.swakos.helper.PrefManager;
import com.swakos.model.ActivateDeal;
import com.swakos.model.Client;
import com.swakos.model.Deal;
import com.swakos.model.Term;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

public class ClientActivity extends AppCompatActivity implements View.OnClickListener{
    private ImageView banner, icFav;
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
    private FirebaseAuth mAuth;
    private String clientName;
    private String clientAddress;
    private String mClientContact;
    private String userDocId, userId;
    private PrefManager prefManager;
    private String bannerURL, clientDesc;
    private HashMap<String, String> standardTerms;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_client);

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        if (mAuth.getCurrentUser() != null) userDocId = mAuth.getCurrentUser().getUid();
        prefManager = new PrefManager(getApplicationContext());
        userId = prefManager.getUserId();

        banner = findViewById(R.id.banner);
        tvDealTitle = findViewById(R.id.tv_deal_title);
        progressBar = findViewById(R.id.progress_bar);
        icFav = findViewById(R.id.ic_fav);
        recyclerViewDeals = findViewById(R.id.recyclerView_client_deals); //Find RecyclerView;
        tvClientAddress = findViewById(R.id.tv_client_address);
        tvClientName = findViewById(R.id.tv_client);
        tvClientDesc = findViewById(R.id.tv_client_desc);

        RecyclerView mTermsRecyclerView = findViewById(R.id.recyclerView_terms);

        icFav.setOnClickListener(this);

        mDealsList = new ArrayList<>();
        bannerURL = getIntent().getStringExtra("banner_url");
        clientName = getIntent().getStringExtra("client_name");
        clientAddress = getIntent().getStringExtra("client_address");
        clientDesc = getIntent().getStringExtra("client_desc");
        mClientType = getIntent().getStringExtra("client_type");
        clientId = getIntent().getStringExtra("client_id");
        standardTerms = (HashMap<String, String>) getIntent().getSerializableExtra("standard_terms");
        clientDocId = getIntent().getStringExtra("client_doc_id");
        mClientContact = getIntent().getStringExtra("client_contact");

        //Log.d("client_type", mClientType);
        //Log.d("client_doc_id", clientDocId);


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

        recyclerViewDeals.setLayoutManager(new LinearLayoutManager(ClientActivity.this, RecyclerView.VERTICAL, false));  //Set Layout Manager
        dealAdapter = new ClientsDealAdapter(ClientActivity.this, mDealsList);
        recyclerViewDeals.setAdapter(dealAdapter);

        recyclerViewDeals.addOnItemTouchListener(new HelperMethods.RecyclerTouchListener(ClientActivity.this, new HelperMethods.ClickListener() {
            @Override
            public void onClick(int position) {
                Intent intent = new Intent(ClientActivity.this, DealActivity.class);
                intent.putExtra("client_name", clientName);
                intent.putExtra("client_address", clientAddress);
                intent.putExtra("client_document_id", clientDocId);
                intent.putExtra("client_contact", mClientContact);
                intent.putExtra("deal_title", mDealsList.get(position).getDeal_title());
                intent.putExtra("price", mDealsList.get(position).getDeal_actual_price());
                intent.putExtra("deal_document_id", mDealsList.get(position).getDeal_doc_id());
                intent.putExtra("position", String.valueOf(position));
                startActivity(intent);
            }
        }));

        tvClientName.setText(clientName);
        tvClientAddress.setText(clientAddress);
        tvClientDesc.setText(clientDesc);

        if (bannerURL != null) Glide.with(this).load(bannerURL).into(banner);

        getDeals();
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.ic_fav:
                if (icFav.isSelected()) {
                    icFav.setImageResource(R.drawable.ic_hearts_grey);
                    icFav.setSelected(false);
                }
                else {
                    icFav.setImageResource(R.drawable.ic_hearts_color);
                    icFav.setSelected(true);
                    setFavourite();
                }
                break;

        }
    }

    private void getDeals(){
        db.collection("available_cities")
                .document("meerut")
                .collection("data")
                .document(mClientType)
                .collection("clients_list")
                .document(clientDocId)
                .collection("deals")
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
                                    if (deal.getStandard_terms() == null){
                                        HashMap<String, String> terms = new HashMap<>();
                                        terms.put("first", "first");
                                        deal.setStandard_terms(terms);
                                    }
                                    Log.e("deal", "data : " + document.getData());
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

    private void  setFavourite() {
        Client favClient= new Client();
        favClient.setName(clientName);
        favClient.setDocumentID(clientDocId);
        favClient.setAddress(clientAddress);
        favClient.setBanner_url(bannerURL);
        favClient.setClient_desc(clientDesc);
        favClient.setStandard_terms(standardTerms);
        favClient.setType(mClientType);
        favClient.setCreated_at(new Timestamp(new Date()));
        favClient.setId(clientId);
        //client.setContact_number();
        //client.setRatings(rating);
        db.collection("users")
                .document(userDocId)
                .collection("favourites_" + userId).document(clientDocId).set(favClient).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    Toast.makeText(ClientActivity.this, "Successfully added to your favourites!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void  getFavourites() {
        db.collection("users")
                .document(userDocId)
                .collection("favourites_" + userId)
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
                                    if (client.getDocumentID().equals(clientDocId)){
                                        icFav.setImageResource(R.drawable.ic_hearts_color);
                                        icFav.setSelected(true);
                                    }
                                }
                            }
                        }
                    }
                });
    }

}
