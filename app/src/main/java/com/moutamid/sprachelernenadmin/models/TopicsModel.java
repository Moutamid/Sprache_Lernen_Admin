package com.moutamid.sprachelernenadmin.models;

public class TopicsModel {
    String ID, topicName, contentType;

    public TopicsModel() {
    }

    public TopicsModel(String ID, String topicName, String contentType) {
        this.ID = ID;
        this.topicName = topicName;
        this.contentType = contentType;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
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
