# Desktop Packaging

## Requisitos para `.exe`
- JDK con `jpackage`
- WiX Toolset en PATH (`candle.exe`, `light.exe`)

## Comando
```bash
powershell -ExecutionPolicy Bypass -File scripts/package-exe.ps1
```

## Comportamiento
- Si WiX esta instalado: genera `exe`.
- Si WiX no esta instalado: fallback automatico a `app-image`.

## Ubicacion de salida
- `dist/`
