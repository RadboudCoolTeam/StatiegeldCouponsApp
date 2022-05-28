package io.github.textrecognisionsample.model;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.util.UUID;

import io.github.textrecognisionsample.model.web.WebCoupon;

@Entity(tableName = "coupons")
public class Coupon {

    @PrimaryKey
    @NonNull
    @ColumnInfo(name = "id")
    private UUID uid;

    @ColumnInfo(name = "date")
    private String date;

    @ColumnInfo(name = "money")
    private String money;

    @ColumnInfo(name = "data")
    private String barcode;

    @ColumnInfo(name = "supermarket_chain")
    private SupermarketChain supermarketChain;

    public static Coupon of(WebCoupon webCoupon) {
        Coupon coupon = new Coupon(
                webCoupon.date,
                webCoupon.money,
                webCoupon.barcode,
                SupermarketChain.of(webCoupon.supermarketChain)
        );

        coupon.uid = webCoupon.localId;
        return coupon;
    }

    public Coupon(String date, String money, String barcode, SupermarketChain supermarketChain) {
        this.uid = UUID.randomUUID();
        this.date = date;
        this.money = money;
        this.barcode = barcode;
        this.supermarketChain = supermarketChain;
    }

    public SupermarketChain getSupermarketChain() {
        return supermarketChain;
    }

    public void setSupermarketChain(SupermarketChain supermarketChain) {
        this.supermarketChain = supermarketChain;
    }

    public UUID getUid() {
        return uid;
    }

    public void setUid(UUID uid) {
        this.uid = uid;
    }

    public String getMoney() {
        return money;
    }

    public void setMoney(String money) {
        this.money = money;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getBarcode() {
        return barcode;
    }

    public void setBarcode(String barcode) {
        this.barcode = barcode;
    }
}
