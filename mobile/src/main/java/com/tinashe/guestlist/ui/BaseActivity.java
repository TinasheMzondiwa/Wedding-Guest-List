package com.tinashe.guestlist.ui;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.customtabs.CustomTabsIntent;
import android.support.v4.app.NavUtils;
import android.support.v4.app.TaskStackBuilder;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.tinashe.guestlist.BuildConfig;
import com.tinashe.guestlist.R;
import com.tinashe.guestlist.utils.customtabs.CustomTabActivityHelper;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by tinashe on 2016/02/08.
 */
public abstract class BaseActivity extends AppCompatActivity implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    private static final String TAG = BaseActivity.class.getName();
    private static final int RC_GOOGLE_LOGIN = 874;
    private final CustomTabActivityHelper.ConnectionCallback customTabConnect
            = new CustomTabActivityHelper.ConnectionCallback() {

        @Override
        public void onCustomTabsConnected() {

        }

        @Override
        public void onCustomTabsDisconnected() {
        }
    };
    protected GoogleSignInOptions mGso;
    protected CustomTabActivityHelper customTab;

    @Nullable
    @Bind(R.id.toolbar)
    Toolbar toolbar;

    /* Client used to interact with Google APIs. */
    private GoogleApiClient mGoogleApiClient;

    public static void navigateUpOrBack(Activity currentActivity,
                                        Class<? extends Activity> syntheticParentActivity) {
        // Retrieve parent activity from AndroidManifest.
        Intent intent = NavUtils.getParentActivityIntent(currentActivity);

        // Synthesize the parent activity when a natural one doesn't exist.
        if (intent == null && syntheticParentActivity != null) {
            try {
                intent = NavUtils.getParentActivityIntent(currentActivity, syntheticParentActivity);
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }
        }

        if (intent == null) {
            // No parent defined in manifest. This indicates the activity may be used by
            // in multiple flows throughout the app and doesn't have a strict parent. In
            // this case the navigation up button should act in the same manner as the
            // back button. This will result in users being forwarded back to other
            // applications if currentActivity was invoked from another application.
            currentActivity.onBackPressed();
        } else {
            if (NavUtils.shouldUpRecreateTask(currentActivity, intent)) {
                // Need to synthesize a backstack since currentActivity was probably invoked by a
                // different app. The preserves the "Up" functionality within the app according to
                // the activity hierarchy defined in AndroidManifest.xml via parentActivity
                // attributes.
                TaskStackBuilder builder = TaskStackBuilder.create(currentActivity);
                builder.addNextIntentWithParentStack(intent);
                builder.startActivities();
            } else {
                // Navigate normally to the manifest defined "Up" activity.
                NavUtils.navigateUpTo(currentActivity, intent);
            }
        }
    }

    protected abstract int getResId();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getResId());
        ButterKnife.bind(this);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
        }

        customTab = new CustomTabActivityHelper();
        customTab.setConnectionCallback(customTabConnect);


        mGso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .requestProfile()
                .build();
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, mGso)
                .build();

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                navigateUpOrBack(BaseActivity.this, null);
                return true;
            case R.id.action_logout:
                signOut();
                return true;
            case R.id.action_version:
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse("https://play.google.com/store/apps/details?id=" + BuildConfig.APPLICATION_ID));
                startActivity(intent);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.d(TAG, "onConnectionSuspended() " + i);
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.d(TAG, "onConnectionFailed() " + connectionResult.toString());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu_main, menu);
        menu.findItem(R.id.action_version).setTitle(
                "Version: " + BuildConfig.VERSION_NAME
        );

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        menu.findItem(R.id.action_logout)
                .setVisible(isLoggedIn());
        return super.onPrepareOptionsMenu(menu);
    }

    protected void signIn() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_GOOGLE_LOGIN);
    }

    protected void signOut() {
        Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(
                new ResultCallback<Status>() {
                    @Override
                    public void onResult(Status status) {

                        upDateProfileUi();

                    }
                });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_GOOGLE_LOGIN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleSignInResult(result);
        }
    }

    private void handleSignInResult(GoogleSignInResult result) {
        Log.d(TAG, "handleSignInResult:" + result.isSuccess());
        if (result.isSuccess()) {
            // Signed in successfully, show authenticated UI.
            GoogleSignInAccount acct = result.getSignInAccount();
            //Use acct

            upDateProfileUi();

        } else {
            // Signed out, show unauthenticated UI.
            //updateUI(false);
            Log.d(TAG, "Signed out");
        }
    }

    protected void upDateProfileUi() {
        supportInvalidateOptionsMenu();
    }

    public boolean isAdmin() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            return false;
        }
        String email = user.getEmail();

        for (String em : getResources().getStringArray(R.array.admin_list)) {
            if (em.equalsIgnoreCase(email)) {
                return true;
            }
        }

        return false;
    }

    public boolean isLoggedIn() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        return user != null;
    }

    @Override
    protected void onStart() {
        super.onStart();
        customTab.bindCustomTabsService(this);
    }

    @Override
    protected void onStop() {
        customTab.unbindCustomTabsService(this);
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        customTab.setConnectionCallback(null);
        super.onDestroy();
    }

    public void launchWebView(String url) {
        CustomTabsIntent intent = new CustomTabsIntent.Builder(customTab.getSession())
                .setToolbarColor(ContextCompat.getColor(this, R.color.colorPrimary))
                .setShowTitle(true)
                .enableUrlBarHiding()
                .setStartAnimations(this, R.anim.slide_up, android.R.anim.fade_out)
                .setExitAnimations(this, android.R.anim.fade_in, R.anim.slide_down)
                .build();
        CustomTabActivityHelper.openCustomTab(BaseActivity.this, intent, Uri.parse(url));
    }
}
