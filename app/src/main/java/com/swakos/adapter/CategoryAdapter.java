package com.swakos.adapter;

import android.content.Context;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.swakos.R;
import com.swakos.model.Category;

import java.util.ArrayList;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.ViewHolder> {
    private ArrayList<Category> mCategoryItemsList;
    private Context mContext;

    public CategoryAdapter(ArrayList<Category> mCategoryItemsList, Context mContext) {
        this.mCategoryItemsList = mCategoryItemsList;
        this.mContext = mContext;
    }

    @NonNull
    @Override
    public CategoryAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.category_item, parent, false);
        return new ViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull CategoryAdapter.ViewHolder holder, int position) {
        Category item = mCategoryItemsList.get(position);
        holder.icon.setImageResource(item.getmIconResourceId());
        holder.name.setText(item.getmName());
    }


    @Override
    public int getItemCount() {
        return mCategoryItemsList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{
        ImageView icon;
        TextView name;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            icon = itemView.findViewById(R.id.category_icon);
            name = itemView.findViewById(R.id.name);

        }
    }


}
