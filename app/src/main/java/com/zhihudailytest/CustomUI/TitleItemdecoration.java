package com.zhihudailytest.CustomUI;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.View;

import com.zhihudailytest.Bean.Story;
import com.zhihudailytest.R;
import com.zhihudailytest.Utils.DateUtil;

import java.util.List;

/**
 * Created by Administrator on 2016/12/4.
 */

public class TitleItemdecoration extends RecyclerView.ItemDecoration {
    private List<Story> mData;
    private Context mContext;
    private int mHeight=30;
    private int textSize=14;
    private Paint mPaint;
    private Rect mBound;
    public TitleItemdecoration(Context context, List<Story> mData) {
        this.mData=mData;
        this.mContext=context;
        mPaint=new Paint();
        mBound=new Rect();
        getPx();

    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);
        int position= ((RecyclerView.LayoutParams) view.getLayoutParams()).getViewLayoutPosition();
        if(position!=0){
            if(position==1){
                outRect.set(0,mHeight,0,0);
            }
            else{
                if (mData.get(position-1).getDate()!=null&&
                        !mData.get(position-1).getDate().equals(mData.get(position-2).getDate())){
                    outRect.set(0,mHeight,0,0);
                }
                else{
                    outRect.set(0,0,0,0);
                }
            }
        }
    }

    @Override
    public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
        super.onDraw(c, parent, state);
        int left=parent.getPaddingLeft();
        int right=parent.getWidth()-parent.getPaddingRight();
        int count=parent.getChildCount();
        for (int i = 0; i < count; i++) {
            View child=parent.getChildAt(i);
            RecyclerView.LayoutParams params= (RecyclerView.LayoutParams) child.getLayoutParams();
            int position=params.getViewLayoutPosition();
           if (position>-1){
                if(position==0){}
                else if(position==1){
                    drawTitleArea(c,left,right,params,0,child);
                }
                else if (mData.get(position-1).getDate()!=null&&
                        !mData.get(position-1).getDate().equals(mData.get(position-2).getDate())){
                    drawTitleArea(c,left,right,params,position-1,child);
                }
            }

        }
    }

    private void drawTitleArea(Canvas c, int left, int right, RecyclerView.LayoutParams params, int position,View child) {
        String date=DateUtil.string2date(mData.get(position).getDate());
        mPaint.setColor(ContextCompat.getColor(mContext, R.color.background));
        c.drawRect(left,child.getTop()-mHeight-params.topMargin,right,child.getTop()-params.topMargin,mPaint);
        mPaint.setTextSize(textSize);
        mPaint.getTextBounds(date,0,date.length(),mBound);
        mPaint.setColor(Color.parseColor("#a9a9a9"));

        c.drawText(date,child.getPaddingLeft()+15
                ,child.getTop()-params.topMargin-(mHeight-mBound.height())/2,mPaint);
    }
    private void getPx(){
        mHeight= (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,mHeight,mContext.getResources().getDisplayMetrics());
        textSize= (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP,textSize,mContext.getResources().getDisplayMetrics());

    }

}
