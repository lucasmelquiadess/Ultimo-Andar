Param(
    [SecureString]$Password
)

$ErrorActionPreference = "Stop"
$root = Split-Path -Parent $PSScriptRoot
$data = Join-Path $root "backend\data"
$backupDir = Join-Path $root "backups"
$stamp = Get-Date -Format "yyyyMMdd-HHmmss"
$tempZip = Join-Path $backupDir "ultimo-andar-backup-$stamp.zip.tmp"
$backupFile = Join-Path $backupDir "ultimo-andar-backup-$stamp.zip.aes"

function Convert-ToPlainText([SecureString]$Secure) {
    $bstr = [Runtime.InteropServices.Marshal]::SecureStringToBSTR($Secure)
    try {
        return [Runtime.InteropServices.Marshal]::PtrToStringBSTR($bstr)
    } finally {
        [Runtime.InteropServices.Marshal]::ZeroFreeBSTR($bstr)
    }
}

function Get-RandomBytes([int]$Length) {
    $bytes = New-Object byte[] $Length
    $rng = [Security.Cryptography.RandomNumberGenerator]::Create()
    try {
        $rng.GetBytes($bytes)
    } finally {
        $rng.Dispose()
    }
    return $bytes
}

function Encrypt-Backup([string]$InputPath, [string]$OutputPath, [SecureString]$SecurePassword) {
    $plainPassword = Convert-ToPlainText $SecurePassword
    $salt = Get-RandomBytes 16
    $iv = Get-RandomBytes 16
    $magic = [Text.Encoding]::ASCII.GetBytes("UA-BACKUP-AES-1`n")

    $kdf = [Security.Cryptography.Rfc2898DeriveBytes]::new($plainPassword, $salt, 200000, [Security.Cryptography.HashAlgorithmName]::SHA256)
    $aes = [Security.Cryptography.Aes]::Create()
    try {
        $keyMaterial = $kdf.GetBytes(64)
        $aesKey = New-Object byte[] 32
        $macKey = New-Object byte[] 32
        [Array]::Copy($keyMaterial, 0, $aesKey, 0, 32)
        [Array]::Copy($keyMaterial, 32, $macKey, 0, 32)

        $aes.Mode = [Security.Cryptography.CipherMode]::CBC
        $aes.Padding = [Security.Cryptography.PaddingMode]::PKCS7
        $aes.Key = $aesKey
        $aes.IV = $iv

        $plainBytes = [IO.File]::ReadAllBytes($InputPath)
        $encryptor = $aes.CreateEncryptor()
        $cipherBytes = $encryptor.TransformFinalBlock($plainBytes, 0, $plainBytes.Length)

        $payloadLength = $magic.Length + $salt.Length + $iv.Length + $cipherBytes.Length
        $payload = New-Object byte[] $payloadLength
        [Buffer]::BlockCopy($magic, 0, $payload, 0, $magic.Length)
        [Buffer]::BlockCopy($salt, 0, $payload, $magic.Length, $salt.Length)
        [Buffer]::BlockCopy($iv, 0, $payload, $magic.Length + $salt.Length, $iv.Length)
        [Buffer]::BlockCopy($cipherBytes, 0, $payload, $magic.Length + $salt.Length + $iv.Length, $cipherBytes.Length)

        $hmac = [Security.Cryptography.HMACSHA256]::new($macKey)
        try {
            $tag = $hmac.ComputeHash($payload)
        } finally {
            $hmac.Dispose()
        }

        $output = New-Object byte[] ($payload.Length + $tag.Length)
        [Buffer]::BlockCopy($payload, 0, $output, 0, $payload.Length)
        [Buffer]::BlockCopy($tag, 0, $output, $payload.Length, $tag.Length)
        [IO.File]::WriteAllBytes($OutputPath, $output)
    } finally {
        $aes.Dispose()
        $kdf.Dispose()
        $plainPassword = $null
    }
}

New-Item -ItemType Directory -Force -Path $backupDir | Out-Null

if (-not (Test-Path $data)) {
    Write-Host "Nenhum dado local encontrado para backup." -ForegroundColor Yellow
    exit 0
}

if (-not $Password) {
    $Password = Read-Host "Senha para proteger o backup" -AsSecureString
}

try {
    Compress-Archive -Path $data -DestinationPath $tempZip -Force
    Encrypt-Backup -InputPath $tempZip -OutputPath $backupFile -SecurePassword $Password
} finally {
    if (Test-Path $tempZip) {
        Remove-Item -LiteralPath $tempZip -Force
    }
}

Write-Host "Backup criptografado criado em $backupFile" -ForegroundColor Green
