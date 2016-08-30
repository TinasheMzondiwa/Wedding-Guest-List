package com.tinashe.guestlist.model;

import android.support.annotation.NonNull;

import com.google.firebase.database.IgnoreExtraProperties;

import java.io.Serializable;
import java.util.List;

/**
 * Created by tinashe on 2016/02/12.
 */
@IgnoreExtraProperties
public class Table implements Serializable, Comparable<Table> {

    private String key;

    private int number;

    private boolean vip;

    private List<String> people;

    public Table() {
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public boolean isVip() {
        return vip;
    }

    public void setVip(boolean vip) {
        this.vip = vip;
    }

    public List<String> getPeople() {
        return people;
    }

    public void setPeople(List<String> people) {
        this.people = people;
    }

    @Override
    public int compareTo(@NonNull Table another) {
        return number < another.number ? -1
                : number > another.number ? 1
                : 0;
    }
}
