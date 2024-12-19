package com.example.finalexamwmp;

public class Subject {
    private String subjectId;
    private String subjectName;
    private int credits;
    private boolean selected;

    public Subject() {}

    public Subject(String subjectId, String subjectName, int credits) {
        this.subjectId = subjectId;
        this.subjectName = subjectName;
        this.credits = credits;
        this.selected = false;
    }

    public String getSubjectId() {
        return subjectId;
    }

    public void setSubjectId(String subjectId) {
        this.subjectId = subjectId;
    }

    public String getSubjectName() {
        return subjectName;
    }

    public void setSubjectName(String subjectName) {
        this.subjectName = subjectName;
    }

    public int getCredits() {
        return credits;
    }

    public void setCredits(int credits) {
        this.credits = credits;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }
}