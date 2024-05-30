package com.toki.tokiapp.test

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.android.gms.ads.AdValue
import com.toki.tokiapp.adslibrary.ads.callback.AdsInterCallBack
import com.toki.tokiapp.adslibrary.ads.callback.AppOpenSplashCallback
import com.toki.tokiapp.adslibrary.ads.callback.NativeAdCallback
import com.toki.tokiapp.adslibrary.utils.AdmobUtil
import com.toki.tokiapp.adslibrary.utils.AppOpenManager

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        AdmobUtil.initAdmob(this, 10000, isDebug = true, isEnableAds = true)
        AppOpenManager.instance?.init(application,"ca-app-pub-3940256099942544/3419835294")
        AppOpenManager.instance?.disableAppResumeWithActivity(MainActivity::class.java)
        AdsManager.loadInter(this, AdsManager.interholder)
        AdmobUtil.loadNativeAd(this, AdsManager.nativeHolder)
        AdmobUtil.loadAndShowAppOpenSplash(this,"",object : AppOpenSplashCallback{
            override fun onAdFail(error: String) {
                onAdClosed()
            }

            override fun onAdClosed() {
               startActivity(Intent(this@MainActivity, SecondActivity::class.java))
            }
        })
    }
}