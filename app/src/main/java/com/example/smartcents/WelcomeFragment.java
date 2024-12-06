package com.example.smartcents;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.ErrorCodes;
import com.firebase.ui.auth.FirebaseAuthUIActivityResultContract;
import com.firebase.ui.auth.IdpResponse;
import com.firebase.ui.auth.data.model.FirebaseAuthUIAuthenticationResult;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FieldValue;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WelcomeFragment extends Fragment {

    private static final String TAG = "WelcomeFragment";
    private final UserRepository userRepository = new UserRepository();

    // FirebaseUI Launcher
    private final ActivityResultLauncher<Intent> signInLauncher =
            registerForActivityResult(
                    new FirebaseAuthUIActivityResultContract(),
                    this::onSignInResult
            );

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_welcome, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        MaterialButton loginRegisterButton = view.findViewById(R.id.loginregisterButton);
        loginRegisterButton.setOnClickListener(v -> startSignInFlow());
    }

    private void startSignInFlow() {
        // Configure FirebaseUI for multiple login methods
        List<AuthUI.IdpConfig> providers = Arrays.asList(
                new AuthUI.IdpConfig.EmailBuilder().build(),
                new AuthUI.IdpConfig.PhoneBuilder().build(),
                new AuthUI.IdpConfig.GoogleBuilder().build()
        );

        // Create and launch sign-in intent
        Intent signInIntent = AuthUI.getInstance()
                .createSignInIntentBuilder()
                .setAvailableProviders(providers)
                .setLogo(R.drawable.ic_launcher_background) // Replace with your app logo
                .build();
        signInLauncher.launch(signInIntent);
    }

    private void onSignInResult(FirebaseAuthUIAuthenticationResult result) {
        IdpResponse response = result.getIdpResponse();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (result.getResultCode() == Activity.RESULT_OK && user != null) {
            if (response != null && response.isNewUser()) {
                // New user: Save profile to Firestore
                saveUserProfile(user);
                navigateToProfile("Please complete your profile.");
            } else {
                // Existing user: Check Firestore profile completeness
                checkUserProfile(user);
            }
        } else {
            handleSignInError(response);
        }
    }

    private void saveUserProfile(FirebaseUser user) {
        Map<String, Object> userProfile = new HashMap<>();
        userProfile.put("firstName", ""); // Placeholder
        userProfile.put("lastName", "");  // Placeholder
        userProfile.put("email", user.getEmail());
        userProfile.put("phone", user.getPhoneNumber());
        userProfile.put("createdAt", FieldValue.serverTimestamp());

        Log.d(TAG, "Saving new user profile for UID: " + user.getUid());
        userRepository.saveUserProfile(user.getUid(), userProfile,
                () -> Log.d(TAG, "User profile saved successfully for new user."),
                e -> Log.e(TAG, "Error saving user profile for new user.", e)
        );
    }

    private void checkUserProfile(FirebaseUser user) {
        userRepository.getUserProfile(user.getUid(),
                documentSnapshot -> {
                    if (documentSnapshot.exists() && documentSnapshot.contains("firstName") && documentSnapshot.contains("lastName")) {
                        navigateToHome("Welcome back!");
                    } else {
                        navigateToProfile("Please complete your profile.");
                    }
                },
                e -> Log.e(TAG, "Error checking user profile", e)
        );
    }

    private void navigateToHome(String message) {
        NavController navController = Navigation.findNavController(requireView());
        navController.navigate(R.id.action_welcomeFragment_to_homeFragment);
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show();
    }

    private void navigateToProfile(String message) {
        NavController navController = Navigation.findNavController(requireView());
        navController.navigate(R.id.action_welcomeFragment_to_profileFragment);
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show();
    }

    private void handleSignInError(IdpResponse response) {
        if (response == null) {
            // User canceled sign-in
            Toast.makeText(requireContext(), "Sign-in canceled. Please try again.", Toast.LENGTH_SHORT).show();
        } else {
            int errorCode = response.getError().getErrorCode();
            String errorMessage = "Sign-in error. Please try again.";

            if (errorCode == ErrorCodes.NO_NETWORK) {
                errorMessage = "No network connection.";
            }

            Toast.makeText(requireContext(), errorMessage, Toast.LENGTH_SHORT).show();
            Log.e(TAG, "Sign-in error: " + errorMessage);
        }
    }
}
