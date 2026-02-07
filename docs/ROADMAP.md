# Roadmap

## Completed
1. Foundation: monorepo (`apps/backend`, `apps/frontend`, `docs`, `scripts`).
2. Backend API: sesiones de juego, importacion de libros y acciones por escena.
3. Frontend React: flujo completo de importacion e interaccion jugable.
4. Desktop packaging: `jpackage` con fallback portable.
5. `v3.3`: memoria narrativa, preguntas cognitivas adaptativas y benchmark de calidad.
6. `v3.4`: persistencia docente en PostgreSQL/H2 con migraciones Flyway + migracion desde JSON legado.
7. `v3.5` (parcial): panel docente y analitica por estudiante con export CSV.
8. `v3.5.0`: seguridad API bearer con rotacion de secreto JWT y dashboard docente con tiempo efectivo + abandono por actividad.

## Next Milestones
1. `v3.6`: observabilidad de produccion y backup/restore.
2. `v3.6`: retiro definitivo de token legacy (`X-Api-Token`) en entornos productivos.
3. `v3.7`: mejora profunda de lectura de libros (OCR, tablas, encabezados complejos, chunking semantico).
