package com.zhihudailytest.Utils;

import android.content.Context;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by Administrator on 2016/6/30.
 */
public class ViewHolder {
    private View mConvertView;

    public int getPosition() {
        return mPosition;
    }

    public void setPosition(int mPosition) {
        this.mPosition = mPosition;
    }

    private int mPosition;
    private SparseArray<View> mViews;
    private View temp;

    public ViewHolder(Context context, int layoutId, int position, ViewGroup parent) {
        this.mConvertView = LayoutInflater.from(context).inflate(layoutId, parent, false);
        this.mConvertView.setTag(this);
        this.mPosition = position;
        mViews=new SparseArray<View>();

    }

    public View getConvertView() {
        return mConvertView;
    }

    public static ViewHolder getViewHolder(Context context, int layoutId,
                                           int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            return new ViewHolder(context, layoutId, position, parent);
        } else {
            ViewHolder holder = (ViewHolder) convertView.getTag();
            holder.mPosition=position;
            return holder;
        }
    }

    public <T extends  View> T getView(int resId){
        temp=mViews.get(mPosition);
       //Log.i("abcd", "getView: "+mPosition);
        if (temp!=null){
            return  (T)temp;
        }
        else{
            temp=mConvertView.findViewById(resId);
            mViews.put(mPosition,temp);
            return (T)temp;
        }
    }

    public ViewHolder setText(int resId, String text) {
        ((TextView)mConvertView.findViewById(resId)).setText(text);
        return  this;
    }
    public ViewHolder setImage(int resId,int imageRes){
        ((ImageView)mConvertView.findViewById(resId)).setImageResource(imageRes);
        return  this;
    }
}
