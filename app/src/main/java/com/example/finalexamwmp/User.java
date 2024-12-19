package com.example.finalexamwmp;

public class User {
    private String userId;
    private String email;
    private int totalCredits;

    public User() {}

    public User(String userId, String email, int totalCredits) {
        this.userId = userId;
        this.email = email;
        this.totalCredits = totalCredits;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public int getTotalCredits() {
        return totalCredits;
    }

    public void setTotalCredits(int totalCredits) {
        this.totalCredits = totalCredits;
    }
}