package com.detech.metalibrary.model

import androidx.lifecycle.MutableLiveData
import com.facebook.ads.InterstitialAd

class InterHolder(var ads: String) {
    var inter: InterstitialAd? = null
    val mutable: MutableLiveData<InterstitialAd> = MutableLiveData()
    var check = false
}