package com.zhihudailytest.Adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.util.Log;
import android.view.ViewGroup;

import com.zhihudailytest.Fragment.DetailFragment;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2016/7/16.
 */
public class DetailAdapter extends FragmentStatePagerAdapter {
    private List<Integer> mData;

    public DetailAdapter(FragmentManager manager, List<Integer> list) {
        super(manager);
        mData = list;

    }


    @Override
    public Fragment getItem(int position) {
        return  DetailFragment.getInstance(mData.get(position));
    }

    @Override
    public int getCount() {
        return mData.size();
    }

    public int getId(int position) {
        return mData.get(position);
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        return super.instantiateItem(container, position);
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        super.destroyItem(container, position, object);
       // fragmentMap.remove(position);
    }
}
