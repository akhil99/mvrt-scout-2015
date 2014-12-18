package com.mvrt.scout;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;

import java.util.jar.Attributes;

/**
 * Created by Lee Mracek on 11/18/2014.
 */
public class DisableablePager extends SmoothPager {
    boolean pagingEnabled = true;

    public DisableablePager(Context context, AttributeSet attr) {
        super(context, attr);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return this.pagingEnabled && super.onTouchEvent(event);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        return this.pagingEnabled && super.onInterceptTouchEvent(event);
    }

    public void setPagingEnabled(boolean enabled) {
        this.pagingEnabled = enabled;
    }
}
