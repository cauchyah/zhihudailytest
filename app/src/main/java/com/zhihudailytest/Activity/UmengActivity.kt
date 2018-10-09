package com.zhihudailytest.Activity

import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Bundle

import com.umeng.socialize.ShareAction
import com.umeng.socialize.UMAuthListener
import com.umeng.socialize.UMShareAPI
import com.umeng.socialize.UMShareListener
import com.umeng.socialize.bean.SHARE_MEDIA
import com.umeng.socialize.media.UMImage
import com.umeng.socialize.utils.ShareBoardlistener
import com.zhihudailytest.R
import com.zhihudailytest.Utils.SPUtils

import butterknife.ButterKnife
import butterknife.OnClick
import kotlinx.android.synthetic.main.activity_umeng.*


class UmengActivity : BaseActivity() {
    internal val displaylist = arrayOf(SHARE_MEDIA.WEIXIN, SHARE_MEDIA.WEIXIN_CIRCLE, SHARE_MEDIA.SINA, SHARE_MEDIA.QQ, SHARE_MEDIA.QZONE)

    private val deListener = object : UMAuthListener {
        override fun onComplete(share_media: SHARE_MEDIA, i: Int, map: Map<String, String>) {
            showToast("delete Oauth success")
            val editor = SPUtils.editor
            editor.remove("name")

            editor.remove("avatar")
            editor.putBoolean("status", false)
            editor.remove("uid")
            editor.commit()
        }

        override fun onError(share_media: SHARE_MEDIA, i: Int, throwable: Throwable) {
            showToast("delete Oauth onError" + throwable.toString())
        }

        override fun onCancel(share_media: SHARE_MEDIA, i: Int) {
            showToast("delete Oauth onCancel")
        }
    }

    private val umengListener = object : UMAuthListener {
        override fun onComplete(share_media: SHARE_MEDIA, i: Int, map: Map<String, String>) {
            showToast("sssss")

            show!!.setText(map["screen_name"])
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

        override fun onError(share_media: SHARE_MEDIA, i: Int, throwable: Throwable) {

        }

        override fun onCancel(share_media: SHARE_MEDIA, i: Int) {

        }
    }

    private val umAuthListener = object : UMAuthListener {
        override fun onComplete(share_media: SHARE_MEDIA, i: Int, map: Map<String, String>) {
            show!!.setText(map["access_token"])
            umShareAPI!!.getPlatformInfo(this@UmengActivity, SHARE_MEDIA.QQ, umengListener)
        }

        override fun onError(share_media: SHARE_MEDIA, i: Int, throwable: Throwable) {
            showToast("error")
        }

        override fun onCancel(share_media: SHARE_MEDIA, i: Int) {
            showToast("cancel")
        }
    }

    private val mShareBoardlistener = ShareBoardlistener { snsPlatform, share_media ->
        val umImage = UMImage(this@UmengActivity, BitmapFactory.decodeResource(resources, R.drawable.kaka))
        val shareAction = ShareAction(this@UmengActivity)

        shareAction.platform = share_media
        shareAction.setCallback(mUmShareListener)//设置每个平台的点击事件
        shareAction.withTitle("分享标题")
        shareAction.withText("分享文本内容")
        shareAction.withTargetUrl("http://www.baidu.com")//点击分享内容打开的链接
        shareAction.withMedia(umImage)//附带的图片，音乐，视频等多媒体对象
        shareAction.share()//发起分享，调起微信，QQ，微博客户端进行分享。
    }
    /**
     * 友盟分享后事件监听器
     */
    private val mUmShareListener = object : UMShareListener {

        override fun onResult(platform: SHARE_MEDIA) {
            showToast("success")
            // TODO 分享成功
        }

        override fun onError(platform: SHARE_MEDIA, t: Throwable) {
            showToast("onError" + t.toString())
            // TODO 分享失败
        }

        override fun onCancel(platform: SHARE_MEDIA) {
            showToast("onCancel")
            // TODO 分享取消
        }

    }

    private var umShareAPI: UMShareAPI? = null
    @OnClick(R.id.share)
    fun onShare() {

        val action = ShareAction(this@UmengActivity)
        action.setDisplayList(*displaylist)
        /* action.withTitle("分享标题");
        action.withText("分享文本内容");
        action.withTargetUrl("http://blog.xiongit.com");//点击分享内容打开的链接*/
        //  action.withMedia(umImage);//附带的图片，音乐，视频等多媒体对象
        action.setShareboardclickCallback(mShareBoardlistener)//设置友盟集成的分享面板的点击监听回调
        action.open()//打开集成的分享面板

    }

    @OnClick(R.id.auth)
    fun onAuth() {
        umShareAPI!!.doOauthVerify(this@UmengActivity, SHARE_MEDIA.QQ, umAuthListener)

    }

    @OnClick(R.id.deauth)
    fun onDeAuth() {
        umShareAPI!!.deleteOauth(this@UmengActivity, SHARE_MEDIA.QQ, deListener)

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_umeng)
        ButterKnife.bind(this)
        umShareAPI = UMShareAPI.get(this)
        val sp = SPUtils.sp
        if (sp.getBoolean("status", false))
            show!!.setText(sp.getString("name", "null") + "\n" + sp.getString("avatar", "null"))

    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {
        super.onActivityResult(requestCode, resultCode, data)
        umShareAPI!!.onActivityResult(requestCode, resultCode, data)
    }

    override fun initView() {

    }

    override fun setListener() {

    }
}
