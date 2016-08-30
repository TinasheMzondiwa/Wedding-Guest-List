package com.tinashe.guestlist.ui;

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.app.ActivityOptions;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.android.gms.common.SignInButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.tinashe.guestlist.R;
import com.tinashe.guestlist.callbacks.MainController;
import com.tinashe.guestlist.model.Feed;
import com.tinashe.guestlist.model.Guest;
import com.tinashe.guestlist.model.Table;
import com.tinashe.guestlist.ui.adapters.TabsFragmentPagerAdapter;
import com.tinashe.guestlist.ui.adapters.ViewFilterListAdapter;
import com.tinashe.guestlist.ui.fragments.FeedLisFragment;
import com.tinashe.guestlist.ui.fragments.GuestListFragment;
import com.tinashe.guestlist.ui.fragments.TablesFragment;
import com.tinashe.guestlist.ui.transitions.FabDialogMorphSetup;
import com.tinashe.guestlist.utils.AnimUtil;
import com.tinashe.guestlist.utils.Constants;
import com.tinashe.guestlist.utils.FeedUtil;
import com.tinashe.guestlist.utils.GuestUtil;
import com.tinashe.guestlist.utils.PermissionUtil;
import com.tinashe.guestlist.utils.SnackBarUtil;
import com.tinashe.guestlist.utils.VersionUtils;
import com.tinashe.guestlist.utils.enums.BundledExtras;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class MainActivity extends BaseActivity implements MainController, ViewFilterListAdapter.FilterCallback,
        NavigationView.OnNavigationItemSelectedListener {

    private static final String TAG = MainActivity.class.getName();
    private static final int REQUEST_CONTACTS = 123;
    private static final int REQUEST_POST_FEED = 124;

    @Bind(R.id.toolbar)
    Toolbar toolbar;

    @Bind(R.id.filters)
    RecyclerView filterList;

    @Bind(R.id.fab)
    FloatingActionButton fab;

    @Bind(R.id.drawer_layout)
    DrawerLayout mDrawer;

    @Bind(R.id.nav_view)
    NavigationView mNavView;

    @Bind(R.id.tabs)
    TabLayout tabLayout;

    @Bind(R.id.view_pager)
    ViewPager viewPager;

    private FirebaseDatabase mDatabase;

    private TabsFragmentPagerAdapter mTabAdapter;

    private ViewFilterListAdapter mFilterAdapter = new ViewFilterListAdapter(this);

    private List<Guest> mGuestList;
    private List<Table> mTableList;
    private List<Feed> mFeedList;

    private boolean permissionGranted = false;

    private int mContentViewHeight;

    private boolean mDoneAnimating = false;

    @Override
    protected int getResId() {
        return R.layout.activity_main;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle(R.string.blank);

        //UI
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, mDrawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        mDrawer.addDrawerListener(toggle);
        toggle.syncState();

        mDrawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED, filterList);

        upDateProfileUi();

        filterList.setAdapter(mFilterAdapter);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (viewPager.getCurrentItem() == 0) {
                    startActivityForResult(
                            new Intent(MainActivity.this, GuestActivity.class),
                            GuestListFragment.REQUEST_ADD_GUEST);
                } else if (viewPager.getCurrentItem() == 1) {
                    startActivityForResult(
                            new Intent(MainActivity.this, TableActivity.class),
                            TablesFragment.REQUEST_ADD_TABLE);

                } else if (viewPager.getCurrentItem() == 3) {
                    showPostDialog();
                }
            }
        });
        mNavView.setNavigationItemSelectedListener(this);

        mTabAdapter = new TabsFragmentPagerAdapter(getSupportFragmentManager(), this);
        viewPager.setAdapter(mTabAdapter);
        tabLayout.setupWithViewPager(viewPager);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(final int position) {
                if (isAdmin()) {
                    switch (position) {
                        case 0:
                            fab.setImageResource(R.drawable.ic_person_add);
                            fab.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    fab.show();
                                }
                            }, 200);
                            break;
                        case 1:
                            fab.setImageResource(R.drawable.ic_group_add);
                            fab.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    fab.show();
                                }
                            }, 200);
                            break;
                        case 2:
                            fab.hide();
                            break;
                        case 3:
                            fab.setImageResource(R.drawable.ic_create);
                            fab.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    fab.show();
                                }
                            }, 200);
                            break;
                    }
                } else {
                    if (position == 3) {
                        fab.setImageResource(R.drawable.ic_create);
                        fab.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                fab.show();
                            }
                        }, 200);
                    } else {
                        fab.hide();
                    }
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        fab.hide();
        toolbar.getViewTreeObserver().addOnPreDrawListener(
                new ViewTreeObserver.OnPreDrawListener() {
                    @Override
                    public boolean onPreDraw() {
                        toolbar.getViewTreeObserver().removeOnPreDrawListener(this);
                        final int widthSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
                        final int heightSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);

                        toolbar.measure(widthSpec, heightSpec);
                        mContentViewHeight = toolbar.getHeight();
                        collapseToolbar();
                        return true;
                    }
                });


        //Firebase
        mDatabase = FirebaseDatabase.getInstance();

        readGuestList();
        readTableList();
        readFeedList();
    }

    private void collapseToolbar() {
        int toolBarHeight;
        TypedValue tv = new TypedValue();
        getTheme().resolveAttribute(android.R.attr.actionBarSize, tv, true);
        toolBarHeight = TypedValue.complexToDimensionPixelSize(
                tv.data, getResources().getDisplayMetrics());

        ValueAnimator valueHeightAnimator = ValueAnimator
                .ofInt(mContentViewHeight, toolBarHeight);

        valueHeightAnimator.addUpdateListener(
                new ValueAnimator.AnimatorUpdateListener() {

                    @Override
                    public void onAnimationUpdate(ValueAnimator animation) {
                        ViewGroup.LayoutParams lp = toolbar.getLayoutParams();
                        lp.height = (Integer) animation.getAnimatedValue();
                        toolbar.setLayoutParams(lp);
                    }
                });

        valueHeightAnimator.start();
        valueHeightAnimator.addListener(
                new AnimatorListenerAdapter() {

                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);

                        if (getGuestFragment() != null && mGuestList != null) {
                            getGuestFragment().setGuestList(
                                    GuestUtil.filter(mGuestList, mFilterAdapter.getCheckedItems())
                            );

                            AnimUtil.slideInEnterAnimation(MainActivity.this, viewPager, new com.nineoldandroids.animation.AnimatorListenerAdapter() {
                                @Override
                                public void onAnimationEnd(com.nineoldandroids.animation.Animator animation) {
                                    super.onAnimationEnd(animation);
                                    if (isAdmin()) {
                                        fab.show();
                                    }
                                }
                            });
                        } else {
                            if (isAdmin()) {
                                fab.show();
                            }
                        }


                        mDoneAnimating = true;

                    }
                });
    }

    @Override
    protected void upDateProfileUi() {
        super.upDateProfileUi();

        View header = mNavView.getHeaderView(0);
        SignInButton signInButton = ButterKnife.findById(header, R.id.sign_in_button);
        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signIn();
            }
        });

        if (!isLoggedIn()) {
            signInButton.setVisibility(View.VISIBLE);
            ButterKnife.findById(header, R.id.signed_in_view)
                    .setVisibility(View.GONE);

            signInButton.setSize(SignInButton.SIZE_STANDARD);
            signInButton.setScopes(mGso.getScopeArray());
            return;
        }

        signInButton.setVisibility(View.GONE);
        ButterKnife.findById(header, R.id.signed_in_view)
                .setVisibility(View.VISIBLE);

        ImageView profile = ButterKnife.findById(header, R.id.profile_img);

        TextView name = ButterKnife.findById(header, R.id.profile_name);
        TextView email = ButterKnife.findById(header, R.id.profile_email);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            return;
        }
        name.setText(user.getDisplayName());
        email.setText(user.getEmail());

        Glide.with(this)
                .load(user.getPhotoUrl())
                .placeholder(R.drawable.ic_account_circle)
                .error(R.drawable.ic_account_circle)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .dontAnimate()
                .into(profile);

        if (isAdmin() && !fab.isShown()) {
            fab.show();
        }
    }

    private void updateSummaryDetails(List<Guest> guestList) {

        View header = mNavView.getHeaderView(0);

        TextView total = ButterKnife.findById(header, R.id.txt_guests_count);
        total.setText(String.valueOf(guestList.size()));

        TextView adults = ButterKnife.findById(header, R.id.txt_adults_count);
        adults.setText(String.valueOf(GuestUtil.adultsCount(guestList)));

        TextView kids = ButterKnife.findById(header, R.id.txt_kids_count);
        kids.setText(String.valueOf(GuestUtil.childrenCount(guestList)));

        TextView family = ButterKnife.findById(header, R.id.txt_family_count);
        family.setText(String.valueOf(GuestUtil.familyCount(guestList)));

        TextView transport = ButterKnife.findById(header, R.id.txt_transport_count);
        transport.setText(String.valueOf(GuestUtil.needTransportCount(guestList)));

        TextView vegan = ButterKnife.findById(header, R.id.txt_vegan_count);
        vegan.setText(String.valueOf(GuestUtil.veganCount(guestList)));
    }

    @Override
    public boolean isPermissionGranted() {
        return permissionGranted;
    }

    @Override
    public void hideFab() {
        fab.hide();
    }

    @Override
    public void showFab() {
        fab.show();
    }

    @Override
    public List<Guest> getGuestList() {
        return mGuestList;
    }

    @Override
    public List<Table> getTablesList() {
        return mTableList;
    }

    @Override
    public List<Feed> getFeedList() {
        return mFeedList;
    }

    @Override
    public void readGuestList() {
        DatabaseReference databaseRef = mDatabase.getReference(Constants.FIREBASE_LOCATION_GUESTS);
        databaseRef.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot snapshot) {

                if (snapshot.getChildrenCount() == 0) {
                    Log.d(TAG, "Your Guest list is Empty...");
                    if (getGuestFragment() != null) {
                        getGuestFragment().showEmptyView();
                    }
                    return;
                }

                mGuestList = new ArrayList<>();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Guest guest = dataSnapshot.getValue(Guest.class);
                    guest.setKey(dataSnapshot.getKey());
                    mGuestList.add(guest);
                }

                if (getGuestFragment() != null && getTablesFragment() != null && mDoneAnimating) {
                    getGuestFragment().setGuestList(
                            GuestUtil.filter(mGuestList, mFilterAdapter.getCheckedItems())
                    );
                    getTablesFragment().setGuestList(mGuestList);
                }


                updateSummaryDetails(mGuestList);

            }

            @Override
            public void onCancelled(DatabaseError error) {
                Log.e(TAG, "ERROR: " + error.toString());
                if (mGuestList.isEmpty() && getGuestFragment() != null) {
                    getGuestFragment().showEmptyView();
                }
            }

        });
    }

    @Nullable
    private GuestListFragment getGuestFragment() {
        return (GuestListFragment) mTabAdapter.getRegisteredFragment(0);
    }

    @Nullable
    private TablesFragment getTablesFragment() {
        return (TablesFragment) mTabAdapter.getRegisteredFragment(1);
    }

    @Nullable
    private FeedLisFragment getFeedFragment() {
        return (FeedLisFragment) mTabAdapter.getRegisteredFragment(3);
    }

    @Override
    public void addGuest(Guest guest) {
        DatabaseReference pushRef = mDatabase.getReference(Constants.FIREBASE_LOCATION_GUESTS);
        pushRef.push().setValue(guest);

        addFeed(FeedUtil.buildGuestAddedFeed(guest));
    }

    @Override
    public void editGuest(Guest guest) {
        DatabaseReference pushRef = mDatabase.getReference(Constants.FIREBASE_LOCATION_GUESTS);
        pushRef.child(guest.getKey()).setValue(guest);
    }

    @Override
    public void deleteGuest(Guest guest) {
        DatabaseReference pushRef = mDatabase.getReference(Constants.FIREBASE_LOCATION_GUESTS);
        pushRef.child(guest.getKey()).setValue(null);
    }

    @Override
    public void readTableList() {
        DatabaseReference databaseRef = mDatabase.getReference(Constants.FIREBASE_LOCATION_TABLES);
        databaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getChildrenCount() == 0) {
                    if (getTablesFragment() != null) {
                        getTablesFragment().showEmptyView();
                    }
                    return;
                }

                mTableList = new ArrayList<>();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Table table = snapshot.getValue(Table.class);
                    table.setKey(snapshot.getKey());
                    mTableList.add(table);
                }

                Collections.sort(mTableList);

                if (getTablesFragment() != null) {
                    getTablesFragment().setTableList(mTableList);
                    getTablesFragment().setGuestList(mGuestList);
                }

            }

            @Override
            public void onCancelled(DatabaseError firebaseError) {

            }
        });
    }

    @Override
    public void addTable(Table table) {
        DatabaseReference pushRef = mDatabase.getReference(Constants.FIREBASE_LOCATION_TABLES);
        pushRef.push().setValue(table);

        for (String key : table.getPeople()) {
            Guest guest = GuestUtil.findGuest(mGuestList, key);

            if (guest != null) {
                guest.setTableNum(table.getNumber());
                editGuest(guest);
            }
        }
    }

    @Override
    public void editTable(Table table) {
        DatabaseReference pushRef = mDatabase.getReference(Constants.FIREBASE_LOCATION_TABLES);
        pushRef.child(table.getKey()).setValue(table);
    }

    @Override
    public void deleteTable(Table table) {

        for (String key : table.getPeople()) {
            Guest guest = GuestUtil.findGuest(mGuestList, key);
            if (guest != null) {
                guest.setTableNum(0);
                editGuest(guest);
            }
        }

        DatabaseReference pushRef = mDatabase.getReference(Constants.FIREBASE_LOCATION_TABLES);
        pushRef.child(table.getKey()).setValue(null);
    }

    @Override
    public void readFeedList() {
        DatabaseReference databaseRef = mDatabase.getReference(Constants.FIREBASE_LOCATION_FEED);
        databaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                mFeedList = new ArrayList<>();

                if (dataSnapshot.getChildrenCount() == 0) {
                    if (getFeedFragment() != null) {
                        getFeedFragment().showEmptyView();
                    }
                    return;
                }

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Feed feed = snapshot.getValue(Feed.class);
                    feed.setKey(snapshot.getKey());
                    mFeedList.add(feed);
                }

                Collections.sort(mFeedList);

                if (getFeedFragment() != null) {
                    getFeedFragment().setFeedList(mFeedList);
                }
            }

            @Override
            public void onCancelled(DatabaseError firebaseError) {

            }
        });
    }

    @Override
    public void addFeed(Feed feed) {
        DatabaseReference pushRef = mDatabase.getReference(Constants.FIREBASE_LOCATION_FEED);
        pushRef.push().setValue(feed);
    }

    @Override
    public void deleteFeed(Feed feed) {
        DatabaseReference deleteRef = mDatabase.getReference(Constants.FIREBASE_LOCATION_FEED);
        deleteRef.child(feed.getKey()).setValue(null);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (viewPager.getCurrentItem() == 0 && getGuestFragment() != null) {
            getGuestFragment().onActivityResult(requestCode, resultCode, data);
        } else if (viewPager.getCurrentItem() == 1 && getTablesFragment() != null) {
            getTablesFragment().onActivityResult(requestCode, resultCode, data);
        } else if (requestCode == REQUEST_POST_FEED && resultCode == RESULT_OK && data != null) {
            Feed feed = (Feed) data.getSerializableExtra(BundledExtras.DATA_OBJECT.name());
            addFeed(feed);
        }

    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_filter:
                if (mDrawer.isDrawerOpen(GravityCompat.END)) {
                    mDrawer.closeDrawer(GravityCompat.END);
                } else {
                    mDrawer.openDrawer(GravityCompat.END);
                }
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {

        if (mDrawer.isDrawerOpen(GravityCompat.START) || mDrawer.isDrawerOpen(GravityCompat.END)) {
            mDrawer.closeDrawers();
        } else {
            super.onBackPressed();
        }

    }

    @Override
    public void updateFilteredList() {
        if (getGuestFragment() != null) {
            getGuestFragment().setGuestList(
                    GuestUtil.filter(mGuestList, mFilterAdapter.getCheckedItems())
            );
        }

    }

    private void showPostDialog() {
        if (!isLoggedIn()) {
            Toast toast = Toast.makeText(this, "Sign in to Post", Toast.LENGTH_LONG);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();

            mDrawer.openDrawer(GravityCompat.START);
            return;
        }
        Intent post = new Intent(this, PostActivity.class);
        if (VersionUtils.isAtLeastL()) {
            post.putExtra(FabDialogMorphSetup.EXTRA_SHARED_ELEMENT_START_COLOR, ContextCompat.getColor
                    (this, R.color.colorAccent));
            ActivityOptions options =
                    ActivityOptions.makeSceneTransitionAnimation(this, fab,
                            getString(R.string.transition_dribbble_login));
            startActivityForResult(post, REQUEST_POST_FEED, options.toBundle());
        } else {
            startActivityForResult(post, REQUEST_POST_FEED);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS)
                != PackageManager.PERMISSION_GRANTED) {

            requestContactPermissions();
        } else {
            permissionGranted = true;
        }
    }


    private void requestContactPermissions() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.READ_CONTACTS)) {
            // Provide an additional rationale to the user if the permission was not granted
            // and the user would benefit from additional context for the use of the permission.
            // For example if the user has previously denied the permission.
            Log.i(TAG,
                    "Displaying Contacts permission rationale to provide additional context.");
            SnackBarUtil.make(mDrawer, R.string.permission_contacts_rationale,
                    Snackbar.LENGTH_INDEFINITE)
                    .setAction(R.string.action_ok, new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            ActivityCompat.requestPermissions(MainActivity.this,
                                    new String[]{Manifest.permission.READ_CONTACTS},
                                    REQUEST_CONTACTS);
                        }
                    })
                    .show();
        } else {

            // Contacts permission has not been granted yet. Request it directly.
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_CONTACTS},
                    REQUEST_CONTACTS);
        }
    }

    /**
     * Callback received when a permissions request has been completed.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {

        if (requestCode == REQUEST_CONTACTS) {
            Log.i(TAG, "Received response for Contacts permissions request.");

            // If we have requested multiple permissions all of them need to be checked.
            if (PermissionUtil.verifyPermissions(grantResults)) {
                permissionGranted = true;
            } else {
                Log.i(TAG, "Contacts permissions were NOT granted.");
                SnackBarUtil.show(mDrawer, R.string.permissions_not_granted,
                        Snackbar.LENGTH_SHORT);
            }

        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }

    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.nav_facebook:
                launchWebView("https://www.facebook.com/events/1083729291648500/");
                break;
            case R.id.nav_registry:
                launchWebView("http://goo.gl/PF94YA");
                break;
        }
        return true;
    }
}
