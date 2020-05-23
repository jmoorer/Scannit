package com.moor.scannit

import android.app.Application
import com.moor.scannit.data.ObjectBox

class App:Application() {

    override fun onCreate() {
        super.onCreate()
        ObjectBox.init(this)
    }
}