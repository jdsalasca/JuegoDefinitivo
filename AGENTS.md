# AGENTS

## Objetivo del repositorio
Mantener y evolucionar un motor educativo que transforma libros de texto en aventuras jugables por consola.

## Convenciones de trabajo
- Base tecnica: backend Java 17 + Maven, frontend React + TypeScript.
- Pruebas: JUnit 5 obligatorias para cambios en logica de negocio.
- Arquitectura por capas backend: `domain`, `engine`, `persistence`, `ui/api`.
- Estructura monorepo: `apps/backend`, `apps/frontend`, `docs`.
- No mezclar logica de entrada/salida con reglas del juego.

## Flujo de ramas
- `main`: produccion.
- `develop`: integracion.
- `feature/*`: nuevas funcionalidades.
- `hotfix/*`: correcciones urgentes.

## Definicion de terminado
- `mvn test` en verde.
- README actualizado si cambia UX/comandos/estructura.
- Sin romper compatibilidad del guardado o con migracion documentada.

## Checklist de PR
- Cobertura de tests para comportamiento nuevo.
- Mensajes de commit claros y atomicos.
- Sin archivos temporales ni artefactos de build.
