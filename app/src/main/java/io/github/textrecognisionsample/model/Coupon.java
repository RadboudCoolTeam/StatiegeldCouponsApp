package io.github.textrecognisionsample.model;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "coupons")
public class Coupon {

    public static int BARCODE_LEN = 13;

    @PrimaryKey(autoGenerate = true)
    public int uid;

    @ColumnInfo(name = "date")
    private String date;

    @ColumnInfo(name = "money")
    private String money;

    @ColumnInfo(name = "data")
    private String data;

    @ColumnInfo(name = "supermarket_chain")
    private SupermarketChain supermarketChain;


    public Coupon(String date, String money, String data, SupermarketChain supermarketChain) {
        this.date = date;
        this.money = money;
        this.data = data;
        this.supermarketChain = supermarketChain;
    }

    public SupermarketChain getSupermarketChain() {
        return supermarketChain;
    }

    public void setSupermarketChain(SupermarketChain supermarketChain) {
        this.supermarketChain = supermarketChain;
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

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }
}
