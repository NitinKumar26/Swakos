package com.swakos.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.swakos.R;
import com.swakos.model.Term;

import java.util.ArrayList;

public class TermAdapter extends RecyclerView.Adapter<TermAdapter.ViewHolder>{
    private ArrayList<Term> termArrayList;
    private Context mContext;

    public TermAdapter(ArrayList<Term> termArrayList, Context mContext) {
        this.termArrayList = termArrayList;
        this.mContext = mContext;
    }

    @Override
    public int getItemCount() {
        return termArrayList.size();
    }

    @NonNull
    @Override
    public TermAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(mContext).inflate(R.layout.term_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull TermAdapter.ViewHolder holder, int position) {
        Term term = termArrayList.get(position);
        holder.tvTerm.setText(term.getTerm());
    }

    class ViewHolder extends RecyclerView.ViewHolder{
        private TextView tvTerm;
        ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTerm = itemView.findViewById(R.id.tv_term);
        }
    }

    public void setItems(ArrayList<Term> items){
        this.termArrayList = items;
    }

}
