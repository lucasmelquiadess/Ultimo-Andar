Param(
    [Parameter(Mandatory = $true)]
    [string]$Root
)

$ErrorActionPreference = "Stop"
$backend = Join-Path $Root "backend"
$logs = Join-Path $Root "logs"
$log = Join-Path $logs "backend.log"
$defaultJava = "C:\Program Files\Java\jdk-17"
$defaultMaven = Join-Path $env:USERPROFILE "Tools\apache-maven-3.9.16"

New-Item -ItemType Directory -Force -Path $logs | Out-Null

if (Test-Path $defaultJava) {
    $env:JAVA_HOME = $defaultJava
}
if (Test-Path $defaultMaven) {
    $env:MAVEN_HOME = $defaultMaven
}
if ($env:JAVA_HOME) {
    $env:Path = "$env:JAVA_HOME\bin;$env:Path"
}
if ($env:MAVEN_HOME) {
    $env:Path = "$env:MAVEN_HOME\bin;$env:Path"
}

Set-Content -Path $log -Value "Backend iniciado em $(Get-Date -Format s)"
Set-Location $backend
mvn -q -DskipTests package *>> $log
$jar = Get-ChildItem target\*.jar | Where-Object { $_.Name -notlike 'original-*' } | Select-Object -First 1
if (-not $jar) {
    throw "JAR do backend nao encontrado."
}
java -jar $jar.FullName *>> $log
