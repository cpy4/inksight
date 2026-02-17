package io.inksight.app.screen.history

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import io.inksight.core.domain.model.Scan
import io.inksight.core.domain.repository.ScanRepository
import io.inksight.core.domain.usecase.GetScanHistoryUseCase
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class HistoryUiState(
    val scans: List<Scan> = emptyList(),
    val searchQuery: String = "",
    val isLoading: Boolean = true,
    val isSearching: Boolean = false,
)

class HistoryScreenModel(
    private val getScanHistoryUseCase: GetScanHistoryUseCase,
    private val scanRepository: ScanRepository,
) : ScreenModel {

    private val _uiState = MutableStateFlow(HistoryUiState())
    val uiState: StateFlow<HistoryUiState> = _uiState.asStateFlow()

    private var searchJob: Job? = null

    init {
        loadScans()
    }

    private fun loadScans() {
        screenModelScope.launch {
            getScanHistoryUseCase().collect { scans ->
                _uiState.value = _uiState.value.copy(
                    scans = scans,
                    isLoading = false,
                )
            }
        }
    }

    fun onSearchQueryChanged(query: String) {
        _uiState.value = _uiState.value.copy(searchQuery = query)
        searchJob?.cancel()
        if (query.isBlank()) {
            loadScans()
            return
        }
        searchJob = screenModelScope.launch {
            _uiState.value = _uiState.value.copy(isSearching = true)
            try {
                val results = getScanHistoryUseCase.search(query)
                _uiState.value = _uiState.value.copy(
                    scans = results,
                    isSearching = false,
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(isSearching = false)
            }
        }
    }

    fun deleteScan(scanId: String) {
        screenModelScope.launch {
            scanRepository.deleteScan(scanId)
        }
    }
}
