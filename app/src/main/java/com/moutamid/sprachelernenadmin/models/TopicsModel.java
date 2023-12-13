package com.moutamid.sprachelernenadmin.models;

public class TopicsModel {
    String ID, topicName;

    public TopicsModel() {
    }

    public TopicsModel(String ID, String topicName) {
        this.ID = ID;
        this.topicName = topicName;
    }

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public String getTopicName() {
        return topicName;
    }

    public void setTopicName(String topicName) {
        this.topicName = topicName;
    }
}
