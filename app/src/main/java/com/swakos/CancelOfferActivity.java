package com.swakos;

import android.app.Activity;
import android.graphics.Paint;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.swakos.helper.PrefManager;
import com.swakos.model.ActivateDeal;
import com.swakos.model.Cancel;
import com.swakos.model.Client;
import com.swakos.model.Deal;

import org.w3c.dom.Text;

import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class CancelOfferActivity extends AppCompatActivity {
    @BindView(R.id.tv_didnt_cancel)
    TextView tvDidntCancel;
    @BindView(R.id.tv_are_you)
    TextView tvAreYouSure;
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private String reason;
    private String userDocId, userId;
    private PrefManager prefManager;
    private ActivateDeal activateDeal;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cancel_offer);

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        if (mAuth.getCurrentUser() != null) userDocId = mAuth.getCurrentUser().getUid();
        prefManager = new PrefManager(getApplicationContext());
        userId = prefManager.getUserId();

        ButterKnife.bind(this);

        tvDidntCancel.setPaintFlags(tvDidntCancel.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);

        activateDeal = getIntent().getParcelableExtra("activated_deal");
        if (activateDeal != null) tvAreYouSure.setText(getString(R.string.are_you_sure, activateDeal.getClient_details().get("name")));

    }

    public void getReason(View view){
        //Is the button now checked?
        boolean checked = ((RadioButton) view).isChecked();
        //Check which radio button was clicked
        switch (view.getId()){
            case R.id.radio_plan_changed:
                if (checked)
                    reason = "Plan changed";
                break;
            case R.id.radio_didnt_read:
                if (checked)
                reason = "Didn't read the terms and conditions.";
                break;
        }
    }



    @OnClick (R.id.tv_didnt_cancel)
    public void didntRead(){
        finish();
    }

    @OnClick (R.id.btn_cancel)
    public void cancelDealBtn(){
        cancelDeal();
    }

    private void cancelDeal() {
        if (reason != null) {
            db.collection("users")
                    .document(userDocId)
                    .collection("activated_deals")
                    .document(activateDeal.getActiveDealDocId())
                    .update("status", "canceled", "cancel_reason", reason,
                            "status_client_doc_id", "canceled_" + DealActivity.client_doc_id)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            finish();
                            Toast.makeText(getApplicationContext(), "Deal canceled successfully!", Toast.LENGTH_LONG).show();
                        }else{
                            Log.e("exception", String.valueOf(task.getException()));
                            //Toast.makeText(CancelOfferActivity.this, String.valueOf(task.getException()), Toast.LENGTH_LONG).show();
                        }
                    }
            });
        }else{
            Toast.makeText(CancelOfferActivity.this, "Please specify the reason.", Toast.LENGTH_SHORT).show();
        }
    }
}
