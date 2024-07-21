package com.example.financialmanagerapp.adapter;

import android.content.Context;
import android.content.res.TypedArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.financialmanagerapp.R;

public class CustomAdapter extends BaseAdapter {

    protected Context context;
    protected String[] items;
    protected TypedArray icons;

    public CustomAdapter(Context context, String[] items, TypedArray icons) {
        this.context = context;
        this.items = items;
        this.icons = icons;
    }
    @Override
    public int getCount() {
        return items.length;
    }

    @Override
    public Object getItem(int position) {
        return items[position];
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(context);
            convertView = inflater.inflate(R.layout.list_item, parent, false);
        }

        ImageView icon = convertView.findViewById(R.id.icon);
        TextView text = convertView.findViewById(R.id.text);

        icon.setImageResource(icons.getResourceId(position, -1));
        text.setText(items[position]);
        return convertView;
    }
}
