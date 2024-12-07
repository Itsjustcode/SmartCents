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

    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private final FirebaseStorage storage = FirebaseStorage.getInstance(); // Added FirebaseStorage instance
    private static final String USERS_COLLECTION = "users";
    private static final String PROFILE_DOCUMENT = "default";
    private static final String TAG = "UserRepository";

    // Save user profile to Firestore under the correct path
    public void saveUserProfile(String userId, Map<String, Object> profileData, Runnable onSuccess, Consumer<Exception> onFailure) {
        // Add timestamp to the profile data
        profileData.put("updatedAt", FieldValue.serverTimestamp());
        Log.d(TAG, "Saving profile to path: users/" + userId + "/profile/default");

        db.collection(USERS_COLLECTION)
                .document(userId)
                .collection("profile")
                .document(PROFILE_DOCUMENT)
                .set(profileData, SetOptions.merge())
                .addOnSuccessListener(aVoid -> {
                    Log.d(TAG, "Profile saved successfully for user: " + userId);
                    onSuccess.run();
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error saving profile for user: " + userId, e);
                    onFailure.accept(e);
                });
    }

    // Retrieve user profile from Firestore
    public void getUserProfile(String userId, Consumer<DocumentSnapshot> onSuccess, Consumer<Exception> onFailure) {
        Log.d(TAG, "Fetching profile from path: users/" + userId + "/profile/default");

        db.collection(USERS_COLLECTION)
                .document(userId)
                .collection("profile")
                .document(PROFILE_DOCUMENT)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    Log.d(TAG, "Profile fetched successfully for user: " + userId);
                    onSuccess.accept(documentSnapshot);
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error fetching profile for user: " + userId, e);
                    onFailure.accept(e);
                });
    }

    // Upload profile image to Firebase Storage
    public void uploadProfileImage(String userId, Uri imageUri, Consumer<String> onSuccess, Consumer<Exception> onFailure) {
        Log.d(TAG, "Starting profile image upload for user: " + userId);

        if (imageUri == null) {
            Log.e(TAG, "Image URI is null. Cannot upload.");
            onFailure.accept(new IllegalArgumentException("Image URI is null"));
            return;
        }

        Log.d(TAG, "Image URI: " + imageUri.toString());

        StorageReference imageRef = storage.getReference().child("profile_images/" + userId + ".jpg");

        imageRef.putFile(imageUri)
                .addOnSuccessListener(taskSnapshot -> {
                    Log.d(TAG, "File uploaded successfully for user: " + userId);
                    imageRef.getDownloadUrl()
                            .addOnSuccessListener(uri -> {
                                Log.d(TAG, "Download URL fetched successfully: " + uri.toString());
                                onSuccess.accept(uri.toString());
                            })
                            .addOnFailureListener(e -> {
                                Log.e(TAG, "Failed to fetch download URL for user: " + userId, e);
                                onFailure.accept(e);
                            });
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error uploading profile image for user: " + userId, e);
                    onFailure.accept(e);
                });
    }

}
