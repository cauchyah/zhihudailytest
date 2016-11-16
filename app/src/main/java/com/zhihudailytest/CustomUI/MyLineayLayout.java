package com.zhihudailytest.CustomUI;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;
import android.widget.ListView;

/**
 * Created by Administrator on 2016/8/3.
 */

public class MyLineayLayout extends LinearLayout {
    public MyLineayLayout(Context context) {
        super(context);
    }

    public MyLineayLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MyLineayLayout(Context context, AttributeSet attrs,
                                 int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    /**
     * 重写该方法，达到使ListView适应ScrollView的效果
     */
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2,
                MeasureSpec.AT_MOST);
        super.onMeasure(widthMeasureSpec, expandSpec);
    }
}