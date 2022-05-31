package io.github.textrecognisionsample.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipDrawable;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Properties;
import java.util.stream.Collectors;

import de.hdodenhof.circleimageview.CircleImageView;
import io.github.textrecognisionsample.R;
import io.github.textrecognisionsample.model.SupermarketChain;
import io.github.textrecognisionsample.model.coupon.Coupon;
import io.github.textrecognisionsample.model.coupon.CouponDao;
import io.github.textrecognisionsample.model.coupon.CouponDatabase;
import io.github.textrecognisionsample.model.statistics.StatisticsData;
import io.github.textrecognisionsample.model.statistics.StatisticsDatabase;
import io.github.textrecognisionsample.model.user.UserData;
import io.github.textrecognisionsample.model.user.UserDatabase;
import io.github.textrecognisionsample.model.web.WebCoupon;
import io.github.textrecognisionsample.model.web.WebUser;
import io.github.textrecognisionsample.model.web.WebUserJsonSerializer;
import io.github.textrecognisionsample.util.ByteArrayToBase64TypeAdapter;
import io.github.textrecognisionsample.util.GetWeather;
import io.github.textrecognisionsample.util.Result;
import io.github.textrecognisionsample.util.Util;

public class Home extends AppCompatActivity {

    private ArrayList<Coupon> coupons = new ArrayList<>();
    private CouponDatabase couponDatabase;
    private StatisticsDatabase statisticsDatabase;
    private UserDatabase userDatabase;

    private GridRecyclerViewAdapter adapter;

    private static final String DATE_FORMAT = "yyyy-MM-dd";

    private boolean floatingMenuClicked = false;

    private Animation rotateOpen;
    private Animation rotateClose;
    private Animation fromBottom;
    private Animation toBottom;

    private Handler update = new Handler();
    private Runnable runnable;

    private Gson gson;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        rotateOpen = AnimationUtils.loadAnimation(this, R.anim.rotate_open);
        rotateClose = AnimationUtils.loadAnimation(this, R.anim.rotate_close);
        fromBottom = AnimationUtils.loadAnimation(this, R.anim.from_bottom);
        toBottom = AnimationUtils.loadAnimation(this, R.anim.to_bottom);

        Properties properties = Util.getProperties(getApplicationContext());
        String API_Key = properties.getProperty("weather_api_key");
        String latitude = properties.getProperty("latitude");
        String longitude = properties.getProperty("longitude");

        GetWeather weather = new GetWeather(API_Key, latitude, longitude);

        CircleImageView avatarButton = findViewById(R.id.avatarButton);

        avatarButton.setImageBitmap(
                Util.resizeBitmap(
                        ((BitmapDrawable) getResources().getDrawable(R.drawable.avatar))
                                .getBitmap(),
                        Util.MAX_SCALE)
        );

        GsonBuilder gsonBuilder = new GsonBuilder()
                .registerTypeAdapter(WebUser.class, new WebUserJsonSerializer())
                .registerTypeAdapter(byte[].class, new ByteArrayToBase64TypeAdapter());
        gson = gsonBuilder.create();

        AsyncTask.execute(() -> {
            userDatabase = UserDatabase.getInstance(this);
            List<UserData> userData = userDatabase.userDao().getAll();

            if (userData.size() > 0) {
                WebUser webUser = gson.fromJson(userData.get(0).getData(), WebUser.class);

                if (webUser == null) {
                    webUser = gson.fromJson(Util.getDefaultUser(getApplicationContext()), WebUser.class);
                    UserDatabase.getInstance(this).userDao().nukeTable();
                    UserDatabase.getInstance(this).userDao().insert(new UserData(Util.getDefaultUser(getApplicationContext())));
                }

                Util.getWebUser().updateUser(webUser);

                runOnUiThread(() -> {
                    if (Util.getWebUser().data != null) {
                        avatarButton.setImageBitmap(
                                Util.resizeBitmap(
                                        BitmapFactory.decodeByteArray(
                                                Util.getWebUser().data,
                                                0,
                                                Util.getWebUser().data.length),
                                        Util.MAX_SCALE)
                        );
                        avatarButton.invalidate();
                    }
                });

            } else {
                UserDatabase.getInstance(this).userDao().insert(new UserData(""));
            }
        });

        AsyncTask.execute(() -> {
            couponDatabase = CouponDatabase.getInstance(this);
            CouponDao dao = couponDatabase.couponDao();

            try {
                WebUser webUser = Util.authUser(Util.getWebUser(), getApplicationContext());

                if (Util.isNetworkAvailable(getApplicationContext()) && webUser != null) {
                    List<WebCoupon> webCoupons = Util.getCoupons(webUser, getApplicationContext());
                    Util.setIsLoggedIn(true);
                    Util.setWebUser(webUser);

                    for (WebCoupon coupon : webCoupons) {
                        if (dao.getAll().stream().anyMatch(e -> e.getUid().equals(coupon.localId))) {
                            dao.update(Coupon.of(coupon));
                        } else {
                            dao.insertAll(Coupon.of(coupon));
                        }
                    }

                    runOnUiThread(() -> {
                        if (Util.getWebUser().data != null) {
                            avatarButton.setImageBitmap(BitmapFactory.decodeByteArray(
                                    Util.getWebUser().data,
                                    0,
                                    Util.getWebUser().data.length)
                            );
                            avatarButton.invalidate();
                        }
                    });
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

            List<Coupon> dbCoupons = dao.getAll();
            coupons.addAll(dbCoupons);
            runOnUiThread(() -> adapter.notifyDataSetChanged());
        });

        AsyncTask.execute(() -> {
            statisticsDatabase = StatisticsDatabase.getInstance(this);
        });

        AsyncTask.execute(() -> {
            try {
                String result = weather.getWeather();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });


        avatarButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PopupMenu popupMenu = new PopupMenu(Home.this, avatarButton);
                popupMenu.getMenuInflater().inflate(R.menu.popup_menu, popupMenu.getMenu());

                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {
                        switch (menuItem.getItemId()) {
                            case R.id.option_account: {
                                if (Util.isIsLoggedIn()) {
                                    Intent intent = new Intent(Home.this, Account.class);
                                    startActivity(intent);
                                } else {
                                    AsyncTask.execute(() -> {
                                        try {
                                            WebUser userFromApi = Util.authUser(Util.getWebUser(), getApplicationContext());
                                            if (userFromApi != null) {
                                                Util.getWebUser().updateUser(userFromApi);
                                                Util.setIsLoggedIn(true);

                                                Intent intent = new Intent(Home.this, Account.class);
                                                startActivity(intent);
                                            } else {
                                                Intent intent = new Intent(Home.this, AccountLogin.class);
                                                startActivityForResult(intent, Result.LOG_IN);
                                            }
                                        } catch (IOException e1) {
                                            e1.printStackTrace();
                                        }
                                    });
                                }
                            }

                            break;
                            case R.id.option_settings:
                                break;
                        }

                        return true;
                    }
                });

                popupMenu.show();

            }
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
                    couponDatabase = CouponDatabase.getInstance(Home.this);
                    CouponDao dao = couponDatabase.couponDao();
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
                            couponDatabase = CouponDatabase.getInstance(Home.this);
                            CouponDao dao = couponDatabase.couponDao();
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
                            couponDatabase = CouponDatabase.getInstance(Home.this);
                            CouponDao dao = couponDatabase.couponDao();

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
            public void onClick(View view) {
                onAddButtonClicked();
            }
        });

        FloatingActionButton addManually = findViewById(R.id.add_manually);

        addManually.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (floatingMenuClicked) {

                    onAddButtonClicked();
                    onAddButtonClicked();

                    SimpleDateFormat formatter = new SimpleDateFormat(DATE_FORMAT);
                    Date date = new Date(System.currentTimeMillis());

                    Coupon coupon = new Coupon(
                            formatter.format(date),
                            "",
                            "",
                            SupermarketChain.UNKNOWN
                    );

                    Intent intent = new Intent(Home.this, EditCoupon.class);

                    intent.putExtra("coupon", gson.toJson(coupon, Coupon.class));
                    intent.putExtra("isEdit", false);

                    startActivityForResult(intent, Result.COUPON_CREATED);
                }
            }
        });

        FloatingActionButton addTakePhoto = findViewById(R.id.add_take_photo);

        addTakePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (floatingMenuClicked) {

                    onAddButtonClicked();

                    Intent intent = new Intent(Home.this, CameraX.class);
                    startActivityForResult(intent, Result.TAKE_PICTURE_CODE);
                }
            }
        });

        RecyclerView recyclerView = findViewById(R.id.recycler_view);

        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        adapter = new GridRecyclerViewAdapter(this, coupons);

        adapter.setClickListener(new GridRecyclerViewAdapter.ItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {

                if (floatingMenuClicked) {
                    onAddButtonClicked();
                }

                Intent intent = new Intent(Home.this, ShowCoupon.class);
                intent.putExtra("coupon", gson.toJson(coupons.get(position)));
                startActivityForResult(intent, Result.VIEW_COUPON);
            }
        });

        recyclerView.setAdapter(adapter);

        SwipeRefreshLayout swipeRefreshContainer = findViewById(R.id.swipe);
        swipeRefreshContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                AsyncTask.execute(() -> {
                    if (Util.isIsLoggedIn()) {
                        try {
                            Util.syncDatabases(getApplicationContext());
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        List<Coupon> dbCoupons = couponDatabase.couponDao().getAll();
                        for (Coupon coupon : dbCoupons) {
                            if (coupons.stream().noneMatch(e -> coupon.getUid().equals(e.getUid()))) {
                                coupons.add(coupon);
                            }
                        }

                        runOnUiThread(() -> {
                            adapter.notifyDataSetChanged();
                            swipeRefreshContainer.setRefreshing(false);

                            if (Util.getWebUser().data != null) {
                                avatarButton.setImageBitmap(
                                        Util.resizeBitmap(
                                                BitmapFactory.decodeByteArray(
                                                        Util.getWebUser().data,
                                                        0,
                                                        Util.getWebUser().data.length),
                                                Util.MAX_SCALE)
                                );
                                avatarButton.invalidate();
                            } else {
                                avatarButton.setImageBitmap(
                                        Util.resizeBitmap(
                                                ((BitmapDrawable) getResources().getDrawable(R.drawable.avatar))
                                                        .getBitmap(),
                                                Util.MAX_SCALE)
                                );
                            }
                        });
                    } else {
                        runOnUiThread(() -> {
                            avatarButton.setImageBitmap(
                                    Util.resizeBitmap(
                                            ((BitmapDrawable) getResources().getDrawable(R.drawable.avatar))
                                                    .getBitmap(),
                                            Util.MAX_SCALE)
                            );
                            Toast.makeText(getApplicationContext(), "You are not logged in!", Toast.LENGTH_SHORT).show();
                            swipeRefreshContainer.setRefreshing(false);
                        });
                    }
                });
            }
        });
    }

    private void onAddButtonClicked() {
        setVisibility(floatingMenuClicked);
        setAnimation(floatingMenuClicked);
        floatingMenuClicked = !floatingMenuClicked;
    }

    private void setAnimation(boolean clicked) {
        FloatingActionButton addManually = findViewById(R.id.add_manually);
        FloatingActionButton addTakePhoto = findViewById(R.id.add_take_photo);
        FloatingActionButton fab = findViewById(R.id.edit_fab);
        if (!clicked) {
            addManually.startAnimation(fromBottom);
            addTakePhoto.startAnimation(fromBottom);
            fab.startAnimation(rotateOpen);
        } else {
            addManually.startAnimation(toBottom);
            addTakePhoto.startAnimation(toBottom);
            fab.startAnimation(rotateClose);
        }
    }

    private void setVisibility(boolean clicked) {
        FloatingActionButton addManually = findViewById(R.id.add_manually);
        FloatingActionButton addTakePhoto = findViewById(R.id.add_take_photo);
        if (!clicked) {
            addManually.setVisibility(View.VISIBLE);
            addTakePhoto.setVisibility(View.VISIBLE);
        } else {
            addManually.setVisibility(View.INVISIBLE);
            addTakePhoto.setVisibility(View.INVISIBLE);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        couponDatabase = CouponDatabase.getInstance(this);

        if (resultCode == RESULT_OK) {
            if (requestCode == Result.VIEW_COUPON) {
                int result = data.getIntExtra("result", 0);

                if (result == Result.COUPON_DELETED) {
                    Coupon coupon = gson.fromJson(data.getStringExtra("coupon"), Coupon.class);

                    int i = 0;
                    while (i < coupons.size() && (!Objects.equals(coupons.get(i).getUid(), coupon.getUid()))) {
                        i++;
                    }

                    try {
                        double convert = Double.parseDouble(coupon.getMoney());
                        AsyncTask.execute(() -> {
                            List<StatisticsData> statisticsData = statisticsDatabase.statisticsDao()
                                    .getDataByChain(coupon.getSupermarketChain());

                            if (statisticsData.size() > 0) {
                                statisticsData.get(0).value += convert;
                                statisticsDatabase.statisticsDao().update(statisticsData.get(0));
                            } else {
                                StatisticsData newData = new StatisticsData();
                                newData.value = convert;
                                newData.supermarketChain = coupon.getSupermarketChain();
                                statisticsDatabase.statisticsDao().insertAll(newData);
                            }
                        });

                    } catch (NumberFormatException ne) {
                        // ignored
                    }

                    coupons.removeIf(e -> Objects.equals(e.getUid(), coupon.getUid()));

                    AsyncTask.execute(() -> couponDatabase.couponDao().delete(coupon));

                    adapter.notifyItemRemoved(i);
                    adapter.notifyItemRangeChanged(i, coupons.size());

                    if (Util.isIsLoggedIn()) {
                        AsyncTask.execute(
                                () -> {
                                    WebCoupon webCoupon = WebCoupon.of(coupon);
                                    try {
                                        Util.deleteCoupon(webCoupon, getApplicationContext());
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                }
                        );
                    }
                } else if (result == Result.COUPON_EDITED) {
                    Coupon coupon = gson.fromJson(data.getStringExtra("coupon"), Coupon.class);

                    int i = 0;
                    while (i < coupons.size() && (!Objects.equals(coupons.get(i).getUid(), coupon.getUid()))) {
                        i++;
                    }

                    if (i != coupons.size()) {
                        coupons.set(i, coupon);
                        AsyncTask.execute(() -> couponDatabase.couponDao().update(coupon));
                        adapter.notifyItemChanged(i);
                    }

                    if (Util.isIsLoggedIn()) {
                        AsyncTask.execute(
                                () -> {
                                    WebCoupon webCoupon = WebCoupon.of(coupon);
                                    try {
                                        Util.updateCoupon(webCoupon, getApplicationContext());
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                }
                        );
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

                int result = data.getIntExtra("result", 0);

                if (result != Result.COUPON_UNCHANGED) {
                    Coupon coupon = gson.fromJson(data.getStringExtra("coupon"), Coupon.class);
                    AsyncTask.execute(() -> couponDatabase.couponDao().insertAll(coupon));
                    coupons.add(coupon);
                    adapter.notifyItemInserted(coupons.size() - 1);

                    if (Util.isIsLoggedIn()) {
                        AsyncTask.execute(
                                () -> {
                                    WebCoupon webCoupon = WebCoupon.of(coupon);
                                    try {
                                        WebCoupon resultCoupon = Util.updateCoupon(webCoupon,
                                                getApplicationContext());
                                        if (resultCoupon != null) {
                                            coupons.set(coupons.size() - 1, Coupon.of(resultCoupon));
                                        }
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                }
                        );
                    }
                }
            }
        }
    }
}