package com.zhihudailytest.CustomUI;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.zhihudailytest.R;

/**
 * Created by Administrator on 2016/11/30.
 */

public class PullToLoadMoreView extends LinearLayout implements View.OnTouchListener {
    public PullToLoadMoreView(Context context) {
        super(context);
        init();
    }


    public PullToLoadMoreView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public PullToLoadMoreView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private View footerView;
    private int touchSlop;
    private int hideFooterHeight;
    private MarginLayoutParams footerParams;
    private RecyclerView recyclerView;
    private LinearLayoutManager linearLayoutManager;
    private boolean isOnce = false;
    private boolean canPull = false;
    private float downY;

    private void init() {
        touchSlop = ViewConfiguration.get(getContext()).getScaledTouchSlop();

    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        if (changed && isOnce == false) {
            recyclerView = (RecyclerView) getChildAt(0);
            footerView = getChildAt(0);
            hideFooterHeight = footerView.getHeight();
            footerParams= (MarginLayoutParams) footerView.getLayoutParams();
            isOnce = true;
            linearLayoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
            recyclerView.setOnTouchListener(this);
        }
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        setIsScrollAble(event);
        if(canPull){
            switch (event.getAction()){
                case MotionEvent.ACTION_DOWN:
                    downY=event.getRawY();
                    break;
                case MotionEvent.ACTION_MOVE:
                    float moveY=event.getRawY();
                    int distance= (int) (moveY-downY);
                    if (distance>0&&footerParams.topMargin==hideFooterHeight)
                        return  false;
                    if (Math.abs(distance)<touchSlop)
                        return false;
                    footerParams.topMargin= (int) (hideFooterHeight-distance/2.0);
                    footerView.setLayoutParams(footerParams);
                    break;


            }
        }
        return false;
    }

    private void setIsScrollAble(MotionEvent event) {
        int count = linearLayoutManager.getChildCount();
        int last = linearLayoutManager.findLastCompletelyVisibleItemPosition();
        if (count == last + 1) {
            if (canPull) {
                downY = event.getRawY();
            }
            canPull = true;
        }
        else{
            /*if(footerParams.topMargin!=hideFooterHeight){
                footerParams.topMargin=hideFooterHeight;
                footerView.setLayoutParams(footerParams);
            }*/
            canPull = false;
        }
        if (count == 0) {
            canPull = true;
        }

    }
}
