# JuegoDefinitivo

Proyecto refactorizado a un motor de aventura educativa que convierte libros `.txt` en escenas jugables.

## Estado (Febrero 2026)
- Motor auto implementado en `Juego/Elcaballero`.
- Proyecto Java 17 con Maven y tests JUnit 5.
- Persistencia de partida y UX de consola para cargar libros y jugar.

## Estructura
- `Juego/Elcaballero`: aplicacion principal.
- `Juego/Elcaballero/legacy/2020-src`: codigo original preservado.

## Ejecucion rapida
```bash
cd Juego/Elcaballero
mvn test
mvn exec:java
```

## Flujo Git recomendado
- `main`: estable.
- `develop`: integracion.
- `feature/*`: trabajo nuevo.

Detalle completo en `Juego/Elcaballero/README.md`.
