package com.toki.tokiapp.adslibrary.ads.model

import androidx.lifecycle.MutableLiveData
import com.google.android.gms.ads.nativead.NativeAd

class NativeHolder(var adId: String, var adId2: String){
    var nativeAd : NativeAd?= null
    var isLoaded = false
    var native_mutable: MutableLiveData<NativeAd> = MutableLiveData()
}