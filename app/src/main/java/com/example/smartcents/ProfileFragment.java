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

    private static final String TAG = "ProfileFragment";
    private final UserRepository userRepository = new UserRepository();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user != null) {
            loadUserProfile(view, user.getUid());
            setupSaveButton(view, user.getUid());
        } else {
            Log.e(TAG, "User is not authenticated.");
            Toast.makeText(requireContext(), "Error: User is not logged in.", Toast.LENGTH_SHORT).show();
        }
    }

    private void loadUserProfile(View view, String userId) {
        Log.d(TAG, "Loading profile for userId: " + userId);
        userRepository.getUserProfile(userId,
                documentSnapshot -> populateFields(view, documentSnapshot),
                e -> Log.e(TAG, "Error loading profile for userId: " + userId, e)
        );
    }

    private void populateFields(View view, DocumentSnapshot document) {
        EditText firstNameInput = view.findViewById(R.id.input_first_name);
        EditText lastNameInput = view.findViewById(R.id.input_last_name);
        EditText emailInput = view.findViewById(R.id.input_email);
        EditText phoneInput = view.findViewById(R.id.input_phone);

        if (document.exists()) {
            firstNameInput.setText(document.getString("firstName"));
            lastNameInput.setText(document.getString("lastName"));
            emailInput.setText(document.getString("email"));
            phoneInput.setText(document.getString("phoneNumber"));
        } else {
            Log.w(TAG, "No profile found for user.");
        }
    }

    private void setupSaveButton(View view, String userId) {
        Button saveButton = view.findViewById(R.id.save_profile_button);

        saveButton.setOnClickListener(v -> {
            EditText firstNameInput = view.findViewById(R.id.input_first_name);
            EditText lastNameInput = view.findViewById(R.id.input_last_name);
            EditText emailInput = view.findViewById(R.id.input_email);
            EditText phoneInput = view.findViewById(R.id.input_phone);

            String firstName = firstNameInput.getText().toString().trim();
            String lastName = lastNameInput.getText().toString().trim();
            String email = emailInput.getText().toString().trim();
            String phone = phoneInput.getText().toString().trim();

            if (firstName.isEmpty() || lastName.isEmpty() || email.isEmpty() || phone.isEmpty()) {
                Toast.makeText(requireContext(), "All fields are required", Toast.LENGTH_SHORT).show();
                return;
            }

            Map<String, Object> profileData = new HashMap<>();
            profileData.put("firstName", firstName);
            profileData.put("lastName", lastName);
            profileData.put("email", email);
            profileData.put("phoneNumber", phone);

            Log.d(TAG, "Saving profile for userId: " + userId);
            userRepository.saveUserProfile(userId, profileData,
                    () -> {
                        Toast.makeText(requireContext(), "Profile saved successfully!", Toast.LENGTH_SHORT).show();
                        Log.d(TAG, "Profile saved to Firestore.");
                        navigateToHome(view);
                    },
                    e -> {
                        Toast.makeText(requireContext(), "Error saving profile", Toast.LENGTH_SHORT).show();
                        Log.e(TAG, "Error saving profile", e);
                    }
            );
        });
    }

    private void navigateToHome(View view) {
        NavController navController = Navigation.findNavController(view);
        navController.navigate(R.id.action_profileFragment_to_homeFragment);
    }
}
