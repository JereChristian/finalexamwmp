package com.example.finalexamwmp;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class EnrollmentSummaryActivity extends AppCompatActivity {
    private static final String TAG = "EnrollmentSummary";
    private FirebaseFirestore db;
    private RecyclerView enrolledSubjectsRecyclerView;
    private TextView totalCreditsTextView;
    private List<Subject> enrolledSubjectsList;
    private SubjectAdapter subjectAdapter;
    private String userId;
    private int totalCredits;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enrollment_summary);

        // Initialize Firebase components
        db = FirebaseFirestore.getInstance();
        userId = FirebaseAuth.getInstance().getCurrentUser() != null ?
                FirebaseAuth.getInstance().getCurrentUser().getUid() : null;
        if (userId == null) {
            Toast.makeText(this, "Please log in first", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        // Initialize UI components
        initializeViews();

        // Fetch enrolled subjects
        fetchEnrolledSubjects();
    }

    private void initializeViews() {
        enrolledSubjectsRecyclerView = findViewById(R.id.enrolledSubjectsListView);
        totalCreditsTextView = findViewById(R.id.totalCreditsText);
        Button goBackButton = findViewById(R.id.goBackToHomeButton);

        enrolledSubjectsList = new ArrayList<>();
        subjectAdapter = new SubjectAdapter(this, enrolledSubjectsList) {
            @Override
            public void onBindViewHolder(SubjectViewHolder holder, int position) {
                Subject subject = enrolledSubjectsList.get(position);
                holder.subjectNameTextView.setText(subject.getSubjectName());
                holder.subjectCreditsTextView.setText("Credits: " + subject.getCredits());
                holder.subjectCheckBox.setVisibility(View.GONE); // Hide the checkbox
            }
        };

        // Set up RecyclerView
        enrolledSubjectsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        enrolledSubjectsRecyclerView.setAdapter(subjectAdapter);

        // Set Go Back button functionality
        goBackButton.setOnClickListener(v -> finish());

        // Initialize total credits
        totalCredits = 0;
    }

    private void fetchEnrolledSubjects() {
        db.collection("enrollments")
                .whereEqualTo("userId", userId)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    enrolledSubjectsList.clear();  // Clear the existing list to avoid duplication
                    totalCredits = 0;  // Reset total credits

                    if (!queryDocumentSnapshots.isEmpty()) {
                        for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                            String subjectId = document.getString("subjectId");
                            if (subjectId != null) {
                                // Fetch subject details by subjectId
                                fetchSubjectDetails(subjectId);
                            }
                        }
                    } else {
                        Log.d(TAG, "No enrollments found for user.");
                        Toast.makeText(this, "You have not enrolled in any subjects.", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error fetching enrollments: ", e);
                    Toast.makeText(this, "Failed to load enrollments.", Toast.LENGTH_SHORT).show();
                });
    }

    private void fetchSubjectDetails(String subjectId) {
        db.collection("subjects")
                .document(subjectId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        try {
                            Subject subject = documentSnapshot.toObject(Subject.class);
                            if (subject != null) {
                                subject.setSubjectId(documentSnapshot.getId()); // Ensure subjectId is set
                                enrolledSubjectsList.add(subject); // Add to the list
                                totalCredits += subject.getCredits(); // Add credits
                            }
                        } catch (Exception e) {
                            Log.e(TAG, "Error parsing subject details: ", e);
                        }

                        // Update the RecyclerView and total credits display
                        subjectAdapter.notifyDataSetChanged();
                        totalCreditsTextView.setText("Total Credits: " + totalCredits);
                    } else {
                        Log.d(TAG, "Subject not found for ID: " + subjectId);
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error fetching subject details for ID: " + subjectId, e);
                });
    }
}
