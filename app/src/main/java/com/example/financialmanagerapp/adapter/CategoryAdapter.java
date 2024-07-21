package com.example.financialmanagerapp.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.financialmanagerapp.R;
import com.example.financialmanagerapp.model.Category;

import java.util.List;

public class CategoryAdapter extends BaseAdapter {
    protected Context context;
    protected List<Category> categories;
    protected int[] icons;

    public CategoryAdapter(Context context, List<Category> categories, int[] icons) {
        this.context = context;
        this.categories = categories;
        this.icons = icons;
    }

    @Override
    public int getCount() {
        return categories.size();
    }

    @Override
    public Object getItem(int position) {
        return categories.get(position);
    }

    @Override
    public long getItemId(int position) {
        return categories.get(position).getId();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.list_item_category, parent, false);
        }

        ImageView iconView = convertView.findViewById(R.id.category_icon);
        TextView nameView = convertView.findViewById(R.id.category_name);

        Category category = categories.get(position);
        iconView.setImageResource(icons[category.get_icon()]);
        nameView.setText(category.get_name());

        // apply color filter to the icon
        int color = Color.parseColor(category.get_color());
        iconView.setColorFilter(color);

        return convertView;
    }
}
