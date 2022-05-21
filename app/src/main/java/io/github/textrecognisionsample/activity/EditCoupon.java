package io.github.textrecognisionsample.activity;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import io.github.textrecognisionsample.R;
import io.github.textrecognisionsample.model.Coupon;
import io.github.textrecognisionsample.model.SupermarketChain;

public class EditCoupon extends AppCompatActivity {

    public static int COUPON_EDITED = 1;
    public static int COUPON_UNCHANGED = 2;

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_coupon);

        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.create();

        Coupon coupon = gson.fromJson(getIntent().getExtras().getString("coupon"), Coupon.class);

        Spinner editSelectChain = findViewById(R.id.editSelectChain);
        List<String> values = Arrays.stream(SupermarketChain.values()).map(SupermarketChain::name).collect(Collectors.toList());
        editSelectChain.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, values));

        int i = 0;
        while (!values.get(i).equals(coupon.getSupermarketChain().name()) && i < values.size()) {
            i++;
        }

        editSelectChain.setSelection(i);

        EditText editMoney = findViewById(R.id.editMoney);
        editMoney.setText(coupon.getMoney());

        EditText editBarcode = findViewById(R.id.editBarcode);
        editBarcode.setText(coupon.getData());

        Button button = findViewById(R.id.editSave);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean edited = false;

                if (!editMoney.getText().toString().equals(coupon.getMoney())) {
                    edited = true;
                    coupon.setMoney(editMoney.getText().toString());
                }

                if (!editBarcode.getText().toString().equals(coupon.getData())) {
                    edited = true;
                    coupon.setData(editBarcode.getText().toString());
                }

                if (!editSelectChain.getSelectedItem().toString().equals(coupon.getSupermarketChain().name())) {
                    edited = true;
                    coupon.setSupermarketChain(SupermarketChain.valueOf(editSelectChain.getSelectedItem().toString()));
                }

                Intent intent = new Intent();
                if (edited) {
                    intent.putExtra("coupon", gson.toJson(coupon));
                    intent.putExtra("result", COUPON_EDITED);
                    setResult(RESULT_OK, intent);
                    finish();
                } else {
                    intent.putExtra("result", COUPON_UNCHANGED);
                    finish();
                }
            }
        });

        ImageButton editBack = findViewById(R.id.editBack);
        editBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finishActivity(COUPON_UNCHANGED);
            }
        });

    }
}