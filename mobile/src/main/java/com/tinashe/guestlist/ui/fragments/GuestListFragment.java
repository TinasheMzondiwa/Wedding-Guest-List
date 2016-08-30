package com.tinashe.guestlist.ui.fragments;


import android.app.Activity;
import android.content.Intent;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.SearchView;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import com.tinashe.guestlist.R;
import com.tinashe.guestlist.callbacks.GuestListCallback;
import com.tinashe.guestlist.model.Guest;
import com.tinashe.guestlist.ui.GuestActivity;
import com.tinashe.guestlist.ui.adapters.GuestListAdapter;
import com.tinashe.guestlist.utils.enums.BundledExtras;

import java.util.List;

/**
 * Created by tinashe on 2016/02/12.
 */
public class GuestListFragment extends BaseTabFragment implements GuestListCallback {

    public static final int REQUEST_ADD_GUEST = 534;
    private static final int REQUEST_EDIT_GUEST = 535;

    private GuestListAdapter mAdapter = new GuestListAdapter(this);

    @Override
    protected void initialize() {
        super.initialize();

        recyclerView.setAdapter(mAdapter);

        fastScroller.setRecyclerView(recyclerView);
        recyclerView.addOnScrollListener(fastScroller.getOnScrollListener());
        fastScroller.setSectionIndicator(sectionTitleIndicator);

        if (controller.getGuestList() != null) {
            setGuestList(controller.getGuestList());
        }
    }

    public void setGuestList(List<Guest> guestList) {
        progressBar.setVisibility(View.GONE);

        mAdapter.setPermissionGranted(controller.isPermissionGranted());
        mAdapter.setGuestList(guestList);
    }

    @Override
    public void onItemSelected(Guest guest) {
        Intent intent = new Intent(getContext(), GuestActivity.class);
        intent.putExtra(BundledExtras.DATA_OBJECT.name(), guest);
        if (isAdmin()) {
            getActivity().startActivityForResult(intent, REQUEST_EDIT_GUEST);
        } else {
            getActivity().startActivity(intent);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_ADD_GUEST && resultCode == Activity.RESULT_OK &&
                (data != null && data.hasExtra(BundledExtras.DATA_OBJECT.name()))) {

            Guest guest = (Guest) data.getSerializableExtra(BundledExtras.DATA_OBJECT.name());
            controller.addGuest(guest);
        } else if (requestCode == REQUEST_EDIT_GUEST && resultCode == Activity.RESULT_OK) {
            Guest guest = (Guest) data.getSerializableExtra(BundledExtras.DATA_OBJECT.name());
            controller.editGuest(guest);
        } else if (requestCode == REQUEST_EDIT_GUEST && resultCode == Activity.RESULT_CANCELED &&
                (data != null && data.hasExtra(BundledExtras.DATA_OBJECT.name()))) {
            Guest guest = (Guest) data.getSerializableExtra(BundledExtras.DATA_OBJECT.name());
            controller.deleteGuest(guest);
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_search, menu);
        MenuItem mSearchItem = menu.findItem(R.id.action_search);
        SearchView mSearchView = (SearchView) MenuItemCompat.getActionView(mSearchItem);
        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (!TextUtils.isEmpty(newText)) {
                    if (mAdapter.getItemCount() == 0) {
                        controller.readGuestList();
                    }

                    mAdapter.getFilter().filter(newText);
                } else {
                    controller.readGuestList();
                }
                return false;
            }
        });

        MenuItemCompat.setOnActionExpandListener(mSearchItem, new MenuItemCompat.OnActionExpandListener() {
            @Override
            public boolean onMenuItemActionExpand(MenuItem item) {
                if (isAdmin()) {
                    controller.hideFab();
                }
                return true;
            }

            @Override
            public boolean onMenuItemActionCollapse(MenuItem item) {
                if (isAdmin()) {
                    controller.showFab();
                }
                return true;
            }
        });
    }

}
