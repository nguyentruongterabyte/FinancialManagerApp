package com.example.financialmanagerapp.custom;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;

import androidx.appcompat.widget.AppCompatButton;

public class DrawableClickableButton extends AppCompatButton {
    private DrawableClickListener listener;

    public DrawableClickableButton(Context context) {
        super(context);
    }

    public DrawableClickableButton(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public DrawableClickableButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_UP) {
            if (listener != null) {
                if (event.getRawX() >= (getRight() - getCompoundDrawables()[2].getBounds().width())) {
                    listener.onDrawableClick();
                    return true;
                }
            }
        }
        return super.onTouchEvent(event);
    }

    @Override
    public boolean performClick() {
        // Ensure accessibility services handle the click event
        super.performClick();
        return true;
    }

    public void setDrawableClickListener(DrawableClickListener listener) {
        this.listener = listener;
    }

    public interface DrawableClickListener {
        void onDrawableClick();
    }
}
