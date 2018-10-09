package com.zhihudailytest

import android.app.Notification
import android.app.NotificationManager
import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.support.v4.app.FragmentTransaction
import android.support.v4.content.ContextCompat
import android.support.v4.view.GravityCompat
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AlertDialog
import android.support.v7.app.NotificationCompat
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.LinearLayoutManager
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.RemoteViews
import butterknife.ButterKnife
import com.zhihudailytest.Activity.BaseActivity
import com.zhihudailytest.Adapter.DrawerAdapter
import com.zhihudailytest.Fragment.HomeFragment
import com.zhihudailytest.Fragment.ThemeFragment
import com.zhihudailytest.Http.RetrofitManager
import com.zhihudailytest.Utils.LogUtil
import com.zhihudailytest.Utils.NetworkUtil
import kotlinx.android.synthetic.main.activity_main.*
import okhttp3.Call
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Request
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import java.io.BufferedOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.util.*
import java.util.concurrent.TimeUnit


class MainActivityOld : BaseActivity(), DrawerAdapter.ItemClickListener, DrawerAdapter.HeaderOnClickListener {

 
  
    var mLinearLayoutManager: LinearLayoutManager?=null
     var drawerToggle: ActionBarDrawerToggle? = null

     var transaction: FragmentTransaction? = null
     val isFirst = true

    private var notificationManager: NotificationManager? = null
    private var remoteView: RemoteViews? = null
    private var notification: Notification? = null

    private var drawerAdapter: DrawerAdapter? = null


    private var oldPosition = 1
    private var cardView: View? = null
    private var old: View? = null


    internal var countTask: Int = 0
    internal var currentTask: Int = 0
    private val handler = object : Handler() {
        override fun handleMessage(msg: Message) {
            if (msg.what > 0 && msg.what <= 100) {
                remoteView!!.setProgressBar(R.id.progressBar, 100, msg.what, false)
                remoteView!!.setTextViewText(R.id.progressText, msg.what.toString() + "%")
                notification!!.contentView = remoteView
                notificationManager!!.notify(1, notification)
                if (msg.what == 100) {
                    postDelayed({ notificationManager!!.cancel(1) }, 2000)
                }

            }

            super.handleMessage(msg)
        }
    }
    private var okHttpClient: OkHttpClient? = null
    private val calls = ArrayList<Call>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        //setTranslucent(this);
//        settoolBar()
        ButterKnife.bind(this)
        initView()
        setDrawerTranslucent(container_layout, this)

        drawerAdapter = DrawerAdapter(this, 1)
        drawer!!.adapter = drawerAdapter
        drawerAdapter!!.headerOnClickListener = this
        setListener()
    }





    override fun initView() {
        mLinearLayoutManager = LinearLayoutManager(this)
        drawer.layoutManager = mLinearLayoutManager
        drawer!!.itemAnimator = DefaultItemAnimator()
        drawer!!.setHasFixedSize(true)
        fragmentManager = supportFragmentManager
        transaction = fragmentManager!!.beginTransaction()
        transaction!!.replace(R.id.contentPanel, HomeFragment(), "position1")

        transaction!!.commit()
    }

    override fun setListener() {
        drawerAdapter!!.setmItemClickListener(this)
        drawerToggle = object : ActionBarDrawerToggle(this, container_layout,
                R.string.drawer_open, R.string.drawer_close) {
            override fun onDrawerStateChanged(newState: Int) {
                super.onDrawerStateChanged(newState)
            }

            override fun onDrawerOpened(drawerView: View?) {
                super.onDrawerOpened(drawerView)
                supportInvalidateOptionsMenu()
            }

            override fun onDrawerClosed(drawerView: View?) {
                super.onDrawerClosed(drawerView)
                supportInvalidateOptionsMenu()
                if (oldPosition == 1)
                    drawer!!.scrollToPosition(0)
            }
        }
        drawerToggle!!.syncState()
        container_layout!!.addDrawerListener(drawerToggle!!)
        toolBar!!.setNavigationOnClickListener(View.OnClickListener {
            if (container_layout!!.isDrawerOpen(GravityCompat.START))
                container_layout!!.closeDrawer(GravityCompat.START)
            else
                container_layout!!.openDrawer(GravityCompat.START)
        })


    }


    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // LogUtil.d("onCreateOptionsMenu");
        // boolean isOpen = container_layout.isDrawerVisible(drawer);
        if (oldPosition == 1) {
            menuInflater.inflate(R.menu.home_menu, menu)
            return true
        }
        return super.onCreateOptionsMenu(menu)
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        /* if (drawerToggle.onOptionsItemSelected(item)) {
            container_layout.post(new Runnable() {
                @Override
                public void run() {
                    supportInvalidateOptionsMenu();
                }
            });
            return true;
        }*/
        return super.onOptionsItemSelected(item)
    }

    override fun onItemClick(position: Int) {
        changeItemBackground(position)


    }

    private fun changeItemBackground(position: Int) {
        if (container_layout!!.isDrawerVisible(drawer!!))
            container_layout!!.closeDrawers()
        //更换背景
        if (position != oldPosition) {
            cardView = mLinearLayoutManager!!.findViewByPosition(position)
            cardView!!.setBackgroundColor(ContextCompat.getColor(this, R.color.background))
            old = mLinearLayoutManager!!.findViewByPosition(oldPosition)
            old!!.setBackgroundColor(ContextCompat.getColor(this, R.color.white))
            oldPosition = position
            //打开对应的fragment
            transaction = fragmentManager!!.beginTransaction()
            if (position != 1) {
                transaction!!.replace(R.id.contentPanel, ThemeFragment.getInstance(
                        drawerAdapter!!.getThemeId(position), drawerAdapter!!.getThemeTitle(position)), "position$position")
            } else {
                transaction!!.replace(R.id.contentPanel, HomeFragment(), "position1")
            }
            transaction!!.commit()

        }


    }

    private fun onHome() {
        //回到首页
        changeItemBackground(1)
        supportInvalidateOptionsMenu()
        // }
    }

    override fun onBackPressed() {
        if (container_layout!!.isDrawerVisible(drawer!!))
            container_layout!!.closeDrawers()
        else if (oldPosition != 1) {
            onHome()
        } else
            super.onBackPressed()
    }

    override fun onClick(view: View) {
        val builder = AlertDialog.Builder(this)

        if (!NetworkUtil.isWifiConnected) {
            builder.setTitle("离线下载")
                    .setMessage("当前为非wifi网络,是否要下载？")
                    .setNegativeButton("取消") { dialog, which -> }
                    .setPositiveButton("确定") { dialog, which -> download(view) }.show()
        } else {
            //wifi状态不提示
            download(view)
        }
    }

    private fun download(view: View) {

        notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val builder = NotificationCompat.Builder(this)
        notification = builder
                .setSmallIcon(R.mipmap.ic_launcher)
                .setWhen(System.currentTimeMillis())
                .build()
        remoteView = RemoteViews(packageName, R.layout.layout_remote_view)
        //    remoteView.setTextViewText(R.id.progressBar,"dsdsd");
        remoteView!!.setProgressBar(R.id.progressBar, 100, 0, false)
        notification!!.contentView = remoteView

        notification!!.tickerText = "开始离线下载"
        //PendingIntent intent=PendingIntent.getActivity(this,1,new Intent(MainActivityOld.this,MainActivityOld.class),PendingIntent);
        notificationManager!!.notify(1, notification)
        //downloadFile("http://image4.90e.com/image/960x600/6326.jpg",10001);
        val observable = RetrofitManager.instance.lastNews
        observable.subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())

                .unsubscribeOn(AndroidSchedulers.mainThread())
                .subscribe({ newsBean ->
                    countTask = newsBean.stories!!.size
                    currentTask = 0
                    //LogUtil.d("countTask"+countTask);
                    //int i=0;
                    for (story in newsBean.stories!!) {
                        //i++;
                        //LogUtil.d("in"+i);
                        RetrofitManager.instance
                                .getStoryDetail(story.id)
                                .subscribeOn(Schedulers.io())
                                .observeOn(Schedulers.io())

                                .unsubscribeOn(AndroidSchedulers.mainThread())
                                .subscribe { storyDetail ->
                                    //  LogUtil.d("call");
                                    downloadFile(storyDetail.image!!, storyDetail.id)
                                }

                    }
                }, { showToast("离线下载失败") })

    }

    // private  File root=new File(getCacheDir(),"image");
    @Synchronized
    private fun downloadFile(url: String, id: Int) {
        if (okHttpClient == null) {
            okHttpClient = OkHttpClient.Builder().connectTimeout(15, TimeUnit.SECONDS)
                    .retryOnConnectionFailure(true)
                    .readTimeout(1, TimeUnit.MINUTES)
                    .build()

        }
        val root = File(cacheDir, "image")
        if (!root.exists()) root.mkdir()
        val file = File(root, "image" + id + "file")
        // LogUtil.d(root.getAbsolutePath()+",root,"+file.getAbsolutePath()+"file path");
        if (file.exists()) {
            currentTask++
            return
        }

        //file.delete();
        try {
            file.createNewFile()
        } catch (e: IOException) {
            e.printStackTrace()
        }

        val request = Request.Builder()
                .url(url)
                .build()
        val call = okHttpClient!!.newCall(request)

        calls.add(call)
        call.enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {}

            @Throws(IOException::class)
            override fun onResponse(call: Call, response: okhttp3.Response) {
                val count = response.body().contentLength().toInt()
                val inputStream = response.body().byteStream()

                val fos = FileOutputStream(file)
                val bos = BufferedOutputStream(fos, 2 * 1024 * 1024)
                val bytes = ByteArray(10 * 1024)

                var current = 0
                var percent: Int
                currentTask++
                LogUtil.d("currentTask$currentTask")
                var length: Int=inputStream.read(bytes)
                while (length != -1) {
                    bos.write(bytes, 0, length)

                    current += length
                    percent = (100 * (current / count * (currentTask * 1.0f / countTask))).toInt()
                    //percent = 100*current/count;
                    handler.sendEmptyMessage(percent)
                    length=inputStream.read(bytes)

                }
                bos.flush()
                bos.close()
                inputStream.close()
                fos.close()
            }

        })


    }

    override fun onDestroy() {
        super.onDestroy()
        for (call in calls) {
            if (!call.isExecuted)
                call.cancel()
        }
    }
}
