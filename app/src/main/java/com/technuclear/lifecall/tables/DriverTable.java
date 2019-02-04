package com.technuclear.lifecall.tables;

public class DriverTable {

    @com.google.gson.annotations.SerializedName("id")
    private String id;

    @com.google.gson.annotations.SerializedName("name")
    private String name;

    @com.google.gson.annotations.SerializedName("phoneNumber")
    private String phoneNumber;

    @com.google.gson.annotations.SerializedName("userId")
    private String userId;

    public DriverTable() {

    }

    @Override
    public String toString() {
        return getName();
    }

    public DriverTable(String id, String name, String phoneNumber, String userId) {
        this.setId(id);
        this.setName(name);
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof DriverTable && ((DriverTable) o).id == id;
    }
}
