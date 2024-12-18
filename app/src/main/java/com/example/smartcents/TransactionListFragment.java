package com.example.smartcents;

import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.*;

import java.util.ArrayList;
import java.util.List;

public class TransactionListFragment extends Fragment {

    //textView to display the user's current balance
    private TextView balanceTextView;

    //recyclerView to display the list of transactions
    private RecyclerView transactionRecyclerView;

    //adapter to bind transaction data to the RecyclerView
    private TransactionAdapter transactionAdapter;

    //repository to manage shared transaction data
    private TransactionRepository sharedTransactionRepository;

    // Pie and Bar charts for visualizing the transactions
    private PieChart pieChart;
    private BarChart barChart;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //create the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_transaction_list, container, false);

        sharedTransactionRepository = TransactionRepository.getInstance();

        RecyclerView recyclerView = view.findViewById(R.id.transaction_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        List<Transaction> transactions = sharedTransactionRepository.getTransactions();
        transactionAdapter = new TransactionAdapter(transactions, getContext());
        recyclerView.setAdapter(transactionAdapter);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //access the shared repository instance to manage transactions
        sharedTransactionRepository = TransactionRepository.getInstance();

        //add mock data to the repository
        addMockData();

        //find and bind the TextView for the balance
        balanceTextView = view.findViewById(R.id.tv_balance);

        //find and bind the RecyclerView for transactions
        transactionRecyclerView = view.findViewById(R.id.transaction_recycler_view);

        //find and bind the PieChart and BarChart for visualization
        pieChart = view.findViewById(R.id.pie_chart);
        barChart = view.findViewById(R.id.bar_chart);

        //set up the RecyclerView with the transaction list
        setupRecyclerView();

        //update the displayed balance
        updateBalance();

        //set up the charts
        setupPieChart();
        setupBarChart();

        //Add Transaction button clicks to navigate to AddTransactionFragment
        Button addTransactionButton = view.findViewById(R.id.btn_add_transaction);
        addTransactionButton.setOnClickListener(v -> {
            Navigation.findNavController(v).navigate(R.id.action_transactionListFragment_to_addTransactionFragment);
        });

        view.findViewById(R.id.btn_home_from_transaction).setOnClickListener(v -> {
            Navigation.findNavController(v).navigate(R.id.action_transactionListFragment_to_homeFragment);
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        //refresh transaction list, balance, and charts when returning to this screen
        transactionAdapter.notifyDataSetChanged();
        updateBalance();
        setupPieChart();
        setupBarChart();
    }

    //sets up the RecyclerView to display the list of transactions
    private void setupRecyclerView() {
        //create an adapter using the list of transactions from the repository
        transactionAdapter = new TransactionAdapter(sharedTransactionRepository.getTransactions(), getContext());

        //use a LinearLayoutManager for a vertical list
        transactionRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        //attach the adapter to the RecyclerView
        transactionRecyclerView.setAdapter(transactionAdapter);
    }

    //updates the balance TextView with the current balance from the repository
    private void updateBalance() {
        double balance = sharedTransactionRepository.getBalance(); //calculate the total balance
        balanceTextView.setText(String.format("Total Balance: $%.2f", balance)); //display the balance
    }

    //sets up the PieChart to display income vs expenses
    private void setupPieChart() {
        List<PieEntry> pieEntries = new ArrayList<>();
        double incomeTotal = sharedTransactionRepository.getTotalIncome();
        double expenseTotal = sharedTransactionRepository.getTotalExpenses();

        pieEntries.add(new PieEntry((float) incomeTotal, "Income"));
        pieEntries.add(new PieEntry((float) expenseTotal, "Expenses"));

        PieDataSet pieDataSet = new PieDataSet(pieEntries, "Income vs Expenses");
        pieDataSet.setColors(new int[]{Color.GREEN, Color.RED});
        PieData pieData = new PieData(pieDataSet);
        pieChart.setData(pieData);
        pieChart.invalidate(); //refresh the chart
    }

    //sets up the BarChart to display monthly expenses
    private void setupBarChart() {
        List<BarEntry> barEntries = new ArrayList<>();
        List<Transaction> transactions = sharedTransactionRepository.getTransactions();


        // Group transactions by month and calculate total expenses per month
        int[] monthlyExpenses = new int[12];
        for (Transaction transaction : transactions) {
            if (transaction.getType() == Transaction.Type.EXPENSE) {
                int month = 0;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    month = transaction.getDateAsLocalDate().getMonthValue() - 1;
                }
                ; // Assuming date is stored as LocalDate
                monthlyExpenses[month] += transaction.getAmount();
            }
        }

        for (int i = 0; i < 12; i++) {
            barEntries.add(new BarEntry(i + 1, (float) monthlyExpenses[i]));
        }

        BarDataSet barDataSet = new BarDataSet(barEntries, "Monthly Expenses");
        barDataSet.setColor(Color.BLUE);
        BarData barData = new BarData(barDataSet);
        barChart.setData(barData);
        barChart.invalidate(); //refresh the chart
    }

    // Adds mock data to the sharedTransactionRepository
    private void addMockData() {
        if (sharedTransactionRepository.getTransactions().isEmpty()) {
            sharedTransactionRepository.addTransaction(new Transaction(Transaction.Type.INCOME, "Salary", 5000.00, "2024-11-23", "Monthly Salary"));
            sharedTransactionRepository.addTransaction(new Transaction(Transaction.Type.EXPENSE, "Groceries", 300.00, "2024-11-23", "Weekly groceries"));
            sharedTransactionRepository.addTransaction(new Transaction(Transaction.Type.EXPENSE, "Electricity Bill", 100.00, "2024-11-23", "Monthly electricity bill"));
            sharedTransactionRepository.addTransaction(new Transaction(Transaction.Type.INCOME, "Freelance Project", 1500.00, "2024-11-23", "Project payment"));
            sharedTransactionRepository.addTransaction(new Transaction(Transaction.Type.EXPENSE, "Gym Membership", 50.00, "2024-11-23", "Monthly gym membership"));

        }
    }
}
