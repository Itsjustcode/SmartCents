package com.example.smartcents;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FieldValue;

import java.util.HashMap;
import java.util.Map;

public class EditViewProfileFragment extends Fragment {

    private static final String TAG = "EditViewProfileFragment"; //tag for logging
    private final UserRepository userRepository = new UserRepository(); //access UserRepository class for Firestore operations
    private ImageView profileImageView; // display the user's profile image
    private Uri selectedImageUri; // To hold the URI of the selected profile image

    //launcher to handle the result when the user picks an image
    private final ActivityResultLauncher<Intent> imagePickerLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                    result -> {
                        // Check if the image selection was successful
                        if (result.getResultCode() == getActivity().RESULT_OK && result.getData() != null) {
                            selectedImageUri = result.getData().getData(); // Get the URI of the selected image
                            profileImageView.setImageURI(selectedImageUri); // Display the image in the ImageView
                        }
                    });

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_edit_view_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Initialize the profile image view
        profileImageView = view.findViewById(R.id.profile_image);

        // Get the currently logged-in user
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user != null) {
            // Load the user's profile data
            loadUserProfile(view, user.getUid());
            // Set up the save button functionality
            setupSaveButton(view, user.getUid());
        } else {
            // If no user is logged in, show an error message "toast"
            Log.e(TAG, "User is not authenticated.");
            Toast.makeText(requireContext(), "Error: User is not logged in.", Toast.LENGTH_SHORT).show();
        }

        // Set up the button to open the image picker
        Button selectImageButton = view.findViewById(R.id.select_image_button);
        selectImageButton.setOnClickListener(v -> openImagePicker());

        // Set up the cancel button to navigate back to the home screen
        Button cancelButton = view.findViewById(R.id.cancel_button);
        cancelButton.setOnClickListener(v -> navigateToHome(view));
    }

    // Opens the image picker for the user to select a profile picture
    private void openImagePicker() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("image/*"); // Restrict to image files
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, false); // Only allow a single selection
        imagePickerLauncher.launch(intent); // Launch the picker
    }

    // Loads the user's profile data from Firestore
    private void loadUserProfile(View view, String userId) {
        userRepository.getUserProfile(userId,
                documentSnapshot -> {
                    // Populate the input fields with the retrieved data
                    populateFields(view, documentSnapshot);

                    // Load the profile picture if a URL is available
                    String profileImageUrl = documentSnapshot.getString("profilePictureUrl");
                    if (profileImageUrl != null) {
                        Glide.with(this).load(profileImageUrl).into(profileImageView);
                    }
                },
                e -> Log.e(TAG, "Error loading profile", e) // Log any errors during data retrieval
        );
    }

    // Fills the input fields with data from Firestore
    private void populateFields(View view, DocumentSnapshot document) {
        try {
            // Get references to input fields
            EditText firstNameInput = view.findViewById(R.id.input_first_name);
            EditText lastNameInput = view.findViewById(R.id.input_last_name);
            EditText emailInput = view.findViewById(R.id.input_email);
            EditText phoneInput = view.findViewById(R.id.input_phone);

            // Set input field values from Firestore data
            firstNameInput.setText(document.getString("firstName"));
            lastNameInput.setText(document.getString("lastName"));
            emailInput.setText(document.getString("email"));
            phoneInput.setText(document.getString("phoneNumber"));
        } catch (NullPointerException e) {
            // Handle cases where fields are null
            Log.e(TAG, "Error populating fields", e);
            Toast.makeText(requireContext(), "Error displaying profile. Please try again.", Toast.LENGTH_SHORT).show();
        }
    }

    // Sets up the functionality of the save button
    private void setupSaveButton(View view, String userId) {
        Button saveButton = view.findViewById(R.id.btn_save_profile);

        saveButton.setOnClickListener(v -> {
            // Get data from input fields
            EditText firstNameInput = view.findViewById(R.id.input_first_name);
            EditText lastNameInput = view.findViewById(R.id.input_last_name);
            EditText emailInput = view.findViewById(R.id.input_email);
            EditText phoneInput = view.findViewById(R.id.input_phone);

            String firstName = firstNameInput.getText().toString().trim();
            String lastName = lastNameInput.getText().toString().trim();
            String email = emailInput.getText().toString().trim();
            String phone = phoneInput.getText().toString().trim();

            // Check if all fields are filled
            if (firstName.isEmpty() || lastName.isEmpty() || email.isEmpty() || phone.isEmpty()) {
                Toast.makeText(requireContext(), "All fields are required", Toast.LENGTH_SHORT).show();
                return;
            }

            // Prepare the data to be saved
            Map<String, Object> profileData = new HashMap<>();
            profileData.put("firstName", firstName);
            profileData.put("lastName", lastName);
            profileData.put("email", email);
            profileData.put("phoneNumber", phone);

            // If a profile image is selected, upload it to Firebase Storage
            if (selectedImageUri != null) {
                userRepository.uploadProfileImage(userId, selectedImageUri,
                        imageUrl -> {
                            profileData.put("profilePictureUrl", imageUrl); // Add image URL to profile data
                            saveProfile(userId, profileData, view); // Save profile with image URL
                        },
                        e -> {
                            Log.e(TAG, "Error uploading profile image", e);
                            Toast.makeText(requireContext(), "Error saving profile image", Toast.LENGTH_SHORT).show();
                        });
            } else {
                saveProfile(userId, profileData, view); // Save profile without image
            }
        });
    }

    // Saves the profile data to Firestore
    private void saveProfile(String userId, Map<String, Object> profileData, View view) {
        userRepository.saveUserProfile(userId, profileData,
                () -> {
                    // Show success message and navigate to home screen
                    Toast.makeText(requireContext(), "Profile updated successfully!", Toast.LENGTH_SHORT).show();
                    navigateToHome(view);
                },
                e -> {
                    // Handle errors during saving
                    Log.e(TAG, "Error saving profile", e);
                    Toast.makeText(requireContext(), "Error saving profile", Toast.LENGTH_SHORT).show();
                });
    }

    // Navigates back to the home screen
    private void navigateToHome(View view) {
        NavController navController = Navigation.findNavController(view);
        navController.navigate(R.id.action_editViewProfileFragment_to_homeFragment);
    }
}
