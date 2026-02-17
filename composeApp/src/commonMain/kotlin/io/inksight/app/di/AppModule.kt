package io.inksight.app.di

import io.inksight.app.screen.history.HistoryScreenModel
import io.inksight.app.screen.home.HomeScreenModel
import io.inksight.app.screen.result.ResultScreenModel
import io.inksight.app.screen.settings.SettingsScreenModel
import org.koin.dsl.module

val appModule = module {
    factory { HomeScreenModel(get(), get(), get(), get()) }
    factory { params -> ResultScreenModel(params.get(), get(), get(), get()) }
    factory { HistoryScreenModel(get(), get()) }
    factory { SettingsScreenModel(get()) }
}
