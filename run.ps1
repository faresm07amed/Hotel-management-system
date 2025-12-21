# Hotel Management System - Run Script

# Set paths
$JAVAFX_LIB = "javafx-sdk-17.0.2\lib"
$MYSQL_JAR = "mysql-connector-j-8.0.33.jar"
$SRC_DIR = "src"
$OUT_DIR = "bin"
$MODULE_PATH = "$JAVAFX_LIB;$MYSQL_JAR"

Write-Host "=== Hotel Management System ===" -ForegroundColor Cyan
Write-Host ""

# Create output directory
if (Test-Path $OUT_DIR) {
    Remove-Item -Recurse -Force $OUT_DIR
}
New-Item -ItemType Directory -Path $OUT_DIR | Out-Null

Write-Host "[1/3] Compiling project..." -ForegroundColor Yellow

# Compile the project
$compileCommand = "javac --module-path `"$MODULE_PATH`" --add-modules javafx.controls,javafx.fxml,javafx.graphics -d `"$OUT_DIR`" -sourcepath `"$SRC_DIR`" `"$SRC_DIR\module-info.java`" `"$SRC_DIR\com\hotel\*.java`" `"$SRC_DIR\com\hotel\model\*.java`" `"$SRC_DIR\com\hotel\dao\*.java`" `"$SRC_DIR\com\hotel\controller\*.java`" `"$SRC_DIR\com\hotel\service\*.java`" `"$SRC_DIR\com\hotel\util\*.java`" `"$SRC_DIR\com\hotel\config\*.java`""

Invoke-Expression $compileCommand

if ($LASTEXITCODE -eq 0) {
    Write-Host "[2/3] Compilation successful!" -ForegroundColor Green
    
    # Copy resources to bin directory
    Write-Host "[3/3] Copying resources..." -ForegroundColor Yellow
    # Create the view directory if it doesn't exist
    $VIEW_DEST = "$OUT_DIR\com\hotel\view"
    if (!(Test-Path $VIEW_DEST)) {
        New-Item -ItemType Directory -Path $VIEW_DEST | Out-Null
    }
    # Copy all files from view directory (FXML, CSS, PNG, etc.)
    Copy-Item -Path "$SRC_DIR\com\hotel\view\*" -Destination "$VIEW_DEST\" -Force -ErrorAction SilentlyContinue
    
    Write-Host ""
    Write-Host "Starting application..." -ForegroundColor Cyan
    Write-Host ""
    
    # Run the application
    $runCommand = "java --module-path `"$MODULE_PATH`" --add-modules javafx.controls,javafx.fxml,javafx.graphics -cp `"$OUT_DIR;$MYSQL_JAR`" com.hotel.HotelManagementApp"
    Invoke-Expression $runCommand
} else {
    Write-Host "[ERROR] Compilation failed!" -ForegroundColor Red
    Write-Host "Please check the error messages above." -ForegroundColor Red
    exit 1
}
