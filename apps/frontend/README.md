# AutoBook Frontend

Frontend React + TypeScript para jugar historias del backend.

## Interfaces por rol
- `Jugador`: ruta `/` (o `/player`)
- `Admin Docente`: ruta `/admin`
- `Debug`: ruta `/debug`

La seleccion de interfaz desde la barra superior actualiza la URL.

## Modo lectura amigable
- Disponible en interfaz `Jugador`.
- Amplia tipografia, controles y contraste para lectura infantil.
- Se guarda preferencia local en `autobook:kidMode`.

## Setup
```bash
cd apps/frontend
copy .env.example .env
npm install
npm run dev
```

## Build
```bash
npm run build
```

## Tests unitarios
```bash
npm run test
```

## Variables
- `VITE_API_BASE` (default: `http://localhost:8080/api`)

## Flujo UX guiado (v3)
1. `Preparar partida`: define jugador e importa libro (`file:///...pdf` o ruta local).
2. `Iniciar partida` o `Cargar sesion`: arranca desde libro seleccionado o reanuda por `sessionId`.
3. `Jugar`: avanza por escenas con acciones guiadas:
   - `Dialogar`
   - `Explorar`
   - `Resolver reto` (seleccion de respuesta)
   - `Usar item` (seleccion de inventario)
4. `Modo Auto Pedagogico`: ejecuta pasos automáticos configurando edad, nivel lector y pasos.
5. `Ruta sugerida`: checklist visual para saber si ya tienes libro, sesion y aula listos.

## Estado de sesion
- El frontend guarda el ultimo `sessionId` en `localStorage` con la clave `autobook:lastSessionId`.
- Al recargar la pagina, ese `sessionId` aparece automaticamente en el campo de reanudacion.

## Telemetria UX
- El frontend reporta eventos de setup/juego al backend (`/api/telemetry/events`).
- Resumen visible en UI (`Telemetria UX`) y disponible por API (`/api/telemetry/summary`).

## Inteligencia narrativa en UI
- Dificultad adaptativa visible por sesion.
- Memoria narrativa (entidades mas frecuentes).
- Relaciones narrativas (grafo top de co-ocurrencias por sesion).

## Espacio docente MVP
- Crear aula y seleccionar aula activa.
- Agregar estudiantes.
- Crear asignaciones de lectura.
- Vincular la sesion de juego actual a estudiante + asignacion.
- Ver dashboard de progreso por estudiante.
- Descargar reporte CSV de aula.

## E2E
```bash
npm run test:e2e
```
