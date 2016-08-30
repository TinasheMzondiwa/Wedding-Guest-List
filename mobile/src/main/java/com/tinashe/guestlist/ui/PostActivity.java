package com.tinashe.guestlist.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v7.widget.SwitchCompat;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.tinashe.guestlist.R;
import com.tinashe.guestlist.model.Feed;
import com.tinashe.guestlist.ui.custom.TextInputWrappedWatcher;
import com.tinashe.guestlist.ui.transitions.FabDialogMorphSetup;
import com.tinashe.guestlist.utils.Constants;
import com.tinashe.guestlist.utils.VersionUtils;
import com.tinashe.guestlist.utils.enums.BundledExtras;

import java.util.Calendar;

import butterknife.Bind;

/**
 * Created by tinashe on 2016/02/18.
 */
public class PostActivity extends BaseActivity {

    @Bind(R.id.container)
    ViewGroup container;

    @Bind(R.id.user_img)
    ImageView userImg;

    @Bind(R.id.user_name)
    TextView userName;

    @Bind(R.id.til_post)
    TextInputLayout tilPost;

    @Bind(R.id.edt_post)
    EditText editPost;

    @Bind(R.id.post)
    Button btnPost;

    @Bind(R.id.check_exit)
    ImageView checkExit;

    @Bind(R.id.switch_admin)
    SwitchCompat switchAdmin;

    boolean isDismissing = false;

    private FirebaseUser user;

    @Override
    protected int getResId() {
        return R.layout.activity_post;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (VersionUtils.isAtLeastL()) {
            FabDialogMorphSetup.setupSharedEelementTransitions(this, container,
                    getResources().getDimensionPixelSize(R.dimen.dialog_corners));
        }

        user = FirebaseAuth.getInstance().getCurrentUser();


        Glide.with(this)
                .load(user.getPhotoUrl())
                .placeholder(R.drawable.ic_account_circle)
                .error(R.drawable.ic_account_circle)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .dontAnimate()
                .into(userImg);
        userName.setText(user.getDisplayName());

        editPost.addTextChangedListener(new TextInputWrappedWatcher(tilPost));

        btnPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(editPost.getText())) {
                    tilPost.setError("Text is required");
                } else {
                    Feed feed = new Feed();
                    feed.setOwnerKey(user.getEmail());
                    feed.setVisible(true);
                    feed.setOwner(getFeedOwner());
                    feed.setOwnerUrl(switchAdmin.isChecked() ? Constants.COUPLE_PHOTO : user.getPhotoUrl() == null ? "" : user.getPhotoUrl().toString());
                    feed.setDisplay(editPost.getText().toString());
                    feed.setTimestampCreated(Calendar.getInstance().getTimeInMillis());

                    Intent intent = new Intent();
                    intent.putExtra(BundledExtras.DATA_OBJECT.name(), feed);
                    setResult(RESULT_OK, intent);

                    if (VersionUtils.isAtLeastL()) {
                        finishAfterTransition();
                    } else {
                        finish();
                    }
                }
            }
        });


        if (isAdmin()) {
            checkExit.setVisibility(View.GONE);
            switchAdmin.setVisibility(View.VISIBLE);

            switchAdmin.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    userName.setText(isChecked ? getString(R.string.app_name) : user.getDisplayName());

                    Glide.with(PostActivity.this)
                            .load(isChecked ? Constants.COUPLE_PHOTO : user.getPhotoUrl() == null ? "" : user.getPhotoUrl().toString())
                            .placeholder(R.drawable.ic_account_circle)
                            .error(R.drawable.ic_account_circle)
                            .diskCacheStrategy(DiskCacheStrategy.ALL)
                            .dontAnimate()
                            .into(userImg);
                }
            });
        }
    }

    private String getFeedOwner() {
        String name = userName.getText().toString();

        if (isAdmin() && !switchAdmin.isChecked()) {
            return name.contains(" ") ? name.substring(0, name.indexOf(" ")) : name;
        } else {
            return name;
        }
    }

    public void dismiss(View view) {
        isDismissing = true;
        setResult(Activity.RESULT_CANCELED);

        if (VersionUtils.isAtLeastL()) {
            finishAfterTransition();
        } else {
            finish();
        }

    }

    @Override
    public void onBackPressed() {
        dismiss(null);
    }
}
