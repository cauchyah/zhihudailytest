package com.zhihudailytest.Activity

import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.view.Menu
import android.webkit.WebView
import android.webkit.WebViewClient
import butterknife.ButterKnife
import com.zhihudailytest.R
import kotlinx.android.synthetic.main.activity_action_mode.*

class ActionModeActivity : BaseActivity() {





    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_action_mode)
        ButterKnife.bind(this)
        //StatusBarCompat.compat(this, ContextCompat.getColor(this,R.color.colorAccent));
        setToolbar()
        val viewClient = object : WebViewClient() {
            override fun onPageFinished(view: WebView, url: String) {
                super.onPageFinished(view, url)
                webView!!.setBackgroundColor(0)
            }
        }
        webView!!.settings.javaScriptEnabled = true
        webView!!.webViewClient = viewClient
        val color = ContextCompat.getColor(this, R.color.web_view_bg)

        //
        webView!!.loadUrl("http://daily.zhihu.com/story/4772126")
        //webView.getBackground().setAlpha(0);
        //  webView.setBackgroundColor(0); // 设置背景色
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.home_menu, menu)
        return true
    }

    override fun initView() {

    }

    override fun setListener() {

    }
}
