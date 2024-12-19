package com.example.finalexamwmp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class HomeActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private TextView welcomeMessage, totalCreditsTextView;
    private Button signOutButton, enrollmentMenuButton, enrollmentSummaryButton;
    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        welcomeMessage = findViewById(R.id.welcomeMessage);
        signOutButton = findViewById(R.id.signOutButton);
        enrollmentMenuButton = findViewById(R.id.enrollmentMenuButton);
        enrollmentSummaryButton = findViewById(R.id.enrollmentSummaryButton);

        // Get the current user
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            // Display welcome message with user email
            String email = currentUser.getEmail();
            welcomeMessage.setText("Welcome, " + email);

            // Fetch user data to display total credits
            String userId = currentUser.getUid();
            mDatabase.child("users").child(userId).get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    User user = task.getResult().getValue(User.class);
                    if (user != null) {
                        totalCreditsTextView.setText("Total Credits: " + user.getTotalCredits());
                    }
                }
            });

            // Handle sign out button
            signOutButton.setOnClickListener(v -> signOut());

            // Handle the enrollment menu button click to navigate to the enrollment screen
            enrollmentMenuButton.setOnClickListener(v -> {
                Intent intent = new Intent(HomeActivity.this, EnrollmentMenuActivity.class);
                startActivity(intent);
            });

            // Handle the enrollment summary button click to view enrolled subjects
            enrollmentSummaryButton.setOnClickListener(v -> {
                Intent intent = new Intent(HomeActivity.this, EnrollmentSummaryActivity.class);
                startActivity(intent);
            });

        } else {
            Toast.makeText(this, "No user logged in", Toast.LENGTH_SHORT).show();
            signOut(); // Sign out and redirect to login screen
        }
    }

    // Sign out method
    private void signOut() {
        mAuth.signOut();
        // Redirect to the login screen
        Intent intent = new Intent(HomeActivity.this, MainActivity.class);
        startActivity(intent);
        finish(); // Close the HomeActivity so the user cannot navigate back
    }
}