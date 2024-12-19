package com.example.finalexamwmp;

// Enrollment.java
public class Enrollment {
    private String enrollmentId;
    private String userId;
    private String subjectId;

    // Default constructor (required for Firebase)
    public Enrollment() {}

    // Constructor with parameters
    public Enrollment(String userId, String subjectId) {
        this.userId = userId;
        this.subjectId = subjectId;
    }

    // Getters and Setters
    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getSubjectId() {
        return subjectId;
    }

    public void setSubjectId(String subjectId) {
        this.subjectId = subjectId;
    }

    public String getEnrollmentId() {
        return enrollmentId;
    }

    public void setEnrollmentId(String enrollmentId) {
        this.enrollmentId = enrollmentId;
    }
}