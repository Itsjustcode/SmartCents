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
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FieldValue;

import java.util.HashMap;
import java.util.Map;

public class EditViewProfileFragment extends Fragment {

    private FirebaseFirestore db;
    private static final String TAG = "EditViewProfileFragment";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_edit_view_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        db = FirebaseFirestore.getInstance();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user != null) {
            loadUserProfile(view, user.getUid());
            setupSaveButton(view, user.getUid());
        } else {
            Log.e(TAG, "User is not authenticated.");
            Toast.makeText(requireContext(), "Error: User is not logged in.", Toast.LENGTH_SHORT).show();
        }

        // Set up Cancel button
        Button cancelButton = view.findViewById(R.id.cancel_button);
        cancelButton.setOnClickListener(v -> {
            NavController navController = Navigation.findNavController(view);
            navController.navigate(R.id.action_editViewProfileFragment_to_homeFragment);
        });
    }

    private void loadUserProfile(View view, String userId) {
        db.collection("users")
                .document(userId)
                .collection("profile")
                .document("default")
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        populateFields(view, documentSnapshot);
                    } else {
                        Log.w(TAG, "No profile found for user.");
                        Toast.makeText(requireContext(), "No profile found.", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error loading profile", e);
                    Toast.makeText(requireContext(), "Error loading profile. Please try again.", Toast.LENGTH_SHORT).show();
                });
    }

    private void populateFields(View view, DocumentSnapshot document) {
        try {
            EditText firstNameInput = view.findViewById(R.id.input_first_name);
            EditText lastNameInput = view.findViewById(R.id.input_last_name);
            EditText emailInput = view.findViewById(R.id.input_email);
            EditText phoneInput = view.findViewById(R.id.input_phone);

            if (firstNameInput == null || lastNameInput == null || emailInput == null || phoneInput == null) {
                throw new NullPointerException("One or more EditText fields are null. Check layout IDs.");
            }

            firstNameInput.setText(document.getString("firstName"));
            lastNameInput.setText(document.getString("lastName"));
            emailInput.setText(document.getString("email"));
            phoneInput.setText(document.getString("phoneNumber"));
        } catch (NullPointerException e) {
            Log.e(TAG, "Error populating fields", e);
            Toast.makeText(requireContext(), "Error displaying profile. Please try again.", Toast.LENGTH_SHORT).show();
        }
    }

    private void setupSaveButton(View view, String userId) {
        Button saveButton = view.findViewById(R.id.btn_save_profile);

        saveButton.setOnClickListener(v -> {
            EditText firstNameInput = view.findViewById(R.id.input_first_name);
            EditText lastNameInput = view.findViewById(R.id.input_last_name);
            EditText emailInput = view.findViewById(R.id.input_email);
            EditText phoneInput = view.findViewById(R.id.input_phone);

            if (firstNameInput == null || lastNameInput == null || emailInput == null || phoneInput == null) {
                Toast.makeText(requireContext(), "Error saving profile. Please try again.", Toast.LENGTH_SHORT).show();
                Log.e(TAG, "Save button clicked but one or more EditText fields are null.");
                return;
            }

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
            profileData.put("updatedAt", FieldValue.serverTimestamp());

            db.collection("users")
                    .document(userId)
                    .collection("profile")
                    .document("default")
                    .set(profileData)
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(requireContext(), "Profile updated successfully!", Toast.LENGTH_SHORT).show();
                        navigateToHome(view);
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(requireContext(), "Error updating profile", Toast.LENGTH_SHORT).show();
                        Log.e(TAG, "Error updating profile", e);
                    });
        });
    }

    private void navigateToHome(View view) {
        NavController navController = Navigation.findNavController(view);
        navController.navigate(R.id.action_editViewProfileFragment_to_homeFragment);
    }
}
