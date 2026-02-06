# AutoBook Adventure

Motor de consola que convierte cualquier libro `.txt` en una experiencia jugable orientada a aprendizaje infantil.

## Objetivo
Cargar un libro, fragmentarlo en escenas y generar decisiones automaticas con retos de comprension lectora.

## Caracteristicas
- Modo auto para cualquier `.txt`.
- Segmentacion inteligente por parrafos y por longitud.
- Preguntas educativas autogeneradas por escena.
- Sistema de stats (vida, conocimiento, coraje, enfoque, puntaje).
- Persistencia en archivo de guardado.
- Catalogo de libros local con muestras iniciales.

## Arquitectura
- `src/main/java/com/juegodefinitivo/autobook/app`: punto de entrada.
- `src/main/java/com/juegodefinitivo/autobook/config`: carga de `application.properties`.
- `src/main/java/com/juegodefinitivo/autobook/domain`: entidades de dominio.
- `src/main/java/com/juegodefinitivo/autobook/engine`: parser + logica de juego + preguntas.
- `src/main/java/com/juegodefinitivo/autobook/persistence`: guardado/carga de partida.
- `src/main/java/com/juegodefinitivo/autobook/ui`: UX de consola.
- `src/main/resources/application.properties`: configuracion.
- `src/main/resources/books`: libros de muestra.
- `legacy/2020-src`: codigo historico preservado.

## Requisitos
- Java 17+
- Maven 3.9+

## Comandos
```bash
mvn test
mvn exec:java
```

## UX de juego
1. Nueva partida.
2. Seleccion de libro detectado o ruta manual.
3. Juego por escenas con 3 acciones:
   - explorar,
   - avanzar con valentia,
   - estudiar escena con pregunta.
4. Guardado automatico por turno y opcion de guardar/salir.

## Persistencia
Archivo por defecto:
- `.autobook-data/savegame.properties`

Configurable desde:
- `src/main/resources/application.properties`

## Calidad
Tests en `src/test/java/com/juegodefinitivo/autobook`:
- parser de escenas,
- generacion de preguntas,
- repositorio de guardado,
- reglas del juego.

## Notas de migracion
El proyecto original de 2020-2021 no se elimino; se conservo en `legacy/2020-src`.
