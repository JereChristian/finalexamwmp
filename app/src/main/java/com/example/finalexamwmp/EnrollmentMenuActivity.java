package com.example.finalexamwmp;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class EnrollmentMenuActivity extends AppCompatActivity {
    private static final String TAG = "EnrollmentMenu";
    private FirebaseFirestore db;
    private RecyclerView subjectsRecyclerView;
    private Button enrollButton;
    private List<Subject> subjectList;
    private SubjectAdapter subjectAdapter;
    private String userId;
    private int totalCredits = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enrollment_menu);

        try {
            db = FirebaseFirestore.getInstance();

            userId = FirebaseAuth.getInstance().getCurrentUser() != null ?
                    FirebaseAuth.getInstance().getCurrentUser().getUid() : null;
            if (userId == null) {
                Toast.makeText(this, "Please log in first", Toast.LENGTH_LONG).show();
                finish();
                return;
            }

            initializeViews();

            fetchCurrentEnrollmentCredits();

            fetchSubjects();

        } catch (Exception e) {
            Log.e(TAG, "Error in onCreate: ", e);
            Toast.makeText(this, "Failed to initialize enrollment menu", Toast.LENGTH_LONG).show();
            finish();
        }
    }

    private void initializeViews() {
        subjectsRecyclerView = findViewById(R.id.subjectsListView);
        if (subjectsRecyclerView == null) {
            throw new IllegalStateException("subjectsListView not found in layout");
        }

        subjectsRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        subjectList = new ArrayList<>();
        subjectAdapter = new SubjectAdapter(this, subjectList);
        subjectsRecyclerView.setAdapter(subjectAdapter);

        enrollButton = findViewById(R.id.enrollButton);
        if (enrollButton == null) {
            throw new IllegalStateException("enrollButton not found in layout");
        }
        enrollButton.setOnClickListener(v -> enrollSubjects());

        Button goBackButton = findViewById(R.id.goBackButton);
        if (goBackButton == null) {
            throw new IllegalStateException("goBackButton not found in layout");
        }
        goBackButton.setOnClickListener(v -> finish());
    }

    private void fetchCurrentEnrollmentCredits() {
        db.collection("enrollments")
                .whereEqualTo("userId", userId)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        String subjectId = document.getString("subjectId");
                        // Fetch subject details to get credits
                        if (subjectId != null) {
                            db.collection("subjects")
                                    .document(subjectId)
                                    .get()
                                    .addOnSuccessListener(subjectDoc -> {
                                        if (subjectDoc.exists()) {
                                            Long credits = subjectDoc.getLong("credits");
                                            if (credits != null) {
                                                totalCredits += credits;
                                            }
                                        }
                                    })
                                    .addOnFailureListener(e -> Log.e(TAG, "Error fetching subject credits: ", e));
                        }
                    }
                })
                .addOnFailureListener(e -> Log.e(TAG, "Error fetching current enrollments: ", e));
    }

    private void fetchSubjects() {
        db.collection("subjects")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    subjectList.clear();
                    if (!queryDocumentSnapshots.isEmpty()) {
                        for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                            try {
                                Subject subject = document.toObject(Subject.class);
                                subject.setSubjectId(document.getId()); // Ensure subject ID is set
                                subjectList.add(subject);
                            } catch (Exception e) {
                                Log.e(TAG, "Error parsing subject: ", e);
                            }
                        }
                    } else {
                        Log.d(TAG, "No subjects found in the database");
                    }
                    subjectAdapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error fetching subjects: ", e);
                    Toast.makeText(this, "Failed to load subjects", Toast.LENGTH_SHORT).show();
                });
    }

    private void enrollSubjects() {
        List<Subject> selectedSubjects = new ArrayList<>();
        int additionalCredits = 0;

        for (Subject subject : subjectList) {
            if (subject.isSelected()) {
                additionalCredits += subject.getCredits();
                selectedSubjects.add(subject);
            }
        }

        Log.d(TAG, "Selected subjects count: " + selectedSubjects.size());

        if (totalCredits + additionalCredits > 24) {
            Toast.makeText(this, "Cannot exceed 24 credits. Current credits: " + totalCredits,
                    Toast.LENGTH_LONG).show();
            return;
        }

        if (selectedSubjects.isEmpty()) {
            Toast.makeText(this, "No subjects selected for enrollment.", Toast.LENGTH_SHORT).show();
            return;
        }

        for (Subject subject : selectedSubjects) {
            enrollInSubject(subject);
        }
    }

    private void enrollInSubject(Subject subject) {
        Enrollment enrollment = new Enrollment(userId, subject.getSubjectId());

        Log.d(TAG, "Enrolling in subject: " + subject.getSubjectName() + " (ID: " + subject.getSubjectId() + ")");

        db.collection("enrollments")
                .whereEqualTo("userId", userId)
                .whereEqualTo("subjectId", subject.getSubjectId())
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (queryDocumentSnapshots.isEmpty()) {
                        // Not enrolled yet, proceed with enrollment
                        db.collection("enrollments")
                                .add(enrollment)
                                .addOnSuccessListener(documentReference -> {
                                    totalCredits += subject.getCredits();
                                    Toast.makeText(this, "Enrolled in " + subject.getSubjectName(),
                                            Toast.LENGTH_SHORT).show();
                                })
                                .addOnFailureListener(e -> {
                                    Log.e(TAG, "Error enrolling in subject: ", e);
                                    Toast.makeText(this, "Failed to enroll in " +
                                            subject.getSubjectName(), Toast.LENGTH_SHORT).show();
                                });
                    } else {
                        Toast.makeText(this, "Already enrolled in " + subject.getSubjectName(),
                                Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> Log.e(TAG, "Error checking existing enrollment: ", e));
    }
}