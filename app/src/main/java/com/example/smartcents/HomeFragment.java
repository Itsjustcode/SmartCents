package com.example.smartcents;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

public class HomeFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Create the layout for the home screen.
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Set up the button to navigate to Transaction List.
        Button viewTransactionsButton = view.findViewById(R.id.btn_view_transactions);
        if (viewTransactionsButton != null) {
            viewTransactionsButton.setOnClickListener(v ->
                    Navigation.findNavController(v).navigate(R.id.action_homeFragment_to_transactionListFragment)
            );
        }
    }
}
