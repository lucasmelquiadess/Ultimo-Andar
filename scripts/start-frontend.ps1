Param(
    [Parameter(Mandatory = $true)]
    [string]$Root
)

$ErrorActionPreference = "Stop"
$frontend = Join-Path $Root "frontend"
$logs = Join-Path $Root "logs"
$log = Join-Path $logs "frontend.log"

New-Item -ItemType Directory -Force -Path $logs | Out-Null

Set-Content -Path $log -Value "Frontend iniciado em $(Get-Date -Format s)"
Set-Location $frontend
npm install *>> $log
npm run dev *>> $log
