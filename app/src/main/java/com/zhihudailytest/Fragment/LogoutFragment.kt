package com.zhihudailytest.Fragment


import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v4.content.LocalBroadcastManager
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView

import com.bumptech.glide.Glide
import com.umeng.socialize.UMAuthListener
import com.umeng.socialize.UMShareAPI
import com.umeng.socialize.bean.SHARE_MEDIA
import com.zhihudailytest.Activity.BaseActivity
import com.zhihudailytest.R
import com.zhihudailytest.Utils.SPUtils

import butterknife.BindView
import butterknife.ButterKnife
import butterknife.OnClick
import jp.wasabeef.glide.transformations.CropCircleTransformation
import kotlinx.android.synthetic.main.fragment_logout.*


class LogoutFragment : BaseFragment() {
    private var umShareAPI: UMShareAPI? = null



    private var platform: String? = null
    private val deListener = object : UMAuthListener {
        override fun onComplete(share_media: SHARE_MEDIA, i: Int, map: Map<String, String>) {
            showToast("注销成功")
            val editor = SPUtils.editor
            editor.remove("name")

            editor.remove("avatar")
            editor.putBoolean("status", false)
            editor.remove("uid")
            editor.remove("bind")
            editor.commit()
            //发送广播
            localBroadcastManager = LocalBroadcastManager.getInstance(context)
            val intent = Intent("logout")
            localBroadcastManager!!.sendBroadcast(intent)
            getActivity().finish()
        }

        override fun onError(share_media: SHARE_MEDIA, i: Int, throwable: Throwable) {
            showToast("注销失败")
        }

        override fun onCancel(share_media: SHARE_MEDIA, i: Int) {
            showToast("delete Oauth onCancel")
        }
    }


    override fun onDoubleClick() {

    }

    internal override fun loadData() {

    }

    internal override fun onBroadcastReceive(context: Context, intent: Intent) {

    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater!!.inflate(R.layout.fragment_logout, container, false)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        ButterKnife.bind(this, view!!)
        init()
    }

    private fun init() {
        toolbar = (getActivity() as BaseActivity).toolbar
        toolbar!!.title = "账号"
        umShareAPI = UMShareAPI.get(context)
        val sp = SPUtils.sp
        platform = sp.getString("bind", "")
        name!!.text = sp.getString("name", "null")
        qq!!.setTextColor(ContextCompat.getColor(context, R.color.light_blue))
        Glide.with(context)
                .load(sp.getString("avatar", ""))
                .bitmapTransform(CropCircleTransformation(context))
                .into(avatar!!)
        if (TextUtils.equals(platform, "sina")) {
            //绑定新浪
            sina!!.text = "解绑新浪微博"
            sina!!.setTextColor(ContextCompat.getColor(context, R.color.gray))
            qq!!.text = "绑定QQ登录"
            qq!!.setTextColor(ContextCompat.getColor(context, R.color.light_blue))
        } else if (TextUtils.equals(platform, "qq")) {
            //绑定QQ
            sina!!.text = "绑定新浪微博"
            sina!!.setTextColor(ContextCompat.getColor(context, R.color.light_blue))
            qq!!.text = "解绑QQ登录"
            qq!!.setTextColor(ContextCompat.getColor(context, R.color.gray))
        }
    }

    @OnClick(R.id.logout)
    fun onLogout() {
        val media: SHARE_MEDIA
        if (TextUtils.equals(platform, "qq"))
            media = SHARE_MEDIA.QQ
        else
            media = SHARE_MEDIA.SINA
        umShareAPI!!.deleteOauth(getActivity(), media, deListener)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        umShareAPI!!.onActivityResult(requestCode, resultCode, data)
    }
}// Required empty public constructor
