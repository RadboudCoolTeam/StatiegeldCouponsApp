package io.github.textrecognisionsample;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.Toast;

import java.util.ArrayList;

public class Home extends AppCompatActivity {

    private ArrayList<Coupon> coupons = new ArrayList<>();

    private int counter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        Button add = findViewById(R.id.add_button);
        Button back = findViewById(R.id.back_button);
        ScrollView scrollView = findViewById(R.id.list);

        Coupon coupon = new Coupon("A", "B", SupermarketChain.AH);
        coupons.add(coupon);

        CustomGrid customGrid = new CustomGrid(Home.this, coupons);

        GridView grid = findViewById(R.id.cards_grid);
        grid.setAdapter(customGrid);
        grid.setOnItemClickListener((adapterView, view, i, l) -> {
            Toast.makeText(Home.this, "You clicked at: " +
                    coupons.get(i).getSupermarketChain().name(), Toast.LENGTH_SHORT).show();
        });

        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Coupon coupon = new Coupon("A", "B", SupermarketChain.AH);
                coupons.add(coupon);
            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }
}