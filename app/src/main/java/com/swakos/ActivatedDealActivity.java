package com.swakos;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.swakos.model.ActivateDeal;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ActivatedDealActivity extends AppCompatActivity {
    private Bitmap bitmap;
    @BindView(R.id.image_qr)
    ImageView qrImage;
    private ActivateDeal activateDeal;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_activated_deals);

        ButterKnife.bind(this);

        byte[] bitmapByteArray = getIntent().getByteArrayExtra("bitmap"); //Get the bitmapArray

        if (bitmapByteArray != null)
            bitmap = BitmapFactory.decodeByteArray(bitmapByteArray, 0, bitmapByteArray.length); ///decode the byteArray into imageView

        //dealTitle.setText(title); //Set Deal Title
        if (bitmap != null) qrImage.setImageBitmap(bitmap); //Set //ImageBitMap Resource

        activateDeal = getIntent().getParcelableExtra("activated_deal");
    }

    @OnClick (R.id.ic_up)
    public void closeActivity(){
        finish();
    }
    @OnClick (R.id.tv_cancel)

    public void cancel(){
        Intent intent = new Intent(ActivatedDealActivity.this, CancelOfferActivity.class);
        intent.putExtra("activated_deal", activateDeal);
        startActivity(intent);
    }
}
