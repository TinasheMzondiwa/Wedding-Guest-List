package com.tinashe.guestlist.callbacks;

import com.tinashe.guestlist.model.Feed;
import com.tinashe.guestlist.model.Guest;
import com.tinashe.guestlist.model.Table;

import java.util.List;

/**
 * Created by tinashe on 2016/02/12.
 */
public interface MainController {

    boolean isPermissionGranted();

    void hideFab();

    void showFab();

    List<Guest> getGuestList();

    List<Table> getTablesList();

    List<Feed> getFeedList();


    void readGuestList();

    void addGuest(Guest guest);

    void editGuest(Guest guest);

    void deleteGuest(Guest guest);


    void readTableList();

    void addTable(Table table);

    void editTable(Table table);

    void deleteTable(Table table);


    void readFeedList();

    void addFeed(Feed feed);

    void deleteFeed(Feed feed);
}
