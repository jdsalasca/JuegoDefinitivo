# AutoBook Adventure

Motor E2E que transforma libros `.txt` y `.pdf` en una aventura jugable con decisiones, retos de lectura, inventario y progreso persistente.

## Caracteristicas
- Importacion de libros por ruta local o URI `file:///`.
- Lectura de texto plano y PDF (Apache PDFBox).
- Segmentacion automatica en escenas jugables.
- Eventos narrativos por escena: dialogo, reto, descubrimiento, batalla y descanso.
- Sistema de inventario utilizable durante la partida.
- Quests de aprendizaje y exploracion.
- Guardado/carga de progreso completo (stats + inventario + escena actual).

## Arquitectura
- `app`: orquestacion de inicio.
- `config`: carga de `application.properties`.
- `ingest`: importacion y extraccion de libros (`txt`/`pdf`).
- `engine`: reglas del juego y acciones del jugador.
- `narrative`: construccion de escenas con sentido y dialogos.
- `persistence`: guardado y restauracion de partidas.
- `ui`: interfaz de consola jugable.

## Requisitos
- Java 17+
- Maven 3.9+

## Ejecutar
```bash
cd apps/backend
mvn test
mvn exec:java
```

## Flujo recomendado con tu PDF
En el menu principal:
1. `Importar libro (.txt/.pdf)`
2. Pega esta ruta:
   - `file:///C:/Users/jdsal/Downloads/El-caballero-de-la-armadura-oxidada-robert-fisher.pdf`
3. `Nueva partida`
4. Elige ese libro y empieza a jugar.

## Guardado
- Archivo: `.autobook-data/savegame.properties`
- Incluye: escena, vida, puntaje, inventario, respuestas correctas y descubrimientos.

## Calidad
Tests unitarios e integracion en `src/test/java/com/juegodefinitivo/autobook`.
CI en GitHub Actions: `.github/workflows/ci.yml`.
