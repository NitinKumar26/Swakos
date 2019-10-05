package com.swakos.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.swakos.ClientActivity;
import com.swakos.MainActivity;
import com.swakos.R;
import com.swakos.adapter.ClientAdapter;
import com.swakos.categoriesActivity.RestaurantActivity;
import com.swakos.helper.HelperMethods;
import com.swakos.helper.PrefManager;
import com.swakos.model.Client;

import java.util.ArrayList;

public class FavouritesFragment extends Fragment {
    private ArrayList<Client> mClientsList;
    private ClientAdapter clientAdapter;
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private String userDocId;
    private String userId;
    private PrefManager prefManager;
    private ShimmerFrameLayout shimmerFrameLayout;
    private LinearLayout appBar;
    private RecyclerView recyclerView;
    private TextView textView;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_favourites, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        if (mAuth.getCurrentUser() != null) userDocId = mAuth.getCurrentUser().getUid();
        if (getContext() != null) prefManager = new PrefManager(getContext());
        userId = prefManager.getUserId();

        mClientsList = new ArrayList<>();

        textView = view.findViewById(R.id.tv_empty);

        shimmerFrameLayout = view.findViewById(R.id.shimmer_view_container);
        appBar = view.findViewById(R.id.relative_toolbar);


        recyclerView = view.findViewById(R.id.recyclerView_favourites);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false));
        clientAdapter = new ClientAdapter(mClientsList, getContext());
        recyclerView.setAdapter(clientAdapter);

        getFavourites();


        recyclerView.addOnItemTouchListener(new HelperMethods.RecyclerTouchListener(getContext(), new HelperMethods.ClickListener() {
            @Override
            public void onClick(int position) {
                Intent intent = new Intent(getContext(), ClientActivity.class);
                if (mClientsList.get(position).getBanner_url() != null) intent.putExtra("banner_url", mClientsList.get(position).getBanner_url());
                intent.putExtra("client_name", mClientsList.get(position).getName());
                intent.putExtra("client_type", mClientsList.get(position).getType());
                intent.putExtra("client_address", mClientsList.get(position).getAddress());
                intent.putExtra("client_id", mClientsList.get(position).getId());
                intent.putExtra("client_doc_id", mClientsList.get(position).getDocumentID());
                intent.putExtra("client_desc", mClientsList.get(position).getClient_desc());
                intent.putExtra("standard_terms", mClientsList.get(position).getStandard_terms());
                //Log.d("desc", mClientsList.get(position).getClient_desc());
                startActivity(intent);
            }
        }));
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
                            shimmerFrameLayout.setVisibility(View.GONE);
                            appBar.setVisibility(View.VISIBLE);
                            Client client;
                            if (task.getResult() != null) {
                                recyclerView.setVisibility(View.VISIBLE);
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    client = document.toObject(Client.class);
                                    mClientsList.add(client);
                                    clientAdapter.setItems(mClientsList);
                                    clientAdapter.notifyDataSetChanged();
                                }

                                if (mClientsList.isEmpty()){
                                    textView.setVisibility(View.VISIBLE);
                                }
                            }

                        }
                    }
                });
    }
}
