package com.swakos.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.FirebaseFirestore;
import com.swakos.MainActivity;
import com.swakos.R;
import com.swakos.model.User;

import java.util.Date;

public class UserDetailsFragment extends Fragment {
    private EditText edTvName, edTVEmail;
    private Button btnDone;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private String userId, phoneNumber, providerId, name, email;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_add_user_details, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        edTvName = view.findViewById(R.id.edtv_username);
        edTVEmail = view.findViewById(R.id.edTv_email);
        btnDone = view.findViewById(R.id.btn_done);


        FirebaseUser user  = mAuth.getCurrentUser();
        //UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder().setPhotoUri();
        //user.updateProfile(profileUpdates)

        btnDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (user != null) {
                    providerId = user.getProviderId();
                    userId = user.getUid();
                    phoneNumber = user.getPhoneNumber();
                }
                name = edTvName.getText().toString().trim();
                email = edTVEmail.getText().toString().trim();
                if (!name.isEmpty() && !email.isEmpty()){
                    User newUser = new User();
                    newUser.setName(name);
                    newUser.setUser_id(userId);
                    newUser.setProvider_id(providerId);
                    newUser.setEmail(email);
                    newUser.setPhone_number(phoneNumber);
                    newUser.setCreated_at(new Timestamp(new Date()));

                    db.collection("users").document(userId).set(newUser).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()){
                                Intent intent = new Intent(getContext(), MainActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent);
                            }else{
                                Toast.makeText(getContext(), task.getException().toString(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        });
    }
}
