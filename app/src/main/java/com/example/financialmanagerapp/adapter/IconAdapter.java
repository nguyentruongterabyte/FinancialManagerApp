package com.example.financialmanagerapp.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import androidx.core.content.ContextCompat;

import com.example.financialmanagerapp.R;
import com.example.financialmanagerapp.utils.Utils;

public class IconAdapter extends BaseAdapter {

    protected Context context;
    protected int[] icons;

    public IconAdapter(Context context, int[] icons) {
        this.context = context;
        this.icons = icons;
    }

    @Override
    public int getCount() {
        return icons.length;
    }

    @Override
    public Object getItem(int position) {
        return icons[position];
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.list_item_icon, parent, false);
        }

        ImageView imageIcon = convertView.findViewById(R.id.image_icon);

        imageIcon.setImageResource(icons[position]);
        imageIcon.setColorFilter(ContextCompat.getColor(context, R.color.black));
        Utils.formattingImageBackground(context, imageIcon, "#807A7A7A");
        return convertView;
    }
}
