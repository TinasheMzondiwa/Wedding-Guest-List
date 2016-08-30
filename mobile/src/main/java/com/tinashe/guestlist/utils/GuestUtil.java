package com.tinashe.guestlist.utils;

import com.tinashe.guestlist.model.Guest;
import com.tinashe.guestlist.utils.enums.ViewType;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by tinashe on 2016/02/09.
 */
public class GuestUtil {

    public static Guest findGuest(List<Guest> guestList, String key) {
        for (Guest guest : guestList) {
            if (guest.getKey().equals(key)) {
                return guest;
            }
        }

        return null;
    }

    public static int adultsCount(List<Guest> guestList) {
        int count = 0;

        for (Guest guest : guestList) {
            if (!guest.isChild()) {
                count++;
            }
        }
        return count;
    }

    public static int childrenCount(List<Guest> guestList) {
        int count = 0;

        for (Guest guest : guestList) {
            if (guest.isChild()) {
                count++;
            }
        }
        return count;
    }

    public static int needTransportCount(List<Guest> guestList) {
        int count = 0;

        for (Guest guest : guestList) {
            if (guest.isNeedsTransport()) {
                count++;
            }
        }
        return count;
    }

    public static int veganCount(List<Guest> guestList) {
        int count = 0;

        for (Guest guest : guestList) {
            if (guest.isVegan()) {
                count++;
            }
        }
        return count;
    }

    public static int familyCount(List<Guest> guestList) {
        int count = 0;

        for (Guest guest : guestList) {
            if (guest.isFamily()) {
                count++;
            }
        }
        return count;
    }

    public static List<Guest> filter(List<Guest> guestList, List<ViewType> viewTypes) {
        List<Guest> filtered = new ArrayList<>();
        for (ViewType type : viewTypes) {
            switch (type) {
                case FAMILY:
                    for (Guest guest : getFamily(guestList)) {
                        if (!filtered.contains(guest)) {
                            filtered.add(guest);
                        }
                    }
                    break;
                case ADULTS:
                    for (Guest guest : getAdults(guestList)) {
                        if (!filtered.contains(guest)) {
                            filtered.add(guest);
                        }
                    }
                    break;
                case CHILDREN:
                    for (Guest guest : getChildren(guestList)) {
                        if (!filtered.contains(guest)) {
                            filtered.add(guest);
                        }
                    }
                    break;
                case NEED_TRANSPORT:
                    for (Guest guest : getNeedTransport(guestList)) {
                        if (!filtered.contains(guest)) {
                            filtered.add(guest);
                        }
                    }
                    break;
                case VEGETARIAN:
                    for (Guest guest : getVegans(guestList)) {
                        if (!filtered.contains(guest)) {
                            filtered.add(guest);
                        }
                    }
                    break;
            }
        }
        return filtered;
    }

    private static List<Guest> getFamily(List<Guest> guestList) {
        List<Guest> filtered = new ArrayList<>();
        for (Guest guest : guestList) {
            if (guest.isFamily()) {
                filtered.add(guest);
            }
        }
        return filtered;
    }

    private static List<Guest> getAdults(List<Guest> guestList) {
        List<Guest> filtered = new ArrayList<>();
        for (Guest guest : guestList) {
            if (!guest.isChild()) {
                filtered.add(guest);
            }
        }
        return filtered;
    }

    private static List<Guest> getChildren(List<Guest> guestList) {
        List<Guest> filtered = new ArrayList<>();
        for (Guest guest : guestList) {
            if (guest.isChild()) {
                filtered.add(guest);
            }
        }
        return filtered;
    }

    private static List<Guest> getNeedTransport(List<Guest> guestList) {
        List<Guest> filtered = new ArrayList<>();
        for (Guest guest : guestList) {
            if (guest.isNeedsTransport()) {
                filtered.add(guest);
            }
        }
        return filtered;
    }


    private static List<Guest> getVegans(List<Guest> guestList) {
        List<Guest> filtered = new ArrayList<>();
        for (Guest guest : guestList) {
            if (guest.isVegan()) {
                filtered.add(guest);
            }
        }
        return filtered;
    }


    public static List<Guest> guestsWithoutTable(List<Guest> guestList) {
        List<Guest> filtered = new ArrayList<>();
        for (Guest guest : guestList) {
            if (guest.getTableNum() == 0) {
                filtered.add(guest);
            }
        }
        return filtered;
    }
}
