# AutoBook Backend API

Backend Spring Boot para convertir libros (`.txt` / `.pdf`) en aventuras jugables.

## Ejecutar
```bash
cd apps/backend
mvn test
mvn spring-boot:run
```

## Endpoints principales
- `GET /api/health`
- `GET /api/books`
- `POST /api/books/import` body: `{ "path": "file:///C:/.../libro.pdf" }`
- `POST /api/game/start` body: `{ "playerName": "Juan", "bookPath": "C:/.../libro.pdf" }`
- `GET /api/game/{sessionId}`
- `POST /api/game/{sessionId}/action` body: `{ "action": "TALK|EXPLORE|CHALLENGE|USE_ITEM", "answerIndex": 1, "itemId": "potion_small" }`

## Notas
- CORS habilitado para `http://localhost:5173`.
- Libros de muestra se copian automaticamente al catalogo.
