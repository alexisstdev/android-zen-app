# 🔄 Funcionalidad de Sincronización ZenApp

## Resumen

ZenApp ahora cuenta con sincronización completa entre el dispositivo Android y un servidor backend Express. Los ajustes del usuario se guardan automáticamente en la nube y se cargan al iniciar la app.

## 🏗️ Arquitectura

### Backend (Express API)
- **Servidor:** Node.js + Express 4.18.2
- **Puerto:** 3000
- **Almacenamiento:** In-memory (Map)
- **URL Base:** `http://10.0.2.2:3000/api/v1` (emulador Android)

### Android (Retrofit)
- **Cliente HTTP:** Retrofit 2.9.0 + OkHttp 4.12.0
- **Serialización:** Moshi 1.15.0 con KSP
- **Patrón:** Repository + ViewModel + StateFlow
- **Identificación:** Android ID único por dispositivo

## 📡 Endpoints API

### 1. Configuración de Usuario
```
GET    /api/v1/users/:userId/settings
POST   /api/v1/users/:userId/settings
PUT    /api/v1/users/:userId/settings
```

**Request Body (POST/PUT):**
```json
{
  "selectedApps": ["com.instagram.android", "com.twitter.android"],
  "pauseTime": "10 segundos",
  "customMessage": "¿Realmente necesitas abrir esta app ahora?",
  "dailyOpens": "3",
  "sessionDuration": "15 minutos"
}
```

**Response:**
```json
{
  "success": true,
  "data": { /* mismo formato que request */ },
  "message": "Settings updated successfully"
}
```

### 2. Backup de Uso
```
POST   /api/v1/users/:userId/usage/backup
```

**Request Body:**
```json
{
  "appPackage": "com.instagram.android",
  "timestamp": 1704067200000,
  "duration": 1800000
}
```

### 3. Contenido Dinámico
```
GET    /api/v1/mindful_prompts        → Frases mindfulness
GET    /api/v1/mini_tasks/daily        → Mini-tareas diarias
GET    /api/v1/quotes/focus            → Frases motivacionales
```

**Response Example:**
```json
{
  "success": true,
  "data": {
    "id": 1,
    "text": "¿Es urgente o puede esperar?",
    "category": "reflection"
  }
}
```

## 🔧 Cómo Usar

### 1. Iniciar el Servidor Backend

```bash
cd /home/sanmiguel/AndroidStudioProjects/ZenApp/api
npm install  # Primera vez solamente
npm start
```

El servidor estará corriendo en `http://localhost:3000`

### 2. Ejecutar la App Android

1. Abre el proyecto en Android Studio
2. Ejecuta la app en un emulador o dispositivo
3. La app automáticamente:
   - Carga configuración del servidor al iniciar
   - Guarda cambios en el servidor cuando modificas ajustes
   - Muestra contenido dinámico (frases, tareas, quotes)

### 3. Estados de la UI

La interfaz muestra automáticamente:

- **Loading:** `CircularProgressIndicator` al cargar datos iniciales
- **Syncing:** Indicador en botón "Sincronizando..." al guardar
- **Error:** Card roja con mensaje de error si falla la conexión
- **Success:** Check verde "✓ Guardado" cuando se sincroniza correctamente

## 📁 Estructura del Código

```
ZenApp/
├── api/                              # Backend Express
│   ├── server.js                    # Servidor principal
│   ├── package.json                 # Dependencias Node
│   └── README.md                    # Docs del API
│
└── app/src/main/java/com/example/zenapp/
    ├── data/
    │   ├── remote/
    │   │   ├── dto/
    │   │   │   └── ApiModels.kt     # DTOs con @JsonClass
    │   │   ├── ZenAppApi.kt         # Interface Retrofit
    │   │   └── RetrofitInstance.kt  # Singleton Retrofit
    │   └── repository/
    │       └── ZenAppRepository.kt  # Capa de datos + Result
    │
    ├── util/
    │   └── UserIdProvider.kt        # Provider Android ID
    │
    └── ui/
        └── blocklist/
            ├── BlocklistViewModel.kt # ViewModel con sync
            └── BlocklistScreen.kt    # UI con loading states
```

## 🎯 Flujo de Sincronización

### Al Abrir la App
```
1. BlocklistViewModel.init()
   ↓
2. loadSettingsFromServer()
   ↓
3. ZenAppRepository.getUserSettings(userId)
   ↓
4. Retrofit HTTP GET /api/v1/users/{userId}/settings
   ↓
5. Servidor retorna settings o crea nuevo usuario
   ↓
6. ViewModel actualiza UI state con datos del servidor
   ↓
7. loadDynamicContent() carga frases/tareas en paralelo
```

### Al Guardar Cambios
```
1. Usuario toca "Guardar Configuración"
   ↓
2. viewModel.saveSettings()
   ↓
3. syncSettingsToServer() (auto-llamado)
   ↓
4. ZenAppRepository.saveUserSettings(userId, dto)
   ↓
5. Retrofit HTTP POST /api/v1/users/{userId}/settings
   ↓
6. Servidor actualiza Map en memoria
   ↓
7. ViewModel muestra "✓ Guardado"
```

## 🛠️ Manejo de Errores

### Network Error
```kotlin
when (result) {
    is Result.Success -> // Datos recibidos
    is Result.Error -> {
        // Muestra: "No se pudo conectar con el servidor"
        errorMessage = result.message
    }
    is Result.Loading -> // isLoading = true
}
```

### Servidor No Disponible
- La app funciona en modo offline
- Muestra mensaje de error temporal
- Usuario puede cerrar error con botón "OK"

### Timeout
- OkHttp timeout configurado a 10 segundos
- Retry manual con botón refresh

## 🔐 Privacidad

- **Identificación:** Android ID único por dispositivo (no requiere login)
- **Almacenamiento:** In-memory (se pierde al reiniciar servidor)
- **Datos enviados:** Solo configuración de apps bloqueadas
- **Sin analytics:** No se recopilan datos de uso real

## 📝 Próximas Mejoras

- [ ] Persistencia con base de datos (MongoDB/PostgreSQL)
- [ ] Autenticación con Firebase Auth
- [ ] Sync multi-dispositivo por cuenta de usuario
- [ ] Offline-first con Room + WorkManager
- [ ] Push notifications para recordatorios
- [ ] Dashboard web para ver estadísticas

## 🐛 Troubleshooting

### "No se pudo conectar con el servidor"
1. Verifica que el servidor esté corriendo: `npm start` en `/api`
2. Confirma que el emulador usa `10.0.2.2` (localhost del host)
3. Revisa logs del servidor en consola
4. Usa `adb logcat` para ver errores Android

### "Error: ECONNREFUSED"
- El servidor no está iniciado
- Puerto 3000 ocupado por otra app

### Datos no se guardan
- Revisa RetrofitInstance que tenga la URL correcta
- Verifica permisos INTERNET en AndroidManifest.xml
- Chequea logs de OkHttp con nivel DEBUG

## 📚 Referencias

- [Retrofit Documentation](https://square.github.io/retrofit/)
- [Moshi JSON Library](https://github.com/square/moshi)
- [Express.js Guide](https://expressjs.com/)
- [Android ViewModel](https://developer.android.com/topic/libraries/architecture/viewmodel)
