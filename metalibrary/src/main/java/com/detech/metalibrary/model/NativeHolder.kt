package com.detech.metalibrary.model

import androidx.lifecycle.MutableLiveData
import com.facebook.ads.NativeAd

class NativeHolder(var ads: String){
    var nativeAd : NativeAd?= null
    var isLoad = false
    var native_mutable: MutableLiveData<NativeAd> = MutableLiveData()
}