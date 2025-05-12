package com.frontend.libertywallet.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.frontend.libertywallet.Entity.PopularItem;
import com.frontend.libertywallet.R;

import java.util.List;

public class PopularAdapter extends RecyclerView.Adapter<PopularAdapter.PopularViewHolder> {

    private List<PopularItem> itemList;

    public PopularAdapter(List<PopularItem> itemList) {
        this.itemList = itemList;
    }

    @NonNull
    @Override
    public PopularViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_recommendation, parent, false);
        return new PopularViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PopularViewHolder holder, int position) {
        PopularItem item = itemList.get(position);
        holder.title.setText(item.getTitle());
        holder.amount.setText(item.getText());
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    public static class PopularViewHolder extends RecyclerView.ViewHolder {
        TextView title, amount;

        public PopularViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.title_recommendation);
            amount = itemView.findViewById(R.id.totalAmount);
        }
    }

    public void updateData(List<PopularItem> newList) {
        itemList = newList;
        notifyDataSetChanged();
    }
}

