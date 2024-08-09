package com.example.financialmanagerapp.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import com.example.financialmanagerapp.R;
import com.example.financialmanagerapp.model.Category;
import com.example.financialmanagerapp.utils.Utils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CategoryAdapter extends BaseAdapter {
    protected Context context;
    protected List<Category> categories;
    protected int[] icons;
    protected boolean chooseMany;
    protected Map<Integer, Boolean> checkBoxStateMap;
    protected List<Integer> checkedIdList;

    public CategoryAdapter(Context context,
                           List<Category> categories,
                           int[] icons,
                           boolean chooseMany,
                           List<Integer> checkedIdList) {
        this.context = context;
        this.categories = categories;
        this.icons = icons;
        this.chooseMany = chooseMany;
        this.checkBoxStateMap = new HashMap<>();
        this.checkedIdList = checkedIdList;

        // Initialize checkBoxStateMap based on checkedIdList
        if (checkedIdList != null) {
            for (Integer id : checkedIdList) {
                checkBoxStateMap.put(id, true);
            }
        }
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
            convertView = LayoutInflater
                    .from(context).inflate(R.layout.list_item_category, parent, false);
        }

        ImageView iconView = convertView.findViewById(R.id.category_icon);
        TextView nameView = convertView.findViewById(R.id.category_name);
        CheckBox checkBox = convertView.findViewById(R.id.checkbox);

        Category category = categories.get(position);
        iconView.setImageResource(icons[category.get_icon()]);
        nameView.setText(category.get_name());

        // set enable checkbox on each item list
        if (chooseMany)
            checkBox.setVisibility(View.VISIBLE);
        else
            checkBox.setVisibility(View.GONE);


        // apply color filter to the icon
        iconView.setColorFilter(ContextCompat.getColor(context, R.color.white));
        Utils.formattingImageBackground(context, iconView, category.get_color());

        // Set checked checkbox state from checkBoxStateMap
        checkBox.setOnCheckedChangeListener(null); // Clear listener before setting checked state
        checkBox.setChecked(Boolean.TRUE.equals(
                checkBoxStateMap.getOrDefault(category.getId(), false)));
        checkBox.setOnCheckedChangeListener((buttonView, isChecked) ->
                checkBoxStateMap.put(category.getId(), isChecked));
        return convertView;
    }

    public Map<Integer, Boolean> getCheckBoxStateMap() {
        return checkBoxStateMap;
    }

    // Method to update the adapter's data
    public void updateCategories(List<Category> newCategories, List<Integer> newCheckedIdList) {
        this.categories = newCategories;
        this.checkedIdList = newCheckedIdList;

        // Update checkBoxStateMap
        this.checkBoxStateMap.clear();
        if (newCheckedIdList != null) {
            for (Integer id : newCheckedIdList) {
                checkBoxStateMap.put(id, true);
            }
        }

        notifyDataSetChanged();
    }

}
