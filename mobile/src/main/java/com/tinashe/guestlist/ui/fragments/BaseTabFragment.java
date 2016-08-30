package com.tinashe.guestlist.ui.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.tinashe.guestlist.R;
import com.tinashe.guestlist.callbacks.MainController;
import com.tinashe.guestlist.ui.MainActivity;

import butterknife.Bind;
import xyz.danoz.recyclerviewfastscroller.sectionindicator.title.SectionTitleIndicator;
import xyz.danoz.recyclerviewfastscroller.vertical.VerticalRecyclerViewFastScroller;


/**
 * Created by tinashe on 2015/11/09.
 */
public abstract class BaseTabFragment extends BaseFragment {

    protected MainController controller;

    @Bind(R.id.fast_scroller)
    VerticalRecyclerViewFastScroller fastScroller;

    @Bind(R.id.fast_scroller_section_title_indicator)
    SectionTitleIndicator sectionTitleIndicator;

    @Bind(R.id.recyclerview)
    RecyclerView recyclerView;

    @Bind(R.id.progress)
    ProgressBar progressBar;

    @Bind(android.R.id.empty)
    TextView emptyView;

    @Override
    protected int getLayoutResId() {
        return R.layout.fragment_list;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onAttach(Context activity) {
        super.onAttach(activity);

        try {
            controller = (MainController) activity;
        } catch (ClassCastException cce) {
            throw new ClassCastException("Implement " + MainController.class.getName());
        }
    }

    @Override
    protected void initialize() {
        super.initialize();

        recyclerView.setHasFixedSize(true);
    }

    public void showEmptyView() {
        progressBar.setVisibility(View.GONE);
        emptyView.setVisibility(View.VISIBLE);
    }

    protected MainActivity getMainActivity() {
        return (MainActivity) getActivity();
    }

    protected boolean isAdmin() {
        return getMainActivity().isAdmin();
    }
}
