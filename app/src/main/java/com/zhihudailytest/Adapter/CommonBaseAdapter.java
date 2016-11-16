package com.zhihudailytest.Adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.zhihudailytest.Utils.ViewHolder;

import java.util.List;

/**
 * Created by Administrator on 2016/7/4.
 */
public abstract class CommonBaseAdapter<T> extends BaseAdapter {


    protected List<T> mDatas;
    protected Context mContext;
    private int mLayoutRes;
    public CommonBaseAdapter(Context context, List<T> mData,int layoutRes){
        this.mContext=context;
        this.mDatas=mData;
        this.mLayoutRes=layoutRes;

    }
    @Override
    public int getCount() {
        return mDatas.size();
    }

    @Override
    public T getItem(int position) {
        return mDatas.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder=ViewHolder.getViewHolder(
                mContext,mLayoutRes,position,convertView,parent);
        T item= mDatas.get(position);
        /*holder.setText(R.id.name,bean.getName()).setText(R.id.desc,bean.getDesc());*/
        initView(holder,item);

        return holder.getConvertView();
    }
    public abstract  void initView(ViewHolder holder,T item);
}

