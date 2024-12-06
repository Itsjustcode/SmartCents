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
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FieldValue;

import java.util.HashMap;
import java.util.Map;

public class ProfileFragment extends Fragment {

    // Firestore database instance
    private FirebaseFirestore db;
    private static final String TAG = "ProfileFragment";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Initialize Firestore
        db = FirebaseFirestore.getInstance();

        // Get current authenticated user
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user != null) {
            setupSaveButton(view, user.getUid()); // Setup save button functionality
        } else {
            Log.e(TAG, "User is not authenticated.");
            Toast.makeText(requireContext(), "Error: User is not logged in.", Toast.LENGTH_SHORT).show();
        }
    }

    private void setupSaveButton(View view, String userId) {
        Button saveButton = view.findViewById(R.id.save_profile_button);
        EditText firstNameInput = view.findViewById(R.id.input_first_name);
        EditText lastNameInput = view.findViewById(R.id.input_last_name);
        EditText emailInput = view.findViewById(R.id.input_email);
        EditText phoneInput = view.findViewById(R.id.input_phone);

        saveButton.setOnClickListener(v -> {
            String firstName = firstNameInput.getText().toString().trim();
            String lastName = lastNameInput.getText().toString().trim();
            String email = emailInput.getText().toString().trim();
            String phone = phoneInput.getText().toString().trim();

            // Validate that all fields are filled
            if (firstName.isEmpty() || lastName.isEmpty() || email.isEmpty() || phone.isEmpty()) {
                Toast.makeText(requireContext(), "All fields are required", Toast.LENGTH_SHORT).show();
                return;
            }

            // Prepare data to save
            Map<String, Object> profileData = new HashMap<>();
            profileData.put("firstName", firstName);
            profileData.put("lastName", lastName);
            profileData.put("email", email);
            profileData.put("phoneNumber", phone);
            profileData.put("profilePictureUrl", "https://example.com/profile.jpg");
            profileData.put("createdAt", FieldValue.serverTimestamp());

            // Log Firestore path for debugging
            Log.d(TAG, "Saving profile to: users/" + userId + "/profile");

// Save the data under the correct path
            db.collection("users")
                    .document(userId) // Use the dynamic userId of the signed-in user
                    .collection("profile") // Navigate to the 'profile' subcollection
                    .document("default") // Save the profile under the 'default' document
                    .set(profileData)
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(requireContext(), "Profile saved successfully!", Toast.LENGTH_SHORT).show();
                        Log.d(TAG, "Profile saved to Firestore.");

                        // Navigate to HomeFragment
                        NavController navController = Navigation.findNavController(view);
                        navController.navigate(R.id.action_profileFragment_to_homeFragment);
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(requireContext(), "Error saving profile", Toast.LENGTH_SHORT).show();
                        Log.e(TAG, "Error saving profile", e);
                    });
        });
    }
}
