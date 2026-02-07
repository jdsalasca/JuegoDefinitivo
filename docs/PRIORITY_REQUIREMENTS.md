# Priority Requirements (Internal Review Baseline)

## Purpose
Definir que implementar primero para maximizar valor comunitario (estudiantes, docentes e instituciones) sin dispersar foco.

## Review Cadence
- Semanal: revision de prioridades por `Student Agent`, `Teacher Agent`, `Quality Agent`, `Delivery Agent`.
- Regla de entrada: toda iniciativa nueva debe mapear a `R1..R5` de `docs/MARKET_VALUE_REQUIREMENTS.md`.
- Regla de salida: no pasa a implementacion si no tiene criterio de aceptacion medible.

## Current Priority Order
1. Seguridad institucional minima (`R5`) [P0]:
   - Autenticacion/roles (`teacher`, `student`, `admin`).
   - Seguridad de importacion de archivos (MIME/firma, limites, saneamiento).
   - Endurecimiento de errores y controles anti-abuso (rate limiting).
2. Evidence Pack docente avanzado (`R2`) [P1]:
   - Tiempo efectivo de lectura.
   - Abandono por actividad.
   - Segmentacion por cohorte y periodo.
3. Calidad pedagogica (`R3`) [P1]:
   - Mejorar banco de preguntas por nivel lector/habilidad.
   - Mantener benchmark de calidad en CI como gate obligatorio.
4. Accesibilidad estudiantil (`R1` + `R3`) [P1]:
   - Alto contraste y modo lectura amigable.
   - Narracion asistida (TTS) y ayudas de lectura.
5. Operacion de plataforma (`R5`) [P2]:
   - Observabilidad (logs estructurados y metricas).
   - Backup/restore y retencion.
   - Contratos OpenAPI para integracion segura.

## Acceptance Gates (must pass)
1. Backend `mvn test` en verde.
2. Frontend `npm run test` y `npm run build` en verde.
3. E2E base `npm run test:e2e` en verde.
4. Criterios de mercado trazados en `docs/MARKET_VALUE_REQUIREMENTS.md`.

## Active Delivery Slice (status)
1. Hardening de import de libros. (implementado base)
2. Anti-duplicados y consistencia de evidencia docente. (implementado)
3. UX infantil accesible (lectura amigable + alto contraste). (implementado)
4. Auth por roles y rate limiting basico. (implementado base)
5. Segmentacion temporal docente (`from`/`to`) en dashboard y CSV. (implementado base)
6. Pendiente inmediato: metrica de tiempo efectivo y abandono por actividad real. (siguiente P1)
