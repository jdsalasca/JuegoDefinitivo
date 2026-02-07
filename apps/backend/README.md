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
- `POST /api/auth/login` body: `{ "username": "...", "password": "..." }`
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
- `GET /api/teacher/classrooms/{classroomId}/dashboard?from=YYYY-MM-DD&to=YYYY-MM-DD`
- `GET /api/teacher/classrooms/{classroomId}/report.csv?from=YYYY-MM-DD&to=YYYY-MM-DD`

### Seguridad API (P0)
- Recomendado: `Authorization: Bearer <accessToken>` emitido por `/api/auth/login`.
- Compatibilidad legacy opcional: `X-Api-Token` (controlado por `app.security.allow-legacy-token`).
- Tokens por rol configurables en `application.properties`:
  - `app.security.student-token`
  - `app.security.teacher-token`
  - `app.security.admin-token`
- Credenciales por rol para login:
  - `app.security.student-username` / `app.security.student-password`
  - `app.security.teacher-username` / `app.security.teacher-password`
  - `app.security.admin-username` / `app.security.admin-password`
- Firma y expiracion del token bearer:
  - `app.security.jwt-secret`
  - `app.security.jwt-previous-secret` (ventana de rotacion)
  - `app.security.jwt-ttl-seconds`
  - `app.security.allow-legacy-token`
- Rate limit basico configurable:
  - `app.rate-limit.window-seconds`
  - `app.rate-limit.max-requests`
  - `app.rate-limit.teacher-max-requests`

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
- Vinculacion de intentos docente protegida contra duplicados (`student_id + assignment_id + session_id`).
- Dashboard docente incluye tiempo efectivo y abandono por actividad real.
- Default local con H2 file DB; PostgreSQL habilitado por variables de entorno Spring datasource.
- Importacion de libros restringida a `.txt` y `.pdf` con limite configurable (`app.import.max-bytes`, default 25MB).
