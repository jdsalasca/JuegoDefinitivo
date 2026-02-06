# AutoBook Frontend

Interfaz React + TypeScript para jugar historias del backend.

## Setup
```bash
cd apps/frontend
cp .env.example .env
npm install
npm run dev
```

## Scripts
- `npm run dev`
- `npm run build`
- `npm run preview`

## Flujo
1. Importa libro por ruta (incluye `file:///...pdf`).
2. Inicia partida seleccionando libro.
3. Ejecuta acciones por escena (`TALK`, `EXPLORE`, `CHALLENGE`, `USE_ITEM`).
4. Continua con `sessionId`.
