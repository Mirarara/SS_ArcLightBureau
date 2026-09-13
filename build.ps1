param(
    [Parameter(Mandatory = $true)]
    [string]$StarsectorPath,
    [string]$JdkPath,
    [string]$LazyLibJar,
    [string]$MagicLibJar,
    [string]$GraphicsLibJar,
    [string]$BoxUtilJar,
    [string]$LunaLibJar,
    [string]$OutputJar
)

$ErrorActionPreference = "Stop"
$modRoot = $PSScriptRoot
$buildPath = Join-Path $modRoot "build"
$classesPath = Join-Path $buildPath "classes"
$stagePath = Join-Path $buildPath "stage"
$sourcePath = Join-Path $modRoot "jars\src"
$baseJar = Join-Path $modRoot "jars\TraverserDesignBureau3.jar"
if ([string]::IsNullOrWhiteSpace($OutputJar)) { $OutputJar = $baseJar }

if ([string]::IsNullOrWhiteSpace($JdkPath)) {
    $jdk = Get-ChildItem -LiteralPath $StarsectorPath -Directory -Filter "jdk-*" |
        Where-Object { Test-Path -LiteralPath (Join-Path $_.FullName "bin\javac.exe") } |
        Sort-Object Name -Descending |
        Select-Object -First 1
    if ($null -eq $jdk) { throw "No bundled JDK found in the Starsector folder." }
    $JdkPath = $jdk.FullName
}

function Find-ModJar([string]$ExplicitPath, [string]$FolderPattern, [string]$RelativePath) {
    if (-not [string]::IsNullOrWhiteSpace($ExplicitPath)) { return $ExplicitPath }
    $folder = Get-ChildItem -LiteralPath (Join-Path $StarsectorPath "mods") -Directory |
        Where-Object { $_.Name -like $FolderPattern } |
        Sort-Object Name -Descending |
        Select-Object -First 1
    if ($null -eq $folder) { throw "Required mod not found: $FolderPattern" }
    return Join-Path $folder.FullName $RelativePath
}

$LazyLibJar = Find-ModJar $LazyLibJar "*LazyLib*" "jars\LazyLib.jar"
$MagicLibJar = Find-ModJar $MagicLibJar "*MagicLib*" "jars\MagicLib.jar"
$GraphicsLibJar = Find-ModJar $GraphicsLibJar "*GraphicsLib*" "jars\Graphics.jar"
$BoxUtilJar = Find-ModJar $BoxUtilJar "*BoxUtil*" "jars\BoxUtilMod.jar"
$LunaLibJar = Find-ModJar $LunaLibJar "*LunaLib*" "jars\LunaLib.jar"

$javacExe = Join-Path $JdkPath "bin\javac.exe"
$jarExe = Join-Path $JdkPath "bin\jar.exe"
$corePath = Join-Path $StarsectorPath "starsector-core"
$coreJars = @(Get-ChildItem -LiteralPath $corePath -File -Filter "*.jar")
$sources = @(Get-ChildItem -LiteralPath $sourcePath -Recurse -File -Filter "*.java")
$required = @($javacExe, $jarExe, $baseJar, $LazyLibJar, $MagicLibJar, $GraphicsLibJar, $BoxUtilJar, $LunaLibJar)
foreach ($path in $required) {
    if (-not (Test-Path -LiteralPath $path -PathType Leaf)) { throw "Required file not found: $path" }
}
if ($coreJars.Count -eq 0) { throw "No Starsector core JARs found under $corePath" }
if ($sources.Count -eq 0) { throw "No Java sources found under $sourcePath" }

New-Item -ItemType Directory -Force -Path $buildPath, (Split-Path $OutputJar) | Out-Null
foreach ($path in @($classesPath, $stagePath)) {
    if (Test-Path -LiteralPath $path) { Remove-Item -Recurse -Force -LiteralPath $path }
    New-Item -ItemType Directory -Path $path | Out-Null
}

$classpath = (@($coreJars.FullName) + @($LazyLibJar, $MagicLibJar, $GraphicsLibJar, $BoxUtilJar, $LunaLibJar)) -join [IO.Path]::PathSeparator
& $javacExe -encoding UTF-8 --release 17 -classpath $classpath -d $classesPath $sources.FullName
if ($LASTEXITCODE -ne 0) { throw "Compilation failed." }

Push-Location $stagePath
try { & $jarExe xf $baseJar } finally { Pop-Location }
if ($LASTEXITCODE -ne 0) { throw "Base JAR extraction failed." }
Get-ChildItem -LiteralPath $stagePath -Recurse -File -Filter "*.class" | Remove-Item -Force
Copy-Item -Path (Join-Path $classesPath "*") -Destination $stagePath -Recurse -Force

& $jarExe --create --file $OutputJar -C $stagePath .
if ($LASTEXITCODE -ne 0) { throw "JAR creation failed." }

$entries = & $jarExe tf $OutputJar
foreach ($entry in @(
    "data/scripts/TDBModPlugin.class",
    "data/campaign/missions/TDB_Repair.class",
    "data/utils/tdb/I18nUtil.class"
)) {
    if ($entries -notcontains $entry) { throw "JAR verification failed: missing $entry" }
}

Write-Host "Built and verified $OutputJar"
