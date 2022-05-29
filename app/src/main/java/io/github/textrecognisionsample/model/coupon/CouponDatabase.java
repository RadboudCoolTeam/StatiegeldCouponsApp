package io.github.textrecognisionsample.model.coupon;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(entities = {Coupon.class}, exportSchema = false, version = 1)
public abstract class CouponDatabase extends RoomDatabase {
    private static final String DB_NAME = "coupons";
    private static CouponDatabase instance;

    public static synchronized CouponDatabase getInstance(Context context) {
        if (instance == null) {
            instance = Room.databaseBuilder(context.getApplicationContext(), CouponDatabase.class,DB_NAME)
                    .build();
        }
        return instance;
    }

    public abstract CouponDao couponDao();
}
