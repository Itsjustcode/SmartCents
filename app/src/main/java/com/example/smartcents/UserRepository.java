package com.example.smartcents;

import android.net.Uri;
import android.util.Log;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.Map;
import java.util.function.Consumer;

public class UserRepository {

    // Firestore database instance for interacting with Firestore
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();

    // Firebase Storage instance for uploading and fetching files
    private final FirebaseStorage storage = FirebaseStorage.getInstance();

    // Constants for Firestore collection and document names
    private static final String USERS_COLLECTION = "users"; // Main collection for user data
    private static final String PROFILE_DOCUMENT = "default"; // Default document for profile data
    private static final String TAG = "UserRepository"; // Tag for logging

    /**
     * Save user profile to Firestore under a specific path
     *
     * userId       The unique ID of the user
     * profileData  The data to save (key-value pairs)
     * onSuccess    A callback to execute on successful save
     * onFailure    A callback to execute if saving fails
     */
    public void saveUserProfile(String userId, Map<String, Object> profileData, Runnable onSuccess, Consumer<Exception> onFailure) {
        // Add a timestamp to the profile data for tracking updates
        profileData.put("updatedAt", FieldValue.serverTimestamp());

        // Log the path where the profile will be saved
        Log.d(TAG, "Saving profile to path: users/" + userId + "/profile/default");

        // Save the data to Firestore
        db.collection(USERS_COLLECTION)
                .document(userId)
                .collection("profile")
                .document(PROFILE_DOCUMENT)
                .set(profileData, SetOptions.merge()) // Merge with existing data instead of overwriting
                .addOnSuccessListener(aVoid -> {
                    // Log and trigger success callback when data is saved
                    Log.d(TAG, "Profile saved successfully for user: " + userId);
                    onSuccess.run();
                })
                .addOnFailureListener(e -> {
                    // Log and trigger failure callback if an error occurs
                    Log.e(TAG, "Error saving profile for user: " + userId, e);
                    onFailure.accept(e);
                });
    }

    /**
     * Retrieve user profile data from Firestore
     *
     * userId    The unique ID of the user
     * onSuccess A callback to execute on successful retrieval
     * onFailure A callback to execute if retrieval fails
     */
    public void getUserProfile(String userId, Consumer<DocumentSnapshot> onSuccess, Consumer<Exception> onFailure) {
        // Log the path where the profile will be fetched
        Log.d(TAG, "Fetching profile from path: users/" + userId + "/profile/default");

        // Fetch the profile data from Firestore
        db.collection(USERS_COLLECTION)
                .document(userId)
                .collection("profile")
                .document(PROFILE_DOCUMENT)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    // Log and trigger success callback when data is fetched
                    Log.d(TAG, "Profile fetched successfully for user: " + userId);
                    onSuccess.accept(documentSnapshot);
                })
                .addOnFailureListener(e -> {
                    // Log and trigger failure callback if an error occurs
                    Log.e(TAG, "Error fetching profile for user: " + userId, e);
                    onFailure.accept(e);
                });
    }

    /**
     * Upload a profile image to Firebase Storage
     *
     * userId    The unique ID of the user
     * imageUri  The URI of the image to upload
     * onSuccess A callback to execute with the image URL on successful upload
     * onFailure A callback to execute if upload fails
     */
    public void uploadProfileImage(String userId, Uri imageUri, Consumer<String> onSuccess, Consumer<Exception> onFailure) {
        // Log the start of the image upload process
        Log.d(TAG, "Starting profile image upload for user: " + userId);

        // Check if the image URI is null
        if (imageUri == null) {
            Log.e(TAG, "Image URI is null. Cannot upload.");
            onFailure.accept(new IllegalArgumentException("Image URI is null"));
            return; // Stop further execution if image URI is missing
        }

        // Log the image URI for debugging purposes
        Log.d(TAG, "Image URI: " + imageUri.toString());

        // Reference to the storage location in Firebase Storage
        StorageReference imageRef = storage.getReference().child("profile_images/" + userId + ".jpg");

        // Upload the image to Firebase Storage
        imageRef.putFile(imageUri)
                .addOnSuccessListener(taskSnapshot -> {
                    // Log success and fetch the download URL of the uploaded image
                    Log.d(TAG, "File uploaded successfully for user: " + userId);
                    imageRef.getDownloadUrl()
                            .addOnSuccessListener(uri -> {
                                // Log and trigger success callback with the image URL
                                Log.d(TAG, "Download URL fetched successfully: " + uri.toString());
                                onSuccess.accept(uri.toString());
                            })
                            .addOnFailureListener(e -> {
                                // Log and trigger failure callback if fetching download URL fails
                                Log.e(TAG, "Failed to fetch download URL for user: " + userId, e);
                                onFailure.accept(e);
                            });
                })
                .addOnFailureListener(e -> {
                    // Log and trigger failure callback if upload fails
                    Log.e(TAG, "Error uploading profile image for user: " + userId, e);
                    onFailure.accept(e);
                });
    }
}
