package io.github.textrecognisionsample;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ScrollView;

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

        LinearLayout linearLayout = new LinearLayout(scrollView.getContext());
        linearLayout.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        scrollView.addView(linearLayout);

        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Coupon coupon = new Coupon("A", "B", SupermarketChain.AH);
                CouponCard couponCard = new CouponCard(Home.this, coupon);
                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                );
                couponCard.setLayoutParams(layoutParams);

                linearLayout.addView(couponCard);
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