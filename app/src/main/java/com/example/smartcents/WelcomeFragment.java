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

    // FirebaseUI Launcher for handling the sign-in flow
    private final ActivityResultLauncher<Intent> signInLauncher =
            registerForActivityResult(
                    new FirebaseAuthUIActivityResultContract(),
                    this::onSignInResult
            );

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment (fragment_welcome.xml)
        return inflater.inflate(R.layout.fragment_welcome, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Initialize the Log In / Register button and set its click listener
        MaterialButton loginRegisterButton = view.findViewById(R.id.loginregisterButton);
        loginRegisterButton.setOnClickListener(v -> startSignInFlow());
    }

    /**
     * Starts the FirebaseUI sign-in flow.
     */
    private void startSignInFlow() {
        // Configure the login options (email, phone, Google)
        List<AuthUI.IdpConfig> providers = Arrays.asList(
                new AuthUI.IdpConfig.EmailBuilder().build(),
                new AuthUI.IdpConfig.PhoneBuilder().build(),
                new AuthUI.IdpConfig.GoogleBuilder().build()
        );

        // Create a sign-in intent and launch the FirebaseUI flow
        Intent signInIntent = AuthUI.getInstance()
                .createSignInIntentBuilder()
                .setAvailableProviders(providers)
                .setLogo(R.drawable.ic_launcher_background) // Replace with app logo later
                .build();
        signInLauncher.launch(signInIntent);
    }

    /**
     * Handles the result of the FirebaseUI sign-in flow.
     *
     * result The result of the sign-in process.
     */
    private void onSignInResult(FirebaseAuthUIAuthenticationResult result) {
        IdpResponse response = result.getIdpResponse();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (result.getResultCode() == Activity.RESULT_OK && user != null) {
            // Check if the user is new or returning
            if (response != null && response.isNewUser()) {
                // New user - Save their profile and navigate to the profile page
                saveUserProfile(user);
                navigateToProfile("Please complete your profile.");
            } else {
                // Existing user - Check if their profile is complete
                checkUserProfile(user);
            }
        } else {
            // Handle errors or user cancelation
            handleSignInError(response);
        }
    }

    /**
     * Saves a new user's profile to Firestore.
     *
     * user The FirebaseUser object representing the newly signed-in user.
     */
    private void saveUserProfile(FirebaseUser user) {
        // Create a basic profile with placeholders for the new user
        Map<String, Object> userProfile = new HashMap<>();
        userProfile.put("firstName", ""); // Placeholder for the first name
        userProfile.put("lastName", "");  // Placeholder for the last name
        userProfile.put("email", user.getEmail());
        userProfile.put("phone", user.getPhoneNumber());
        userProfile.put("createdAt", FieldValue.serverTimestamp()); // Track the account creation time

        // Log the user ID and save the profile data to Firestore
        Log.d(TAG, "Saving new user profile for UID: " + user.getUid());
        userRepository.saveUserProfile(user.getUid(), userProfile,
                () -> Log.d(TAG, "User profile saved successfully for new user."),
                e -> Log.e(TAG, "Error saving user profile for new user.", e)
        );
    }

    /**
     * Checks whether the existing user's profile is complete in Firestore.
     *
     * user The FirebaseUser object representing the logged-in user.
     */
    private void checkUserProfile(FirebaseUser user) {
        userRepository.getUserProfile(user.getUid(),
                documentSnapshot -> {
                    // If the profile exists and contains required fields, navigate to home
                    if (documentSnapshot.exists() && documentSnapshot.contains("firstName") && documentSnapshot.contains("lastName")) {
                        navigateToHome("Welcome back!");
                    } else {
                        // Incomplete profile - Navigate to the profile page
                        navigateToProfile("Please complete your profile.");
                    }
                },
                e -> Log.e(TAG, "Error checking user profile", e)
        );
    }

    /**
     * Navigates to the home screen and displays a welcome message.
     *
     * message The message to display in a toast.
     */
    private void navigateToHome(String message) {
        NavController navController = Navigation.findNavController(requireView());
        navController.navigate(R.id.action_welcomeFragment_to_homeFragment);
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show();
    }

    /**
     * Navigates to the profile setup screen and displays a message.
     *
     * @param message The message to display in a toast.
     */
    private void navigateToProfile(String message) {
        NavController navController = Navigation.findNavController(requireView());
        navController.navigate(R.id.action_welcomeFragment_to_profileFragment);
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show();
    }

    /**
     * Handles sign-in errors or user cancelation.
     *
     * response The response from the FirebaseUI sign-in process.
     */
    private void handleSignInError(IdpResponse response) {
        if (response == null) {
            // User canceled the sign-in process
            Toast.makeText(requireContext(), "Sign-in canceled. Please try again.", Toast.LENGTH_SHORT).show();
        } else {
            // Determine the error type and display an appropriate message
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
