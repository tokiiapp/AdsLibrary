package com.toki.tokiapp.adslibrary.utils

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.ConnectivityManager
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.DisplayMetrics
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.LinearLayout
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ProcessLifecycleOwner
import com.facebook.shimmer.ShimmerFrameLayout
import com.google.ads.mediation.admob.AdMobAdapter
import com.google.android.gms.ads.*
import com.google.android.gms.ads.appopen.AppOpenAd
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.google.android.gms.ads.nativead.NativeAdOptions
import com.google.android.gms.ads.nativead.NativeAdView
import com.toki.tokiapp.adslibrary.R
import com.toki.tokiapp.adslibrary.ads.callback.AdsInterCallBack
import com.toki.tokiapp.adslibrary.ads.callback.AppOpenSplashCallback
import com.toki.tokiapp.adslibrary.ads.callback.BannerCallBack
import com.toki.tokiapp.adslibrary.ads.callback.LoadInterCallBack
import com.toki.tokiapp.adslibrary.ads.callback.NativeAdCallback
import com.toki.tokiapp.adslibrary.ads.enumads.GoogleENative
import com.toki.tokiapp.adslibrary.ads.model.InterHolderMulti
import com.toki.tokiapp.adslibrary.ads.model.InterHolderSimple
import com.toki.tokiapp.adslibrary.ads.enumads.CollapsibleBanner
import com.toki.tokiapp.adslibrary.ads.model.NativeHolder
import java.util.Date

object AdmobUtil {
    //Ẩn hiện quảng cáo
    var isShowAds = true

    //Dùng ID Test để hiển thị quảng cáo
    var isTesting = false

    // Timeout init admob
    var timeOut = 0

    // Biến check lần cuối hiển thị quảng cáo
    var lastTimeShowInterstitial: Long = 0
    var adRequest: AdRequest? = null

    //Check quảng cáo đang show hay không
    var isAdShowing = false
    var isClick = false
    var dialogFullScreen: Dialog? = null

    var shimmerFrameLayout: ShimmerFrameLayout? = null

    fun initAdmob(context: Context, timeout: Int, isDebug: Boolean, isEnableAds: Boolean) {
        timeOut = if (timeout > 0) {
            timeout
        } else {
            10000
        }


        isTesting = isDebug

        isShowAds = isEnableAds

        MobileAds.initialize(context) {

        }
        initAdRequest(timeOut)
    }

    // get AdRequest
    fun initAdRequest(timeOut: Int) {
        adRequest = AdRequest.Builder()
            .setHttpTimeoutMillis(timeOut)
            .build()
    }

    //check open network
    fun isNetworkConnected(context: Context): Boolean {
        val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        return cm.activeNetworkInfo != null && cm.activeNetworkInfo!!.isConnected
    }

    fun loadAndShowAdInterstitial(activity: Activity, admobId: String, adsInterCallBack: AdsInterCallBack, enableLoadingDialog: Boolean) {
        isAdShowing = false
        if (adRequest == null) {
            initAdRequest(timeOut)
        }
        if (!isShowAds || !isNetworkConnected(activity)) {
            adsInterCallBack.onAdFail("No internet")
            return
        }
        var id = admobId
        if (isTesting) {
            id = activity.getString(R.string.test_ads_admob_inter_id)
        }
        if (enableLoadingDialog) {
            dialogLoading(activity)
        }

        adRequest?.let {
            InterstitialAd.load(activity, id, it, object : InterstitialAdLoadCallback() {
                override fun onAdLoaded(interstitialAd: InterstitialAd) {
                    super.onAdLoaded(interstitialAd)
                    adsInterCallBack.onAdLoaded()
                    interstitialAd.onPaidEventListener = OnPaidEventListener { adValue: AdValue? -> adsInterCallBack.onPaid(adValue) }
                    interstitialAd.fullScreenContentCallback = object : FullScreenContentCallback() {
                        override fun onAdDismissedFullScreenContent() {
                            super.onAdDismissedFullScreenContent()
                            isAdShowing = false
                            lastTimeShowInterstitial = Date().time
                            adsInterCallBack.onEventClickAdClosed()
                            dismissAdDialog()
                        }

                        override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                            super.onAdFailedToShowFullScreenContent(adError)
                            isAdShowing = false

                            adsInterCallBack.onAdFail(adError.message)
                        }

                        override fun onAdShowedFullScreenContent() {
                            super.onAdShowedFullScreenContent()
                            isAdShowing = true
                            adsInterCallBack.onAdShowed()
                            try {
                                interstitialAd.setOnPaidEventListener(adsInterCallBack::onPaid)
                            } catch (e: Exception) {
                            }
                            dismissAdDialog()

                        }
                    }
                    showInterstitialAdNew(activity, interstitialAd, adsInterCallBack)

                }

                override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                    super.onAdFailedToLoad(loadAdError)
                    isAdShowing = false
                    adsInterCallBack.onAdFail(loadAdError.message)
                    dismissAdDialog()
                }
            })
        }
    }


    fun loadMultiInterstitial(context: Context, interHolder: InterHolderMulti, loadInterCallBack: LoadInterCallBack) {
        if (!isShowAds || !isNetworkConnected(context)) {
            loadInterCallBack.onAdFail("No internet")
            return
        }

        if (interHolder.inter != null || interHolder.inter2 != null) {
            return
        }
        interHolder.isLoaded = false
        if (adRequest == null) {
            initAdRequest(timeOut)
        }

        if (isTesting) {
            interHolder.adId = context.getString(R.string.test_ads_admob_inter_id)
        }

        if (adRequest != null) {
            InterstitialAd.load(context, interHolder.adId, adRequest!!, object : InterstitialAdLoadCallback() {
                override fun onAdLoaded(interstitialAd: InterstitialAd) {
                    super.onAdLoaded(interstitialAd)
                    loadInterCallBack.onAdLoaded()
                    interHolder.mutableLiveData1.value = interstitialAd
                    interHolder.inter = interstitialAd
                    interHolder.isLoaded = true
                }

                override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                    super.onAdFailedToLoad(loadAdError)
                    isAdShowing = false
                    interHolder.mutableLiveData1.value = null
                    interHolder.isLoaded = true
                }
            })

            InterstitialAd.load(context, interHolder.adId, adRequest!!, object : InterstitialAdLoadCallback() {
                override fun onAdLoaded(interstitialAd: InterstitialAd) {
                    super.onAdLoaded(interstitialAd)
                    loadInterCallBack.onAdLoaded()
                    interHolder.mutableLiveData2.value = interstitialAd
                    interHolder.inter2 = interstitialAd
                    interHolder.isLoaded = true
                }

                override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                    super.onAdFailedToLoad(loadAdError)
                    isAdShowing = false
                    interHolder.mutableLiveData2.value = null
                    interHolder.isLoaded = true
                }
            })
        }
    }

    fun loadInterstitial(context: Context, interHolder: InterHolderSimple, loadInterCallBack: LoadInterCallBack) {
        if (!isShowAds || !isNetworkConnected(context)) {
            loadInterCallBack.onAdFail("No internet")
            return
        }

        if (interHolder.inter != null) {
            return
        }
        interHolder.isLoaded = false
        if (adRequest == null) {
            initAdRequest(timeOut)
        }

        if (isTesting) {
            interHolder.adId = context.getString(R.string.test_ads_admob_inter_id)
        }

        if (adRequest != null) {
            InterstitialAd.load(context, interHolder.adId, adRequest!!, object : InterstitialAdLoadCallback() {
                override fun onAdLoaded(interstitialAd: InterstitialAd) {
                    super.onAdLoaded(interstitialAd)
                    loadInterCallBack.onAdLoaded()
                    interHolder.mutableLiveData.value = interstitialAd
                    interHolder.inter = interstitialAd
                    interHolder.isLoaded = true
                }

                override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                    super.onAdFailedToLoad(loadAdError)
                    isAdShowing = false
                    interHolder.mutableLiveData.value = null
                    interHolder.isLoaded = true
                }
            })

        }
    }

    fun showInterstitial(activity: Activity, interHolder: InterHolderSimple, timeout: Int, adsInterCallBack: AdsInterCallBack, enableLoadingDialog: Boolean) {
        if (!isShowAds || !isNetworkConnected(activity)) {
            isAdShowing = false

            adsInterCallBack.onAdFail("No internet")
            return
        }
        adsInterCallBack.onAdLoaded()
        val handler = Handler(Looper.getMainLooper())
        val runnable = Runnable {
            if (!interHolder.isLoaded) {

                interHolder.mutableLiveData.removeObservers(activity as LifecycleOwner)
                isAdShowing = false
                dismissAdDialog()
                adsInterCallBack.onAdFail("timeout")
            }
        }
        handler.postDelayed(runnable, timeout.toLong())

        if (!interHolder.isLoaded) {
            interHolder.mutableLiveData.observe(activity as LifecycleOwner) {
                if (it != null) {
                    interHolder.mutableLiveData.removeObservers(activity as LifecycleOwner)
                    it.fullScreenContentCallback = object : FullScreenContentCallback() {
                        override fun onAdDismissedFullScreenContent() {
                            super.onAdDismissedFullScreenContent()
                            isAdShowing = false
                            lastTimeShowInterstitial = Date().time
                            interHolder.mutableLiveData.removeObservers(activity as LifecycleOwner)
                            interHolder.inter = null
                            adsInterCallBack.onEventClickAdClosed()
                            dismissAdDialog()
                        }

                        override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                            super.onAdFailedToShowFullScreenContent(adError)
                            isAdShowing = false
                            interHolder.inter = null
                            interHolder.mutableLiveData.removeObservers(activity as LifecycleOwner)
                            adsInterCallBack.onAdFail(adError.message)
                        }

                        override fun onAdShowedFullScreenContent() {
                            super.onAdShowedFullScreenContent()
                            handler.removeCallbacksAndMessages(null)
                            isAdShowing = true
                            adsInterCallBack.onAdShowed()
                            try {
                                interHolder.inter?.setOnPaidEventListener(adsInterCallBack::onPaid)
                            } catch (e: Exception) {
                            }
                            dismissAdDialog()

                        }
                    }

                    showInterstitialAdNew(activity, it, adsInterCallBack)

                }else{
                    adsInterCallBack.onAdFail("fail")
                }
            }
            return
        }

        if (interHolder.inter != null) {
            if (enableLoadingDialog) {
                dialogLoading(activity)
            }
            interHolder.inter!!.fullScreenContentCallback = object : FullScreenContentCallback() {
                override fun onAdDismissedFullScreenContent() {
                    super.onAdDismissedFullScreenContent()
                    isAdShowing = false
                    lastTimeShowInterstitial = Date().time
                    interHolder.mutableLiveData.removeObservers(activity as LifecycleOwner)
                    interHolder.inter = null
                    adsInterCallBack.onEventClickAdClosed()
                    dismissAdDialog()
                }

                override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                    super.onAdFailedToShowFullScreenContent(adError)
                    isAdShowing = false
                    interHolder.inter = null
                    interHolder.mutableLiveData.removeObservers(activity as LifecycleOwner)
                    adsInterCallBack.onAdFail(adError.message)
                }

                override fun onAdShowedFullScreenContent() {
                    super.onAdShowedFullScreenContent()
                    handler.removeCallbacksAndMessages(null)
                    isAdShowing = true
                    adsInterCallBack.onAdShowed()
                    try {
                        interHolder.inter?.setOnPaidEventListener(adsInterCallBack::onPaid)
                    } catch (e: Exception) {
                    }
                    dismissAdDialog()

                }

            }
            showInterstitialAdNew(activity, interHolder.inter, adsInterCallBack)

        } else {
            isAdShowing = false
            adsInterCallBack.onAdFail("inter null")

        }


    }


    private fun showInterstitialAdNew(activity: Activity, mInterstitialAd: InterstitialAd?, callback: AdsInterCallBack) {
        if (ProcessLifecycleOwner.get().lifecycle.currentState.isAtLeast(Lifecycle.State.RESUMED) && mInterstitialAd != null) {
            Handler(Looper.getMainLooper()).postDelayed({
                isAdShowing = true
                callback.onStartAction()
                mInterstitialAd.setOnPaidEventListener(callback::onPaid)
                mInterstitialAd.show(activity)
            }, 400)

        } else {
            isAdShowing = false
            dismissAdDialog()
            callback.onAdFail("onResume")
        }
    }

    fun loadNativeAd(context: Context, nativeHolder: NativeHolder) {
        if (!isShowAds || !isNetworkConnected(context)) {
            return
        }

        if (isTesting) {
            nativeHolder.adId = context.getString(R.string.test_ads_admob_native_id)
        }
        nativeHolder.isLoaded = false

        val adLoader: AdLoader = AdLoader.Builder(context, nativeHolder.adId).forNativeAd {
            nativeHolder.nativeAd = it
            nativeHolder.isLoaded = true
            nativeHolder.native_mutable.setValue(it)

        }.withAdListener(object : AdListener() {
            override fun onAdFailedToLoad(adError: LoadAdError) {
                super.onAdFailedToLoad(adError)
                loadNativeAd2(context, nativeHolder)
            }
        }).withNativeAdOptions(NativeAdOptions.Builder().build()).build()
        if (adRequest != null) {
            adLoader.loadAd(adRequest!!)
        }
    }

    private fun loadNativeAd2(context: Context, nativeHolder: NativeHolder) {
        if (!isShowAds || !isNetworkConnected(context)) {
            return
        }

        if (isTesting) {
            nativeHolder.adId2 = context.getString(R.string.test_ads_admob_native_id)
        }
        nativeHolder.isLoaded = false

        val adLoader: AdLoader = AdLoader.Builder(context, nativeHolder.adId2).forNativeAd {
            nativeHolder.nativeAd = it
            nativeHolder.isLoaded = true
            nativeHolder.native_mutable.setValue(it)
        }.withAdListener(object : AdListener() {
            override fun onAdFailedToLoad(adError: LoadAdError) {
                super.onAdFailedToLoad(adError)
                nativeHolder.nativeAd = null
                nativeHolder.isLoaded = false
                nativeHolder.native_mutable.setValue(null)
            }
        }).withNativeAdOptions(NativeAdOptions.Builder().build()).build()
        if (adRequest != null) {
            adLoader.loadAd(adRequest!!)
        }
    }

    fun showNativeAd(activity: Activity, nativeHolder: NativeHolder, viewGroup: ViewGroup, layout: Int, size: GoogleENative, nativeAdCallback: NativeAdCallback) {
        if (!isShowAds || !isNetworkConnected(activity)) {
            viewGroup.visibility = ViewGroup.GONE
            return
        }
        viewGroup.removeAllViews()
        if (nativeHolder.isLoaded) {
            if (nativeHolder.nativeAd != null) {
                val adView = activity.layoutInflater
                    .inflate(layout, null) as NativeAdView

                NativeAdPopulate.populateNativeAdView(nativeHolder.nativeAd!!, adView, GoogleENative.UNIFIED_MEDIUM)

                nativeHolder.native_mutable.removeObservers((activity as LifecycleOwner))
                viewGroup.addView(adView)
                nativeAdCallback.onNativeAdLoaded()
                nativeHolder.nativeAd!!.setOnPaidEventListener(nativeAdCallback::onAdPaid)

            } else {
                nativeHolder.native_mutable.removeObservers((activity as LifecycleOwner))
                nativeAdCallback.onAdFail()
            }
        } else {
            val tagView: View = if (size === GoogleENative.UNIFIED_MEDIUM) {
                activity.layoutInflater.inflate(R.layout.layoutnative_loading_medium, null, false)
            } else {
                activity.layoutInflater.inflate(R.layout.layoutnative_loading_small, null, false)
            }
            viewGroup.addView(tagView, 0)
            if (shimmerFrameLayout == null) shimmerFrameLayout = tagView.findViewById<ShimmerFrameLayout>(R.id.shimmer_view_container)
            shimmerFrameLayout?.startShimmer()
            nativeHolder.native_mutable.observe(activity as LifecycleOwner) {
                if (it != null) {
                    val adView = activity.layoutInflater
                        .inflate(layout, null) as NativeAdView
                    NativeAdPopulate.populateNativeAdView(it, adView, GoogleENative.UNIFIED_MEDIUM)
                    if (shimmerFrameLayout != null) {
                        shimmerFrameLayout!!.stopShimmer()
                    }
                    viewGroup.addView(adView)
                    nativeAdCallback.onNativeAdLoaded()
                    nativeHolder.native_mutable.removeObservers((activity as LifecycleOwner))
                    it.setOnPaidEventListener(nativeAdCallback::onAdPaid)
                } else {
                    if (shimmerFrameLayout != null) {
                        shimmerFrameLayout!!.stopShimmer()
                    }
                    nativeAdCallback.onAdFail()
                    nativeHolder.native_mutable.removeObservers((activity as LifecycleOwner))
                }
            }
        }
    }

    fun loadAndShowNative(activity: Activity, nativeId: String, viewGroup: ViewGroup, layout: Int, size: GoogleENative, nativeAdCallback: NativeAdCallback){
        if (!isShowAds || !isNetworkConnected(activity)) {
            nativeAdCallback.onAdFail()
            return
        }
        var idNative = nativeId
        if (isTesting) {
            idNative = activity.getString(R.string.test_ads_admob_native_id)
        }
        val tagView: View = if (size === GoogleENative.UNIFIED_MEDIUM) {
            activity.layoutInflater.inflate(R.layout.layoutnative_loading_medium, null, false)
        } else {
            activity.layoutInflater.inflate(R.layout.layoutnative_loading_small, null, false)
        }
        viewGroup.addView(tagView, 0)
        if (shimmerFrameLayout == null) shimmerFrameLayout = tagView.findViewById<ShimmerFrameLayout>(R.id.shimmer_view_container)
        shimmerFrameLayout?.startShimmer()

        val adLoader: AdLoader = AdLoader.Builder(activity, idNative).forNativeAd {
            val adView = activity.layoutInflater
                .inflate(layout, null) as NativeAdView

            NativeAdPopulate.populateNativeAdView(it, adView, size)
            if (shimmerFrameLayout != null) {
                shimmerFrameLayout!!.stopShimmer()
            }
            viewGroup.addView(adView)
            nativeAdCallback.onNativeAdLoaded()
            it.setOnPaidEventListener(nativeAdCallback::onAdPaid)
        }.withAdListener(object : AdListener() {
            override fun onAdFailedToLoad(adError: LoadAdError) {
                super.onAdFailedToLoad(adError)
                nativeAdCallback.onAdFail()
            }
        }).withNativeAdOptions(NativeAdOptions.Builder().build()).build()
        if (adRequest != null) {
            adLoader.loadAd(adRequest!!)
        }
    }

    fun loadBannerAd(activity: Activity, bannerId: String, viewGroup: ViewGroup, bannerCallBack: BannerCallBack) {
        if (!isShowAds || !isNetworkConnected(activity)) {
            viewGroup.visibility = View.GONE
            bannerCallBack.onFailed()
            return
        }

        val mAdView = AdView(activity)
        var id = bannerId
        if (isTesting) {
            id = activity.getString(R.string.test_ads_admob_banner_id)
        }
        mAdView.adUnitId = id
        val adSize: AdSize = getAdSize(activity)

        mAdView.setAdSize(adSize)
        viewGroup.removeAllViews()
        val tagView = activity.layoutInflater.inflate(R.layout.layoutbanner_loading, null, false)
        viewGroup.addView(tagView, 0)
        viewGroup.addView(mAdView, 1)
        shimmerFrameLayout = tagView.findViewById(R.id.shimmer_view_container)
        shimmerFrameLayout?.startShimmer()

        mAdView.onPaidEventListener = OnPaidEventListener { adValue -> bannerCallBack.onPaid(adValue, mAdView) }
        mAdView.adListener = object : AdListener() {
            override fun onAdLoaded() {
                shimmerFrameLayout?.stopShimmer()
                viewGroup.removeView(tagView)
                bannerCallBack.onLoad()
            }

            override fun onAdFailedToLoad(adError: LoadAdError) {
                Log.e(" Admod", "failloadbanner" + adError.message)
                shimmerFrameLayout?.stopShimmer()
                viewGroup.removeView(tagView)
                bannerCallBack.onFailed()
            }

            override fun onAdOpened() {}
            override fun onAdClicked() {
                // Code to be executed when the user clicks on an ad.
            }

            override fun onAdClosed() {
                // Code to be executed when the user is about to return
                // to the app after tapping on an ad.
            }
        }

        if (adRequest != null) {
            mAdView.loadAd(adRequest!!)
        }
    }


    fun loadAdBannerCollapsible(activity: Activity, bannerId: String, collapsibleBannersize: CollapsibleBanner, viewGroup: ViewGroup, callback: BannerCallBack) {
        var bannerId1 = bannerId
        if (!isShowAds || !isNetworkConnected(activity)) {
            viewGroup.visibility = View.GONE
            return
        }
        val mAdView = AdView(activity)
        if (isTesting) {
            bannerId1 = activity.getString(R.string.test_ads_admob_banner_id)
        }
        mAdView.adUnitId = bannerId1
        val adSize = getAdSize(activity)
        mAdView.setAdSize(adSize)
        viewGroup.removeAllViews()
        val tagView = activity.layoutInflater.inflate(R.layout.layoutbanner_loading, null, false)
        viewGroup.addView(tagView, 0)
        viewGroup.addView(mAdView, 1)
        shimmerFrameLayout = tagView.findViewById(R.id.shimmer_view_container)
        shimmerFrameLayout?.startShimmer()
        mAdView.onPaidEventListener = OnPaidEventListener { adValue -> callback.onPaid(adValue, mAdView) }
        mAdView.adListener = object : AdListener() {
            override fun onAdLoaded() {
                shimmerFrameLayout?.stopShimmer()
                viewGroup.removeView(tagView)
                callback.onLoad()
            }

            override fun onAdFailedToLoad(adError: LoadAdError) {
                Log.e(" Admod", "failloadbanner" + adError.message)
                shimmerFrameLayout?.stopShimmer()
                viewGroup.removeView(tagView)
                callback.onFailed()
            }

            override fun onAdOpened() {}
            override fun onAdClicked() {
                // Code to be executed when the user clicks on an ad.
            }

            override fun onAdClosed() {
                // Code to be executed when the user is about to return
                // to the app after tapping on an ad.
                callback.onClosed(adSize)
            }
        }
        val extras = Bundle()
        var anchored = "top"
        anchored = if (collapsibleBannersize === CollapsibleBanner.TOP) {
            "top"
        } else {
            "bottom"
        }
        extras.putString("collapsible", anchored)
        val adRequest2 = AdRequest.Builder().addNetworkExtrasBundle(AdMobAdapter::class.java, extras)
            .build()
        if (adRequest2 != null) {
            mAdView.loadAd(adRequest2)
        }
        Log.e(" Admod", "loadAdBanner")
    }


    fun loadAndShowAppOpenSplash(activity: Activity, appOpenId: String, appOpenSplashCallback: AppOpenSplashCallback) {
        var appResumeAdId = ""
        if (adRequest == null) {
            initAdRequest(timeOut)
        }
        appResumeAdId = if (isTesting) {
            activity.getString(R.string.test_ads_admob_app_open)
        } else {
            appOpenId
        }
        if (!isShowAds || !isNetworkConnected(activity)) {
            appOpenSplashCallback.onAdFail("No internet")
            return
        }

        adRequest?.let {
            AppOpenAd.load(activity, appResumeAdId, it, object : AppOpenAd.AppOpenAdLoadCallback() {
                override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                    super.onAdFailedToLoad(loadAdError)
                    appOpenSplashCallback.onAdFail(loadAdError.message)
                }

                override fun onAdLoaded(appOpenAd: AppOpenAd) {
                    super.onAdLoaded(appOpenAd)
                    appOpenAd.show(activity)
                    appOpenAd.fullScreenContentCallback = object : FullScreenContentCallback() {
                        override fun onAdClicked() {
                            super.onAdClicked()
                        }

                        override fun onAdDismissedFullScreenContent() {
                            super.onAdDismissedFullScreenContent()
                            appOpenSplashCallback.onAdClosed()
                        }

                        override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                            super.onAdFailedToShowFullScreenContent(adError)
                            appOpenSplashCallback.onAdFail(adError.message)
                        }

                        override fun onAdImpression() {
                            super.onAdImpression()
                        }

                        override fun onAdShowedFullScreenContent() {
                            super.onAdShowedFullScreenContent()
                        }
                    }
                }
            })
        }
    }

    private fun getAdSize(context: Activity): AdSize {
        // Step 2 - Determine the screen width (less decorations) to use for the ad width.
        val display = context.windowManager.defaultDisplay
        val outMetrics = DisplayMetrics()
        display.getMetrics(outMetrics)
        val widthPixels = outMetrics.widthPixels.toFloat()
        val density = outMetrics.density
        val adWidth = (widthPixels / density).toInt()
        // Step 3 - Get adaptive ad size and return for setting on the ad view.
        return AdSize.getCurrentOrientationAnchoredAdaptiveBannerAdSize(context, adWidth)
    }

    fun dismissAdDialog() {
        dialogFullScreen?.let {
            if (it.isShowing) {
                it.dismiss()
            }
        }
    }

    fun dialogLoading(context: Context) {
        dialogFullScreen = Dialog(context)
        dialogFullScreen?.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialogFullScreen?.setContentView(R.layout.dialog_full_screen)
        dialogFullScreen?.setCancelable(false)
        dialogFullScreen?.window?.setBackgroundDrawable(ColorDrawable(Color.WHITE))
        dialogFullScreen?.window?.setLayout(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.MATCH_PARENT
        )
        dialogFullScreen?.show()
    }
}