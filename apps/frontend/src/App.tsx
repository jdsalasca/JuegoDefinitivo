import { useCallback, useEffect, useMemo, useState } from "react";
import "./App.css";
import { importBook, listBooks, loadState, sendAction, startGame } from "./api";
import type { BookView, GameState } from "./types";

type ActionKind = "TALK" | "EXPLORE" | "CHALLENGE" | "USE_ITEM";

function App() {
  const [books, setBooks] = useState<BookView[]>([]);
  const [selectedBookPath, setSelectedBookPath] = useState<string>("");
  const [playerName, setPlayerName] = useState<string>("Aventurero");
  const [importPath, setImportPath] = useState<string>(
    "file:///C:/Users/jdsal/Downloads/El-caballero-de-la-armadura-oxidada-robert-fisher.pdf",
  );
  const [sessionIdInput, setSessionIdInput] = useState<string>("");
  const [answerIndex, setAnswerIndex] = useState<number>(1);
  const [selectedItemId, setSelectedItemId] = useState<string>("");
  const [state, setState] = useState<GameState | null>(null);
  const [loading, setLoading] = useState<boolean>(false);
  const [error, setError] = useState<string>("");

  const inventoryEntries = useMemo(() => {
    if (!state) {
      return [] as Array<[string, number]>;
    }
    return Object.entries(state.inventory);
  }, [state]);

  const refreshBooks = useCallback(async () => {
    const items = await listBooks();
    setBooks(items);
    if (!selectedBookPath && items.length > 0) {
      setSelectedBookPath(items[0].path);
    }
  }, [selectedBookPath]);

  useEffect(() => {
    refreshBooks().catch(() => {
      // no-op on initial load
    });
  }, [refreshBooks]);

  async function withRequest(task: () => Promise<void>) {
    setLoading(true);
    setError("");
    try {
      await task();
    } catch (err) {
      const message = err instanceof Error ? err.message : "Error inesperado.";
      setError(message);
    } finally {
      setLoading(false);
    }
  }

  function resolveItemId() {
    if (selectedItemId) {
      return selectedItemId;
    }
    return inventoryEntries.length > 0 ? inventoryEntries[0][0] : "";
  }

  return (
    <main className="layout">
      <section className="hero">
        <h1>AutoBook Quest Lab</h1>
        <p>
          Convierte libros en una aventura interactiva. Importa, juega, responde retos y
          completa quests.
        </p>
      </section>

      <section className="grid">
        <article className="panel controls">
          <h2>Control</h2>
          <label>
            Nombre del jugador
            <input value={playerName} onChange={(e) => setPlayerName(e.target.value)} />
          </label>

          <label>
            Ruta para importar (.txt/.pdf)
            <input value={importPath} onChange={(e) => setImportPath(e.target.value)} />
          </label>

          <div className="row">
            <button
              disabled={loading}
              onClick={() =>
                withRequest(async () => {
                  await importBook(importPath);
                  await refreshBooks();
                })
              }
            >
              Importar Libro
            </button>
            <button disabled={loading} onClick={() => withRequest(refreshBooks)}>
              Refrescar Catalogo
            </button>
          </div>

          <label>
            Libro seleccionado
            <select
              value={selectedBookPath}
              onChange={(e) => setSelectedBookPath(e.target.value)}
            >
              {books.map((book) => (
                <option key={book.path} value={book.path}>
                  {book.title} [{book.format}]
                </option>
              ))}
            </select>
          </label>

          <div className="row">
            <button
              disabled={loading || !selectedBookPath}
              onClick={() =>
                withRequest(async () => {
                  const result = await startGame(playerName, selectedBookPath);
                  setState(result);
                  setSessionIdInput(result.sessionId);
                })
              }
            >
              Iniciar Partida
            </button>
          </div>

          <label>
            Session ID
            <input
              value={sessionIdInput}
              onChange={(e) => setSessionIdInput(e.target.value)}
              placeholder="Pega un sessionId"
            />
          </label>
          <button
            disabled={loading || !sessionIdInput}
            onClick={() =>
              withRequest(async () => {
                const loaded = await loadState(sessionIdInput);
                setState(loaded);
              })
            }
          >
            Cargar Sesion
          </button>
        </article>

        <article className="panel gameplay">
          <h2>Partida</h2>
          {!state && <p>Inicia una partida para ver la escena jugable.</p>}

          {state && (
            <>
              <header className="hud">
                <p>
                  <strong>{state.playerName}</strong> · {state.bookTitle}
                </p>
                <p>
                  Vida {state.life} | Conocimiento {state.knowledge} | Coraje {state.courage}
                </p>
                <p>
                  Enfoque {state.focus} | Puntaje {state.score} | Correctas {state.correctAnswers}
                </p>
              </header>

              {state.currentScene ? (
                <section className="scene">
                  <h3>
                    {state.currentScene.title} ({state.currentScene.index + 1}/{state.currentScene.total})
                  </h3>
                  <p className="event-tag">Evento: {state.currentScene.eventType}</p>
                  <p className="npc-tag">NPC: {state.currentScene.npc}</p>
                  <p>{state.currentScene.text}</p>
                </section>
              ) : (
                <section className="scene done">
                  <h3>Aventura completada</h3>
                  <p>La partida no tiene escenas pendientes.</p>
                </section>
              )}

              <section className="actions">
                <h3>Acciones</h3>
                <div className="row wrap">
                  {(["TALK", "EXPLORE", "CHALLENGE", "USE_ITEM"] as ActionKind[]).map((action) => (
                    <button
                      key={action}
                      disabled={loading || !state.currentScene}
                      onClick={() =>
                        withRequest(async () => {
                          const result = await sendAction(
                            state.sessionId,
                            action,
                            action === "CHALLENGE" ? answerIndex : undefined,
                            action === "USE_ITEM" ? resolveItemId() : undefined,
                          );
                          setState(result);
                        })
                      }
                    >
                      {action}
                    </button>
                  ))}
                </div>

                {state.currentScene && (
                  <>
                    <label>
                      Respuesta para CHALLENGE
                      <select
                        value={answerIndex}
                        onChange={(e) => setAnswerIndex(Number(e.target.value))}
                      >
                        {state.currentScene.challenge.options.map((option, idx) => (
                          <option key={option + idx} value={idx + 1}>
                            {idx + 1}. {option}
                          </option>
                        ))}
                      </select>
                    </label>

                    <label>
                      Item para USE_ITEM
                      <select
                        value={selectedItemId}
                        onChange={(e) => setSelectedItemId(e.target.value)}
                      >
                        <option value="">(auto)</option>
                        {inventoryEntries.map(([id, qty]) => (
                          <option key={id} value={id}>
                            {id} x{qty}
                          </option>
                        ))}
                      </select>
                    </label>
                  </>
                )}
              </section>

              <section className="meta">
                <h3>Inventario</h3>
                {inventoryEntries.length === 0 && <p>Vacio</p>}
                {inventoryEntries.length > 0 && (
                  <ul>
                    {inventoryEntries.map(([id, qty]) => (
                      <li key={id}>
                        {id} x{qty}
                      </li>
                    ))}
                  </ul>
                )}

                <h3>Quests</h3>
                <ul>
                  {state.quests.map((quest) => (
                    <li key={quest.id} className={quest.completed ? "done" : "pending"}>
                      {quest.title}: {quest.completed ? "COMPLETADA" : "PENDIENTE"}
                    </li>
                  ))}
                </ul>
              </section>
            </>
          )}
        </article>
      </section>

      <section className="status">
        <p>{loading ? "Procesando..." : "Listo."}</p>
        <p>{state?.lastMessage ?? "Sin eventos aun."}</p>
        {error && <p className="error">Error: {error}</p>}
      </section>
    </main>
  );
}

export default App;
