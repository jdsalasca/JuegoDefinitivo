# Contributing

Gracias por contribuir.

## Workflow
1. Crear rama desde `develop`: `feature/<nombre>` o `hotfix/<nombre>`.
2. Implementar cambios pequenos y atomicos.
3. Ejecutar validaciones locales:
   - Backend: `cd apps/backend && mvn test`
   - Frontend: `cd apps/frontend && npm install && npm run build`
4. Actualizar docs si cambia flujo o arquitectura.
5. Abrir PR contra `develop` con resumen tecnico y evidencia de pruebas.

## Code Standards
- Java 17 en backend.
- React + TypeScript en frontend.
- Evitar mezclar UI con logica de dominio.
- Agregar tests para cambios de comportamiento.

## PR Checklist
- [ ] Tests en verde.
- [ ] Sin artefactos de build versionados.
- [ ] README/docs actualizados si aplica.
- [ ] Cambios enfocados en una sola responsabilidad.
