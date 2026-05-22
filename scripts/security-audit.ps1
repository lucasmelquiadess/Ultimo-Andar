$ErrorActionPreference = "Stop"
$root = Split-Path -Parent $PSScriptRoot
$backend = Join-Path $root "backend"
$frontend = Join-Path $root "frontend"

Write-Host "Verificando dependencias do frontend..." -ForegroundColor Cyan
Push-Location $frontend
npm audit
Pop-Location

Write-Host "Verificando dependencias do backend..." -ForegroundColor Cyan
Push-Location $backend
mvn org.owasp:dependency-check-maven:check
Pop-Location
