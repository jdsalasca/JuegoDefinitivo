# Release Notes v3.5.1

Fecha: 2026-02-07

## Resumen
Release de consolidacion sobre `v3.5.0` con foco en publicacion operativa:
- ejecutable desktop actualizado,
- descripcion de release clara para comunidad,
- README ampliado con direcciones por interfaz y modos de uso.

## Interfaces y rutas de uso
1. Jugador
- Web local: `http://localhost:5173/`
- Desktop: `http://localhost:8080/`
- Objetivo: lectura interactiva y progresion pedagogica.

2. Admin Docente
- Web local: `http://localhost:5173/admin`
- Desktop: `http://localhost:8080/admin`
- Objetivo: gestion de aula, evidencia y monitoreo.

3. Debug Interno
- Web local: `http://localhost:5173/debug`
- Desktop: `http://localhost:8080/debug`
- Objetivo: inspeccion tecnica, telemetria y calidad narrativa.

## Componentes de la release
- `AutoBookQuest-win64.zip` (portable con `AutoBookQuest.exe`)
- `latest.json` (manifiesto para auto-update)

## Capacidades principales incluidas
1. Seguridad P0
- Login bearer por rol (`/api/auth/login`).
- JWT con expiracion, rotacion de secreto y control de token legacy.

2. Evidence Pack docente P1
- Tiempo efectivo de lectura total y promedio.
- Abandono por actividad real.
- Filtros por periodo en dashboard y export CSV.

3. UX por interfaz
- Separacion clara `Jugador` / `Admin` / `Debug`.
- Panel admin/debug con login API bearer.

## Notas operativas
- API base local: `http://localhost:8080/api`
- Health: `http://localhost:8080/api/health`
- Dashboard docente API: `/api/teacher/classrooms/{classroomId}/dashboard`
- CSV docente API: `/api/teacher/classrooms/{classroomId}/report.csv`

