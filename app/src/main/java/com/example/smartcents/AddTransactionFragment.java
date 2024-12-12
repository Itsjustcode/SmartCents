package com.example.smartcents;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

public class AddTransactionFragment extends Fragment {

    // Fields for user input and transaction repository
    private EditText inputCategory, inputAmount, inputNotes;
    private RadioGroup typeGroup;
    private TransactionRepository sharedTransactionRepository;

    // Field to handle savings-specific transactions
    private String associatedSavingsGoal;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_add_transaction, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Initialize the shared transaction repository
        sharedTransactionRepository = TransactionRepository.getInstance();

        // Link UI components to corresponding fields
        inputCategory = view.findViewById(R.id.et_category);
        inputAmount = view.findViewById(R.id.et_amount);
        inputNotes = view.findViewById(R.id.et_notes);
        typeGroup = view.findViewById(R.id.rg_type);

        // Check if the fragment was passed a savings goal to associate the transaction with
        if (getArguments() != null) {
            associatedSavingsGoal = getArguments().getString("savingsGoalName");
        }

        // Set up the save button and its click action
        Button saveTransactionButton = view.findViewById(R.id.btn_save_transaction);
        saveTransactionButton.setOnClickListener(v -> {
            try {
                // Save the transaction and navigate back
                saveTransaction();
                Navigation.findNavController(v).navigateUp();
            } catch (Exception e) {
                Toast.makeText(requireContext(), "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void saveTransaction() {
        // Get the selected type from the radio group
        String typeText = ((RadioButton) typeGroup.findViewById(typeGroup.getCheckedRadioButtonId())).getText().toString().toUpperCase();
        Transaction.Type type = Transaction.Type.valueOf(typeText);

        // Retrieve user inputs for the transaction details
        String category = inputCategory.getText().toString().trim();
        double amount = Double.parseDouble(inputAmount.getText().toString().trim());
        String notes = inputNotes.getText().toString().trim();
        String date = "2024-12-10"; // Placeholder for current date logic

        // Create a new transaction
        Transaction newTransaction = new Transaction(type, category, amount, date, notes);

        if (type == Transaction.Type.SAVINGS && associatedSavingsGoal != null) {
            // Add the transaction to the specific savings goal
            SavingsRepository.getInstance().addSavings(associatedSavingsGoal, newTransaction);
        } else {
            // Add the transaction to the general repository
            sharedTransactionRepository.addTransaction(newTransaction);
        }
    }
}
