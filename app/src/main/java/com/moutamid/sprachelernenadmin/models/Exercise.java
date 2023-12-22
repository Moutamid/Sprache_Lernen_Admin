package com.moutamid.sprachelernenadmin.models;

public class Exercise {

    String ID, level, name;

    public Exercise() {
    }

    public Exercise(String ID, String level, String name) {
        this.ID = ID;
        this.level = level;
        this.name = name;
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
