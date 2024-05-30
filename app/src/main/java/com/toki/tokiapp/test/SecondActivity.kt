package com.toki.tokiapp.test

import android.os.Bundle
import android.widget.FrameLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.core.content.ContextCompat
import com.codemybrainsout.ratingdialog.RatingDialog
import com.google.android.gms.ads.AdValue
import com.google.android.gms.ads.AdView
import com.toki.tokiapp.adslibrary.ads.callback.BannerCallBack
import com.toki.tokiapp.adslibrary.ads.callback.NativeAdCallback
import com.toki.tokiapp.adslibrary.ads.enumads.GoogleENative
import com.toki.tokiapp.adslibrary.utils.AdmobUtil

class SecondActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_second)
        AdmobUtil.loadBannerAd(this,"",findViewById<FrameLayout>(R.id.fl_banner),object : BannerCallBack{
            override fun onLoad() {

            }

            override fun onFailed() {

            }

            override fun onPaid(adValue: AdValue?, mAdView: AdView?) {
               
            }
        })
        AdmobUtil.loadAndShowNative(this, "ca-app-pub-3940256099942544/2247696110",findViewById<FrameLayout>(R.id.fl_native),R.layout.ad_template_medium,GoogleENative.UNIFIED_MEDIUM,object : NativeAdCallback{
            override fun onNativeAdLoaded() {

            }

            override fun onAdFail() {

            }

            override fun onAdPaid(adValue: AdValue?) {

            }
        })

//        AdmobUtil.showNativeAd(this,AdsManager.nativeHolder,findViewById<FrameLayout>(R.id.fl_native),R.layout.ad_template_medium,GoogleENative.UNIFIED_MEDIUM,object : NativeAdCallback{
//            override fun onNativeAdLoaded() {
//
//            }
//
//            override fun onAdFail() {
//
//            }
//
//            override fun onAdPaid(adValue: AdValue?) {
//
//            }
//        })
        findViewById<AppCompatButton>(R.id.tv_show_inter).setOnClickListener {
            AdsManager.showAdInter(this, AdsManager.interholder, object : AdsManager.AdListener {
                override fun onAdClosed() {

                }

                override fun onFailed() {

                }
            })
        }

        findViewById<AppCompatButton>(R.id.tv_show_rate).setOnClickListener {
            val ratingDialog = RatingDialog.Builder(this)
                .session(1)
                .date(1)
                .setNameApp(getString(R.string.app_name))
                .setIcon(R.mipmap.ic_launcher)
                .setEmail("momcenter.ads@gmail.com")
                .isShowButtonLater(true)
                .isClickLaterDismiss(true)
                .setOnlickRate { rate -> Toast.makeText(this@SecondActivity, "Rate$rate", Toast.LENGTH_SHORT).show() }
                .setTextButtonLater("Maybe Later")
                .setOnlickMaybeLate { Toast.makeText(this@SecondActivity, "Feedback cannot be left blank", Toast.LENGTH_SHORT).show() }
                .ratingButtonColor(ContextCompat.getColor(this, R.color.purple_200))
                .build()


            //Cancel On Touch Outside
            ratingDialog.setCanceledOnTouchOutside(false)
            //show
            ratingDialog.show()
        }
    }
}