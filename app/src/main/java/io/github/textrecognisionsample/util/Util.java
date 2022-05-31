package io.github.textrecognisionsample.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Build;

import androidx.annotation.RequiresApi;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.zxing.common.BitMatrix;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

import io.github.textrecognisionsample.R;
import io.github.textrecognisionsample.model.coupon.Coupon;
import io.github.textrecognisionsample.model.coupon.CouponDao;
import io.github.textrecognisionsample.model.coupon.CouponDatabase;
import io.github.textrecognisionsample.model.user.UserData;
import io.github.textrecognisionsample.model.user.UserDatabase;
import io.github.textrecognisionsample.model.web.WebCoupon;
import io.github.textrecognisionsample.model.web.WebUser;
import io.github.textrecognisionsample.model.web.WebUserJsonSerializer;

public class Util {

    private static final int WHITE = 0xFFFFFFFF;
    private static final int BLACK = 0xFF000000;
    private static Properties properties = null;

    private static WebUser webUser = new WebUser("", "", null);

    private static boolean isLoggedIn = false;

    public static final int MAX_SCALE = 150;

    public static Properties getProperties(Context context) {
        if (properties == null) {
            InputStream inputStream = context.getResources().openRawResource(R.raw.app);
            properties = new Properties();
            try {
                properties.load(inputStream);
            } catch (IOException e) {
                // ignored
            }
        }

        return properties;
    }

    public static Bitmap convertToBitmap(Drawable drawable, int widthPixels, int heightPixels) {
        Bitmap mutableBitmap = Bitmap.createBitmap(widthPixels, heightPixels, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(mutableBitmap);
        drawable.setBounds(0, 0, widthPixels, heightPixels);
        drawable.draw(canvas);

        return mutableBitmap;
    }

    public static Bitmap resizeBitmap(Bitmap bitmap, int scale) {
        double ratio;
        if (bitmap.getWidth() >= bitmap.getHeight()) {
            ratio = (double) Util.MAX_SCALE / bitmap.getWidth();

            double newHeight = bitmap.getHeight() * ratio;

            bitmap = Bitmap.createScaledBitmap(bitmap, Util.MAX_SCALE, (int) newHeight, false);
        } else {
            ratio = (double) Util.MAX_SCALE / bitmap.getHeight();

            double newWidth = bitmap.getWidth() * ratio;

            bitmap = Bitmap.createScaledBitmap(bitmap, (int) newWidth, Util.MAX_SCALE, false);
        }

        return bitmap;
    }

    public static String getDefaultUser(Context c) {
        return getProperties(c).getProperty("default_user");
    }

    public static WebUser authUser(WebUser user, Context context) throws IOException {
        String address = Util.getAddress(context);
        URL url = new URL(address + "users/auth");
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        urlConnection.setRequestProperty("Content-Type", "application/json");
        urlConnection.setRequestProperty("Accept", "application/json");
        urlConnection.setRequestMethod("POST");

        Util.getWebUser().email = user.email;
        Util.getWebUser().password = user.password;
        Util.getWebUser().name = " ";

        GsonBuilder gsonBuilder = new GsonBuilder()
                .registerTypeAdapter(WebUser.class, new WebUserJsonSerializer())
                .registerTypeAdapter(byte[].class, new ByteArrayToBase64TypeAdapter());
        Gson gson = gsonBuilder.create();

        OutputStreamWriter writer = new OutputStreamWriter(urlConnection.getOutputStream());
        writer.write(gson.toJson(Util.getWebUser(), WebUser.class));
        writer.flush();

        StringBuilder stringBuilder = new StringBuilder();
        int httpResult = urlConnection.getResponseCode();
        if (httpResult == HttpURLConnection.HTTP_ACCEPTED) {
            BufferedReader br = new BufferedReader(
                    new InputStreamReader(urlConnection.getInputStream(), "utf-8"));
            String line = null;
            while ((line = br.readLine()) != null) {
                stringBuilder.append(line).append("\n");
            }
            br.close();

            WebUser userFromApi = gson.fromJson(stringBuilder.toString(), WebUser.class);

            AsyncTask.execute(() -> {
                List<UserData> dataList = UserDatabase.getInstance(context).userDao().getAll();
                UserData data;
                if (dataList.size() > 0) {
                    data = dataList.get(0);
                    data.setData(gson.toJson(Util.getWebUser()));
                    UserDatabase.getInstance(context).userDao().update(data);
                } else {
                    data = new UserData(gson.toJson(Util.getWebUser()));
                    UserDatabase.getInstance(context).userDao().insert(data);
                }
            });

            return userFromApi;
        }

        return null;
    }

    public static List<WebCoupon> getCoupons(WebUser user, Context context) throws IOException {
        String address = Util.getAddress(context);
        URL url = new URL(address + "users/" + user.id + "/coupons");
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        urlConnection.setRequestProperty("Content-Type", "application/json");
        urlConnection.setRequestProperty("Accept", "application/json");
        urlConnection.setRequestMethod("POST");

        GsonBuilder gsonBuilder = new GsonBuilder()
                .registerTypeAdapter(WebUser.class, new WebUserJsonSerializer());
        Gson gson = gsonBuilder.create();

        OutputStreamWriter writer = new OutputStreamWriter(urlConnection.getOutputStream());
        writer.write(gson.toJson(Util.getWebUser(), WebUser.class));
        writer.flush();

        StringBuilder stringBuilder = new StringBuilder();
        int httpResult = urlConnection.getResponseCode();

        if (httpResult == HttpURLConnection.HTTP_ACCEPTED) {
            BufferedReader br = new BufferedReader(
                    new InputStreamReader(urlConnection.getInputStream(), "utf-8"));
            String line = null;
            while ((line = br.readLine()) != null) {
                stringBuilder.append(line).append("\n");
            }
            br.close();

            return Arrays.asList(gson.fromJson(stringBuilder.toString(), WebCoupon[].class));
        }

        return new ArrayList<>();
    }

    public static WebCoupon updateCoupon(WebCoupon webCoupon, Context context) throws IOException {
        String address = Util.getAddress(context);
        URL url = new URL(address + "users/" + getWebUser().id + "/updateCoupon");
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        urlConnection.setRequestProperty("Content-Type", "application/json");
        urlConnection.setRequestProperty("Accept", "application/json");
        urlConnection.setRequestMethod("POST");

        GsonBuilder gsonBuilder = new GsonBuilder()
                .registerTypeAdapter(WebUser.class, new WebUserJsonSerializer());
        Gson gson = gsonBuilder.create();

        OutputStreamWriter writer = new OutputStreamWriter(urlConnection.getOutputStream());

        writer.write(gson.toJson(Pair.of(Util.getWebUser(), webCoupon), Pair.class));
        writer.flush();

        StringBuilder stringBuilder = new StringBuilder();
        int httpResult = urlConnection.getResponseCode();

        if (httpResult == HttpURLConnection.HTTP_ACCEPTED) {
            BufferedReader br = new BufferedReader(
                    new InputStreamReader(urlConnection.getInputStream(), "utf-8"));
            String line = null;
            while ((line = br.readLine()) != null) {
                stringBuilder.append(line).append("\n");
            }
            br.close();

            return gson.fromJson(stringBuilder.toString(), WebCoupon.class);
        }

        return null;
    }

    public static String deleteCoupon(WebCoupon webCoupon, Context context) throws IOException {
        String address = Util.getAddress(context);
        URL url = new URL(address + "users/" + getWebUser().id + "/delete");
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        urlConnection.setRequestProperty("Content-Type", "application/json");
        urlConnection.setRequestProperty("Accept", "application/json");
        urlConnection.setRequestMethod("POST");

        GsonBuilder gsonBuilder = new GsonBuilder()
                .registerTypeAdapter(WebUser.class, new WebUserJsonSerializer());
        Gson gson = gsonBuilder.create();

        OutputStreamWriter writer = new OutputStreamWriter(urlConnection.getOutputStream());

        writer.write(gson.toJson(Pair.of(Util.getWebUser(), webCoupon), Pair.class));
        writer.flush();

        StringBuilder stringBuilder = new StringBuilder();
        int httpResult = urlConnection.getResponseCode();

        if (httpResult == HttpURLConnection.HTTP_ACCEPTED) {
            BufferedReader br = new BufferedReader(
                    new InputStreamReader(urlConnection.getInputStream(), "utf-8"));
            String line = null;
            while ((line = br.readLine()) != null) {
                stringBuilder.append(line).append("\n");
            }
            br.close();

            return stringBuilder.toString();
        }

        return null;
    }

    public static String createAccount(WebUser webUser, Context context) throws IOException {
        String address = Util.getAddress(context);
        URL url = new URL(address + "users/new");
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        urlConnection.setRequestProperty("Content-Type", "application/json");
        urlConnection.setRequestProperty("Accept", "application/json");
        urlConnection.setRequestMethod("POST");

        GsonBuilder gsonBuilder = new GsonBuilder()
                .registerTypeAdapter(WebUser.class, new WebUserJsonSerializer());
        Gson gson = gsonBuilder.create();

        OutputStreamWriter writer = new OutputStreamWriter(urlConnection.getOutputStream());

        writer.write(gson.toJson(webUser, WebUser.class));
        writer.flush();

        StringBuilder stringBuilder = new StringBuilder();
        int httpResult = urlConnection.getResponseCode();

        if (httpResult == HttpURLConnection.HTTP_ACCEPTED) {
            BufferedReader br = new BufferedReader(
                    new InputStreamReader(urlConnection.getInputStream(), "utf-8"));
            String line = null;
            while ((line = br.readLine()) != null) {
                stringBuilder.append(line);
            }
            br.close();

            return stringBuilder.toString();
        }

        return null;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public static void syncDatabases(Context context) throws IOException {

        CouponDao dao = CouponDatabase.getInstance(context).couponDao();

        List<Coupon> coupons = dao.getAll();

        for (Coupon coupon : coupons) {
            updateCoupon(WebCoupon.of(coupon), context);
        }

        List<WebCoupon> fromDb = getCoupons(getWebUser(), context);

        for (WebCoupon webCoupon : fromDb) {
            if (dao.getAll().stream().anyMatch(e -> e.getUid().equals(webCoupon.localId))) {
                dao.update(Coupon.of(webCoupon));
            } else {
                dao.insertAll(Coupon.of(webCoupon));
            }
        }

    }

    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    public static String getAddress(Context context) {
        return getProperties(context).getProperty("address");
    }

    public static Bitmap matrixToBitmap(BitMatrix matrix) {
        int width = matrix.getWidth();
        int height = matrix.getHeight();
        int[] pixels = new int[width * height];
        for (int y = 0; y < height; y++) {
            int offset = y * width;
            for (int x = 0; x < width; x++) {
                pixels[offset + x] = matrix.get(x, y) ? BLACK : WHITE;
            }
        }

        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        bitmap.setPixels(pixels, 0, width, 0, 0, width, height);

        return bitmap;
    }

    public static WebUser getWebUser() {
        return webUser;
    }

    public static void setWebUser(WebUser newWebUser) {
        webUser = newWebUser;
    }

    public static boolean isIsLoggedIn() {
        return isLoggedIn;
    }

    public static void setIsLoggedIn(boolean isLoggedIn) {
        Util.isLoggedIn = isLoggedIn;
    }
}
