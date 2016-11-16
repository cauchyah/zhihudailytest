package com.zhihudailytest;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.PagerTabStrip;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SlidingPaneLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import butterknife.Bind;
import butterknife.ButterKnife;

public class PagerActivity extends AppCompatActivity {
    @Bind(R.id.tab)
    PagerTabStrip tabs;
    @Bind(R.id.viewPager)
    ViewPager viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pager);
        ButterKnife.bind(this);
        MyPagerAdapter adapter=new MyPagerAdapter(this);
        viewPager.setAdapter(adapter);
        tabs.setBackgroundColor(ContextCompat.getColor(this,R.color.aqua));
        tabs.setTabIndicatorColor(ContextCompat.getColor(this,R.color.colorPrimary));
        //tabs.setV
    }

    class MyPagerAdapter extends PagerAdapter{
        private Context mContext;

        public MyPagerAdapter(Context mContext) {
            this.mContext = mContext;
        }

        @Override
        public int getCount() {
            return 8;
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return (view==object);
        }

        @Override
        public CharSequence getPageTitle(int position) {

            return "position"+position;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            ImageView image=new ImageView(mContext);
            container.addView(image);
            Glide.with(mContext)
                    .load(R.drawable.kaka)
                    .into(image);
            return image;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }

    }
}
