package com.moutamid.sprachelernenadmin.models;

public class WritingModel {
    String id, topic, letter;
    String germanTopic, germanLetter;
    String audio, germanAudio;
    public WritingModel() {
    }

    public WritingModel(String id, String topic, String letter, String germanTopic, String germanLetter, String audio, String germanAudio) {
        this.id = id;
        this.topic = topic;
        this.letter = letter;
        this.germanTopic = germanTopic;
        this.germanLetter = germanLetter;
        this.audio = audio;
        this.germanAudio = germanAudio;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public String getLetter() {
        return letter;
    }

    public void setLetter(String letter) {
        this.letter = letter;
    }

    public String getGermanTopic() {
        return germanTopic;
    }

    public void setGermanTopic(String germanTopic) {
        this.germanTopic = germanTopic;
    }

    public String getGermanLetter() {
        return germanLetter;
    }

    public void setGermanLetter(String germanLetter) {
        this.germanLetter = germanLetter;
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
