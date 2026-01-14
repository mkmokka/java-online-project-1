package com.tutorapp.controller;

import com.tutorapp.model.User;
import com.tutorapp.service.UserService;
import com.tutorapp.util.FirestoreUtil;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseToken;
import com.google.cloud.firestore.Firestore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class AuthController {

    @Autowired
    private UserService userService;

    // ================== LOGIN / REGISTRATION ==================
    @PostMapping("/login")
    public String login(@RequestParam String idToken, Model model) {
        try {
            // Verify Firebase ID token
            FirebaseToken decodedToken = FirebaseAuth.getInstance().verifyIdToken(idToken);
            String uid = decodedToken.getUid();
            String email = decodedToken.getEmail();

            Firestore db = FirestoreUtil.mainDB();

            // Check if user already exists
            User user = db.collection("users")
                          .document(uid)
                          .get()
                          .get()
                          .toObject(User.class);

            if (user == null) {
                user = new User();
                user.setUid(uid);
                user.setEmail(email);

                // Role assignment
                user.setRole(email.equals("admin@yourapp.com") ? "ADMIN" : "STUDENT");

                // Registration status
                user.setStatus(user.getRole().equals("ADMIN") ? "ACTIVE" : "PENDING");

                // Save user to main + backup DB
                userService.saveUser(user);
            }

            // Handle login status
            if ("DENIED".equals(user.getStatus())) {
                model.addAttribute("error", "Your registration is denied by admin.");
                return "login";
            } else if ("PENDING".equals(user.getStatus())) {
                model.addAttribute("error", "Your registration is pending admin approval.");
                return "login";
            }

            model.addAttribute("user", user);
            return "dashboard";

        } catch (Exception e) {
            model.addAttribute("error", "Login Failed: " + e.getMessage());
            return "login";
        }
    }
}
