package com.example.smartcents;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.DocumentSnapshot;
import java.util.function.Consumer;

public class UserRepository {

    private final FirebaseFirestore db = FirebaseFirestore.getInstance();

    public void getUserProfile(String userId, Consumer<DocumentSnapshot> onSuccess, Consumer<Exception> onFailure) {
        db.collection("users").document(userId)
                .get()
                .addOnSuccessListener(onSuccess::accept)
                .addOnFailureListener(onFailure::accept);
    }
}
