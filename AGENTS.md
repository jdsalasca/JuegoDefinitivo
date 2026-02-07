# AGENTS

## Objetivo del repositorio
Mantener y evolucionar una plataforma educativa que transforma libros en aventuras jugables via API web y frontend interactivo.

## Agentes de producto (enfoque mercado)
- `Student Agent`: prioriza engagement, claridad de juego y progresion pedagogica.
- `Teacher Agent`: prioriza evidencia, seguimiento por estudiante y exportabilidad.
- `Quality Agent`: protege coherencia narrativa y calidad de preguntas mediante benchmarks.
- `Delivery Agent`: mantiene GitFlow, CI verde y releases reproducibles.

## Convenciones de trabajo
- Base tecnica: backend Java 17 + Maven, frontend React + TypeScript.
- Pruebas: cobertura automatizada para cambios de comportamiento.
- Arquitectura por capas backend: `domain`, `engine`, `persistence`, `service`, `api`.
- Estructura monorepo: `apps/backend`, `apps/frontend`, `docs`, `scripts`.
- No mezclar entrada/salida con reglas de dominio.
- Priorizacion vigente y criterios de revision interna: `docs/PRIORITY_REQUIREMENTS.md`.

## Flujo de ramas
- `main`: produccion.
- `develop`: integracion.
- `feature/*`: nuevas funcionalidades.
- `hotfix/*`: correcciones urgentes.

## Definicion de terminado
- Backend `mvn test` en verde.
- Frontend `npm run test` en verde.
- Frontend `npm run build` en verde.
- CI sin fallos.
- Documentacion actualizada para cambios de UX/arquitectura.
- Requisitos de mercado trazados en `docs/MARKET_VALUE_REQUIREMENTS.md`.

## Checklist de PR
- Cobertura de tests para comportamiento nuevo.
- Mensajes de commit claros y atomicos.
- Sin artefactos de build ni archivos temporales versionados.
