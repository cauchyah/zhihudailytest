package com.zhihudailytest.CustomUI;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ScrollView;

/**
 * Created by Administrator on 2016/7/9.
 */
public class MyScrollView extends ScrollView {
    public  interface OnScrollListener{
        void onScroll(int scrollY);
    }
    private int lastScrollY;
    private OnScrollListener mScrollListener;

    public OnScrollListener getmScrollListener() {
        return mScrollListener;
    }

    public void setmScrollListener(OnScrollListener mScrollListener) {
        this.mScrollListener = mScrollListener;
    }

    public MyScrollView(Context context) {
        super(context);
    }

    public MyScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MyScrollView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }
    Handler mHandler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            int scrollY=MyScrollView.this.getScrollY();
            if (scrollY!=lastScrollY){
                lastScrollY=scrollY;
                mHandler.sendMessageDelayed(mHandler.obtainMessage(),5);
            }
            if (mScrollListener!=null)
                    mScrollListener.onScroll(scrollY);


        }
    };

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        if (mScrollListener!=null){
            mScrollListener.onScroll(lastScrollY=this.getScrollY());
        }
        else if(ev.getAction()==MotionEvent.ACTION_UP){
                mHandler.sendMessageDelayed(mHandler.obtainMessage(),5);
        }
        return super.onTouchEvent(ev);
    }
}
