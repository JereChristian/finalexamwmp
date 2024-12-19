package com.example.finalexamwmp;

public class Enrollment {
    private String enrollmentId;
    private String userId;
    private String subjectId;

    public Enrollment() {}

    public Enrollment(String userId, String subjectId) {
        this.userId = userId;
        this.subjectId = subjectId;
    }

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