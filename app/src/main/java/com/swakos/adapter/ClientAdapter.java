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
    private HashMap<String, String> specialDealItem, secondSpecialDeal;
    private String actual_price_first, actual_price_second, off_price_first, off_price_second, title_first, title_second;
    private TextView tvDealTitle, tvDealPrice, tvDealOffPrice, tvDealTitleSecond, tvDealPriceSecond, tvDealOffPriceSecond;
    private RelativeLayout relativeFirst, relativeSecond;

    public ClientAdapter(ArrayList<Client> mClientsList, Context mContext) {
        this.mClientsList = mClientsList;
        this.mContext = mContext;
        setHasStableIds(true);
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

        //holder.tvRating.setText(client.getAverage_rating());
        Glide.with(mContext).load(client.getBanner_url()).placeholder(R.drawable.placeholder_gradient).into(holder.mDealImage);

        checkSpecialDeals(client, holder.linearFirstItem, holder.linearSecondItem);

    }

    @Override
    public int getItemCount() {
        return mClientsList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{
        private TextView tvClientName, tvClientAddress, tvRating;
        private ImageView mDealImage;
        private LinearLayout linearSpecialDeals;
        private View dividerView;
        private LinearLayout linearFirstItem, linearSecondItem;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            //Linear Special Deals
            linearSpecialDeals = itemView.findViewById(R.id.linear_special_deals);
            linearFirstItem = itemView.findViewById(R.id.linear_first_item);
            linearSecondItem = itemView.findViewById(R.id.linear_second_item);
            //Relative Second
            relativeSecond = itemView.findViewById(R.id.relative_second);
            relativeFirst = itemView.findViewById(R.id.relative_first);
            dividerView = itemView.findViewById(R.id.view_divider);

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

    private void checkSpecialDeals(@NonNull Client item, LinearLayout linearItem, LinearLayout liearItemSecondDeal) {

        if (item.getSpecial_deals() != null){
            if (item.getSpecial_deals().get("first_deal") != null) { // Check if the first special deal exists
                specialDealItem = item.getSpecial_deals().get("first_deal"); //get first special deal HashMap
                showViews(linearItem); //show the first special deal views
                setSpecialDeals(specialDealItem, tvDealTitle, tvDealPrice, tvDealOffPrice, relativeFirst, relativeSecond); //update the UI with special deal;
            }
            if (item.getSpecial_deals().get("second_deal") != null){
                secondSpecialDeal = item.getSpecial_deals().get("second_deal"); //Check if the second special deal exists
                showViews(liearItemSecondDeal); //Get the second special Deal
                setSpecialDeals(secondSpecialDeal, tvDealTitleSecond, tvDealPriceSecond, tvDealOffPriceSecond, relativeFirst, relativeSecond); //Update the UI with special deal
            }else hideViews(liearItemSecondDeal);
        }else {
            hideViews(linearItem);
            hideViews(liearItemSecondDeal);
        }
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    private void setSpecialDeals(@NonNull HashMap<String, String> deal, @NonNull TextView tvTitle, @NonNull TextView tvActualPrice,
                                 @NonNull TextView tvOffPrice, RelativeLayout relativeFirst, RelativeLayout relativeSecond) {
        if (deal == specialDealItem) {
            title_first = deal.get("title");
            actual_price_first = deal.get("actual_price");
            off_price_first = deal.get("off_price");

            if (actual_price_first.isEmpty())
                hidePriceView(relativeFirst);

            tvTitle.setText(title_first);
            tvActualPrice.setText(actual_price_first);
            tvOffPrice.setText(off_price_first);

        }else{
            title_second = deal.get("title");
            actual_price_second = deal.get("actual_price");
            off_price_second = deal.get("off_price");

            if (actual_price_second.isEmpty())
                hidePriceView(relativeFirst);

            tvDealTitleSecond.setText(title_second);
            tvDealPriceSecond.setText(actual_price_second);
            tvDealOffPriceSecond.setText(off_price_second);
        }


    }

    private void hidePriceView(@NonNull RelativeLayout relativeItems){
        relativeItems.setVisibility(View.GONE);
    }
    private void showPriceView (@NonNull RelativeLayout relativeItems){
        relativeItems.setVisibility(View.VISIBLE);
    }

    private void showViews(@NonNull LinearLayout item){
        item.setVisibility(View.VISIBLE);
    }

    private void hideViews(@NonNull LinearLayout item){
        item.setVisibility(View.GONE);
    }


}
