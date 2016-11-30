package com.zhihudailytest.CustomUI;


import android.graphics.PorterDuff;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.zhihudailytest.Bean.AdvInfo;
import com.zhihudailytest.R;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class ViewPagerFragment extends Fragment {

    private static final int WHEEL = 10;
    private static final int WHEEL_WAIT = 11;
    private ViewPager viewPager;
    private LinearLayout indicatorLayout;
    private List<AdvInfo> list ;
    private ViewPagerAdapter adapter;
    private boolean isWheel = true;
    private static final int delayTime = 5000;
    private long lastTouchTime;
    public static final int RIGHT = 1;
    public static final int LEFT = 2;
    public static final int CENTER = 3;
    private int checkedColor;
    private int uncheckColor;

    public ViewPagerFragment() {
        // Required empty public constructor
        super();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view = inflater.inflate(R.layout.fragment_view_pager, container, false);
        viewPager = (ViewPager) view.findViewById(R.id.viewPager);
        indicatorLayout = (LinearLayout) view.findViewById(R.id.indicatorLayout);
        return view;
    }

    public void setIndicator(int position, int checkedColor, int uncheckColor) {
        this.checkedColor = checkedColor;
        this.uncheckColor = uncheckColor;
        switch (position) {
            case LEFT:
                indicatorLayout.setGravity(Gravity.LEFT);
                break;
            case RIGHT:
                indicatorLayout.setGravity(Gravity.RIGHT);
                break;
            case CENTER:
                indicatorLayout.setGravity(Gravity.CENTER_HORIZONTAL);
                break;
            default:
                indicatorLayout.setGravity(Gravity.CENTER_HORIZONTAL);

        }
        drawIndicator();

    }

    private void drawIndicator() {
        if (list.size()<1) indicatorLayout.setVisibility(View.GONE);
        View dot;
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(20, 20);
        params.leftMargin = 8;
        for (int i = 0; i < list.size() - 2; i++) {
            dot = new View(getContext());
            dot.setBackgroundResource(R.drawable.dot_normal);
            if (i == 0)
                dot.getBackground().setColorFilter(this.checkedColor, PorterDuff.Mode.OVERLAY);
            else dot.getBackground().setColorFilter(this.uncheckColor, PorterDuff.Mode.OVERLAY);
            dot.setLayoutParams(params);
            indicatorLayout.addView(dot);
        }


    }

    private int currentPosition;

    public void setData(List<AdvInfo> resIds) {
        this.list=resIds;
        if (adapter == null) {
            adapter = new ViewPagerAdapter();
        }
        viewPager.setAdapter(adapter);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                lastTouchTime = System.currentTimeMillis();
                currentPosition = position;
                if (position < 1) {
                    currentPosition = list.size() - 2;
                } else if (position > (list.size() - 2)) {
                    currentPosition = 1;
                }
                selectIndicator(currentPosition - 1);
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                if (state == 0) {
                    if (currentPosition == list.size() - 2 || currentPosition == 1)
                        viewPager.setCurrentItem(currentPosition, false);
                }

            }
        });
        //设置第一张要显示的图片
        if (list != null && list.size() > 1) {
            viewPager.setCurrentItem(1);
        }

    }

    private void selectIndicator(int position) {
        int count = indicatorLayout.getChildCount();
        View view;
        for (int i = 0; i < count; i++) {
            view = indicatorLayout.getChildAt(i);
            if (i == position) {
                // view.setEnabled(true);
                view.getBackground().setColorFilter(this.checkedColor, PorterDuff.Mode.OVERLAY);
            } else {
                // view.setEnabled(false);
                view.getBackground().setColorFilter(this.uncheckColor, PorterDuff.Mode.OVERLAY);
            }
        }
    }

    public void notifyDataSetChange() {
        if (adapter != null) {
            adapter.notifyDataSetChanged();
            setWheel(true);
            setIndicator(CENTER, ContextCompat.getColor(getContext(), R.color.checkedColor), ContextCompat.getColor(getContext(), R.color.uncheckColor));
            viewPager.setCurrentItem(1);
        }
    }

    private static class MyHandler extends Handler {


        @Override
        public void handleMessage(Message msg) {
            if (weakReference.get() != null) {
                ViewPagerFragment fraggment = weakReference.get();
                if (fraggment.list.size() > 0) {
                    if (msg.what == WHEEL) {

                        int position = fraggment.viewPager.getCurrentItem();
                        fraggment.viewPager.setCurrentItem((position + 1) % fraggment.list.size());
                        removeCallbacks(fraggment.task);
                        postDelayed(fraggment.task, delayTime);
                    } else if (msg.what == WHEEL_WAIT) {
                        removeCallbacks(fraggment.task);
                        postDelayed(fraggment.task, delayTime);
                    }
                }
            }
        }

        public MyHandler(ViewPagerFragment fragment) {
            weakReference = new WeakReference<ViewPagerFragment>(fragment);
        }

        private WeakReference<ViewPagerFragment> weakReference;
    }

    final Runnable task = new Runnable() {
        @Override
        public void run() {
            if (isWheel) {
                //避免手动滑动后，马上翻页，应该等到下一个延迟时间才自动滑动
                if (System.currentTimeMillis() - lastTouchTime > delayTime - 500) {
                    mHandler.sendEmptyMessage(WHEEL);
                } else {
                    mHandler.sendEmptyMessage(WHEEL_WAIT);
                }
            }
        }
    };
    private MyHandler mHandler = new MyHandler(this);

    public void setWheel(boolean isWheel) {
        this.isWheel = isWheel;
        if (isWheel && list.size() > 0) {
            mHandler.removeCallbacks(task);
            mHandler.postDelayed(task, delayTime);
        }
    }

    private class ViewPagerAdapter extends PagerAdapter {

        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view.equals((View) object);
        }

        @Override
        public Object instantiateItem(ViewGroup container, final int position) {
            View view=LayoutInflater.from(getContext()).inflate(R.layout.header_view,null);

            final ImageView imageView = (ImageView) view.findViewById(R.id.imageView);
            TextView textView= (TextView) view.findViewById(R.id.title);
            final AdvInfo item = list.get(position);
            textView.setText(item.getTitle());
            if (item.getRes() != null) {
                Glide.with(getContext())
                        .load(item.getRes())
                        .into(imageView);
            } else {
                Glide.with(getContext())
                        .load(item.getUrl())
                        .into(imageView);
            }
            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mListener != null) {
                        mListener.onClick(imageView, position, item);
                    }
                }
            });
            container.addView(view);
            return view;

        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }
    }

    public interface onImageClickListener {
        void onClick(View view, int position, AdvInfo item);
    }

    private onImageClickListener mListener;

    public void setOnImageClickListener(onImageClickListener listener) {
        this.mListener = listener;
    }

}
