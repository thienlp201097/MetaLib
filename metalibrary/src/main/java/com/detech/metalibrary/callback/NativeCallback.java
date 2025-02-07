package com.detech.metalibrary.callback;


import com.facebook.ads.NativeAd;

public interface NativeCallback {
    void onLoadedAndGetNativeAd(NativeAd ad );
    void onNativeAdLoaded();
    void onAdFail(String error);
}
