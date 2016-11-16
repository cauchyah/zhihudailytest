package com.zhihudailytest.Adapter;

import android.content.Context;
import android.graphics.PointF;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.zhihudailytest.Activity.DetailActivity;
import com.zhihudailytest.Bean.Story;
import com.zhihudailytest.Bean.TopStory;
import com.zhihudailytest.R;
import com.zhihudailytest.Utils.DataBaseDao;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import jp.wasabeef.glide.transformations.gpu.VignetteFilterTransformation;

/**
 * Created by Administrator on 2016/7/5.
 */
public class MyPagerAdapter extends PagerAdapter {

    private  Context mContext;
    private  List<Story> mList;


    public MyPagerAdapter(Context context, List<Story> mList){
        super();
        this.mContext=context;
        this.mList=mList;



    }

    @Override
    public int getCount() {
        return mList.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view==object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, final int position) {
        ImageView iv=new ImageView(mContext);
        Story one=mList.get(position);
        Glide.with(mContext)
                .load(one.getImage())

                .bitmapTransform(new CenterCrop(mContext),
                        new VignetteFilterTransformation(
                                mContext,new PointF(0.5f, 0.5f),
                                new float[] { 0.1f, 0.1f, 0.1f }, 0.0f, 0.9f))
                .into(iv);

        container.addView(iv);
        iv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Story story=mList.get(position);
                int currentId=story.getId();
                ArrayList<Integer> idList=new ArrayList<Integer>();
                for(Story one:mList){
                        int id=one.getId();
                        idList.add(id);
                }
                Bundle bundle=new Bundle();
                bundle.putIntegerArrayList("ids",idList);
                bundle.putInt("current",currentId);
                DetailActivity.actionStart(mContext,bundle);
                if (!story.isReaded()){

                    DataBaseDao dao=new DataBaseDao();
                    dao.markRead(currentId);
                    story.setReaded(true);

                }

            }
        });
        return iv;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }
}
