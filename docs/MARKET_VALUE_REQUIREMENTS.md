# Market Value Requirements (Students + Teachers)

## North Star
Convertir lectura en evidencia de aprendizaje accionable para aula.

## Requerimientos de alto valor

### R1. Modelo aula real (Teacher/Classroom/Student/Assignment)
- Crear clases y estudiantes.
- Asignar libros a una clase.
- Relacionar sesiones de juego con estudiante + asignacion.
- Estado: `MVP` requerido para adopcion inicial docente.

### R2. Evidence Pack docente
- Dashboard por clase con:
  - progreso promedio
  - puntaje promedio
  - precision de retos
  - dificultad adaptativa dominante
  - intentos activos/finalizados
- Export CSV por clase para uso fuera de plataforma.
- Estado: `MVP` requerido para ventas piloto.

### R3. Calidad pedagogica
- Preguntas por nivel cognitivo (literal/inferencial/critico).
- Dificultad adaptativa por desempeno.
- Benchmark de calidad narrativa en CI para evitar regresiones.
- Estado: implementado base, requiere refinamiento de banco de preguntas.

### R4. Trazabilidad narrativa
- Memoria narrativa por sesion.
- Grafo de entidades y relaciones por sesion.
- Exposicion API para inspeccion docente/analitica.
- Estado: implementado base.

### R5. Operatividad institucional
- Persistencia multiusuario en DB (PostgreSQL + Flyway).
- Autenticacion por rol (teacher/student/admin).
- Seguridad de importacion de archivos y limites.
- Estado: pendiente.

## Backlog ejecutable (prioridad)
1. Teacher Workspace MVP (R1 + R2).
2. Reportes docentes historicos por periodo (R2).
3. Banco de preguntas por habilidades lectoras (R3).
4. Persistencia en PostgreSQL y migraciones (R5).
5. Integraciones educativas (Google Classroom/LMS/LTI) (R5+).

## Criterios de aceptacion para piloto
- Docente puede crear clase y agregar al menos 20 estudiantes.
- Docente puede asignar un libro y ver progreso por estudiante.
- Reporte CSV descargable por clase con metricas de aprendizaje.
- 0 regresiones en benchmark narrativo y E2E basico.

