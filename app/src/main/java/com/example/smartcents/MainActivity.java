package com.example.smartcents;

// Import necessary classes
import android.os.Bundle; // Handles saving and restoring the activity state
import android.util.Log; // Allows us to log messages for debugging
import androidx.appcompat.app.AppCompatActivity; // Provides the base for activities using modern Android components
import androidx.navigation.fragment.NavHostFragment; // Helps manage navigation between app screens

// MainActivity: Entry point of the app
public class MainActivity extends AppCompatActivity {

    // Called when the activity is created
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("MainActivity", "Setting content view to activity_main"); // Log message for debugging
        setContentView(R.layout.activity_main); // Set the layout for this activity

        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager()
                .findFragmentById(R.id.nav_host_fragment); // Find the navigation host fragment

        if (navHostFragment != null) {
            Log.d("MainActivity", "NavHostFragment found successfully."); // Log success message
        } else {
            Log.e("MainActivity", "NavHostFragment not found."); // Log error message if fragment not found
        }
    }
}
