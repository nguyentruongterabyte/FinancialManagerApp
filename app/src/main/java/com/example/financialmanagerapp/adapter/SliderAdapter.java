package com.example.financialmanagerapp.adapter;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.financialmanagerapp.R;
import com.example.financialmanagerapp.model.SliderData;
import com.smarteist.autoimageslider.SliderViewAdapter;

import java.util.ArrayList;
import java.util.List;

public class SliderAdapter extends SliderViewAdapter<SliderAdapter.SliderAdapterViewHolder> {
    // List for storing urls of images
    private final List<SliderData> mSliderItems;

    public SliderAdapter(ArrayList<SliderData> mSliderItems) {
        this.mSliderItems = mSliderItems;
    }

    // We are inflating the layout_slider
    // inside on Create View Holder method
    @Override
    public SliderAdapterViewHolder onCreateViewHolder(ViewGroup parent) {
        @SuppressLint("InflateParams") View inflate = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_slider, null);
        return new SliderAdapterViewHolder(inflate);
    }


    // Inside on bind view holder we will
    // Set data to item of Slider View
    @Override
    public void onBindViewHolder(SliderAdapter.SliderAdapterViewHolder viewHolder, int position) {
        final SliderData sliderItem = mSliderItems.get(position);

        // Glide is use to load image
        // from url in your imageView
        Glide.with(viewHolder.itemView)
                .load(sliderItem.getImgUrl())
                .fitCenter()
                .into(viewHolder.imageViewBackground);
        viewHolder.title.setText(sliderItem.getTitle());
        viewHolder.description.setText(sliderItem.getDescription());
    }

    @Override
    public int getCount() {
        return mSliderItems.size();
    }

    static class SliderAdapterViewHolder extends SliderViewAdapter.ViewHolder {

        // Adapter class for initializing
        // the views of our slider view.
        View itemView;
        ImageView imageViewBackground;
        TextView title, description;
        public SliderAdapterViewHolder(View itemView) {
            super(itemView);
            imageViewBackground = itemView.findViewById(R.id.myimage);
            title = itemView.findViewById(R.id.slider_title);
            description = itemView.findViewById(R.id.slider_description);
            this.itemView = itemView;
        }
    }


}
