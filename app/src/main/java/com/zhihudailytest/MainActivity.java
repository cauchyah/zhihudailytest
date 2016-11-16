package com.zhihudailytest;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.app.NotificationCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.RemoteViews;

import com.zhihudailytest.Activity.BaseActivity;
import com.zhihudailytest.Adapter.DrawerAdapter;
import com.zhihudailytest.Bean.NewsBean;
import com.zhihudailytest.Bean.Story;
import com.zhihudailytest.Bean.StoryDetail;
import com.zhihudailytest.Fragment.BaseFragment;
import com.zhihudailytest.Fragment.HomeFragment;
import com.zhihudailytest.Fragment.ThemeFragment;
import com.zhihudailytest.Http.RetrofitManager;
import com.zhihudailytest.Utils.LogUtil;
import com.zhihudailytest.Utils.NetworkUtil;
import com.zhihudailytest.Utils.SPUtils;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import butterknife.Bind;
import butterknife.ButterKnife;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;


public class MainActivity extends BaseActivity implements DrawerAdapter.ItemClickListener,
        DrawerAdapter.HeaderOnClickListener,BaseActivity.LoginOrLogoutListener {


    @Bind(R.id.container_layout)
    DrawerLayout mDrawerContainer;
    @Bind(R.id.drawer)
    RecyclerView mDrawerContent;
    LinearLayoutManager mLinearLayoutManager;
    private ActionBarDrawerToggle drawerToggle;
    private int oldPosition = 1;
    private NotificationManager notificationManager;
    private RemoteViews remoteView;
    private Notification notification;
    private DrawerAdapter drawerAdapter;
    private HomeFragment homeFragment;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        LogUtil.e("abcd","oncreate");
        ButterKnife.bind(this);
        setToolbar();
        setDrawerTranslucent(mDrawerContainer, this);
        fragmentManager = getSupportFragmentManager();
        if (savedInstanceState!=null){
            fragmentTransaction=fragmentManager.beginTransaction();
            homeFragment= (HomeFragment) fragmentManager.findFragmentByTag("position1");
            oldPosition=savedInstanceState.getInt("oldPosition",1);
            hideAllFragement(fragmentTransaction,oldPosition);
            fragmentTransaction.commit();

        }
        initView();
        drawerAdapter = new DrawerAdapter(this,oldPosition);
        mDrawerContent.setAdapter(drawerAdapter);
        setListener();
    }
    @Override
    protected void initView() {

        mLinearLayoutManager = new LinearLayoutManager(this);
        mDrawerContent.setLayoutManager(mLinearLayoutManager);
        mDrawerContent.setItemAnimator(new DefaultItemAnimator());
        mDrawerContent.setHasFixedSize(true);
        fragmentTransaction = fragmentManager.beginTransaction();
        if (homeFragment==null) {
            homeFragment = new HomeFragment();
            fragmentTransaction.add(R.id.contentPanel, homeFragment, "position1");
        }
        if (oldPosition==1)
            fragmentTransaction.show(homeFragment);
        fragmentTransaction.commit();

    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private long lastClick=0;
    @Override
    protected void setListener() {
        drawerAdapter.setHeaderOnClickListener(this);
        setListener(this);
        drawerAdapter.setmItemClickListener(this);
        drawerToggle = new ActionBarDrawerToggle(this, mDrawerContainer,
                R.string.drawer_open, R.string.drawer_close) {
            @Override
            public void onDrawerStateChanged(int newState) {
                super.onDrawerStateChanged(newState);
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                supportInvalidateOptionsMenu();
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                supportInvalidateOptionsMenu();
                if (oldPosition == 1)
                    mDrawerContent.scrollToPosition(0);
            }
        };
        drawerToggle.syncState();
        mDrawerContainer.addDrawerListener(drawerToggle);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mDrawerContainer.isDrawerOpen(GravityCompat.START)) {
                    mDrawerContainer.closeDrawer(GravityCompat.START);

                }
                else {
                    mDrawerContainer.openDrawer(GravityCompat.START);
                }
            }
        });
        toolbar.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                long now=System.currentTimeMillis();
                if (lastClick==0)
                    lastClick=now;
                else if(now-lastClick<500) {
                    BaseFragment fragment = (BaseFragment) fragmentManager.findFragmentByTag("position" + oldPosition);
                    fragment.onDoubleClick();
                }
                lastClick=now;
            }
        });
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("oldPosition",oldPosition);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (oldPosition == 1) {
            getMenuInflater().inflate(R.menu.home_menu, menu);
            return true;
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_mode:
                SharedPreferences sp=SPUtils.getThemeSP();
               boolean isNight=sp.getBoolean("isNight",false);
                SharedPreferences.Editor editor=SPUtils.getThemeEditor();
                if (isNight){
                    editor.putBoolean("isNight",false);
                    getDelegate().setLocalNightMode(AppCompatDelegate.MODE_NIGHT_NO);

                }
                else {
                    editor.putBoolean("isNight", true);

                    getDelegate().setLocalNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                }
                editor.commit();
                recreate();
                return true;
        }
        return super.onOptionsItemSelected(item);

    }

    @Override
    public void onItemClick(int position) {
        changeItemBackground(position);
    }
    private void changeItemBackground(int position) {
        if (mDrawerContainer.isDrawerVisible(mDrawerContent))
            mDrawerContainer.closeDrawers();
        //更换背景
        if (position != oldPosition) {
            drawerAdapter.changeItem(position,oldPosition);
            oldPosition = position;
            //打开对应的fragment
            fragmentTransaction = fragmentManager.beginTransaction();
            hideAllFragement(fragmentTransaction,-1);
            if (position != 1) {
                ThemeFragment fragment= (ThemeFragment) fragmentManager.findFragmentByTag("position"+position);
                if (fragment!=null){
                    fragmentTransaction.show(fragment);

                }
                else {
                    fragmentTransaction.add(R.id.contentPanel, ThemeFragment.getInstance(
                            drawerAdapter.getThemeId(position), drawerAdapter.getThemeTitle(position)), "position" + position);
                    //transaction.addToBackStack()
                }
            } else {
                fragmentTransaction.show(homeFragment);

            }
            fragmentTransaction.commit();

        }


    }
    private void hideAllFragement(FragmentTransaction transaction,int index) {

        if (homeFragment!=null)
            transaction.hide(homeFragment);
        //int count =fragmentManager.getBackStackEntryCount();
        for (int i = 2; i < 14; i++) {
            ThemeFragment fragment= (ThemeFragment) fragmentManager.findFragmentByTag("position"+i);
            if (fragment!=null&&i!=index)
                transaction.hide(fragment);
        }

    }
    private void onHome() {
        //回到首页
            changeItemBackground(1);
            supportInvalidateOptionsMenu();
    }
    @Override
    public void onBackPressed() {
        if (mDrawerContainer.isDrawerVisible(mDrawerContent))
            mDrawerContainer.closeDrawers();
        else if (oldPosition != 1) {
            onHome();
        } else super.onBackPressed();
    }
    @Override
    public void onClick(final View view) {
        AlertDialog.Builder builder= new AlertDialog.Builder(this);
        if (!NetworkUtil.isWifiConnected()){
        builder.setTitle("离线下载")
                .setMessage("当前为非wifi网络,是否要下载？")
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                })
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        download(view);
                    }
                }).show();
        }
        else {
            //wifi状态不提示
            download(view);
        }
    }
    int countTask;
    int currentTask;
    private void download(final View view){

      notificationManager= (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        NotificationCompat.Builder builder=new NotificationCompat.Builder(this);
        notification=builder
                .setSmallIcon(R.drawable.zhihu_icon)
                .setWhen(System.currentTimeMillis())
                .build();
        remoteView=new RemoteViews(getPackageName(),R.layout.layout_remote_view);
    //    remoteView.setTextViewText(R.id.progressBar,"dsdsd");
        remoteView.setProgressBar(R.id.progressBar,100,0,false);
        notification.contentView=remoteView;

        notification.tickerText="开始离线下载";
        //PendingIntent intent=PendingIntent.getActivity(this,1,new Intent(MainActivityOld.this,MainActivityOld.class),PendingIntent);
       notificationManager.notify(1,notification);

        downloadNews();
    }
    private void downloadNews(){
        Observable<NewsBean> observable= RetrofitManager.getInstance().getLastNews();
        Subscription subscription=observable.subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .unsubscribeOn(AndroidSchedulers.mainThread())
                .flatMap(new Func1<NewsBean, Observable<Story>>() {
                    @Override
                    public Observable<Story> call(NewsBean newsBean) {
                        countTask=newsBean.getStories().size();
                        currentTask=0;
                        LogUtil.d("call call call");
                       return Observable.from(newsBean.getStories());
                    }
                })
                .flatMap(new Func1<Story, Observable<StoryDetail>>() {
                    @Override
                    public Observable<StoryDetail> call(Story story) {
                        return   RetrofitManager.getInstance().getStoryDetail(story.getId());
                    }
                })
                .subscribe(new Action1<StoryDetail>() {
                    @Override
                    public void call(StoryDetail storyDetail) {
                        downloadFile(storyDetail.getImage(),storyDetail.getId());
                    }
                });

               /* .subscribe(new Action1<NewsBean>() {
                    @Override
                    public void call(NewsBean newsBean) {
                        countTask=newsBean.getStories().size();
                        currentTask=0;
                        for (Story story : newsBean.getStories()) {
                            RetrofitManager.getInstance()
                                    .getStoryDetail(story.getId())
                                    .subscribeOn(Schedulers.io())
                                    .observeOn(Schedulers.io())
                                    .unsubscribeOn(AndroidSchedulers.mainThread())
                                    .subscribe(new Action1<StoryDetail>() {
                                        @Override
                                        public void call(StoryDetail storyDetail) {
                                            //  LogUtil.d("call");
                                            downloadFile(storyDetail.getImage(),storyDetail.getId());
                                        }
                                    });
                        }
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        showToast("离线下载失败");
                    }
                });*/
    }
   private Handler handler=new Handler(){
       @Override
       public void handleMessage(Message msg) {
           if (msg.what>0&&msg.what<=100){
               remoteView.setProgressBar(R.id.progressBar,100,msg.what,false);
               remoteView.setTextViewText(R.id.progressText,msg.what+"%");
               notification.contentView=remoteView;
               notificationManager.notify(1,notification);
               if (msg.what==100){
                   postDelayed(new Runnable() {
                       @Override
                       public void run() {
                           notificationManager.cancel(1);
                       }
                   },2000);
               }

           }

           super.handleMessage(msg);
       }
   };
    private OkHttpClient okHttpClient;
    private List<Call> calls=new ArrayList<Call>();
    private synchronized void   downloadFile(String url, final int id){
        if (okHttpClient==null) {
            okHttpClient = new OkHttpClient.Builder().connectTimeout(15,TimeUnit.SECONDS)
                    .retryOnConnectionFailure(true)
                    .readTimeout(1,TimeUnit.MINUTES)
                    .build();

        }
        File root=new File(getCacheDir(),"image");
        if (!root.exists()) root.mkdir();
        final File file=new File(root,"image"+id+"file");
       // LogUtil.d(root.getAbsolutePath()+",root,"+file.getAbsolutePath()+"file path");
        if (file.exists()){
            currentTask++;
            handler.sendEmptyMessage((int) (currentTask/(countTask*1.0f)*100));
            return;
        }

            //file.delete();
        try {
            file.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        final Request request=new Request.Builder()
                .url(url)
                .build();
        Call call=okHttpClient.newCall(request);

        calls.add(call);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
            }

            @Override
            public void onResponse(Call call, okhttp3.Response response) throws IOException {
                int count= (int) response.body().contentLength();
                InputStream inputStream=response.body().byteStream();

                FileOutputStream fos=new FileOutputStream(file);
                BufferedOutputStream bos=new BufferedOutputStream(fos,2*1024*1024);
                byte[] bytes=new byte[10*1024];
                int length;
                int current=0;
                int percent;
                currentTask++;
                LogUtil.d("currentTask"+currentTask);
                while ((length=inputStream.read(bytes))!=-1){
                    bos.write(bytes,0,length);

                        current += length;
                        percent = (int) (100*(current/count*(currentTask*1.0f/countTask)));
                        //percent = 100*current/count;
                        handler.sendEmptyMessage(percent);

                }
                bos.flush();
                bos.close();
                inputStream.close();
                fos.close();
            }
        });
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        LogUtil.e("abcd","on destroy");
        handler.removeCallbacksAndMessages(null);
        for (Call call:calls){
            if (!call.isExecuted())
                call.cancel();
        }
    }

    @Override
    public void onReceive(Intent intent, Context context) {
        drawerAdapter.notifyItemRangeChanged(0, 1);
    }

}
