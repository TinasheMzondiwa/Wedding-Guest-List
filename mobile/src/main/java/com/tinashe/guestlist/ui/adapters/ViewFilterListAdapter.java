package com.tinashe.guestlist.ui.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.tinashe.guestlist.R;
import com.tinashe.guestlist.utils.enums.ViewType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by tinashe on 2015/11/18.
 */
public class ViewFilterListAdapter extends RecyclerView.Adapter<ViewFilterListAdapter.FilterViewHolder> {

    private List<ViewType> mViewTypes = Arrays.asList(ViewType.values());

    private Map<ViewType, Boolean> checkedItems = new HashMap<>();

    private FilterCallback callback;

    public ViewFilterListAdapter(FilterCallback callback) {
        this.callback = callback;
        init();
    }

    public void init() {
        for (ViewType type : mViewTypes) {
            checkedItems.put(type, true);
        }
    }

    public List<ViewType> getCheckedItems() {
        List<ViewType> checked = new ArrayList<>();
        Iterator iterator = checkedItems.entrySet().iterator();

        while (iterator.hasNext()) {
            Map.Entry pair = (Map.Entry) iterator.next();

            if ((Boolean) (pair.getValue())) {
                checked.add((ViewType) pair.getKey());
            }

        }
        return checked;
    }

    @Override
    public FilterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new FilterViewHolder(
                LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.filter_item, parent, false)
        );
    }

    @Override
    public void onBindViewHolder(final FilterViewHolder holder, int position) {
        final ViewType key = mViewTypes.get(position);

        holder.title.setText(holder.itemView.getContext().getString(key.getTitleResId()));

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                holder.checkBox.performClick();
            }
        });

        holder.checkBox.setOnCheckedChangeListener(null);

        holder.checkBox.setChecked(checkedItems.get(key));

        holder.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean checked) {
                checkedItems.put(key, checked);
                callback.updateFilteredList();
            }
        });
    }


    @Override
    public int getItemCount() {
        return mViewTypes.size();
    }


    public interface FilterCallback {

        void updateFilteredList();
    }

    class FilterViewHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.filter_check)
        CheckBox checkBox;

        @Bind(R.id.filter_title)
        TextView title;

        public FilterViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
