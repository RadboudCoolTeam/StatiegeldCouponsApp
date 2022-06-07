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

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
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

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);

        MaterialToolbar materialToolbar = findViewById(R.id.account_bar);
        materialToolbar.setNavigationOnClickListener(view -> {
            Intent intent = new Intent(Account.this, Home.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
        });

        ImageView imageView = findViewById(R.id.accountAvatar);
        if (Util.getWebUser().data == null) {
            imageView.setImageResource(R.drawable.ic_baseline_account_circle_24);
        } else {
            imageView.setImageBitmap(Util.resizeBitmap(BitmapFactory.decodeByteArray(
                    Util.getWebUser().data, 0,
                    Util.getWebUser().data.length), Util.getAccountAvatarImageSize(this)));
        }

        TextView textView = findViewById(R.id.accountInfo);
        textView.setText(Util.getWebUser().name);

        Button logout = findViewById(R.id.accountLogout);

        logout.setOnClickListener(view -> {
            Util.setIsLoggedIn(false);

            AsyncTask.execute(() -> UserDatabase.getInstance(getApplicationContext()).userDao().nukeTable());

            Util.getWebUser().reset();

            Intent intent = new Intent(Account.this, AccountLogin.class);
            startActivity(intent);
        });

        AsyncTask.execute(() -> {
            ArrayList<BarEntry> barEntries = new ArrayList<>();
            StatisticsDao dao = StatisticsDatabase.getInstance(getApplicationContext()).statisticsDao();
            List<StatisticsData> data = dao.getAll();
            ArrayList<String> label = new ArrayList<>();

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

            int i = 0;
            for (StatisticsData statisticsData : data) {
                barEntries.add(new BarEntry((float) i, statisticsData.value.floatValue() +
                        values.get(statisticsData.supermarketChain)));
                label.add(statisticsData.supermarketChain.getFriendlyName());
                i++;
            }

            for (Map.Entry<SupermarketChain, Float> entry : values.entrySet()) {
                if (data.stream().noneMatch(e -> e.supermarketChain == entry.getKey())) {
                    barEntries.add(new BarEntry((float) i, entry.getValue()));
                    label.add(entry.getKey().getFriendlyName());
                    i++;
                }
            }


            BarDataSet dataSet = new BarDataSet(barEntries, "Values");
            ArrayList<IBarDataSet> dataSets = new ArrayList<>();
            dataSets.add(dataSet);

            BarData barData = new BarData(dataSets);
            barData.setValueTextSize(10f);
            barData.setBarWidth(0.9f);

            runOnUiThread(() -> {
                BarChart barChart = findViewById(R.id.chart);
                barChart.setData(barData);

                XAxis xAxis = barChart.getXAxis();
                xAxis.setPosition(XAxis.XAxisPosition.BOTTOM_INSIDE);

                ValueFormatter valueFormatter = new ValueFormatter() {
                    @Override
                    public String getFormattedValue(float value) {
                        return label.get((int) value);
                    }
                };

                xAxis.setGranularity(1f);
                xAxis.setValueFormatter(valueFormatter);

                barChart.invalidate();
            });
        });
    }
}