package com.example.smartcents;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.ErrorCodes;
import com.firebase.ui.auth.FirebaseAuthUIActivityResultContract;
import com.firebase.ui.auth.IdpResponse;
import com.firebase.ui.auth.data.model.FirebaseAuthUIAuthenticationResult;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Arrays;
import java.util.List;

public class WelcomeFragment extends Fragment {

    private static final String TAG = "WelcomeFragment";

    private final ActivityResultLauncher<Intent> signInLauncher =
            registerForActivityResult(
                    new FirebaseAuthUIActivityResultContract(),
                    this::onSignInResult
            );

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView: Inflating layout.");
        return inflater.inflate(R.layout.fragment_welcome, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.d(TAG, "onViewCreated: Setting up views.");

        // Check if the user is already authenticated
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        //if (user != null) {
            //Log.d(TAG, "onViewCreated: User already signed in. Navigating to Home.");
            //navigateToHome("Welcome back, " + (user.getDisplayName() != null ? user.getDisplayName() : "User") + "!");
            //return;
        //}

        // Set up Login/Register button
        MaterialButton loginRegisterButton = view.findViewById(R.id.loginregisterButton);
        if (loginRegisterButton != null) {
            loginRegisterButton.setOnClickListener(v -> {
                Log.d(TAG, "onViewCreated: Login/Register button clicked.");
                startSignInFlow();
            });
        } else {
            Log.e(TAG, "onViewCreated: loginregisterButton not found in layout.");
        }
    }

    private void startSignInFlow() {
        Log.d(TAG, "startSignInFlow: Starting sign-in flow.");
        // Authentication providers
        List<AuthUI.IdpConfig> providers = Arrays.asList(
                new AuthUI.IdpConfig.PhoneBuilder().build(),
                new AuthUI.IdpConfig.EmailBuilder().build(),
                new AuthUI.IdpConfig.GoogleBuilder().build()
        );

        // Create and launch sign-in intent
        Intent signInIntent = AuthUI.getInstance()
                .createSignInIntentBuilder()
                .setAvailableProviders(providers)
                .setLogo(R.drawable.ic_launcher_background) // App logo
                .build();
        signInLauncher.launch(signInIntent);
    }

    private void onSignInResult(FirebaseAuthUIAuthenticationResult result) {
        Log.d(TAG, "onSignInResult: Handling sign-in result.");
        IdpResponse response = result.getIdpResponse();
        if (result.getResultCode() == Activity.RESULT_OK) {
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            if (user != null) {
                Log.d(TAG, "onSignInResult: User signed in successfully.");
                boolean isNewUser = response != null && response.isNewUser();
                if (isNewUser) {
                    sendVerificationCode(user);
                } else {
                    navigateToHome("Welcome back, " + (user.getDisplayName() != null ? user.getDisplayName() : "User") + "!");
                }
            } else {
                Log.e(TAG, "onSignInResult: User is null after successful sign-in.");
            }
        } else {
            if (response == null) {
                // User canceled the sign-in flow
                Log.w(TAG, "onSignInResult: User canceled sign-in.");
                showError("Sign-in canceled. Please try again.");
            } else {
                // Handle sign-in error
                Log.e(TAG, "onSignInResult: Sign-in error. Code: " + response.getError().getErrorCode());
                handleSignInError(response.getError().getErrorCode());
            }
        }
    }

    private void sendVerificationCode(FirebaseUser user) {
        if (user.getPhoneNumber() != null) {
            Log.d(TAG, "sendVerificationCode: Sending verification code to phone.");
            navigateToHome("Phone verification code sent to " + user.getPhoneNumber() + "!");
        } else if (!user.isEmailVerified()) {
            Log.d(TAG, "sendVerificationCode: Sending verification email.");
            user.sendEmailVerification()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "sendVerificationCode: Verification email sent.");
                            navigateToHome("Verification email sent to " + user.getEmail() + "!");
                        } else {
                            Log.e(TAG, "sendVerificationCode: Failed to send verification email.");
                            showError("Failed to send verification email. Please try again.");
                        }
                    });
        }
    }

    private void handleSignInError(int errorCode) {
        String errorMessage;
        switch (errorCode) {
            case ErrorCodes.NO_NETWORK:
                errorMessage = "No network connection. Please check your internet and try again.";
                break;
            case ErrorCodes.UNKNOWN_ERROR:
            default:
                errorMessage = "An unknown error occurred. Please try again.";
                break;
        }
        Log.e(TAG, "handleSignInError: " + errorMessage);
        showError(errorMessage);
    }

    private void navigateToHome(String message) {
        Log.d(TAG, "navigateToHome: Navigating to Home with message: " + message);
        NavController navController = Navigation.findNavController(requireView());
        navController.navigate(R.id.action_welcomeFragment_to_homeFragment);
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show();
    }

    private void showError(String message) {
        Log.e(TAG, "showError: " + message);
        Toast.makeText(requireContext(), message, Toast.LENGTH_LONG).show();
    }
}
