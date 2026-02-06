# Desktop Packaging

## Requisitos
- JDK con `jpackage`
- WiX Toolset v3 en PATH (`candle.exe`, `light.exe`) para instalador `.exe`

## Comando
```bash
powershell -ExecutionPolicy Bypass -File scripts/package-exe.ps1
```

## Resultado
- Con WiX: genera instalador `.exe` en `dist/`.
- Sin WiX: fallback automatico a `app-image` portable en `dist/`.

## Nota tecnica
El script crea un input temporal con **solo el jar final** para evitar incluir artefactos de compilacion no deseados.
