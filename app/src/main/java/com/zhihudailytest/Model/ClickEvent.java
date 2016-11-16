package com.zhihudailytest.Model;

import android.content.Context;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

/**
 * Created by Administrator on 2016/7/7.
 */
public class ClickEvent {

    public void loadImage(ImageView imageView, Context context){
        Glide.with(context)
                .load("http://pic2.zhimg.com/6d714ed960d980f254b2195ddea99236.jpg")
                .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                .skipMemoryCache(true)
                .into(imageView);
    }
}
