package com.example.zenapp.ui.appselection

import android.app.Application
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.zenapp.ui.blocklist.AppItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class AppSelectionUiState(
    val isLoading: Boolean = true,
    val allApps: List<AppItemWithIcon> = emptyList(),
    val filteredApps: List<AppItemWithIcon> = emptyList(),
    val selectedCount: Int = 0,
    val searchQuery: String = ""
)

data class AppItemWithIcon(
    val appItem: AppItem,
    val icon: ImageBitmap?
)

class AppSelectionViewModel(application: Application) : AndroidViewModel(application) {
    
    private val _uiState = MutableStateFlow(AppSelectionUiState())
    val uiState: StateFlow<AppSelectionUiState> = _uiState.asStateFlow()
    
    private var preSelectedPackages: Set<String> = emptySet()

    init {
        loadInstalledApps()
    }
    
    fun setPreSelectedApps(selectedApps: List<AppItem>) {
        preSelectedPackages = selectedApps.map { it.packageName }.toSet()
        // Re-aplicar selección a las apps ya cargadas
        _uiState.update { state ->
            val updatedApps = state.allApps.map { appWithIcon ->
                if (appWithIcon.appItem.packageName in preSelectedPackages) {
                    appWithIcon.copy(
                        appItem = appWithIcon.appItem.copy(isBlocked = true)
                    )
                } else {
                    appWithIcon
                }
            }
            val selectedCount = updatedApps.count { it.appItem.isBlocked }
            val filteredApps = if (state.searchQuery.isBlank()) {
                updatedApps
            } else {
                updatedApps.filter { 
                    it.appItem.name.contains(state.searchQuery, ignoreCase = true) 
                }
            }
            state.copy(
                allApps = updatedApps,
                filteredApps = filteredApps,
                selectedCount = selectedCount
            )
        }
    }

    private fun loadInstalledApps() {
        viewModelScope.launch(Dispatchers.IO) {
            val pm = getApplication<Application>().packageManager
            val installedApps: List<ApplicationInfo> = pm.getInstalledApplications(PackageManager.GET_META_DATA)
            
            val apps = installedApps.mapNotNull { appInfo ->
                val launchIntent = pm.getLaunchIntentForPackage(appInfo.packageName)
                if (launchIntent != null) {
                    val appName = pm.getApplicationLabel(appInfo).toString()
                    val appIcon: Drawable = pm.getApplicationIcon(appInfo)
                    
                    val appItem = AppItem(
                        name = appName,
                        packageName = appInfo.packageName,
                        category = AppCategory.OTHER,
                        isBlocked = appInfo.packageName in preSelectedPackages
                    )
                    
                    AppItemWithIcon(
                        appItem = appItem,
                        icon = drawableToImageBitmap(appIcon)
                    )
                } else {
                    null
                }
            }.sortedBy { it.appItem.name }
            
            val selectedCount = apps.count { it.appItem.isBlocked }
            _uiState.update { 
                it.copy(
                    isLoading = false,
                    allApps = apps,
                    filteredApps = apps,
                    selectedCount = selectedCount
                )
            }
        }
    }

    fun onSearchQueryChange(query: String) {
        _uiState.update { state ->
            val filtered = if (query.isBlank()) {
                state.allApps
            } else {
                state.allApps.filter { 
                    it.appItem.name.contains(query, ignoreCase = true) 
                }
            }
            
            state.copy(
                searchQuery = query,
                filteredApps = filtered
            )
        }
    }

    fun toggleAppSelection(packageName: String) {
        _uiState.update { state ->
            val updatedApps = state.allApps.map { appWithIcon ->
                if (appWithIcon.appItem.packageName == packageName) {
                    appWithIcon.copy(
                        appItem = appWithIcon.appItem.copy(
                            isBlocked = !appWithIcon.appItem.isBlocked
                        )
                    )
                } else {
                    appWithIcon
                }
            }
            
            val selectedCount = updatedApps.count { it.appItem.isBlocked }
            
            val filteredApps = if (state.searchQuery.isBlank()) {
                updatedApps
            } else {
                updatedApps.filter { 
                    it.appItem.name.contains(state.searchQuery, ignoreCase = true) 
                }
            }
            
            state.copy(
                allApps = updatedApps,
                filteredApps = filteredApps,
                selectedCount = selectedCount
            )
        }
    }

    fun getSelectedApps(): List<AppItem> {
        return _uiState.value.allApps
            .filter { it.appItem.isBlocked }
            .map { it.appItem }
    }

    private fun drawableToImageBitmap(drawable: Drawable): ImageBitmap? {
        return try {
            if (drawable is BitmapDrawable) {
                drawable.bitmap.asImageBitmap()
            } else {
                val bitmap = Bitmap.createBitmap(
                    drawable.intrinsicWidth,
                    drawable.intrinsicHeight,
                    Bitmap.Config.ARGB_8888
                )
                val canvas = Canvas(bitmap)
                drawable.setBounds(0, 0, canvas.width, canvas.height)
                drawable.draw(canvas)
                bitmap.asImageBitmap()
            }
        } catch (e: Exception) {
            null
        }
    }
}
