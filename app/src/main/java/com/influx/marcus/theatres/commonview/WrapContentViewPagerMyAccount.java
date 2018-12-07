package com.influx.marcus.theatres.commonview;


import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by TheLittleNaruto on 21-07-2015 at 16:51
 */
public class WrapContentViewPagerMyAccount extends ViewPager {

    private int mCurrentPagePosition = 0;

    private int[] heightArr = new int[106];  // max number of elements in viewpager

    public WrapContentViewPagerMyAccount(Context context) {
        super(context);
    }

    public WrapContentViewPagerMyAccount(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {


        int mode = MeasureSpec.getMode(heightMeasureSpec);
        if (mode == MeasureSpec.UNSPECIFIED || mode == MeasureSpec.AT_MOST) {
            // super has to be called in the beginning so the child views can be initialized.
            // super.onMeasure(widthMeasureSpec, heightMeasureSpec);
            int height = 0;
            int numOfElements = getChildCount();
            for (int i = 0; i < numOfElements; i++) {
                View child = getChildAt(i);
                child.measure(widthMeasureSpec, MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED));
                int h = child.getMeasuredHeight();
                height = h;
                //if (h > height) height = h;
                heightArr[i] = height;
            }
            heightMeasureSpec = MeasureSpec.makeMeasureSpec(heightArr[mCurrentPagePosition], MeasureSpec.EXACTLY);
            heightMeasureSpec = heightMeasureSpec + 180;
        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

    }

    public void reMeasureCurrentPage(int position) {
        mCurrentPagePosition = position;
        requestLayout();
    }
}

