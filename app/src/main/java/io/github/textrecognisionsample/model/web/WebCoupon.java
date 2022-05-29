package io.github.textrecognisionsample.model.web;

import java.util.UUID;

import io.github.textrecognisionsample.model.coupon.Coupon;

public class WebCoupon {

    public UUID localId;

    public String date;

    public String money;

    public String barcode;

    public WebSupermarketChain supermarketChain;

    public long userId;

    public long id;

    public WebCoupon(UUID localId, String date, String money, String barcode, WebSupermarketChain webSupermarketChain, long userId, long id) {
        this.localId = localId;
        this.date = date;
        this.money = money;
        this.barcode = barcode;
        this.supermarketChain = webSupermarketChain;
        this.userId = userId;
        this.id = id;
    }

    public static WebCoupon of(Coupon coupon) {
        return new WebCoupon(
                coupon.getUid(),
                coupon.getDate(),
                coupon.getMoney(),
                coupon.getBarcode(),
                WebSupermarketChain.of(coupon.getSupermarketChain()),
                coupon.getUserId(),
                coupon.getDatabaseId()
        );
    }
}
