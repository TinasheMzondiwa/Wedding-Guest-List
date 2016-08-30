package com.tinashe.guestlist.model;

import android.support.annotation.NonNull;

import com.google.firebase.database.IgnoreExtraProperties;

import java.io.Serializable;
import java.util.Calendar;

/**
 * Created by tinashe on 2016/02/17.
 */
@IgnoreExtraProperties
public class Feed implements Serializable, Comparable<Feed> {

    private String key;

    private String ownerKey;

    private boolean visible;

    private String display;

    private String owner;

    private String ownerUrl;

    private long timestampCreated;

    public Feed() {
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getOwnerKey() {
        return ownerKey;
    }

    public void setOwnerKey(String ownerKey) {
        this.ownerKey = ownerKey;
    }

    public long getTimestampCreated() {
        return timestampCreated;
    }

    public void setTimestampCreated(long timestampCreated) {
        this.timestampCreated = timestampCreated;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public String getDisplay() {
        return display;
    }

    public void setDisplay(String display) {
        this.display = display;
    }

    public boolean isVisible() {
        return visible;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    public String getOwnerUrl() {
        return ownerUrl;
    }

    public void setOwnerUrl(String ownerUrl) {
        this.ownerUrl = ownerUrl;
    }

    @Override
    public int compareTo(@NonNull Feed another) {

        Calendar l_time = Calendar.getInstance();
        l_time.setTimeInMillis(timestampCreated);

        Calendar r_time = Calendar.getInstance();
        r_time.setTimeInMillis(another.timestampCreated);

        return r_time.compareTo(l_time);
    }
}
