package com.example.smartcents;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class NavigationBarFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_navigation_bar, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        BottomNavigationView bottomNavigationView = view.findViewById(R.id.BottomNavigationView);

        // Obtain the NavController for the frame layout container
        NavHostFragment navHostFragment =
                (NavHostFragment) getChildFragmentManager().findFragmentById(R.id.frame_layout);
        NavController navController = navHostFragment.getNavController();

        // Set up the listener for BottomNavigationView
        bottomNavigationView.setOnItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case R.id.homeFragment:
                    navController.navigate(R.id.homeFragment);
                    return true;
                case R.id.action_statistics:
                    navController.navigate(R.id.fragment_statistics);
                    return true;
                case R.id.action_budgeting:
                    navController.navigate(R.id.fragment_budgeting);
                    return true;
                case R.id.action_profile:
                    navController.navigate(R.id.fragment_profile);
                    return true;
            }
            return false;
        });
    }
}