package com.detech.metalibrary.callback

interface BannerCallBack {
    fun onClickAds()
    fun onLoad()
    fun onFailed(message : String)
}