package com.perfectearth.bhagavadgita.Utilis;

import android.content.Context;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;

import androidx.core.widget.NestedScrollView;

public class CustomNestedScrollView extends NestedScrollView {

    private GestureDetector gestureDetector;

    public CustomNestedScrollView(Context context) {
        super(context);
    }

    public CustomNestedScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CustomNestedScrollView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setGestureDetector(GestureDetector gestureDetector) {
        this.gestureDetector = gestureDetector;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if (gestureDetector != null) {
            gestureDetector.onTouchEvent(ev);
        }
        return super.onInterceptTouchEvent(ev);
    }
    @Override
    public boolean performClick() {
        super.performClick();
        return true;
    }
}
