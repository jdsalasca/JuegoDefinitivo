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

## Arquitectura
- `domain`: entidades de juego.
- `engine`: reglas de combate/progreso/retos.
- `ingest`: importacion y lectura TXT/PDF.
- `narrative`: construccion de escenas.
- `service`: orquestacion de sesiones.
- `api`: controladores y DTOs.

## Notas
- CORS permitido para `http://localhost:5173`.
- El frontend empaquetado se copia a `src/main/resources/static` solo durante build desktop.
