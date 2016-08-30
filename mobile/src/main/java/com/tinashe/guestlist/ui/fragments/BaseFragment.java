package com.tinashe.guestlist.ui.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.tinashe.guestlist.ui.BaseActivity;

import butterknife.ButterKnife;

/**
 * Created by tinashe on 2015/11/09.
 */
public abstract class BaseFragment extends Fragment {

    protected abstract int getLayoutResId();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(getLayoutResId(), container, false);
        ButterKnife.bind(this, rootView);
        return rootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initialize();
    }

    protected void initialize() {
    }

    protected BaseActivity getBaseActivity() {
        return (BaseActivity) getActivity();
    }
}
