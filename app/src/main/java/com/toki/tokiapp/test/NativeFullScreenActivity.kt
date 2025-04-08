package com.toki.tokiapp.test

import android.os.Bundle
import android.widget.FrameLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.gms.ads.AdValue
import com.toki.tokiapp.adslibrary.ads.callback.NativeAdCallback
import com.toki.tokiapp.adslibrary.utils.AdmobUtil

class NativeFullScreenActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_native_full_screen)
        AdmobUtil.showNativeFullScreen(this,AdsManager.nativeHolder,findViewById<FrameLayout>(R.id.fl_native),R.layout.ad_unified,object : NativeAdCallback{
            override fun onNativeAdLoaded() {

            }

            override fun onAdFail() {

            }

            override fun onAdPaid(adValue: AdValue?) {

            }
        })
    }
}