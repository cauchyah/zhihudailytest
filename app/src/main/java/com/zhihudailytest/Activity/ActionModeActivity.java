package com.zhihudailytest.Activity;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.view.ActionMode;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.zhihudailytest.R;
import com.zhihudailytest.Utils.StatusBarCompat;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ActionModeActivity extends BaseActivity {


    @Bind(R.id.webView)
    WebView webView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_action_mode);
        ButterKnife.bind(this);
        //StatusBarCompat.compat(this, ContextCompat.getColor(this,R.color.colorAccent));
        setToolbar();
        WebViewClient viewClient=new WebViewClient(){
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                webView.setBackgroundColor(0);
            }
        };
        webView.getSettings().setJavaScriptEnabled(true);
        webView.setWebViewClient(viewClient);
        int color =ContextCompat.getColor(this,R.color.web_view_bg);

        //
        webView.loadUrl("http://daily.zhihu.com/story/4772126");
        //webView.getBackground().setAlpha(0);
      //  webView.setBackgroundColor(0); // 设置背景色
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.home_menu,menu);
        return true;
    }

    @Override
    protected void initView() {

    }

    @Override
    protected void setListener() {

    }
}
