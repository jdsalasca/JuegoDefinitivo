# Release Notes v3.5.0

Fecha: 2026-02-07

## Enfoque del release
- Cerrar P0 de seguridad institucional con estrategia realista de migracion.
- Cerrar P1 de evidencia docente con metricas accionables para aula.

## Cambios principales
1. Seguridad API reforzada (P0)
- Login bearer por rol consolidado (`/api/auth/login`).
- Tokens JWT con expiracion y soporte de rotacion de secreto:
  - `app.security.jwt-secret` (activo)
  - `app.security.jwt-previous-secret` (anterior para ventana de migracion)
- Control explicito de compatibilidad legacy:
  - `app.security.allow-legacy-token=true|false`
  - Permite retirar `X-Api-Token` por entorno productivo sin romper local/dev.

2. Evidence Pack docente avanzado (P1)
- Dashboard docente incluye:
  - `totalEffectiveReadingMinutes`
  - `averageEffectiveMinutesPerAttempt`
  - `abandonmentByActivity` (actividad real de intento activo)
- Progreso por estudiante incluye:
  - `averageEffectiveMinutes`
- CSV docente ampliado con:
  - columna `avg_effective_minutes`
  - bloque `summary_metric`
  - bloque `abandonment_activity`

3. UI Admin pulida para operacion
- Filtros de periodo (`Desde`/`Hasta`) aplicados al dashboard.
- Descarga CSV con el mismo rango temporal.
- Panel de autenticacion API en interfaces admin/debug para operar con bearer.

## Calidad y validacion
- Backend: `mvn test` verde (37 tests).
- Frontend: `npm run test`, `npm run build`, `npm run test:e2e` en verde.

## Notas de migracion
1. Produccion recomendada:
- Definir `app.security.jwt-secret` robusto.
- Definir `app.security.jwt-previous-secret` temporal durante rotacion.
- Cambiar clientes a bearer.
- Cambiar `app.security.allow-legacy-token=false` una vez migrados.

2. Compatibilidad:
- `X-Api-Token` sigue disponible si `allow-legacy-token=true`.

