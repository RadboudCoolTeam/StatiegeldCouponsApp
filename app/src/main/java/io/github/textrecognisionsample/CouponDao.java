package io.github.textrecognisionsample;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface CouponDao {
    @Query("SELECT * FROM coupons")
    List<Coupon> getAll();

    @Query("SELECT * FROM coupons WHERE uid IN (:CouponIds)")
    List<Coupon> loadAllByIds(int[] CouponIds);

    @Query("SELECT * FROM coupons WHERE uid LIKE :uid LIMIT 1")
    Coupon findByUid(int uid);

    @Query("SELECT * FROM coupons WHERE supermarket_chain LIKE :first LIMIT 1")
    Coupon findBySupermarketChain(String first);

    @Query("DELETE FROM coupons")
    public void nukeTable();

    @Insert
    void insertAll(Coupon... Coupons);

    @Delete
    void delete(Coupon Coupon);
}
