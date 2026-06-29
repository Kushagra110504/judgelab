param()
$ErrorActionPreference = "Continue"
$root = "C:\Users\kushg\Downloads\judgelab"

Write-Host "=== [1] Starting Docker Desktop ==="
$dockerDesktop = Get-Process -Name "com.docker.backend" -ErrorAction SilentlyContinue
if (-not $dockerDesktop) {
    Start-Process "C:\Program Files\Docker\Docker\Docker Desktop.exe"
    Write-Host "Launched Docker Desktop, polling for engine..."
    $elapsed = 0
    while ($elapsed -lt 150) {
        Start-Sleep 5; $elapsed += 5
        if (Get-Process -Name "com.docker.backend" -ErrorAction SilentlyContinue) {
            Write-Host "Backend process up at ${elapsed}s"; break
        }
        Write-Host "  ${elapsed}s..."
    }
    Write-Host "Waiting 35s for Linux engine to fully initialize..."
    Start-Sleep 35
} else {
    Write-Host "Docker already running, waiting 10s to stabilize..."
    Start-Sleep 10
}

Write-Host "=== [2] Setting Docker context ==="
& docker context use desktop-linux

Write-Host "=== [3] Verifying Docker engine is responsive ==="
for ($i = 0; $i -lt 12; $i++) {
    $out = & docker ps 2>&1
    if ($LASTEXITCODE -eq 0) { Write-Host "Engine responsive!"; break }
    Write-Host "  Retry $i - not ready yet, sleeping 5s..."
    Start-Sleep 5
}

Write-Host "=== [4] Cleaning up stale containers and volume ==="
& docker stop judgelab-postgres 2>&1 | Out-Null
& docker rm   judgelab-postgres 2>&1 | Out-Null
$volRemove = & docker volume rm judgelab_postgres_data 2>&1
Write-Host "Volume rm: $volRemove"

Write-Host "=== [5] Starting fresh PostgreSQL ==="
Set-Location $root
& docker compose up -d 2>&1

Write-Host "=== [6] Waiting 20s for full PostgreSQL initialisation ==="
Start-Sleep 20
& docker ps --format "table {{.Names}}`t{{.Status}}`t{{.Ports}}" 2>&1

Write-Host "=== [7] Verifying PG user and database via TCP ==="; 
& docker exec judgelab-postgres bash -c "PGPASSWORD=judgelab psql -h 127.0.0.1 -U judgelab -d judgelab -c '\du'" 2>&1
& docker exec judgelab-postgres bash -c "PGPASSWORD=judgelab psql -h 127.0.0.1 -U judgelab -d judgelab -c '\l'"  2>&1

Write-Host "=== [8] Launching Spring Boot (35s capture) ==="
$sbOut = Join-Path $root "sb_out.txt"
$sbErr = Join-Path $root "sb_err.txt"
Remove-Item $sbOut, $sbErr -ErrorAction SilentlyContinue

$sbArgs = @{
    FilePath               = "mvn"
    ArgumentList           = "spring-boot:run"
    WorkingDirectory       = (Join-Path $root "backend")
    PassThru               = $true
    RedirectStandardOutput = $sbOut
    RedirectStandardError  = $sbErr
}
$proc = Start-Process @sbArgs


Write-Host "Spring Boot PID: $($proc.Id) - waiting 35s..."
Start-Sleep 35

Write-Host "=== Spring Boot stdout (filtered) ==="
if (Test-Path $sbOut) {
    Get-Content $sbOut | Where-Object { $_ -match "Started|Flyway|migrate|Hibernate|validate|ERROR|Exception|HHH|schema|V1" }
}
Write-Host "=== Spring Boot stderr (filtered) ==="
if (Test-Path $sbErr) {
    Get-Content $sbErr | Where-Object { $_ -match "Started|Flyway|migrate|Hibernate|validate|ERROR|Exception|HHH|schema|V1" }
}

Write-Host "=== [9] Stopping Spring Boot ==="
Stop-Process -Id $proc.Id -Force -ErrorAction SilentlyContinue
Write-Host "DONE"
