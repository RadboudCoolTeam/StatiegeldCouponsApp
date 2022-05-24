package io.github.textrecognisionsample.activity;

import android.annotation.SuppressLint;
import android.content.AsyncQueryHandler;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.ColorStateList;
import android.graphics.Typeface;
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
import androidx.core.content.res.ResourcesCompat;
import androidx.loader.content.AsyncTaskLoader;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipDrawable;
import com.google.android.material.chip.ChipGroup;
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
import io.github.textrecognisionsample.util.Result;

public class Home extends AppCompatActivity {

    private ArrayList<Coupon> coupons = new ArrayList<>();
    private CouponDatabase db;
    private GridRecyclerViewAdapter adapter;

    private static final String DATE_FORMAT = "yyyy-MM-dd";

    @RequiresApi(api = Build.VERSION_CODES.O)
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

        ChipGroup group = findViewById(R.id.chips_list);
        ArrayList<Chip> chips = new ArrayList<>();

        Chip chip = new Chip(group.getContext());
        chip.setText("All");
        ChipDrawable chipDrawable = ChipDrawable.createFromAttributes(this,
                null,
                0,
                com.google.android.material.R.style.Widget_MaterialComponents_Chip_Choice);
        chipDrawable.setCheckable(true);
        chip.setChipDrawable(chipDrawable);
        chip.setChecked(true);
        group.addView(chip);
        chips.add(chip);

        for (SupermarketChain chain : SupermarketChain.values()) {
            chip = new Chip(group.getContext());
            chip.setText(chain.getFriendlyName());
            chipDrawable = ChipDrawable.createFromAttributes(this,
                    null,
                    0,
                    com.google.android.material.R.style.Widget_MaterialComponents_Chip_Choice);
            chipDrawable.setCheckable(true);
            chip.setChipDrawable(chipDrawable);
            chip.setRippleColorResource(chain.getColor());
            group.addView(chip);
            chips.add(chip);
        }

        chips.get(0).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                coupons.clear();
                AsyncTask.execute(() -> {
                    db = CouponDatabase.getInstance(Home.this);
                    CouponDao dao = db.couponDao();
                    List<Coupon> dbCoupons = dao.getAll();
                    coupons.addAll(dbCoupons);
                    runOnUiThread(() -> adapter.notifyDataSetChanged());
                });

                for (int i = 1; i < chips.size(); i++) {
                    chips.get(i).setChecked(false);
                }

                chips.get(0).setChecked(true);
            }
        });

        for (int i = 1; i < chips.size(); i++) {
            int finalI = i;
            chips.get(i).setOnClickListener(new View.OnClickListener() {
                @SuppressLint("NotifyDataSetChanged")
                @Override
                public void onClick(View view) {

                    int count = 0;
                    for (int i = 1; i < chips.size(); i++) {
                        if (chips.get(i).isChecked()) {
                            count++;
                        }
                    }

                    coupons.clear();
                    if (count == 0) {
                        AsyncTask.execute(() -> {
                            db = CouponDatabase.getInstance(Home.this);
                            CouponDao dao = db.couponDao();
                            List<Coupon> dbCoupons = dao.getAll();
                            coupons.addAll(dbCoupons);
                            runOnUiThread(() -> adapter.notifyDataSetChanged());
                        });

                        for (int i = 1; i < chips.size(); i++) {
                            chips.get(i).setChecked(false);
                        }

                        chips.get(0).setChecked(true);
                    } else {
                        AsyncTask.execute(() -> {
                            db = CouponDatabase.getInstance(Home.this);
                            CouponDao dao = db.couponDao();

                            for (int i = 1; i < chips.size(); i++) {
                                if (chips.get(i).isChecked()) {
                                    List<Coupon> dbCoupons = dao.findBySupermarketChain(
                                            SupermarketChain.getByFriendlyName(
                                                    chips.get(i).getText().toString()
                                            ).name()
                                    );
                                    coupons.addAll(dbCoupons);
                                }
                            }

                            runOnUiThread(() -> adapter.notifyDataSetChanged());
                        });
                        chips.get(0).setChecked(false);
                    }
                }
            });
        }


        FloatingActionButton fab = findViewById(R.id.edit_fab);

        fab.setImageResource(R.drawable.ic_baseline_add_24);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Home.this, CameraX.class);
                startActivityForResult(intent, Result.TAKE_PICTURE_CODE);
            }
        });

        RecyclerView recyclerView = findViewById(R.id.recycler_view);

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
                startActivityForResult(intent, Result.VIEW_COUPON);
            }
        });

        recyclerView.setAdapter(adapter);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.create();
        db = CouponDatabase.getInstance(this);

        if (resultCode == RESULT_OK) {
            if (requestCode == Result.VIEW_COUPON) {
                int result = data.getIntExtra("result", 0);

                if (result == Result.COUPON_DELETED) {
                    Coupon coupon = gson.fromJson(data.getStringExtra("coupon"), Coupon.class);

                    int i = 0;
                    while (i < coupons.size() && (coupons.get(i).uid != coupon.uid)) {
                        i++;
                    }

                    coupons.removeIf(e -> e.uid == coupon.uid);

                    AsyncTask.execute(() -> db.couponDao().delete(coupon));

                    adapter.notifyItemRemoved(i);
                    adapter.notifyItemRangeChanged(i, coupons.size());

                } else if (result == Result.COUPON_EDITED) {
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
            } else if (requestCode == Result.TAKE_PICTURE_CODE) {
                String barcode = data.getStringExtra("barcode");
                String shop = data.getStringExtra("shop");
                String price = data.getStringExtra("shop_price");

                SimpleDateFormat formatter = new SimpleDateFormat(DATE_FORMAT);
                Date date = new Date(System.currentTimeMillis());

                List<String> values = Arrays.stream(SupermarketChain.values()).map(SupermarketChain::name).collect(Collectors.toList());

                int i = 0;
                while (i < values.size() && !values.get(i).equals(shop)) {
                    i++;
                }

                Coupon coupon = new Coupon(
                        formatter.format(date),
                        price,
                        barcode,
                        SupermarketChain.valueOf(values.get(i))
                );

                Intent intent = new Intent(Home.this, EditCoupon.class);

                intent.putExtra("coupon", gson.toJson(coupon, Coupon.class));
                intent.putExtra("isEdit", false);

                startActivityForResult(intent, Result.COUPON_CREATED);
            } else if (requestCode == Result.COUPON_CREATED) {
                Coupon coupon = gson.fromJson(data.getStringExtra("coupon"), Coupon.class);
                AsyncTask.execute(() -> db.couponDao().insertAll(coupon));
                coupons.add(coupon);
                adapter.notifyItemInserted(coupons.size() - 1);
            }
        }
    }
}