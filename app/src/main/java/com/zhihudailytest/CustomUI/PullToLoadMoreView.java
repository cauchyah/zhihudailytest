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
import android.widget.Scroller;
import android.widget.Toast;

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
    private Scroller mScoller;

    private void init() {
        touchSlop = ViewConfiguration.get(getContext()).getScaledTouchSlop();
        mScoller=new Scroller(getContext());
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
                   /* if (distance>0&&footerParams.bottomMargin==-hideFooterHeight)
                        return  false;
                    if (Math.abs(distance)<touchSlop)
                        return false;*/
                    //footerParams.bottomMargin= (int) (hideFooterHeight+distance/2.0);
                   // footerView.setLayoutParams(footerParams);
                    mScoller.startScroll(0,0,0,distance);
                    break;


            }
        }
        return false;
    }

    @Override
    public void computeScroll() {
       if (mScoller.computeScrollOffset()){
           this.scrollTo(mScoller.getCurrX(),mScoller.getCurrY());
           invalidate();
       }
    }

    private void setIsScrollAble(MotionEvent event) {
        int count = linearLayoutManager.getItemCount();
        int last = linearLayoutManager.findLastCompletelyVisibleItemPosition();
       // Toast.makeText(getContext(),"count"+count+";LAST"+last,Toast.LENGTH_SHORT).show();
        float moveY=event.getRawY();

        if (count-1 == last&&moveY-downY<0) {
            Toast.makeText(getContext(),"FOOTER",Toast.LENGTH_SHORT).show();
            canPull = true;
        }
        else{
           /* if(footerParams.bottomMargin!=0){
                footerParams.bottomMargin=0;
                footerView.setLayoutParams(footerParams);
            }*/
            canPull = false;
        }
        if (count == 0) {
            canPull = true;
        }
        if (!canPull) {
            downY = event.getRawY();
        }

    }
}
