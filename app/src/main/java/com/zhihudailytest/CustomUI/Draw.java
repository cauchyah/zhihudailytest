package com.zhihudailytest.CustomUI;

import android.content.Context;
import android.view.View;
import android.widget.LinearLayout;

import com.zhihudailytest.R;

/**
 * Created by Administrator on 2016/7/7.
 */
public class Draw {
    public static void drawDot(Context context,LinearLayout dotLayout, int size){
        View dot = null;
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(20, 20);
        params.leftMargin = 8;
        for (int i = 0; i <size; i++) {
            dot = new View(context);
            dot.setEnabled(false);
            dot.setBackgroundResource(R.drawable.dot_selector);
            dot.setLayoutParams(params);
            dotLayout.addView(dot);
        }
    }
}
