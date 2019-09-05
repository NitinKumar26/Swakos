package com.swakos;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        auth = FirebaseAuth.getInstance();
        TextView tvSignUpText = findViewById(R.id.tv_signup_now);

        tvSignUpText.setOnClickListener(view -> startActivity(new Intent(LoginActivity.this, SignupActivity.class)));

        Button loginButton = findViewById(R.id.login_button);
        loginButton.setOnClickListener(view -> startActivity(new Intent(LoginActivity.this, MainActivity.class)));

        //HelperMethods.loadFragment(new LoginFragment(), LoginActivity.this);
    }

    @Override
    protected void onStart() {
        super.onStart();

        if (auth.getCurrentUser() != null) {
            startActivity(new Intent(LoginActivity.this, MainActivity.class));
            finish();
        }
    }
}
