@echo off
setlocal
powershell -ExecutionPolicy Bypass -File "%~dp0package-exe.ps1" %*
endlocal
