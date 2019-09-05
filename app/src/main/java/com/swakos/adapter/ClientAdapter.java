package com.swakos.adapter;

import android.content.Context;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.swakos.R;
import com.swakos.helper.HelperMethods;
import com.swakos.model.Client;
import java.util.ArrayList;
import java.util.HashMap;

public class ClientAdapter extends RecyclerView.Adapter<ClientAdapter.ViewHolder> {
    private ArrayList<Client> mClientsList;
    private Context mContext;
    private HashMap<String, Object> specialDealItem, secondSpecialDeal;
    private String actual_price_first, actual_price_second, off_price_first, off_price_second, title_first, title_second;

    public ClientAdapter(ArrayList<Client> mClientsList, Context mContext) {
        this.mClientsList = mClientsList;
        this.mContext = mContext;
    }

    @NonNull
    @Override
    public ClientAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.little_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ClientAdapter.ViewHolder holder, int position) {

        Client client = mClientsList.get(position);
        holder.tvClientName.setText(client.getName());
        holder.tvClientAddress.setText(client.getAddress());

        if (client.getRatings() == null){
            holder.tvRating.setBackground(mContext.getResources().getDrawable(R.drawable.bg_rating_default));
            holder.tvRating.setPadding(HelperMethods.pix(mContext, 8), HelperMethods.pix(mContext, 2),
                                        HelperMethods.pix(mContext, 8), HelperMethods.pix(mContext, 2));
            holder.tvRating.setText("-");
        }

        if (client.getSpecial_deals() != null) {
            if (client.getSpecial_deals().get("first_deal") != null) {
                specialDealItem = client.getSpecial_deals().get("first_deal");
                title_first = specialDealItem.get("title").toString();
                actual_price_first = specialDealItem.get("actual_price").toString();
                off_price_first = specialDealItem.get("off_price").toString();
            }

            if (client.getSpecial_deals().get("second_deal") != null) {
                secondSpecialDeal = client.getSpecial_deals().get("second_deal");
                title_second = secondSpecialDeal.get("title").toString();
                actual_price_second = secondSpecialDeal.get("actual_price").toString();
                off_price_second = secondSpecialDeal.get("off_price").toString();
            } else {
                holder.tvDealTitleSecond.setVisibility(View.GONE);
                holder.relativeSecond.setVisibility(View.GONE);
            }
        }else holder.linearSpecialDeals.setVisibility(View.GONE); //Hide Special Deals Linear Layout if there are no special deals available


        holder.tvDealPrice.setText(actual_price_first);
        holder.tvDealOffPrice.setText(off_price_first);
        holder.tvDealTitle.setText(title_first);

        holder.tvDealPriceSecond.setText(actual_price_second);
        holder.tvDealOffPriceSecond.setText(off_price_second);
        holder.tvDealTitleSecond.setText(title_second);

        //holder.tvRating.setText(client.getAverage_rating());
        Glide.with(mContext).load(client.getBanner_url()).placeholder(R.drawable.placeholder_gradient).into(holder.mDealImage);

    }

    @Override
    public int getItemCount() {
        return mClientsList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{
        private TextView tvDealTitle, tvDealPrice, tvDealOffPrice, tvClientName, tvClientAddress,
                tvDealTitleSecond, tvDealPriceSecond, tvDealOffPriceSecond, tvRating;
        private ImageView mDealImage;
        private LinearLayout linearSpecialDeals;
        private RelativeLayout relativeSecond;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            //Linear Special Deals
            linearSpecialDeals = itemView.findViewById(R.id.linear_special_deals);
            //Relative Second
            relativeSecond = itemView.findViewById(R.id.relative_second);

            //First Deal Views
            tvDealTitle = itemView.findViewById(R.id.deal_title);  //First Deal Title
            tvDealPrice = itemView.findViewById(R.id.deal_regular_price); //First Deal Regular Price
            tvDealOffPrice = itemView.findViewById(R.id.deal_off_price); //First Deal Off Price

            //Second Deal Views
            tvDealTitleSecond = itemView.findViewById(R.id.deal_title_second); //Second Deal Title
            tvDealPriceSecond = itemView.findViewById(R.id.deal_regular_price_second); //Second Deal Regular Price
            tvDealOffPriceSecond = itemView.findViewById(R.id.deal_off_price_second); // Second Deal Off Price

            //Client Details View
            mDealImage = itemView.findViewById(R.id.deal_image); //Client Image
            tvClientName = itemView.findViewById(R.id.client_name); //Client Name
            tvClientAddress = itemView.findViewById(R.id.client_address); //Client Address
            tvRating = itemView.findViewById(R.id.tv_rating); //Client Rating
            //Set Paint Flags
            tvDealPrice.setPaintFlags(tvDealPrice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            tvDealPriceSecond.setPaintFlags(tvDealPriceSecond.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        }
    }

    public void setItems(ArrayList<Client> clients){
        this.mClientsList = clients;
    }
}
