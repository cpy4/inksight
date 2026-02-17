package io.inksight.app.di

import org.koin.core.KoinApplication
import org.koin.core.context.startKoin

fun initKoin(platformConfig: KoinApplication.() -> Unit = {}) {
    startKoin {
        platformConfig()
        modules(
            // Modules will be added as they are built
        )
    }
}
