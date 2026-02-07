# Next Steps (Prioritized)

0. Ya implementado en esta iteracion
- E2E browser tests con Playwright.
- Modo auto pedagogico configurable por edad/nivel.
- Telemetria UX basica con resumen por evento/etapa.
- Pipeline de release con artefacto desktop + `latest.json`.
- Pipeline base de normalizacion de texto (headers/footers repetidos, numeros de pagina, guiones de corte).

1. Persistencia productiva
- Migrar sesiones/inventario runtime (partidas de juego) a PostgreSQL con Flyway.
- Agregar estrategia de backup/restore y retencion.

2. Seguridad y robustez
- Endurecer validaciones de import (MIME real + escaneo anti-archivo malicioso).
- Validar rutas y endurecer manejo de errores.
- Agregar rate limiting basico por IP/sesion.

3. Calidad de narrativa
- Mejorar extraccion semantica de personajes/lugares sobre texto ya normalizado.
- Añadir continuidad narrativa entre escenas.
- Incorporar niveles de dificultad por edad.

4. Experiencia docente
- Metricas de tiempo efectivo y abandono por actividad.
- Segmentacion por cohorte (curso, nivel lector, fecha).
- Reporte PDF ademas de CSV.

5. Plataforma
- Frontend tests unitarios (Vitest + Testing Library).
- Publicar imagen Docker para backend.

6. Desktop distribucion
- Instalador `.exe` oficial cuando WiX este disponible en runner.
- Activar firma obligatoria de binarios en release.
