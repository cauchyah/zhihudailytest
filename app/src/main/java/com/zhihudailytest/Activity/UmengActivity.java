package com.zhihudailytest.Activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.widget.EditText;

import com.umeng.socialize.ShareAction;
import com.umeng.socialize.UMAuthListener;
import com.umeng.socialize.UMShareAPI;
import com.umeng.socialize.UMShareListener;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.media.UMImage;
import com.umeng.socialize.shareboard.SnsPlatform;
import com.umeng.socialize.utils.ShareBoardlistener;
import com.zhihudailytest.R;
import com.zhihudailytest.Utils.SPUtils;

import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class UmengActivity extends BaseActivity{
    final SHARE_MEDIA[] displaylist = new SHARE_MEDIA[]
            {
                    SHARE_MEDIA.WEIXIN, SHARE_MEDIA.WEIXIN_CIRCLE,SHARE_MEDIA.SINA,
                    SHARE_MEDIA.QQ, SHARE_MEDIA.QZONE
            };
    @BindView(R.id.show)
    EditText show;
    @OnClick(R.id.share)
    public void onShare(){

        ShareAction action = new ShareAction(UmengActivity.this);
        action.setDisplayList(displaylist);
       /* action.withTitle("分享标题");
        action.withText("分享文本内容");
        action.withTargetUrl("http://blog.xiongit.com");//点击分享内容打开的链接*/
      //  action.withMedia(umImage);//附带的图片，音乐，视频等多媒体对象
        action.setShareboardclickCallback(mShareBoardlistener);//设置友盟集成的分享面板的点击监听回调
        action.open();//打开集成的分享面板

    };
    @OnClick(R.id.auth)
    public  void onAuth(){
        umShareAPI.doOauthVerify(UmengActivity.this,SHARE_MEDIA.QQ,umAuthListener);

    };
    @OnClick(R.id.deauth)
    public void onDeAuth(){
          umShareAPI.deleteOauth(UmengActivity.this,SHARE_MEDIA.QQ,deListener);

    };
    private UMAuthListener deListener=new UMAuthListener() {
        @Override
        public void onComplete(SHARE_MEDIA share_media, int i, Map<String, String> map) {
            showToast("delete Oauth success");
            SharedPreferences.Editor editor = SPUtils.getEditor();
            editor.remove("name");

            editor.remove("avatar");
            editor.putBoolean("status", false);
            editor.remove("uid");
            editor.commit();
        }

        @Override
        public void onError(SHARE_MEDIA share_media, int i, Throwable throwable) {
            showToast("delete Oauth onError"+throwable.toString());
        }

        @Override
        public void onCancel(SHARE_MEDIA share_media, int i) {
            showToast("delete Oauth onCancel");
        }
    };

  private  UMAuthListener listener=new UMAuthListener() {
      @Override
      public void onComplete(SHARE_MEDIA share_media, int i, Map<String, String> map) {
                showToast("sssss");

                show.setText(map.get("screen_name"));
        /*       String result=map.get("result");
          try {
              JSONObject object=new JSONObject(result);
              SharedPreferences.Editor editor = SPUtils.getEditor();
              editor.putString("name", object.getString("name"));
              editor.putString("avatar", object.getString("profile_image_url"));
              editor.putBoolean("status", true);
              editor.putString("uid", object.getString("idstr"));
              editor.commit();
          } catch (JSONException e) {
              e.printStackTrace();
          }*/


      }

      @Override
      public void onError(SHARE_MEDIA share_media, int i, Throwable throwable) {

      }

      @Override
      public void onCancel(SHARE_MEDIA share_media, int i) {

      }
  };

  private UMAuthListener umAuthListener=new UMAuthListener() {
      @Override
      public void onComplete(SHARE_MEDIA share_media, int i, Map<String, String> map) {
          show.setText(map.get("access_token"));
          umShareAPI.getPlatformInfo(UmengActivity.this,SHARE_MEDIA.QQ,listener);
      }

      @Override
      public void onError(SHARE_MEDIA share_media, int i, Throwable throwable) {
            showToast("error");
      }

      @Override
      public void onCancel(SHARE_MEDIA share_media, int i) {
        showToast("cancel");
      }
  };

    private ShareBoardlistener mShareBoardlistener=new ShareBoardlistener() {
        @Override
        public void onclick(SnsPlatform snsPlatform, SHARE_MEDIA share_media) {
            UMImage umImage=new UMImage(UmengActivity.this, BitmapFactory.decodeResource(getResources(),R.drawable.kaka));
            ShareAction shareAction = new ShareAction(UmengActivity.this);

            shareAction.setPlatform(share_media);
            shareAction.setCallback(mUmShareListener);//设置每个平台的点击事件
            shareAction.withTitle("分享标题");
            shareAction.withText("分享文本内容");
            shareAction.withTargetUrl("http://www.baidu.com");//点击分享内容打开的链接
            shareAction.withMedia(umImage);//附带的图片，音乐，视频等多媒体对象
            shareAction.share();//发起分享，调起微信，QQ，微博客户端进行分享。
        }
    };
    /**
     * 友盟分享后事件监听器
     */
    private UMShareListener mUmShareListener = new UMShareListener() {

        @Override
        public void onResult(SHARE_MEDIA platform) {
            showToast("success");
            // TODO 分享成功
        }

        @Override
        public void onError(SHARE_MEDIA platform, Throwable t) {
            showToast("onError"+t.toString());
            // TODO 分享失败
        }

        @Override
        public void onCancel(SHARE_MEDIA platform) {
            showToast("onCancel");
            // TODO 分享取消
        }

    };

    private UMShareAPI umShareAPI;
   @Override
   protected void onCreate(@Nullable Bundle savedInstanceState) {
       super.onCreate(savedInstanceState);
       setContentView(R.layout.activity_umeng);
       ButterKnife.bind(this);
       umShareAPI=UMShareAPI.get(this);
       SharedPreferences sp=SPUtils.getSP();
       if (sp.getBoolean("status",false))
       show.setText(sp.getString("name","null")+"\n"+sp.getString("avatar","null"));

   }
































    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        umShareAPI.onActivityResult( requestCode, resultCode, data);
    }

    @Override
    protected void initView() {

    }

    @Override
    protected void setListener() {

    }
}
