package com.swakos;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class DealQRActivity extends AppCompatActivity {
    private Bitmap bitmap;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.deal_qr_activity);

        ImageView qrImage = findViewById(R.id.image_qr);
        TextView dealTitle = findViewById(R.id.tv_deal_title);
        Button btnDone = findViewById(R.id.btn_done);

        byte[] bitmapByteArray = getIntent().getByteArrayExtra("bitmap"); //Get the bitmapArray
        String title = getIntent().getStringExtra("deal_title");
        String userId = getIntent().getStringExtra("userId");

        if (bitmapByteArray != null)
            bitmap = BitmapFactory.decodeByteArray(bitmapByteArray, 0, bitmapByteArray.length); ///decode the byteArray into imageView

        //dealTitle.setText(title); //Set Deal Title
        if (bitmap != null) qrImage.setImageBitmap(bitmap); //Set //ImageBitMap Resource

        btnDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }
}
