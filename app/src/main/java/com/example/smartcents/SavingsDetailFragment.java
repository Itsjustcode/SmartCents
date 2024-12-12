package com.example.smartcents;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.core.content.ContextCompat;


import java.util.ArrayList;
import java.util.List;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import android.graphics.Color;


public class SavingsDetailFragment extends Fragment {

    private Savings savings;
    private TextView savingsNameTextView, amountSavedTextView, amountRemainingTextView;
    private RecyclerView transactionsRecyclerView;
    private TransactionAdapter transactionAdapter;
    private List<Transaction> transactionsList = new ArrayList<>();
    private Button addTransactionButton, viewSavingsButton;
    private PieChart savingsChart;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_savings_detail, container, false);

        // Initialize UI elements
        savingsNameTextView = view.findViewById(R.id.text_view_savings_name);
        amountSavedTextView = view.findViewById(R.id.text_view_amount_saved);
        amountRemainingTextView = view.findViewById(R.id.text_view_amount_remaining);
        transactionsRecyclerView = view.findViewById(R.id.transactions_recycler_view);
        addTransactionButton = view.findViewById(R.id.add_transaction_button);
        viewSavingsButton = view.findViewById(R.id.view_savings_button);
        savingsChart = view.findViewById(R.id.savings_chart);
        // Get the selected savings object
        if (getArguments() != null) {
            savings = (Savings) getArguments().getSerializable("selectedSavings");
            if (savings != null) {
                savingsNameTextView.setText(savings.getName());
                amountSavedTextView.setText("Saved: $" + savings.getCurrentAmount());
                amountRemainingTextView.setText("Remaining: $" + (savings.getGoalAmount() - savings.getCurrentAmount()));
                setupSavingsChart();
            }
        }


        // Set up the RecyclerView
        transactionsList = new ArrayList<>();
        if (savings != null && savings.getTransactions() != null) {
            transactionsList.addAll(savings.getTransactions());
        }
        transactionAdapter = new TransactionAdapter(transactionsList, getContext());
        transactionsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        transactionsRecyclerView.setAdapter(transactionAdapter);

        updateTransactionsList();

        // Navigate to AddTransactionFragment when clicking addTransactionButton
        addTransactionButton.setOnClickListener(v -> {
            // Create a bundle to pass the savings ID or details to the AddTransactionFragment
            Bundle bundle = new Bundle();
            bundle.putSerializable("selectedSavings", savings);

            // Navigate to the AddTransactionFragment
            NavController navController = Navigation.findNavController(requireView());
            navController.navigate(R.id.action_savingsDetailFragment_to_addTransactionFragment, bundle);
        });

        // Navigate back to SavingsFragment when clicking viewSavingsButton
        viewSavingsButton.setOnClickListener(v -> {
            NavController navController = Navigation.findNavController(requireView());
            navController.navigate(R.id.action_savingsDetailFragment_to_savingsFragment);
        });

        return view;
    }

    private void setupSavingsChart() {
        if (savings != null) {
            double savedAmount = savings.getCurrentAmount();
            double remainingAmount = savings.getGoalAmount() - savedAmount;

            // Ensure remainingAmount is not negative
            if (remainingAmount < 0) remainingAmount = 0;

            List<PieEntry> entries = new ArrayList<>();
            entries.add(new PieEntry((float) savedAmount, "Saved"));
            entries.add(new PieEntry((float) remainingAmount, "Remaining"));

            PieDataSet dataSet = new PieDataSet(entries, "Savings Progress");
            dataSet.setColors(new int[]{
                    ContextCompat.getColor(requireContext(), R.color.colorPrimary),
                    ContextCompat.getColor(requireContext(), R.color.colorAccent)
            });
            dataSet.setValueTextSize(12f);

            PieData data = new PieData(dataSet);
            data.setValueTextColor(Color.WHITE);

            savingsChart.setData(data);
            savingsChart.setUsePercentValues(true);
            savingsChart.invalidate(); // Refresh chart
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        // Refresh the transactions list when the user returns from adding a new transaction
        updateTransactionsList();
    }

    private void updateTransactionsList() {
        // Fetch the updated transactions list (assuming that your SavingsRepository updates)
        transactionsList.clear();
        if (savings != null) {
            transactionsList.addAll(savings.getTransactions()); // Assuming Savings holds updated transactions
        }
        transactionAdapter.notifyDataSetChanged();
    }
}


