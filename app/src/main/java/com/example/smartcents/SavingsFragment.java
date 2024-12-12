package com.example.smartcents;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import java.util.ArrayList;
import java.util.List;

public class SavingsFragment extends Fragment {

    private RecyclerView savingsRecyclerView;
    private SavingsAdapter savingsAdapter;
    private List<Savings> savingsList;
    private SavingsRepository savingsRepository;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_savings, container, false);

        // Get instance of SavingsRepository
        savingsRepository = SavingsRepository.getInstance();

        savingsRecyclerView = view.findViewById(R.id.savings_recycler_view);
        savingsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // Get savings list from repository
        savingsList = new ArrayList<>(savingsRepository.getSavingsList());

        // Add mock data only if the list is empty
        if (savingsList.isEmpty()) {
            savingsList.add(new Savings("Emergency Fund", "General", 1000.00, 500.00));
            savingsList.add(new Savings("Vacation Fund", "Travel", 3000.00, 1000.00));
        }

        savingsAdapter = new SavingsAdapter(savingsList, getContext(), savings -> {
            // Navigate to the detailed view
            Bundle bundle = new Bundle();
            bundle.putSerializable("selectedSavings", savings);
            NavController navController = Navigation.findNavController(requireView());
            navController.navigate(R.id.action_savingsFragment_to_savingsDetailFragment, bundle);
        });
        savingsRecyclerView.setAdapter(savingsAdapter);

        Button addSavingsButton = view.findViewById(R.id.btn_add_savings);
        addSavingsButton.setOnClickListener(v -> {
            // Navigate back to SavingsFragment and refresh the data
            NavController navController = Navigation.findNavController(requireView());
            navController.navigate(R.id.action_savingsFragment_to_addSavingsFragment);
        });

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        // Clear and re-populate the list to reflect any changes made to the savings goals
        savingsList.clear();
        savingsList.addAll(savingsRepository.getSavingsList());
        savingsAdapter.notifyDataSetChanged();
    }

    private void navigateToSavingsDetail(Savings savings) {
        // Pass selected savings information to the detail fragment
        Bundle bundle = new Bundle();
        bundle.putSerializable("selectedSavings", savings);
        NavController navController = Navigation.findNavController(requireView());
        navController.navigate(R.id.action_savingsFragment_to_savingsDetailFragment, bundle);
    }
}
