package com.technuclear.lifecall.entities;

public class Friend extends Entity implements Comparable {

    String phoneNumber;

    public Friend(String name, int age, int weight, boolean hasDisease, String disease, String bloodGroup, String phoneNumber) {
        super(name, age, weight, hasDisease, disease, bloodGroup);
        this.phoneNumber = phoneNumber;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    @Override
    public int compareTo(Object o) {
        Friend friend = (Friend) o;
        return this.getName().compareTo(friend.getName());
    }
}