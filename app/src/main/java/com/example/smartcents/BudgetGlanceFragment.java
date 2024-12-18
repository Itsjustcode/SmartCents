package com.example.smartcents;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;

public class BudgetGlanceFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //create the layout for this screen
        return inflater.inflate(R.layout.fragment_budget_glance, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //Navigate to the Budget Detailed view
        view.findViewById(R.id.btn_detailed_view).setOnClickListener(v -> {
            Navigation.findNavController(v).navigate(R.id.action_budgetGlanceFragment_to_budgetDetailedFragment);
        });


        //debugging - clicking Detailed View still does nothing
        view.findViewById(R.id.btn_detailed_view).setOnClickListener(v -> {
            Log.d("BudgetGlanceFragment", "Detailed View button clicked");
            Navigation.findNavController(v).navigate(R.id.action_budgetGlanceFragment_to_budgetDetailedFragment);
        });

        view.findViewById(R.id.btn_home_from_glance).setOnClickListener(v -> {
            Navigation.findNavController(v).navigate(R.id.action_budgetGlanceFragment_to_homeFragment);
        });
    }
}