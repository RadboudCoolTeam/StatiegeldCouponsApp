package io.github.textrecognisionsample.activity;

import android.content.AsyncQueryHandler;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.loader.content.AsyncTaskLoader;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.oned.EAN13Writer;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import io.github.textrecognisionsample.R;
import io.github.textrecognisionsample.model.Coupon;
import io.github.textrecognisionsample.model.CouponDao;
import io.github.textrecognisionsample.model.CouponDatabase;
import io.github.textrecognisionsample.model.SupermarketChain;

public class Home extends AppCompatActivity {

    private ArrayList<Coupon> coupons = new ArrayList<>();
    private CouponDatabase db;
    private GridRecyclerViewAdapter adapter;

    public static int VIEW_COUPON = 1;

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        AsyncTask.execute(() -> {
            db = CouponDatabase.getInstance(this);
            CouponDao dao = db.couponDao();
            List<Coupon> dbCoupons = dao.getAll();
            coupons.addAll(dbCoupons);
        });

        Button add = findViewById(R.id.add_button);
        Button back = findViewById(R.id.back_button);
        EditText addDate = findViewById(R.id.add_date);
        EditText addMoney = findViewById(R.id.add_money);
        EditText addBarcode = findViewById(R.id.add_barcode);
        FloatingActionButton fab = findViewById(R.id.edit_fab);

        fab.setImageResource(R.drawable.ic_baseline_add_24);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Home.this, CameraX.class);
                startActivityForResult(intent, CameraX.TAKE_PICTURE_CODE);
            }
        });

        Spinner spinner = findViewById(R.id.add_chain);
        spinner.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_spinner_item,
                Arrays.stream(SupermarketChain.values()).map(SupermarketChain::name).collect(Collectors.toList())));

        RecyclerView recyclerView = findViewById(R.id.recycler_view);
        //recyclerView.setNestedScrollingEnabled(false);

        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        adapter = new GridRecyclerViewAdapter(this, coupons);

        adapter.setClickListener(new GridRecyclerViewAdapter.ItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Toast.makeText(Home.this, "Item: " + coupons.get(position).getSupermarketChain().name(), Toast.LENGTH_SHORT).show();

                GsonBuilder builder = new GsonBuilder();
                Gson gson = builder.create();

                Intent intent = new Intent(Home.this, ShowCoupon.class);
                intent.putExtra("coupon", gson.toJson(coupons.get(position)));
                startActivityForResult(intent, VIEW_COUPON);
            }
        });

        recyclerView.setAdapter(adapter);

        add.setOnClickListener(view -> {
            try {
                EAN13Writer barcodeWriter = new EAN13Writer();

                BitMatrix bitMatrix = barcodeWriter.encode(addBarcode.getText().toString(),
                        BarcodeFormat.EAN_13, 300, 150);

                Coupon coupon = new Coupon(addDate.getText().toString(), addMoney.getText().toString(),
                        addBarcode.getText().toString(),
                        SupermarketChain.valueOf(spinner.getSelectedItem().toString()));
                coupons.add(coupon);

                AsyncTask.execute(() -> db.couponDao().insertAll(coupon));

                adapter.notifyItemInserted(coupons.size() - 1);
            } catch (Exception e) {
                AlertDialog.Builder builder = new AlertDialog.Builder(Home.this);
                builder.setMessage("Please, verify coupon and try again!")
                        .setTitle("Wrong barcode length!");
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });

        back.setOnClickListener(view -> finish());
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.create();
        db = CouponDatabase.getInstance(this);

        if (resultCode == RESULT_OK) {
            if (requestCode == VIEW_COUPON) {
                int result = data.getIntExtra("result", 0);

                if (result == ShowCoupon.COUPON_DELETED) {
                    Coupon coupon = gson.fromJson(data.getStringExtra("coupon"), Coupon.class);

                    coupons.remove(coupon);

                    AsyncTask.execute(() -> db.couponDao().delete(coupon));

                    adapter.notifyDataSetChanged();
                } else if (result == EditCoupon.COUPON_EDITED) {
                    Coupon coupon = gson.fromJson(data.getStringExtra("coupon"), Coupon.class);

                    int i = 0;
                    while (i < coupons.size() && (coupons.get(i).uid != coupon.uid)) {
                        i++;
                    }

                    if (i != coupons.size()) {
                        coupons.set(i, coupon);
                        AsyncTask.execute(() -> db.couponDao().update(coupon));
                        adapter.notifyItemChanged(i);
                    }
                }
            } else if (requestCode == CameraX.TAKE_PICTURE_CODE) {
                String text = data.getStringExtra("barcode");
                String shop = data.getStringExtra("shop");
                String price = data.getStringExtra("shop_price");
                // String DEBUG = data.getStringExtra("text");
                EditText addDate = findViewById(R.id.add_date);
                EditText addMoney = findViewById(R.id.add_money);
                EditText addBarcode = findViewById(R.id.add_barcode);
                Spinner spinner = findViewById(R.id.add_chain);


                SimpleDateFormat formatter= new SimpleDateFormat("yyyy-MM-dd");
                Date date = new Date(System.currentTimeMillis());

                addDate.setText(formatter.format(date));
                addMoney.setText(price);
                addBarcode.setText(text);

                List<String> values = Arrays.stream(SupermarketChain.values()).map(SupermarketChain::name).collect(Collectors.toList());
                spinner.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, values));

                int i = 0;
                while (i < values.size() && !values.get(i).equals(shop)) {
                    i++;
                }

                spinner.setSelection(i);
            }
        }
    }
}