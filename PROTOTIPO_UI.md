# ZenApp - Prototipo de Interfaz

## Descripción
ZenApp es una aplicación de concentración que ayuda a los usuarios a bloquear apps distractoras con configuraciones personalizables.

## Estructura del Proyecto

### 📱 Vistas Principales

#### 1. Vista de Bloqueos (BlocklistFragment)
**Ubicación:** `ui/blocklist/BlocklistFragment.kt`

**Componentes:**
- ✅ **Selector de Apps**: Card clickeable que muestra el conteo de apps seleccionadas (ej: "5 apps")
- ✅ **Botón de Selección**: Al hacer clic abre la Activity de selección de apps
- ✅ **Pausar por**: Spinner con opciones de tiempo (5s, 10s, 30s, 1min, 5min, 10min)
- ✅ **Con el mensaje**: EditText para mensaje personalizado
- ✅ **Objetivo diario**: 
  - Spinner de aperturas por día (1, 2, 3, 5, 10, Ilimitado)
  - Spinner de duración de sesión (5min, 10min, 15min, 30min, 1hora)
- ✅ **Botón Guardar**: Guarda la configuración y muestra toast con confirmación

**Archivos relacionados:**
- `fragment_blocklist.xml` - Layout principal
- `BlocklistViewModel.kt` - ViewModel
- `AppItem.kt` - Data class para las apps (con categoría)

#### 2. Activity de Selección de Apps (AppSelectionActivity)
**Ubicación:** `ui/appselection/AppSelectionActivity.kt`

**Componentes:**
- ✅ **Toolbar**: Con botón de retroceso
- ✅ **SearchBar**: Barra de búsqueda para filtrar apps en tiempo real
- ✅ **Contador de Selección**: Muestra cuántas apps están seleccionadas
- ✅ **Lista por Categorías**: RecyclerView con apps organizadas en:
  - 🌐 Redes Sociales (Instagram, Facebook, Twitter, WhatsApp, Telegram, Snapchat)
  - 🎮 Juegos (Candy Crush, PUBG Mobile, Clash Royale, Among Us, Minecraft)
  - 🎬 Entretenimiento (TikTok, YouTube, Netflix, Spotify, Twitch)
  - 💼 Productividad (Chrome, Gmail)
  - 📱 Otras (Reddit, Pinterest)
- ✅ **Botón Seleccionar/Deseleccionar Todo**: Alterna la selección de todas las apps
- ✅ **Botón Confirmar**: Guarda la selección y retorna al fragment principal

**Archivos relacionados:**
- `activity_app_selection.xml` - Layout principal
- `item_category.xml` - Layout de cada categoría
- `item_app.xml` - Layout de cada app
- `AppSelectionActivity.kt` - Activity principal
- `AppSelectionAdapter.kt` - Adaptador para apps individuales
- `CategoryAdapter.kt` - Adaptador para categorías
- `AppCategory.kt` - Enum de categorías

#### 3. Vista de Ajustes (SettingsFragment)
**Ubicación:** `ui/settings/SettingsFragment.kt`

**Componentes:**
- ✅ **Deshabilitar rápido**: Switch para desactivar todos los bloqueos temporalmente
- ✅ **Pausar bloqueos**:
  - Spinner con duraciones (5min, 15min, 30min, 1h, 2h, 4h)
  - Botón "Pausar Ahora"
  - Card de estado de pausa (se muestra cuando está pausado)
  - Botón "Cancelar Pausa"
- ✅ **Estadísticas del día**: Card con contadores mockeados (12 bloqueos, 45m ahorrado)

**Archivos relacionados:**
- `fragment_settings.xml` - Layout principal
- `SettingsViewModel.kt` - ViewModel

### 🎨 Recursos

#### Layouts
- `fragment_blocklist.xml` - Layout de la vista principal
- `fragment_settings.xml` - Layout de ajustes
- `activity_app_selection.xml` - Layout de selección de apps
- `item_app.xml` - Item de lista de apps
- `item_category.xml` - Header de categoría con RecyclerView
- `activity_main.xml` - Activity principal con BottomNavigationView

#### Navegación
- `mobile_navigation.xml` - Grafo de navegación (2 destinos)
- `bottom_nav_menu.xml` - Menú de navegación inferior (2 items)

#### Strings
- `title_blocklist`: "Bloqueos"
- `title_settings`: "Ajustes"
- `app_name`: "Zen App"
- `select_apps`: "Seleccionar Apps"

### 📂 Estructura de Paquetes

```
com.example.zenapp
└── ui
    ├── blocklist
    │   ├── BlocklistFragment.kt
    │   ├── BlocklistViewModel.kt
    │   └── AppItem.kt (con categoría)
    ├── appselection (NUEVO)
    │   ├── AppSelectionActivity.kt
    │   ├── AppSelectionAdapter.kt
    │   ├── CategoryAdapter.kt
    │   └── AppCategory.kt
    └── settings
        ├── SettingsFragment.kt
        └── SettingsViewModel.kt
```

## 🎯 Estado Actual

### ✅ Implementado
- [x] Estructura de navegación con 2 vistas principales
- [x] Vista de Bloqueos con selector de apps mejorado
- [x] **Activity separada para selección de apps**
- [x] **Barra de búsqueda funcional en tiempo real**
- [x] **Apps organizadas por categorías (Social, Games, Entretenimiento, etc.)**
- [x] **Botón con contador de apps seleccionadas**
- [x] Vista de Ajustes con controles rápidos
- [x] Lista de 20 apps mockeadas con categorías
- [x] Spinners configurados con valores predeterminados
- [x] Mensajes de confirmación (Toasts)
- [x] Layout responsive con ScrollView
- [x] CardViews para mejor organización visual
- [x] Material Design Components
- [x] ActivityResultLauncher para comunicación entre Activity y Fragment

### 🆕 Nuevo en esta versión
- **AppSelectionActivity**: Activity separada con diseño limpio y Material Toolbar
- **Búsqueda en tiempo real**: Filtra apps mientras escribes
- **Categorización**: Apps organizadas en 5 categorías claras
- **Selección múltiple**: Botón para seleccionar/deseleccionar todo
- **Contador visual**: Muestra cantidad de apps seleccionadas en ambas pantallas
- **Navegación mejorada**: Botón de retroceso y confirmación explícita

### ⏳ Próximos Pasos (No implementado - solo prototipo)
- [ ] Funcionalidad real de bloqueo de apps
- [ ] Overlay/Dialog de espera cuando se abre una app bloqueada
- [ ] Contador de tiempo en el overlay
- [ ] Persistencia de datos (SharedPreferences/Room)
- [ ] Sistema de permisos (UsageStatsManager)
- [ ] Detección de apertura de apps
- [ ] Temporizador real para pausas
- [ ] Estadísticas reales
- [ ] Iconos reales de las apps instaladas

## 🚀 Cómo Probar

1. Compila el proyecto en Android Studio
2. Ejecuta en un emulador o dispositivo físico
3. **En la vista "Bloqueos":**
   - Toca el card "0 apps" para abrir el selector
   - Busca apps usando la barra de búsqueda
   - Selecciona/deselecciona apps por categoría
   - Usa "Seleccionar Todo" o "Deseleccionar Todo"
   - Presiona "Confirmar" para guardar
4. **De vuelta en "Bloqueos":**
   - Verás el contador actualizado (ej: "5 apps")
   - Configura spinners y mensaje personalizado
   - Presiona "Guardar Configuración"
5. **En "Ajustes":**
   - Activa/desactiva el switch
   - Pausa los bloqueos temporalmente

## 📝 Notas Técnicas

- **ViewBinding**: Habilitado para acceso seguro a las vistas
- **RecyclerView anidados**: CategoryAdapter contiene AppSelectionAdapter
- **Material Components**: TextInputLayout, MaterialToolbar, CardView
- **Navigation Component**: Para navegación entre fragmentos
- **ActivityResultLauncher**: Para comunicación Activity ↔ Fragment
- **BottomNavigationView**: Para menú de navegación principal
- **TextWatcher**: Para búsqueda en tiempo real
- **Serializable**: Para pasar objetos entre Activities (AppItem)

## 🎨 Características de UI

- Diseño limpio con espaciado consistente (16dp padding)
- Material Toolbar con tema oscuro en AppSelectionActivity
- SearchBar con iconos de búsqueda y clear
- Cards con elevación para separar secciones
- Categorías con headers visuales
- Colores del tema Material Design
- Iconos placeholder (usar ic_launcher temporalmente)
- Layout adaptable con ScrollView
- Feedback visual con Toasts
- Contador en tiempo real de apps seleccionadas


