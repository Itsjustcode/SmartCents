package com.example.smartcents;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class TransactionListFragment extends Fragment {

    private TextView tvBalance;
    private Button btnAddTransaction;
    private RecyclerView rvTransactions;
    private TransactionAdapter transactionAdapter;
    private TransactionRepository transactionRepository;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_transaction_list, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //initialize UI components
        tvBalance = view.findViewById(R.id.tv_balance);
        btnAddTransaction = view.findViewById(R.id.btn_add_transaction);
        rvTransactions = view.findViewById(R.id.rv_transactions);

        //initialize repository and adapter
        transactionRepository = new TransactionRepository();
        transactionAdapter = new TransactionAdapter(transactionRepository.getTransactions(), transaction -> {
            //handle click on transaction (optional for now), next patch lol
        });

        //set up RecyclerView
        rvTransactions.setLayoutManager(new LinearLayoutManager(getContext()));
        rvTransactions.setAdapter(transactionAdapter);

        //set up Add Transaction button
        btnAddTransaction.setOnClickListener(v -> addDummyTransaction());

        //update the balance display
        updateBalance();
    }

    //add a dummy transaction (for testing purposes)
    private void addDummyTransaction() {
        //example dummy transaction
        Transaction dummyTransaction = new Transaction(
                "expense",
                "Food",
                20.50,
                "11-15-2024",
                "Lunch"
        );
        transactionRepository.addTransaction(dummyTransaction);

        //notify the adapter and update balance
        transactionAdapter.notifyDataSetChanged();
        updateBalance();
    }

    //update the total balance display
    private void updateBalance() {
        double balance = transactionRepository.getBalance();
        tvBalance.setText(String.format("Total Balance: $%.2f", balance));
    }
}
