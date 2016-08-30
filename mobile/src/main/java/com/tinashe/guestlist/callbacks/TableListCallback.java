package com.tinashe.guestlist.callbacks;

import com.tinashe.guestlist.model.Guest;
import com.tinashe.guestlist.model.Table;

/**
 * Created by tinashe on 2016/02/12.
 */
public interface TableListCallback {

    boolean isUserAdmin();

    void deleteTable(Table table);

    void addGuest(Table table);

    void showGuestOptions(Guest guest, Table table);
}
