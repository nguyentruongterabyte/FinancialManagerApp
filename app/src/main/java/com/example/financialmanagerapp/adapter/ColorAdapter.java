package com.example.financialmanagerapp.adapter;

import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import androidx.core.content.ContextCompat;

import com.example.financialmanagerapp.R;

public class ColorAdapter extends BaseAdapter {

    protected Context context;
    protected int[] colors;

    public ColorAdapter(Context context, int[] colors) {
        this.context = context;
        this.colors = colors;
    }

    @Override
    public int getCount() {
        return colors.length;
    }

    @Override
    public Object getItem(int position) {
        return colors[position];
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.list_item_color, parent, false);
        }

        View vColor = convertView.findViewById(R.id.v_color);

        // Get the color resource ID from the array
        int colorResId = colors[position];

        // Retrieve the actual color value using ContextCompact
        int color = ContextCompat.getColor(context, colorResId);
        GradientDrawable drawable = new GradientDrawable();
        drawable.setShape(GradientDrawable.RECTANGLE);

        // Convert dp to px
        drawable.setCornerRadius(12 * context.getResources().getDisplayMetrics().density);
        drawable.setColor(color);

        vColor.setBackground(drawable);
        return convertView;
    }
}
