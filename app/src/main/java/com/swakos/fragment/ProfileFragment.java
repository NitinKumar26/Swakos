package com.swakos.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.swakos.SignupActivity;
import com.swakos.adapter.ProfileAdapter;
import com.swakos.model.ProfileItem;
import com.swakos.R;
import com.swakos.helper.DividerItemDecorator;
import com.swakos.model.User;

import java.util.ArrayList;

public class ProfileFragment extends Fragment {
    private RecyclerView.ItemDecoration dividerItemDecoration;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private String userId, userName, userEmail;
    private TextView tvUserName, tvContact;
    private User user;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        getSetUserDetails();

        RecyclerView recyclerView = view.findViewById(R.id.recyclerView_profile);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false));

        TextView tvLogout = view.findViewById(R.id.tv_logout);
        tvContact = view.findViewById(R.id.user_contact);
        tvUserName = view.findViewById(R.id.user_name);


        if (getContext() != null)
            dividerItemDecoration = new DividerItemDecorator(ContextCompat.getDrawable(getContext(), R.drawable.divider));

        recyclerView.addItemDecoration(dividerItemDecoration);

        ArrayList<ProfileItem> mProfileItemsList = new ArrayList<>();
        mProfileItemsList.add(new ProfileItem("All My Orders", R.drawable.ic_list));
        mProfileItemsList.add(new ProfileItem("Pending Deals", R.drawable.ic_pending_payment));
        mProfileItemsList.add(new ProfileItem("Finished Deals", R.drawable.ic_finished));
        mProfileItemsList.add(new ProfileItem("Invite Friends", R.drawable.ic_invite));
        mProfileItemsList.add(new ProfileItem("Customer Support", R.drawable.ic_support));
        mProfileItemsList.add(new ProfileItem("Rate Our App", R.drawable.ic_rate));
        mProfileItemsList.add(new ProfileItem("Make a Suggestion", R.drawable.ic_suggest));

        ProfileAdapter adapter = new ProfileAdapter(mProfileItemsList, getContext());
        recyclerView.setAdapter(adapter);

        tvLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mAuth.signOut();
                Intent intent = new Intent(getContext(), SignupActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        });
    }

    private void getSetUserDetails(){
        if (mAuth.getCurrentUser() != null){
            userId = mAuth.getUid();

            db.collection("users")
                    .document(userId)
                    .get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    user = documentSnapshot.toObject(User.class);
                    if (user != null) {
                        tvUserName.setText(user.getName());
                        tvContact.setText(user.getPhone_number());
                    }
                }
            });

        }
    }
}
