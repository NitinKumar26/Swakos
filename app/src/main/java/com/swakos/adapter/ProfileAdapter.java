package com.swakos.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.swakos.model.ProfileItem;
import com.swakos.R;

import java.util.ArrayList;

public class ProfileAdapter extends RecyclerView.Adapter<ProfileAdapter.ViewHolder> {

    private ArrayList<ProfileItem> mProfileItems;
    private Context mContext;


    public ProfileAdapter(ArrayList<ProfileItem> mProfileItems, Context mContext) {
        this.mProfileItems = mProfileItems;
        this.mContext = mContext;
    }

    @NonNull
    @Override
    public ProfileAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.profile_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProfileAdapter.ViewHolder holder, int position) {
        ProfileItem item = mProfileItems.get(position);
        holder.icon.setImageResource(item.getmIconResourceId());
        holder.itemLabel.setText(item.getmItemName());
    }

    @Override
    public int getItemCount() {
        return mProfileItems.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{
        private ImageView icon;
        private TextView itemLabel;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            icon = itemView.findViewById(R.id.icon);
            itemLabel = itemView.findViewById(R.id.text_profile_item);
        }
    }



}
