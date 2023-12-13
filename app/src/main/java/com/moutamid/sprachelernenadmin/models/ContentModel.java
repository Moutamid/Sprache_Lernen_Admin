package com.moutamid.sprachelernenadmin.models;

import java.util.ArrayList;

public class ContentModel {
    String ID;
    TopicsModel topicsModel;
    String heading, note;
    boolean hasOptions, haveTable;
    ArrayList<String> options;
    ArrayList<String> rows;

    public ContentModel() {
    }

    public ContentModel(String ID, TopicsModel topicsModel, String heading, String note, boolean hasOptions, boolean haveTable, ArrayList<String> options, ArrayList<String> rows) {
        this.ID = ID;
        this.topicsModel = topicsModel;
        this.heading = heading;
        this.note = note;
        this.hasOptions = hasOptions;
        this.haveTable = haveTable;
        this.options = options;
        this.rows = rows;
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

    public String getHeading() {
        return heading;
    }

    public void setHeading(String heading) {
        this.heading = heading;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public boolean isHasOptions() {
        return hasOptions;
    }

    public void setHasOptions(boolean hasOptions) {
        this.hasOptions = hasOptions;
    }

    public boolean isHaveTable() {
        return haveTable;
    }

    public void setHaveTable(boolean haveTable) {
        this.haveTable = haveTable;
    }

    public ArrayList<String> getOptions() {
        return options;
    }

    public void setOptions(ArrayList<String> options) {
        this.options = options;
    }

    public ArrayList<String> getRows() {
        return rows;
    }

    public void setRows(ArrayList<String> rows) {
        this.rows = rows;
    }
}
