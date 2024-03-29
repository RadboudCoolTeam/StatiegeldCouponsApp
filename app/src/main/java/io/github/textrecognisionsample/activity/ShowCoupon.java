package io.github.textrecognisionsample.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.oned.Code128Writer;
import com.google.zxing.oned.EAN13Writer;

import io.github.textrecognisionsample.R;
import io.github.textrecognisionsample.model.coupon.Coupon;
import io.github.textrecognisionsample.util.Result;
import io.github.textrecognisionsample.util.Util;

public class ShowCoupon extends AppCompatActivity {

    private boolean edited = false;
    private Coupon coupon;

    private int BARCODE_WIDTH;
    private int BARCODE_HEIGHT;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_coupon);

        BARCODE_WIDTH = Util.getResized(this, Util.MAX_SCALE_PERCENT_BARCODE);
        BARCODE_HEIGHT = Util.getResized(this, 100 - Util.MAX_SCALE_PERCENT_BARCODE);

        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.create();

        coupon = gson.fromJson(getIntent().getExtras().getString("coupon"), Coupon.class);

        FloatingActionButton showBarcodeEdit = findViewById(R.id.showBarcodeEdit);
        FloatingActionButton showBarcodeDelete = findViewById(R.id.showBarcodeDelete);

        showBarcodeDelete.setImageResource(R.drawable.ic_baseline_delete_24);

        updateCouponUi(coupon);

        MaterialToolbar materialToolbar = findViewById(R.id.show_bar);
        materialToolbar.setNavigationOnClickListener(view -> {
            Intent intent = new Intent();
            intent.putExtra("coupon", gson.toJson(coupon));
            intent.putExtra("result", edited ? Result.COUPON_EDITED : Result.COUPON_UNCHANGED);
            setResult(RESULT_OK, intent);
            finish();
        });

        showBarcodeEdit.setOnClickListener(view -> {
            Intent intent = new Intent(ShowCoupon.this, EditCoupon.class);

            intent.putExtra("coupon", gson.toJson(coupon));
            intent.putExtra("isEdit", true);

            startActivityForResult(intent, Result.EDIT_COUPON);
        });

        showBarcodeDelete.setOnClickListener(view -> {
            Intent intent = new Intent();
            intent.putExtra("coupon", gson.toJson(coupon));
            intent.putExtra("result", Result.COUPON_DELETED);
            setResult(RESULT_OK, intent);
            finish();
        });
    }

    private void updateCouponUi(Coupon coupon) {
        TextView showPrice = findViewById(R.id.showPrice);
        ImageView barcodeImage = findViewById(R.id.barcodeImage);
        ImageView showSupermarketChain = findViewById(R.id.showSupermarketChain);

        try {
            EAN13Writer barcodeWriter = new EAN13Writer();

            BitMatrix bitMatrix = barcodeWriter.encode(coupon.getBarcode(), BarcodeFormat.EAN_13, BARCODE_WIDTH, BARCODE_HEIGHT);

            barcodeImage.setImageBitmap(Util.matrixToBitmap(bitMatrix));
        } catch (Exception e) {
            try {
                Code128Writer writer = new Code128Writer();
                BitMatrix bitMatrix = writer.encode(coupon.getBarcode(), BarcodeFormat.CODE_128, BARCODE_WIDTH, BARCODE_HEIGHT);
                barcodeImage.setImageBitmap(Util.matrixToBitmap(bitMatrix));
            } catch (Exception e1) {
                barcodeImage.setImageResource(R.drawable.ic_baseline_error_outline_24);
            }
        }

        showPrice.setText(coupon.getMoney() + " €");
        showSupermarketChain.setImageResource(coupon.getSupermarketChain().getDrawable());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == Result.EDIT_COUPON) {

                int result = data.getIntExtra("result", 0);

                if (result == Result.COUPON_EDITED) {
                    GsonBuilder builder = new GsonBuilder();
                    Gson gson = builder.create();
                    coupon = gson.fromJson(data.getStringExtra("coupon"), Coupon.class);
                    updateCouponUi(coupon);

                    edited = true;
                }
            }
        }
    }
}