package com.tutorapp.service;

import com.tutorapp.model.User;
import com.tutorapp.util.FirestoreUtil;
import com.google.cloud.firestore.Firestore;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    public void saveUser(User user) throws Exception{
        Firestore main = FirestoreUtil.mainDB();
        Firestore backup = FirestoreUtil.backupDB();

        main.collection("users").document(user.uid).set(user);
        backup.collection("users_backup").document(user.uid).set(user);
    }
}
