package com.moutamid.sprachelernenadmin.models;

import java.util.ArrayList;

public class TrialContent {
    String uid;
    int count;
    ExerciseModel exerciseModel;
    ContentModel contentModel;
    boolean isExercise, isContent;

    public TrialContent() {
    }

    public TrialContent(String uid, int count, ExerciseModel exerciseModel, ContentModel contentModel, boolean isExercise, boolean isContent) {
        this.uid = uid;
        this.count = count;
        this.exerciseModel = exerciseModel;
        this.contentModel = contentModel;
        this.isExercise = isExercise;
        this.isContent = isContent;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public ExerciseModel getExerciseModel() {
        return exerciseModel;
    }

    public void setExerciseModel(ExerciseModel exerciseModel) {
        this.exerciseModel = exerciseModel;
    }

    public ContentModel getContentModel() {
        return contentModel;
    }

    public void setContentModel(ContentModel contentModel) {
        this.contentModel = contentModel;
    }

    public boolean isExercise() {
        return isExercise;
    }

    public void setExercise(boolean exercise) {
        isExercise = exercise;
    }

    public boolean isContent() {
        return isContent;
    }

    public void setContent(boolean content) {
        isContent = content;
    }
}
