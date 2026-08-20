# Load .env.local into the current PowerShell session (pre-prod local run)
$ErrorActionPreference = "Stop"
$Root = Split-Path -Parent (Split-Path -Parent $MyInvocation.MyCommand.Path)
$EnvFile = Join-Path $Root ".env.local"

if (-not (Test-Path $EnvFile)) {
    throw "Missing $EnvFile"
}

Get-Content $EnvFile | ForEach-Object {
    if ($_ -match '^\s*#' -or $_ -match '^\s*$') { return }
    $pair = $_ -split '=', 2
    if ($pair.Count -eq 2) {
        [System.Environment]::SetEnvironmentVariable($pair[0].Trim(), $pair[1].Trim(), "Process")
    }
}

Write-Host "Loaded $EnvFile"
Write-Host "CMS_DB_USERNAME=$env:CMS_DB_USERNAME"
Write-Host "Backend: http://localhost:8015"
