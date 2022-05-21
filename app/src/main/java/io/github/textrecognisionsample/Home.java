package io.github.textrecognisionsample;

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
import androidx.room.Room;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.oned.EAN13Writer;
import com.google.zxing.oned.UPCEANReader;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class Home extends AppCompatActivity {

    private ArrayList<Coupon> coupons = new ArrayList<>();
    private CouponDatabase db;

    int counter;

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

        Spinner spinner = findViewById(R.id.add_chain);
        spinner.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_spinner_item,
                Arrays.stream(SupermarketChain.values()).map(SupermarketChain::name).collect(Collectors.toList())));

        RecyclerView recyclerView = findViewById(R.id.recycler_view);
        //recyclerView.setNestedScrollingEnabled(false);

        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        GridRecyclerViewAdapter adapter = new GridRecyclerViewAdapter(this, coupons);

        adapter.setClickListener(new GridRecyclerViewAdapter.ItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                System.out.println(1);
                Toast.makeText(Home.this, "Item: " + coupons.get(position).getSupermarketChain().name(), Toast.LENGTH_SHORT).show();

                GsonBuilder builder = new GsonBuilder();
                Gson gson = builder.create();

                Intent intent = new Intent(Home.this, ShowCoupon.class);
                intent.putExtra("coupon", gson.toJson(coupons.get(position)));
                startActivity(intent);
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
}