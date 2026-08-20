<#
.SYNOPSIS
  Build an urgent CMS release zip: JAR + Flyway SQL + frontend source + docs.
  Skips WAR, Docker, and DB dump (add dump under database/dump/ yourself if needed).

.EXAMPLE
  .\scripts\make-release-pack.ps1
  .\scripts\make-release-pack.ps1 -Version 1.0.0
  .\scripts\make-release-pack.ps1 -SkipBuild
#>
[CmdletBinding()]
param(
    [string] $Version = "",
    [switch] $SkipBuild,
    [switch] $SkipFrontendCopy
)

$ErrorActionPreference = "Stop"

$Root = Split-Path -Parent (Split-Path -Parent $MyInvocation.MyCommand.Path)
if (-not (Test-Path (Join-Path $Root "pom.xml"))) {
    throw "Run from repo: expected pom.xml under $Root"
}

$Templates = Join-Path $Root "scripts\release-templates"
$DateStamp = Get-Date -Format "yyyyMMdd"
if ([string]::IsNullOrWhiteSpace($Version)) {
    $pomPath = Join-Path $Root "pom.xml"
    $pomXml = [xml](Get-Content -Raw $pomPath)
    $Version = $pomXml.project.version
    if ([string]::IsNullOrWhiteSpace($Version)) { $Version = "1.0.0-SNAPSHOT" }
}

$PackName = "CMS-Release-$DateStamp-v$Version"
$ReleasesDir = Join-Path $Root "releases"
$PackDir = Join-Path $ReleasesDir $PackName
$ZipPath = Join-Path $ReleasesDir "$PackName.zip"

Write-Host "==> Release pack: $PackName"
Write-Host "    Root: $Root"

# Clean previous pack folder with same name
if (Test-Path $PackDir) {
    Remove-Item -Recurse -Force $PackDir
}
New-Item -ItemType Directory -Path $PackDir | Out-Null

# --- Backend JAR ---
if (-not $SkipBuild) {
    Write-Host "==> Building backend JAR (mvn)..."
    Push-Location $Root
    try {
        & mvn -B clean package -pl core-service -am "-DskipTests"
        if ($LASTEXITCODE -ne 0) { throw "Maven build failed (exit $LASTEXITCODE)" }
    }
    finally {
        Pop-Location
    }
}
else {
    Write-Host "==> SkipBuild: using existing JAR in core-service/target"
}

$JarDir = Join-Path $PackDir "backend\jar"
New-Item -ItemType Directory -Path $JarDir | Out-Null
$JarSource = Get-ChildItem -Path (Join-Path $Root "core-service\target") -Filter "core-service-*.jar" |
    Where-Object { $_.Name -notmatch "\.original$" -and $_.Name -notmatch "sources|javadoc" } |
    Sort-Object LastWriteTime -Descending |
    Select-Object -First 1

if (-not $JarSource) {
    throw "No core-service-*.jar found under core-service/target. Run without -SkipBuild."
}
Copy-Item $JarSource.FullName -Destination $JarDir
Write-Host "    Copied $($JarSource.Name)"

# --- Flyway migrations ---
$MigDest = Join-Path $PackDir "database\schema-migrations"
New-Item -ItemType Directory -Path $MigDest | Out-Null
$MigSrc = Join-Path $Root "core-service\src\main\resources\db\migration"
Copy-Item -Path (Join-Path $MigSrc "*") -Destination $MigDest -Recurse
New-Item -ItemType Directory -Path (Join-Path $PackDir "database\dump") | Out-Null
Copy-Item (Join-Path $Templates "DATABASE.txt") (Join-Path $PackDir "database\DATABASE.txt")
"# Place Oracle dump files here (manual)." | Set-Content (Join-Path $PackDir "database\dump\README.txt")

# --- Config ---
$ConfigDir = Join-Path $PackDir "config"
New-Item -ItemType Directory -Path $ConfigDir | Out-Null
Copy-Item (Join-Path $Templates "env.example") (Join-Path $ConfigDir "env.example")

# --- Docs ---
$DocsDir = Join-Path $PackDir "docs"
New-Item -ItemType Directory -Path $DocsDir | Out-Null
Copy-Item (Join-Path $Templates "DEPLOY.md") (Join-Path $DocsDir "DEPLOY.md")
$SmokeSrc = Join-Path $Root "scripts\api-smoke-test.sh"
if (Test-Path $SmokeSrc) {
    Copy-Item $SmokeSrc (Join-Path $DocsDir "api-smoke-test.sh")
}

# --- Frontend (source without node_modules / .next) ---
$FeDestRoot = Join-Path $PackDir "frontend"
New-Item -ItemType Directory -Path $FeDestRoot | Out-Null
Copy-Item (Join-Path $Templates "FRONTEND-BUILD.txt") (Join-Path $FeDestRoot "FRONTEND-BUILD.txt")

if (-not $SkipFrontendCopy) {
    $FeSrc = Join-Path $Root "cms-frontend"
    $FeDest = Join-Path $FeDestRoot "cms-frontend"
    if (-not (Test-Path $FeSrc)) {
        Write-Warning "cms-frontend not found; skipping frontend source copy."
    }
    else {
        Write-Host "==> Copying frontend source (excluding node_modules, .next, secrets)..."
        New-Item -ItemType Directory -Path $FeDest | Out-Null
        # /E copy subdirs; /XD exclude dirs; /XF exclude secret/local files
        & robocopy $FeSrc $FeDest /E `
            /XD node_modules .next .git .turbo coverage .idea .vscode `
            /XF *.log .env .env.local .env.production .env.*.local Dockerfile .dockerignore `
            /NFL /NDL /NJH /NJS /nc /ns /np | Out-Null
        # robocopy exit codes 0-7 are success
        if ($LASTEXITCODE -ge 8) {
            throw "robocopy failed with exit code $LASTEXITCODE"
        }
        $global:LASTEXITCODE = 0
    }
}

# --- Git / version metadata ---
$GitCommit = "unknown"
try {
    Push-Location $Root
    $GitCommit = (git rev-parse --short HEAD 2>$null)
    if (-not $GitCommit) { $GitCommit = "unknown" }
}
catch { $GitCommit = "unknown" }
finally { Pop-Location }

# --- 00-README ---
$Readme = @"
CMS Release Pack
================
Name:     $PackName
Built:    $(Get-Date -Format "yyyy-MM-dd HH:mm:ss")
Version:  $Version
Git:      $GitCommit
JAR:      $($JarSource.Name)

Contents
--------
backend/jar/                  Runnable Spring Boot JAR (Java 21)
database/schema-migrations/   Flyway SQL (Oracle)
database/dump/                Empty — add your Oracle dump manually
frontend/                     Next.js source + FRONTEND-BUILD.txt
config/env.example            Env vars (no secrets)
docs/DEPLOY.md                Deploy steps

NOT included
------------
WAR, Docker images/Dockerfiles, automated DB dump

Quick start (backend)
---------------------
1. Set env vars from config/env.example
2. java -jar backend/jar/$($JarSource.Name)
3. API: http://localhost:8015

See docs/DEPLOY.md for full instructions.
"@
Set-Content -Path (Join-Path $PackDir "00-README.txt") -Value $Readme -Encoding UTF8

# --- Checksums ---
Write-Host "==> Writing checksums..."
$checksumLines = @()
Get-ChildItem -Path $PackDir -Recurse -File | ForEach-Object {
    $rel = $_.FullName.Substring($PackDir.Length).TrimStart("\", "/")
    $hash = (Get-FileHash -Algorithm SHA256 -Path $_.FullName).Hash
    $checksumLines += "$hash  $rel"
}
$checksumLines | Set-Content -Path (Join-Path $PackDir "01-CHECKSUMS.txt") -Encoding UTF8

# --- Zip ---
Write-Host "==> Creating zip..."
if (Test-Path $ZipPath) {
    Remove-Item -Force $ZipPath
}
Compress-Archive -Path $PackDir -DestinationPath $ZipPath -CompressionLevel Optimal

$ZipHash = (Get-FileHash -Algorithm SHA256 -Path $ZipPath).Hash
$ZipSizeMb = [math]::Round((Get-Item $ZipPath).Length / 1MB, 2)

Write-Host ""
Write-Host "Done."
Write-Host "  Folder: $PackDir"
Write-Host "  Zip:    $ZipPath ($ZipSizeMb MB)"
Write-Host "  SHA256: $ZipHash"
Write-Host ""
Write-Host "Optional: copy your Oracle dump into database\dump\ then re-zip, or zip manually."
