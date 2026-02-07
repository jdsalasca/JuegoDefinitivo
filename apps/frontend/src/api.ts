import type {
  AssignmentRecord,
  BookView,
  Classroom,
  ClassroomDashboard,
  GameState,
  LoginResponse,
  NarrativeGraph,
  StudentRecord,
  TelemetrySummary,
} from "./types";

const API_BASE = import.meta.env.VITE_API_BASE ?? "http://localhost:8080/api";
const API_TOKEN = import.meta.env.VITE_API_TOKEN ?? "dev-admin-token";
const ACCESS_TOKEN_KEY = "autobook:accessToken";
export const API_BASE_URL = API_BASE;
let accessToken = localStorage.getItem(ACCESS_TOKEN_KEY) ?? "";

async function request<T>(path: string, init?: RequestInit): Promise<T> {
  const headers = new Headers(init?.headers);
  headers.set("Content-Type", "application/json");
  if (accessToken) {
    headers.set("Authorization", `Bearer ${accessToken}`);
  } else if (API_TOKEN) {
    headers.set("X-Api-Token", API_TOKEN);
  }
  const response = await fetch(`${API_BASE}${path}`, {
    headers,
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

export async function login(username: string, password: string): Promise<LoginResponse> {
  const payload = await request<LoginResponse>("/auth/login", {
    method: "POST",
    body: JSON.stringify({ username, password }),
  });
  accessToken = payload.accessToken;
  localStorage.setItem(ACCESS_TOKEN_KEY, payload.accessToken);
  return payload;
}

export function clearAccessToken(): void {
  accessToken = "";
  localStorage.removeItem(ACCESS_TOKEN_KEY);
}

export function hasAccessToken(): boolean {
  return Boolean(accessToken);
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

export async function listClassrooms(): Promise<Classroom[]> {
  return request<Classroom[]>("/teacher/classrooms");
}

export async function createClassroom(name: string, teacherName: string): Promise<Classroom> {
  return request<Classroom>("/teacher/classrooms", {
    method: "POST",
    body: JSON.stringify({ name, teacherName }),
  });
}

export async function addStudent(classroomId: string, name: string): Promise<void> {
  await request<void>(`/teacher/classrooms/${classroomId}/students`, {
    method: "POST",
    body: JSON.stringify({ name }),
  });
}

export async function listStudents(classroomId: string): Promise<StudentRecord[]> {
  return request<StudentRecord[]>(`/teacher/classrooms/${classroomId}/students`);
}

export async function createAssignment(classroomId: string, title: string, bookPath: string): Promise<void> {
  await request<void>(`/teacher/classrooms/${classroomId}/assignments`, {
    method: "POST",
    body: JSON.stringify({ title, bookPath }),
  });
}

export async function listAssignments(classroomId: string): Promise<AssignmentRecord[]> {
  return request<AssignmentRecord[]>(`/teacher/classrooms/${classroomId}/assignments`);
}

export async function linkAttempt(studentId: string, assignmentId: string, sessionId: string): Promise<void> {
  await request<void>("/teacher/attempts/link", {
    method: "POST",
    body: JSON.stringify({ studentId, assignmentId, sessionId }),
  });
}

export async function fetchClassroomDashboard(classroomId: string): Promise<ClassroomDashboard> {
  return request<ClassroomDashboard>(`/teacher/classrooms/${classroomId}/dashboard`);
}

export async function fetchClassroomDashboardByRange(
  classroomId: string,
  from?: string,
  to?: string,
): Promise<ClassroomDashboard> {
  const params = new URLSearchParams();
  if (from) {
    params.set("from", from);
  }
  if (to) {
    params.set("to", to);
  }
  const query = params.toString();
  const suffix = query ? `?${query}` : "";
  return request<ClassroomDashboard>(`/teacher/classrooms/${classroomId}/dashboard${suffix}`);
}
