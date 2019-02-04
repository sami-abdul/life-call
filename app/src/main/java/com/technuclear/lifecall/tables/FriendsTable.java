package com.technuclear.lifecall.tables;

public class FriendsTable {

    @com.google.gson.annotations.SerializedName("id")
    private String mId;

    @com.google.gson.annotations.SerializedName("name")
    private String mName;

    @com.google.gson.annotations.SerializedName("age")
    private int age;

    @com.google.gson.annotations.SerializedName("weight")
    private int weight;

    @com.google.gson.annotations.SerializedName("hasDisease")
    private boolean mHasDisease;

    @com.google.gson.annotations.SerializedName("disease")
    private String disease;

    @com.google.gson.annotations.SerializedName("bloodGroup")
    private String bloodGroup;

    @com.google.gson.annotations.SerializedName("phoneNumber")
    private String phoneNumber;

    @com.google.gson.annotations.SerializedName("friendID")
    private String friendID;

    public FriendsTable() {

    }

    @Override
    public String toString() {
        return getName();
    }

    public FriendsTable(String id, String name, int age, int weight, boolean mHasDisease, String disease, String bloodGroup, String phoneNumber) {
        this.setId(id);
        this.setName(name);
        this.setAge(age);
        this.setWeight(weight);
        this.setHasDisease(mHasDisease);
        this.setDisease(disease);
        this.setBloodGroup(bloodGroup);
        this.setPhoneNumber(phoneNumber);
    }

    public String getName() {
        return mName;
    }

    public final void setName(String text) {
        mName = text;
    }

    public String getId() {
        return mId;
    }

    public final void setId(String id) {
        mId = id;
    }

    public boolean getHasDisease() {
        return mHasDisease;
    }

    public void setHasDisease(boolean hasDisease) {
        mHasDisease = hasDisease;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public int getWeight() {
        return weight;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }

    public String getBloodGroup() {
        return bloodGroup;
    }

    public void setBloodGroup(String bloodGroup) {
        this.bloodGroup = bloodGroup;
    }

    public String getDisease() {
        return disease;
    }

    public void setDisease(String disease) {
        this.disease = disease;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getFriendID() {
        return friendID;
    }

    public void setFriendID(String friendID) {
        this.friendID = friendID;
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof UserTable && ((FriendsTable) o).mId == mId;
    }
}