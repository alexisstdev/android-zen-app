package com.example.zenapp.ui.blocklist

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.zenapp.data.remote.dto.UserSettingsDto
import com.example.zenapp.data.repository.Result
import com.example.zenapp.data.repository.ZenAppRepository
import com.example.zenapp.util.UserIdProvider
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class BlocklistUiState(
    val selectedApps: List<AppItem> = emptyList(),
    val customMessage: String = "Mantén el enfoque. Esta app estará disponible en un momento.",
    val pauseTime: String = "30 segundos",
    val dailyOpens: String = "3",
    val sessionDuration: String = "10 minutos",
    val isSaved: Boolean = false,
    val isLoading: Boolean = false,
    val isSyncing: Boolean = false,
    val errorMessage: String? = null,
    val mindfulPrompt: String? = null,
    val dailyTask: String? = null,
    val focusQuote: String? = null
)

class BlocklistViewModel(application: Application) : AndroidViewModel(application) {
    
    private val repository = ZenAppRepository()
    private val userId = UserIdProvider.getUserId(application)
    
    private val _uiState = MutableStateFlow(BlocklistUiState())
    val uiState: StateFlow<BlocklistUiState> = _uiState.asStateFlow()

    init {
        loadSettingsFromServer()
        loadDynamicContent()
    }

    private fun loadSettingsFromServer() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            
            when (val result = repository.getUserSettings(userId)) {
                is Result.Success -> {
                    val settings = result.data
                    val apps = settings.selectedApps.map { packageName ->
                        AppItem(
                            name = packageName.split(".").last().capitalize(),
                            packageName = packageName,
                            category = com.example.zenapp.ui.appselection.AppCategory.OTHER,
                            isBlocked = true
                        )
                    }
                    
                    _uiState.update {
                        it.copy(
                            selectedApps = apps,
                            customMessage = settings.customMessage.takeIf { msg -> msg.isNotEmpty() } 
                                ?: it.customMessage,
                            pauseTime = settings.pauseTime.takeIf { time -> time.isNotEmpty() } 
                                ?: it.pauseTime,
                            dailyOpens = settings.dailyOpens.takeIf { opens -> opens.isNotEmpty() } 
                                ?: it.dailyOpens,
                            sessionDuration = settings.sessionDuration.takeIf { duration -> duration.isNotEmpty() } 
                                ?: it.sessionDuration,
                            isLoading = false
                        )
                    }
                }
                is Result.Error -> {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = "Error al cargar: ${result.message}"
                        )
                    }
                }
                else -> {}
            }
        }
    }

    private fun loadDynamicContent() {
        viewModelScope.launch {
            // Load mindful prompt
            launch {
                when (val result = repository.getMindfulPrompt()) {
                    is Result.Success -> {
                        _uiState.update { it.copy(mindfulPrompt = result.data) }
                    }
                    else -> {}
                }
            }
            
            // Load daily task
            launch {
                when (val result = repository.getDailyMiniTask()) {
                    is Result.Success -> {
                        _uiState.update { it.copy(dailyTask = result.data) }
                    }
                    else -> {}
                }
            }
            
            // Load focus quote
            launch {
                when (val result = repository.getFocusQuote()) {
                    is Result.Success -> {
                        _uiState.update { it.copy(focusQuote = result.data) }
                    }
                    else -> {}
                }
            }
        }
    }

    fun addSelectedApps(apps: List<AppItem>) {
        _uiState.update { state ->
            val currentApps = state.selectedApps.toMutableList()
            apps.forEach { newApp ->
                if (currentApps.none { it.packageName == newApp.packageName }) {
                    currentApps.add(newApp)
                }
            }
            state.copy(
                selectedApps = currentApps,
                isSaved = false
            )
        }
        syncSettingsToServer()
    }

    fun updateCustomMessage(message: String) {
        _uiState.update { it.copy(customMessage = message, isSaved = false) }
    }

    fun updatePauseTime(time: String) {
        _uiState.update { it.copy(pauseTime = time, isSaved = false) }
    }

    fun updateDailyOpens(opens: String) {
        _uiState.update { it.copy(dailyOpens = opens, isSaved = false) }
    }

    fun updateSessionDuration(duration: String) {
        _uiState.update { it.copy(sessionDuration = duration, isSaved = false) }
    }

    fun saveSettings() {
        _uiState.update { it.copy(isSaved = true) }
        syncSettingsToServer()
    }

    private fun syncSettingsToServer() {
        viewModelScope.launch {
            _uiState.update { it.copy(isSyncing = true, errorMessage = null) }
            
            val currentState = _uiState.value
            val settingsDto = UserSettingsDto(
                selectedApps = currentState.selectedApps.map { it.packageName },
                customMessage = currentState.customMessage,
                pauseTime = currentState.pauseTime,
                dailyOpens = currentState.dailyOpens,
                sessionDuration = currentState.sessionDuration
            )
            
            when (repository.saveUserSettings(userId, settingsDto)) {
                is Result.Success -> {
                    _uiState.update {
                        it.copy(
                            isSyncing = false,
                            isSaved = true
                        )
                    }
                }
                is Result.Error -> {
                    _uiState.update {
                        it.copy(
                            isSyncing = false,
                            errorMessage = "Error al sincronizar"
                        )
                    }
                }
                else -> {}
            }
        }
    }

    fun refreshDynamicContent() {
        loadDynamicContent()
    }

    fun clearError() {
        _uiState.update { it.copy(errorMessage = null) }
    }

    fun refreshDynamicContent() {
        loadDynamicContent()
    }

    fun getSelectedAppsCount(): Int {
        return _uiState.value.selectedApps.size
    }
}


