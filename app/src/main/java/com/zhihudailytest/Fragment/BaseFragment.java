package com.zhihudailytest.Fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Toast;

import com.zhihudailytest.Activity.BaseActivity;
import com.zhihudailytest.R;
import com.zhihudailytest.Utils.NetworkUtil;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Administrator on 2016/7/25.
 */
public abstract class BaseFragment extends Fragment {
    protected LocalBroadcastManager localBroadcastManager;
    protected ReadChangeReceiver receiver;
   // @Bind(R.id.swipeRefreshLayout)
    protected SwipeRefreshLayout swipeRefreshLayout;
   protected Toolbar toolbar;
    protected LinearLayoutManager mLinearLayoutManager;
    protected BaseActivity activity;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    public LinearLayoutManager getLayoutManager(){
        return mLinearLayoutManager;
    }

    public   abstract void onDoubleClick();



    protected void setSwipeRefreshLayout(View view){
        swipeRefreshLayout= (SwipeRefreshLayout) view.findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.setColorSchemeColors(ContextCompat.getColor(getContext(),R.color.colorPrimary));
        SwipeRefreshLayout.OnRefreshListener listener=new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (NetworkUtil.isNetworkConnected())
                    loadData();
                else swipeRefreshLayout.setRefreshing(false);
            }
        };
        swipeRefreshLayout.setOnRefreshListener(listener);
        //手动下拉刷新
        swipeRefreshLayout.post(new Runnable(){
            @Override
            public void run() {
                swipeRefreshLayout.setRefreshing(true);
                loadData();
            }
        });
    }

    abstract void loadData();

    protected void  registerReceiver(){
        IntentFilter intentFilter=new IntentFilter("readChange");
        localBroadcastManager=LocalBroadcastManager.getInstance(getContext());
        receiver=new ReadChangeReceiver();
        localBroadcastManager.registerReceiver(receiver, intentFilter);
    }
    class ReadChangeReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            onBroadcastReceive(context,intent);

        }
    }
    abstract void onBroadcastReceive(Context context,Intent intent);

    protected void showToast(String msg){
        Toast.makeText(getContext(),msg,Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (receiver!=null)
        localBroadcastManager.unregisterReceiver(receiver);
    }
}
