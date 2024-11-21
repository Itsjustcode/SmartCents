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
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class TransactionListFragment extends Fragment {

    //textView to display the user's current balance
    private TextView balanceTextView;

    //recyclerView to display the list of transactions
    private RecyclerView transactionRecyclerView;

    //adapter to bind transaction data to the RecyclerView
    private TransactionAdapter transactionAdapter;

    //repository to manage shared transaction data
    private TransactionRepository sharedTransactionRepository;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //create the layout for this fragment
        return inflater.inflate(R.layout.fragment_transaction_list, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //access the shared repository instance to manage transactions
        sharedTransactionRepository = TransactionRepository.getInstance();

        //find and bind the TextView for the balance
        balanceTextView = view.findViewById(R.id.tv_balance);

        //find and bind the RecyclerView for transactions
        transactionRecyclerView = view.findViewById(R.id.rv_transactions);

        //set up the RecyclerView with the transaction list
        setupRecyclerView();

        //update the displayed balance
        updateBalance();

        //Add Transaction button clicks to navigate to AddTransactionFragment
        Button addTransactionButton = view.findViewById(R.id.btn_add_transaction);
        addTransactionButton.setOnClickListener(v -> {
            Navigation.findNavController(v).navigate(R.id.action_transactionListFragment_to_addTransactionFragment);
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        //refresh transaction list and balance when returning to this screen
        transactionAdapter.notifyDataSetChanged();
        updateBalance();
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
}
