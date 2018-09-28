package com.zhihudailytest.Fragment;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.umeng.socialize.UMAuthListener;
import com.umeng.socialize.UMShareAPI;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.zhihudailytest.Activity.BaseActivity;
import com.zhihudailytest.R;
import com.zhihudailytest.Utils.SPUtils;

import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import jp.wasabeef.glide.transformations.CropCircleTransformation;


public class LogoutFragment extends BaseFragment {
    private UMShareAPI umShareAPI;

    public LogoutFragment() {
        // Required empty public constructor
    }



    @Override
    public void onDoubleClick() {

    }

    @Override
    void loadData() {

    }

    @Override
    void onBroadcastReceive(Context context, Intent intent) {

    }

    @BindView(R.id.avatar)
    ImageView avatar;
    @BindView(R.id.sina)
    TextView sina;
    @BindView(R.id.qq)
    TextView qq;
    @BindView(R.id.name)
    TextView name;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_logout, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this,view);
        init();
    }

    private String platform;
    private void init() {
        toolbar=((BaseActivity)getActivity()).getToolbar();
        toolbar.setTitle("账号");
        umShareAPI= UMShareAPI.get(getContext());
        SharedPreferences sp=SPUtils.getSP();
        platform=sp.getString("bind","");
        name.setText(sp.getString("name","null"));
        qq.setTextColor(ContextCompat.getColor(getContext(),R.color.light_blue));
        Glide.with(getContext())
                .load(sp.getString("avatar",""))
                .bitmapTransform(new CropCircleTransformation(getContext()))
                .into(avatar);
        if (TextUtils.equals(platform,"sina")){
            //绑定新浪
            sina.setText("解绑新浪微博");
            sina.setTextColor(ContextCompat.getColor(getContext(),R.color.gray));
            qq.setText("绑定QQ登录");
            qq.setTextColor(ContextCompat.getColor(getContext(),R.color.light_blue));
        }
        else if (TextUtils.equals(platform,"qq")){
            //绑定QQ
            sina.setText("绑定新浪微博");
            sina.setTextColor(ContextCompat.getColor(getContext(),R.color.light_blue));
            qq.setText("解绑QQ登录");
            qq.setTextColor(ContextCompat.getColor(getContext(),R.color.gray));
        }
    }
    @OnClick(R.id.logout)
    public  void onLogout(){
        SHARE_MEDIA media;
        if (TextUtils.equals(platform,"qq"))
            media= SHARE_MEDIA.QQ;
        else  media=SHARE_MEDIA.SINA;
        umShareAPI.deleteOauth(getActivity(),media,deListener);
    }
    private UMAuthListener deListener=new UMAuthListener() {
        @Override
        public void onComplete(SHARE_MEDIA share_media, int i, Map<String, String> map) {
            showToast("注销成功");
            SharedPreferences.Editor editor = SPUtils.getEditor();
            editor.remove("name");

            editor.remove("avatar");
            editor.putBoolean("status", false);
            editor.remove("uid");
            editor.remove("bind");
            editor.commit();
            //发送广播
            localBroadcastManager= LocalBroadcastManager.getInstance(getContext());
            Intent intent=new Intent("logout");
            localBroadcastManager.sendBroadcast(intent);
            getActivity().finish();
        }

        @Override
        public void onError(SHARE_MEDIA share_media, int i, Throwable throwable) {
            showToast("注销失败");
        }

        @Override
        public void onCancel(SHARE_MEDIA share_media, int i) {
            showToast("delete Oauth onCancel");
        }
    };

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        umShareAPI.onActivityResult( requestCode, resultCode, data);
    }
}
