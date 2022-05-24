package io.github.textrecognisionsample.activity;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.card.MaterialCardView;

import java.util.ArrayList;

import io.github.textrecognisionsample.R;
import io.github.textrecognisionsample.model.Coupon;

public class GridRecyclerViewAdapter extends RecyclerView.Adapter<GridRecyclerViewAdapter.ViewHolder> {

    private final Context context;
    private final ArrayList<Coupon> coupons;
    private final LayoutInflater inflater;
    private ItemClickListener clickListener;

    public GridRecyclerViewAdapter(Context context, ArrayList<Coupon> coupons) {
        this.context = context;
        this.coupons = coupons;

        inflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.coupon_card, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Coupon coupon = coupons.get(position);

        holder.couponDate.setText(coupon.getDate());
        holder.couponMoney.setText(coupon.getMoney() + "€");
        holder.imageView.setImageResource(coupon.getSupermarketChain().getDrawable());
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        protected TextView couponDate;
        protected TextView couponMoney;
        protected ImageView imageView;
        protected MaterialCardView materialCardView;

        public ViewHolder(View view) {
            super(view);
            couponDate = view.findViewById(R.id.date_Text);
            couponMoney = view.findViewById(R.id.money_Text);
            imageView = view.findViewById(R.id.supermarket_chain_Image);
            materialCardView = view.findViewById(R.id.coupon_id);

            materialCardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (clickListener != null) {
                        clickListener.onItemClick(view, getAdapterPosition());
                    }
                }
            });
        }

        @Override
        public void onClick(View view) {
            if (clickListener != null) {
                clickListener.onItemClick(view, getAdapterPosition());
            }
        }
    }

    void setClickListener(ItemClickListener clickListener) {
        this.clickListener = clickListener;
    }

    public interface ItemClickListener {
        void onItemClick(View view, int position);
    }

    public Coupon getItem(int position) {
        return coupons.get(position);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public int getItemCount() {
        return coupons.size();
    }
}
