package com.example.smartcents;

import android.os.Bundle;
import android.util.Log;
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

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.HashMap;
import java.util.Map;

public class ProfileFragment extends Fragment {

    private static final String TAG = "ProfileFragment"; // Used for logging debug/error messages
    private final UserRepository userRepository = new UserRepository(); // Helper class to handle Firestore operations

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the fragment layout the XML file that defines the UI for this fragment
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Get the currently logged-in user
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user != null) {
            // If a user is logged in, load their profile data and set up the save button
            loadUserProfile(view, user.getUid());
            setupSaveButton(view, user.getUid());
        } else {
            // If no user is logged in, display an error message
            Log.e(TAG, "User is not authenticated.");
            Toast.makeText(requireContext(), "Error: User is not logged in.", Toast.LENGTH_SHORT).show();
        }
    }

    // Loads the user's profile from Firestore
    private void loadUserProfile(View view, String userId) {
        Log.d(TAG, "Loading profile for userId: " + userId);
        userRepository.getUserProfile(userId,
                documentSnapshot -> populateFields(view, documentSnapshot), // Populate the input fields if successful
                e -> Log.e(TAG, "Error loading profile for userId: " + userId, e) // Log any errors that occur
        );
    }

    // Fills the input fields with data retrieved from Firestore
    private void populateFields(View view, DocumentSnapshot document) {
        // Reference the input fields in the layout
        EditText firstNameInput = view.findViewById(R.id.input_first_name);
        EditText lastNameInput = view.findViewById(R.id.input_last_name);
        EditText emailInput = view.findViewById(R.id.input_email);
        EditText phoneInput = view.findViewById(R.id.input_phone);

        if (document.exists()) {
            // If the document exists, set the values of the input fields
            firstNameInput.setText(document.getString("firstName"));
            lastNameInput.setText(document.getString("lastName"));
            emailInput.setText(document.getString("email"));
            phoneInput.setText(document.getString("phoneNumber"));
        } else {
            // If no profile is found, log a warning
            Log.w(TAG, "No profile found for user.");
        }
    }

    // Sets up the functionality of the save button
    private void setupSaveButton(View view, String userId) {
        Button saveButton = view.findViewById(R.id.save_profile_button);

        saveButton.setOnClickListener(v -> {
            // Get the values entered in the input fields
            EditText firstNameInput = view.findViewById(R.id.input_first_name);
            EditText lastNameInput = view.findViewById(R.id.input_last_name);
            EditText emailInput = view.findViewById(R.id.input_email);
            EditText phoneInput = view.findViewById(R.id.input_phone);

            String firstName = firstNameInput.getText().toString().trim();
            String lastName = lastNameInput.getText().toString().trim();
            String email = emailInput.getText().toString().trim();
            String phone = phoneInput.getText().toString().trim();

            // Check if any of the fields are empty
            if (firstName.isEmpty() || lastName.isEmpty() || email.isEmpty() || phone.isEmpty()) {
                Toast.makeText(requireContext(), "All fields are required", Toast.LENGTH_SHORT).show();
                return; // Stop execution if any field is empty
            }

            // Prepare the data to save to Firestore
            Map<String, Object> profileData = new HashMap<>();
            profileData.put("firstName", firstName);
            profileData.put("lastName", lastName);
            profileData.put("email", email);
            profileData.put("phoneNumber", phone);

            // Save the profile to Firestore
            Log.d(TAG, "Saving profile for userId: " + userId);
            userRepository.saveUserProfile(userId, profileData,
                    () -> {
                        // Show a success message and navigate to the home screen
                        Toast.makeText(requireContext(), "Profile saved successfully!", Toast.LENGTH_SHORT).show();
                        Log.d(TAG, "Profile saved to Firestore.");
                        navigateToHome(view);
                    },
                    e -> {
                        // Log an error if saving fails and show a message to the user
                        Toast.makeText(requireContext(), "Error saving profile", Toast.LENGTH_SHORT).show();
                        Log.e(TAG, "Error saving profile", e);
                    }
            );
        });
    }

    // Navigates back to the home screen
    private void navigateToHome(View view) {
        NavController navController = Navigation.findNavController(view);
        navController.navigate(R.id.action_profileFragment_to_homeFragment);
    }
}
