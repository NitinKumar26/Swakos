package com.swakos;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthProvider;
import com.swakos.fragment.RegistrationFragment;
import com.swakos.helper.HelperMethods;

public class SignupActivity extends AppCompatActivity {
    private static final String TAG = "SignupActivity";
    private static final String KEY_VERIFY_IN_PROGRESS = "key_verify_in_progress";

    private static final int STATE_INITIALIZED = 1;
    private static final int STATE_CODE_SENT = 2;
    private static final int STATE_VERIFY_FAILED = 3;
    private static final int STATE_VERIFY_SUCCESS = 4;
    private static final int STATE_SIGNIN_FAILED = 5;
    private static final int STATE_SIGNIN_SUCCESS = 6;

    // [START declare_auth]
    private FirebaseAuth mAuth;
    // [END declare_auth]

    private boolean mVerificationInProgress = false;
    private String mVerificationId;
    private PhoneAuthProvider.ForceResendingToken mResendToken;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;

    //private ViewGroup mPhoneNumberViews;
    //private ViewGroup mSignedInViews;

    //private TextView mStatusText;
    //private TextView mDetailText;

    //private EditText mPhoneNumberField;
    //private EditText mVerificationField;

    //private Button mStartButton;
    //private Button mVerifyButton;
    //private Button mResendButton;
    //private Button mSignOutButton;
    private EditText mPhoneNumberField;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        mAuth = FirebaseAuth.getInstance();

        if (mAuth.getCurrentUser() != null){
            Intent intent = new Intent(SignupActivity.this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        }

        //Restore instance state
        if (savedInstanceState != null) onRestoreInstanceState(savedInstanceState);

        //Assign Views
        mPhoneNumberField = findViewById(R.id.edtv_phone_number);

        HelperMethods.loadFragment(new RegistrationFragment(), SignupActivity.this);

    }
}
