package com.toki.tokiapp.test

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.ads.AdValue
import com.google.android.gms.ads.MediaAspectRatio
import com.toki.tokiapp.adslibrary.ads.callback.NativeAdCallback
import com.toki.tokiapp.adslibrary.ads.callback.RewardAdCallback
import com.toki.tokiapp.adslibrary.utils.AdmobUtil
import com.toki.tokiapp.adslibrary.utils.AppOpenManager

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        AdmobUtil.initAdmob(this, 10000, isDebug = true, isEnableAds = true)
        AppOpenManager.instance?.init(application,"ca-app-pub-3940256099942544/3419835294")
//        AppOpenManager.instance?.disableAppResumeWithActivity(MainActivity::class.java)
        AdsManager.loadInter(this, AdsManager.interholder)
        AdmobUtil.loadNativeFullScreen(this, AdsManager.nativeHolder,MediaAspectRatio.SQUARE,object : NativeAdCallback{
            override fun onNativeAdLoaded() {

            }

            override fun onAdFail() {

            }

            override fun onAdPaid(adValue: AdValue?) {

            }

        })
//        AdmobUtil.loadAndShowAppOpenSplash(this,"",object : AppOpenSplashCallback{
//            override fun onAdFail(error: String) {
//                onAdClosed()
//            }
//
//            override fun onAdClosed() {
//               startActivity(Intent(this@MainActivity, SecondActivity::class.java))
//            }
//        })

        findViewById<TextView>(R.id.tv_main).setOnClickListener {
            AdmobUtil.loadAndShowAdRewardWithCallback(this,"",object : RewardAdCallback{
                override fun onAdClosed() {
                    if (AdmobUtil.mRewardedAd != null) {
                        AdmobUtil.mRewardedAd = null
                    }
                    AdmobUtil.dismissAdDialog()
                    startActivity(Intent(this@MainActivity,SecondActivity::class.java))

                }

                override fun onAdShowed() {
                    Handler().postDelayed(AdmobUtil::dismissAdDialog, 800)
                }

                override fun onAdFail(message: String?) {
                    AdmobUtil.dismissAdDialog()
                }

                override fun onEarned() {
                    if (AdmobUtil.mRewardedAd != null) {
                        AdmobUtil.mRewardedAd = null
                    }
                    AdmobUtil.dismissAdDialog()

                }

                override fun onPaid(adValue: AdValue?, adUnitAds: String?) {

                }
            },true)
        }
    }
}