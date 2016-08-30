package com.tinashe.guestlist.ui.adapters;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.SectionIndexer;

import com.tinashe.guestlist.R;
import com.tinashe.guestlist.callbacks.GuestListCallback;
import com.tinashe.guestlist.databinding.GuestItemBinding;
import com.tinashe.guestlist.model.Guest;
import com.tinashe.guestlist.utils.ContactUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

/**
 * Created by tinashe on 2016/02/08.
 */
public class GuestListAdapter extends RecyclerView.Adapter<GuestListAdapter.GuestViewHolder> implements Filterable, SectionIndexer {

    private static final String TAG = GuestListAdapter.class.getName();

    private List<Guest> guestList = new ArrayList<>();
    private List<Guest> originalList;

    private List<String> names = new ArrayList<>();

    private GuestListCallback callback;

    private boolean permissionGranted = false;

    private HashMap<String, Bitmap> photosMap = new HashMap<>();

    public GuestListAdapter(GuestListCallback callback) {
        this.callback = callback;
    }

    public void setPermissionGranted(boolean permissionGranted) {
        this.permissionGranted = permissionGranted;
    }

    public void setGuestList(List<Guest> guestList) {
        Collections.sort(guestList);

        names.clear();
        for (Guest g : guestList) {
            names.add(g.getFirstName());
        }

        this.guestList = guestList;
        this.originalList = guestList;
        this.notifyDataSetChanged();
    }

    @Override
    public GuestViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new GuestViewHolder(
                GuestItemBinding.inflate(
                        LayoutInflater.from(parent.getContext()),
                        parent, false
                ).getRoot()
        );
    }

    @Override
    public void onBindViewHolder(GuestViewHolder holder, int position) {
        final Guest guest = guestList.get(position);
        holder.binding.setGuest(guest);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callback.onItemSelected(guest);
            }
        });

        if (permissionGranted) {
            holder.binding.contactImg.setImageResource(R.drawable.ic_account_circle);

            Context context = holder.itemView.getContext();

            if (!TextUtils.isEmpty(guest.getPhone()) && guest.getPhone().length() > 3) {
                setPhoto(guest, holder.itemView.getContext(), holder.binding.contactImg);
            } else if (!TextUtils.isEmpty(guest.getEmail()) && guest.getEmail().length() > 3) {

                if (photosMap.containsKey(guest.getKey())) {
                    holder.binding.contactImg.setImageBitmap(photosMap.get(guest.getKey()));
                } else {
                    long id = ContactUtil.getContactIdByEmailAddress(
                            holder.itemView.getContext(),
                            guest.getEmail()

                    );

                    if (id > 0) {
                        Bitmap photo = ContactUtil.openDisplayPhoto(context, id);
                        if (photo != null) {
                            holder.binding.contactImg.setImageBitmap(photo);
                            photosMap.put(guest.getKey(), photo);
                        }
                    }
                }

            }
        }


    }

    private void setPhoto(Guest guest, Context context, ImageView img) {
        if (photosMap.containsKey(guest.getKey())) {
            img.setImageBitmap(photosMap.get(guest.getKey()));
        } else {
            long id = ContactUtil.getContactIDFromNumber(
                    guest.getPhone(),
                    context
            );

            if (id > 0) {
                Bitmap photo = ContactUtil.openDisplayPhoto(context, id);
                if (photo != null) {
                    img.setImageBitmap(photo);
                    photosMap.put(guest.getKey(), photo);
                }
            }


        }
    }

    @Override
    public int getItemCount() {
        return guestList.size();
    }

    @Override
    public Filter getFilter() {
        return new GuestFilter();
    }

    @Override
    public Object[] getSections() {
        return names.toArray();
    }

    @Override
    public int getPositionForSection(int sectionIndex) {
        return 0;
    }

    @Override
    public int getSectionForPosition(int position) {
        if (position >= names.size()) {
            position = names.size() - 1;
        }
        return position;
    }


    class GuestViewHolder extends RecyclerView.ViewHolder {

        GuestItemBinding binding;

        public GuestViewHolder(View itemView) {
            super(itemView);
            binding = DataBindingUtil.bind(itemView);
        }
    }


    class GuestFilter extends Filter {

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            FilterResults result = new FilterResults();
            if (!TextUtils.isEmpty(constraint)) {
                constraint = constraint.toString().toLowerCase();
                List<Guest> filteredList = new ArrayList<>();

                for (int i = 0, l = originalList.size(); i < l; i++) {
                    Guest guest = originalList.get(i);
                    String searchTerm = guest.getFirstName() + " " + guest.getLastName();
                    if (searchTerm.toLowerCase().contains(constraint)) {
                        filteredList.add(guest);
                    }

                    result.count = filteredList.size();
                    result.values = filteredList;

                }
            } else {
                synchronized (this) {
                    result.values = originalList;
                    result.count = originalList.size();
                }
            }

            return result;
        }

        @SuppressWarnings("unchecked")
        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            guestList.clear();
            if (results.values != null) {
                guestList.addAll((List<Guest>) results.values);
            }

            notifyDataSetChanged();
        }
    }
}
