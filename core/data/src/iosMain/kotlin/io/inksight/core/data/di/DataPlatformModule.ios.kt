package io.inksight.core.data.di

import com.russhwolf.settings.NSUserDefaultsSettings
import com.russhwolf.settings.Settings
import io.inksight.core.data.db.DatabaseDriverFactory
import org.koin.core.module.Module
import org.koin.dsl.module
import platform.Foundation.NSUserDefaults

actual val dataPlatformModule: Module = module {
    single { DatabaseDriverFactory() }
    single<Settings> { NSUserDefaultsSettings(NSUserDefaults.standardUserDefaults) }
}
