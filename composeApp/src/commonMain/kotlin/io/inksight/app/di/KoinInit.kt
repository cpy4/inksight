package io.inksight.app.di

import io.inksight.core.data.di.dataModule
import io.inksight.core.data.di.dataPlatformModule
import io.inksight.core.platform.di.platformModule
import org.koin.core.KoinApplication
import org.koin.core.context.startKoin

fun initKoin(platformConfig: KoinApplication.() -> Unit = {}) {
    startKoin {
        platformConfig()
        modules(
            platformModule,
            dataPlatformModule,
            dataModule,
            appModule,
        )
    }
}
