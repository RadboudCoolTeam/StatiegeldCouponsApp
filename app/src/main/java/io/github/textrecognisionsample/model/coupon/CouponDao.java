package io.github.textrecognisionsample.model.coupon;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;
import java.util.UUID;

@Dao
public interface CouponDao {
    @Query("SELECT * FROM coupons")
    List<Coupon> getAll();

    @Query("SELECT * FROM coupons WHERE id IN (:CouponIds)")
    List<Coupon> loadAllByIds(int[] CouponIds);

    @Query("SELECT * FROM coupons WHERE id LIKE :uid LIMIT 1")
    Coupon findByUid(UUID uid);

    @Query("SELECT * FROM coupons WHERE supermarket_chain LIKE :first")
    List<Coupon> findBySupermarketChain(String first);

    @Query("DELETE FROM coupons")
    public void nukeTable();

    @Insert
    void insertAll(Coupon... Coupons);

    @Delete
    void delete(Coupon coupon);

    @Update
    void update(Coupon coupon);
}
