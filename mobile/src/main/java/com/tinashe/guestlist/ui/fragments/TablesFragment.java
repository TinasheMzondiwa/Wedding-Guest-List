package com.tinashe.guestlist.ui.fragments;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;

import com.tinashe.guestlist.R;
import com.tinashe.guestlist.callbacks.GuestListCallback;
import com.tinashe.guestlist.callbacks.TableListCallback;
import com.tinashe.guestlist.model.Guest;
import com.tinashe.guestlist.model.Table;
import com.tinashe.guestlist.ui.GuestActivity;
import com.tinashe.guestlist.ui.adapters.GuestListAdapter;
import com.tinashe.guestlist.ui.adapters.TableListAdapter;
import com.tinashe.guestlist.utils.FeedUtil;
import com.tinashe.guestlist.utils.GuestUtil;
import com.tinashe.guestlist.utils.enums.BundledExtras;

import java.util.Collections;
import java.util.List;

import butterknife.ButterKnife;

/**
 * Created by tinashe on 2016/02/12.
 */
public class TablesFragment extends BaseTabFragment implements TableListCallback {

    public static final int REQUEST_ADD_TABLE = 334;
    @SuppressWarnings("unused")
    private static final String TAG = TablesFragment.class.getName();
    private TableListAdapter mAdapter = new TableListAdapter(this);

    private AlertDialog mDialog;

    @Override
    protected void initialize() {
        super.initialize();
        fastScroller.setVisibility(View.GONE);
        sectionTitleIndicator.setVisibility(View.GONE);

        StaggeredGridLayoutManager manager = new StaggeredGridLayoutManager(
                getResources().getInteger(R.integer.columns), StaggeredGridLayoutManager.VERTICAL
        );
        manager.setGapStrategy(StaggeredGridLayoutManager.GAP_HANDLING_MOVE_ITEMS_BETWEEN_SPANS);
        recyclerView.setLayoutManager(manager);
        recyclerView.setAdapter(mAdapter);

        if (controller.getGuestList() != null && controller.getTablesList() != null) {
            setGuestList(controller.getGuestList());
            setTableList(controller.getTablesList());
        }
    }

    public void setTableList(List<Table> tableList) {
        Collections.sort(tableList);

        mAdapter.setTableList(tableList);

        progressBar.setVisibility(View.GONE);
        emptyView.setVisibility(View.GONE);
    }

    public void setGuestList(List<Guest> guestList) {
        mAdapter.setGuestList(guestList);

        progressBar.setVisibility(View.GONE);
        progressBar.setVisibility(View.GONE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_ADD_TABLE && resultCode == Activity.RESULT_OK) {
            Table table = (Table) data.getSerializableExtra(BundledExtras.DATA_OBJECT.name());

            controller.addTable(table);
        }
    }

    @Override
    public boolean isUserAdmin() {
        return isAdmin();
    }

    @Override
    public void deleteTable(Table table) {
        controller.deleteTable(table);
    }

    @Override
    public void addGuest(final Table table) {
        View dialogView = LayoutInflater.from(getContext())
                .inflate(R.layout.dialog_guest_picker, null, false);
        final RecyclerView list = ButterKnife.findById(dialogView, R.id.recyclerview);
        GuestListAdapter adapter = new GuestListAdapter(new GuestListCallback() {
            @Override
            public void onItemSelected(Guest guest) {
                mDialog.dismiss();

                table.getPeople().add(guest.getKey());
                guest.setTableNum(table.getNumber());

                controller.editGuest(guest);
                controller.editTable(table);

                controller.addFeed(FeedUtil.buildGuestAssignedTableFeed(guest, table));
            }
        });
        adapter.setPermissionGranted(controller.isPermissionGranted());

        list.setAdapter(adapter);
        adapter.setGuestList(GuestUtil.guestsWithoutTable(mAdapter.getGuestList()));

        mDialog = new AlertDialog.Builder(getContext())
                .setTitle(R.string.action_add)
                .setView(dialogView)
                .setNegativeButton(R.string.action_cancel, null)
                .create();

        mDialog.show();

    }

    @Override
    public void showGuestOptions(final Guest guest, final Table table) {
        String[] arrs = {"View", "Remove"};
        new AlertDialog.Builder(getContext())
                .setItems(arrs, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0:
                                Intent intent = new Intent(getContext(), GuestActivity.class);
                                intent.putExtra(BundledExtras.DATA_OBJECT.name(), guest);
                                getActivity().startActivity(intent);
                                break;
                            case 1:
                                showDeleteDialog(guest, table);
                                break;
                        }
                    }
                }).create().show();

    }

    private void showDeleteDialog(final Guest guest, final Table table) {
        mDialog = new AlertDialog.Builder(getContext())
                .setTitle("Remove Guest")
                .setMessage("Are you sure you want to remove " + guest.getFirstName() + "?")
                .setNegativeButton(R.string.action_remove, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        guest.setTableNum(0);
                        controller.editGuest(guest);

                        table.getPeople().remove(guest.getKey());
                        if (table.getPeople().isEmpty()) {
                            controller.deleteTable(table);
                        } else {
                            controller.editTable(table);
                        }

                    }
                })
                .setPositiveButton("No", null)
                .create();

        mDialog.show();
    }
}
