package io.github.textrecognisionsample;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.stream.Collectors;

public class Home extends AppCompatActivity {

    private static ArrayList<Coupon> coupons = new ArrayList<>();

    private int counter;

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        Button add = findViewById(R.id.add_button);
        Button back = findViewById(R.id.back_button);
        EditText addDate = findViewById(R.id.add_date);
        EditText addMoney = findViewById(R.id.add_money);
        Spinner spinner = findViewById(R.id.add_chain);
        spinner.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_spinner_item,
                Arrays.stream(SupermarketChain.values()).map(SupermarketChain::name).collect(Collectors.toList())));

        RecyclerView recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setNestedScrollingEnabled(false);

        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        GridRecyclerViewAdapter adapter = new GridRecyclerViewAdapter(this, coupons);

        adapter.setClickListener(new GridRecyclerViewAdapter.ItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {

            }
        });

        recyclerView.setAdapter(adapter);

        add.setOnClickListener(view -> {
            Coupon coupon = new Coupon(addDate.getText().toString(), addMoney.getText().toString(),
                    SupermarketChain.valueOf(spinner.getSelectedItem().toString()));
            coupons.add(coupon);
            adapter.notifyItemInserted(coupons.size() - 1);
        });

        back.setOnClickListener(view -> finish());
    }
}