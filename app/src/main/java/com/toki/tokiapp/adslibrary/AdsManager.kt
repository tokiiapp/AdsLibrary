package com.toki.tokiapp.adslibrary

import android.app.Activity
import android.content.Context
import com.google.android.gms.ads.AdValue
import com.toki.tokiapp.adslibrary.ads.callback.AdsInterCallBack
import com.toki.tokiapp.adslibrary.ads.callback.LoadInterCallBack
import com.toki.tokiapp.adslibrary.ads.model.InterHolderSimple
import com.toki.tokiapp.adslibrary.utils.AdmobUtil
import com.toki.tokiapp.adslibrary.ads.model.NativeHolder


object AdsManager {
    var nativeHolder = NativeHolder("ca-app-pub-3940256099942544/2247696110\n","ca-app-pub-3940256099942544/2247696110\n")
    var interholder = InterHolderSimple("ca-app-pub-3940256099942544/1033173712")
    fun loadInter(context: Context, interHolder: InterHolderSimple) {
       AdmobUtil.loadInterstitial(context,interHolder,object : LoadInterCallBack{
           override fun onAdClosed() {
               
           }

           override fun onAdLoaded() {
            
           }

           override fun onAdFail(error: String) {
               
           }
       })
    }

    fun showAdInter(
        activity: Activity,
        interHolder: InterHolderSimple,
        callback: AdListener,
    ) {
        AdmobUtil.showInterstitial(activity,interHolder,10000,object : AdsInterCallBack{
            override fun onStartAction() {
                callback.onAdClosed()
            }

            override fun onEventClickAdClosed() {
                
            }

            override fun onAdShowed() {
              
            }

            override fun onAdLoaded() {
              
            }

            override fun onAdFail(error: String?) {
              callback.onAdClosed()
            }

            override fun onPaid(adValue: AdValue?) {
              
            }
        },true)
    }

    interface AdListener {
        fun onAdClosed()
        fun onFailed()
    }
}
