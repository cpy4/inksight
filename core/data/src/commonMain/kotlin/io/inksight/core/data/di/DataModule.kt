package io.inksight.core.data.di

import io.inksight.core.data.api.ClaudeApiClient
import io.inksight.core.data.api.createHttpClient
import io.inksight.core.data.db.DatabaseDriverFactory
import io.inksight.core.data.db.InkSightDatabase
import io.inksight.core.data.repository.ScanRepositoryImpl
import io.inksight.core.data.repository.SettingsRepositoryImpl
import io.inksight.core.data.repository.TranscriptionRepositoryImpl
import io.inksight.core.domain.repository.ScanRepository
import io.inksight.core.domain.repository.SettingsRepository
import io.inksight.core.domain.repository.TranscriptionRepository
import io.inksight.core.domain.usecase.GetScanHistoryUseCase
import io.inksight.core.domain.usecase.TranscribeImageUseCase
import io.inksight.core.domain.usecase.UpdateTranscriptionUseCase
import org.koin.dsl.module

val dataModule = module {
    // HTTP & API
    single { createHttpClient() }
    single { ClaudeApiClient(get()) }

    // Database
    single { get<DatabaseDriverFactory>().createDriver() }
    single { InkSightDatabase(get()) }

    // Repositories
    single<ScanRepository> { ScanRepositoryImpl(get()) }
    single<TranscriptionRepository> { TranscriptionRepositoryImpl(get(), get()) }
    single<SettingsRepository> { SettingsRepositoryImpl(get(), get()) }

    // Use Cases
    factory { TranscribeImageUseCase(get(), get()) }
    factory { GetScanHistoryUseCase(get()) }
    factory { UpdateTranscriptionUseCase(get()) }
}
