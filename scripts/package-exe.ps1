param(
    [string]$AppVersion = "3.0.0",
    [string]$AppName = "AutoBookQuest",
    [string]$PackageType = "exe"
)

$ErrorActionPreference = "Stop"

$root = Split-Path -Parent $PSScriptRoot
$frontend = Join-Path $root "apps/frontend"
$backend = Join-Path $root "apps/backend"
$staticDir = Join-Path $backend "src/main/resources/static"

Write-Host "[1/5] Building frontend"
Push-Location $frontend
npm ci
npm run build
Pop-Location

Write-Host "[2/5] Sync frontend dist into backend static resources"
if (Test-Path $staticDir) {
    Get-ChildItem -Path $staticDir -Force | Remove-Item -Recurse -Force
}
Copy-Item -Path (Join-Path $frontend "dist/*") -Destination $staticDir -Recurse -Force

Write-Host "[3/5] Packaging Spring Boot jar"
Push-Location $backend
mvn clean package -DskipTests
Pop-Location

$jarPath = Join-Path $backend ("target/autobook-adventure-" + $AppVersion + ".jar")
if (!(Test-Path $jarPath)) {
    throw "No se encontro jar en $jarPath"
}

Write-Host "[4/5] Running jpackage"
$jpackageOut = Join-Path $root "dist"
New-Item -ItemType Directory -Force -Path $jpackageOut | Out-Null

$wixLight = Get-Command light.exe -ErrorAction SilentlyContinue
$wixCandle = Get-Command candle.exe -ErrorAction SilentlyContinue
if ($PackageType -eq "exe" -and ($null -eq $wixLight -or $null -eq $wixCandle)) {
    Write-Warning "WiX no detectado. Se generara app-image en lugar de exe."
    $PackageType = "app-image"
}

$jpackageArgs = @(
    "--type", $PackageType,
    "--name", $AppName,
    "--dest", $jpackageOut,
    "--input", (Join-Path $backend "target"),
    "--main-jar", (Split-Path $jarPath -Leaf),
    "--main-class", "org.springframework.boot.loader.launch.JarLauncher",
    "--java-options", "-Dserver.port=8080"
)

& jpackage @jpackageArgs

Write-Host "[5/5] Done. Package in $jpackageOut"
