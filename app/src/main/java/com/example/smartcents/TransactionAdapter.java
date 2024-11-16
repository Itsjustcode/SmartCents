package com.example.smartcents;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class TransactionAdapter extends RecyclerView.Adapter<TransactionAdapter.TransactionViewHolder> {
    private final List<Transaction> transactions;
    private final OnTransactionClickListener onTransactionClickListener;

    // Constructor with click listener
    public TransactionAdapter(List<Transaction> transactions, OnTransactionClickListener listener) {
        this.transactions = transactions;
        this.onTransactionClickListener = listener;
    }

    @NonNull
    @Override
    public TransactionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_transaction, parent, false);
        return new TransactionViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TransactionViewHolder holder, int position) {
        Transaction transaction = transactions.get(position);
        holder.tvCategory.setText(transaction.getCategory());
        holder.tvAmount.setText(String.format("$%.2f", transaction.getAmount()));
        holder.tvDate.setText(transaction.getDate());

        // Color coding for income and expense
        if ("expense".equalsIgnoreCase(transaction.getType())) {
            holder.tvAmount.setTextColor(holder.itemView.getContext().getResources().getColor(android.R.color.holo_red_dark));
        } else {
            holder.tvAmount.setTextColor(holder.itemView.getContext().getResources().getColor(android.R.color.holo_green_dark));
        }

        // Set click listener for the transaction item
        holder.itemView.setOnClickListener(v -> {
            if (onTransactionClickListener != null) {
                onTransactionClickListener.onTransactionClick(transaction);
            }
        });
    }

    @Override
    public int getItemCount() {
        return transactions.size();
    }

    static class TransactionViewHolder extends RecyclerView.ViewHolder {
        TextView tvCategory, tvAmount, tvDate;

        public TransactionViewHolder(@NonNull View itemView) {
            super(itemView);
            tvCategory = itemView.findViewById(R.id.tv_category);
            tvAmount = itemView.findViewById(R.id.tv_amount);
            tvDate = itemView.findViewById(R.id.tv_date);
        }
    }

    // Interface for click listener
    public interface OnTransactionClickListener {
        void onTransactionClick(Transaction transaction);
    }
}
