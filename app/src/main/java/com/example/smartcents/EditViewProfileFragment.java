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

    private static final String TAG = "EditViewProfileFragment";
    private final UserRepository userRepository = new UserRepository();
    private ImageView profileImageView;
    private Uri selectedImageUri;

    private final ActivityResultLauncher<Intent> imagePickerLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                    result -> {
                        if (result.getResultCode() == getActivity().RESULT_OK && result.getData() != null) {
                            selectedImageUri = result.getData().getData();
                            profileImageView.setImageURI(selectedImageUri);
                        }
                    });

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_edit_view_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        profileImageView = view.findViewById(R.id.profile_image);
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user != null) {
            loadUserProfile(view, user.getUid());
            setupSaveButton(view, user.getUid());
        } else {
            Log.e(TAG, "User is not authenticated.");
            Toast.makeText(requireContext(), "Error: User is not logged in.", Toast.LENGTH_SHORT).show();
        }

        Button selectImageButton = view.findViewById(R.id.select_image_button);
        selectImageButton.setOnClickListener(v -> openImagePicker());

        Button cancelButton = view.findViewById(R.id.cancel_button);
        cancelButton.setOnClickListener(v -> navigateToHome(view));
    }

    private void openImagePicker() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("image/*"); // Allows selection of all image types
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, false); // Disable multiple selections
        imagePickerLauncher.launch(intent);
    }

    private void loadUserProfile(View view, String userId) {
        userRepository.getUserProfile(userId,
                documentSnapshot -> {
                    populateFields(view, documentSnapshot);
                    String profileImageUrl = documentSnapshot.getString("profilePictureUrl");
                    if (profileImageUrl != null) {
                        Glide.with(this).load(profileImageUrl).into(profileImageView);
                    }
                },
                e -> Log.e(TAG, "Error loading profile", e)
        );
    }

    private void populateFields(View view, DocumentSnapshot document) {
        try {
            EditText firstNameInput = view.findViewById(R.id.input_first_name);
            EditText lastNameInput = view.findViewById(R.id.input_last_name);
            EditText emailInput = view.findViewById(R.id.input_email);
            EditText phoneInput = view.findViewById(R.id.input_phone);

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

            if (selectedImageUri != null) {
                userRepository.uploadProfileImage(userId, selectedImageUri,
                        imageUrl -> {
                            profileData.put("profilePictureUrl", imageUrl);
                            saveProfile(userId, profileData, view);
                        },
                        e -> {
                            Log.e(TAG, "Error uploading profile image", e);
                            Toast.makeText(requireContext(), "Error saving profile image", Toast.LENGTH_SHORT).show();
                        });
            } else {
                saveProfile(userId, profileData, view);
            }
        });
    }

    private void saveProfile(String userId, Map<String, Object> profileData, View view) {
        userRepository.saveUserProfile(userId, profileData,
                () -> {
                    Toast.makeText(requireContext(), "Profile updated successfully!", Toast.LENGTH_SHORT).show();
                    navigateToHome(view);
                },
                e -> {
                    Log.e(TAG, "Error saving profile", e);
                    Toast.makeText(requireContext(), "Error saving profile", Toast.LENGTH_SHORT).show();
                });
    }

    private void navigateToHome(View view) {
        NavController navController = Navigation.findNavController(view);
        navController.navigate(R.id.action_editViewProfileFragment_to_homeFragment);
    }
}
