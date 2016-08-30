package com.tinashe.guestlist.ui.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.tinashe.guestlist.R;
import com.tinashe.guestlist.callbacks.FeedListCallback;
import com.tinashe.guestlist.model.Feed;
import com.tinashe.guestlist.utils.FeedUtil;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by tinashe on 2016/02/17.
 */
public class FeedListAdapter extends RecyclerViewSlidingAdapter<FeedListAdapter.FeedHolder> {

    private List<Feed> feedList = new ArrayList<>();

    private FeedListCallback mCallback;

    public FeedListAdapter(Context mContext, FeedListCallback callback) {
        super(mContext);
        this.mCallback = callback;
    }

    public void setFeedList(List<Feed> feedList) {
        this.feedList = feedList;
        this.notifyDataSetChanged();
    }


    @Override
    public FeedHolder onCreateViewHolder(ViewGroup parent, int position) {
        return new FeedHolder(
                LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.feed_item, parent, false)
        );
    }

    @Override
    public void onBindViewHolder(FeedHolder holder, int position) {
        super.onBindViewHolder(holder, position);

        final Feed feed = feedList.get(position);

        holder.feedImg.setImageResource(R.drawable.ic_account_circle);
        Glide.with(holder.itemView.getContext())
                .load(feed.getOwnerUrl())
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(holder.feedImg);

        holder.owner.setText(feed.getOwner());
        holder.text.setText(Html.fromHtml(feed.getDisplay().replace("\n", "<br/>")));

        holder.time.setText(
                DateUtils.getRelativeTimeSpanString(
                        feed.getTimestampCreated(),
                        Calendar.getInstance().getTimeInMillis(), 0)
        );

        if (FeedUtil.isOwner(feed)) {
            holder.feedDelete.setVisibility(View.VISIBLE);
            holder.feedDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mCallback.deleteFeed(feed);
                }
            });
        } else {
            holder.feedDelete.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return feedList.size();
    }

    class FeedHolder extends RecyclerView.ViewHolder {

        @Bind(R.id.feed_img)
        ImageView feedImg;

        @Bind(R.id.feed_owner)
        TextView owner;

        @Bind(R.id.feed_time)
        TextView time;

        @Bind(R.id.feed_text)
        TextView text;

        @Bind(R.id.feed_delete)
        ImageView feedDelete;

        public FeedHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
