package com.zhihudailytest.CustomUI;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.util.AttributeSet;
import android.view.ViewGroup;

/**
 * Created by Administrator on 2016/7/18.
 */
public class NoPaddingCardView extends CardView {

    public NoPaddingCardView(Context context) {
        super(context);
        init();
    }

    public NoPaddingCardView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public NoPaddingCardView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        // Optional: Prevent pre-L from adding inner card padding
        setPreventCornerOverlap(false);
        // Optional: make Lollipop and above add shadow padding to match pre-L padding
        setUseCompatPadding(true);
    }

    @Override
    public void setLayoutParams(ViewGroup.LayoutParams params) {
        // FIX shadow padding
        if (params instanceof MarginLayoutParams) {
            MarginLayoutParams layoutParams = (MarginLayoutParams) params;
           // layoutParams.bottomMargin -= (getPaddingBottom() - getContentPaddingBottom());
           //layoutParams.bottomMargin = -7;
            layoutParams.leftMargin -= (getPaddingLeft() - getContentPaddingLeft());
            layoutParams.rightMargin -= (getPaddingRight() - getContentPaddingRight());
           // layoutParams.topMargin -= (getPaddingTop() - getContentPaddingTop());
           //layoutParams.topMargin = -5;
        }

        super.setLayoutParams(params);
    }
}
