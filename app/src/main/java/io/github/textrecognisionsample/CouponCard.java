package io.github.textrecognisionsample;

import android.content.Context;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.TextView;

public class CouponCard extends GridLayout {

    private TextView dateText;
    private TextView moneyText;

    private SupermarketChain supermarketChain;
    private ImageView supermarketChainImage;

    public CouponCard(Context context, Coupon coupon) {
        super(context);
        init(context, coupon);
    }

    private void init(Context context, Coupon coupon) {
        String date = coupon.getDate();
        String money = coupon.getMoney();
        SupermarketChain chain = coupon.getSupermarketChain();

        setColumnCount(2);
        setRowCount(2);

        setSupermarketChain(chain);
        initComponents(context);

        setDateText(date);
        setMoneyText(money);
    }

    private void initComponents(Context context) {
        dateText = new TextView(context);
        moneyText = new TextView(context);
        supermarketChainImage = new ImageView(context);

        addView(dateText, 0);
        addView(moneyText, 1);
        addView(supermarketChainImage, 2);

        int resId = supermarketChain.getDrawable();
        supermarketChainImage.setImageDrawable(context.getResources().getDrawable(resId));
    }

    public CharSequence getDateText() {
        return dateText.getText();
    }

    public CharSequence getMoneyText() {
        return moneyText.getText();
    }

    public void setDateText(String text) {
        dateText.setText(text);
    }

    public void setMoneyText(String text) {
        moneyText.setText(text);
    }

    public SupermarketChain getSupermarketChain() {
        return supermarketChain;
    }

    public void setSupermarketChain(SupermarketChain supermarketChain) {
        this.supermarketChain = supermarketChain;
    }

}
