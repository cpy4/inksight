package io.inksight.app

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.transitions.SlideTransition
import io.inksight.app.screen.home.HomeScreen
import io.inksight.app.theme.InkSightTheme
import io.inksight.core.domain.repository.SettingsRepository
import org.koin.compose.koinInject

@Composable
fun App() {
    val settingsRepository = koinInject<SettingsRepository>()
    val settings by settingsRepository.getSettings().collectAsState(initial = null)

    InkSightTheme(darkTheme = settings?.isDarkMode) {
        Navigator(HomeScreen()) { navigator ->
            SlideTransition(navigator)
        }
    }
}
