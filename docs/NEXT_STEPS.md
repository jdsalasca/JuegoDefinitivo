# Next Steps (Prioritized)

0. Ya implementado en esta iteracion
- E2E browser tests con Playwright.
- Modo auto pedagogico configurable por edad/nivel.
- Telemetria UX basica con resumen por evento/etapa.
- Pipeline de release con artefacto desktop + `latest.json`.
- Pipeline base de normalizacion de texto (headers/footers repetidos, numeros de pagina, guiones de corte).
- Inicio de separacion de experiencia por interfaz (`Jugador`, `Admin Docente`, `Debug`).
- Historias de usuario iniciales en `docs/USER_STORIES.md`.
- Navegacion por rutas por interfaz (`/`, `/admin`, `/debug`).
- Suite unitaria frontend inicial con Vitest.
- Persistencia runtime de sesiones de juego en DB (`game_sessions`) con Flyway.
- Modo lectura amigable para ninos (tipografia y controles ampliados en interfaz jugador).
- Modo de alto contraste para accesibilidad en interfaz jugador.
- Prevencion de intentos duplicados en espacio docente para mantener metricas confiables.
- Hardening base de importacion: validacion de firma PDF y control de contenido binario en TXT.

1. Persistencia productiva
- Agregar estrategia de backup/restore y retencion.

2. Seguridad y robustez
- Endurecer validaciones de import (MIME real + escaneo anti-archivo malicioso). (avance parcial: firma/contenido base)
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
