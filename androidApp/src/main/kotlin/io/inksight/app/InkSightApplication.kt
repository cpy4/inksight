package io.inksight.app

import android.app.Application
import io.inksight.app.di.initKoin
import org.koin.android.ext.koin.androidContext

class InkSightApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        initKoin {
            androidContext(this@InkSightApplication)
        }
    }
}
