package com.swakos.adapter;

import android.content.Context;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.swakos.R;
import com.swakos.model.Deal;

import java.util.ArrayList;

public class ClientsDealAdapter extends RecyclerView.Adapter<ClientsDealAdapter.ViewHolder> {
    private Context mContext;
    private ArrayList<Deal> mDealsList;

    public ClientsDealAdapter(Context mContext, ArrayList<Deal> mDealsList) {
        this.mContext = mContext;
        this.mDealsList = mDealsList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.deal_item_client_details, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Deal deal = mDealsList.get(position);
        holder.tvDealTitle.setText(deal.getDeal_title());

        if (String.valueOf(deal.getDeal_actual_price()).equals("0")) holder.linearPrice.setVisibility(View.GONE);
        else {
            holder.tvDealPrice.setText(String.valueOf(deal.getDeal_actual_price()));
            holder.tvDealOffPrice.setText(String.valueOf(deal.getDeal_discounted_price()));
        }

    }

    @Override
    public int getItemCount() {
        return mDealsList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private TextView tvDealTitle, tvDealPrice, tvDealOffPrice;
        private LinearLayout linearPrice;
        ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvDealTitle = itemView.findViewById(R.id.tv_deal_title);
            tvDealPrice = itemView.findViewById(R.id.tv_deal_regular_price);
            tvDealOffPrice = itemView.findViewById(R.id.tv_deal_off);

            linearPrice = itemView.findViewById(R.id.linear_price);

            tvDealPrice.setPaintFlags(tvDealPrice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        }
    }

    public void setItems(ArrayList<Deal> mDealsList){
        this.mDealsList = mDealsList;
    }
}
