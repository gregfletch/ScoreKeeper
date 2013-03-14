package com.fletch.gamescorekeeper;

import java.util.List;

import android.content.Context;
import android.database.DataSetObserver;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SpinnerAdapter;
import android.widget.TextView;

public class PlayerSpinnerAdapter implements SpinnerAdapter {

    private final List<Player> playerList;
    private final Context context;

    public PlayerSpinnerAdapter(Context context, List<Player> playerList) {

        this.context = context;
        this.playerList = playerList;
    }

    @Override
    public int getCount() {
        return this.playerList.size();
    }

    @Override
    public Object getItem(int position) {
        return this.playerList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        return android.R.layout.simple_spinner_dropdown_item;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        TextView textView = new TextView(context);
        textView.setText(this.playerList.get(position).getName());
        textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
        textView.setPadding(14, 16, 0, 20);
        return textView;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public boolean isEmpty() {
        return this.playerList.isEmpty();
    }

    @Override
    public void registerDataSetObserver(DataSetObserver observer) {

    }

    @Override
    public void unregisterDataSetObserver(DataSetObserver observer) {

    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        return this.getView(position, convertView, parent);
    }
}
