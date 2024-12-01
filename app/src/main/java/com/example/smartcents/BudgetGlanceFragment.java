package com.example.smartcents;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;

import java.util.ArrayList;
import java.util.List;

import static com.example.smartcents.TransactionRepository.*;

public class BudgetGlanceFragment extends Fragment {

    private TextView totalAmount;
    private BudgetSettings userSettings; //User budget settings
    private PieChart budgetPieChart; //Budget chart at a glance
    private RecyclerView budgetRecyclerView;
    private TransactionAdapter transactionAdapter;
    private TransactionRepository transactionRepository = TransactionRepository.getInstance();



    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //create the layout for this screen
        View view = inflater.inflate(R.layout.fragment_budget_glance, container, false);

        transactionRepository = TransactionRepository.getInstance();

        RecyclerView recyclerView = view.findViewById(R.id.rv_transactions);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        List<Transaction> transactions = transactionRepository.getTransactions();
        transactionAdapter = new TransactionAdapter(transactions, getContext());
        recyclerView.setAdapter(transactionAdapter);

        //Temporary budget setting for visual
        userSettings = new BudgetSettings(false, 4000, "Test Budget");

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //access repository
        transactionRepository = TransactionRepository.getInstance();

        transactionRepository.addTransaction(new Transaction(Transaction.Type.INCOME, "Salary", 4000, "11-01-2024", "Salary deposit"));
        transactionRepository.addTransaction(new Transaction(Transaction.Type.EXPENSE, "Rent", 1500, "11-03-2024", "Monthly rent payment"));
        transactionRepository.addTransaction(new Transaction(Transaction.Type.EXPENSE, "Electric", 150, "11-05-2024", "Monthly electric payment"));
        transactionRepository.addTransaction(new Transaction(Transaction.Type.EXPENSE, "Subscription", 19.99, "11-07-2024", "Monthly book club subscription"));

        //bind the top text bar
        totalAmount = view.findViewById(R.id.top_Text);
        //Bind the RV
        budgetRecyclerView = view.findViewById(R.id.rv_transactions);

        //Bind the pie chart
        budgetPieChart = view.findViewById(R.id.pie_chart);


        updateBalance();
        setupPieChart();
        setupRecyclerView();

        //Navigate to the Budget Detailed view
        view.findViewById(R.id.btn_detailed_view).setOnClickListener(v -> {
            Navigation.findNavController(v).navigate(R.id.action_budgetGlanceFragment_to_budgetDetailedFragment);
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        //refresh transaction list, balance, and charts when returning to this screen
        transactionAdapter.notifyDataSetChanged();
        updateBalance();
        setupPieChart();
    }

    private void updateBalance() {
        double balance = (userSettings.getAmount() - transactionRepository.getTotalExpenses()); //calculate the total balance
        totalAmount.setText(String.format("Remaining Balance: $%.2f", balance)); //display the balance
    }
    private void setupRecyclerView() {
        //create an adapter using the list of transactions from the repository
        transactionAdapter = new TransactionAdapter(transactionRepository.getTransactions(), getContext());

        //use a LinearLayoutManager for a vertical list
        budgetRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        //attach the adapter to the RecyclerView
        budgetRecyclerView.setAdapter(transactionAdapter);
    }
    private void setupPieChart() {
        List<PieEntry> pieEntries = new ArrayList<>();
        double incomeTotal = transactionRepository.getTotalIncome();
        double expenseTotal = transactionRepository.getTotalExpenses();

        pieEntries.add(new PieEntry((float) incomeTotal, "Budget"));
        pieEntries.add(new PieEntry((float) expenseTotal, "Expenses"));

        PieDataSet pieDataSet = new PieDataSet(pieEntries, "Income vs Expenses");
        pieDataSet.setColors(new int[]{Color.BLUE, Color.RED});
        PieData pieData = new PieData(pieDataSet);
        budgetPieChart.setData(pieData);
        budgetPieChart.invalidate(); //refresh the chart
    }
}