package com.example.smartcents;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

public class AddSavingsFragment extends Fragment {

    private EditText savingsNameEditText;
    private EditText goalAmountEditText;
    private EditText initialAmountEditText;
    private Button addSavingsButton;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_add_savings, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Initialize UI elements
        savingsNameEditText = view.findViewById(R.id.edit_text_savings_name);
        goalAmountEditText = view.findViewById(R.id.edit_text_goal_amount);
        initialAmountEditText = view.findViewById(R.id.edit_text_initial_amount);
        addSavingsButton = view.findViewById(R.id.button_add_savings);

        // Set click listener for add savings button
        addSavingsButton.setOnClickListener(v -> addSavings());
    }

    private void addSavings() {
        String name = savingsNameEditText.getText().toString().trim();
        String goalAmountStr = goalAmountEditText.getText().toString().trim();
        String initialAmountStr = initialAmountEditText.getText().toString().trim();

        if (name.isEmpty() || goalAmountStr.isEmpty() || initialAmountStr.isEmpty()) {
            Toast.makeText(getContext(), "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        double goalAmount;
        double initialAmount;

        try {
            goalAmount = Double.parseDouble(goalAmountStr);
            initialAmount = Double.parseDouble(initialAmountStr);
        } catch (NumberFormatException e) {
            Toast.makeText(getContext(), "Please enter valid amounts", Toast.LENGTH_SHORT).show();
            return;
        }

        // Create new savings goal
        Savings newSavings = new Savings(name, "General", goalAmount, initialAmount);

        // Add the new savings goal to the repository
        SavingsRepository.getInstance().addSavings(newSavings);

        // Navigate back to the savings list
        NavController navController = Navigation.findNavController(requireView());
        navController.navigate(R.id.action_addSavingsFragment_to_savingsFragment);

        // Show success message
        Toast.makeText(getContext(), "Savings goal added successfully", Toast.LENGTH_SHORT).show();
    }
}
