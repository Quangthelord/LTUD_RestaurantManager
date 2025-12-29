@echo off
echo ========================================
echo Building Restaurant Manager Executable
echo ========================================
echo.

REM Check if Java is installed
java -version >nul 2>&1
if %errorlevel% neq 0 (
    echo ERROR: Java is not installed or not in PATH!
    echo Please install Java 17 or later.
    pause
    exit /b 1
)

echo Step 1: Cleaning previous builds...
call mvn clean
if %errorlevel% neq 0 (
    echo ERROR: Maven clean failed!
    pause
    exit /b %errorlevel%
)

echo.
echo Step 2: Compiling and packaging application...
call mvn package
if %errorlevel% neq 0 (
    echo ERROR: Maven package failed!
    pause
    exit /b %errorlevel%
)

echo.
echo Step 3: Creating executable with jpackage...
echo.

REM Check if jpackage is available (Java 14+)
jpackage --version >nul 2>&1
if %errorlevel% neq 0 (
    echo WARNING: jpackage is not available. Creating alternative launcher instead...
    echo.
    goto :create_launcher
)

REM Create directory for installer
if not exist "dist" mkdir dist

REM Use jpackage to create Windows installer
jpackage ^
    --input target ^
    --name "Restaurant Manager" ^
    --main-jar restaurant-manager-1.0.0.jar ^
    --main-class com.restaurantmanagement.app.MainApp ^
    --type msi ^
    --dest dist ^
    --app-version 1.0.0 ^
    --description "Restaurant Management System" ^
    --vendor "Restaurant Management" ^
    --win-dir-chooser ^
    --win-menu ^
    --win-shortcut

if %errorlevel% neq 0 (
    echo.
    echo WARNING: jpackage failed. Creating alternative launcher instead...
    echo.
    goto :create_launcher
)

echo.
echo ========================================
echo SUCCESS! Executable created in 'dist' folder
echo ========================================
echo.
echo You can find the installer at: dist\Restaurant Manager-1.0.0.msi
echo.
pause
exit /b 0

:create_launcher
echo Creating standalone launcher...
echo.

REM Create launcher directory
if not exist "dist\launcher" mkdir dist\launcher

REM Copy the fat JAR
copy target\restaurant-manager-1.0.0.jar dist\launcher\RestaurantManager.jar >nul

REM Create launcher batch file
(
echo @echo off
echo title Restaurant Manager
echo cd /d "%%~dp0"
echo java -jar RestaurantManager.jar
echo if errorlevel 1 pause
) > dist\launcher\RestaurantManager.bat

REM Create README
(
echo Restaurant Manager - Standalone Launcher
echo ========================================
echo.
echo To run the application:
echo   1. Double-click RestaurantManager.bat
echo.
echo Requirements:
echo   - Java 17 or later must be installed
echo   - Download from: https://adoptium.net/
echo.
echo If you encounter any issues:
echo   - Make sure Java is installed
echo   - Check that Java is in your system PATH
echo   - Try running: java -version
) > dist\launcher\README.txt

echo.
echo ========================================
echo Alternative launcher created!
echo ========================================
echo.
echo Location: dist\launcher\
echo.
echo Files created:
echo   - RestaurantManager.jar (the application)
echo   - RestaurantManager.bat (launcher script)
echo   - README.txt (instructions)
echo.
echo Users need Java 17+ installed to run this.
echo.
pause
exit /b 0

