package com.tinashe.guestlist.ui.custom;

import android.support.design.widget.TextInputLayout;
import android.text.Editable;
import android.text.TextWatcher;

/**
 * Created by tinashe on 2016/02/08.
 */
public class TextInputWrappedWatcher implements TextWatcher {

    private TextInputLayout inputLayout;

    public TextInputWrappedWatcher(TextInputLayout inputLayout) {
        this.inputLayout = inputLayout;
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        inputLayout.setError(null);
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {

    }
}
