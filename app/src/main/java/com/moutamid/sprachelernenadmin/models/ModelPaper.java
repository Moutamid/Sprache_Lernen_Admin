package com.moutamid.sprachelernenadmin.models;

import java.util.ArrayList;

public class ModelPaper {
    public String id, name, link;
    public ArrayList<String> voices;

    public ModelPaper() {}

    @Override
    public String toString() {
        return "ModelPaper{" +
                "name='" + name + '\'' +
                ", link='" + link + '\'' +
                '}';
    }
}
