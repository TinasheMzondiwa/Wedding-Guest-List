package com.tinashe.guestlist.model;

import android.support.annotation.NonNull;

import com.google.firebase.database.IgnoreExtraProperties;

import java.io.Serializable;
import java.util.HashMap;

/**
 * Created by tinashe on 2016/02/08.
 */

@IgnoreExtraProperties
public class Guest implements Serializable, Comparable<Guest> {

    private String key;

    private String title;

    private String firstName;

    private String lastName;

    private String email;

    private String phone;

    private HashMap<String, Object> timestampCreated;

    private boolean child;

    private boolean family;

    private boolean needsTransport;

    private boolean vegan;

    private String note;

    private int tableNum;

    public Guest() {
    }

    public Guest(String firstName, String lastName, HashMap<String, Object> timestampCreated) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.timestampCreated = timestampCreated;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public HashMap<String, Object> getTimestampCreated() {
        return timestampCreated;
    }

    public void setTimestampCreated(HashMap<String, Object> timestampCreated) {
        this.timestampCreated = timestampCreated;
    }

    public boolean isChild() {
        return child;
    }

    public void setChild(boolean child) {
        this.child = child;
    }

    public boolean isFamily() {
        return family;
    }

    public void setFamily(boolean family) {
        this.family = family;
    }

    public boolean isNeedsTransport() {
        return needsTransport;
    }

    public void setNeedsTransport(boolean needsTransport) {
        this.needsTransport = needsTransport;
    }

    public boolean isVegan() {
        return vegan;
    }

    public void setVegan(boolean vegan) {
        this.vegan = vegan;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public int getTableNum() {
        return tableNum;
    }

    public void setTableNum(int tableNum) {
        this.tableNum = tableNum;
    }

    @Override
    public int compareTo(@NonNull Guest another) {
        return firstName.compareTo(another.firstName);
    }
}
