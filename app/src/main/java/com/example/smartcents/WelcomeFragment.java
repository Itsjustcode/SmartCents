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
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        NavController navController = Navigation.findNavController(view);

        if (user != null) {
            Log.d(TAG, "onViewCreated: User already signed in.");
            checkUserProfile(user, navController);
        }

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
        List<AuthUI.IdpConfig> providers = Arrays.asList(
                new AuthUI.IdpConfig.PhoneBuilder().build(),
                new AuthUI.IdpConfig.EmailBuilder().build(),
                new AuthUI.IdpConfig.GoogleBuilder().build()
        );

        Intent signInIntent = AuthUI.getInstance()
                .createSignInIntentBuilder()
                .setAvailableProviders(providers)
                .setLogo(R.drawable.ic_launcher_background)
                .build();
        signInLauncher.launch(signInIntent);
    }

    private void onSignInResult(FirebaseAuthUIAuthenticationResult result) {
        Log.d(TAG, "onSignInResult: Handling sign-in result.");
        IdpResponse response = result.getIdpResponse();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (result.getResultCode() == Activity.RESULT_OK && user != null) {
            boolean isNewUser = response != null && response.isNewUser();
            if (isNewUser) {
                saveUserProfileToFirestore(user);
                navigateToProfile("Please complete your profile.");
            } else {
                checkUserProfile(user, Navigation.findNavController(requireView()));
            }
        } else {
            if (response == null) {
                Log.w(TAG, "onSignInResult: User canceled sign-in.");
                showError("Sign-in canceled. Please try again.");
            } else {
                Log.e(TAG, "onSignInResult: Sign-in error. Code: " + response.getError().getErrorCode());
                handleSignInError(response.getError().getErrorCode());
            }
        }
    }

    private void saveUserProfileToFirestore(FirebaseUser user) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        Map<String, Object> userProfile = new HashMap<>();

        String displayName = user.getDisplayName() != null ? user.getDisplayName() : "Anonymous";
        String[] nameParts = displayName.split(" ", 2);
        String firstName = nameParts.length > 0 ? nameParts[0] : "";
        String lastName = nameParts.length > 1 ? nameParts[1] : "";

        userProfile.put("firstName", firstName);
        userProfile.put("lastName", lastName);
        userProfile.put("email", user.getEmail());
        userProfile.put("phone", user.getPhoneNumber());
        userProfile.put("createdAt", FieldValue.serverTimestamp());

        db.collection("users").document(user.getUid())
                .set(userProfile, SetOptions.merge())
                .addOnSuccessListener(aVoid -> Log.d(TAG, "User profile saved successfully."))
                .addOnFailureListener(e -> Log.e(TAG, "Error saving user profile.", e));
    }

    private void checkUserProfile(FirebaseUser user, NavController navController) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference docRef = db.collection("users").document(user.getUid());

        docRef.get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists() && documentSnapshot.contains("firstName") && documentSnapshot.contains("lastName")) {
                Log.d(TAG, "checkUserProfile: Profile complete. Navigating to Home.");
                navigateToHome("Welcome back, " + documentSnapshot.getString("firstName") + "!");
            } else {
                Log.d(TAG, "checkUserProfile: Profile incomplete. Navigating to Profile.");
                navigateToProfile("Please complete your profile.");
            }
        }).addOnFailureListener(e -> Log.e(TAG, "Error checking user profile.", e));
    }

    private void navigateToHome(String message) {
        Log.d(TAG, "navigateToHome: Navigating to Home with message: " + message);
        NavController navController = Navigation.findNavController(requireView());
        navController.navigate(R.id.action_welcomeFragment_to_homeFragment);
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show();
    }

    private void navigateToProfile(String message) {
        Log.d(TAG, "navigateToProfile: Navigating to Profile with message: " + message);
        NavController navController = Navigation.findNavController(requireView());
        navController.navigate(R.id.action_homeFragment_to_profileFragment);
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show();
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

    private void showError(String message) {
        Log.e(TAG, "showError: " + message);
        Toast.makeText(requireContext(), message, Toast.LENGTH_LONG).show();
    }
}
