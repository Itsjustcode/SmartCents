package com.example.smartcents;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.DocumentSnapshot;

public class ProfileFragment extends Fragment {

    private FirebaseFirestore db;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        db = FirebaseFirestore.getInstance(); // Initialize Firestore
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user != null) {
            fetchUserProfile(user.getUid(), view);
        } else {
            Log.e("ProfileFragment", "User is not authenticated.");
        }
    }

    private void fetchUserProfile(String userId, View view) {
        db.collection("users").document(userId)
                .get()
                .addOnSuccessListener(documentSnapshot -> updateUIWithUserData(documentSnapshot, view))
                .addOnFailureListener(e -> Log.e("ProfileFragment", "Error fetching user profile", e));
    }

    private void updateUIWithUserData(DocumentSnapshot documentSnapshot, View view) {
        if (documentSnapshot.exists()) {
            // Extract user profile fields
            String firstName = documentSnapshot.getString("firstName");
            String lastName = documentSnapshot.getString("lastName");
            String email = documentSnapshot.getString("email");
            String phone = documentSnapshot.getString("phone");

            // Update UI with user profile data
            ((TextView) view.findViewById(R.id.profile_first_name)).setText("First Name: " + (firstName != null ? firstName : "N/A"));
            ((TextView) view.findViewById(R.id.profile_last_name)).setText("Last Name: " + (lastName != null ? lastName : "N/A"));
            ((TextView) view.findViewById(R.id.profile_email)).setText("Email: " + (email != null ? email : "N/A"));
            ((TextView) view.findViewById(R.id.profile_phone)).setText("Phone: " + (phone != null ? phone : "N/A"));
        } else {
            Log.e("ProfileFragment", "User profile document does not exist.");
        }
    }
}
