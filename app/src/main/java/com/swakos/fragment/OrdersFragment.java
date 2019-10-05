package com.swakos.fragment;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;
import com.swakos.ActivatedDealActivity;
import com.swakos.R;
import com.swakos.adapter.ClientAdapter;
import com.swakos.adapter.ClientsDealAdapter;
import com.swakos.adapter.OrdersAdapter;
import com.swakos.helper.HelperMethods;
import com.swakos.helper.PrefManager;
import com.swakos.model.ActivateDeal;
import com.swakos.model.Client;
import com.swakos.model.Deal;

import java.io.ByteArrayOutputStream;
import java.sql.Time;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class OrdersFragment extends Fragment {

    private ArrayList<ActivateDeal> dealsArrayList;
    private FirebaseFirestore db;
    private String userDocId;
    private PrefManager prefManager;
    private FirebaseAuth mAuth;
    private OrdersAdapter clientAdapter;
    private String userId;
    private TextView tvEmpty;
    private ProgressBar progressBar;
    private ActivateDeal activateDeal;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_orders, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        if (mAuth.getCurrentUser() != null) userDocId = mAuth.getCurrentUser().getUid();
        if (getContext() != null) prefManager = new PrefManager(getContext());
        userId = prefManager.getUserId();

        dealsArrayList = new ArrayList<>();
        tvEmpty = view.findViewById(R.id.empty_text);
        progressBar = view.findViewById(R.id.progress_bar);
        RecyclerView recyclerView = view.findViewById(R.id.recyclerView_orders);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false));

        clientAdapter = new OrdersAdapter(getContext(), dealsArrayList);
        recyclerView.setAdapter(clientAdapter);

        getClients();

        recyclerView.addOnItemTouchListener(new HelperMethods.RecyclerTouchListener(getContext(), new HelperMethods.ClickListener() {
            @Override
            public void onClick(int position) {
                activateDeal = dealsArrayList.get(position);
                String dealDocId  = dealsArrayList.get(position).getDeal_doc_id();
                Intent intent = new Intent(getContext(), ActivatedDealActivity.class);
                intent.putExtra("activated_deal", activateDeal);
                MultiFormatWriter multiFormatWriter = new MultiFormatWriter();
                try {
                    BitMatrix bitMatrix = multiFormatWriter.encode(dealDocId, BarcodeFormat.QR_CODE,
                            HelperMethods.pix(getContext(), 160), HelperMethods.pix(getContext(), 160));
                    BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
                    Bitmap bitmap = barcodeEncoder.createBitmap(bitMatrix);

                    ByteArrayOutputStream stream = new ByteArrayOutputStream(); //Initialize the ByteArrayOutputStream
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream); //convert & compress the bitmap to jpeg
                    byte[] byteArray = stream.toByteArray(); //add the converted bitmap into a byteArray[]
                    intent.putExtra("bitmap", byteArray); //Send the converted bitmap into intent
                    if (getContext() != null) startActivity(intent);
                }catch(WriterException e){
                    e.printStackTrace();
                }

                /*
                Timestamp dealTimeStamp = dealsArrayList.get(position).getCreated_at();

                Date dealDate = dealTimeStamp.toDate();

                Timestamp timestamp = new Timestamp(new Date());
                Date date = timestamp.toDate();
                Calendar calendar = Calendar.getInstance();
                Log.d("date", String.valueOf(date));
                int compare  = dealDate.compareTo(date);
                if (compare == 0){
                    Log.e("date_result", "dates matched");
                }else{
                    Log.e("date_result_else", String.valueOf(compare));
                }

                 */
            }
        }));


        Log.d("userid", userId);
        Log.d("user_doc_id", userDocId);
    }


    private void  getClients() {
        db.collection("users")
                .document(userDocId)
                .collection("activated_deals").whereEqualTo("status", "pending").get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        progressBar.setVisibility(View.GONE);
                        if (task.isSuccessful()) {
                            ActivateDeal deal;
                            if (task.getResult() != null) {
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    deal = document.toObject(ActivateDeal.class);
                                    deal.setActiveDealDocId(document.getId());
                                    dealsArrayList.add(deal);
                                    clientAdapter.setItems(dealsArrayList);
                                    clientAdapter.notifyDataSetChanged();
                                }

                                if (dealsArrayList.isEmpty()){
                                    tvEmpty.setVisibility(View.VISIBLE);
                                }
                            }
                        }
                    }
                });
    }
}
