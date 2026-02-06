# GitFlow del proyecto

## Ramas
- `main`: produccion. Solo recibe merges de release o hotfix.
- `develop`: integracion continua. Todas las features se integran aqui primero.
- `feature/*`: trabajo incremental por funcionalidad.
- `hotfix/*`: correcciones urgentes partiendo de `main`.
- `release/*`: estabilizacion de version antes de publicar en `main`.

## Flujo recomendado
1. Crear feature desde `develop`.
2. Abrir PR `feature/* -> develop`.
3. Validar CI completo (backend + frontend + E2E).
4. Para versionar: crear `release/x.y.z` desde `develop`.
5. PR `release/* -> main`, merge y tag `vX.Y.Z`.
6. Ejecutar workflow `release` para publicar artefactos desktop y `latest.json`.
7. Back-merge de `main` hacia `develop` si hubo ajustes de release.

## Convencion de push
- Push de trabajo diario: `develop` y `feature/*`.
- Push directo a `main`: no permitido salvo mantenimiento de release/hotfix.

