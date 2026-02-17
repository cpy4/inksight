package io.inksight.core.platform.di

import io.inksight.core.platform.FileManager
import io.inksight.core.platform.ImageProcessor
import io.inksight.core.platform.SecureStorage
import io.inksight.core.platform.ShareManager
import org.koin.core.module.Module
import org.koin.dsl.module

actual val platformModule: Module = module {
    single { ImageProcessor() }
    single { SecureStorage(get()) }
    single { ShareManager(get()) }
    single { FileManager(get()) }
}
