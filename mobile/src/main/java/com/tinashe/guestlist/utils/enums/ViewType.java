package com.tinashe.guestlist.utils.enums;

import com.tinashe.guestlist.R;

/**
 * Created by tinashe on 2016/02/09.
 */
public enum ViewType {

    FAMILY(R.string.family),

    ADULTS(R.string.adults),

    CHILDREN(R.string.children),

    NEED_TRANSPORT(R.string.need_transport),

    VEGETARIAN(R.string.vegetarian_info);

    private int titleResId;

    ViewType(int titleResId) {
        this.titleResId = titleResId;
    }

    public int getTitleResId() {
        return titleResId;
    }
}
