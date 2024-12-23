package com.example.smartcents;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;

import java.util.ArrayList;
import java.util.List;

public class BudgetGlanceFragment extends Fragment {

    private boolean firstTime = true;
    private TextView totalAmount; //For the balance text
    private TextView btnName; //For the button text to display users name
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

        //Temporary user settings for visual
        CreateUser();

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //access repository
        transactionRepository = TransactionRepository.getInstance();

        //Add fake transactions for now
        if(firstTime) {
            transactionRepository.addTransaction(new Transaction(Transaction.Type.INCOME, "Salary", 4000, "11-01-2024", "Salary deposit"));
            transactionRepository.addTransaction(new Transaction(Transaction.Type.EXPENSE, "Rent", 1500, "11-03-2024", "Monthly rent payment"));
            transactionRepository.addTransaction(new Transaction(Transaction.Type.EXPENSE, "Electric", 150, "11-05-2024", "Monthly electric payment"));
            transactionRepository.addTransaction(new Transaction(Transaction.Type.EXPENSE, "Subscription", 19.99, "11-07-2024", "Monthly book club subscription"));
            firstTime = false;
        }

        //Bind the top text bar with balance
        totalAmount = view.findViewById(R.id.top_Text);
        //Bind the button text with name
        btnName = view.findViewById(R.id.btn_detailed_view);
        //Bind the RV
        budgetRecyclerView = view.findViewById(R.id.rv_transactions);

        //Bind the pie chart
        budgetPieChart = view.findViewById(R.id.pie_chart);


        updateBalance(); //Update the balance remaining
        setupPieChart(); //Configure and display the PieChart
        setupRecyclerView(); //Configure and display the RV

        //Navigate to the Budget Detailed view
        view.findViewById(R.id.btn_detailed_view).setOnClickListener(v -> {
            Navigation.findNavController(v).navigate(R.id.action_budgetGlanceFragment_to_budgetDetailedFragment);
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        //Update any changes to the budget amount, and instantiate the PieChart
        transactionAdapter.notifyDataSetChanged();
        updateBalance();
        setupPieChart();
    }

    private void CreateUser() { //Will enhance later, creates a basic default profile now
        userSettings = new BudgetSettings("Brendan", false, 4000, "Test Budget");
    }
    private void updateBalance() { //Updates the balance and budget name if changed
        btnName.setText(userSettings.getUserName() + "'s Budget");
        double balance = (userSettings.getAmount() - transactionRepository.getTotalExpenses()); //calculate the total balance
        totalAmount.setText(String.format("Remaining Balance: $%.2f", balance)); //display the balance
    }
    private void setupRecyclerView() {
        //Create a transaction adapter
        transactionAdapter = new TransactionAdapter(transactionRepository.getTransactions(), getContext());

        budgetRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        //Bind the adapter to the RV
        budgetRecyclerView.setAdapter(transactionAdapter);
    }
    private void setupPieChart() { //Setup and configure the PieChart
        List<PieEntry> pieEntries = new ArrayList<>();
        double incomeTotal = transactionRepository.getTotalIncome();
        double expenseTotal = transactionRepository.getTotalExpenses();

        //Split The total budget from expenses
        pieEntries.add(new PieEntry((float) incomeTotal, "Budget"));
        pieEntries.add(new PieEntry((float) expenseTotal, "Expenses"));

        //Customize
        PieDataSet pieDataSet = new PieDataSet(pieEntries, "Income vs Expenses");
        pieDataSet.setColors(new int[]{Color.BLUE, Color.RED});
        PieData pieData = new PieData(pieDataSet);
        budgetPieChart.setData(pieData);
        budgetPieChart.invalidate(); //refresh the chart
    }
}