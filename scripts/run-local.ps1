Param(
    [switch]$WithSecurity,
    [switch]$WithoutSecurity
)

$ErrorActionPreference = "Stop"
$root = Split-Path -Parent $PSScriptRoot
$logs = Join-Path $root "logs"
$backendStarter = Join-Path $PSScriptRoot "start-backend.ps1"
$frontendStarter = Join-Path $PSScriptRoot "start-frontend.ps1"

Write-Host "Iniciando Ultimo Andar em modo local..." -ForegroundColor Cyan

New-Item -ItemType Directory -Force -Path $logs | Out-Null

function Test-PortBusy([int]$Port) {
    return [bool](Get-NetTCPConnection -LocalPort $Port -State Listen -ErrorAction SilentlyContinue)
}

function Get-FreePort([int]$Preferred) {
    $port = $Preferred
    while (Test-PortBusy $port) {
        $port++
    }
    return $port
}

if ($WithoutSecurity) {
    $env:APP_SECURITY_ENABLED = "false"
} else {
    $env:APP_SECURITY_ENABLED = "true"
}

$backendPort = Get-FreePort 8080
$frontendPort = Get-FreePort 5173
$env:SERVER_PORT = "$backendPort"
$env:VITE_DEV_PORT = "$frontendPort"
$env:VITE_PROXY_TARGET = "http://localhost:$backendPort"

Start-Process powershell -WindowStyle Hidden -ArgumentList "-NoExit", "-ExecutionPolicy", "Bypass", "-File", "`"$backendStarter`"", "-Root", "`"$root`""
Start-Process powershell -WindowStyle Hidden -ArgumentList "-NoExit", "-ExecutionPolicy", "Bypass", "-File", "`"$frontendStarter`"", "-Root", "`"$root`""

Write-Host "Backend:  http://localhost:$backendPort" -ForegroundColor Green
Write-Host "Frontend: http://localhost:$frontendPort" -ForegroundColor Green
Write-Host "Login inicial: admin / admin123. O sistema exigira troca de senha no primeiro acesso." -ForegroundColor Yellow
Write-Host "Banco H2 local em backend/data/db. Storage em backend/data/storage." -ForegroundColor Yellow
Write-Host "Logs: $logs" -ForegroundColor Yellow
