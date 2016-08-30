package com.tinashe.guestlist.ui;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckedTextView;
import android.widget.EditText;
import android.widget.Toast;

import com.doodle.android.chips.ChipsView;
import com.doodle.android.chips.model.Contact;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.tinashe.guestlist.R;
import com.tinashe.guestlist.callbacks.GuestListCallback;
import com.tinashe.guestlist.model.Guest;
import com.tinashe.guestlist.model.Table;
import com.tinashe.guestlist.ui.adapters.GuestListAdapter;
import com.tinashe.guestlist.ui.custom.TextInputWrappedWatcher;
import com.tinashe.guestlist.utils.Constants;
import com.tinashe.guestlist.utils.enums.BundledExtras;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by tinashe on 2016/02/12.
 */
public class TableActivity extends BaseActivity implements GuestListCallback {

    @SuppressWarnings("unused")
    private static final String TAG = TableActivity.class.getName();

    private static final int MAX_GUESTS_PER_TABLE = 10;

    @Bind(R.id.chipsView)
    ChipsView mChipsView;

    @Bind(R.id.recycler_view)
    RecyclerView recyclerView;

    private FirebaseDatabase mDatabase;

    List<Guest> mGuestList = new ArrayList<>();
    List<Guest> mSelectedList = new ArrayList<>();

    Table mTable = new Table();

    private GuestListAdapter mAdapter = new GuestListAdapter(this);

    @Override
    protected int getResId() {
        return R.layout.activity_table;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        setTitle(R.string.blank);

        recyclerView.setAdapter(mAdapter);

        //Firebase
        mDatabase = FirebaseDatabase.getInstance();
        initList();

        mChipsView.setChipsListener(new ChipsView.ChipsListener() {
            @Override
            public void onChipAdded(ChipsView.Chip chip) {
                Contact contact = chip.getContact();
                mTable.getPeople().add(contact.getFirstName());

                initList();

                if (mTable.getPeople().size() == MAX_GUESTS_PER_TABLE) {
                    recyclerView.setVisibility(View.GONE);
                }
            }

            @Override
            public void onChipDeleted(ChipsView.Chip chip) {
                Contact contact = chip.getContact();
                String key = contact.getFirstName();
                mTable.getPeople().remove(key);

                Guest toRemove = null;
                for (Guest guest : mSelectedList) {
                    if (guest.getKey().equals(key)) {
                        toRemove = guest;
                        break;
                    }
                }

                if (toRemove != null) {
                    mSelectedList.remove(toRemove);
                    initList();
                }

                if (recyclerView.getVisibility() == View.GONE) {
                    recyclerView.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onTextChanged(CharSequence text) {
                if (mTable.getPeople().size() == MAX_GUESTS_PER_TABLE) {
                    return;
                }

                if (!TextUtils.isEmpty(text)) {
                    if (mAdapter.getItemCount() == 0) {
                        initList();
                    }

                    mAdapter.getFilter().filter(text);
                } else {
                    //initList();
                }
            }
        });

        mTable.setPeople(new ArrayList<String>());
    }

    private void initList() {
        DatabaseReference dbRef = mDatabase.getReference(Constants.FIREBASE_LOCATION_GUESTS);
        dbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if (snapshot.getChildrenCount() == 0) {
                    finish();
                    return;
                }

                mGuestList = new ArrayList<>();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Guest guest = dataSnapshot.getValue(Guest.class);
                    guest.setKey(dataSnapshot.getKey());

                    if (guest.getTableNum() > 0) {
                        continue;
                    }

                    boolean there = false;
                    for (Guest g : mSelectedList) {
                        if (g.getKey().equals(guest.getKey())) {
                            there = true;
                        }
                    }

                    if (!there) {
                        mGuestList.add(guest);
                    }

                }

                mAdapter.setGuestList(mGuestList);
            }

            @Override
            public void onCancelled(DatabaseError firebaseError) {

            }
        });
    }

    @Override
    public void onItemSelected(Guest guest) {
        mSelectedList.add(guest);

        String display = guest.getFirstName() + " " + guest.getLastName();

        //Generate random Robot image :-)
        Uri url = Uri.parse("https://robohash.org/" + guest.getKey());
        Contact contact = new Contact(
                guest.getKey(), null,
                display,
                guest.getEmail(), null);
        mChipsView.addChip(display, url, contact);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_table, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_num) {
            showTableNumDialog();
            return true;
        } else if (item.getItemId() == R.id.action_save) {
            saveTable();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void showTableNumDialog() {
        View dialogView = LayoutInflater.from(this)
                .inflate(R.layout.dialog_table_num, null, false);

        TextInputLayout til = ButterKnife.findById(dialogView, R.id.table_num_til);
        final EditText editText = ButterKnife.findById(dialogView, R.id.edt_table_num);
        final CheckedTextView checkedTextView = ButterKnife.findById(dialogView, R.id.check_vip);
        checkedTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkedTextView.toggle();
                mTable.setVip(checkedTextView.isChecked());
            }
        });

        if (mTable.getNumber() != 0) {
            editText.setText(String.valueOf(mTable.getNumber()));
        }

        editText.addTextChangedListener(new TextInputWrappedWatcher(til));

        new AlertDialog.Builder(this)
                .setTitle(R.string.table_num)
                .setView(dialogView)
                .setPositiveButton(R.string.action_save, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (!TextUtils.isEmpty(editText.getText())) {
                            int tableNum = Integer.parseInt(editText.getText().toString());
                            mTable.setNumber(tableNum);

                            setTitle(getString(R.string.table_number, tableNum));
                        }
                    }
                })
                .setNegativeButton(R.string.action_cancel, null)
                .create().show();
    }

    private void saveTable() {
        if (mTable.getPeople().size() == 0) {
            Toast.makeText(this, "Please add Guests to the Table", Toast.LENGTH_LONG).show();
        } else if (mTable.getNumber() == 0) {
            showTableNumDialog();
        } else {
            Intent intent = new Intent();
            intent.putExtra(BundledExtras.DATA_OBJECT.name(), mTable);
            setResult(RESULT_OK, intent);
            finish();
        }
    }
}
