package com.swakos.fragment;

import android.content.Intent;
import android.net.Uri;
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
import com.swakos.AllOrdersActivity;
import com.swakos.BuildConfig;
import com.swakos.SignupActivity;
import com.swakos.SuggestionActivity;
import com.swakos.adapter.ProfileAdapter;
import com.swakos.helper.HelperMethods;
import com.swakos.helper.PrefManager;
import com.swakos.model.ProfileItem;
import com.swakos.R;
import com.swakos.helper.DividerItemDecorator;
import com.swakos.model.User;

import java.util.ArrayList;

public class ProfileFragment extends Fragment {
    private RecyclerView.ItemDecoration dividerItemDecoration;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private String userId;
    private TextView tvUserName, tvContact, tvVersionName;
    private User user;
    private PrefManager prefManager;

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
        if (getContext() != null) prefManager = new PrefManager(getContext());
        String username = prefManager.getUserName();
        String emailOrPhone = prefManager.getUserPhoneOrEmail();

        //getSetUserDetails();

        RecyclerView recyclerView = view.findViewById(R.id.recyclerView_profile);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false));

        TextView tvLogout = view.findViewById(R.id.tv_logout);
        tvContact = view.findViewById(R.id.user_contact);
        tvUserName = view.findViewById(R.id.user_name);
        tvVersionName = view.findViewById(R.id.tv_versionName);

        tvVersionName.setText(getString(R.string.app_version, BuildConfig.VERSION_NAME));

        tvUserName.setText(username);
        tvContact.setText(emailOrPhone);

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

        recyclerView.addOnItemTouchListener(new HelperMethods.RecyclerTouchListener(getContext(), new HelperMethods.ClickListener() {
            @Override
            public void onClick(int position) {
                Intent intent = new Intent(getContext(), AllOrdersActivity.class);
                switch (position){
                    case 0:
                        intent.putExtra("status", "all");
                        startActivity(intent);
                        break;
                    case 1:
                        intent.putExtra("status", "pending");
                        startActivity(intent);
                        break;
                    case 2:
                        intent.putExtra("status", "finished");
                        startActivity(intent);
                        break;
                    case 3:
                        Intent sendIntent = new Intent();
                        sendIntent.setAction(Intent.ACTION_SEND);
                        sendIntent.putExtra(Intent.EXTRA_TEXT, "https://play.google.com/store/apps/details?id=com.swakos");
                        sendIntent.setType("text/plain");
                        startActivity(sendIntent);
                        break;
                    case 4:
                        String number = "8535076580";
                        Uri callUri = Uri.parse("tel:" + number);
                        //Intent callIntent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + "8535076580"));
                        Intent callIntent = new Intent(Intent.ACTION_DIAL, callUri);
                        startActivity(callIntent);
                        break;
                    case 5:
                        Intent intentRate = new Intent("android.intent.action.VIEW", Uri.parse("https://play.google.com/store/apps/details?id=com.swakos"));
                        startActivity(intentRate);
                        break;
                    case 6:
                        startActivity(new Intent(getContext(), SuggestionActivity.class));
                        break;
                }
            }
        }));


        tvLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mAuth.signOut();
                prefManager.clearSession();
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
                        if (user.getPhone_number() == null) tvContact.setText(user.getEmail());
                        else tvContact.setText(user.getPhone_number());
                    }
                }
            });

        }
    }
}
