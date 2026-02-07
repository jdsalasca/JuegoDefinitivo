import { expect, test } from "@playwright/test";

test("guided flow supports start, manual action and autoplay with telemetry", async ({ page }) => {
  let currentSceneIndex = 0;
  let score = 0;
  let totalEvents = 0;
  const sessionId = "session-e2e-1";

  await page.route("**/api/books", async (route) => {
    if (route.request().method() === "GET") {
      await route.fulfill({
        json: [{ title: "Libro Demo", path: "C:/books/demo.txt", format: "txt" }],
      });
      return;
    }
    await route.fallback();
  });

  await page.route("**/api/game/start", async (route) => {
    currentSceneIndex = 0;
    score = 0;
    await route.fulfill({
      json: gameState(sessionId, score, currentSceneIndex, "Partida iniciada."),
    });
  });

  await page.route("**/api/game/**/action", async (route) => {
    currentSceneIndex += 1;
    score += 5;
    await route.fulfill({
      json: gameState(sessionId, score, currentSceneIndex, "Accion manual aplicada."),
    });
  });

  await page.route("**/api/game/**/autoplay", async (route) => {
    currentSceneIndex += 2;
    score += 12;
    await route.fulfill({
      json: gameState(sessionId, score, currentSceneIndex, "Modo auto aplicado. (auto-steps=2)"),
    });
  });

  await page.route("**/api/game/**/graph", async (route) => {
    await route.fulfill({
      json: {
        sessionId,
        nodes: { Mentor: 2, "Lugar: Bosque": 1 },
        links: [{ source: "Mentor", target: "Lugar: Bosque", weight: 1 }],
      },
    });
  });

  await page.route("**/api/telemetry/events", async (route) => {
    totalEvents += 1;
    await route.fulfill({ status: 202 });
  });

  await page.route("**/api/telemetry/summary", async (route) => {
    await route.fulfill({
      json: {
        totalEvents,
        byEvent: { action_executed: Math.min(totalEvents, 1), autoplay_run: totalEvents > 1 ? 1 : 0 },
        byStage: { play: totalEvents, setup: 0 },
      },
    });
  });

  await page.route("**/api/teacher/classrooms", async (route) => {
    if (route.request().method() === "GET") {
      await route.fulfill({ json: [] });
      return;
    }
    await route.fulfill({
      json: { id: "cls1", name: "Aula", teacherName: "Docente", students: 0, assignments: 0 },
    });
  });
  await page.route("**/api/teacher/classrooms/*/students", async (route) => {
    if (route.request().method() === "GET") {
      await route.fulfill({ json: [] });
      return;
    }
    await route.fulfill({ status: 200, body: "{}" });
  });
  await page.route("**/api/teacher/classrooms/*/assignments", async (route) => {
    if (route.request().method() === "GET") {
      await route.fulfill({ json: [] });
      return;
    }
    await route.fulfill({ status: 200, body: "{}" });
  });
  await page.route("**/api/teacher/classrooms/*/dashboard", async (route) => {
    await route.fulfill({
      json: {
        classroomId: "cls1",
        classroomName: "Aula",
        teacherName: "Docente",
        students: 0,
        assignments: 0,
        activeAttempts: 0,
        completedAttempts: 0,
        abandonmentRatePercent: 0,
        studentProgress: [],
      },
    });
  });

  await page.goto("/");
  await page.getByTestId("start-game").click();
  await expect(page.getByText("Jugador:")).toContainText("Aventurero");

  await page.getByTestId("execute-action").click();
  await expect(page.getByText("Puntaje").locator("..").getByText("5")).toBeVisible();

  await page.getByTestId("run-autoplay").click();
  await expect(page.getByText("Modo auto aplicado").first()).toBeVisible();
  await expect(page.getByTestId("telemetry-total")).toHaveText("3");
});

function gameState(sessionId: string, score: number, index: number, lastMessage: string) {
  return {
    sessionId,
    playerName: "Aventurero",
    bookTitle: "Libro Demo",
    completed: false,
    life: 95,
    knowledge: 6,
    courage: 4,
    focus: 5,
    score,
    correctAnswers: 1,
    discoveries: 1,
    inventory: { potion_small: 1 },
    narrativeMemory: { Mentor: 2, "Lugar: Bosque": 1 },
    adaptiveDifficulty: "INTERMEDIATE",
    quests: [
      {
        id: "reader",
        title: "Cronista aprendiz",
        description: "Responde correctamente 3 retos de lectura.",
        completed: false,
      },
    ],
    currentScene: {
      index,
      total: 8,
      title: `Escena ${index + 1}`,
      text: "Texto de prueba",
      eventType: "DIALOGUE",
      npc: "Mentor",
      challenge: {
        prompt: "Pregunta?",
        options: ["A", "B", "C", "D"],
      },
      entities: ["Mentor", "Lugar: Bosque"],
      cognitiveLevel: "LITERAL",
      continuityHint: "Mantener continuidad con Mentor.",
    },
    lastMessage,
  };
}
