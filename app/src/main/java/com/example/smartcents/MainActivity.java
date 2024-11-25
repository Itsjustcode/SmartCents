package com.example.smartcents;

// Import necessary classes
import android.os.Bundle;
import android.util.Log;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;
import com.example.smartcents.databinding.ActivityMainBinding;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding; // Binding for the activity's layout

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot()); // Set the activity layout using ViewBinding

        // Set up NavController for the navigation host
        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager()
                .findFragmentById(R.id.nav_host_fragment);

        if (navHostFragment != null) {
            Log.d("MainActivity", "NavHostFragment found successfully.");
            NavController navController = navHostFragment.getNavController();

            // Set up the BottomNavigationView with the NavController
            BottomNavigationView bottomNavigationView = binding.bottomNavigationView;
            NavigationUI.setupWithNavController(bottomNavigationView, navController);
        } else {
            Log.e("MainActivity", "NavHostFragment not found.");
        }
    }
}