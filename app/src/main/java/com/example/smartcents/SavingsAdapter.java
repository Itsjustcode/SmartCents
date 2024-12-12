package com.example.smartcents;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.content.Context;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import android.os.Bundle;



import java.util.List;

public class SavingsAdapter extends RecyclerView.Adapter<SavingsAdapter.SavingsViewHolder> {

    private List<Savings> savingsList;
    private final Context context;
    private final OnSavingsClickListener listener;

    public interface OnSavingsClickListener {
        void onSavingsClick(Savings savings);
    }

    public SavingsAdapter(List<Savings> savingsList, Context context, OnSavingsClickListener listener) {
        this.savingsList = savingsList;
        this.context = context;
        this.listener = listener;
    }

    @NonNull
    @Override
    public SavingsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_savings, parent, false);
        return new SavingsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SavingsViewHolder holder, int position) {
        Savings savings = savingsList.get(position);
        // Set the goal name
        holder.savingsGoalName.setText(savings.getName());

        // Set the total goal amount
        String goalAmountText = "Total Goal Amount: $" + String.format("%.2f", savings.getGoalAmount());
        holder.savingsGoalAmount.setText(goalAmountText);

        // Add click listener to navigate to Savings Detail
        holder.itemView.setOnClickListener(v -> {
            NavController navController = Navigation.findNavController(v);
            Bundle bundle = new Bundle();
            bundle.putSerializable("selectedSavings", savings);
            navController.navigate(R.id.action_savingsFragment_to_savingsDetailFragment, bundle);
        });
    }

    @Override
    public int getItemCount() {
        return savingsList.size();
    }

    public void updateSavingsList(List<Savings> newSavingsList) {
        this.savingsList = newSavingsList;
    }


    public static class SavingsViewHolder extends RecyclerView.ViewHolder {
        TextView savingsGoalName;
        TextView savingsGoalAmount;

        public SavingsViewHolder(@NonNull View itemView) {
            super(itemView);
            savingsGoalName = itemView.findViewById(R.id.savings_goal_name);
            savingsGoalAmount = itemView.findViewById(R.id.savings_goal_amount);
        }
    }
}
