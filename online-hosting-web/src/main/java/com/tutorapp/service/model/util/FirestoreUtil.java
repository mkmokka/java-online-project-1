package com.tutorapp.util;

import com.google.cloud.firestore.Firestore;
import com.google.firebase.cloud.FirestoreClient;

public class FirestoreUtil {

    public static Firestore mainDB() {
        return FirestoreClient.getFirestore();
    }

    public static Firestore backupDB() {
        return FirestoreClient.getFirestore(); // For demo, can configure second Firebase App
    }
}
