package com.example.smartcents;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import android.util.Log;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

public class UserRepository {

    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
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
}
