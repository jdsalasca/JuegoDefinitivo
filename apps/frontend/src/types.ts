export type BookView = {
  title: string;
  path: string;
  format: string;
};

export type ChallengeView = {
  prompt: string;
  options: string[];
};

export type SceneView = {
  index: number;
  total: number;
  title: string;
  text: string;
  eventType: string;
  npc: string;
  challenge: ChallengeView;
};

export type QuestView = {
  id: string;
  title: string;
  description: string;
  completed: boolean;
};

export type GameState = {
  sessionId: string;
  playerName: string;
  bookTitle: string;
  completed: boolean;
  life: number;
  knowledge: number;
  courage: number;
  focus: number;
  score: number;
  correctAnswers: number;
  discoveries: number;
  inventory: Record<string, number>;
  quests: QuestView[];
  currentScene: SceneView | null;
  lastMessage: string;
};
