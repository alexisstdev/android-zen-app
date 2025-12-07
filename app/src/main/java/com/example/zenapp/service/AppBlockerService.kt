package com.example.zenapp.service

import android.accessibilityservice.AccessibilityService
import android.content.Intent
import android.util.Log
import android.view.accessibility.AccessibilityEvent
import com.example.zenapp.data.local.ZenAppDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch

class AppBlockerService : AccessibilityService() {
    
    private val TAG = "AppBlockerService"
    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.Main)
    private lateinit var database: ZenAppDatabase
    private var blockedPackages = setOf<String>()
    private var lastBlockedPackage: String? = null
    private var lastBlockTime: Long = 0
    
    override fun onServiceConnected() {
        super.onServiceConnected()
        Log.d(TAG, "Service connected")
        database = ZenAppDatabase.getDatabase(applicationContext)
        
        // Escuchar cambios en apps bloqueadas
        serviceScope.launch {
            database.blockedAppDao().getAllBlockedApps().collect { apps ->
                blockedPackages = apps.map { it.packageName }.toSet()
                Log.d(TAG, "Blocked apps updated: ${blockedPackages.size} apps")
                blockedPackages.forEach { Log.d(TAG, "  - $it") }
            }
        }
    }
    
    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        event ?: return
        
        if (event.eventType == AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED) {
            val packageName = event.packageName?.toString() ?: return
            
            // Ignorar nuestra app y la pantalla de bloqueo
            if (packageName == this.packageName) {
                return
            }
            
            // Ignorar apps del sistema críticas
            if (packageName.startsWith("com.android.systemui") || 
                packageName.startsWith("com.android.launcher") ||
                packageName == "android") {
                return
            }
            
            Log.d(TAG, "Window changed to: $packageName")
            
            // Si la app está bloqueada, mostrar pantalla de bloqueo
            if (packageName in blockedPackages) {
                // Prevenir múltiples triggers en menos de 2 segundos
                val currentTime = System.currentTimeMillis()
                if (packageName == lastBlockedPackage && (currentTime - lastBlockTime) < 2000) {
                    Log.d(TAG, "Skipping duplicate block event for $packageName")
                    return
                }
                
                Log.d(TAG, "Blocking app: $packageName")
                lastBlockedPackage = packageName
                lastBlockTime = currentTime
                showBlockScreen(packageName)
            }
        }
    }
    
    private fun showBlockScreen(packageName: String) {
        serviceScope.launch {
            val settings = database.settingsDao().getSettingsOnce()
            val blockedApp = database.blockedAppDao().getBlockedApp(packageName)
            
            if (settings != null && blockedApp != null) {
                Log.d(TAG, "Showing block screen for ${blockedApp.appName}")
                val intent = Intent(this@AppBlockerService, BlockScreenActivity::class.java).apply {
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                    addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY)
                    putExtra("PACKAGE_NAME", packageName)
                    putExtra("APP_NAME", blockedApp.appName)
                    putExtra("PAUSE_TIME", settings.pauseTime)
                    putExtra("CUSTOM_MESSAGE", settings.customMessage)
                }
                startActivity(intent)
            } else {
                Log.w(TAG, "Cannot show block screen: settings=$settings, app=$blockedApp")
            }
        }
    }
    
    override fun onInterrupt() {
        // No action needed
    }
    
    override fun onDestroy() {
        super.onDestroy()
        serviceScope.cancel()
    }
}
