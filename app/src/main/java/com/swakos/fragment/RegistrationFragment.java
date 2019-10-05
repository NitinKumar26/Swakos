package com.swakos.fragment;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.swakos.MainActivity;
import com.swakos.R;
import com.swakos.helper.HelperMethods;
import com.swakos.helper.PrefManager;
import com.swakos.model.User;

import org.apache.commons.lang3.RandomStringUtils;

import java.util.Date;

public class RegistrationFragment extends Fragment {
    private FirebaseAuth mAuth;
    private GoogleSignInClient mGoogleSignInClient;
    private static final int RC_SIGN_IN = 9001;
    private ProgressDialog pDialog;
    private String userDocId;
    private FirebaseFirestore db;
    private String username, usermobile;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_phone, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        //Find the required views
        EditText edTvPhoneNumber = view.findViewById(R.id.edtv_phone_number);
        Button button = view.findViewById(R.id.signup_button);
        LinearLayout btnGoogle = view.findViewById(R.id.btn_google);
        //TextView tvOtherEmail = view.findViewById(R.id.tv_other_email);
        //tvOtherEmail.setPaintFlags(tvOtherEmail.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);

        pDialog = new ProgressDialog(getContext());
        pDialog.setMessage("Please Wait.. ");

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String countryCode = "91";
                String phoneNumber = edTvPhoneNumber.getText().toString().trim();
                if (phoneNumber.isEmpty() || phoneNumber.length() < 10){
                    edTvPhoneNumber.setError("Valid Phone Number is required");
                    edTvPhoneNumber.requestFocus();
                    return;
                }
                Bundle bundle = new Bundle();
                bundle.putString("phoneNumber", "+" + countryCode + phoneNumber);
                OTPFragment otpFragment = new OTPFragment();
                otpFragment.setArguments(bundle);
                if (getActivity() != null) HelperMethods.loadFragmentNew(otpFragment, getActivity());
            }
        });
        btnGoogle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // [START config_signin]
                // Configure Google Sign In
                GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                        .requestIdToken(getString(R.string.default_web_client_id))
                        .requestEmail()
                        .build();
                // [END config_signin]
                if (getContext()!= null) mGoogleSignInClient = GoogleSignIn.getClient(getContext(), gso);
                pDialog.show();
                signIn();
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        //Check if the user is signed in (non-null) and update the UI accordingly
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            //Toast.makeText(LogInActivity.this, "User is already logged in", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(getContext(), MainActivity.class)); //Start Main Activity
            if (getActivity() != null) getActivity().finish();  //Finish this activity
        }
    }



    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        //super.onActivityResult(requestCode, resultCode, data);
        //Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN){
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                //Google sign in was successful, authenticate with Firebase
                Toast.makeText(getContext(), "Google Sign in successful ", Toast.LENGTH_SHORT).show();
                GoogleSignInAccount account = task.getResult(ApiException.class);
                if (account != null) {
                    username= account.getDisplayName();
                    //userDocId = account.getId();
                    firebaseAuthWithGoogle(account);
                }

            }catch (ApiException e){
                //Google Sign in failed update the UI accordingly
                if (pDialog.isShowing()) pDialog.hide();
                Toast.makeText(getContext(), "Sign In Failed", Toast.LENGTH_SHORT).show();
                Log.w("LoginActivity", "Google Sign in Failed", e);
            }
        }
    }

    private void signIn(){
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    // [START auth_with_google]
    private void firebaseAuthWithGoogle(@NonNull GoogleSignInAccount acct) {
        //Log.d(TAG, "firebaseAuthWithGoogle:" + acct.getId());
        // [START_EXCLUDE silent]
        //showProgressDialog();
        // [END_EXCLUDE]


        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        if (getActivity()!= null) {
            mAuth.signInWithCredential(credential)
                    .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                // Sign in success, update UI with the signed-in user's information
                                //Log.d(TAG, "signInWithCredential:success");
                                if (pDialog.isShowing()) pDialog.hide();

                                FirebaseUser user = mAuth.getCurrentUser();

                                if (user != null) userDocId = user.getUid();//
                                db.collection("users").document(userDocId).get().
                                        addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                    @Override
                                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                                        if (documentSnapshot.exists()){
                                            PrefManager prefManager;
                                            if (getContext() != null) {
                                                prefManager = new PrefManager(getContext());
                                                if (documentSnapshot.getString("phone_number") != null)
                                                    prefManager.setUserData(documentSnapshot.getString("name"), documentSnapshot.getString("phone_number")
                                                    , documentSnapshot.getString("user_id"));
                                                else
                                                    prefManager.setUserData(documentSnapshot.getString("name"), documentSnapshot.getString("email")
                                                    , documentSnapshot.getString("user_id"));
                                            }

                                            Intent intent = new Intent(getContext(), MainActivity.class);
                                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                            startActivity(intent);
                                            if (getActivity() != null) getActivity().finish();
                                        }else{
                                            if (user!= null) {
                                                User newUser = new User();
                                                newUser.setName(username);
                                                newUser.setUser_doc_id(userDocId);
                                                newUser.setUser_id(RandomStringUtils.randomNumeric(8));
                                                newUser.setProvider_id(user.getProviderId());
                                                newUser.setEmail(user.getEmail());
                                                newUser.setPhone_number(user.getPhoneNumber());
                                                newUser.setCreated_at(new Timestamp(new Date()));
                                                db.collection("users").document(userDocId).set(newUser).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        if (task.isSuccessful()){
                                                            PrefManager prefManager = new PrefManager(getContext());
                                                            if (newUser.getPhone_number() != null)
                                                                prefManager.setUserData(newUser.getName(), newUser.getPhone_number(), newUser.getUser_id());
                                                            else
                                                                prefManager.setUserData(newUser.getName(), newUser.getEmail(), newUser.getUser_id());
                                                            Intent intent = new Intent(getContext(), MainActivity.class);
                                                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                                            startActivity(intent);
                                                        }else{
                                                            if (task.getException()!= null)
                                                            Toast.makeText(getContext(), task.getException().toString(), Toast.LENGTH_SHORT).show();
                                                        }
                                                    }
                                                });
                                            }
                                        }
                                    }
                                });
                                //updateUI(user);
                            } else {
                                // If sign in fails, display a message to the user.
                                //Log.w(TAG, "signInWithCredential:failure", task.getException());

                                Toast.makeText(getContext(), "Authentication Failed", Toast.LENGTH_SHORT).show();
                                //updateUI(null);
                            }

                            // [START_EXCLUDE]
                            //hideProgressDialog();
                            // [END_EXCLUDE]
                        }
                    });
        }
    }
    // [END auth_with_google]
}
