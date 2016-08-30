package com.tinashe.guestlist.ui.adapters;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.util.SparseArray;
import android.view.ViewGroup;

import com.tinashe.guestlist.R;
import com.tinashe.guestlist.ui.fragments.FeedLisFragment;
import com.tinashe.guestlist.ui.fragments.GuestListFragment;
import com.tinashe.guestlist.ui.fragments.TablesFragment;
import com.tinashe.guestlist.ui.fragments.VenueFragment;

/**
 * Created by tinashe on 2016/02/12.
 */
public class TabsFragmentPagerAdapter extends FragmentStatePagerAdapter {

    private final Context mContext;
    private SparseArray<Fragment> registeredFragments = new SparseArray<>();

    public TabsFragmentPagerAdapter(FragmentManager fm, Context context) {
        super(fm);
        this.mContext = context;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        Fragment fragment = (Fragment) super.instantiateItem(container, position);
        registeredFragments.put(position, fragment);
        return fragment;
    }

    public Fragment getRegisteredFragment(int position) {
        return registeredFragments.get(position);
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return new GuestListFragment();
            case 1:
                return new TablesFragment();
            case 2:
                return new VenueFragment();
            default:
                return new FeedLisFragment();
        }
    }

    @Override
    public int getCount() {
        return 4;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        if (position == 0) {
            return mContext.getString(R.string.guests).toUpperCase();
        } else if (position == 1) {
            return mContext.getString(R.string.tables).toUpperCase();
        } else if (position == 2) {
            return mContext.getString(R.string.venue).toUpperCase();
        } else return mContext.getString(R.string.feed);
    }
}
