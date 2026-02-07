# JuegoDefinitivo

Plataforma educativa open source que convierte libros en aventuras jugables para mejorar comprension lectora mediante juego.

## Mision
Democratizar el aprendizaje lector transformando contenido textual (TXT/PDF) en experiencias interactivas accesibles para ninos, familias y docentes.

## Vision
Ser una referencia abierta en tecnologia educativa hispanohablante para aprendizaje basado en narrativa interactiva, con impacto medible en comprension y motivacion.

## Monorepo
- `apps/backend`: API Spring Boot + motor de juego.
- `apps/frontend`: interfaz React + TypeScript.
- `docs`: arquitectura, desktop, roadmap y backlog.
- `scripts`: empaquetado desktop.

## GitFlow operativo
- Rama de integracion: `develop`.
- Rama de produccion: `main`.
- Features siempre por `feature/* -> develop`.
- Releases por `release/* -> main` con tag `vX.Y.Z`.
- Guia detallada: `docs/GITFLOW.md`.

## Inicio rapido local
```bash
# Terminal 1
cd apps/backend
mvn test
mvn spring-boot:run

# Terminal 2
cd apps/frontend
copy .env.example .env
npm install
npm run dev
```

Abrir: `http://localhost:5173`

## Persistencia (v3.4)
- Flyway ejecuta migraciones SQL en startup (`apps/backend/src/main/resources/db/migration`).
- Default local: H2 en archivo (`./.autobook-data/autobook-db`).
- Produccion: configurar PostgreSQL via `SPRING_DATASOURCE_URL`, `SPRING_DATASOURCE_USERNAME`, `SPRING_DATASOURCE_PASSWORD`.
- Si existe `teacher-workspace.json` legado, el backend migra su contenido a DB en el primer arranque y lo renombra a `teacher-workspace.migrated.json`.

## Uso con PDF real
1. En `1. Preparar partida`, usa `Importar libro` con ruta como `file:///C:/Users/<usuario>/Downloads/libro.pdf`.
2. Selecciona el libro importado y pulsa `2. Iniciar partida`.
3. En `3. Jugar`, avanza por escenas con `Dialogar`, `Explorar`, `Resolver reto` o `Usar item`.
4. Para continuar luego, usa `Cargar sesion` con el `sessionId` (tambien se recuerda automaticamente en el navegador).

## Release desktop
- Release publico actual: `v3.5.0`
- Incluye `AutoBookQuest-win64.zip` (portable con `AutoBookQuest.exe`).
- Para instalador `.exe` tipo setup con `jpackage`, se requiere WiX v3 instalado.
- El workflow `release` publica tambien `latest.json` para auto-update.
- Notas del release: `docs/RELEASE_NOTES_v3.5.0.md`.

## Calidad
- Backend: `mvn test`
- Frontend: `npm run build`
- CI en GitHub Actions para backend y frontend.

## Documentacion clave
- `docs/PROJECT_CHARTER.md`
- `docs/ROADMAP.md`
- `docs/NEXT_STEPS.md`
- `docs/LEARNING_TRACK_TODO.md`
- `docs/MARKET_VALUE_REQUIREMENTS.md`
- `docs/DESKTOP.md`
- `CONTRIBUTING.md`
- `CODE_OF_CONDUCT.md`
