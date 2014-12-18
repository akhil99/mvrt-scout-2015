package com.mvrt.scout;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.animation.Interpolator;
import android.widget.Scroller;

import java.lang.reflect.Field;

/**
 * Created by Lee on 10/28/2014.
 */
public class SmoothPager extends ViewPager {

    public SmoothPager(Context context, AttributeSet attr) {
        super(context, attr);
        setEnabled(true);
        postInitViewPager();
    }

    private ScrollerCustomDuration mScroller = null;

    private void postInitViewPager() {
        try {
            Class<?> viewpager = ViewPager.class;
            Field scroller = viewpager.getDeclaredField("mScroller");
            scroller.setAccessible(true);
            Field interpolator = viewpager.getDeclaredField("sInterpolator");
            interpolator.setAccessible(true);

            mScroller = new ScrollerCustomDuration(getContext(),
                    (Interpolator) interpolator.get(null));
            scroller.set(this, mScroller);
        } catch (Exception e) {
            Log.e(Constants.Logging.VIEWPAGER_LOGCAT.getPath(), e.getStackTrace().toString());
        }
    }

    public void setScrollDurationFactor(double scrollFactor) {
        mScroller.setScrollDurationFactor(scrollFactor);
    }

    private class ScrollerCustomDuration extends Scroller {
        private double mScrollFactor = 2;

        public ScrollerCustomDuration(Context context) {
            super(context);
        }

        public ScrollerCustomDuration(Context context, Interpolator interpolator) {
            super(context, interpolator);
        }

        public ScrollerCustomDuration(Context context,
                                      Interpolator interpolator, boolean flywheel) {
            super(context, interpolator, flywheel);
        }

        /**
         * Set the factor by which the duration will change
         */
        public void setScrollDurationFactor(double scrollFactor) {
            mScrollFactor = scrollFactor;
        }

        @Override
        public void startScroll(int startX, int startY, int dx, int dy,
                                int duration) {
            super.startScroll(startX, startY, dx, dy,
                    1000);
        }
    }
}
