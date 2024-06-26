package com.moutamid.sprachelernenadmin.models;

public class VocabularyModel {
    String ID, topicID, name, nameGerman, image, audio, germanAudio;
    int pos;
    public VocabularyModel() {
    }

    public VocabularyModel(String ID, String topicID, String name, String nameGerman, String image, String audio, String germanAudio, int pos) {
        this.ID = ID;
        this.topicID = topicID;
        this.name = name;
        this.nameGerman = nameGerman;
        this.image = image;
        this.audio = audio;
        this.germanAudio = germanAudio;
        this.pos = pos;
    }

    public VocabularyModel(String ID, String topicID, String name, String nameGerman, String image, String audio, String germanAudio) {
        this.ID = ID;
        this.topicID = topicID;
        this.name = name;
        this.nameGerman = nameGerman;
        this.image = image;
        this.audio = audio;
        this.germanAudio = germanAudio;
    }

    public int getPos() {
        return pos;
    }

    public void setPos(int pos) {
        this.pos = pos;
    }

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public String getTopicID() {
        return topicID;
    }

    public void setTopicID(String topicID) {
        this.topicID = topicID;
    }
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNameGerman() {
        return nameGerman;
    }

    public void setNameGerman(String nameGerman) {
        this.nameGerman = nameGerman;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getAudio() {
        return audio;
    }

    public void setAudio(String audio) {
        this.audio = audio;
    }

    public String getGermanAudio() {
        return germanAudio;
    }

    public void setGermanAudio(String germanAudio) {
        this.germanAudio = germanAudio;
    }
}
