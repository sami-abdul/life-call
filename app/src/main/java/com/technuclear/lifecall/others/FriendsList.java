package com.technuclear.lifecall.others;

import com.technuclear.lifecall.entities.Entity;
import com.technuclear.lifecall.entities.Friend;

import java.util.*;

public class FriendsList {

    public static ArrayList<Friend> friends;

    public FriendsList() {
        friends = new ArrayList<>();
    }

    public void add(String name, int age, int weight, boolean hasDisease, String disease, String bloodGroup, String phoneNumber) {
        friends.add(new Friend(name, age, weight, hasDisease, disease, bloodGroup, phoneNumber));
    }

    public void add(Entity entity) {
        friends.add((Friend) entity);
    }

    public void delete(Entity entity) {
        friends.remove(entity);
    }

    public void delete(String id) {

    }

    public Entity find(String id) {
        return null;
    }

    public ArrayList<Entity> search(String criteria) {
        ArrayList<Entity> friends = new ArrayList<>();
        Iterator<Friend> iterator = FriendsList.friends.iterator();
        while (iterator.hasNext()) {
            Friend friend = iterator.next();
            if (friend.getName().equals(criteria) || friend.getDisease().equals(criteria))
                friends.add(friend);
        }
        return friends;
    }

    public ArrayList<Entity> search(int criteria) {
        ArrayList<Entity> friends = new ArrayList<>();
        Iterator<Friend> iterator = FriendsList.friends.iterator();
        while (iterator.hasNext()) {
            Friend friend = iterator.next();
            if (friend.getAge() == criteria)
                friends.add(friend);
        }
        return friends;
    }

    public ArrayList<Entity> search(double criteria) {
        ArrayList<Entity> friends = new ArrayList<>();
        Iterator<Friend> iterator = FriendsList.friends.iterator();
        while (iterator.hasNext()) {
            Friend friend = iterator.next();
            if (friend.getWeight() == criteria)
                friends.add(friend);
        }
        return friends;
    }

    public void sort(String criteria) {
        if (criteria.equalsIgnoreCase("name")) {
            NameSort sort = new NameSort();
            Collections.sort(FriendsList.friends, sort);
        }
        else if (criteria.equalsIgnoreCase("age")) {
            AgeSort sort = new AgeSort();
            Collections.sort(FriendsList.friends, sort);
        }
        else if (criteria.equalsIgnoreCase("weight")) {
            WeightSort sort = new WeightSort();
            Collections.sort(FriendsList.friends, sort);
        }
    }

    @Override
    public String toString() {
        return FriendsList.friends.toString();
    }

    class NameSort implements Comparator<Friend> {

        @Override
        public int compare(Friend o1, Friend o2) {
            return o1.getName().compareTo(o2.getName());
        }
    }

    class AgeSort implements Comparator<Friend> {

        @Override
        public int compare(Friend o1, Friend o2) {
            return Integer.valueOf(o1.getAge()).compareTo(Integer.valueOf(o2.getAge()));
        }
    }

    class WeightSort implements Comparator<Friend> {

        @Override
        public int compare(Friend o1, Friend o2) {
            return Double.valueOf(o1.getWeight()).compareTo(Double.valueOf(o2.getWeight()));
        }
    }
}
