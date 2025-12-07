# 🔒 Sistema de Bloqueo de Apps - ZenApp

## Funcionalidad Implementada

ZenApp ahora incluye un **sistema completo de bloqueo de apps** que intercepta cuando intentas abrir aplicaciones bloqueadas y te muestra una pantalla de pausa mindful.

## 🏗️ Arquitectura Completa

### 1. **Base de Datos Local (Room)**
- `BlockedAppEntity`: Apps bloqueadas con nombre y packageName
- `SettingsEntity`: Configuración (tiempo de pausa, mensaje, límites diarios)
- **Cache offline**: Funciona sin internet después de la primera sincronización

### 2. **Servicio de Accesibilidad**
- `AppBlockerService`: Detecta cuando abres una app
- Monitorea eventos `TYPE_WINDOW_STATE_CHANGED`
- Filtra apps del sistema automáticamente
- Solo actúa sobre apps en tu lista de bloqueo

### 3. **Pantalla de Bloqueo**
- `BlockScreenActivity`: Interfaz que se muestra al abrir app bloqueada
- Cuenta regresiva configurable (5 seg - 10 min)
- Mensaje personalizado
- Opciones:
  - ✅ "Mantener el enfoque" → Vuelve atrás
  - ⏰ Esperar timer → "Abrir de todas formas"

### 4. **Sincronización Dual**
- Servidor Express → Sync multi-dispositivo (cuando hay internet)
- Room Database → Cache local (funciona offline)
- Estrategia: "Cache-First con Sync en Background"

## 📱 Flujo de Uso

### Primera Configuración

1. **Selecciona Apps**
   - Navega a "Bloqueos"
   - Toca "Agregar apps"
   - Selecciona las apps que quieres bloquear
   - Toca "Confirmar selección"

2. **Configura Parámetros**
   - **Pausar por**: Tiempo de espera antes de poder abrir (5s - 10min)
   - **Con el mensaje**: Texto personalizado que verás
   - **Objetivo diario**: Límite de veces por día (próximamente)
   - **Duración de sesión**: Tiempo máximo por uso (próximamente)

3. **Activa el Servicio**
   - Ve a la pestaña "Ajustes"
   - Toca "Abrir Configuración"
   - Sistema → Accesibilidad → ZenApp
   - Activa el servicio
   - Acepta permisos

4. **Guarda Configuración**
   - Toca "Guardar Configuración"
   - Se sincroniza con el servidor (si hay internet)
   - Se guarda en cache local (Room)

### Uso Diario

```
Intentas abrir Instagram
       ↓
AppBlockerService detecta el paquete
       ↓
Verifica si está en blocked_apps (Room)
       ↓
BlockScreenActivity aparece
       ↓
Cuenta regresiva: "30 segundos"
Mensaje: "¿Realmente necesitas abrir esta app ahora?"
       ↓
OPCIÓN 1: "Mantener el enfoque" → Vuelves al home
OPCIÓN 2: Esperar 30s → "Abrir de todas formas" → Instagram se abre
```

## 🔧 Componentes Técnicos

### Room Database
```kotlin
@Database(entities = [BlockedAppEntity, SettingsEntity])
abstract class ZenAppDatabase : RoomDatabase() {
    abstract fun blockedAppDao(): BlockedAppDao
    abstract fun settingsDao(): SettingsDao
}
```

### Servicio de Accesibilidad
```xml
<!-- AndroidManifest.xml -->
<service android:name=".service.AppBlockerService"
    android:permission="android.permission.BIND_ACCESSIBILITY_SERVICE">
    <intent-filter>
        <action android:name="android.accessibilityservice.AccessibilityService" />
    </intent-filter>
    <meta-data android:resource="@xml/accessibility_service_config" />
</service>
```

### Pantalla de Bloqueo
```kotlin
BlockScreenActivity: ComponentActivity
├── CountDownTimer: Cuenta regresiva
├── BlockScreen: Composable UI
│   ├── App Icon + Name
│   ├── Custom Message
│   ├── Timer Circle
│   └── Buttons (Close / Proceed)
└── parseTimeToMillis(): Convierte "30 segundos" → 30000ms
```

## 🎯 Características

### ✅ Implementado
- [x] Detección automática de apps bloqueadas
- [x] Pantalla de pausa con timer configurable
- [x] Mensaje personalizado
- [x] Cache offline con Room
- [x] Sincronización con servidor
- [x] Preselección de apps ya bloqueadas
- [x] UI de configuración del servicio
- [x] Botón "Mantener el enfoque"
- [x] Botón "Abrir de todas formas" (después del timer)

### 🔄 Próximamente
- [ ] Tracking de aperturas diarias por app
- [ ] Límite de sesiones diarias (bloqueo después de N usos)
- [ ] Duración máxima de sesión (cierre automático después de X minutos)
- [ ] Estadísticas de uso
- [ ] Notificaciones de resumen diario
- [ ] Modo "Horario de enfoque" (bloquear en ciertos horarios)

## 🔐 Permisos Necesarios

### Accesibilidad (`BIND_ACCESSIBILITY_SERVICE`)
- **Uso**: Detectar qué app está en primer plano
- **Alcance**: Solo lee el `packageName` de la app activa
- **No lee**: Contenido de pantalla, texto, contraseñas, etc.

### Uso de Apps (`PACKAGE_USAGE_STATS`)
- **Uso**: Obtener lista de apps instaladas
- **Futuro**: Estadísticas de tiempo de uso

### Ventana del Sistema (`SYSTEM_ALERT_WINDOW`)
- **Uso**: Mostrar BlockScreenActivity sobre otras apps
- **Necesario**: Para interceptar la apertura de apps

## 🐛 Troubleshooting

### El servicio no detecta apps bloqueadas

1. **Verifica que el servicio esté activo**:
   ```
   Ajustes → Accesibilidad → ZenApp → ON
   ```

2. **Reinicia el servicio**:
   - Desactívalo y actívalo nuevamente
   - O reinicia el dispositivo

3. **Verifica permisos**:
   ```
   Configuración → Apps → ZenApp → Permisos
   ```

### La pantalla de bloqueo no aparece

1. **Permiso de ventanas emergentes**:
   ```
   Configuración → Apps → ZenApp → Aparecer en primer plano
   ```

2. **Verifica que la app esté en la lista**:
   - Ve a "Bloqueos"
   - Asegúrate de que la app tenga el ✓

3. **Revisa logs en Android Studio**:
   ```
   adb logcat | grep AppBlockerService
   ```

### Los ajustes no se guardan

1. **Sin internet**: Se guardan localmente, sincronizarán después
2. **Con errores**: Revisa la card roja de error
3. **Room corruption**: Desinstala y reinstala la app

## 📊 Base de Datos Local

### Tabla: blocked_apps
| Columna | Tipo | Descripción |
|---------|------|-------------|
| packageName | String (PK) | com.instagram.android |
| appName | String | Instagram |
| isBlocked | Boolean | true |

### Tabla: settings
| Columna | Tipo | Descripción |
|---------|------|-------------|
| id | Int (PK) | 1 |
| pauseTime | String | "30 segundos" |
| customMessage | String | Mensaje personalizado |
| dailyOpens | String | "3" |
| sessionDuration | String | "10 minutos" |

## 🔄 Sincronización

```
┌─────────────────┐
│  BlocklistVM    │
│  (UI Layer)     │
└────────┬────────┘
         │
         ↓
┌─────────────────┐
│  Repository     │  ← saveUserSettings()
│  (Data Layer)   │
└────┬───────┬────┘
     │       │
     ↓       ↓
┌─────────┐ ┌─────────┐
│  Room   │ │ Retrofit│
│  (Local)│ │ (Remote)│
└─────────┘ └─────────┘
     ↓
┌─────────────────┐
│ AppBlockerService│ ← Lee desde Room
│ (Background)    │
└─────────────────┘
```

## 🎨 UI de Bloqueo

### Diseño
- Fondo oscuro (#121212) para reducir distracción
- Timer circular grande y visible
- Mensaje mindful centrado
- 2 botones claros:
  - Primario: "Mantener el enfoque" (outline)
  - Secundario: "Abrir de todas formas" (solo después del timer)

### Estados
1. **Bloqueado**: Timer corriendo, solo botón "Mantener el enfoque"
2. **Desbloqueado**: Timer terminado, ambos botones visibles

## 📝 Notas de Desarrollo

- **AccessibilityService** es la forma oficial de Android para detectar apps activas
- **Room** provee cache offline y es más rápido que SharedPreferences
- **CountDownTimer** maneja la cuenta regresiva de forma eficiente
- El servicio corre en un **CoroutineScope** independiente para no bloquear UI

## 🚀 Próximos Pasos

1. Implementar tracking de sesiones
2. Agregar límite de aperturas diarias
3. Estadísticas visuales con gráficas
4. Modo "Focus Time" con horarios
5. Widgets de home screen
6. Quick Settings Tile para activar/desactivar bloqueo

---

**Importante**: El servicio de accesibilidad DEBE estar activo para que funcione el bloqueo. Sin este permiso, la app solo guardará configuración pero no interceptará apps.
