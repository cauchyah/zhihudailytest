package com.zhihudailytest.Fragment;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.umeng.socialize.UMAuthListener;
import com.umeng.socialize.UMShareAPI;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.zhihudailytest.R;
import com.zhihudailytest.Utils.SPUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;

import butterknife.ButterKnife;
import butterknife.OnClick;


public class LoginFragment extends BaseFragment {

    private UMShareAPI umShareAPI;
    public LoginFragment() {
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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        return inflater.inflate(R.layout.fragment_login, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this,view);
        umShareAPI= UMShareAPI.get(getContext());
    }

   String bind;
    SHARE_MEDIA platform;
    @OnClick(R.id.sinaLogin)
    public void onSinaLogin(){
        platform=SHARE_MEDIA.SINA;
        bind="sina";
        umShareAPI.doOauthVerify(getActivity(),SHARE_MEDIA.SINA,umAuthListener);
    };
    @OnClick(R.id.qqLogin)
    public void onQQLogin(){
        platform= SHARE_MEDIA.QQ;
        bind="qq";
        umShareAPI.doOauthVerify(getActivity(),platform,umAuthListener);
    };
    private UMAuthListener umAuthListener=new UMAuthListener() {
        @Override
        public void onComplete(SHARE_MEDIA share_media, int i, Map<String, String> map) {

            umShareAPI.getPlatformInfo(getActivity(),platform,listener);
        }

        @Override
        public void onError(SHARE_MEDIA share_media, int i, Throwable throwable) {
            showToast("授权失败");
        }

        @Override
        public void onCancel(SHARE_MEDIA share_media, int i) {
            showToast("cancel");
        }
    };

    private UMAuthListener listener=new UMAuthListener() {
        @Override
        public void onComplete(SHARE_MEDIA share_media, int i, Map<String, String> map) {
            SharedPreferences.Editor editor = SPUtils.getEditor();
            editor.putString("bind",bind);
            // show.setText(map.get("result"));
            if (share_media==SHARE_MEDIA.SINA){
                String result=map.get("result");
                try {
                    JSONObject object=new JSONObject(result);
                    editor.putString("name", object.getString("name"));
                    editor.putString("avatar", object.getString("profile_image_url"));
                    editor.putBoolean("status", true);
                    editor.putString("uid", object.getString("idstr"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            else if (share_media==SHARE_MEDIA.QQ){
                editor.putString("name", map.get("screen_name"));
                editor.putString("avatar", map.get("profile_image_url"));
                editor.putBoolean("status", true);
                editor.putString("uid", map.get("openid"));
            }
            editor.commit();
            //发送广播
            localBroadcastManager= LocalBroadcastManager.getInstance(getContext());
            Intent intent=new Intent("login");
            localBroadcastManager.sendBroadcast(intent);
            getActivity().finish();



        }

        @Override
        public void onError(SHARE_MEDIA share_media, int i, Throwable throwable) {
            showToast("授权失败");
        }

        @Override
        public void onCancel(SHARE_MEDIA share_media, int i) {

        }
    };
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        umShareAPI.onActivityResult( requestCode, resultCode, data);
    }
}
