import { useCallback, useEffect, useMemo, useState } from "react";
import "./App.css";
import { importBook, listBooks, loadState, sendAction, startGame } from "./api";
import type { BookView, GameState } from "./types";

type ActionKind = "TALK" | "EXPLORE" | "CHALLENGE" | "USE_ITEM";

type ActionDescriptor = {
  key: ActionKind;
  label: string;
  description: string;
  needsChallenge: boolean;
  needsItem: boolean;
};

const ACTIONS: ActionDescriptor[] = [
  {
    key: "TALK",
    label: "Dialogar",
    description: "Habla con el personaje para obtener contexto y progreso seguro.",
    needsChallenge: false,
    needsItem: false,
  },
  {
    key: "EXPLORE",
    label: "Explorar",
    description: "Busca recursos y descubrimientos. Puede incluir riesgo.",
    needsChallenge: false,
    needsItem: false,
  },
  {
    key: "CHALLENGE",
    label: "Resolver reto",
    description: "Responde una pregunta de lectura para ganar conocimiento.",
    needsChallenge: true,
    needsItem: false,
  },
  {
    key: "USE_ITEM",
    label: "Usar item",
    description: "Consume un item del inventario para obtener ventaja.",
    needsChallenge: false,
    needsItem: true,
  },
];

const SESSION_KEY = "autobook:lastSessionId";

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
  const [selectedAction, setSelectedAction] = useState<ActionKind>("TALK");
  const [state, setState] = useState<GameState | null>(null);
  const [statusMessage, setStatusMessage] = useState<string>("Listo para iniciar.");
  const [loading, setLoading] = useState<boolean>(false);
  const [error, setError] = useState<string>("");

  const activeAction = useMemo(
    () => ACTIONS.find((action) => action.key === selectedAction) ?? ACTIONS[0],
    [selectedAction],
  );

  const inventoryEntries = useMemo(() => {
    if (!state) {
      return [] as Array<[string, number]>;
    }
    return Object.entries(state.inventory);
  }, [state]);

  const progress = useMemo(() => {
    if (!state?.currentScene) {
      return 0;
    }
    return Math.floor(((state.currentScene.index + 1) / state.currentScene.total) * 100);
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
      setError("No se pudo cargar el catalogo inicial.");
    });
  }, [refreshBooks]);

  useEffect(() => {
    const savedSession = localStorage.getItem(SESSION_KEY);
    if (savedSession) {
      setSessionIdInput(savedSession);
    }
  }, []);

  async function withRequest(task: () => Promise<void>, successMessage?: string) {
    setLoading(true);
    setError("");
    try {
      await task();
      if (successMessage) {
        setStatusMessage(successMessage);
      }
    } catch (err) {
      const message = err instanceof Error ? err.message : "Error inesperado.";
      setError(message);
      setStatusMessage("Operacion fallida.");
    } finally {
      setLoading(false);
    }
  }

  function persistSession(sessionId: string) {
    setSessionIdInput(sessionId);
    localStorage.setItem(SESSION_KEY, sessionId);
  }

  function resolveItemId() {
    if (selectedItemId) {
      return selectedItemId;
    }
    return inventoryEntries.length > 0 ? inventoryEntries[0][0] : "";
  }

  function canSendAction(): boolean {
    if (!state?.currentScene || state.completed) {
      return false;
    }
    if (activeAction.needsItem && inventoryEntries.length === 0) {
      return false;
    }
    return true;
  }

  async function executeSelectedAction() {
    if (!state) {
      return;
    }
    const itemId = activeAction.needsItem ? resolveItemId() : undefined;
    const challengeAnswer = activeAction.needsChallenge ? answerIndex : undefined;
    const result = await sendAction(state.sessionId, activeAction.key, challengeAnswer, itemId);
    setState(result);
    setStatusMessage(result.lastMessage);
  }

  return (
    <main className="app-shell">
      <header className="topbar">
        <div>
          <p className="eyebrow">Lectura interactiva</p>
          <h1>AutoBook Quest</h1>
          <p className="subtitle">
            Flujo guiado: importa un libro, inicia sesion y juega escena por escena.
          </p>
        </div>
        <div className="topbar-status">
          <span className={`dot ${loading ? "busy" : "idle"}`} />
          <span>{loading ? "Procesando" : "Disponible"}</span>
        </div>
      </header>

      <section className="content-grid">
        <aside className="panel setup-panel">
          <h2>1. Preparar partida</h2>

          <label>
            Nombre del jugador
            <input value={playerName} onChange={(e) => setPlayerName(e.target.value)} />
          </label>

          <label>
            Ruta de libro (.txt/.pdf)
            <input value={importPath} onChange={(e) => setImportPath(e.target.value)} />
          </label>

          <div className="row">
            <button
              disabled={loading}
              onClick={() =>
                withRequest(async () => {
                  await importBook(importPath);
                  await refreshBooks();
                }, "Libro importado y catalogo actualizado.")
              }
            >
              Importar libro
            </button>
            <button disabled={loading} onClick={() => withRequest(refreshBooks, "Catalogo actualizado.")}>
              Refrescar
            </button>
          </div>

          <label>
            Libro elegido
            <select value={selectedBookPath} onChange={(e) => setSelectedBookPath(e.target.value)}>
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
                  persistSession(result.sessionId);
                  setSelectedAction("TALK");
                }, "Partida iniciada correctamente.")
              }
            >
              2. Iniciar partida
            </button>
          </div>

          <h3>Reanudar sesion</h3>
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
                persistSession(sessionIdInput);
              }, "Sesion cargada.")
            }
          >
            Cargar sesion
          </button>
        </aside>

        <section className="panel play-panel">
          <h2>3. Jugar</h2>
          {!state && <p className="placeholder">Inicia o carga una sesion para comenzar.</p>}

          {state && (
            <>
              <section className="hud">
                <div>
                  <p className="hud-title">{state.bookTitle}</p>
                  <p>
                    Jugador: <strong>{state.playerName}</strong>
                  </p>
                </div>
                <div className="progress-wrap">
                  <div className="progress-meta">
                    <span>Progreso</span>
                    <span>{progress}%</span>
                  </div>
                  <div className="progress-bar">
                    <div className="progress-fill" style={{ width: `${progress}%` }} />
                  </div>
                </div>
              </section>

              <section className="stats-grid">
                <Metric label="Vida" value={state.life} variant="life" />
                <Metric label="Conocimiento" value={state.knowledge} variant="knowledge" />
                <Metric label="Coraje" value={state.courage} variant="courage" />
                <Metric label="Enfoque" value={state.focus} variant="focus" />
                <Metric label="Puntaje" value={state.score} variant="score" />
              </section>

              {state.currentScene ? (
                <article className="scene-card">
                  <h3>
                    {state.currentScene.title} ({state.currentScene.index + 1}/{state.currentScene.total})
                  </h3>
                  <p className="scene-meta">
                    Evento: <strong>{state.currentScene.eventType}</strong> · NPC: <strong>{state.currentScene.npc}</strong>
                  </p>
                  <p className="scene-text">{state.currentScene.text}</p>
                </article>
              ) : (
                <article className="scene-card complete">
                  <h3>Aventura finalizada</h3>
                  <p>Ya no quedan escenas pendientes para esta sesion.</p>
                </article>
              )}

              <section className="actions-panel">
                <h3>Accion recomendada</h3>
                <div className="action-grid">
                  {ACTIONS.map((action) => (
                    <button
                      key={action.key}
                      className={`action-tile ${selectedAction === action.key ? "selected" : ""}`}
                      onClick={() => setSelectedAction(action.key)}
                      disabled={loading || !state.currentScene || state.completed}
                    >
                      <span className="action-label">{action.label}</span>
                      <span className="action-desc">{action.description}</span>
                    </button>
                  ))}
                </div>

                {state.currentScene && activeAction.needsChallenge && (
                  <label>
                    Respuesta del reto
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
                )}

                {activeAction.needsItem && (
                  <label>
                    Item a usar
                    <select
                      value={selectedItemId}
                      onChange={(e) => setSelectedItemId(e.target.value)}
                    >
                      <option value="">Seleccionar automaticamente</option>
                      {inventoryEntries.map(([id, qty]) => (
                        <option key={id} value={id}>
                          {id} x{qty}
                        </option>
                      ))}
                    </select>
                  </label>
                )}

                <button
                  className="play-button"
                  disabled={loading || !canSendAction()}
                  onClick={() => withRequest(executeSelectedAction)}
                >
                  Ejecutar: {activeAction.label}
                </button>
              </section>

              <section className="bottom-grid">
                <article className="mini-panel">
                  <h4>Inventario</h4>
                  {inventoryEntries.length === 0 ? (
                    <p>Sin items</p>
                  ) : (
                    <ul>
                      {inventoryEntries.map(([id, qty]) => (
                        <li key={id}>
                          {id} x{qty}
                        </li>
                      ))}
                    </ul>
                  )}
                </article>

                <article className="mini-panel">
                  <h4>Quests</h4>
                  <ul>
                    {state.quests.map((quest) => (
                      <li key={quest.id} className={quest.completed ? "quest-done" : "quest-pending"}>
                        {quest.title}
                      </li>
                    ))}
                  </ul>
                </article>
              </section>
            </>
          )}
        </section>
      </section>

      <footer className="feedback-strip">
        <p>{statusMessage}</p>
        {state?.lastMessage && <p>{state.lastMessage}</p>}
        {error && <p className="error">Error: {error}</p>}
      </footer>
    </main>
  );
}

type MetricProps = {
  label: string;
  value: number;
  variant: "life" | "knowledge" | "courage" | "focus" | "score";
};

function Metric({ label, value, variant }: MetricProps) {
  return (
    <div className={`metric metric-${variant}`}>
      <span>{label}</span>
      <strong>{value}</strong>
    </div>
  );
}

export default App;
