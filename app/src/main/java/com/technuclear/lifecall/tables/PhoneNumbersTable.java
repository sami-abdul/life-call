package com.technuclear.lifecall.tables;

public class PhoneNumbersTable {

    @com.google.gson.annotations.SerializedName("id")
    private String id;

    @com.google.gson.annotations.SerializedName("phoneNumber")
    private String phoneNumber;

    @com.google.gson.annotations.SerializedName("userId")
    private String userId;

    public PhoneNumbersTable() {

    }

    public PhoneNumbersTable(String id, String phoneNumber, String userId) {
        this.setId(id);
        this.setPhoneNumber(phoneNumber);
        this.setUserId(userId);
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
}
