# Next Steps (Prioritized)

0. Ya implementado en esta iteracion
- E2E browser tests con Playwright.
- Modo auto pedagogico configurable por edad/nivel.
- Telemetria UX basica con resumen por evento/etapa.
- Pipeline de release con artefacto desktop + `latest.json`.

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
- Frontend tests unitarios (Vitest + Testing Library).
- Publicar imagen Docker para backend.

6. Desktop distribucion
- Instalador `.exe` oficial cuando WiX este disponible en runner.
- Activar firma obligatoria de binarios en release.
