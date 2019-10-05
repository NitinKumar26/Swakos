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

import com.google.firebase.Timestamp;
import com.swakos.R;
import com.swakos.model.ActivateDeal;

import org.apache.commons.lang3.StringUtils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.Map;

public class OrdersAdapter extends RecyclerView.Adapter<OrdersAdapter.ViewHolder> {
    private Context mContext;
    private ArrayList<ActivateDeal> mActivateDealsList;

    public OrdersAdapter(Context mContext, ArrayList<ActivateDeal> mActivateDealsList) {
        this.mContext = mContext;
        this.mActivateDealsList = mActivateDealsList;
    }

    @NonNull
    @Override
    public OrdersAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.activated_deal_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ActivateDeal activateDeal = mActivateDealsList.get(position);
        holder.tvDealTitle.setText(activateDeal.getDeal_title());

        if (String.valueOf(activateDeal.getDeal_actual_price()).equals("0")) holder.linearPrice.setVisibility(View.GONE);
        else {
            holder.tvDealPrice.setText(String.valueOf(activateDeal.getDeal_actual_price()));
            holder.tvDealOffPrice.setText(String.valueOf(activateDeal.getDeal_discounted_price()));
        }
        Map<String, Object> clientDetails = activateDeal.getClient_details();
        String clientName = (String) clientDetails.get("name");
        Timestamp timestamp = activateDeal.getCreated_at();
        Date date = timestamp.toDate();
        String created_at =  date.toString();
        holder.tvClientName.setText(clientName);
        holder.tvDate.setText(created_at);
        holder.tvStatus.setText(StringUtils.capitalize(activateDeal.getStatus()));

    }

    @Override
    public int getItemCount() {
        return mActivateDealsList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private TextView tvDealTitle, tvDealPrice, tvDealOffPrice, tvClientName, tvDate, tvStatus;
        private LinearLayout linearPrice;
        ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvDealTitle = itemView.findViewById(R.id.tv_deal_title);
            tvDealPrice = itemView.findViewById(R.id.tv_deal_regular_price);
            tvDealOffPrice = itemView.findViewById(R.id.tv_deal_off);
            tvClientName = itemView.findViewById(R.id.tv_client_name);
            tvDate = itemView.findViewById(R.id.tv_date);
            linearPrice = itemView.findViewById(R.id.linear_price);
            tvStatus = itemView.findViewById(R.id.tv_status);
            tvDealPrice.setPaintFlags(tvDealPrice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        }
    }

    public void setItems(ArrayList<ActivateDeal> mDealsList){
        this.mActivateDealsList = mDealsList;
    }
}
