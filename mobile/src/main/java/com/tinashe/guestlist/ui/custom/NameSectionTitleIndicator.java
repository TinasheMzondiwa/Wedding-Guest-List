package com.tinashe.guestlist.ui.custom;

import android.content.Context;
import android.util.AttributeSet;

import xyz.danoz.recyclerviewfastscroller.sectionindicator.title.SectionTitleIndicator;

/**
 * Created by tinashe on 2015/11/03.
 */
public class NameSectionTitleIndicator extends SectionTitleIndicator<String> {
    public NameSectionTitleIndicator(Context context) {
        super(context);
    }

    public NameSectionTitleIndicator(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public NameSectionTitleIndicator(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void setSection(String name) {
        setTitleText(name.toUpperCase().charAt(0) + "");
    }
}
