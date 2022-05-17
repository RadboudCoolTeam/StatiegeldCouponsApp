package io.github.textrecognisionsample;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

public class CustomGrid extends BaseAdapter {

    private final Context context;
    private final ArrayList<Coupon> coupons;

    public CustomGrid(Context context, ArrayList<Coupon> coupons) {
        this.context = context;
        this.coupons = coupons;
    }

    @Override
    public int getCount() {
        return coupons.size();
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        View grid;

        Coupon coupon = coupons.get(i);

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        if (view == null) {
            grid = inflater.inflate(R.layout.coupon_card, null);

            TextView couponDate = grid.findViewById(R.id.date_Text);
            TextView couponMoney = grid.findViewById(R.id.money_Text);
            ImageView imageView = grid.findViewById(R.id.imageView);

            couponDate.setText(coupon.getDate());
            couponMoney.setText(coupon.getMoney());
        } else {
            grid = viewGroup;
        }

        return grid;
    }
}
