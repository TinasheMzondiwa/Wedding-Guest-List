package com.tinashe.guestlist.utils;

import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.tinashe.guestlist.model.Feed;
import com.tinashe.guestlist.model.Guest;
import com.tinashe.guestlist.model.Table;

import java.util.Calendar;

/**
 * Created by tinashe on 2016/02/18.
 */
public class FeedUtil {

    public static Feed buildGuestAddedFeed(@NonNull Guest guest) {
        Feed feed = new Feed();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) { //Should never happen
            return feed;
        }

        String name = user.getDisplayName();
        if (TextUtils.isEmpty(name)) {
            name = "";
        }

        feed.setVisible(true);
        feed.setOwnerKey(user.getEmail());
        feed.setOwnerUrl(user.getPhotoUrl() == null ? null : user.getPhotoUrl().toString());
        feed.setOwner(name.contains(" ") ? name.substring(0, name.indexOf(" ")) : name);
        feed.setDisplay("...added <b>" + guest.getFirstName() + "</b>");
        feed.setTimestampCreated(Calendar.getInstance().getTimeInMillis());


        return feed;
    }

    public static Feed buildGuestAssignedTableFeed(@NonNull Guest guest, @NonNull Table table) {
        Feed feed = new Feed();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) { //Should never happen
            return feed;
        }

        String name = user.getDisplayName();
        if (TextUtils.isEmpty(name)) {
            name = "";
        }

        feed.setVisible(true);
        feed.setOwnerKey(user.getEmail());
        feed.setOwnerUrl(user.getPhotoUrl() == null ? null : user.getPhotoUrl().toString());
        feed.setOwner(name.contains(" ") ? name.substring(0, name.indexOf(" ")) : name);
        feed.setDisplay("...assigned <b>" + guest.getFirstName() + "</b> to " + "<b>Table #" + table.getNumber() + "</b>");
        feed.setTimestampCreated(Calendar.getInstance().getTimeInMillis());

        return feed;
    }

    public static boolean isOwner(@NonNull Feed feed) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            return false;
        }
        String userEmail = user.getEmail();

        return (!TextUtils.isEmpty(userEmail) &&
                !TextUtils.isEmpty(feed.getOwnerKey()) &&
                feed.getOwnerKey().equals(userEmail));
    }
}
