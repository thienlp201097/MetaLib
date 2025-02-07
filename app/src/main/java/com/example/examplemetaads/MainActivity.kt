package com.example.examplemetaads

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.examplemetaads.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    val binding by lazy { ActivityMainBinding.inflate(layoutInflater) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)

        binding.loadNative.setOnClickListener {
            AdsManager.loadNative(this,AdsManager.nativeHolder)
        }
        binding.showNative.setOnClickListener {
            AdsManager.showNative(this,AdsManager.nativeHolder,binding.nativeAdContainer)
        }
        binding.showInter.setOnClickListener {
            AdsManager.loadAndShowInter(this,AdsManager.interHolder)
        }
        AdsManager.loadBanner(this,AdsManager.banner,binding.frBanner)
    }
}