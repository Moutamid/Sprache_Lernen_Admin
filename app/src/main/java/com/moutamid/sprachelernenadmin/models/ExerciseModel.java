package com.moutamid.sprachelernenadmin.models;

import java.util.ArrayList;

public class ExerciseModel {
    String ID;
    String level;
    String question;
    String exerciseName;
    ArrayList<String> options;
    String rightAnswer;
    boolean isMCQs, isFillBlank, isOrder;
    String explanation;

    public ExerciseModel() {
    }

    public ExerciseModel(String ID, String level, String question, String exerciseName, ArrayList<String> options, String rightAnswer, boolean isMCQs, boolean isFillBlank, boolean isOrder, String explanation) {
        this.ID = ID;
        this.level = level;
        this.question = question;
        this.exerciseName = exerciseName;
        this.options = options;
        this.rightAnswer = rightAnswer;
        this.isMCQs = isMCQs;
        this.isFillBlank = isFillBlank;
        this.isOrder = isOrder;
        this.explanation = explanation;
    }

    public String getExplanation() {
        return explanation;
    }

    public void setExplanation(String explanation) {
        this.explanation = explanation;
    }

    public String getExerciseName() {
        return exerciseName;
    }

    public void setExerciseName(String exerciseName) {
        this.exerciseName = exerciseName;
    }

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public ArrayList<String> getOptions() {
        return options;
    }

    public void setOptions(ArrayList<String> options) {
        this.options = options;
    }

    public String getRightAnswer() {
        return rightAnswer;
    }

    public void setRightAnswer(String rightAnswer) {
        this.rightAnswer = rightAnswer;
    }

    public boolean isMCQs() {
        return isMCQs;
    }

    public void setMCQs(boolean MCQs) {
        isMCQs = MCQs;
    }

    public boolean isFillBlank() {
        return isFillBlank;
    }

    public void setFillBlank(boolean fillBlank) {
        isFillBlank = fillBlank;
    }

    public boolean isOrder() {
        return isOrder;
    }

    public void setOrder(boolean order) {
        isOrder = order;
    }
}
