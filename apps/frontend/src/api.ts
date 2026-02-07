import type { BookView, GameState, NarrativeGraph, TelemetrySummary } from "./types";

const API_BASE = import.meta.env.VITE_API_BASE ?? "http://localhost:8080/api";

async function request<T>(path: string, init?: RequestInit): Promise<T> {
  const response = await fetch(`${API_BASE}${path}`, {
    headers: {
      "Content-Type": "application/json",
    },
    ...init,
  });

  if (!response.ok) {
    let message = `HTTP ${response.status}`;
    try {
      const body = (await response.json()) as { error?: string; message?: string };
      message = body.error ?? body.message ?? message;
    } catch {
      // ignore parse errors
    }
    throw new Error(message);
  }

  const contentType = response.headers.get("content-type") ?? "";
  if (!contentType.includes("application/json")) {
    return undefined as T;
  }
  return (await response.json()) as T;
}

export async function listBooks(): Promise<BookView[]> {
  return request<BookView[]>("/books");
}

export async function importBook(path: string): Promise<BookView> {
  return request<BookView>("/books/import", {
    method: "POST",
    body: JSON.stringify({ path }),
  });
}

export async function startGame(playerName: string, bookPath: string): Promise<GameState> {
  return request<GameState>("/game/start", {
    method: "POST",
    body: JSON.stringify({ playerName, bookPath }),
  });
}

export async function sendAction(
  sessionId: string,
  action: "TALK" | "EXPLORE" | "CHALLENGE" | "USE_ITEM",
  answerIndex?: number,
  itemId?: string,
): Promise<GameState> {
  return request<GameState>(`/game/${sessionId}/action`, {
    method: "POST",
    body: JSON.stringify({ action, answerIndex, itemId }),
  });
}

export async function loadState(sessionId: string): Promise<GameState> {
  return request<GameState>(`/game/${sessionId}`);
}

export async function loadNarrativeGraph(sessionId: string): Promise<NarrativeGraph> {
  return request<NarrativeGraph>(`/game/${sessionId}/graph`);
}

export async function autoplay(
  sessionId: string,
  ageBand: string,
  readingLevel: string,
  maxSteps: number,
): Promise<GameState> {
  return request<GameState>(`/game/${sessionId}/autoplay`, {
    method: "POST",
    body: JSON.stringify({ ageBand, readingLevel, maxSteps }),
  });
}

export async function trackEvent(
  sessionId: string | null,
  eventName: string,
  stage: string,
  elapsedMs: number,
  metadata: Record<string, string> = {},
): Promise<void> {
  await request<void>("/telemetry/events", {
    method: "POST",
    body: JSON.stringify({ sessionId, eventName, stage, elapsedMs, metadata }),
  });
}

export async function fetchTelemetrySummary(): Promise<TelemetrySummary> {
  return request<TelemetrySummary>("/telemetry/summary");
}
