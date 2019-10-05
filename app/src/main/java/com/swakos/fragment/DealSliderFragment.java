package com.swakos.fragment;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Paint;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;
import com.swakos.ClientActivity;
import com.swakos.DealActivity;
import com.swakos.DealQRActivity;
import com.swakos.PopupCancelActivity;
import com.swakos.R;
import com.swakos.adapter.TermAdapter;
import com.swakos.helper.HelperMethods;
import com.swakos.helper.PrefManager;
import com.swakos.model.ActivateDeal;
import com.swakos.model.Deal;
import com.swakos.model.Term;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class DealSliderFragment extends Fragment {

    //@BindView(R.id.deal_title)
    TextView tvDealTitle;
    //@BindView(R.id.tv_rupee)
    TextView tvRuppee;
    //@BindView(R.id.tv_deal_actual_price)
    TextView tvDealPrice;
    //@BindView(R.id.tv_deal_off_price)
    TextView tvDealOffPrice;
    ///@BindView(R.id.tv_activate_now)
    TextView tvActivateNow;
    //@BindView(R.id.progress_bar)
    ProgressBar progressBar;
    //@BindView(R.id.buy_card)
    RelativeLayout relativeBuyCard;

    private int currentPageNumber;
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private String userDocId;
    private String userId;
    private Deal thisDeal;
    private PrefManager prefManager;
    private ActivateDeal activateDeal;
    private RecyclerView mRecyclerViewDealConditions;
    private ArrayList<Term> termArrayList;
    private HashMap<String, String> standardTerms;
    private Term termItem;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.layout_deal_item, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (getArguments() != null) currentPageNumber = getArguments().getInt("position");

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        if (mAuth.getCurrentUser() != null) userDocId = mAuth.getCurrentUser().getUid();
        if (getContext() != null) prefManager = new PrefManager(getContext());
        userId = prefManager.getUserId();

        termArrayList = new ArrayList<>();

        //ButterKnife.bind(view);

        tvDealTitle = view.findViewById(R.id.deal_title);
        tvRuppee = view.findViewById(R.id.tv_rupee);
        tvDealPrice = view.findViewById(R.id.tv_deal_actual_price);
        tvDealOffPrice = view.findViewById(R.id.tv_deal_off_price);
        tvActivateNow = view.findViewById(R.id.tv_activate_now);
        progressBar = view.findViewById(R.id.progress_bar);
        relativeBuyCard = view.findViewById(R.id.buy_card);

        mRecyclerViewDealConditions = view.findViewById(R.id.recyclerView_deal_conditions);

        thisDeal = ClientActivity.mDealsList.get(currentPageNumber);

        if (String.valueOf(thisDeal.getDeal_actual_price()).equals("0")) tvRuppee.setVisibility(View.GONE);
        else {
            tvDealPrice.setPaintFlags(tvDealPrice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG); //Set paint flags on the actual price
            tvDealPrice.setText(String.valueOf(thisDeal.getDeal_actual_price()));
            tvDealOffPrice.setText(String.valueOf(thisDeal.getDeal_discounted_price()));
        }

        tvDealTitle.setText(thisDeal.getDeal_title());

        relativeBuyCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //activateDeal();
                getPendingDeals();
            }
        });


        standardTerms = thisDeal.getStandard_terms();
        TermAdapter termAdapter = new TermAdapter(termArrayList, getContext());
        mRecyclerViewDealConditions.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false));
        mRecyclerViewDealConditions.setAdapter(termAdapter);

        Log.e("hasmap_values", String.valueOf(standardTerms.values()));

            for (String term : standardTerms.values()) {
                termItem = new Term();
                termItem.setTerm(term);
                termArrayList.add(termItem);
                termAdapter.setItems(termArrayList);
                termAdapter.notifyDataSetChanged();
        }
    }

    private void activateDeal(){
        Map<String, Object> client_details = new HashMap<>();
        client_details.put("name", DealActivity.clientName);
        client_details.put("address", DealActivity.clientAddress);
        client_details.put("contact", DealActivity.clientPhoneNumber);
        client_details.put("client_doc_id", DealActivity.client_doc_id);
        activateDeal = new ActivateDeal();
        activateDeal.setDeal_title(thisDeal.getDeal_title());
        activateDeal.setDeal_doc_id(thisDeal.getDeal_doc_id());
        activateDeal.setDeal_id(thisDeal.getDeal_id());
        activateDeal.setCreated_at(new Timestamp(new Date()));
        activateDeal.setClient_details(client_details);
        activateDeal.setDeal_actual_price(thisDeal.getDeal_actual_price());
        activateDeal.setDeal_discounted_price(thisDeal.getDeal_discounted_price());
        activateDeal.setStatus("pending");
        activateDeal.setStatus_client_doc_id("pending_" + DealActivity.client_doc_id);
        db.collection("users")
                .document(userDocId)
                .collection("activated_deals")
                .document()
                .set(activateDeal)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        progressBar.setVisibility(View.GONE);
                        tvActivateNow.setVisibility(View.VISIBLE);
                        //Toast.makeText(mContext, "Deal Activated", Toast.LENGTH_SHORT).show();
                        String dealDocId = thisDeal.getDeal_doc_id();
                        Intent intent = new Intent(getContext(), DealQRActivity.class);
                        intent.putExtra("deal_title", thisDeal.getDeal_title());
                        intent.putExtra("userDocId", userDocId);

                        //relativeBuyCard.setVisibility(View.GONE);

                        MultiFormatWriter multiFormatWriter = new MultiFormatWriter();
                        try{
                            BitMatrix bitMatrix = multiFormatWriter.encode(dealDocId, BarcodeFormat.QR_CODE,
                                    HelperMethods.pix(getContext(), 160), HelperMethods.pix(getContext(), 160));
                            BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
                            Bitmap bitmap = barcodeEncoder.createBitmap(bitMatrix);

                            ByteArrayOutputStream stream = new ByteArrayOutputStream(); //Initialize the ByteArrayOutputStream
                            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream); //convert & compress the bitmap to jpeg
                            byte[] byteArray = stream.toByteArray(); //add the converted bitmap into a byteArray[]
                            intent.putExtra("bitmap", byteArray); //Send the converted bitmap into intent
                            //activateDeal(dealDocId);
                            if (getContext() != null) getContext().startActivity(intent);
                        }catch (WriterException e){
                            e.printStackTrace();
                        }
                    }
                });
    }


    private void getPendingDeals(){
        progressBar.setVisibility(View.VISIBLE);
        tvActivateNow.setVisibility(View.GONE);
        db.collection("users")
                .document(userDocId)
                .collection("activated_deals")
                .whereEqualTo("status_client_doc_id", "pending_" + DealActivity.client_doc_id)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    /*
                    if (task.getResult().isEmpty())
                        Toast.makeText(getContext(), "No activated deals", Toast.LENGTH_SHORT).show();
                    if (task.isSuccessful()){
                        //progressBar.setVisibility(View.GONE);
                        if (task.getResult() != null) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d("result", document.getId() + "=>" + document.getData());

                            }
                        }else{
                            Log.d("result", "Error getting documents:", task.getException());
                        }
                    }
                     */

                    if (task.getResult() != null){
                        if (!task.getResult().isEmpty()){
                            if (getContext() != null) {
                                //relativeBuyCard.setBackground(getContext().getResources().getDrawable(R.drawable.bg_little));
                                //tvActivateNow.setTextColor(getContext().getResources().getColor(R.color.colorBlack));
                                //tvActivateNow.setText("Cancel Previous Deal");
                                //Popup message
                                progressBar.setVisibility(View.GONE);
                                tvActivateNow.setVisibility(View.VISIBLE);
                                ActivateDeal activateDeal = null;
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    //Log.d("result", document.getId() + "=>" + document.getData());
                                    //document.getData().get("deal_title");
                                    //Log.d("name", String.valueOf(document.getData().get("deal_title")));
                                    activateDeal = document.toObject(ActivateDeal.class);
                                    activateDeal.setActiveDealDocId(document.getId());
                                }
                                Intent intent = new Intent(getContext(), PopupCancelActivity.class);
                                intent.putExtra("client_name", DealActivity.clientName);
                                intent.putExtra("activated_deal", activateDeal);
                                //Log.e("client_name_slider", DealActivity.clientName);
                                startActivity(intent);
                            }
                        }else{

                            activateDeal();
                        }
                    }
                }
        });
    }


}
