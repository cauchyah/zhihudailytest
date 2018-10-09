package com.zhihudailytest.Fragment


import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.support.v4.content.LocalBroadcastManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.umeng.socialize.UMAuthListener
import com.umeng.socialize.UMShareAPI
import com.umeng.socialize.bean.SHARE_MEDIA
import com.zhihudailytest.R
import com.zhihudailytest.Utils.SPUtils

import org.json.JSONException
import org.json.JSONObject

import butterknife.ButterKnife
import butterknife.OnClick


class LoginFragment : BaseFragment() {

    private var umShareAPI: UMShareAPI? = null

    internal var bind: String?=null
    internal var platform: SHARE_MEDIA?=null
    private val umAuthListener = object : UMAuthListener {
        override fun onComplete(share_media: SHARE_MEDIA, i: Int, map: Map<String, String>) {

            umShareAPI!!.getPlatformInfo(getActivity(), platform, listener)
        }

        override fun onError(share_media: SHARE_MEDIA, i: Int, throwable: Throwable) {
            showToast("授权失败")
        }

        override fun onCancel(share_media: SHARE_MEDIA, i: Int) {
            showToast("cancel")
        }
    }

    private val listener = object : UMAuthListener {
        override fun onComplete(share_media: SHARE_MEDIA, i: Int, map: Map<String, String>) {
            val editor = SPUtils.editor
            editor.putString("bind", bind)
            // show.setText(map.get("result"));
            if (share_media == SHARE_MEDIA.SINA) {
                val result = map["result"]
                try {
                    val `object` = JSONObject(result)
                    editor.putString("name", `object`.getString("name"))
                    editor.putString("avatar", `object`.getString("profile_image_url"))
                    editor.putBoolean("status", true)
                    editor.putString("uid", `object`.getString("idstr"))
                } catch (e: JSONException) {
                    e.printStackTrace()
                }

            } else if (share_media == SHARE_MEDIA.QQ) {
                editor.putString("name", map["screen_name"])
                editor.putString("avatar", map["profile_image_url"])
                editor.putBoolean("status", true)
                editor.putString("uid", map["openid"])
            }
            editor.commit()
            //发送广播
            localBroadcastManager = LocalBroadcastManager.getInstance(context)
            val intent = Intent("login")
            localBroadcastManager!!.sendBroadcast(intent)
            getActivity().finish()


        }

        override fun onError(share_media: SHARE_MEDIA, i: Int, throwable: Throwable) {
            showToast("授权失败")
        }

        override fun onCancel(share_media: SHARE_MEDIA, i: Int) {

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

        return inflater!!.inflate(R.layout.fragment_login, container, false)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        ButterKnife.bind(this, view!!)
        umShareAPI = UMShareAPI.get(context)
    }

    @OnClick(R.id.sinaLogin)
    fun onSinaLogin() {
        platform = SHARE_MEDIA.SINA
        bind = "sina"
        umShareAPI!!.doOauthVerify(getActivity(), SHARE_MEDIA.SINA, umAuthListener)
    }

    @OnClick(R.id.qqLogin)
    fun onQQLogin() {
        platform = SHARE_MEDIA.QQ
        bind = "qq"
        umShareAPI!!.doOauthVerify(getActivity(), platform, umAuthListener)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        umShareAPI!!.onActivityResult(requestCode, resultCode, data)
    }
}// Required empty public constructor
