package com.swakos;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.swakos.helper.PrefManager;
import java.util.HashMap;
import java.util.Map;

public class SuggestionActivity extends AppCompatActivity {
    private EditText edTvSuggestion;
    private ImageView imgUp;
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private PrefManager prefManager;
    private String userDocId;
    private String userId;
    private String userName, userContact;
    private ProgressBar progressBar;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_suggestion);

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        if (mAuth.getCurrentUser() != null) userDocId = mAuth.getCurrentUser().getUid();
        prefManager = new PrefManager(getApplicationContext());
        userId = prefManager.getUserId();
        userName = prefManager.getUserName();
        userContact = prefManager.getUserPhoneOrEmail();

        edTvSuggestion = findViewById(R.id.edTv_suggestion);
        progressBar = findViewById(R.id.progress_bar);

        imgUp = findViewById(R.id.ic_up);

        imgUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        Button btnSuggestion = findViewById(R.id.btn_suggestion);
        btnSuggestion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                submitSuggestion();
            }
        });
    }


    private void submitSuggestion(){
        if (!edTvSuggestion.getText().toString().isEmpty()) {
            progressBar.setVisibility(View.VISIBLE);
            Map<String, Object> suggestionMap = new HashMap<>();
            suggestionMap.put("suggestion", edTvSuggestion.getText().toString());
            suggestionMap.put("userId", userId);
            suggestionMap.put("user_contact", userContact);
            suggestionMap.put("user_name", userName);
            db.collection("suggestions")
                    .document()
                    .set(suggestionMap)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            progressBar.setVisibility(View.GONE);
                            edTvSuggestion.setText("");
                            Toast.makeText(SuggestionActivity.this, "Thank you for your valuable feedback and suggestions", Toast.LENGTH_LONG).show();

                            //relativeBuyCard.setVisibility(View.GONE);
                        }
                    });
        }else{
            Toast.makeText(SuggestionActivity.this, "Please enter your feedback or suggestion.", Toast.LENGTH_LONG).show();
        }
    }
}
