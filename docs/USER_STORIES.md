# User Stories - Producto Colaborativo de Lectura Didactica

## Objetivo
Construir una plataforma con experiencia limpia por rol:
- `Jugador`: leer, jugar y aprender.
- `Admin Docente`: crear aula y monitorear progreso.
- `Debug Interno`: inspeccionar calidad tecnica y narrativa.

## Historias MVP - Jugador
1. Como estudiante, quiero iniciar una partida desde un libro para convertir la lectura en una aventura.
   Criterio de aceptacion: puedo importar/seleccionar libro, iniciar sesion y ver escena inicial.
2. Como estudiante, quiero elegir acciones manuales para participar activamente en la lectura.
   Criterio de aceptacion: puedo ejecutar `Dialogar`, `Explorar`, `Resolver reto` y `Usar item`.
3. Como estudiante, quiero usar modo auto pedagogico por edad/nivel para recibir apoyo adaptativo.
   Criterio de aceptacion: configuro edad, nivel y pasos; la partida avanza y actualiza progreso.
4. Como estudiante, quiero retomar mi sesion para no perder avance.
   Criterio de aceptacion: puedo cargar una sesion por `sessionId` y continuar.

## Historias MVP - Admin Docente
1. Como docente, quiero crear aulas y registrar estudiantes para organizar mi clase.
   Criterio de aceptacion: puedo crear aula y agregar estudiantes desde la interfaz admin.
2. Como docente, quiero crear asignaciones de lectura por aula para planificar actividades.
   Criterio de aceptacion: puedo crear asignaciones con libro y verlas listadas.
3. Como docente, quiero vincular un intento de juego a estudiante/asignacion para tener evidencia.
   Criterio de aceptacion: puedo asociar una sesion activa con estudiante y asignacion.
4. Como docente, quiero ver un dashboard resumido para monitorear progreso.
   Criterio de aceptacion: veo estudiantes, progreso promedio y score promedio por aula.
5. Como docente, quiero exportar CSV para trabajo institucional.
   Criterio de aceptacion: puedo descargar `report.csv` del aula activa.

## Historias MVP - Debug Interno
1. Como equipo de producto/calidad, quiero cargar una sesion por id para inspeccionarla.
   Criterio de aceptacion: puedo cargar estado con `sessionId` en interfaz debug.
2. Como equipo tecnico, quiero ver telemetria por evento/etapa para detectar friccion UX.
   Criterio de aceptacion: visualizo contadores por evento y por stage.
3. Como equipo de calidad narrativa, quiero inspeccionar memoria y relaciones de entidades.
   Criterio de aceptacion: visualizo memoria narrativa y enlaces del grafo por sesion.

## Priorizacion de construccion
1. Fase 1: interfaz `Jugador` limpia + flujo completo de lectura/juego.
2. Fase 2: interfaz `Admin Docente` limpia + evidencia y monitoreo.
3. Fase 3: interfaz `Debug` interna + herramientas de observabilidad narrativa.

## Fuera de MVP inmediato
- Internacionalizacion completa (`es/en/pt`).
- Reporte PDF docente.
- Integraciones LMS/LTI.
- Instalador `.exe` firmado.
