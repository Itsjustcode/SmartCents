package com.example.smartcents;

import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.data.*;

import java.util.ArrayList;
import java.util.List;

public class BudgetDetailedFragment extends Fragment {

    private BarChart budgetBarChart; //Budget chart
    private RecyclerView budgetRecyclerView;
    private TransactionAdapter transactionAdapter;
    private TransactionRepository transactionRepository = TransactionRepository.getInstance();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //create the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_budget_detailed, container, false);

        transactionRepository = TransactionRepository.getInstance();

        RecyclerView recyclerView = view.findViewById(R.id.rv_transactionsDetial);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        List<Transaction> transactions = transactionRepository.getTransactions();
        transactionAdapter = new TransactionAdapter(transactions, getContext());
        recyclerView.setAdapter(transactionAdapter);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //access repository
        transactionRepository = TransactionRepository.getInstance();

        budgetRecyclerView = view.findViewById(R.id.rv_transactionsDetial);

        //Bind the pie chart
        budgetBarChart = view.findViewById(R.id.bar_chart);

        //updateBalance(); //Update the balance remaining
        setupBarChart(); //Configure and display the BarChart
        setupRecyclerView(); //Configure and display the RV
    }

    private void setupRecyclerView() {
        //Create a transaction adapter
        transactionAdapter = new TransactionAdapter(transactionRepository.getTransactions(), getContext());

        budgetRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        //Bind the adapter to the RV
        budgetRecyclerView.setAdapter(transactionAdapter);
    }

    private void setupBarChart() {
        List<BarEntry> barEntries = new ArrayList<>();
        List<Transaction> transactions = transactionRepository.getTransactions();

        int[] monthlyExpenses = new int[12]; //Seperate by month
        for (Transaction transaction : transactions) {
            if (transaction.getType() == Transaction.Type.EXPENSE) {
                int month = 0;
                monthlyExpenses[month] += transaction.getAmount();
            }
        }
        //Add an entry for each month
        for (int i = 0; i < 12; i++) {
            barEntries.add(new BarEntry(i + 1, (float) monthlyExpenses[i]));
        }

        BarDataSet barDataSet = new BarDataSet(barEntries, "Monthly Expenses");
        barDataSet.setColor(Color.BLUE);
        BarData barData = new BarData(barDataSet);
        budgetBarChart.setData(barData);
        budgetBarChart.invalidate();
    }
}