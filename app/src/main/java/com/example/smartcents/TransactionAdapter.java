package com.example.smartcents;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class TransactionAdapter extends RecyclerView.Adapter<TransactionAdapter.TransactionViewHolder> {

    //list of transactions to display
    private final List<Transaction> transactions;

    //listener for handling item clicks
    private final OnTransactionClickListener onTransactionClickListener;

    //context for accessing resources like colors
    private final Context context;

    //constructor initializes the list of transactions and the context
    public TransactionAdapter(List<Transaction> transactions, Context context) {
        this.transactions = transactions;
        this.context = context;

        //default listener does nothing
        this.onTransactionClickListener = transaction -> {};
    }

    @NonNull
    @Override
    public TransactionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //create the layout for each transaction item
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_transaction, parent, false);
        return new TransactionViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TransactionViewHolder holder, int position) {
        //get the current transaction from the list
        Transaction transaction = transactions.get(position);

        //set category, amount, and date text for the transaction item
        holder.tvCategory.setText(transaction.getCategory());
        holder.tvAmount.setText(String.format("$%.2f", transaction.getAmount()));
        holder.tvDate.setText(transaction.getDate());

        //change the amount text color based on whether it's an income or expense
        if (transaction.getType() == Transaction.Type.EXPENSE) {
            holder.tvAmount.setTextColor(holder.itemView.getContext().getResources().getColor(android.R.color.holo_red_dark));
        } else {
            holder.tvAmount.setTextColor(holder.itemView.getContext().getResources().getColor(android.R.color.holo_green_dark));
        }

        //set a click listener for the transaction item
        holder.itemView.setOnClickListener(v -> {
            if (onTransactionClickListener != null) {
                onTransactionClickListener.onTransactionClick(transaction); //trigger the listener's callback
            }
        });
    }

    @Override
    public int getItemCount() {
        //return the total number of transactions in the list
        return transactions.size();
    }

    //viewHolder class to hold and bind views for each transaction item
    static class TransactionViewHolder extends RecyclerView.ViewHolder {
        TextView tvCategory, tvAmount, tvDate; //textViews for transaction details

        public TransactionViewHolder(@NonNull View itemView) {
            super(itemView);

            //bind the TextViews to their respective IDs in the layout
            tvCategory = itemView.findViewById(R.id.tv_category);
            tvAmount = itemView.findViewById(R.id.tv_amount);
            tvDate = itemView.findViewById(R.id.tv_date);
        }
    }

    //interface to handle clicks on individual transactions
    public interface OnTransactionClickListener {
        void onTransactionClick(Transaction transaction); //callback for when a transaction is clicked
    }
}
