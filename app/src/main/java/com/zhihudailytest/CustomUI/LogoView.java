package com.zhihudailytest.CustomUI;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;

import com.zhihudailytest.R;

/**
 * Created by Administrator on 2016/8/2.
 */
public class LogoView extends View {
    protected static final int DEFAULT_TEXT_SIZE=28;
    protected static final int DEFAULT_OFFSET=10;
    protected static final int DEFAULT_TEXT_COLOR=0xffffffff;
    protected static final int DEFAULT_RING_WIDTH=5;
    protected static final int DEFAULT_RADIUS=20;

    protected int mTextSize=sp2px(DEFAULT_TEXT_SIZE);
    protected int mTextColor=DEFAULT_TEXT_COLOR;
    protected int mOffset=dp2px(DEFAULT_OFFSET);
    protected int mRingWidth=dp2px(DEFAULT_RING_WIDTH);
    protected int mRadius=dp2px(DEFAULT_RADIUS);
    private Paint mPaint;
    public LogoView(Context context) {
        this(context,null);
    }

    public LogoView(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public LogoView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mPaint=new Paint();
        TypedArray ta=context.obtainStyledAttributes(attrs, R.styleable.LogoView);
        /*mPaint.setTextSize(mTextSize);
        mPaint.setColor(mTextColor);*/
       obtainAttributeSet2(ta);
    }

    private void obtainAttributeSet2(TypedArray ta) {
        if (ta==null) return;
        //
 //if (ta==null) return;
      //  mOffset= (int) ta.getDimension(R.styleable.LogoViewStyleable_offset,mOffset);
        mRadius= (int) ta.getDimension(R.styleable.LogoView_radius,mRadius);
        mRingWidth= (int) ta.getDimension(R.styleable.LogoView_ring_width,mRingWidth);
        mTextColor= ta.getColor(R.styleable.LogoView_text_color,mTextColor);
        mTextSize= (int) ta.getDimension(R.styleable.LogoView_text_size,mTextSize);


        ta.recycle();
        mPaint.setTextSize(mTextSize);
        mPaint.setColor(mTextColor);
        mPaint.setAntiAlias(true);
    }
    @Override
    protected synchronized void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width=MeasureSpec.getSize(widthMeasureSpec);

        int height=measureHeight(heightMeasureSpec);
        setMeasuredDimension(width,height);
       // mRealWidth=getMeasuredWidth()-getPaddingLeft()-getPaddingRight();
    }
    private int measureHeight(int heightMeasureSpec) {
        int mode=MeasureSpec.getMode(heightMeasureSpec);
        int size=MeasureSpec.getSize(heightMeasureSpec);
        int result=0;
        if(mode==MeasureSpec.EXACTLY){
            result=size;
        }else{
            int TextHeight= (int) (mPaint.descent()-mPaint.ascent());
            result=getPaddingBottom()+getPaddingTop()
                    +Math.max(mRadius*2+mRingWidth,Math.abs(TextHeight));
            if(mode==MeasureSpec.AT_MOST){
                result=Math.min(result,size);
            }
        }
        return result;

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        String text="知乎日报";
        int textWidth= (int) mPaint.measureText(text);
        int left= (int) ((getWidth()-mRadius*2-textWidth*0.90f)/2.0f);
        int top=(int) (getHeight()/2.0f-mRadius);
        RectF rel=new RectF(left,top,mRadius*2+left,mRadius*2+top);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(mRingWidth);
        canvas.drawArc(rel,85,245,false,mPaint);
        mPaint.setStyle(Paint.Style.FILL);
        float x=left+mRadius*2-textWidth*0.10f;
        canvas.drawText(text,x, mRadius*2+getPaddingTop(),mPaint);

    }

    protected int dp2px(int dpVal){
        return (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                dpVal,
                getResources().getDisplayMetrics());
    }
    protected int sp2px(int spVal){
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP,
                spVal,
                getResources().getDisplayMetrics());
    }
}
