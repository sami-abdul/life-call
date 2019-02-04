package com.technuclear.lifecall.entities;

public class User extends Entity {
    private String id;

    public User(String id, String name, int age, int weight, boolean hasDisease, String disease, String bloodGroup) {
        super(name, age, weight, hasDisease, disease, bloodGroup);
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
