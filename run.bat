@echo off
REM Hotel Management System - Run Script

set JAVAFX_LIB=javafx-sdk-17.0.2\lib
set MYSQL_JAR=mysql-connector-j-8.0.33.jar
set MODULE_PATH=%JAVAFX_LIB%;%MYSQL_JAR%

echo === Hotel Management System ===
echo.

REM Create bin directory if it doesn't exist
if not exist bin mkdir bin

echo [1/3] Compiling project...

REM Compile all Java files
javac -encoding UTF-8 --module-path "%MODULE_PATH%" --add-modules javafx.controls,javafx.fxml,javafx.graphics -d bin src\module-info.java src\com\hotel\*.java src\com\hotel\model\*.java src\com\hotel\dao\*.java src\com\hotel\controller\*.java src\com\hotel\service\*.java src\com\hotel\util\*.java src\com\hotel\config\*.java

if %ERRORLEVEL% EQU 0 (
    echo [2/3] Compilation successful!
    
    REM Copy FXML and resources
    echo [3/3] Copying resources...
    if not exist bin\com\hotel\view mkdir bin\com\hotel\view
    copy src\com\hotel\view\*.fxml bin\com\hotel\view\ >nul 2>&1
    copy src\com\hotel\view\styles.css bin\com\hotel\view\ >nul 2>&1
    
    echo.
    echo Starting application...
    echo.
    
    REM Run the application
    java --module-path "%MODULE_PATH%" --add-modules javafx.controls,javafx.fxml,javafx.graphics -cp "bin;%MYSQL_JAR%" com.hotel.HotelManagementApp
) else (
    echo [ERROR] Compilation failed!
    echo Please check the error messages above.
    pause
    exit /b 1
)
