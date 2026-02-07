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
  entities: string[];
  cognitiveLevel: string;
  continuityHint: string;
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
  narrativeMemory: Record<string, number>;
  adaptiveDifficulty: string;
  quests: QuestView[];
  currentScene: SceneView | null;
  lastMessage: string;
};

export type TelemetrySummary = {
  totalEvents: number;
  byEvent: Record<string, number>;
  byStage: Record<string, number>;
};

export type NarrativeLink = {
  source: string;
  target: string;
  weight: number;
};

export type NarrativeGraph = {
  sessionId: string;
  nodes: Record<string, number>;
  links: NarrativeLink[];
};

export type Classroom = {
  id: string;
  name: string;
  teacherName: string;
  students: number;
  assignments: number;
};

export type StudentProgress = {
  studentId: string;
  studentName: string;
  attempts: number;
  completedAttempts: number;
  averageScore: number;
  averageCorrectAnswers: number;
  averageProgressPercent: number;
  averageEffectiveMinutes: number;
  dominantDifficulty: string;
};

export type ClassroomDashboard = {
  classroomId: string;
  classroomName: string;
  teacherName: string;
  students: number;
  assignments: number;
  activeAttempts: number;
  completedAttempts: number;
  abandonmentRatePercent: number;
  totalEffectiveReadingMinutes: number;
  averageEffectiveMinutesPerAttempt: number;
  abandonmentByActivity: {
    eventType: string;
    activeAttempts: number;
    activeRatePercent: number;
  }[];
  studentProgress: StudentProgress[];
};

export type LoginResponse = {
  accessToken: string;
  tokenType: string;
  expiresAtEpochSeconds: number;
  role: string;
};

export type StudentRecord = {
  id: string;
  classroomId: string;
  name: string;
};

export type AssignmentRecord = {
  id: string;
  classroomId: string;
  title: string;
  bookPath: string;
};
