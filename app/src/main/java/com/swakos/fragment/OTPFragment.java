package com.swakos.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskExecutors;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.swakos.MainActivity;
import com.swakos.R;
import com.swakos.helper.HelperMethods;

import java.util.concurrent.TimeUnit;

public class OTPFragment extends Fragment implements View.OnClickListener{
    private FirebaseAuth mAuth;
    private String phoneNumber;
    private String verificationId;
    private EditText editText;
    private ProgressBar progressBar;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_otp_verify, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //Get the firebase auth instance
        mAuth = FirebaseAuth.getInstance();


        editText = view.findViewById(R.id.otp_field);
        progressBar = view.findViewById(R.id.progress_bar);
        view.findViewById(R.id.verify_button).setOnClickListener(this);

        Bundle bundle = getArguments();
        if (bundle != null)
            phoneNumber = bundle.getString("phoneNumber");

        sendVerificationCode(phoneNumber);
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.verify_button){
            if (editText.getText().toString().isEmpty() || editText.getText().toString().length() < 6){
                editText.setError("Enter code..");
                editText.requestFocus();
                return;
            }
            verifyCode(editText.getText().toString());
        }

    }

    private void verifyCode(String code) {
        progressBar.setVisibility(View.VISIBLE);
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId, code);
        signInWithCredential(credential);
    }

    private void signInWithCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            progressBar.setVisibility(View.GONE);
                            if (getActivity() != null)
                                HelperMethods.loadFragmentNew(new UserDetailsFragment(), getActivity());

                            //Intent intent = new Intent(getContext(), MainActivity.class);
                            //intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            //startActivity(intent);
                        } else {
                            progressBar.setVisibility(View.GONE);
                            if (task.getException() != null)
                                Toast.makeText(getContext(), task.getException().getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }

    private void sendVerificationCode(String number) {
        //Show Progress Bar
        progressBar.setVisibility(View.VISIBLE);
        //[START start_phone_auth]
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                number,                       // Phone number to verify
                60,                        // Timeout duration
                TimeUnit.SECONDS,             // Unit of timeout
                TaskExecutors.MAIN_THREAD,    // Activity (for callback binding)
                mCallBack                     // OnVerificationStateChangedCallbacks
        );
        //[END start_phone_auth]
    }



    private PhoneAuthProvider.OnVerificationStateChangedCallbacks
            mCallBack = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

        @Override
        public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
            super.onCodeSent(s, forceResendingToken);
            //The SMS verification code has been sent to the provided phone number, we
            //now need to ask the user to enter the code and then construct a credential
            //by combining the code with a verification ID.
            progressBar.setVisibility(View.GONE);
            verificationId = s;
        }

        @Override
        public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {
            //This callback will be invoked in two situations
            // 1 - Instant verification:
            //     In some cases the phone number can be instantly verified.
            //     without needing to send or enter a verification code.
            // 2 - Auto-retrieval:
            //     On some devices Google Play Services can automatically detect
            //     the incoming verification SMS and perform verification without user action.

            String code = phoneAuthCredential.getSmsCode();
            if (code != null) {
                progressBar.setVisibility(View.GONE);
                editText.setText(code);
                verifyCode(code);
            }
        }

        @Override
        public void onVerificationFailed(FirebaseException e) {
            //This callback is invoked if an invalid request for verification is made,
            //for instance if the phone number format is not valid.
            progressBar.setVisibility(View.GONE);
            Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_LONG).show();
        }
    };
}