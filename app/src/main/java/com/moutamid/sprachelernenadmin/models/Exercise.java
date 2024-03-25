package com.moutamid.sprachelernenadmin.models;

public class Exercise {

    String ID, level, name;
    int exerciseCount;
    public Exercise() {
    }

    public Exercise(String ID, String level, String name, int exerciseCount) {
        this.ID = ID;
        this.level = level;
        this.name = name;
        this.exerciseCount = exerciseCount;
    }

    public int getExerciseCount() {
        return exerciseCount;
    }

    public void setExerciseCount(int exerciseCount) {
        this.exerciseCount = exerciseCount;
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
