# JuegoDefinitivo

Plataforma educativa que convierte libros en aventuras jugables.

## Monorepo
- `apps/backend`: API Spring Boot del motor AutoBook.
- `apps/frontend`: interfaz React para jugar historias.
- `docs`: roadmap y arquitectura.

## Inicio rapido
```bash
# Terminal 1
cd apps/backend
mvn spring-boot:run

# Terminal 2
cd apps/frontend
cp .env.example .env
npm install
npm run dev
```

## Flujo con tu PDF
1. En la UI React, usa `Importar Libro`.
2. Pega: `file:///C:/Users/jdsal/Downloads/El-caballero-de-la-armadura-oxidada-robert-fisher.pdf`
3. Elige libro e inicia partida.
