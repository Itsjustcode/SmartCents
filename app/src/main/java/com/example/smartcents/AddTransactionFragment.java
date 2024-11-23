package com.example.smartcents;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

public class AddTransactionFragment extends Fragment {

    //fields and transaction repository
    private EditText inputCategory, inputAmount, inputNotes; //inputs for transaction details
    private RadioGroup typeGroup; //Radio buttons for selecting transaction type
    private TransactionRepository sharedTransactionRepository; //Shared repository for managing transactions

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //Inflate/create the layout file to create the UI for this fragment
        return inflater.inflate(R.layout.fragment_add_transaction, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //access the shared repository to save transactions
        sharedTransactionRepository = TransactionRepository.getInstance();

        //find and link input fields to their respective UI elements
        inputCategory = view.findViewById(R.id.et_category); //input for transaction category
        inputAmount = view.findViewById(R.id.et_amount); //input for transaction amount
        inputNotes = view.findViewById(R.id.et_notes); //input for optional transaction notes
        typeGroup = view.findViewById(R.id.rg_type); //radio group for selecting income or expense

        //Set up the save button and its click action
        Button saveTransactionButton = view.findViewById(R.id.btn_save_transaction);
        saveTransactionButton.setOnClickListener(v -> {
            //Save the transaction and navigate back to the transaction list
            saveTransaction();
            Navigation.findNavController(v).navigate(R.id.action_addTransactionFragment_to_transactionListFragment);
        });
    }

    private void saveTransaction() {
        //get the selected type from the radio group (income or expense)
        String typeText = ((RadioButton) typeGroup.findViewById(typeGroup.getCheckedRadioButtonId())).getText().toString().toUpperCase();
        Transaction.Type type = Transaction.Type.valueOf(typeText);

        //get the user inputs for category, amount, and notes
        String category = inputCategory.getText().toString();
        double amount = Double.parseDouble(inputAmount.getText().toString());
        String notes = inputNotes.getText().toString();

        //placeholder for the current date (to be replaced with dynamic date logic)
        String date = "11-19-2024";

        //create a new transaction object with the user inputs
        Transaction newTransaction = new Transaction(type, category, amount, date, notes);

        //add the transaction to the shared repository
        sharedTransactionRepository.addTransaction(newTransaction);
    }
}
