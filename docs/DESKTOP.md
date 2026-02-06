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
- En pipeline de release: se publica `AutoBookQuest-win64.zip` + `latest.json` para auto-update.

## Firma de binarios en CI
- El workflow `.github/workflows/release.yml` firma opcionalmente los `.exe` si existen secretos:
  - `WINDOWS_CERT_BASE64`
  - `WINDOWS_CERT_PASSWORD`

## Nota tecnica
El script crea un input temporal con **solo el jar final** para evitar incluir artefactos de compilacion no deseados.
