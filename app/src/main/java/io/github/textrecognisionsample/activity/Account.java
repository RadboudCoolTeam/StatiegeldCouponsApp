package io.github.textrecognisionsample.activity;

import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.android.material.appbar.MaterialToolbar;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.github.textrecognisionsample.R;
import io.github.textrecognisionsample.model.SupermarketChain;
import io.github.textrecognisionsample.model.coupon.Coupon;
import io.github.textrecognisionsample.model.coupon.CouponDao;
import io.github.textrecognisionsample.model.coupon.CouponDatabase;
import io.github.textrecognisionsample.model.statistics.StatisticsDao;
import io.github.textrecognisionsample.model.statistics.StatisticsData;
import io.github.textrecognisionsample.model.statistics.StatisticsDatabase;
import io.github.textrecognisionsample.model.user.UserDatabase;
import io.github.textrecognisionsample.util.Util;

public class Account extends AppCompatActivity {

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);

        setupToolbar();

        setupAvatar();

        setupAccountInfo();

        setupAccountInfo();

        setupLogoutButton();

        displayStatistic();
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void setupToolbar() {
        MaterialToolbar materialToolbar = findViewById(R.id.account_bar);
        materialToolbar.setNavigationOnClickListener(view -> {
            Intent intent = new Intent(Account.this, Home.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
        });
    }

    private void setupAvatar() {
        ImageView imageView = findViewById(R.id.accountAvatar);
        if (Util.getWebUser().data == null) {
            imageView.setImageResource(R.drawable.ic_baseline_account_circle_24);
        } else {
            imageView.setImageBitmap(Util.resizeBitmap(BitmapFactory.decodeByteArray(
                    Util.getWebUser().data, 0,
                    Util.getWebUser().data.length), Util.getAccountAvatarImageSize(this)));
        }
    }

    private void setupAccountInfo() {
        TextView textView = findViewById(R.id.accountInfo);
        textView.setText(Util.getWebUser().name);
    }

    private void setupLogoutButton() {
        Button logout = findViewById(R.id.accountLogout);

        logout.setOnClickListener(view -> {
            Util.setIsLoggedIn(false);

            AsyncTask.execute(() -> UserDatabase.getInstance(getApplicationContext()).userDao().nukeTable());

            Util.getWebUser().reset();

            Intent intent = new Intent(Account.this, AccountLogin.class);
            startActivity(intent);
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void displayStatistic() {
        AsyncTask.execute(() -> {
            ArrayList<PieEntry> pieEntries = new ArrayList<>();
            StatisticsDao dao = StatisticsDatabase.getInstance(getApplicationContext()).statisticsDao();
            List<StatisticsData> data = dao.getAll();
            ArrayList<SupermarketChain> label = new ArrayList<>();

            CouponDao couponDao = CouponDatabase.getInstance(getApplicationContext()).couponDao();
            List<Coupon> coupons = couponDao.getAll();
            Map<SupermarketChain, Float> values = new HashMap<>();
            for (SupermarketChain supermarketChain : SupermarketChain.values()) {
                values.put(supermarketChain, 0.0f);
            }

            for (Coupon coupon : coupons) {
                if (values.containsKey(coupon.getSupermarketChain())) {
                    try {
                        values.put(coupon.getSupermarketChain(),
                                (float) (values.get(coupon.getSupermarketChain()) +
                                        Double.parseDouble(coupon.getMoney())));
                    } catch (NumberFormatException e) {
                        // ignored
                    }
                } else {
                    values.put(coupon.getSupermarketChain(), (float) +
                            Double.parseDouble(coupon.getMoney()));
                }
            }

            int size = 0;
            for (StatisticsData statisticsData : data) {
                float value = statisticsData.value.floatValue() +
                        values.get(statisticsData.supermarketChain);
                if (value > 0.0f) {
                    pieEntries.add(new PieEntry(value,
                            statisticsData.supermarketChain.getFriendlyName()));
                    label.add(statisticsData.supermarketChain);
                    size++;
                }
            }

            for (Map.Entry<SupermarketChain, Float> entry : values.entrySet()) {
                if (data.stream().noneMatch(e -> e.supermarketChain == entry.getKey())) {
                    if (entry.getValue() > 0.0f) {
                        pieEntries.add(new PieEntry(entry.getValue(), entry.getKey().getFriendlyName()));
                        label.add(entry.getKey());
                        size++;
                    }
                }
            }

            PieDataSet pieDataSet = new PieDataSet(pieEntries, "Values");
            int[] colors = new int[size];

            for (int i = 0; i < size; i++) {
                colors[i] = ContextCompat.getColor(this, label.get(i).getColor());
            }

            pieDataSet.setColors(colors);
            PieData pieData = new PieData(pieDataSet);
            pieData.setValueTextSize(10f);

            runOnUiThread(() -> {
                PieChart pieChart = findViewById(R.id.chart);
                pieChart.setData(pieData);
                pieChart.invalidate();
                pieChart.setDrawEntryLabels(true);
                pieChart.setContentDescription("");
                pieChart.getDescription().setEnabled(false);

                Legend legend = pieChart.getLegend();
                legend.setForm(Legend.LegendForm.CIRCLE);
                legend.setWordWrapEnabled(true);
            });
        });
    }
}