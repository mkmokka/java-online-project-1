package com.tutorapp.controller;

import com.tutorapp.model.User;
import com.tutorapp.util.FirestoreUtil;
import com.google.cloud.firestore.Firestore;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.concurrent.ExecutionException;

@Controller
public class AdminController {

    // ================== ADMIN PANEL ==================
    @GetMapping("/admin")
    public String adminPanel(Model model) {
        try {
            Firestore db = FirestoreUtil.mainDB();

            // Fetch pending users
            List<User> pendingUsers = db.collection("users")
                                        .whereEqualTo("status", "PENDING")
                                        .get()
                                        .get()
                                        .toObjects(User.class);

            model.addAttribute("pendingUsers", pendingUsers);
            return "admin";

        } catch (InterruptedException | ExecutionException e) {
            model.addAttribute("error", "Failed to load pending users: " + e.getMessage());
            return "admin";
        }
    }

    @PostMapping("/admin/approve")
    @ResponseBody
    public String approveUser(@RequestParam String uid) {
        try {
            FirestoreUtil.mainDB()
                    .collection("users")
                    .document(uid)
                    .update("status", "ACTIVE");

            FirestoreUtil.backupDB()
                    .collection("users_backup")
                    .document(uid)
                    .update("status", "ACTIVE");

            return "User Approved!";

        } catch (Exception e) {
            return "Error approving user: " + e.getMessage();
        }
    }

    @PostMapping("/admin/deny")
    @ResponseBody
    public String denyUser(@RequestParam String uid) {
        try {
            FirestoreUtil.mainDB()
                    .collection("users")
                    .document(uid)
                    .update("status", "DENIED");

            FirestoreUtil.backupDB()
                    .collection("users_backup")
                    .document(uid)
                    .update("status", "DENIED");

            return "User Denied!";

        } catch (Exception e) {
            return "Error denying user: " + e.getMessage();
        }
    }
}
