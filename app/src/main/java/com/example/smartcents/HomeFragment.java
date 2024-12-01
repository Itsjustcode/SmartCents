package com.example.smartcents;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;


public class HomeFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Set up the button to navigate to Transaction List.
        Button viewTransactionsButton = view.findViewById(R.id.btn_view_transactions);
        if (viewTransactionsButton != null) {
            viewTransactionsButton.setOnClickListener(v -> {
                Navigation.findNavController(v).navigate(R.id.action_homeFragment_to_transactionListFragment);
            });
        }

        //button to navigate to Budget At A Glance.
        Button viewBudgetButton = view.findViewById(R.id.btn_view_budget);
        viewBudgetButton.setOnClickListener(v ->
                Navigation.findNavController(v).navigate(R.id.action_homeFragment_to_budgetGlanceFragment)
        );
        // Button to view profile
        Button btnViewProfile = view.findViewById(R.id.btn_view_profile);
        btnViewProfile.setOnClickListener(v -> {
            NavController navController = Navigation.findNavController(view);
            navController.navigate(R.id.action_homeFragment_to_editViewProfileFragment);
        });

        // Button to navigate to Savings Activity.
        Button viewSavingsButton = view.findViewById(R.id.btn_view_savings);
        viewSavingsButton.setOnClickListener(v -> {
            NavController navController = Navigation.findNavController(view);
            navController.navigate(R.id.action_homeFragment_to_savingsFragment);
        });



    }
}
