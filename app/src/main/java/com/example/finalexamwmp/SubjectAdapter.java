package com.example.finalexamwmp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class SubjectAdapter extends RecyclerView.Adapter<SubjectAdapter.SubjectViewHolder> {
    private Context context;
    private List<Subject> subjectList;

    public SubjectAdapter(Context context, List<Subject> subjectList) {
        this.context = context;
        this.subjectList = subjectList;
    }

    @Override
    public SubjectViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.subject_item, parent, false);
        return new SubjectViewHolder(view);
    }

    @Override
    public void onBindViewHolder(SubjectViewHolder holder, int position) {
        Subject subject = subjectList.get(position);
        holder.subjectNameTextView.setText(subject.getSubjectName());
        holder.subjectCreditsTextView.setText("Credits: " + subject.getCredits());
        holder.subjectCheckBox.setChecked(true); // Assuming all subjects are already enrolled

        holder.subjectCheckBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            subject.setSelected(isChecked);
        });
    }

    @Override
    public int getItemCount() {
        return subjectList.size();
    }

    public static class SubjectViewHolder extends RecyclerView.ViewHolder {
        public TextView subjectNameTextView, subjectCreditsTextView;
        public CheckBox subjectCheckBox;

        public SubjectViewHolder(View view) {
            super(view);
            subjectNameTextView = view.findViewById(R.id.subjectNameTextView);
            subjectCreditsTextView = view.findViewById(R.id.subjectCreditsTextView);
            subjectCheckBox = view.findViewById(R.id.subjectCheckBox);
        }
    }
}