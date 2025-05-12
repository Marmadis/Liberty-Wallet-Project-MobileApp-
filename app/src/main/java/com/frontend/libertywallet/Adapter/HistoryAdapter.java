package com.frontend.libertywallet.Adapter;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.frontend.libertywallet.Entity.HistoryItem;
import com.frontend.libertywallet.R;

import java.util.List;

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.HistoryViewHolder> {

    private List<HistoryItem> items;

    public HistoryAdapter(List<HistoryItem> items) {
        this.items = items;
    }

    @NonNull
    @Override
    public HistoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_history, parent, false);
        return new HistoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HistoryViewHolder holder, int position) {
        HistoryItem item = items.get(position);
        holder.category.setText(item.getCategory());
        holder.date.setText(item.getDate());
        holder.amount.setText(item.getAmount());
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public void updateData(List<HistoryItem> newItems) {
        this.items = newItems;
        notifyDataSetChanged();
    }

    static class HistoryViewHolder extends RecyclerView.ViewHolder {
        TextView category, date, amount;

        public HistoryViewHolder(@NonNull View itemView) {
            super(itemView);
            category = itemView.findViewById(R.id.history_category);
            date = itemView.findViewById(R.id.history_date);
            amount = itemView.findViewById(R.id.history_amount);
        }
    }
}
