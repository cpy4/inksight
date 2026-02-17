package io.inksight.core.data.di

import com.russhwolf.settings.Settings
import com.russhwolf.settings.SharedPreferencesSettings
import io.inksight.core.data.db.DatabaseDriverFactory
import org.koin.core.module.Module
import org.koin.dsl.module

actual val dataPlatformModule: Module = module {
    single { DatabaseDriverFactory(get()) }
    single<Settings> {
        SharedPreferencesSettings(
            delegate = get<android.content.Context>()
                .getSharedPreferences("inksight_prefs", android.content.Context.MODE_PRIVATE)
        )
    }
}
