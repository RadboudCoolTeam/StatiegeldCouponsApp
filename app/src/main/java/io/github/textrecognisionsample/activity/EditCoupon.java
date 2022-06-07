package io.github.textrecognisionsample.activity;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import io.github.textrecognisionsample.R;
import io.github.textrecognisionsample.model.coupon.Coupon;
import io.github.textrecognisionsample.model.SupermarketChain;
import io.github.textrecognisionsample.util.Result;

public class EditCoupon extends AppCompatActivity {

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_coupon);

        MaterialToolbar materialToolbar = findViewById(R.id.edit_bar);
        materialToolbar.setNavigationOnClickListener(view -> {
            Intent intent = new Intent(EditCoupon.this, Home.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
        });

        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.create();

        Coupon coupon = gson.fromJson(getIntent().getExtras().getString("coupon"), Coupon.class);
        boolean isEdit = getIntent().getExtras().getBoolean("isEdit");

        AutoCompleteTextView editSelectChain = findViewById(R.id.editSelectChainText);
        List<String> values = Arrays.stream(SupermarketChain.values()).map(SupermarketChain::name)
                .collect(Collectors.toList());
        editSelectChain.setAdapter(new ArrayAdapter<>(this, R.layout.list_item, values));

        editSelectChain.setText(coupon.getSupermarketChain().name(), false);
        //editSelectChain.setText(coupon.getSupermarketChain().name());

        EditText editMoney = findViewById(R.id.editMoney);
        editMoney.setText(coupon.getMoney());

        EditText editBarcode = findViewById(R.id.editBarcode);
        editBarcode.setText(coupon.getBarcode());

        Button button = findViewById(R.id.editSave);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean edited = false;

                if (!editMoney.getText().toString().equals(coupon.getMoney())) {
                    edited = true;
                    coupon.setMoney(editMoney.getText().toString());
                }

                if (!editBarcode.getText().toString().equals(coupon.getBarcode())) {
                    edited = true;
                    coupon.setBarcode(editBarcode.getText().toString());
                }

                if (!editSelectChain.getText().toString().equals(coupon.getSupermarketChain().name())) {
                    edited = true;
                    coupon.setSupermarketChain(SupermarketChain.valueOf(editSelectChain.getText().toString()));
                }

                Intent intent = new Intent();

                if (isEdit) {
                    if (edited) {
                        intent.putExtra("coupon", gson.toJson(coupon));
                        intent.putExtra("result", Result.COUPON_EDITED);
                        setResult(RESULT_OK, intent);
                        finish();
                    } else {
                        intent.putExtra("result", Result.COUPON_UNCHANGED);
                        finish();
                    }
                } else {
                    intent.putExtra("coupon", gson.toJson(coupon));
                    intent.putExtra("result", Result.COUPON_CREATED);
                    setResult(RESULT_OK, intent);
                    finish();
                }
            }
        });

    }
}