package com.moutamid.sprachelernenadmin.models;

public class PaymentModel {
    String ID, userID, username, userEmail, price, duration, proofImage;
    long timestamp;

    boolean isApprove;

    public PaymentModel() {
    }

    public PaymentModel(String ID, String userID, String username, String userEmail, String price, String duration, String proofImage, long timestamp) {
        this.ID = ID;
        this.userID = userID;
        this.username = username;
        this.userEmail = userEmail;
        this.price = price;
        this.duration = duration;
        this.proofImage = proofImage;
        this.timestamp = timestamp;
    }

    public PaymentModel(String ID, String userID, String username, String userEmail, String price, String duration, String proofImage, long timestamp, boolean isApprove) {
        this.ID = ID;
        this.userID = userID;
        this.username = username;
        this.userEmail = userEmail;
        this.price = price;
        this.duration = duration;
        this.proofImage = proofImage;
        this.timestamp = timestamp;
        this.isApprove = isApprove;
    }

    public boolean isApprove() {
        return isApprove;
    }

    public void setApprove(boolean approve) {
        isApprove = approve;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public String getProofImage() {
        return proofImage;
    }

    public void setProofImage(String proofImage) {
        this.proofImage = proofImage;
    }
}
