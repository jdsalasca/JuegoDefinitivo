# Next Steps (Prioritized)

1. Persistencia productiva
- Migrar sesiones/inventario a PostgreSQL con Flyway.
- Agregar migraciones versionadas y estrategia de respaldo.

2. Seguridad y robustez
- Limitar tamano y tipo de archivos importados.
- Validar rutas y endurecer manejo de errores.
- Agregar rate limiting basico por IP/sesion.

3. Calidad de narrativa
- Mejorar extraccion semantica de personajes/lugares.
- Añadir continuidad narrativa entre escenas.
- Incorporar niveles de dificultad por edad.

4. Experiencia docente
- Dashboard de progreso por estudiante.
- Metricas de comprension (aciertos, tiempo, abandono).
- Export de reportes CSV.

5. Plataforma
- Frontend tests (Vitest + Testing Library).
- E2E browser tests (Playwright).
- Publicar imagen Docker para backend.

6. Desktop distribucion
- Pipeline CI para empaquetado multiplataforma.
- Instalador `.exe` oficial cuando WiX este disponible en runner.
- Firma de binarios para confianza del usuario.
