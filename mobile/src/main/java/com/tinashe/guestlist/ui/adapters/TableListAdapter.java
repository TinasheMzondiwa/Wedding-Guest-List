package com.tinashe.guestlist.ui.adapters;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.tinashe.guestlist.R;
import com.tinashe.guestlist.callbacks.TableListCallback;
import com.tinashe.guestlist.databinding.GuestItemBinding;
import com.tinashe.guestlist.model.Guest;
import com.tinashe.guestlist.model.Table;
import com.tinashe.guestlist.ui.GuestActivity;
import com.tinashe.guestlist.utils.GuestUtil;
import com.tinashe.guestlist.utils.enums.BundledExtras;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by tinashe on 2016/02/12.
 */
public class TableListAdapter extends RecyclerView.Adapter<TableListAdapter.TableViewHolder> {

    private List<Table> tableList = new ArrayList<>();

    private List<Guest> guestList = new ArrayList<>();

    private TableListCallback mCallback;

    public TableListAdapter(TableListCallback mCallback) {
        this.mCallback = mCallback;
    }

    public void setTableList(List<Table> tableList) {
        this.tableList = tableList;
        this.notifyDataSetChanged();
    }

    public List<Guest> getGuestList() {
        return guestList;
    }

    public void setGuestList(List<Guest> guestList) {
        this.guestList = guestList;
        this.notifyDataSetChanged();
    }

    @Override
    public TableViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new TableViewHolder(
                LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.table_item, parent, false)
        );
    }

    @Override
    public void onBindViewHolder(final TableViewHolder holder, int position) {
        final Table table = tableList.get(position);

        final Context context = holder.itemView.getContext();
        holder.tableNum.setText(context.getString(R.string.table_number, table.getNumber()));
        holder.guestsCount.setText(context.getString(R.string.guests_count, table.getPeople().size()));

        holder.vipImg.setVisibility(table.isVip() ? View.VISIBLE : View.GONE);

        holder.guestContainer.removeAllViews();

        for (String key : table.getPeople()) {
            final Guest guest = GuestUtil.findGuest(guestList, key);
            if (guest == null) {
                return;
            }
            View root = GuestItemBinding.inflate(
                    LayoutInflater.from(context),
                    holder.guestContainer, false
            ).getRoot();

            GuestItemBinding binding = DataBindingUtil.bind(root);
            binding.setGuest(guest);

            binding.getRoot().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mCallback.isUserAdmin()) {
                        mCallback.showGuestOptions(guest, table);
                    } else {
                        Intent intent = new Intent(context, GuestActivity.class);
                        intent.putExtra(BundledExtras.DATA_OBJECT.name(), guest);
                        context.startActivity(intent);
                    }
                }
            });


            holder.guestContainer.addView(binding.getRoot());


        }

        holder.toggle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean show = holder.guestContainer.getVisibility() == View.GONE;
                holder.toggle.setImageResource(show ? R.drawable.ic_expand_less : R.drawable.ic_expand_more);
                holder.guestContainer.setVisibility(show ? View.VISIBLE : View.GONE);

                if (mCallback.isUserAdmin()) {
                    holder.addBtn.setVisibility((table.getPeople().size() < 10 && show) ? View.VISIBLE : View.GONE);
                }
            }
        });

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.toggle.performClick();
            }
        });


        if (mCallback.isUserAdmin()) {
            holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {

                    new AlertDialog.Builder(context)
                            .setTitle(R.string.action_delete)
                            .setMessage("Delete this Table?")
                            .setNegativeButton(R.string.action_delete, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    mCallback.deleteTable(table);
                                }
                            })
                            .setPositiveButton("No", null)
                            .create().show();
                    return false;
                }
            });

            holder.addBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mCallback.addGuest(table);
                }
            });
        }


    }

    @Override
    public int getItemCount() {
        return tableList.size();
    }


    class TableViewHolder extends RecyclerView.ViewHolder {

        @Bind(R.id.table_num)
        TextView tableNum;

        @Bind(R.id.txt_guests_count)
        TextView guestsCount;

        @Bind(R.id.img_vip)
        ImageView vipImg;

        @Bind(R.id.toggle)
        ImageView toggle;

        @Bind(R.id.guests_container)
        LinearLayout guestContainer;

        @Bind(R.id.btn_add)
        Button addBtn;

        public TableViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
