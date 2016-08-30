package com.tinashe.guestlist.ui.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;

import com.nineoldandroids.view.ViewHelper;
import com.nineoldandroids.view.ViewPropertyAnimator;

public abstract class RecyclerViewSlidingAdapter<T extends RecyclerView.ViewHolder> extends RecyclerView.Adapter<T> {
    protected Context mContext;
    private int mLastAnimatedPosition = -1;

    protected RecyclerViewSlidingAdapter(Context mContext) {
        this.mContext = mContext;
    }

    @Override
    public abstract T onCreateViewHolder(ViewGroup parent, int position);

    @Override
    public abstract int getItemCount();

    @Override
    public void onBindViewHolder(T holder, int position) {
        if (position > mLastAnimatedPosition) {
            mLastAnimatedPosition = position;

            slideInEnterAnimation(mContext, holder.itemView);
        }
    }

    public void resetAnimation() {
        mLastAnimatedPosition = -1;
    }

    private void slideInEnterAnimation(Context context, View view) {
        DisplayMetrics display = context.getResources().getDisplayMetrics();
        int screenHeight = display.heightPixels;

        ViewHelper.setTranslationY(view, screenHeight);
        ViewPropertyAnimator
                .animate(view)
                .translationY(0)
                .setDuration(700)
                .setInterpolator(new DecelerateInterpolator(3f))
                .start();
    }

}
