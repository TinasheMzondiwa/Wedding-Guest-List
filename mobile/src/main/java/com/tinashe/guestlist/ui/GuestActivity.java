package com.tinashe.guestlist.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckedTextView;
import android.widget.EditText;
import android.widget.Spinner;

import com.google.firebase.database.ServerValue;
import com.tinashe.guestlist.R;
import com.tinashe.guestlist.model.Guest;
import com.tinashe.guestlist.ui.custom.TextInputWrappedWatcher;
import com.tinashe.guestlist.utils.Constants;
import com.tinashe.guestlist.utils.enums.BundledExtras;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import butterknife.Bind;
import butterknife.OnClick;

public class GuestActivity extends BaseActivity {

    @Bind(R.id.title_spinner)
    Spinner spinner;

    @Bind(R.id.til_first_name)
    TextInputLayout tilFirstName;
    @Bind(R.id.til_last_name)
    TextInputLayout tilLastName;
    @Bind(R.id.til_email)
    TextInputLayout tilEmail;
    @Bind(R.id.til_phone)
    TextInputLayout tilPhone;

    @Bind(R.id.edt_first_name)
    EditText firstName;
    @Bind(R.id.edt_lastname)
    EditText lastName;
    @Bind(R.id.edt_email)
    EditText email;
    @Bind(R.id.edt_phone)
    EditText phone;

    @Bind(R.id.check_child)
    CheckedTextView checkChild;
    @Bind(R.id.check_family)
    CheckedTextView checkFamily;
    @Bind(R.id.check_transport)
    CheckedTextView checkTransport;
    @Bind(R.id.check_vegan)
    CheckedTextView checkVegan;

    @Bind(R.id.btn_submit)
    Button submit;

    private List<String> TITLES;

    private Guest newGuest = new Guest();

    private boolean mEdit = false;

    @Override
    protected int getResId() {
        return R.layout.activity_guest;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        TITLES = Arrays.asList(getResources().getStringArray(R.array.title_options));

        if (getIntent().hasExtra(BundledExtras.DATA_OBJECT.name())) {
            newGuest = (Guest) getIntent().getSerializableExtra(BundledExtras.DATA_OBJECT.name());
            setTitle(newGuest.getFirstName() + " " + newGuest.getLastName());
            if (newGuest.getTableNum() != 0) {
                getSupportActionBar().setSubtitle(getString(R.string.table_number, newGuest.getTableNum()));
            }

            populateUi();

            mEdit = isAdmin();
        }

        initUi();

    }

    private void initUi() {

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.title_options, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        if (!isAdmin()) {
            spinner.setClickable(false);
            spinner.setEnabled(false);
        }
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                if (isAdmin()) {
                    newGuest.setTitle(TITLES.get(position));
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        firstName.addTextChangedListener(new TextInputWrappedWatcher(tilFirstName));
        lastName.addTextChangedListener(new TextInputWrappedWatcher(tilLastName));
        email.addTextChangedListener(new TextInputWrappedWatcher(tilEmail));
        phone.addTextChangedListener(new TextInputWrappedWatcher(tilPhone));
    }

    private void populateUi() {
        if (!TextUtils.isEmpty(newGuest.getTitle())) {

            spinner.post(new Runnable() {
                @Override
                public void run() {
                    spinner.setSelection(TITLES.indexOf(newGuest.getTitle()));
                }
            });

        }

        firstName.setText(newGuest.getFirstName());
        lastName.setText(newGuest.getLastName());
        email.setText(newGuest.getEmail());
        phone.setText(newGuest.getPhone());

        if (!isAdmin()) {
            tilFirstName.setCounterEnabled(false);
            tilLastName.setCounterEnabled(false);

            disableEditView(firstName);
            disableEditView(lastName);
            disableEditView(email);
            disableEditView(phone);

            submit.setVisibility(View.GONE);
        }

        checkChild.setChecked(newGuest.isChild());
        checkFamily.setChecked(newGuest.isFamily());
        checkTransport.setChecked(newGuest.isNeedsTransport());
        checkVegan.setChecked(newGuest.isVegan());


    }

    private void disableEditView(EditText editText) {
        editText.setFocusable(false);
        editText.setFocusableInTouchMode(false);
    }


    @OnClick({R.id.check_child, R.id.check_family, R.id.check_transport, R.id.check_vegan})
    public void onCheckClick(CheckedTextView textView) {
        if (!isAdmin()) {
            return;
        }

        textView.toggle();

        newGuest.setChild(checkChild.isChecked());
        newGuest.setNeedsTransport(checkTransport.isChecked());
        newGuest.setFamily(checkFamily.isChecked());
        newGuest.setVegan(checkVegan.isChecked());
    }

    @OnClick(R.id.btn_submit)
    public void submitGuest(Button button) {
        if (isValidName() && !isPhoneRequired()) {
            submitGuest();
        }
    }


    private boolean isValidName() {
        if (TextUtils.isEmpty(firstName.getText())) {
            tilFirstName.setError(getString(R.string.default_input_error));
            return false;
        }

        if (TextUtils.isEmpty(lastName.getText())) {
            tilLastName.setError(getString(R.string.default_input_error));
            return true;
        }
        return true;
    }

    private boolean isValidEmail() {
        if (TextUtils.isEmpty(email.getText()) && !(Patterns.EMAIL_ADDRESS.matcher(email.getText()).matches())) {
            tilEmail.setError(getString(R.string.default_input_error));
            return false;
        } else {
            return true;
        }

    }

    private boolean isPhoneRequired() {
        if (checkTransport.isChecked() && TextUtils.isEmpty(phone.getText())) {
            tilPhone.setError(getString(R.string.error_phone_required));
            return true;
        } else {
            return false;
        }
    }

    private void submitGuest() {
        newGuest.setFirstName(firstName.getText().toString());
        newGuest.setLastName(lastName.getText().toString());
        newGuest.setEmail(email.getText().toString());
        newGuest.setPhone(TextUtils.isEmpty(phone.getText()) ? "" : phone.getText().toString());
        HashMap<String, Object> timestamp = new HashMap<>();
        timestamp.put(Constants.FIREBASE_PROPERTY_TIMESTAMP, ServerValue.TIMESTAMP);
        newGuest.setTimestampCreated(timestamp);

        Intent intent = new Intent();
        intent.putExtra(BundledExtras.DATA_OBJECT.name(), newGuest);
        setResult(RESULT_OK, intent);
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_delete, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        if (!(isAdmin() && mEdit)) {
            menu.findItem(R.id.action_delete).setVisible(false);
        }
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_delete) {
            Intent intent = new Intent();
            intent.putExtra(BundledExtras.DATA_OBJECT.name(), newGuest);
            setResult(RESULT_CANCELED, intent);
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
}
