package com.example.examplemetaads

import android.app.Activity
import android.view.ViewGroup
import com.admob.max.dktlibrary.MetaENative
import com.detech.metalibrary.MetaUtils
import com.detech.metalibrary.callback.AdsInterCallBack
import com.detech.metalibrary.callback.AdsNativeCallBackAdmod
import com.detech.metalibrary.callback.BannerCallBack
import com.detech.metalibrary.callback.NativeCallback
import com.detech.metalibrary.model.InterHolder
import com.detech.metalibrary.model.NativeHolder
import com.facebook.ads.NativeAd
import com.facebook.ads.NativeAdLayout

object AdsManager {
    val interHolder = InterHolder("1354114092262306_1354193655587683")
    val nativeHolder = NativeHolder("1354114092262306_1354193802254335")
    val banner = "1354114092262306_1354114885595560"
    
    fun loadBanner(activity: Activity,id : String,viewGroup: ViewGroup){
        MetaUtils.loadAdBanner(activity,id,viewGroup,object : BannerCallBack {
            override fun onClickAds() {
                
            }

            override fun onLoad() {
                
            }

            override fun onFailed(message: String) {
                
            }
        })
    }
    
    fun loadNative(activity: Activity,nativeHolder: NativeHolder){
        MetaUtils.loadAndGetNativeAds(activity,nativeHolder,object : NativeCallback {
            override fun onLoadedAndGetNativeAd(ad: NativeAd?) {
                
            }

            override fun onNativeAdLoaded() {
                
            }

            override fun onAdFail(error: String?) {
                
            }

        })
    }

    fun showNative(activity: Activity,nativeHolder: NativeHolder,viewGroup: NativeAdLayout){
        MetaUtils.showNativeAdsWithLayout(activity,nativeHolder,viewGroup, com.detech.metalibrary.R.layout.layout_native_meta,MetaENative.UNIFIED_MEDIUM,object :
            AdsNativeCallBackAdmod {
            override fun NativeLoaded() {
                
            }

            override fun NativeFailed(massage: String) {
                
            }

        })
    }
    
    fun loadAndShowInter(activity: Activity,interHolder: InterHolder){
        MetaUtils.loadAndShowInterstitialAd(activity,interHolder,true,object : AdsInterCallBack {
            override fun onStartAction() {
                
            }

            override fun onEventClickAdClosed() {
                
            }

            override fun onAdShowed() {
                
            }

            override fun onAdLoaded() {
                
            }

            override fun onAdFail(error: String?) {
                
            }
        })
    }
}