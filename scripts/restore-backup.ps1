Param(
    [Parameter(Mandatory = $true)]
    [string]$BackupFile,
    [SecureString]$Password,
    [switch]$Force
)

$ErrorActionPreference = "Stop"
$root = Split-Path -Parent $PSScriptRoot
$backend = Join-Path $root "backend"
$data = Join-Path $backend "data"
$tempZip = Join-Path ([IO.Path]::GetTempPath()) ("ultimo-andar-restore-" + [Guid]::NewGuid() + ".zip")

function Convert-ToPlainText([SecureString]$Secure) {
    $bstr = [Runtime.InteropServices.Marshal]::SecureStringToBSTR($Secure)
    try {
        return [Runtime.InteropServices.Marshal]::PtrToStringBSTR($bstr)
    } finally {
        [Runtime.InteropServices.Marshal]::ZeroFreeBSTR($bstr)
    }
}

function Decrypt-Backup([string]$InputPath, [string]$OutputPath, [SecureString]$SecurePassword) {
    $plainPassword = Convert-ToPlainText $SecurePassword
    $bytes = [IO.File]::ReadAllBytes($InputPath)
    $magic = [Text.Encoding]::ASCII.GetBytes("UA-BACKUP-AES-1`n")
    $tagLength = 32
    $saltLength = 16
    $ivLength = 16
    $headerLength = $magic.Length + $saltLength + $ivLength

    if ($bytes.Length -le ($headerLength + $tagLength)) {
        throw "Arquivo de backup invalido."
    }
    for ($i = 0; $i -lt $magic.Length; $i++) {
        if ($bytes[$i] -ne $magic[$i]) {
            throw "Arquivo de backup invalido."
        }
    }

    $payloadLength = $bytes.Length - $tagLength
    $payload = New-Object byte[] $payloadLength
    $tag = New-Object byte[] $tagLength
    [Buffer]::BlockCopy($bytes, 0, $payload, 0, $payloadLength)
    [Buffer]::BlockCopy($bytes, $payloadLength, $tag, 0, $tagLength)

    $salt = New-Object byte[] $saltLength
    $iv = New-Object byte[] $ivLength
    $cipherBytes = New-Object byte[] ($payloadLength - $headerLength)
    [Buffer]::BlockCopy($payload, $magic.Length, $salt, 0, $saltLength)
    [Buffer]::BlockCopy($payload, $magic.Length + $saltLength, $iv, 0, $ivLength)
    [Buffer]::BlockCopy($payload, $headerLength, $cipherBytes, 0, $cipherBytes.Length)

    $kdf = [Security.Cryptography.Rfc2898DeriveBytes]::new($plainPassword, $salt, 200000, [Security.Cryptography.HashAlgorithmName]::SHA256)
    $aes = [Security.Cryptography.Aes]::Create()
    try {
        $keyMaterial = $kdf.GetBytes(64)
        $aesKey = New-Object byte[] 32
        $macKey = New-Object byte[] 32
        [Array]::Copy($keyMaterial, 0, $aesKey, 0, 32)
        [Array]::Copy($keyMaterial, 32, $macKey, 0, 32)

        $hmac = [Security.Cryptography.HMACSHA256]::new($macKey)
        try {
            $expected = $hmac.ComputeHash($payload)
        } finally {
            $hmac.Dispose()
        }
        for ($i = 0; $i -lt $tagLength; $i++) {
            if ($tag[$i] -ne $expected[$i]) {
                throw "Senha incorreta ou backup alterado."
            }
        }

        $aes.Mode = [Security.Cryptography.CipherMode]::CBC
        $aes.Padding = [Security.Cryptography.PaddingMode]::PKCS7
        $aes.Key = $aesKey
        $aes.IV = $iv
        $decryptor = $aes.CreateDecryptor()
        $plainBytes = $decryptor.TransformFinalBlock($cipherBytes, 0, $cipherBytes.Length)
        [IO.File]::WriteAllBytes($OutputPath, $plainBytes)
    } finally {
        $aes.Dispose()
        $kdf.Dispose()
        $plainPassword = $null
    }
}

if (-not $Password) {
    $Password = Read-Host "Senha do backup" -AsSecureString
}

if ((Test-Path $data) -and -not $Force) {
    throw "A pasta backend\data ja existe. Use -Force para sobrescrever."
}

try {
    Decrypt-Backup -InputPath $BackupFile -OutputPath $tempZip -SecurePassword $Password
    Expand-Archive -Path $tempZip -DestinationPath $backend -Force
} finally {
    if (Test-Path $tempZip) {
        Remove-Item -LiteralPath $tempZip -Force
    }
}

Write-Host "Backup restaurado em $data" -ForegroundColor Green
