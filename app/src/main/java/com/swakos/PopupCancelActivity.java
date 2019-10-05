package com.swakos;

import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.swakos.model.ActivateDeal;

import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class PopupCancelActivity extends AppCompatActivity {
    @BindView(R.id.tv_deal_title)
    TextView tvDealTitle;
    @BindView(R.id.tv_deal_regular_price)
    TextView tvDealRegularPrice;
    @BindView(R.id.tv_deal_off)
    TextView tvDealOffPrice;
    @BindView(R.id.tv_client_name_deal)
    TextView tvClientName;
    @BindView(R.id.tv_date)
    TextView tvDate;
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.ic_up)
    ImageView imgUp;
    @BindView(R.id.tv_client_name)
    TextView textView;
    @BindView(R.id.tv_instruction_one)
    TextView tvInstructionOne;
    @BindView(R.id.tv_instruction_two)
    TextView tvInstructionTwo;
    ActivateDeal activateDeal;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.popup_cancel);

        ButterKnife.bind(this);

        String clientName = getIntent().getStringExtra("client_name");

        //Log.e(clientName, clientName);

        textView.setText(clientName);
        tvTitle.setText(clientName);
        imgUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        activateDeal = getIntent().getParcelableExtra("activated_deal");

        tvDealRegularPrice.setPaintFlags(tvDealRegularPrice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);

        if (activateDeal != null) {
            tvDealTitle.setText(activateDeal.getDeal_title());
            tvDealRegularPrice.setText(String.valueOf(activateDeal.getDeal_actual_price()));
            tvDealOffPrice.setText(String.valueOf(activateDeal.getDeal_discounted_price()));

            Map<String, Object> clientDetails = activateDeal.getClient_details();
            Log.e("client_details", String.valueOf(clientDetails));

            tvClientName.setText(String.valueOf(clientDetails.get("name")));
            tvDate.setText(String.valueOf(activateDeal.getCreated_at()));
        }

        tvInstructionTwo.setText(getString(R.string.instruction_two, clientName));
        tvInstructionOne.setText(getString(R.string.instruction_one, clientName));
    }

    @OnClick (R.id.btn)
    public void cancel(){
        Intent intent = new Intent(PopupCancelActivity.this, CancelOfferActivity.class);
        intent.putExtra("activated_deal", activateDeal);
        startActivity(intent);
    }


}
