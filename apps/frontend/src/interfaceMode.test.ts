import { describe, expect, it } from "vitest";
import { modeFromPath, pathFromMode } from "./interfaceMode";

describe("interface mode routing", () => {
  it("maps pathnames to modes", () => {
    expect(modeFromPath("/")).toBe("player");
    expect(modeFromPath("/player")).toBe("player");
    expect(modeFromPath("/admin")).toBe("admin");
    expect(modeFromPath("/admin/classroom")).toBe("admin");
    expect(modeFromPath("/debug")).toBe("debug");
    expect(modeFromPath("/debug/session/x")).toBe("debug");
  });

  it("maps modes to canonical pathnames", () => {
    expect(pathFromMode("player")).toBe("/");
    expect(pathFromMode("admin")).toBe("/admin");
    expect(pathFromMode("debug")).toBe("/debug");
  });
});
