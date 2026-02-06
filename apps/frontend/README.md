# AutoBook Frontend

Frontend React + TypeScript para jugar historias del backend.

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

## Variables
- `VITE_API_BASE` (default: `http://localhost:8080/api`)

## Flujo UX
1. Importar libro (`file:///...pdf` o ruta local).
2. Seleccionar libro y jugador.
3. Iniciar partida.
4. Ejecutar acciones por escena (`TALK`, `EXPLORE`, `CHALLENGE`, `USE_ITEM`).
5. Continuar con `sessionId`.
