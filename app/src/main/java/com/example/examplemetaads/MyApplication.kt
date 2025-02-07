package com.example.examplemetaads

import android.app.Application
import com.detech.metalibrary.MetaUtils

class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        MetaUtils.initMeta(this, true)
    }
}