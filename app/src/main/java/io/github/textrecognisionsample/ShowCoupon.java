package io.github.textrecognisionsample;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.oned.EAN13Writer;

public class ShowCoupon extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_coupon);

        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.create();

        Coupon coupon = gson.fromJson(getIntent().getExtras().getString("coupon"), Coupon.class);

        TextView showPrice = findViewById(R.id.showPrice);
        ImageView barcodeImage = findViewById(R.id.barcodeImage);
        ImageView showSupermarketChain = findViewById(R.id.showSupermarketChain);

        ImageButton showBarcodeBack = findViewById(R.id.showBarcodeBack);
        ImageButton showBarcodeEdit = findViewById(R.id.showBarcodeEdit);
        ImageButton showBarcodeDelete = findViewById(R.id.showBarcodeDelete);

        EAN13Writer barcodeWriter = new EAN13Writer();

        BitMatrix bitMatrix = barcodeWriter.encode(coupon.getData(), BarcodeFormat.EAN_13, 300, 150);

        barcodeImage.setImageBitmap(Util.matrixToBitmap(bitMatrix));
        showPrice.setText(coupon.getMoney());
        showSupermarketChain.setImageResource(coupon.getSupermarketChain().getDrawable());

        showBarcodeBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }
}