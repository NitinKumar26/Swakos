package com.swakos.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.swakos.R;
import com.swakos.helper.GlideApp;
import com.swakos.model.Update;
import java.util.ArrayList;

public class SliderAdapterNew extends RecyclerView.Adapter<SliderAdapterNew.ViewHolder> {
    private ArrayList<Update> mUpdatesList;
    private Context mContext;

    public SliderAdapterNew(ArrayList<Update> mUpdatesList, Context mContext) {
        this.mUpdatesList = mUpdatesList;
        this.mContext = mContext;
    }

    @NonNull
    @Override
    public SliderAdapterNew.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
    View view = LayoutInflater.from(mContext).inflate(R.layout.recyclerview_main_item, parent, false);
    return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SliderAdapterNew.ViewHolder holder, int position) {
        Update update = mUpdatesList.get(position);
        GlideApp.with(mContext)
                .load(update.getImage_address())
                .placeholder(mContext.getResources().getDrawable(R.drawable.placeholder_gradient))
                .into(holder.imageView);
    }

    @Override
    public int getItemCount() {
        return mUpdatesList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{
        ImageView imageView;
        ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.slider_image);
        }
    }

    public void setItems(ArrayList<Update> updates){
        this.mUpdatesList = updates;
    }
}
