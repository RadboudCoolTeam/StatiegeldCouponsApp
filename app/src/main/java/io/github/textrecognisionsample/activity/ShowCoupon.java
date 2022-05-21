package io.github.textrecognisionsample.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
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

import io.github.textrecognisionsample.R;
import io.github.textrecognisionsample.model.Coupon;
import io.github.textrecognisionsample.util.Util;

public class ShowCoupon extends AppCompatActivity {

    public static int COUPON_DELETED = 1;
    public static int EDIT_COUPON = 2;
    private boolean edited = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_coupon);

        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.create();

        Coupon coupon = gson.fromJson(getIntent().getExtras().getString("coupon"), Coupon.class);

        ImageButton showBarcodeBack = findViewById(R.id.showBarcodeBack);
        ImageButton showBarcodeEdit = findViewById(R.id.showBarcodeEdit);
        ImageButton showBarcodeDelete = findViewById(R.id.showBarcodeDelete);

        updateCouponUi(coupon);

        showBarcodeBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.putExtra("coupon", gson.toJson(coupon));
                intent.putExtra("result", edited ? EditCoupon.COUPON_EDITED : EditCoupon.COUPON_UNCHANGED);
                setResult(RESULT_OK, intent);
                finish();
            }
        });

        showBarcodeEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ShowCoupon.this, EditCoupon.class);

                intent.putExtra("coupon", gson.toJson(coupon));

                startActivityForResult(intent, EDIT_COUPON);
            }
        });

        showBarcodeDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.putExtra("coupon", gson.toJson(coupon));
                intent.putExtra("result", COUPON_DELETED);
                setResult(RESULT_OK, intent);
                finish();
            }
        });
    }

    private void updateCouponUi(Coupon coupon) {
        TextView showPrice = findViewById(R.id.showPrice);
        ImageView barcodeImage = findViewById(R.id.barcodeImage);
        ImageView showSupermarketChain = findViewById(R.id.showSupermarketChain);

        EAN13Writer barcodeWriter = new EAN13Writer();

        BitMatrix bitMatrix = barcodeWriter.encode(coupon.getData(), BarcodeFormat.EAN_13, 300, 150);

        barcodeImage.setImageBitmap(Util.matrixToBitmap(bitMatrix));
        showPrice.setText(coupon.getMoney() + " €");
        showSupermarketChain.setImageResource(coupon.getSupermarketChain().getDrawable());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == EDIT_COUPON) {

                int result = data.getIntExtra("result", 0);

                if (result == EditCoupon.COUPON_EDITED) {
                    GsonBuilder builder = new GsonBuilder();
                    Gson gson = builder.create();
                    Coupon coupon = gson.fromJson(data.getStringExtra("coupon"), Coupon.class);
                    updateCouponUi(coupon);

                    edited = true;
                }
            }
        }
    }
}