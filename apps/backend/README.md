# AutoBook Backend API

Backend Spring Boot que convierte libros (`.txt` / `.pdf`) en aventuras jugables.

## Ejecutar
```bash
cd apps/backend
mvn test
mvn spring-boot:run
```

## API
- `GET /api/health`
- `GET /api/books`
- `POST /api/books/import` body: `{ "path": "file:///C:/.../libro.pdf" }`
- `POST /api/game/start` body: `{ "playerName": "Juan", "bookPath": "C:/.../libro.pdf" }`
- `GET /api/game/{sessionId}`
- `POST /api/game/{sessionId}/action` body: `{ "action": "TALK|EXPLORE|CHALLENGE|USE_ITEM", "answerIndex": 1, "itemId": "potion_small" }`
- `POST /api/game/{sessionId}/autoplay` body: `{ "ageBand": "9-12", "readingLevel": "intermediate", "maxSteps": 3 }`
- `GET /api/game/{sessionId}/graph`
- `POST /api/telemetry/events`
- `GET /api/telemetry/summary`
- `GET /api/teacher/classrooms`
- `POST /api/teacher/classrooms`
- `POST /api/teacher/classrooms/{classroomId}/students`
- `GET /api/teacher/classrooms/{classroomId}/students`
- `POST /api/teacher/classrooms/{classroomId}/assignments`
- `GET /api/teacher/classrooms/{classroomId}/assignments`
- `POST /api/teacher/attempts/link`
- `GET /api/teacher/classrooms/{classroomId}/dashboard`
- `GET /api/teacher/classrooms/{classroomId}/report.csv`

## Arquitectura
- `domain`: entidades de juego.
- `engine`: reglas de combate/progreso/retos.
- `ingest`: importacion y lectura TXT/PDF.
- `narrative`: construccion de escenas.
- `service`: orquestacion de sesiones.
- `api`: controladores y DTOs.
- `docs/LEARNING_TRACK_TODO.md`: plan de mejora pedagogica por fases.

## Notas
- CORS permitido para `http://localhost:5173`.
- El frontend empaquetado se copia a `src/main/resources/static` solo durante build desktop.
- El pipeline narrativo incluye normalizacion de texto, memoria de entidades, grafo de relaciones y nivel cognitivo por escena.
- Persistencia docente sobre JDBC + Flyway (`classrooms`, `students`, `assignments`, `attempts`).
- Persistencia runtime de sesiones de juego sobre JDBC + Flyway (`game_sessions`).
- Default local con H2 file DB; PostgreSQL habilitado por variables de entorno Spring datasource.
- Importacion de libros restringida a `.txt` y `.pdf` con limite configurable (`app.import.max-bytes`, default 25MB).
