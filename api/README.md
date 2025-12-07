# ZenApp API

API sencilla para sincronización de configuraciones y contenido dinámico de ZenApp.

## Instalación

```bash
cd api
npm install
```

## Ejecutar

```bash
npm start
```

Para desarrollo con auto-reload:

```bash
npm run dev
```

## Endpoints

### Settings
- `GET /api/v1/users/:userId/settings` - Obtener configuración
- `POST /api/v1/users/:userId/settings` - Guardar configuración
- `PUT /api/v1/users/:userId/settings` - Actualizar configuración

### Usage Backup
- `POST /api/v1/users/:userId/usage/backup` - Respaldar datos de uso

### Dynamic Content
- `GET /api/v1/mindful_prompts` - Pregunta de concientización aleatoria
- `GET /api/v1/mini_tasks/daily` - Tarea diaria aleatoria
- `GET /api/v1/quotes/focus` - Cita inspiradora aleatoria

### Health
- `GET /api/v1/health` - Estado del servidor

## Ejemplo de uso

```bash
# Guardar configuración
curl -X POST http://localhost:3000/api/v1/users/user123/settings \
  -H "Content-Type: application/json" \
  -d '{
    "selectedApps": ["com.instagram", "com.twitter"],
    "customMessage": "Respira profundo",
    "pauseTime": "60 segundos"
  }'

# Obtener prompt
curl http://localhost:3000/api/v1/mindful_prompts
```
