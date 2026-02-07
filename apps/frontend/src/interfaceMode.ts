export type InterfaceMode = "player" | "admin" | "debug";

export type InterfaceModeOption = {
  key: InterfaceMode;
  label: string;
  description: string;
};

export const INTERFACE_MODES: InterfaceModeOption[] = [
  { key: "player", label: "Jugador", description: "Lectura y aventura sin distracciones." },
  { key: "admin", label: "Admin Docente", description: "Aulas, estudiantes, asignaciones y seguimiento." },
  { key: "debug", label: "Debug", description: "Telemetria e inspeccion tecnica de sesiones." },
];

export function modeFromPath(pathname: string): InterfaceMode {
  if (pathname.startsWith("/admin")) {
    return "admin";
  }
  if (pathname.startsWith("/debug")) {
    return "debug";
  }
  return "player";
}

export function pathFromMode(mode: InterfaceMode): string {
  if (mode === "admin") {
    return "/admin";
  }
  if (mode === "debug") {
    return "/debug";
  }
  return "/";
}
