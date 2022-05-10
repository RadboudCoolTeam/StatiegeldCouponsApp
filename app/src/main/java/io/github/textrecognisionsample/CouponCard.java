package io.github.textrecognisionsample;

import android.content.ContentResolver;
import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.net.Uri;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class CouponCard extends LinearLayout {

    int INDEX_0 = 0;
    int INDEX_1 = 1;
    int INDEX_2 = 2;

    private TextView dateText;
    private TextView moneyText;

    private SupermarketChain supermarketChain;
    private ImageView supermarketChainImage;


    public CouponCard(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        inflate(context, R.layout.coupon_card, this);
        int[] sets = {
                R.attr.date,
                R.attr.money,
                R.attr.supermarketChain
        };
        TypedArray a = context.obtainStyledAttributes(attrs, sets);
        String date = a.getString(INDEX_0);
        String money = a.getString(INDEX_1);
        SupermarketChain chain = SupermarketChain.values()[a.getInt(R.styleable.CouponCard_supermarketChain, 0)];
        a.recycle();

        setSupermarketChain(chain);
        initComponents(context);

        setDateText(date);
        setMoneyText(money);
    }

    private void initComponents(Context context) {
        dateText = findViewById(R.id.date_Text);
        moneyText = findViewById(R.id.money_Text);
        supermarketChainImage = findViewById(R.id.supermarket_chain_Image);

        Resources resources = context.getResources();
        int resId = supermarketChain.getDrawable();
        supermarketChainImage.setImageURI(Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://"
                + resources.getResourcePackageName(resId) + '/' + resources.getResourceTypeName(resId)
                + '/' + resources.getResourceEntryName(resId)));
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
