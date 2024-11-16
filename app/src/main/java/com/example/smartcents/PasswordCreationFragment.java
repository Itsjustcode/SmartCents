package com.example.smartcents;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

// Fragment for creating a password
public class PasswordCreationFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate and return the layout for this fragment
        return inflater.inflate(R.layout.fragment_password_creation, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Find the continue button by its ID
        View continueButton = view.findViewById(R.id.btn_continue);

        // Set what happens when the continue button is clicked
        continueButton.setOnClickListener(v -> {
            NavController navController = Navigation.findNavController(view); // Get the navigation controller
            navController.navigate(R.id.action_passwordCreationFragment_to_doneFragment); // Navigate to the Done screen
        });
    }
}
