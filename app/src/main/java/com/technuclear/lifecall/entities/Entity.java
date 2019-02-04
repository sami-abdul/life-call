package com.technuclear.lifecall.entities;

public class Entity {
    private String name;
    private int age;
    private int weight;
    private boolean hasDisease;
    private String disease;

    public enum BloodGroup {
        A_POSITIVE, A_NEGATIVE, B_POSITIVE, B_NEGATIVE,
        O_POSITIVE, O_NEGATIVE, AB_POSITIVE, AB_NEGATIVE
    }

    private BloodGroup bloodGroup;

    public Entity(String name, int age, int weight, boolean hasDisease, String disease, String bloodGroup) {
        this.name = name;
        this.age = age;
        this.weight = weight;
        this.hasDisease = hasDisease;
        this.disease = (hasDisease) ? disease : "";
        setBloodGroup(bloodGroup);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public boolean isHasDisease() {
        return hasDisease;
    }

    public void setHasDisease(boolean hasDisease) {
        this.hasDisease = hasDisease;
    }

    public String getDisease() {
        return disease;
    }

    public void setDisease(String disease) {
        this.disease = disease;
    }

    public String getBloodGroup() {
        if (bloodGroup.equals(BloodGroup.A_POSITIVE))
            return "A+";
        if (bloodGroup.equals(BloodGroup.A_NEGATIVE))
            return "A-";
        if (bloodGroup.equals(BloodGroup.B_POSITIVE))
            return "B+";
        if (bloodGroup.equals(BloodGroup.B_NEGATIVE))
            return "B-";
        if (bloodGroup.equals(BloodGroup.O_POSITIVE))
            return "O+";
        if (bloodGroup.equals(BloodGroup.O_NEGATIVE))
            return "O-";
        if (bloodGroup.equals(BloodGroup.AB_POSITIVE))
            return "AB+";
        else
            return "AB-";
    }

    public void setBloodGroup(String bloodGroup) {
        switch (bloodGroup) {
            case "A+":
                this.bloodGroup = BloodGroup.A_POSITIVE;
            case "A-":
                this.bloodGroup = BloodGroup.A_NEGATIVE;
            case "B+":
                this.bloodGroup = BloodGroup.B_POSITIVE;
            case "B-":
                this.bloodGroup = BloodGroup.B_NEGATIVE;
            case "O+":
                this.bloodGroup = BloodGroup.O_POSITIVE;
            case "O-":
                this.bloodGroup = BloodGroup.O_NEGATIVE;
            case "AB+":
                this.bloodGroup = BloodGroup.AB_POSITIVE;
            case "AB-":
                this.bloodGroup = BloodGroup.AB_NEGATIVE;
        }
    }

    @Override
    public String toString() {
        return this.getName();
    }
}