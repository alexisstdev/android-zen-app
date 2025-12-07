# Fix: Preview No Detecta Configuración Actualizada

## Problema Identificado
El `AppBlockActivity` no estaba leyendo la configuración de Room. Usaba valores hardcodeados:
- appName = "Instagram"
- message = hardcoded text
- waitTimeSeconds = 30

Esto significaba que el preview NUNCA mostraba los valores guardados, sin importar lo que el usuario configurara.

## Solución Implementada

### 1. AppBlockActivity.kt - Lectura de Room
```kotlin
// Antes: valores hardcoded
val appName = "Instagram"
val message = "..."
val waitTimeSeconds = 30

// Después: lee de Room
val database = ZenAppDatabase.getDatabase(applicationContext)
val settings = runBlocking {
    database.settingsDao().getSettingsOnce()
}

val message = settings?.customMessage ?: "Mantén el enfoque..."
val pauseTime = settings?.pauseTime ?: "30 segundos"
```

### 2. UI Simplificada
Eliminado:
- `totalOpens` / `remainingOpens` (no existen en SettingsEntity)
- Dots indicator
- Lógica de límite de aperturas

Ahora solo:
- Muestra el mensaje personalizado
- Temporizador basado en `pauseTime` (30s, 5min, 1h, etc.)
- Botón "Abrir de todas formas" que inicia countdown
- Botón "Mantener el enfoque" para cerrar

### 3. Helper Functions
```kotlin
parseTimeToSeconds(pauseTime: String): Int
// "30 segundos" → 30
// "5 minutos" → 300
// "1 hora" → 3600

formatTime(seconds: Int): String
// 90 → "1:30"
// 25 → "25s"
```

### 4. Logging Agregado

**ZenAppRepository.cacheSettings():**
```
D/ZenAppRepository: cacheSettings: Saving 3 apps
D/ZenAppRepository: cacheSettings: pauseTime=5 minutos, message=Enfócate en tu trabajo
D/ZenAppRepository: cacheSettings: Saved 3 apps to Room
D/ZenAppRepository: cacheSettings: Settings saved to Room
```

**AppBlockActivity.onCreate():**
```
D/AppBlockActivity: Settings from Room: SettingsEntity(id=1, pauseTime=5 minutos, customMessage=Enfócate...)
D/AppBlockActivity: Using message: Enfócate en tu trabajo
D/AppBlockActivity: Using pauseTime: 5 minutos
```

## Cómo Probar

### Test 1: Preview Manual
1. Abre la app
2. Ve a Settings → selecciona apps
3. Configura:
   - Mensaje: "Prueba de mensaje"
   - Pausa: "5 minutos"
4. Guarda
5. Verifica logs: `adb logcat | grep -E "ZenAppRepository|AppBlockActivity"`
6. Deberías ver:
   ```
   ZenAppRepository: cacheSettings: pauseTime=5 minutos, message=Prueba de mensaje
   ZenAppRepository: cacheSettings: Settings saved to Room
   ```
7. Abre "Vista Previa" desde Settings
8. Deberías ver:
   ```
   AppBlockActivity: Settings from Room: SettingsEntity(...)
   AppBlockActivity: Using message: Prueba de mensaje
   AppBlockActivity: Using pauseTime: 5 minutos
   ```
9. El preview debe mostrar "Prueba de mensaje" y countdown de 5 minutos

### Test 2: AccessibilityService
1. Habilita AccessibilityService en Settings del sistema
2. Selecciona una app (ej: Chrome)
3. Configura mensaje y pausa
4. Guarda
5. Verifica logs:
   ```bash
   adb logcat -c
   adb logcat | grep -E "AppBlockerService|AppBlockActivity|ZenAppRepository"
   ```
6. Abre la app bloqueada (Chrome)
7. Deberías ver:
   ```
   AppBlockerService: Window changed to: com.android.chrome
   AppBlockerService: Blocking app: com.android.chrome (Chrome)
   AppBlockActivity: Settings from Room: SettingsEntity(...)
   ```
8. La pantalla de bloqueo debe mostrar tu mensaje y pausa configurados

### Test 3: Verificar Room Directamente
```bash
# Conectar a la BD
adb shell "run-as com.example.zenapp cat /data/data/com.example.zenapp/databases/zen_app_database" > /tmp/zen.db
sqlite3 /tmp/zen.db

# Consultar settings
SELECT * FROM settings_table;

# Consultar apps bloqueadas
SELECT * FROM blocked_apps_table;
```

## Flujo Completo Verificado

1. **Save Settings:**
   ```
   SettingsScreen
   → BlocklistViewModel.syncSettingsToServer()
   → ZenAppRepository.saveUserSettings()
   → cacheSettings() ← GUARDA EN ROOM
   → API call
   ```

2. **Preview:**
   ```
   AppBlockActivity.onCreate()
   → database.settingsDao().getSettingsOnce() ← LEE DE ROOM
   → AppBlockScreen(message, pauseTime)
   ```

3. **Service Blocking:**
   ```
   AppBlockerService.onAccessibilityEvent()
   → showBlockScreen()
   → database.settingsDao().getSettingsOnce() ← LEE DE ROOM
   → startActivity(AppBlockActivity) con extras
   ```

## Archivos Modificados
- `app/src/main/java/com/example/zenapp/ui/appblock/AppBlockActivity.kt`
  - Agregado lectura de Room
  - Simplificado UI (removido totalOpens/remainingOpens)
  - Agregado parseTimeToSeconds() y formatTime()
  - Agregado logging

- `app/src/main/java/com/example/zenapp/data/repository/ZenAppRepository.kt`
  - Agregado logging en cacheSettings()

## Próximos Pasos
Si aún no funciona después de estos cambios:

1. Verificar que `saveSettings()` se llama correctamente
2. Verificar que `cacheSettings()` completa sin errores
3. Usar Room Inspector en Android Studio
4. Verificar que `runBlocking` no causa problemas de thread
5. Considerar usar `lifecycleScope.launch` en lugar de `runBlocking`
