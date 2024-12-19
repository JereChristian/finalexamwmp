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

        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            String email = currentUser.getEmail();
            welcomeMessage.setText("Welcome, " + email);

            String userId = currentUser.getUid();
            mDatabase.child("users").child(userId).get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    User user = task.getResult().getValue(User.class);
                    if (user != null) {
                        totalCreditsTextView.setText("Total Credits: " + user.getTotalCredits());
                    }
                }
            });

            signOutButton.setOnClickListener(v -> signOut());

            enrollmentMenuButton.setOnClickListener(v -> {
                Intent intent = new Intent(HomeActivity.this, EnrollmentMenuActivity.class);
                startActivity(intent);
            });

            enrollmentSummaryButton.setOnClickListener(v -> {
                Intent intent = new Intent(HomeActivity.this, EnrollmentSummaryActivity.class);
                startActivity(intent);
            });

        } else {
            Toast.makeText(this, "No user logged in", Toast.LENGTH_SHORT).show();
            signOut();
        }
    }

    // Sign out method
    private void signOut() {
        mAuth.signOut();
        Intent intent = new Intent(HomeActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}