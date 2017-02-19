package com.reim.android.filterrecyclerviewsample;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.reim.android.filterrecyclerview.FilterRecyclerAdapter;

import org.jetbrains.annotations.NotNull;

class MainFilterRecyclerAdapter extends FilterRecyclerAdapter<ListItem> {

    private LayoutInflater inflater;

    MainFilterRecyclerAdapter(Context context) {
        inflater = LayoutInflater.from(context);
    }

    @Override
    public boolean filterByConstraint(@NonNull ListItem item, @NotNull String constraint) {

        String lowerName = item.getName().toLowerCase();
        String lowerConstraint = constraint.trim().toLowerCase();

        return lowerName.contains(lowerConstraint);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ItemViewHolder(inflater.inflate(R.layout.item_row, parent, false));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ((ItemViewHolder) holder).initialize(findItemByPosition(position));
    }

    private class ItemViewHolder extends RecyclerView.ViewHolder {

        private TextView textName;

        ItemViewHolder(View itemView) {
            super(itemView);
            textName = (TextView) itemView.findViewById(R.id.item_row_name);
        }

        private void initialize(ListItem listItem) {
            textName.setText(listItem.getName() + " / " + listItem.getCategory());
        }
    }
}
