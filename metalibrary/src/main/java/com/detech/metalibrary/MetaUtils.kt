package com.detech.metalibrary

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.ConnectivityManager
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.lifecycle.LifecycleOwner
import com.admob.max.dktlibrary.GoogleENative
import com.airbnb.lottie.LottieAnimationView
import com.detech.metalibrary.callback.AdsInterCallBack
import com.detech.metalibrary.callback.AdsNativeCallBackAdmod
import com.detech.metalibrary.callback.BannerCallBack
import com.detech.metalibrary.callback.NativeCallback
import com.detech.metalibrary.model.InterHolder
import com.detech.metalibrary.model.NativeHolder
import com.facebook.ads.Ad
import com.facebook.ads.AdError
import com.facebook.ads.AdListener
import com.facebook.ads.AdOptionsView
import com.facebook.ads.AdSettings
import com.facebook.ads.AdSize
import com.facebook.ads.AdView
import com.facebook.ads.AudienceNetworkAds
import com.facebook.ads.InterstitialAd
import com.facebook.ads.InterstitialAdListener
import com.facebook.ads.MediaView
import com.facebook.ads.NativeAd
import com.facebook.ads.NativeAdLayout
import com.facebook.ads.NativeAdListener
import com.facebook.shimmer.ShimmerFrameLayout

object MetaUtils {
    const val TAG = "==Meta ADS=="
    var shimmerFrameLayout: ShimmerFrameLayout?=null
    var isTesting = false
    var dialogFullScreen: Dialog? = null

    fun initMeta(context: Context, isDebug : Boolean){
        isTesting = isDebug
        AudienceNetworkAds.initialize(context)
        AdSettings.addTestDevice("02e1dfda-2026-4436-a0e5-43812c5f7816")
    }

    @JvmStatic
    fun isNetworkConnected(context: Context): Boolean {
        try {
            val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            return cm.activeNetworkInfo != null && cm.activeNetworkInfo!!.isConnected
        }catch (e : Exception){
            return false
        }
    }

    fun dialogLoading(context: Activity) {
        dialogFullScreen = Dialog(context)
        dialogFullScreen?.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialogFullScreen?.setContentView(R.layout.dialog_full_screen)
        dialogFullScreen?.setCancelable(false)
        dialogFullScreen?.window!!.setBackgroundDrawable(ColorDrawable(Color.WHITE))
        dialogFullScreen?.window!!.setLayout(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.MATCH_PARENT
        )
        val img = dialogFullScreen?.findViewById<LottieAnimationView>(R.id.imageView3)
        img?.setAnimation(R.raw.gifloading)
        try {
            if (!context.isFinishing && dialogFullScreen != null && dialogFullScreen?.isShowing == false) {
                dialogFullScreen?.show()
            }
        } catch (ignored: Exception) {
        }

    }

    @JvmStatic
    fun dismissAdDialog() {
        try {
            if (dialogFullScreen != null && dialogFullScreen?.isShowing == true) {
                dialogFullScreen?.dismiss()
            }
        }catch (_: Exception){

        }
    }

    @JvmStatic
    fun loadAdBanner(
        activity: Activity,
        bannerId: String?,
        viewGroup: ViewGroup,
        bannerAdCallback: BannerCallBack
    ) {
        var id = bannerId
        if (!isNetworkConnected(activity)) {
            viewGroup.visibility = View.GONE
            bannerAdCallback.onFailed("No internet")
            return
        }
        if (isTesting) {
            id = "IMG_16_9_APP_INSTALL#$bannerId"
        }
        val tagView = activity.layoutInflater.inflate(R.layout.layoutbanner_loading, null, false)
        val adView = AdView(activity, id, AdSize.BANNER_HEIGHT_50)

        try {
            viewGroup.removeAllViews()
            viewGroup.addView(tagView, 0)
            viewGroup.addView(adView, 1)
        }catch (_: Exception){

        }
        shimmerFrameLayout = tagView.findViewById(R.id.shimmer_view_container)
        shimmerFrameLayout?.startShimmer()
        // Add the ad view to your activity layout
        val adListener: AdListener = object : AdListener {
            override fun onError(ad: Ad?, adError: AdError) {
                Log.d(TAG, "onError: " + adError.errorMessage)
                shimmerFrameLayout?.stopShimmer()
                viewGroup.removeView(tagView)
                bannerAdCallback.onFailed(adError.errorMessage)
            }

            override fun onAdLoaded(ad: Ad?) {
                shimmerFrameLayout?.stopShimmer()
                viewGroup.removeView(tagView)
                bannerAdCallback.onLoad()
            }

            override fun onAdClicked(ad: Ad?) {
            }

            override fun onLoggingImpression(ad: Ad?) {

            }
        }
        // Request an ad
        adView.loadAd(adView.buildLoadAdConfig().withAdListener(adListener).build())
    }


    @JvmStatic
    fun loadAndGetNativeAds(
        context: Context,
        nativeHolder: NativeHolder,
        adCallback: NativeCallback
    ) {
        if (!isNetworkConnected(context)) {
            adCallback.onAdFail("No internet")
            return
        }
        var id = nativeHolder.ads
        //If native is loaded return
        if (nativeHolder.nativeAd != null) {
            Log.d("===AdsLoadsNative", "Native not null")
            return
        }
        if (isTesting) {
            id = "IMG_16_9_APP_INSTALL#" + nativeHolder.ads
        }
        val nativeAd = NativeAd(context, id)
        nativeHolder.isLoad = true
        val nativeAdListener: NativeAdListener = object : NativeAdListener {
            override fun onMediaDownloaded(ad: Ad) {
                // Native ad finished downloading all assets
                Log.e(TAG, "Native ad finished downloading all assets.")
            }

            override fun onError(ad: Ad, adError: AdError) {
                // Native ad failed to load
                Log.e(TAG, "Native ad failed to load: " + adError.errorMessage)
                nativeHolder.nativeAd = null
                nativeHolder.isLoad = false
                nativeHolder.native_mutable.value = null
                adCallback.onAdFail(adError.errorMessage)
            }

            override fun onAdLoaded(ad: Ad) {
                // Native ad is loaded and ready to be displayed
                Log.d(TAG, "Native ad is loaded and ready to be displayed!")
                if (nativeAd == null || nativeAd != ad) {
                    return;
                }
                nativeHolder.nativeAd = nativeAd
                nativeHolder.isLoad = false
                nativeHolder.native_mutable.value = nativeAd
                adCallback.onLoadedAndGetNativeAd(nativeAd)
                // Inflate Native Ad into Container
            }

            override fun onAdClicked(ad: Ad) {
                // Native ad clicked
                Log.d(TAG, "Native ad clicked!")
            }

            override fun onLoggingImpression(ad: Ad) {
                // Native ad impression
                Log.d(TAG, "Native ad impression logged!")
            }
        }

        // Request an ad
        nativeAd.loadAd(
            nativeAd.buildLoadAdConfig()
                .withAdListener(nativeAdListener)
                .build()
        )
    }


    @JvmStatic
    fun showNativeAdsWithLayout(
        activity: Activity,
        nativeHolder: NativeHolder,
        viewGroup: NativeAdLayout,
        layout: Int,
        size: GoogleENative,
        callback: AdsNativeCallBackAdmod
    ) {
        if (!isNetworkConnected(activity)) {
            viewGroup.visibility = View.GONE
            return
        }
        if (shimmerFrameLayout != null) {
            shimmerFrameLayout?.stopShimmer()
        }
        try {
            viewGroup.removeAllViews()
        }catch (_: Exception){

        }

        if (!nativeHolder.isLoad) {
            if (nativeHolder.nativeAd != null) {
                if (shimmerFrameLayout != null) {
                    shimmerFrameLayout?.stopShimmer()
                }
                try {
                    inflateAd(activity, nativeHolder.nativeAd!!,layout, viewGroup)
                }catch (_: Exception){

                }

                callback.NativeLoaded()
                nativeHolder.native_mutable.removeObservers((activity as LifecycleOwner))
            } else {
                if (shimmerFrameLayout != null) {
                    shimmerFrameLayout?.stopShimmer()
                }
                callback.NativeFailed("None Show")
                nativeHolder.native_mutable.removeObservers((activity as LifecycleOwner))
            }
        } else {
            val tagView: View = if (size === GoogleENative.UNIFIED_MEDIUM) {
                activity.layoutInflater.inflate(R.layout.layoutnative_loading_medium, null, false)
            } else if (size === GoogleENative.UNIFIED_SMALL){
                activity.layoutInflater.inflate(R.layout.layoutnative_loading_small, null, false)
            }else{
                activity.layoutInflater.inflate(R.layout.layoutbanner_loading, null, false)
            }
            try {
                viewGroup.addView(tagView, 0)
            }catch (_ : Exception){

            }

            if (shimmerFrameLayout == null) shimmerFrameLayout = tagView.findViewById(R.id.shimmer_view_container)
            shimmerFrameLayout?.startShimmer()
            nativeHolder.native_mutable.observe((activity as LifecycleOwner)) { nativeAd: NativeAd? ->
                if (nativeAd != null) {
                    if (shimmerFrameLayout != null) {
                        shimmerFrameLayout?.stopShimmer()
                    }
                    try {
                        inflateAd(activity, nativeAd,layout, viewGroup)
                    }catch (_: Exception){

                    }

                    callback.NativeLoaded()
                    nativeHolder.native_mutable.removeObservers((activity as LifecycleOwner))
                } else {
                    if (shimmerFrameLayout != null) {
                        shimmerFrameLayout?.stopShimmer()
                    }
                    callback.NativeFailed("None Show")
                    nativeHolder.native_mutable.removeObservers((activity as LifecycleOwner))
                }
            }
        }
    }

    private fun inflateAd(activity: Activity, nativeAd: NativeAd,layout : Int, viewGroup: NativeAdLayout) {
        nativeAd.unregisterView()

        // Add the Ad view into the ad container.
        val inflater = LayoutInflater.from(activity)
        // Inflate the Ad view.  The layout referenced should be the one you created in the last step.
        val adView = inflater.inflate(layout, viewGroup, false) as LinearLayout
        viewGroup.addView(adView)

        // Add the AdOptionsView
        val adChoicesContainer = activity.findViewById<LinearLayout>(R.id.ad_choices_container)
        val adOptionsView = AdOptionsView(activity, nativeAd, viewGroup)
        adChoicesContainer.removeAllViews()
        adChoicesContainer.addView(adOptionsView, 0)

        // Create native UI using the ad metadata.
        val nativeAdIcon: MediaView = adView.findViewById(R.id.native_ad_icon)
        val nativeAdTitle: TextView = adView.findViewById(R.id.native_ad_title)
        val nativeAdMedia: MediaView = adView.findViewById(R.id.native_ad_media)
        val nativeAdSocialContext: TextView = adView.findViewById(R.id.native_ad_social_context)
        val nativeAdBody: TextView = adView.findViewById(R.id.native_ad_body)
        val sponsoredLabel: TextView = adView.findViewById(R.id.native_ad_sponsored_label)
        val nativeAdCallToAction: Button = adView.findViewById(R.id.native_ad_call_to_action)

        // Set the Text.
        nativeAdTitle.text = nativeAd.advertiserName
        nativeAdBody.text = nativeAd.adBodyText
        nativeAdSocialContext.text = nativeAd.adSocialContext
        nativeAdCallToAction.visibility =
            if (nativeAd.hasCallToAction()) View.VISIBLE else View.INVISIBLE
        nativeAdCallToAction.text = nativeAd.adCallToAction
        sponsoredLabel.text = nativeAd.sponsoredTranslation

        // Create a list of clickable views
        val clickableViews: MutableList<View> = ArrayList()
        clickableViews.add(nativeAdTitle)
        clickableViews.add(nativeAdCallToAction)

        // Register the Title and CTA button to listen for clicks.
        nativeAd.registerViewForInteraction(
            adView, nativeAdMedia, nativeAdIcon, clickableViews
        )
    }

    fun loadAndShowInterstitialAd(activity: Activity, admobId: InterHolder, enableLoadingDialog: Boolean, adCallback : AdsInterCallBack){
        if (!isNetworkConnected(activity)) {
            adCallback.onAdFail("No internet")
            return
        }
        var id = admobId.ads
        if (isTesting) {
            id = "IMG_16_9_APP_INSTALL#" + admobId.ads
        }
        if (enableLoadingDialog) {
            dialogLoading(activity)
        }

        val interstitialAd = InterstitialAd(activity, id)
        val interstitialAdListener: InterstitialAdListener = object : InterstitialAdListener {
            override fun onInterstitialDisplayed(ad: Ad) {
                // Interstitial ad displayed callback
                adCallback.onAdShowed()
                Handler(Looper.getMainLooper()).postDelayed({
                    dismissAdDialog()
                },800)
            }

            override fun onInterstitialDismissed(ad: Ad) {
                // Interstitial dismissed callback
                adCallback.onEventClickAdClosed()
            }

            override fun onError(ad: Ad, adError: AdError) {
                // Ad error callback
            }

            override fun onAdLoaded(ad: Ad) {
                // Interstitial ad is loaded and ready to be displayed
                // Show the ad
                interstitialAd.show()
            }

            override fun onAdClicked(ad: Ad) {
                // Ad clicked callback
            }

            override fun onLoggingImpression(ad: Ad) {
                // Ad impression logged callback
                // Please refer to Monetization Manager or Reporting API for final impression numbers
            }
        }
        // For auto play video ads, it's recommended to load the ad
        // at least 30 seconds before it is shown
        interstitialAd.loadAd(
            interstitialAd.buildLoadAdConfig()
                .withAdListener(interstitialAdListener)
                .build()
        )
    }
}