package com.moutamid.sprachelernenadmin.models;

import java.util.ArrayList;

public class ExerciseModel {
    String ID;
    TopicsModel topicsModel;
    String question;
    ArrayList<String> options;
    String rightAnswer;
    boolean isMCQs, isFillBlank, isOrder;

    public ExerciseModel() {
    }

    public ExerciseModel(String ID, TopicsModel topicsModel, String question, ArrayList<String> options, String rightAnswer, boolean isMCQs, boolean isFillBlank, boolean isOrder) {
        this.ID = ID;
        this.topicsModel = topicsModel;
        this.question = question;
        this.options = options;
        this.rightAnswer = rightAnswer;
        this.isMCQs = isMCQs;
        this.isFillBlank = isFillBlank;
        this.isOrder = isOrder;
    }

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public TopicsModel getTopicsModel() {
        return topicsModel;
    }

    public void setTopicsModel(TopicsModel topicsModel) {
        this.topicsModel = topicsModel;
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
